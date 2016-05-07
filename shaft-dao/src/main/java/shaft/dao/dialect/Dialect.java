package shaft.dao.dialect;

import javax.annotation.Nullable;

public abstract class Dialect {


    public static Dialect create(String name) throws IllegalArgumentException {

        if (MysqlDialect.PRODUCT_NAME.equals(name)) {
            return new MysqlDialect();
        }

        throw new IllegalArgumentException("Unsupported database " + name);
    }

    /**
     * @param sql    原始 sql
     * @param offset 分页开始记录（从 0 开始, 等价于 (pageNo-1)*pageSize）
     * @param limit  返回数量
     * @return null if not supported
     */
    @Nullable
    public abstract String getPaginationSQL(String sql, int offset, int limit);

}
