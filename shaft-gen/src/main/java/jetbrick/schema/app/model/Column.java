package jetbrick.schema.app.model;

import jetbrick.util.builder.ToStringBuilder;

public final class Column {
    private String name;

    public Column() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflection(this);
    }
}
