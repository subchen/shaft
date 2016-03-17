package jetbrick.schema.app.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * 单个枚举变量
 */
public class EnumItem {
    protected int pid;
    protected int id;
    protected String identifier;
    protected String name;

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
