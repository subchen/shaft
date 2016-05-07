package shaft.sync.task.bulk;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jetbrick.util.StringUtils;
import shaft.sync.UpgradeTask;
import shaft.sync.task.schema.model.SchemaChecksum;

/**
 * 初始化数据的升降级
 */
public final class BulkUpgradeTask extends UpgradeTask {
    private final Logger log = LoggerFactory.getLogger(BulkUpgradeTask.class);
    private final String SCHEMA_BULK_FILE = "/META-INF/schema-bulk.xml";
    private final String FILE_ENCODING = "utf-8";

    private final List<SchemaBulkFile> bulkFileQueue = new ArrayList<SchemaBulkFile>();

    public BulkUpgradeTask() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initialize() {
        // 读取数据库中存在的 BULK数据文件
        Map<String, SchemaChecksum> db_checksum_map = new HashMap<String, SchemaChecksum>();
        List<SchemaChecksum> db_checksum_list = SchemaChecksum.DAO.loadSomeEx("type", "BULK");
        for (SchemaChecksum checksum : db_checksum_list) {
            db_checksum_map.put(checksum.getName(), checksum);
        }

        // 读取当前的BULK文件，并比较是否有变化
        InputStream schemaXml = getClass().getResourceAsStream(SCHEMA_BULK_FILE);
        XmlNode root = XmlNode.create(schemaXml);
        for (XmlNode node : root.elements()) {
            try {
                SchemaBulkFile bulkFile = new SchemaBulkFile();
                bulkFile.setFileName(node.attribute("file").asString());
                bulkFile.setTableClass((Class<? extends Entity>) node.attribute("class").asClass());
                bulkFile.setChecksum(node.attribute("checksum").asString());

                SchemaChecksum checksum = db_checksum_map.get(bulkFile.getFileName());
                bulkFile.setInfo(checksum);

                if (checksum == null || !StringUtils.equals(bulkFile.getChecksum(), checksum.getChecksum())) {
                    // 有变化，则加入到 queue
                    bulkFileQueue.add(bulkFile);
                }
            } catch (Exception e) {
                throw SystemException.unchecked(e);
            }
        }

        db_checksum_list.clear();
        db_checksum_map.clear();
    }

    @Override
    public void destory() {
        bulkFileQueue.clear();
    }

    @Override
    public boolean isRequired() {
        if (bulkFileQueue.size() == 0) {
            log.info("Database Bulk load is not required.");
        }
        return bulkFileQueue.size() > 0;
    }

    @Override
    public void execute() {
        fileLog.println(">>>> Database Bulk load checking ...");
        fileLog.println(">>>> date = %s", DateUtils.getNowStr());
        fileLog.println("");

        try {
            for (SchemaBulkFile bulk : bulkFileQueue) {
                fileLog.println(">>>> file = ", bulk.getFileName());

                String packageName = bulk.getTableClass().getPackage().getName();
                packageName = StringUtils.replace(packageName, ".", "/");
                String fileName = "/META-INF/bulk/" + bulk.getFileName();
                InputStream is = getClass().getResourceAsStream(fileName);

                doBulk(is, bulk);

                updateSchemaTable(bulk);
            }
        } catch (Throwable e) {
            throw SystemException.unchecked(e);
        }
    }

    private void updateSchemaTable(SchemaBulkFile bulk) {
        // update global checksum
        SchemaChecksum info = bulk.getInfo();
        if (info == null) {
            info = SchemaChecksum.newInstance();
        }
        info.setName(bulk.getFileName());
        info.setType("BULK");
        info.setChecksum(bulk.getChecksum());
        info.setTimestamp(DateUtils.getTimestamp());
        info.saveOrUpdate();
    }

