package shaft.dao.dialect;

public final class MysqlDialect extends Dialect {

    public static final String PRODUCT_NAME = "MySQL";

    @Override
    public String getPaginationSQL(String sql, int offset, int limit) {
        if (offset > 0) {
            return sql + " limit " + offset + "," + limit;
        } else {
            return sql + " limit " + limit;
        }
    }
}
