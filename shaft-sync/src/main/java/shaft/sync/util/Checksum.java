package shaft.sync.util;

import java.util.IdentityHashMap;
import java.util.Map;

import jetbrick.util.builder.EqualsBuilder;
import jetbrick.util.codec.MD5Utils;
import shaft.dao.metadata.DbColumn;
import shaft.dao.metadata.DbTable;

public final class Checksum {
    private final Map<Object, String> cache = new IdentityHashMap<>(128);

    public String compute(DbTable table) {
        String checksum = cache.get(table);
        if (checksum == null) {
            StringBuilder sb = new StringBuilder(128);
            sb.append(table.getName()).append('#');
            table.getColumns().forEach(column -> {
                sb.append(column.getName()).append('#');
                sb.append(column.getTypeName()).append('#');
                sb.append(column.getTypeLength()).append('#');
                sb.append(column.getTypePrecision()).append('#');
                sb.append(column.isNullable()).append('#');
                sb.append(column.getDefaultValue()).append('#');
            });
            checksum = MD5Utils.md5Hex(sb.toString());
            cache.put(table, checksum);
        }
        return checksum;
    }

    public boolean isEqual(DbColumn c1, DbColumn c2) {
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(c1.getName(), c2.getName());
        builder.append(c1.getTypeName(), c2.getTypeName());
        builder.append(c1.getTypeLength(), c2.getTypeLength());
        builder.append(c1.getTypePrecision(), c2.getTypePrecision());
        builder.append(c1.isNullable(), c2.isNullable());
        builder.append(c1.getDefaultValue(), c2.getDefaultValue());
        return builder.isEquals();
    }

}
