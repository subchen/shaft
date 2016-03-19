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
package shaft.dao.handler;

import shaft.dao.ResultSetHandler;
import shaft.dao.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PagelistHandler<T> implements ResultSetHandler<List<T>> {

    private RowMapper<T> mapper;
    private int first;
    private int max;

    public PagelistHandler(RowMapper<T> mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<T> handle(ResultSet rs) throws SQLException {
        if (first > 1) {
            boolean succ = rs.absolute(first - 1);
            if (!succ) {
                return Collections.<T>emptyList();
            }
        }

        List<T> rows = new ArrayList<T>();
        while (rs.next()) {
            rows.add(mapper.handle(rs));
            if (max > 0 && rows.size() >= max) {
                break;
            }
        }
        return rows;
    }

    public void setFirstResult(int first) {
        this.first = first;
    }

    public void setMaxResults(int max) {
        this.max = max;
    }
}
