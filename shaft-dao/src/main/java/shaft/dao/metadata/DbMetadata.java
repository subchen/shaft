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
package shaft.dao.metadata;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import shaft.dao.DbHelper;

public final class DbMetadata {
    private final DbHelper db;

    public DbMetadata(DbHelper db) {
        this.db = db;
    }

    public String getDatabaseName() {
        return db.executeMetaData(metaData -> metaData.getDatabaseProductName());
    }

    public List<String> getTableList() throws SQLException {
        final List<String> nameList = new ArrayList<>(64);
        db.executeMetaData(metaData -> {
            try (ResultSet rs = metaData.getTables(null, null, null, new String[]{"TABLE"})) {
                while (rs.next()) {
                    String name = rs.getString("TABLE_NAME");
                    nameList.add(name);
                }
            }
            return null;
        });
        return nameList;
    }

    @Nullable
    public DbTable getTable(String tableName) throws SQLException {
        List<DbColumn> columnList = new ArrayList<>(16);
        db.executeMetaData(metaData -> {
            try (ResultSet rs = metaData.getColumns(null, null, tableName.toUpperCase(), null)) {
                while (rs.next()) {
                    DbColumn.Builder cb = new DbColumn.Builder();
                    cb.setName(rs.getString("COLUMN_NAME"));
                    cb.setTypeName(rs.getString("TYPE_NAME"));
                    cb.setTypeLength(rs.getInt("COLUMN_SIZE"));
                    cb.setTypePrecision(rs.getInt("DECIMAL_DIGITS"));
                    cb.setNullable(rs.getBoolean("NULLABLE"));
                    cb.setDefaultValue(rs.getObject("COLUMN_DEF").toString());
                    columnList.add(cb.build());
                }
            }
            return null;
        });

        if (columnList.isEmpty()) {
            return null;
        }

        DbTable.Builder tb = new DbTable.Builder();
        tb.setName(tableName);
        tb.setColumns(columnList);
        return tb.build();
    }

    public boolean tableExist(String tableName) throws SQLException {
        return db.executeMetaData(metaData -> {
            try (ResultSet rs = metaData.getTables(null, null, tableName.toUpperCase(), new String[]{"TABLE"})) {
                return rs.next();
            }
        });
    }

}
