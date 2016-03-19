package shaft.sync.util;

import jetbrick.util.codec.MD5Utils;
import shaft.dao.metadata.DbTable;

import java.util.IdentityHashMap;
import java.util.Map;

public final class ChecksumUtils {
    private static final Map<Object, String> cache = new IdentityHashMap<>(128);


    public static String compute(DbTable table) {
        String checksum = cache.get(table);
        if (checksum == null) {
            StringBuilder sb = new StringBuilder(128);
            sb.append(table.getName()).append('#');
            table.getColumns().forEach(column -> {
                sb.append(column.getTypeName()).append('#');
                sb.append(column.getTypeLength()).append('#');
                sb.append(column.getTypePrecision()).append('#');
                sb.append(column.isNullable()).append('#');
                sb.append(column.getDefaultValue()).append('#');
            });
            checksum = MD5Utils.md5Hex(sb.toString());
            cache.put(column, checksum);
        }
        return checksum;
    }

}
