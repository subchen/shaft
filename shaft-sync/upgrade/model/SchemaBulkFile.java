package jetbrick.dao.schema.upgrade.model;

import jetbrick.dao.schema.data.Entity;

public class SchemaBulkFile {
    private String fileName;
    private Class<? extends Entity> tableClass;
    private String checksum;
    private SchemaChecksum info;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Class<? extends Entity> getTableClass() {
        return tableClass;
    }

    public void setTableClass(Class<? extends Entity> tableClass) {
        this.tableClass = tableClass;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public SchemaChecksum getInfo() {
        return info;
    }

    public void setInfo(SchemaChecksum info) {
        this.info = info;
    }
}
