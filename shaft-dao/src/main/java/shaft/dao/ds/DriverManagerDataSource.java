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
package shaft.dao.ds;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import jetbrick.util.ClassLoaderUtils;

public final class DriverManagerDataSource extends AbstractDataSource {
    private String url;
    private String username;
    private String password;
    private Properties connectionProperties;

    public DriverManagerDataSource() {
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConnectionProperties(Properties connectionProperties) {
        this.connectionProperties = connectionProperties;
    }

    public void setDriverClassName(String driverClassName) {
        try {
            Class.forName(driverClassName, true, ClassLoaderUtils.getDefault());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Could not load JDBC driver class [" + driverClassName + "]", e);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getConnectionFromDriver(username, password);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getConnectionFromDriver(username, password);
    }

    private Connection getConnectionFromDriver(String username, String password) throws SQLException {
        Properties props = new Properties();
        if (connectionProperties != null) {
            props.putAll(connectionProperties);
        }
        if (username != null) {
            props.setProperty("user", username);
        }
        if (password != null) {
            props.setProperty("password", password);
        }
        return getConnectionFromDriver(props);
    }

    private Connection getConnectionFromDriver(Properties props) throws SQLException {
        return DriverManager.getConnection(url, props);
    }
}
