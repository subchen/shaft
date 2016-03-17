package jetbrick.schema.app.model;

import java.io.File;
import java.util.*;
import jetbrick.collection.ListOrderedMap;
import jetbrick.util.builder.ToStringBuilder;

/**
 * 描述一个Table
 */
public final class Table {
    private Catalog catalog;
    private String database; // 数据库名 (多数据源用)
    private String tableName; // 表名
    private String tabelClass; // 不带包名
    private String displayName;
    private String description;

    private PK pk;
    private Map<String, Column> columns;
    private List<Index> indexs;
    private List<Relationship> relationships; // 自己的表内字段引用了其他的表
    private List<Relationship> inverseRelationships; // 其他表的字段引用了自己的表

    public Table() {
        this.columns = new ListOrderedMap();
        this.indexs = new ArrayList<Index>();
        this.relationships = new ArrayList<Relationship>();
        this.inverseRelationships = new ArrayList<Relationship>();
    }

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

    //----- columns --------------------------------------------------
    public void addColumn(Column column) {
        columns.put(column.getColumnName().toUpperCase(), column);
    }

    public Column getColumn(String columnName) {
        return columns.get(columnName.toUpperCase());
    }

    public List<Column> getColumns() {
        return (List<Column>) columns.values();
    }

    //----- toString --------------------------------------------------
    @Override
    public String toString() {
        return ToStringBuilder.reflection(this);
    }
}
