package jetbrick.schema.app.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 外键关系
 */
public class OneToManyRelation {
    private String name;
    private TableColumn column;
    private TableInfo reference;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TableColumn getColumn() {
        return column;
    }

    public void setColumn(TableColumn column) {
        this.column = column;
    }

    public TableInfo getReference() {
        return reference;
    }

    public void setReference(TableInfo reference) {
        this.reference = reference;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
