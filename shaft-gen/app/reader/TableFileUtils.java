package jetbrick.schema.app.reader;

import java.io.File;
import java.util.List;
import jetbrick.commons.lang.CamelCaseUtils;
import jetbrick.commons.xml.XmlNode;
import jetbrick.schema.app.model.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableFileUtils {
    private static final Logger log = LoggerFactory.getLogger(TableFileUtils.class);

    public static void mappingTableList(Schema schema, XmlNode root) {
        for (XmlNode node : root.selectNodes("table-list/table")) {
            String tableXml = node.attribute("file").asString();
            File tableXmlFile = new File(schema.getSchemaFile().getParentFile(), tableXml);
            mappingTableFile(schema, tableXmlFile);
        }

        OneToManyUtils.process(schema);
        log.debug("XmlSchema loaded successfully.");
    }

    // read bulk
    private static void mappingTableFile(Schema schema, File fileXml) {
        log.debug("table file = " + fileXml);

        XmlNode root = XmlNode.create(fileXml);

        TableInfo table = new TableInfo();
        table.setSchema(schema);
        table.setTableName(StringUtils.substringBefore(fileXml.getName(), "."));
        table.setTableClass(CamelCaseUtils.toCapitalizeCamelCase(table.getTableName()));
        table.setDisplayName(root.attribute("display-name").asString());
        table.setDescription(root.attribute("description").asString());
        table.setCacheSupport(root.attribute("cache").asBoolean(false));
        table.setCacheMaxSize(root.attribute("cache-size").asInt(0));
        table.setCacheMaxLiveSeconds(root.attribute("cache-live-seconds").asInt(0));
        table.setCacheMaxIdleSeconds(root.attribute("cache-idle-seconds").asInt(0));
        schema.addTable(table);

        ColumnUtils.addDefaultPrimaryKey(table, root.attribute("primary-key-type").asString());
        ColumnUtils.mappingColumnList(table, root);

        //mappingSchemaPrimaryKey(table, root.element("primary-key"));

        List<XmlNode> indexNodes = root.elements("index");
        for (XmlNode node : indexNodes) {
            mappingSchemaIndex(table, node);
        }
    }

    protected static void mappingSchemaPrimaryKey(TableInfo table, XmlNode node) {
        String column = node.attribute("column").asString();
        String sequence = node.attribute("sequence").asString();

        if (column == null) {
            String error = String.format("Primary key is missing in table [%s].", table.getTableName());
            throw new RuntimeException(error);
        }
        TableColumn c = table.getColumn(column.trim());
        if (c == null) {
            String error = String.format("Coulmn [%s] is not found in table [%s].", column, table.getTableName());
            throw new RuntimeException(error);
        }
        c.setPrimaryKey(true);

        PrimaryKey pk = table.getPrimaryKey();
        pk.setSequence(sequence);
        pk.setColumn(c);
    }

    private static void mappingSchemaIndex(TableInfo table, XmlNode node) {
        String unique = node.attribute("unique").asString();
        String name = node.attribute("name").asString();
        String columns = node.attribute("columns").asString();
        String sorts = node.attribute("sorts").asString();

        if (name == null) {
            name = "idx_" + table.getTableName() + "_" + Math.abs((table.getTableName() + columns).hashCode());
        }

        TableIndex index = new TableIndex();
        index.setName(name);
        index.setUnique(Boolean.valueOf(unique));
        index.setSorts(sorts);

        String columnlist[] = StringUtils.split(columns, ",");
        for (String column : columnlist) {
            column = column.trim();
            TableColumn c = table.getColumn(column);
            if (c == null) {
                String error = String.format("Coulmn [%s] is not found in table [%s].", column, table.getTableName());
                throw new RuntimeException(error);
            }
            index.getColumns().add(c);
        }

        table.getIndexs().add(index);
    }

}
