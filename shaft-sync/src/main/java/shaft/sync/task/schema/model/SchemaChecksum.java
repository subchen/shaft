package shaft.sync.task.schema.model;

import jetbrick.util.builder.ToStringBuilder;

import java.sql.Timestamp;

public final class SchemaChecksum {
    private String name;
    private String checksum;
    private Timestamp timestamp;
    private String version;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


    @Override
    public String toString() {
        return ToStringBuilder.reflection(this);
    }
}
