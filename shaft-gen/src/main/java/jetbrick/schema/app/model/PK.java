package jetbrick.schema.app.model;

import jetbrick.util.builder.ToStringBuilder;

public final class PK {
    private String name;
    private Column column;
    private IdGenerator generator;

    public PK() {
        name = "pk";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    public String getGenerator() {
        return generator;
    }

    public void setGenerator(String generator) {
        this.generator = generator;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflection(this);
    }
}
