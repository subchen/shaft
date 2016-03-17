package jetbrick.dao.schema.data;

import java.util.ArrayList;
import java.util.List;
import jetbrick.dao.schema.validator.Validator;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 只读的数据库字段，由SchemaInfoImpl初始化
 */
public class SchemaColumn {
    protected SchemaInfo<? extends Entity> schema;
    protected String fieldName;
    protected Class<?> fieldClass;

    protected String columnName;
    protected String typeName;
    protected Integer typeLength;
    protected Integer typeScale;
    protected boolean nullable;
    protected Object defaultValue;
    protected String displayName;
    protected String description;

    protected boolean primaryKey;
    protected SchemaInfo<? extends Entity> referenceSchema;
    protected boolean json;

    protected List<Validator> validators = new ArrayList<Validator>();

    public SchemaInfo<? extends Entity> getSchema() {
        return schema;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Class<?> getFieldClass() {
        return fieldClass;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getTypeName() {
        return typeName;
    }

    public Integer getTypeLength() {
        return typeLength;
    }

    public Integer getTypeScale() {
        return typeScale;
    }

    public boolean isNullable() {
        return nullable;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public SchemaInfo<? extends Entity> getReferenceSchema() {
        return referenceSchema;
    }

    public boolean isJson() {
        return json;
    }

    public List<Validator> getValidators() {
        return validators;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
