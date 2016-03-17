package jetbrick.schema.app.model;

import jetbrick.util.builder.ToStringBuilder;

public final class IdGenerator {
    private String name;
    private String parameters;

    public IdGenerator() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflection(this);
    }
}
