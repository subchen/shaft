package shaft.dao.dialect;

public final class SQLServerDialect extends shaft.dao.dialect.Dialect {

    public static final String PRODUCT_NAME = "Microsoft SQL Server";

    @Override
    public String getPaginationSQL(String sql, int offset, int limit) {
        if (offset == 0) {
            sql = "select top " + limit + " * from (" + sql + ") as temp";
        } else {
            sql = sql.replaceAll("\\s+", " ");
            // 从原始 sql 中获取 order by 子句
            int orderbyPos = sql.toLowerCase().lastIndexOf(" order by ");
            String sorts = null;
            if (orderbyPos > 0) {
                sorts = sql.substring(orderbyPos);
                if (sorts.indexOf(")") > 0) {
                    sorts = null; // skip the nested order by
                }
            }
            if (sorts == null) {
                //sorts = "order by id";
                return null;
            }
            //@formatter:off
            sql = "select * from ("
                    + "  select top " + (offset + limit) + " row_number() over(" + sorts + ") as row, * from (" + sql + ")"
                    + ") as temp where row > " + offset;
            //@formatter:on
        }
        return sql;
    }
}
