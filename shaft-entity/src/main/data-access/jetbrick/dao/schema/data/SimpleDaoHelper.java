package jetbrick.dao.schema.data;

import java.util.Date;
import jetbrick.dao.dialect.Dialect;
import jetbrick.dao.orm.ConnectionCallback;
import jetbrick.dao.orm.Transaction;

/**
 * 多表操作或者自定义SQL执行
 */
public interface SimpleDaoHelper {

    public Dialect getDialect();

    // ----- table ---------------------------------------
    public boolean tableExist(String tableName);

    // ----- transaction ---------------------------------------
    public Transaction transaction();

    // ----- query ---------------------------------------
    public Integer queryAsInt(String sql, Object... parameters);

    public Long queryAsLong(String sql, Object... parameters);

    public String queryAsString(String sql, Object... parameters);

    public Boolean queryAsBoolean(String sql, Object... parameters);

    public Date queryAsDate(String sql, Object... parameters);

    public <T> T[] queryAsArray(Class<T> arrayClass, String sql, Object... parameters);

    // ----- execute ---------------------------------------
    public int execute(String sql, Object... parameters);

    public void execute(ConnectionCallback callback);

    public void execute(SimpleDaoHelperCallback<SimpleDaoHelper> callback);
}
