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

import jetbrick.util.builder.ToStringBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DbTable {
    private String name;
    private List<DbColumn> columns;
    private Map<String, DbColumn> columnMap;

    public String getName() {
        return name;
    }

    public List<DbColumn> getColumns() {
        return columns;
    }

    public DbColumn getColumn(String name) {
        if (columnMap == null) {
            columnMap = new HashMap<>(columns.size());
            columns.forEach(c -> {
                columnMap.put(c.getName().toUpperCase(), c);
            });
        }
        return columnMap.get(name.toUpperCase());
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflection(this);
    }

    public static class Builder {
        private final DbTable obj = new DbTable();

        public void setName(String name) {
            obj.name = name;
        }

        public void setColumns(List<DbColumn> columns) {
            obj.columns = Collections.unmodifiableList(columns);
        }

        public DbTable build() {
            return obj;
        }
    }
}
