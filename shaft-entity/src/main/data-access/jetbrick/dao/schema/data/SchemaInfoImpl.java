package jetbrick.dao.schema.data;

import java.util.*;
import jetbrick.commons.exception.SystemException;
import jetbrick.dao.dialect.SubStyleType;
import jetbrick.dao.schema.validator.*;
import jetbrick.dao.schema.validator.spi.*;
import org.apache.commons.beanutils.BeanUtils;

public class SchemaInfoImpl<T extends Entity> extends SchemaInfo<T> {

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setTableClass(Class<T> tableClass) {
        this.tableClass = tableClass;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setCacheSupport(boolean cacheSupport) {
        this.cacheSupport = cacheSupport;
    }

    public void setCacheMaxSize(int cacheMaxSize) {
        this.cacheMaxSize = cacheMaxSize;
    }

    public void setCacheMaxLiveSeconds(int cacheMaxLiveSeconds) {
        this.cacheMaxLiveSeconds = cacheMaxLiveSeconds;
    }

    public void setCacheMaxIdleSeconds(int cacheMaxIdleSeconds) {
        this.cacheMaxIdleSeconds = cacheMaxIdleSeconds;
    }

    //@formatter:off
    public SchemaColumn addColumn(String fieldName, Class<?> fieldClass, 
			String columnName, String typeName, Integer typeLength, Integer typeScale,
            boolean nullable, Object defaultValue, 
            String displayName, String description, 
            boolean primaryKey, SchemaInfo<? extends Entity> referenceSchema, 
            boolean json) {
    //@formatter:on
        SchemaColumn c = new SchemaColumn();
        c.schema = (SchemaInfo<? extends Entity>) this;
        c.fieldName = fieldName;
        c.fieldClass = fieldClass;
        c.columnName = columnName;
        c.typeName = typeName;
        c.typeLength = typeLength;
        c.typeScale = typeScale;
        c.nullable = nullable;
        c.defaultValue = defaultValue;
        c.displayName = displayName;
        c.description = description;
        c.primaryKey = primaryKey;
        c.referenceSchema = referenceSchema;
        c.json = json;

        columns.put(c.getFieldName(), c);

        return c;
    }

    public void addDefaultValidators() {
        for (SchemaColumn column : getColumns()) {
            addDefaultValidators(column);
        }
    }

    private void addDefaultValidators(SchemaColumn column) {
        Set<String> lengths = new HashSet<String>(4);
        lengths.add(SubStyleType.CHAR);
        lengths.add(SubStyleType.BINARY);

        Set<String> varlengths = new HashSet<String>(4);
        varlengths.add(SubStyleType.CHAR);
        varlengths.add(SubStyleType.VARCHAR);
        varlengths.add(SubStyleType.BINARY);
        varlengths.add(SubStyleType.VARBINARY);

        List<Validator> validators = column.getValidators();
        String type = column.getTypeName();
        if (SubStyleType.DATETIME_STRING.equals(type)) {
            validators.add(0, DateTimeValidator.getInstance());
        } else if (SubStyleType.DATE_STRING.equals(type)) {
            validators.add(0, DateValidator.getInstance());
        } else if (SubStyleType.TIME_STRING.equals(type)) {
            validators.add(0, TimeValidator.getInstance());
        } else if (SubStyleType.UUID.equals(type)) {
            validators.add(0, new LengthValidator(16, 16));
        } else if (varlengths.contains(type)) {
            LengthValidator v = null;
            for (Validator validator : validators) {
                if (validator instanceof LengthValidator) {
                    v = (LengthValidator) validator;
                }
            }

            Integer length = column.getTypeLength();
            if (length == null) {
                throw new ValidatorException("column %s.%s missing length.", tableName, column.getColumnName());
            }

            int minLen = lengths.contains(type) ? length : (column.isNullable() ? 0 : 1);
            int maxLen = length;

            if (v == null) {
                validators.add(0, new LengthValidator(minLen, maxLen));
            } else {
                v.setMin(Math.max(minLen, v.getMin()));
                v.setMax(Math.min(maxLen, v.getMax()));
            }
        }
    }

    public ValidatorSetter addValidator(SchemaColumn column, String validator) {
        Validator v = ValidatorFactory.createValidator(validator);
        if (v == null) {
            throw new SystemException("validator not found: " + validator);
        }
        column.getValidators().add(v);
        return new ValidatorSetter(v);
    }

    public static class ValidatorSetter {
        private Validator v;

        public ValidatorSetter(Validator v) {
            this.v = v;
        }

        public ValidatorSetter set(String name, Object value) {
            try {
                BeanUtils.setProperty(v, name, value);
            } catch (Exception e) {
                throw SystemException.unchecked(e);
            }
            return this;
        }
    }

}
