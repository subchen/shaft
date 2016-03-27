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

import javax.annotation.Nullable;
import java.util.regex.Pattern;

public final class PagelistSql {

    // 从一个标准的select语句中，生成一个 select count(*) 语句
    public static String getSelectCountSQL(String sql) {
        String countSQL = sql.replaceAll("\\s+", " ");
        int pos = countSQL.toLowerCase().indexOf(" from ");
        countSQL = countSQL.substring(pos);

        pos = countSQL.toLowerCase().lastIndexOf(" order by ");
        int lastPos = countSQL.toLowerCase().lastIndexOf(")");
        if (pos != -1 && pos > lastPos) {
            countSQL = countSQL.substring(0, pos);
        }

        String regex = "(left|right|inner) join (fetch )?\\w+(\\.\\w+)*";
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        countSQL = p.matcher(countSQL).replaceAll("");

        countSQL = "select count(*) " + countSQL;
        return countSQL;
    }

    /**
     * @param sql         原始 sql
     * @param offset      分页开始记录（从 0 开始, 等价于 (pageNo-1)*pageSize）
     * @param limit       返回数量
     * @param productName 数据库名称(来自于 DatabaseMetaData.getDatabaseProductName())
     * @return  null if not supported
     */
    @Nullable
    public static String getSelectPageSQL(String sql, int offset, int limit, String productName) {
        if ("MySQL".equals(productName)) {
            if (offset > 0) {
                return sql + " limit " + offset + "," + limit;
            } else {
                return sql + " limit " + limit;
            }
        }

        if ("Oracle".equals(productName)) {
            //@formatter:off
            sql = "select * from ("
                    + "  select t.*, ROWNUM row from ("
                    + sql
                    + "  ) t where ROWNUM <= " + (offset + limit) + ")";
            //@formatter:on
            if (offset > 0) {
                sql = sql + " where row > " + offset;
            }
            return sql;
        }


        if ("H2".equals(productName)) {
            sql = sql + " limit " + limit;
            if (offset > 0) {
                sql = sql + " offset " + offset;
            }
            return sql;
        }


        if ("Microsoft SQL Server".equals(productName)) {
            if (offset == 0) {
                sql = "select top " + limit + " * from (" + sql + ") as temp";
            } else {
                sql = sql.replaceAll("\\s+", " ");
                // 从原始 sql 中获取 order by 子句
                int orderbyPos = sql.toLowerCase().lastIndexOf(" order by ");
                String sorts = null;
                if (orderbyPos > 0) {
                    sorts = sql.substring(orderbyPos);
                    if (sorts.indexOf(")") > 0) {
                        sorts = null; // skip the nested order by
                    }
                }
                if (sorts == null) {
                    //sorts = "order by id";
                    return null;
                }
                //@formatter:off
                sql = "select * from ("
                        + "  select top " + (offset + limit) + " row_number() over(" + sorts + ") as row, * from (" + sql + ")"
                        + ") as temp where row > " + offset;
                //@formatter:on
            }
            return sql;
        }

        // unsupported database
        return null;
    }
}
