package jetbrick.schema.app.model;

import java.util.ArrayList;
import java.util.List;
import jetbrick.dao.schema.validator.Validator;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 描述一个字段
 */
public class TableColumn {
    protected TableInfo table;
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
    protected boolean json;
    protected EnumGroup enumGroup;

    protected List<Validator> validators = new ArrayList<Validator>();

    //----- getter / setter --------------------------------------------------
    public TableInfo getTable() {
        return table;
    }

    public void setTable(TableInfo table) {
        this.table = table;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Class<?> getFieldClass() {
        return fieldClass;
    }

    public void setFieldClass(Class<?> fieldClass) {
        this.fieldClass = fieldClass;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Integer getTypeLength() {
        return typeLength;
    }

    public void setTypeLength(Integer typeLength) {
        this.typeLength = typeLength;
    }

    public Integer getTypeScale() {
        return typeScale;
    }

    public void setTypeScale(Integer typeScale) {
        this.typeScale = typeScale;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isJson() {
        return json;
    }

    public void setJson(boolean json) {
        this.json = json;
    }

    public EnumGroup getEnumGroup() {
        return enumGroup;
    }

    public void setEnumGroup(EnumGroup enumGroup) {
        this.enumGroup = enumGroup;
    }

    public List<Validator> getValidators() {
        return validators;
    }

    //----- toString --------------------------------------------------
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
