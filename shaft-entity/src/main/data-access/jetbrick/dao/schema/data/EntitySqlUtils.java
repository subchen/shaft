package jetbrick.dao.schema.data;

import java.util.ArrayList;
import java.util.List;
import jetbrick.dao.dialect.Dialect;
import org.apache.commons.lang3.StringUtils;

public class EntitySqlUtils {

    public static String get_sql_table_create(SchemaInfo<?> schema, Dialect dialect) {
        StringBuilder sqls = new StringBuilder();
        List<String> pks = new ArrayList<String>(3);

        sqls.append("create table " + dialect.getIdentifier(schema.getTableName()) + " (\n");
        for (SchemaColumn c : schema.getColumns()) {
            if (c.isPrimaryKey()) {
                pks.add(dialect.getIdentifier(c.getColumnName()));
            }
            sqls.append("    ");
            sqls.append(dialect.getIdentifier(c.getColumnName()));
            sqls.append(" ");
            sqls.append(dialect.asSqlType(c.getTypeName(), c.getTypeLength(), c.getTypeScale()));
            sqls.append(c.isNullable() ? "" : " not null");
            sqls.append(",\n");
        }

        sqls.append("    primary key (" + StringUtils.join(pks, ",") + ")\n");
        sqls.append(");\n");

        return sqls.toString();
    }

    public static String get_sql_insert(SchemaInfo<?> schema, Dialect dialect) {
        List<String> names = new ArrayList<String>();
        for (SchemaColumn c : schema.getColumns()) {
            names.add(dialect.getIdentifier(c.getColumnName()));
        }
        String sql = "insert into %s (%s) values (%s)";
        String tableNameIdentifier = dialect.getIdentifier(schema.getTableName());
        //@formatter:off
        return String.format(sql, 
            tableNameIdentifier,
            StringUtils.join(names, ","), 
            StringUtils.repeat("?", ",", names.size())
        );
        //@formatter:on
    }

    public static String get_sql_update(SchemaInfo<?> schema, Dialect dialect) {
        List<String> names = new ArrayList<String>();
        for (SchemaColumn c : schema.getColumns()) {
            if (!c.isPrimaryKey()) {
                names.add(dialect.getIdentifier(c.getColumnName()) + "=?");
            }
        }
        String sql = "update %s set %s where id=?";
        String tableNameIdentifier = dialect.getIdentifier(schema.getTableName());
        return String.format(sql, tableNameIdentifier, StringUtils.join(names, ","));
    }

    public static String get_sql_delete(SchemaInfo<?> schema, Dialect dialect) {
        String tableNameIdentifier = dialect.getIdentifier(schema.getTableName());
        return "delete from " + tableNameIdentifier + " where id=?";
    }

    public static String get_sql_select_object(SchemaInfo<?> schema, Dialect dialect) {
        String tableNameIdentifier = dialect.getIdentifier(schema.getTableName());
        return "select * from " + tableNameIdentifier + " where id=?";
    }

    //	public static String get_sql_select_ids(String sql) {
    //		sql = " " + sql.replaceAll("\\s+", " ");
    //		int pos = sql.toLowerCase().indexOf(" from ");
    //		return "select id " + sql.substring(pos);
    //	}
}
