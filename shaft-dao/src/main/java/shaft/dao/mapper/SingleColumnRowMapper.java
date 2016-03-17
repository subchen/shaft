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
package shaft.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import shaft.dao.RowMapper;
import jetbrick.typecast.TypeCastUtils;

public final class SingleColumnRowMapper<T> implements RowMapper<T> {

    private Class<T> targetClass;

    public SingleColumnRowMapper(Class<T> targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    public T handle(ResultSet rs) throws SQLException {
        Object result = rs.getObject(1);
        return TypeCastUtils.convert(result, targetClass);
    }

}
