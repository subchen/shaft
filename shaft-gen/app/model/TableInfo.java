package jetbrick.schema.app.model;

import java.io.File;
import java.util.*;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 描述一个Table
 */
@SuppressWarnings("unchecked")
public class TableInfo {
    protected final Map<String, TableColumn> columns = new ListOrderedMap();
    protected String tableName;
    protected String tableClass; // 不带包名
    protected String displayName;
    protected String description;

    protected boolean cacheSupport;
    protected int cacheMaxSize;
    protected int cacheMaxLiveSeconds;
    protected int cacheMaxIdleSeconds;

    protected Schema schema;
    protected File bulkFile;

    protected PrimaryKey primaryKey = new PrimaryKey();
    protected List<OneToManyRelation> importedRelations = new ArrayList<OneToManyRelation>(); // 自己的表内字段引用了其他的表
    protected List<OneToManyRelation> exportedRelations = new ArrayList<OneToManyRelation>(); // 其他表的字段引用了自己的表
    protected List<TableIndex> indexs = new ArrayList<TableIndex>();

    //----- getter / setter --------------------------------------------------
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableClass() {
        return tableClass;
    }

    public void setTableClass(String tableClass) {
        this.tableClass = tableClass;
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

    public boolean isCacheSupport() {
        return cacheSupport;
    }

    public void setCacheSupport(boolean cacheSupport) {
        this.cacheSupport = cacheSupport;
    }

    public int getCacheMaxSize() {
        return cacheMaxSize;
    }

    public void setCacheMaxSize(int cacheMaxSize) {
        this.cacheMaxSize = cacheMaxSize;
    }

    public int getCacheMaxLiveSeconds() {
        return cacheMaxLiveSeconds;
    }

    public void setCacheMaxLiveSeconds(int cacheMaxLiveSeconds) {
        this.cacheMaxLiveSeconds = cacheMaxLiveSeconds;
    }

    public int getCacheMaxIdleSeconds() {
        return cacheMaxIdleSeconds;
    }

    public void setCacheMaxIdleSeconds(int cacheMaxIdleSeconds) {
        this.cacheMaxIdleSeconds = cacheMaxIdleSeconds;
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public File getBulkFile() {
        return bulkFile;
    }

    public void setBulkFile(File bulkFile) {
        this.bulkFile = bulkFile;
    }

    //----- key --------------------------------------------------
    public PrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    public List<OneToManyRelation> getImportedRelations() {
        return importedRelations;
    }

    public List<OneToManyRelation> getExportedRelations() {
        return exportedRelations;
    }

    public List<TableIndex> getIndexs() {
        return indexs;
    }

    //----- columns --------------------------------------------------
    public void addColumn(TableColumn column) {
        columns.put(column.getColumnName().toUpperCase(), column);
    }

    public TableColumn getColumn(String columnName) {
        return columns.get(columnName.toUpperCase());
    }

    public List<TableColumn> getColumns() {
        return (List<TableColumn>) columns.values();
    }

    //----- toString --------------------------------------------------
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
