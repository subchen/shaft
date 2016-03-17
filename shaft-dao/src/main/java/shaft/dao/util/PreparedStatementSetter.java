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
package shaft.dao.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import jetbrick.util.ClassUtils;

public final class PreparedStatementSetter {

    public static void setValue(PreparedStatement ps, int index, Object value) throws SQLException {
        if (value == null) {
            ps.setObject(index, null);
            return;
        }
        Class<?> requiredType = value.getClass();
        requiredType = ClassUtils.primitiveToWrapper(requiredType);

        if (requiredType == String.class) {
            ps.setString(index, (String) value);
        } else if (requiredType == Integer.class) {
            ps.setInt(index, ((Number) value).intValue());
        } else if (requiredType == Float.class) {
            ps.setFloat(index, ((Number) value).floatValue());
        } else if (requiredType == Double.class) {
            ps.setDouble(index, ((Number) value).doubleValue());
        } else if (requiredType == Boolean.class) {
            ps.setBoolean(index, ((Boolean) value).booleanValue());
        } else if (requiredType == java.sql.Date.class) {
            ps.setDate(index, (java.sql.Date) value);
        } else if (requiredType == java.sql.Time.class) {
            ps.setTime(index, (java.sql.Time) value);
        } else if (requiredType == java.sql.Timestamp.class) {
            ps.setTimestamp(index, (java.sql.Timestamp) value);
        } else if (requiredType == java.util.Date.class) {
            ps.setTimestamp(index, new java.sql.Timestamp(((java.util.Date) value).getTime()));
        } else if (requiredType == Byte.class) {
            ps.setByte(index, ((Number) value).byteValue());
        } else if (requiredType == Short.class) {
            ps.setShort(index, ((Number) value).shortValue());
        } else if (requiredType == Long.class) {
            ps.setLong(index, ((Number) value).longValue());
        } else if (requiredType == java.math.BigDecimal.class) {
            ps.setBigDecimal(index, (java.math.BigDecimal) value);
        } else if (requiredType == Number.class) {
            ps.setDouble(index, ((Number) value).doubleValue());
        } else if (requiredType == byte[].class) {
            ps.setBytes(index, (byte[]) value);
        } else if (requiredType == java.sql.Blob.class) {
            ps.setBlob(index, (java.sql.Blob) value);
        } else if (requiredType == java.sql.Clob.class) {
            ps.setClob(index, (java.sql.Clob) value);
        } else if (requiredType == java.net.URL.class) {
            ps.setURL(index, (java.net.URL) value);
        } else {
            // Some unknown type desired -> rely on getObject.
            ps.setObject(index, value);
        }
    }
}
