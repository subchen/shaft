package shaft.sync.task.schema;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jetbrick.util.StringUtils;
import shaft.dao.metadata.DbColumn;
import shaft.dao.metadata.DbTable;
import shaft.sync.App;
import shaft.sync.UpgradeTask;
import shaft.sync.task.schema.model.SchemaChecksum;
import shaft.sync.util.Checksum;
import shaft.sync.util.ChecksumUtils;

/**
 * 数据库表结构的自动升降级
 */
public final class SchemaUpgradeTask extends UpgradeTask {
    private final Logger log = LoggerFactory.getLogger(SchemaUpgradeTask.class);

    private final DelegateSchemaHook delegateSchemaHook = new DelegateSchemaHook();

    private final Map<String, DbTable> fileSchemaMap = new LinkedHashMap<>(64);
    private final Map<String, SchemaChecksum> dbChecksumMap = new LinkedHashMap<>(64);
    private final Set<String> dbTableNameSet = new HashSet<>(64);

    private SchemaChecksumDao dao;
    private int sumAdded = 0;
    private int sumUpdated = 0;
    private int sumDeleted = 0;

    private Checksum checksum = new Checksum();

    public SchemaUpgradeTask() {
    }

    public void addSchemaHook(SchemaHook hook) {
        delegateSchemaHook.add(hook);
    }

    @Override
    public void init(App app) {
        super.init(app);
        dao = new SchemaChecksumDao(jdbc);
    }

    @Override
    public void execute() {
        auditLog.println(">>>> Database Schema Table Upgrade checking ...");
        auditLog.println(">>>> date = %s", new Date());
        auditLog.println("");

        delegateSchemaHook.start();

        dao.ensureCreate();

        readSchemaInfo();
        computeSchemaChanges();

        delegateSchemaHook.done();

        auditLog.println(">>>> Database Schema Upgrade completed.\n");
        auditLog.println(">>>> Total tables: %d created, %d updated, %d deleted.\n", sumAdded, sumUpdated, sumDeleted);
        auditLog.println("");
    }

    private void readSchemaInfo() {
        log.debug("Reading schema from files ...");
        List<DbTable> fileSchemaList = getSchemaInfoList();
        for (DbTable schema : fileSchemaList) {
            fileSchemaMap.put(schema.getName().toUpperCase(), schema);
        }

        log.debug("Reading schema from database ...");
        List<SchemaChecksum> dbChecksumList = dao.list();
        for (SchemaChecksum checksum : dbChecksumList) {
            dbChecksumMap.put(checksum.getName().toUpperCase(), checksum);
        }

        log.debug("Reading all exist table names from database ...");
        List<String> tableNameList = jdbc.getMetadataTableList();
        for (String name : tableNameList) {
            dbTableNameSet.add(name.toUpperCase());
        }
    }

    private void computeSchemaChanges() {
        log.debug("Compute database schema changes ...");

        for (DbTable table : fileSchemaMap.values()) {
            String tableName = table.getName().toUpperCase();
            SchemaChecksum checksum = dbChecksumMap.get(tableName);
            if (checksum == null) {
                // new added
                if (dbTableNameSet.contains(tableName)) {
                    log.warn("Table {} exists, skipped to create, updating ...", table.getName());
                    tableUpdate(table);
                } else {
                    log.info("Table {} creating ...", table.getName());
                    tableCreate(table);
                }

                checksum = new SchemaChecksum();
                checksum.setName(table.getName());
                checksum.setChecksum(table.checksum());
                checksum.setTimestamp(new Timestamp(new Date().getTime()));
                checksum.setVersion(version);
                dao.save(checksum);

            } else if (StringUtils.equals(checksum.getChecksum(), ChecksumUtils.compute(table))) {
                // updated
                if (dbTableNameSet.contains(tableName)) {
                    log.info("Table {} updating ...", table.getName());
                    tableUpdate(table);
                } else {
                    log.warn("Table {} not exists, creating ...", table.getName());
                    tableCreate(table);
                }

                checksum.setChecksum(ChecksumUtils.compute(table));
                checksum.setTimestamp(new Timestamp(new Date().getTime()));
                checksum.setVersion(version);
                dao.update(checksum);

            } else {
                log.debug("Table {} no changes", table.getName());
            }
        }

        // lookup deleted tables
        for (SchemaChecksum checksum : dbChecksumMap.values()) {
            String tableName = checksum.getName().toUpperCase();
            if (!fileSchemaMap.containsKey(tableName)) {
                // to delete
                if (dbTableNameSet.contains(tableName)) {
                    log.info("Table {} deleting ...", checksum.getName());
                    tableDelete(checksum.getName());
                } else {
                    log.warn("Table {} not exists, skipped", checksum.getName());
                }

                dao.delete(checksum);
            }
        }

        // clean
        dbChecksumMap.clear();
        fileSchemaMap.clear();
    }

    private void tableCreate(DbTable table) {
        delegateSchemaHook.tableCreateBefore(table);

        jdbc.tableCreate(table);
        sumAdded++;

        delegateSchemaHook.tableCreateAfter(table);
    }

    private void tableDelete(String tableName) {
        delegateSchemaHook.tableDeleteBefore(tableName);

        jdbc.tableDelete(tableName);
        sumDeleted++;

        delegateSchemaHook.tableDeleteAfter(tableName);
    }

    private void tableUpdate(DbTable table) {
        List<DbColumn> columnList = jdbc.getMetadataColumnList(table.getName());

        DbColumn lastColumn = null;
        for (DbColumn sc : table.getColumns()) {
            DbColumn dc = dbColumnMap.get(sc.getName());

            if (dc == null) {
                // add column
                jdbc.columnCreate(sc, lastColumn);
                delegateSchemaHook.columnCreateAfter(sc);
            } else {
                if (isColumnChanged(sc, dc)) {
                    // update column
                    jdbc.columnUpdate(sc);
                    delegateSchemaHook.columnUpdateAfter(sc);
                }
            }

            lastColumn = sc;
        }

        // lookup deleted column 
        for (DbColumn dc : columnList) {
            if (dbColumnMap.get(dc.getName()) == null) {
                // drop column
                delegateSchemaHook.columnDeleteBefore(dc);
                jdbc.columnDelete(dc);
                delegateSchemaHook.columnDeleteAfter(dc);
            }
        }

        sumUpdated++;
        delegateSchemaHook.tableUpdateAfter(table);
    }

    private boolean isColumnChanged(DbColumn sc, DbColumn dc) {
        String column_type = dialect.asSqlType(sc.getTypeName(), sc.getTypeLength(), sc.getTypeScale());

        if (checksum.isEqual(sc, dc)) {
            return false;
        }

        String message = "changed column: " + sc.getName();
        message += ", type: " + dc.asSqlType().toUpperCase() + " -> " + column_type;
        message += ", nullable: " + dc.isNullable() + " -> " + sc.isNullable();
        log.warn(message);
        return true;
    }

}
