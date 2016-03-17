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

import java.sql.CallableStatement;
import java.sql.SQLException;
import jetbrick.util.ClassUtils;

public final class CallableStatementGetter {

    public static Object getValue(CallableStatement cs, int index, Class<?> requiredType) throws SQLException {
        Object value = null;
        boolean wasNullCheck = false;

        if (requiredType == null) {
            return cs.getObject(index);
        }

        requiredType = ClassUtils.primitiveToWrapper(requiredType);

        // Explicitly extract typed value, as far as possible.
        if (requiredType == String.class) {
            value = cs.getString(index);
        } else if (requiredType == Integer.class) {
            value = Integer.valueOf(cs.getInt(index));
            wasNullCheck = true;
        } else if (requiredType == Double.class) {
            value = new Double(cs.getDouble(index));
            wasNullCheck = true;
        } else if (requiredType == Boolean.class) {
            value = (cs.getBoolean(index) ? Boolean.TRUE : Boolean.FALSE);
            wasNullCheck = true;
        } else if (requiredType == java.sql.Date.class) {
            value = cs.getDate(index);
        } else if (requiredType == java.sql.Time.class) {
            value = cs.getTime(index);
        } else if (requiredType == java.sql.Timestamp.class) {
            value = cs.getTimestamp(index);
        } else if (requiredType == java.util.Date.class) {
            value = new java.util.Date(cs.getTimestamp(index).getTime());
        } else if (requiredType == Byte.class) {
            value = Byte.valueOf(cs.getByte(index));
            wasNullCheck = true;
        } else if (requiredType == Short.class) {
            value = Short.valueOf(cs.getShort(index));
            wasNullCheck = true;
        } else if (requiredType == Long.class) {
            value = Long.valueOf(cs.getLong(index));
            wasNullCheck = true;
        } else if (requiredType == Float.class) {
            value = new Float(cs.getFloat(index));
            wasNullCheck = true;
        } else if (requiredType == Number.class) {
            value = new Double(cs.getDouble(index));
            wasNullCheck = true;
        } else if (requiredType == byte[].class) {
            value = cs.getBytes(index);
        } else if (requiredType == java.math.BigDecimal.class) {
            value = cs.getBigDecimal(index);
        } else if (requiredType == java.sql.Blob.class) {
            value = cs.getBlob(index);
        } else if (requiredType == java.sql.Clob.class) {
            value = cs.getClob(index);
        } else if (requiredType == java.net.URL.class) {
            value = cs.getURL(index);
        } else {
            // Some unknown type desired -> rely on getObject.
            value = cs.getObject(index);
        }

        // Perform was-null check if demanded (for results that the
        // JDBC driver returns as primitives).
        if (wasNullCheck && value != null && cs.wasNull()) {
            value = null;
        }
        return value;
    }

}
