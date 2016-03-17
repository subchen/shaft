package jetbrick.schema.app.model;

import jetbrick.util.builder.ToStringBuilder;

public final class Relationship {
    private String name;
    private Column source;
    private Table target;

    public Relationship() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Column getSource() {
        return source;
    }

    public void setSource(Column source) {
        this.source = source;
    }

    public Table getTarget() {
        return target;
    }

    public void setTarget(Table target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflection(this);
    }
}
