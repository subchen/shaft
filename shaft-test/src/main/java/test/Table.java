package test;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jetbrick.util.builder.ToStringBuilder;

public class Table {
    private String name;
    @JsonProperty("class")
    private String clazz;
    private List<Column> columns;

    public String getName() {
        return name;
    }

    public String getClazz() {
        return clazz;
    }

    public List<Column> getColumns() {
        return columns;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflection(this);
    }
}
