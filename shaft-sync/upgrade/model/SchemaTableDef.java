package jetbrick.dao.schema.upgrade.model;

import jetbrick.dao.schema.data.Entity;
import jetbrick.dao.schema.data.SchemaInfo;

public class SchemaTableDef {
    private Action action;
    private SchemaInfo<? extends Entity> schema;
    private SchemaChecksum checksum;

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public SchemaInfo<? extends Entity> getSchema() {
        return schema;
    }

    public void setSchema(SchemaInfo<? extends Entity> schema) {
        this.schema = schema;
    }

    public SchemaChecksum getChecksum() {
        return checksum;
    }

    public void setChecksum(SchemaChecksum checksum) {
        this.checksum = checksum;
    }

    public enum Action {
        CREATE, UPDATE, DELETE
    }

}
