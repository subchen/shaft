package shaft.dao.dialect;

public final class OracleDialect extends Dialect {

    public static final String PRODUCT_NAME = "Oracle";

    @Override
    public String getPaginationSQL(String sql, int offset, int limit) {
        //@formatter:off
        sql = "select * from ("
                + "  select t.*, ROWNUM row from ("
                + sql
                + "  ) t where ROWNUM <= " + (offset + limit) + ")";
        //@formatter:on
        if (offset > 0) {
            sql = sql + " where row > " + offset;
        }
        return sql;
    }
}
