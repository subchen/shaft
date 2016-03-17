package jetbrick.dao.schema.data.jdbc;

import java.util.*;
import jetbrick.commons.exception.SystemException;
import jetbrick.dao.dialect.Dialect;
import jetbrick.dao.orm.ConnectionCallback;
import jetbrick.dao.orm.Transaction;
import jetbrick.dao.orm.jdbc.JdbcHelper;
import jetbrick.dao.schema.data.SimpleDaoHelper;
import jetbrick.dao.schema.data.SimpleDaoHelperCallback;
import org.slf4j.LoggerFactory;

public class JdbcDaoHelper implements SimpleDaoHelper {
    protected final JdbcHelper dao;
    protected final Dialect dialect;

    public JdbcDaoHelper(JdbcHelper dao) {
        this.dao = dao;
        this.dialect = dao.getDialect();

        LoggerFactory.getLogger(JdbcDaoHelper.class).debug("JdbcDaoHelper init completed.");
    }

    public JdbcHelper getJdbcHelper() {
        return dao;
    }

    // ----- dialect ---------------------------------------
    @Override
    public Dialect getDialect() {
        return dialect;
    }

    // ----- transaction ---------------------------------------
    /**
     * 启动一个事务(默认支持子事务)
     */
    @Override
    public Transaction transaction() {
        return dao.transaction();
    }

    // ----- table ---------------------------------------
    @Override
    public boolean tableExist(String tableName) {
        return dao.tableExist(tableName);
    }

    // ----- execute ---------------------------------------
    @Override
    public int execute(String sql, Object... parameters) {
        return dao.execute(sql, parameters);
    }

    public int[] executeBatch(String sql, List<Object[]> parameters) {
        return dao.executeBatch(sql, parameters);
    }

    // 作为一个事务运行
    @Override
    public void execute(ConnectionCallback callback) {
        Transaction tx = transaction();
        try {
            dao.execute(callback);
            tx.commit();
        } catch (Throwable e) {
            tx.rollback();
            throw SystemException.unchecked(e);
        } finally {
            tx.close();
        }
    }

    // 作为一个事务运行
    @Override
    public void execute(final SimpleDaoHelperCallback<SimpleDaoHelper> callback) {
        Transaction tx = transaction();
        try {
            callback.execute(this);
            tx.commit();
        } catch (Throwable e) {
            tx.rollback();
            throw SystemException.unchecked(e);
        } finally {
            tx.close();
        }
    }

    // ----- query ---------------------------------------
    @Override
    public Integer queryAsInt(String sql, Object... parameters) {
        return dao.queryAsInt(sql, parameters);
    }

    @Override
    public Long queryAsLong(String sql, Object... parameters) {
        return dao.queryAsLong(sql, parameters);
    }

    @Override
    public String queryAsString(String sql, Object... parameters) {
        return dao.queryAsString(sql, parameters);
    }

    @Override
    public Boolean queryAsBoolean(String sql, Object... parameters) {
        return dao.queryAsBoolean(sql, parameters);
    }

    @Override
    public Date queryAsDate(String sql, Object... parameters) {
        return dao.queryAsDate(sql, parameters);
    }

    public Map<String, Object> queryAsMap(String sql, Object... parameters) {
        return dao.queryAsMap(sql, parameters);
    }

    @Override
    public <T> T[] queryAsArray(Class<T> arrayComponentClass, String sql, Object... parameters) {
        return dao.queryAsArray(arrayComponentClass, sql, parameters);
    }
}
