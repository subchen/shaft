package jetbrick.dao.schema.data;

import java.util.List;
import java.util.Map;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 提供只读的数据库Table对象， 要初始化SchemaInfo，请使用 SchemaInfoImpl
 */
public abstract class SchemaInfo<T extends Entity> {
    @SuppressWarnings("unchecked")
    protected final Map<String, SchemaColumn> columns = new ListOrderedMap();

    protected String tableName;
    protected Class<T> tableClass;
    protected String displayName;
    protected String description;

    protected String checksum;
    protected String timestamp;

    protected boolean cacheSupport;
    protected int cacheMaxSize;
    protected int cacheMaxLiveSeconds;
    protected int cacheMaxIdleSeconds;

    public SchemaColumn getColumn(String fieldName) {
        return columns.get(fieldName);
    }

    public List<SchemaColumn> getColumns() {
        return (List<SchemaColumn>) columns.values();
    }

    public String getTableName() {
        return tableName;
    }

    public Class<T> getTableClass() {
        return tableClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getChecksum() {
        return checksum;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isCacheSupport() {
        return cacheSupport;
    }

    public int getCacheMaxSize() {
        return cacheMaxSize;
    }

    public int getCacheMaxLiveSeconds() {
        return cacheMaxLiveSeconds;
    }

    public int getCacheMaxIdleSeconds() {
        return cacheMaxIdleSeconds;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