    protected void doBulk(InputStream inputStream, SchemaBulkFile bulk) throws IOException {
        SchemaInfo<? extends Entity> schema = EntityUtils.getSchema(bulk.getTableClass());

        Reader reader = new InputStreamReader(inputStream, FILE_ENCODING);

        Builder builder = new CsvPreference.Builder('\'', ',', "\n");
        builder.surroundingSpacesNeedQuotes(true);
        CsvListReader csv = new CsvListReader(reader, builder.build());

        String[] headers = csv.getHeader(true);
        for (int i = 0; i < headers.length; i++) {
            headers[i] = dialect.getIdentifier(headers[i]);
        }

        CellProcessor[] processors = getCsvProcessors(schema);
        List<List<Object>> datalist = new ArrayList<List<Object>>();
        while (true) {
            List<Object> bean = csv.read(processors);
            if (bean == null) break;
            datalist.add(bean);
        }
        csv.close();

        String sql = String.format("insert into %s (%s) values (%s)", schema.getTableName(), StringUtils.join(headers, ","), StringUtils.repeat("?", ",", headers.length));
        int inserted = 0;
        int duplicated = 0;
        int failed = 0;
        for (List<Object> data : datalist) {
            try {
                executeSQLWithFileLog(sql, data.toArray());
                inserted++;
            } catch (DuplicateKeyException e) {
                duplicated++;
            } catch (Exception e) {
                failed++;
                log.error("Ignored Unknown Exception.", e);
            }
        }
        fileLog.println(">>>> Total: %d inserted, %d duplicated, %d failed.\n", inserted, duplicated, failed);
    }

    private CellProcessor[] getCsvProcessors(SchemaInfo<? extends Entity> schema) {
        List<CellProcessor> plist = new ArrayList<CellProcessor>();
        for (SchemaColumn c : schema.getColumns()) {
            CellProcessor p = null;
            String type = c.getTypeName();
            if (SubStyleType.UID.equals(type)) {
                p = new ParseInt();
            } else if (SubStyleType.UUID.equals(type)) {
                p = new Strlen(new int[]{16});
            } else if (SubStyleType.ENUM.equals(type)) {
                p = new ParseInt();
            } else if (SubStyleType.CHAR.equals(type)) {
                p = new Strlen(new int[]{c.getTypeLength()});
            } else if (SubStyleType.VARCHAR.equals(type)) {
                p = new StrMinMax(0, c.getTypeLength());
            } else if (SubStyleType.TEXT.equals(type)) {
                p = new StrMinMax(0, c.getTypeLength() == null ? Integer.MAX_VALUE : c.getTypeLength());
            } else if (SubStyleType.INT.equals(type)) {
                p = new ParseInt();
            } else if (SubStyleType.LONG.equals(type)) {
                p = new ParseLong();
            } else if (SubStyleType.BIGINT.equals(type)) {
                p = new ParseBigDecimal();
            } else if (SubStyleType.DOUBLE.equals(type)) {
                p = new ParseDouble();
            } else if (SubStyleType.DECIMAL.equals(type)) {
                p = new ParseDouble();
            } else if (SubStyleType.BOOLEAN.equals(type)) {
                p = new ParseBool();
            } else if (SubStyleType.DATETIME_STRING.equals(type)) {
                p = new Strlen(new int[]{DateUtils.FORMAT_DATE_TIME.length()});
            } else if (SubStyleType.DATE_STRING.equals(type)) {
                p = new Strlen(new int[]{DateUtils.FORMAT_DATE.length()});
            } else if (SubStyleType.TIME_STRING.equals(type)) {
                p = new Strlen(new int[]{DateUtils.FORMAT_TIME.length()});
            } else if (SubStyleType.DATETIME.equals(type)) {
                p = new ParseDate(DateUtils.FORMAT_DATE_TIME);
            } else if (SubStyleType.TIMESTAMP.equals(type)) {
                p = new ParseDate(DateUtils.FORMAT_DATE_TIME);
            } else if (SubStyleType.DATE.equals(type)) {
                p = new ParseDate(DateUtils.FORMAT_DATE);
            } else if (SubStyleType.TIME.equals(type)) {
                p = new ParseDate(DateUtils.FORMAT_TIME);
            }

            if (p instanceof StringCellProcessor) {
                p = new Trim((StringCellProcessor) p);
            }

            if (c.isNullable()) {
                p = new Optional(p);
            } else {
                p = new NotNull(p);
            }
            plist.add(p);
        }

        return plist.toArray(new CellProcessor[0]);
    }

}
