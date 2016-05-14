package shaft.dao.dialect;

public final class PostgreSqlDialect extends Dialect {

    public static final String PRODUCT_NAME = "PostgreSQL";

    @Override
    public String getPaginationSQL(String sql, int offset, int limit) {
        if (offset > 0) {
            return sql + " limit " + limit + " offset " + offset;
        } else {
            return sql + " limit " + limit;
        }
    }
}
