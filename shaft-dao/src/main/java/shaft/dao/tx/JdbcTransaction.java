/**
 * Copyright 2013-2014 Guoqiang Chen, Shanghai, China. All rights reserved.
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
package shaft.dao.tx;

import java.sql.Connection;
import java.sql.SQLException;
import shaft.dao.TransactionException;
import shaft.dao.util.DbUtils;

/**
 * Jdbc 事务对象
 */
public final class JdbcTransaction implements Transaction {
    private final Connection conn;
    private final int defaultLevel;
    private final ThreadLocal<JdbcTransaction> transationHandler;

    public JdbcTransaction(Connection conn, int level, ThreadLocal<JdbcTransaction> transationHandler) {
        this.conn = conn;
        this.transationHandler = transationHandler;
        this.defaultLevel = conn.getTransactionIsolation();

        try {
            if (conn.getAutoCommit()) {
                conn.setAutoCommit(false);
            }
            if (level != Transaction.DEFAULT_LEVEL) {
                conn.setTransactionIsolation(level);
            }
        } catch (SQLException e) {
            throw new TransactionException(e);
        }
    }

    public Connection getConnection() {
        return conn;
    }

    /**
     * 提交一个事务
     */
    @Override
    public void commit() {
        try {
            if (conn.isClosed()) {
                throw new TransactionException("the connection is closed in transaction.");
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            throw new TransactionException(e);
        }
    }

    /**
     * 回滚一个事务
     */
    @Override
    public void rollback() {
        try {
            if (conn.isClosed()) {
                throw new TransactionException("the connection is closed in transaction.");
            }
            conn.rollback();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            throw new TransactionException(e);
        }
    }

    /**
     * 结束一个事务
     */
    @Override
    public void close() {
        try {
            if (conn.isClosed()) {
                throw new TransactionException("the connection is closed in transaction.");
            }
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(defaultLevel);
            DbUtils.closeQuietly(conn);
        } catch (SQLException e) {
            throw new TransactionException(e);
        } finally {
            transationHandler.set(null);
        }
    }

}
