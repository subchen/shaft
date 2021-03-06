package jetbrick.schema.app.model;

import java.io.File;
import java.util.*;
import jetbrick.commons.lang.IdentityUtils;
import jetbrick.dao.dialect.Dialect;
import org.apache.commons.collections.map.ListOrderedMap;

/**
 * 管理所有的表.
 */
public final class Catalog {
    protected Resource schemaXml;
    protected String dialect;

    protected Config config = new Properties();
    protected Properties typeNameAlias = new Properties();
    protected Map<Integer, EnumGroup> enumGroups = new ListOrderedMap();
    protected Map<String, TableInfo> tables = new ListOrderedMap();
    protected List<BulkFile> bulkFiles = new ArrayList<BulkFile>();

    public Resource getSchemaXml() {
        return schemaXml;
    }

    public void setSchemaXml(File schemaXml) {
        this.schemaXml = schemaXml;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public Properties getProperties() {
        return properties;
    }

    public Properties getTypeNameAlias() {
        return typeNameAlias;
    }

    public List<BulkFile> getBulkFiles() {
        return bulkFiles;
    }

    //----- enum group --------------------------------------------------
    public void addEnumGroup(EnumGroup group) {
        if (enumGroups.containsKey(group.getPid())) {
            String error = String.format("Enum group %d is duplicate for %s.", group.getPid(), group.getIdentifier());
            throw new RuntimeException(error);
        }
        enumGroups.put(group.getPid(), group);
    }

    public EnumGroup getReferenceEnumGroup(int pid) {
        EnumGroup ref = enumGroups.get(pid);
        if (ref != null) {
            ref = ref.createChild();
            enumGroups.put(IdentityUtils.randomInt(), ref);
        }
        return ref;
    }

    public List<EnumGroup> getEnumGroups() {
        return (List<EnumGroup>) enumGroups.values();
    }

    //----- table --------------------------------------------------
    public TableInfo getTable(String name) {
        return tables.get(name.toLowerCase());
    }

    public List<TableInfo> getTables() {
        return (List<TableInfo>) tables.values();
    }

    public void addTable(TableInfo table) {
        tables.put(table.getTableName().toLowerCase(), table);
    }

    //----- property --------------------------------------------------
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getPackageName() {
        return properties.getProperty("package.name");
    }
}
