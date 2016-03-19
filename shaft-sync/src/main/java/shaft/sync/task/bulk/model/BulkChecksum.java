package shaft.sync.task.bulk.model;

import jetbrick.util.builder.ToStringBuilder;

import java.util.Date;

public final class BulkChecksum {
    private String name;
    private String bulk;
    private String checksum;
    private Date date;
    private String version;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBulk() {
        return bulk;
    }

    public void setBulk(String bulk) {
        this.bulk = bulk;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
