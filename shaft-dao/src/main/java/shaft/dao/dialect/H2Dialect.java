package shaft.dao.dialect;

public final class H2Dialect extends shaft.dao.dialect.Dialect {

    public static final String PRODUCT_NAME = "H2";

    @Override
    public String getPaginationSQL(String sql, int offset, int limit) {
        if (offset > 0) {
            return sql + " limit " + limit + " offset " + offset;
        } else {
            return sql + " limit " + limit;
        }
    }
}
