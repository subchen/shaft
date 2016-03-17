package jetbrick.schema.app.model.methods;

import jetbrick.commons.lang.EncodeUtils;
import jetbrick.schema.app.model.*;

public class ChecksumUtils {

    public static String checksum(Schema schema) {
        StringBuilder sb = new StringBuilder();
        for (TableInfo t : schema.getTables()) {
            sb.append(checksum(t));
        }
        return EncodeUtils.encodeMD5(sb.toString());
    }

    public static String checksum(TableInfo table) {
        StringBuilder sb = new StringBuilder();
        sb.append(table.getTableName());
        for (TableColumn c : table.getColumns()) {
            sb.append(c.getColumnName());
            sb.append(c.getTypeName());
            sb.append(c.getTypeLength());
            sb.append(c.getTypeScale());
            sb.append(c.isNullable());
            sb.append(c.getDefaultValue());
            sb.append(c.isPrimaryKey());
        }
        return EncodeUtils.encodeMD5(sb.toString());
    }

    public static String enumChecksum(Schema schema) {
        StringBuilder sb = new StringBuilder();
        for (EnumGroup g : schema.getEnumGroups()) {
            if (g.isIndependence()) {
                sb.append(enumChecksum(g));
            }
        }
        return EncodeUtils.encodeMD5(sb.toString());
    }

    public static String enumChecksum(EnumGroup g) {
        StringBuilder sb = new StringBuilder();
        for (EnumItem en : g.getItems()) {
            sb.append(en.getId());
            sb.append(en.getName());
        }
        return EncodeUtils.encodeMD5(sb.toString());
    }

    public static String checksum(BulkFile bulk) {
        return EncodeUtils.encodeMD5(bulk.getContents());
    }
}
