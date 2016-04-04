/**
 * Copyright 2016 Guoqiang Chen, Shanghai, China. All rights reserved.
 *
 *   Author: Guoqiang Chen
 *    Email: subchen@gmail.com
 *   WebURL: https://github.com/subchen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package shaft.dao;

import jetbrick.util.Validate;
import shaft.dao.cb.ConnectionCallback;
import shaft.dao.cb.MetadataCallback;
import shaft.dao.cb.PreparedStatementCallback;
import shaft.dao.cb.ResultSetCallback;
import shaft.dao.handler.PagelistHandler;
import shaft.dao.handler.RowListHandler;
import shaft.dao.handler.SingleRowHandler;
import shaft.dao.mapper.ArrayRowMapper;
import shaft.dao.mapper.BeanRowMapper;
import shaft.dao.mapper.MapRowMapper;
import shaft.dao.mapper.SingleColumnRowMapper;
import shaft.dao.metadata.DbMetadata;
import shaft.dao.tx.JdbcNestedTransaction;
import shaft.dao.tx.JdbcTransaction;
import shaft.dao.tx.Transaction;
import shaft.dao.util.PagelistSql;
import shaft.dao.util.PreparedStatementCreator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 数据库操作。单例使用
 */
public final class DbHelper {
    public static final String KEY_TRANSACTION_NESTED_ENABLED = "shaft.dao.transaction.nested.disabled";

    private static final boolean TRANSACTION_NESTED_ENABLED = "true".equals(System.getProperty(KEY_TRANSACTION_NESTED_ENABLED));

    // 当前线程(事务)
    private final ThreadLocal<JdbcTransaction> transactionHandler = new ThreadLocal<>();
    private final DataSource dataSource;
    private DbMetadata metaData;
    private String productName;

