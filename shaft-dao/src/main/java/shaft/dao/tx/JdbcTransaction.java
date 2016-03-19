/**
 * Copyright 2016 Guoqiang Chen, Shanghai, China. All rights reserved.
 * <p>
 * Author: Guoqiang Chen
 * Email: subchen@gmail.com
 * WebURL: https://github.com/subchen
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package shaft.dao.tx;

import shaft.dao.TransactionException;
import shaft.dao.util.DbUtils;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Jdbc 事务对象
 */
public final class JdbcTransaction implements Transaction {
    private final Connection conn;
    private final int defaultIsolationLevel;
    private final ThreadLocal<JdbcTransaction> transactionHandler;

    public JdbcTransaction(Connection conn, int isolationLevel, ThreadLocal<JdbcTransaction> transactionHandler) {
        this.conn = conn;
        this.transactionHandler = transactionHandler;

        try {
            if (conn.getAutoCommit()) {
                conn.setAutoCommit(false);
            }

            this.defaultIsolationLevel = conn.getTransactionIsolation();
            if (isolationLevel != Transaction.DEFAULT_ISOLATION_LEVEL) {
                conn.setTransactionIsolation(isolationLevel);
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
            conn.setTransactionIsolation(defaultIsolationLevel);
            DbUtils.closeQuietly(conn);
        } catch (SQLException e) {
            throw new TransactionException(e);
        } finally {
            transactionHandler.set(null);
        }
    }

}
