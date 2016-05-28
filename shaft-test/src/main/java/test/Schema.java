package test;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jetbrick.util.builder.ToStringBuilder;

public class Schema {
    @JsonProperty("package")
    private String packageName;
    private List<String> tables;

    public String getPackageName() {
        return packageName;
    }

    public List<String> getTables() {
        return tables;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflection(this);
    }
}
