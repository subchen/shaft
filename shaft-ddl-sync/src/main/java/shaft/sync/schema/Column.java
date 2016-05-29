package shaft.sync.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jetbrick.util.builder.ToStringBuilder;

@JsonIgnoreProperties({"annotations", "enum"})
public class Column {
    private String name;
    private String type;
    private boolean required = true;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isRequired() {
        return required;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflection(this);
    }
}
