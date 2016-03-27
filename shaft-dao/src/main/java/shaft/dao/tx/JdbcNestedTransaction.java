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
package shaft.dao.tx;

import shaft.dao.TransactionException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

/**
 * Jdbc 子事务
 */
public final class JdbcNestedTransaction implements Transaction {
    private final Connection conn;
    private Savepoint savepoint;

    public JdbcNestedTransaction(Connection conn) {
        this.conn = conn;

        try {
            savepoint = conn.setSavepoint();
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
            // 子事务不需要 commit
            // conn.commit(savepoint);
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
            conn.rollback(savepoint);
        } catch (SQLException e) {
            throw new TransactionException(e);
        }
    }

    /**
     * 结束一个事务
     */
    @Override
    public void close() {
        // 子事务不需要 close
    }

}
