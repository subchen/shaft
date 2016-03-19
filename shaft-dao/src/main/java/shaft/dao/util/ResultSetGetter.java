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
package shaft.dao.util;

import jetbrick.util.ClassUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetGetter {

    public static <T> T getValue(ResultSet rs, String name, Class<T> requiredType) throws SQLException {
        return getValue(rs, rs.findColumn(name), requiredType);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getValue(ResultSet rs, int index, Class<T> requiredType) throws SQLException {
        if (requiredType == null) {
            return (T) rs.getObject(index);
        }

        Object value;
        boolean wasNullCheck = false;

        requiredType = (Class<T>) ClassUtils.primitiveToWrapper(requiredType);

        // Explicitly extract typed value, as far as possible.
        if (requiredType == String.class) {
            value = rs.getString(index);
        } else if (requiredType == Integer.class) {
            value = Integer.valueOf(rs.getInt(index));
            wasNullCheck = true;
        } else if (requiredType == Double.class) {
            value = new Double(rs.getDouble(index));
            wasNullCheck = true;
        } else if (requiredType == Boolean.class) {
            value = (rs.getBoolean(index) ? Boolean.TRUE : Boolean.FALSE);
            wasNullCheck = true;
        } else if (requiredType == java.sql.Date.class) {
            value = rs.getDate(index);
        } else if (requiredType == java.sql.Time.class) {
            value = rs.getTime(index);
        } else if (requiredType == java.sql.Timestamp.class) {
            value = rs.getTimestamp(index);
        } else if (requiredType == java.util.Date.class) {
            value = new java.util.Date(rs.getTimestamp(index).getTime());
        } else if (requiredType == Byte.class) {
            value = Byte.valueOf(rs.getByte(index));
            wasNullCheck = true;
        } else if (requiredType == Short.class) {
            value = Short.valueOf(rs.getShort(index));
            wasNullCheck = true;
        } else if (requiredType == Long.class) {
            value = Long.valueOf(rs.getLong(index));
            wasNullCheck = true;
        } else if (requiredType == Float.class) {
            value = new Float(rs.getFloat(index));
            wasNullCheck = true;
        } else if (requiredType == Number.class) {
            value = new Double(rs.getDouble(index));
            wasNullCheck = true;
        } else if (requiredType == byte[].class) {
            value = rs.getBytes(index);
        } else if (requiredType == java.math.BigDecimal.class) {
            value = rs.getBigDecimal(index);
        } else if (requiredType == java.sql.Blob.class) {
            value = rs.getBlob(index);
        } else if (requiredType == java.sql.Clob.class) {
            value = rs.getClob(index);
        } else if (requiredType == java.net.URL.class) {
            value = rs.getURL(index);
        } else {
            // Some unknown type desired -> rely on getObject.
            value = rs.getObject(index);
        }

        // Perform was-null check if demanded (for results that the
        // JDBC driver returns as primitives).
        if (wasNullCheck && rs.wasNull()) {
            value = null;
        }
        return (T) value;
    }

}
