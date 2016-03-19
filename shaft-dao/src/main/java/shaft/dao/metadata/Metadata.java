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
package shaft.dao.metadata;

import com.sun.istack.internal.Nullable;
import shaft.dao.DbException;
import shaft.dao.DbHelper;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class Metadata {
    private final DbHelper db;

    public Metadata(DbHelper db) {
        this.db = db;
    }

    public String getDatabaseName() {
        try (Connection conn = db.getConnection()) {
            return conn.getMetaData().getDatabaseProductName();
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    public List<String> getTableList() {
        final List<String> nameList = new ArrayList<>(64);
        try (Connection conn = db.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet rs = metaData.getTables(null, null, null, new String[]{"TABLE"})) {
                while (rs.next()) {
                    String name = rs.getString("TABLE_NAME");
                    nameList.add(name);
                }
            }
        }
        return nameList;
    }

    @Nullable
    public DbTable getTable(String tableName) {
        List<DbColumn> columnList = new ArrayList<>(16);
        try (Connection conn = db.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet rs = metaData.getColumns(null, null, tableName.toUpperCase(), null)) {
                while (rs.next()) {
                    DbColumn.Builder cb = new DbColumn.Builder();
                    cb.setName(rs.getString("COLUMN_NAME"));
                    cb.setTypeName(rs.getString("TYPE_NAME"));
                    cb.setTypeLength(rs.getInt("COLUMN_SIZE"));
                    cb.setTypeScale(rs.getInt("DECIMAL_DIGITS"));
                    cb.setNullable(rs.getBoolean("NULLABLE"));
                    cb.setDefaultValue(rs.getObject("COLUMN_DEF").toString());
                    columnList.add(cb.build());
                }
            }
        }

        if (columnList.isEmpty()) {
            return null;
        }

        DbTable.Builder tb = new DbTable.Builder();
        tb.setName(tableName);
        tb.setColumns(columnList);
        return table.build();
    }

    public boolean tableExist(String tableName) {
        try (Connection conn = db.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet rs = metaData.getTables(null, null, tableName.toUpperCase(), new String[]{"TABLE"})) {
                return rs.next();
            }
        }
    }

}