    public DbHelper(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public DbMetadata getMetadata() {
        if (metaData == null) {
            metaData = new DbMetadata(this);
        }
        return metaData;
    }

    public String getProductName() {
        if (productName == null) {
            productName = getMetadata().getDatabaseName();
        }
        return productName;
    }

    /**
     * 启动一个事务
     */
    public Transaction transaction() {
        return transaction(Transaction.DEFAULT_ISOLATION_LEVEL);
    }

    /**
     * 启动一个事务
     *
     * @param isolationLevel 事务隔离级别
     * @return 事务对象
     */
    public Transaction transaction(int isolationLevel) {
        if (transactionHandler.get() != null) {
            if (TRANSACTION_NESTED_ENABLED) {
                return new JdbcNestedTransaction(transactionHandler.get().getConnection());
            }
            throw new TransactionException("Can't begin a nested transaction.");
        }
        try {
            JdbcTransaction tx = new JdbcTransaction(dataSource.getConnection(), isolationLevel, transactionHandler);
            transactionHandler.set(tx);
            return tx;
        } catch (SQLException e) {
            throw new TransactionException(e);
        }
    }

    /**
     * 获取一个当前线程的连接(事务中)，如果没有，则新建一个。
     */
    private Connection getConnection() {
        JdbcTransaction tx = transactionHandler.get();
        try {
            if (tx == null) {
                return dataSource.getConnection();
            } else {
                return tx.getConnection();
            }
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    /**
     * 释放一个连接，如果 Connection 不在事务中，则关闭它，否则不处理。
     */
    private void closeConnection(Connection conn) {
        if (transactionHandler.get() == null) {
            // not in transaction, close it
            if (conn != null) {
                try {
                    conn.close();
                } catch(SQLException e) {
                }
            }
        }
    }

    public <T> List<T> queryAsList(RowMapper<T> rowMapper, String sql, Object... parameters) {
        Validate.notNull(rowMapper, "rowMapper is null.");

        ResultSetHandler<List<T>> rsh = new RowListHandler<T>(rowMapper);
        return query(rsh, sql, parameters);
    }

    public <T> List<T> queryAsList(Class<T> beanClass, String sql, Object... parameters) {
        Validate.notNull(beanClass, "beanClass is null.");

        RowMapper<T> rowMapper = getRowMapper(beanClass);
        return queryAsList(rowMapper, sql, parameters);
    }

    public <T> T queryAsObject(RowMapper<T> rowMapper, String sql, Object... parameters) {
        ResultSetHandler<T> rsh = new SingleRowHandler<T>(rowMapper);
        return query(rsh, sql, parameters);
    }

    public <T> T queryAsObject(Class<T> beanClass, String sql, Object... parameters) {
        Validate.notNull(beanClass, "beanClass is null.");

        RowMapper<T> rowMapper = getRowMapper(beanClass);
        return queryAsObject(rowMapper, sql, parameters);
    }

    public Integer queryAsInt(String sql, Object... parameters) {
        return queryAsObject(Integer.class, sql, parameters);
    }

    public Long queryAsLong(String sql, Object... parameters) {
        return queryAsObject(Long.class, sql, parameters);
    }

    public Double queryAsDouble(String sql, Object... parameters) {
        return queryAsObject(Double.class, sql, parameters);
    }

    public String queryAsString(String sql, Object... parameters) {
        return queryAsObject(String.class, sql, parameters);
    }

    public Boolean queryAsBoolean(String sql, Object... parameters) {
        return queryAsObject(Boolean.class, sql, parameters);
    }

    public Date queryAsDate(String sql, Object... parameters) {
        return queryAsObject(Date.class, sql, parameters);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> queryAsMap(String sql, Object... parameters) {
        return queryAsObject(Map.class, sql, parameters);
    }

    @SuppressWarnings("unchecked")
    public <T> T[] queryAsArray(Class<T> arrayComponentClass, String sql, Object... parameters) {
        try {
            Class<T[]> clazz = (Class<T[]>) Class.forName("[" + arrayComponentClass.getName());
            return queryAsObject(clazz, sql, parameters);
        } catch (ClassNotFoundException e) {
            throw new DbException(e);
        }
    }

    public <T> Pagelist<T> queryAsPagelist(PageInfo pageInfo, Class<T> beanClass, String sql, Object... parameters) {
        Validate.notNull(beanClass, "beanClass is null.");

        RowMapper<T> rowMapper = getRowMapper(beanClass);
        return queryAsPagelist(pageInfo, rowMapper, sql, parameters);
    }

    public <T> Pagelist<T> queryAsPagelist(PageInfo pageInfo, RowMapper<T> rowMapper, String sql, Object... parameters) {
        Validate.notNull(pageInfo, "pageInfo is null.");
        Validate.notNull(rowMapper, "rowMapper is null.");

        PagelistImpl<T> pagelist = new PagelistImpl<T>(pageInfo);
        if (pageInfo.getTotalCount() < 0) {
            String countSQL = PagelistSql.getSelectCountSQL(sql);
            int count = queryAsInt(countSQL, parameters);
            pagelist.setTotalCount(count);
        }

        List<T> items = Collections.emptyList();
        if (pagelist.getTotalCount() > 0) {
            String pageSQL = PagelistSql.getSelectPageSQL(sql, pagelist.getFirstResult(), pagelist.getPageSize(), getProductName());
            PagelistHandler<T> rsh = new PagelistHandler<T>(rowMapper);
            if (pageSQL == null) {
                // 如果不支持分页，那么使用原始的分页方法 ResultSet.absolute(first)
                rsh.setFirstResult(pagelist.getFirstResult());
            } else {
                // 使用数据库自身的分页SQL语句，将直接返回某一个
                rsh.setFirstResult(0);
                sql = pageSQL;
            }
            rsh.setMaxResults(pagelist.getPageSize());
            items = query(rsh, sql, parameters);
        }
        pagelist.setItems(items);

        return pagelist;
    }

    public <T> T query(ResultSetHandler<T> rsh, String sql, Object... parameters) {
        Validate.notNull(rsh, "rsh is null.");
        Validate.notNull(sql, "sql is null.");

        Connection conn = null;
        try {
            conn = getConnection();
            try (PreparedStatement ps = PreparedStatementCreator.createPreparedStatement(conn, sql, parameters)){
                try (ResultSet rs = ps.executeQuery()){
                    return rsh.handle(rs);
                }
            }
        } catch (SQLException e) {
            throw new DbException(e).set("sql", sql).set("parameters", parameters);
        } finally {
            closeConnection(conn);
        }
    }

    public int executeUpdate(String sql, Object... parameters) {
        Validate.notNull(sql, "sql is null.");

        Connection conn = null;
        try {
            conn = getConnection();
            try (PreparedStatement ps = PreparedStatementCreator.createPreparedStatement(conn, sql, parameters)) {
                return ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DbException(e).set("sql", sql).set("parameters", parameters);
        } finally {
            closeConnection(conn);
        }
    }

    public int[] executeBatch(String sql, List<Object[]> parameters) {
        Validate.notNull(sql, "sql is null.");

        Connection conn = null;
        int[] rows;

        try {
            conn = getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)){
                for (Object[] parameter : parameters) {
                    for (int i = 0; i < parameter.length; i++) {
                        ps.setObject(i + 1, parameter[i]);
                    }
                    ps.addBatch();
                }
                rows = ps.executeBatch();
            }
        } catch (SQLException e) {
            throw new DbException(e).set("sql", sql).set("parameters", parameters);
        } finally {
            closeConnection(conn);
        }

        return rows;
    }

    public <T> T execute(ConnectionCallback<T> callback) {
        Connection conn = null;
        try {
            conn = getConnection();
            return callback.execute(conn);
        } catch (SQLException e) {
            throw new DbException(e);
        } finally {
            closeConnection(conn);
        }
    }

    public int executeUpdate(String sql, PreparedStatementCallback callback) {
        Connection conn = null;
        try {
            conn = getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                if (callback != null) {
                    callback.setParameters(ps);
                }
                return ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DbException(e);
        } finally {
            closeConnection(conn);
        }
    }

    public <T> T executeQuery(String sql, ResultSetCallback<T> callback) {
        Connection conn = null;
        try {
            conn = getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    return callback.execute(rs);
                }
            }
        } catch (SQLException e) {
            throw new DbException(e);
        } finally {
            closeConnection(conn);
        }
    }

    public <T> T executeMetaData(MetadataCallback<T> callback) {
        Connection conn = null;
        try {
            conn = getConnection();
            return callback.execute(conn.getMetaData());
        } catch (SQLException e) {
            throw new DbException(e);
        } finally {
            closeConnection(conn);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> RowMapper<T> getRowMapper(Class<T> beanClass) {
        RowMapper<T> rowMapper;
        if (beanClass == Map.class) {
            rowMapper = (RowMapper<T>) new MapRowMapper();
        } else if (beanClass.isArray()) {
            rowMapper = (RowMapper<T>) new ArrayRowMapper();
        } else if (beanClass.getName().startsWith("java.")) {
            rowMapper = new SingleColumnRowMapper<T>(beanClass);
        } else {
            rowMapper = new BeanRowMapper<T>(beanClass);
        }
        return rowMapper;
    }
}
