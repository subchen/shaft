package shaft.dao.dialect;

import jetbrick.util.StringUtils;

public final class SQLServerDialect extends Dialect {

    public static final String PRODUCT_NAME = "Microsoft SQL Server";

    @Override
    public String getProductName() {
        return PRODUCT_NAME;
    }

    @Override
    protected String getQuotedIdentifier(String name) {
        return "[" + name + "]";
    }

    @Override
    public String valueEscape(String value) {
        return StringUtils.replace(value, "'", "''");
    }

    @Override
    public String getTableDropSQL(String table) {
        return String.format("drop table %s", table, getIdentifier(table));
    }

    @Override
    public String getTableRenameSQL(String oldName, String newName) {
        return String.format("alter table %s rename to %s", getIdentifier(oldName), getIdentifier(newName));
    }

    @Override
    public String getColumnAddSQL(String table, String columnDefinition, String columnPosition) {
        return String.format("alter table %s add %s", getIdentifier(table), columnDefinition);
    }

    @Override
    public String getColumnModifySQL(String table, String columnDefinition, String columnPosition) {
        return String.format("alter table %s alter column %s", getIdentifier(table), columnDefinition);
    }

    @Override
    public String getColumnDropSQL(String table, String column) {
        return String.format("alter table %s drop column %s", getIdentifier(table), getIdentifier(column));
    }

    @Override
    public String getPaginationSQL(String sql, int offset, int limit) {
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

    @Override
    protected void initializeReservedWords() {
        reservedWords.add("ADD");
        reservedWords.add("ALL");
        reservedWords.add("ALTER");
        reservedWords.add("AND");
        reservedWords.add("ANY");
        reservedWords.add("AS");
        reservedWords.add("ASC");
        reservedWords.add("AUTHORIZATION");
        reservedWords.add("BACKUP");
        reservedWords.add("BEGIN");
        reservedWords.add("BETWEEN");
        reservedWords.add("BREAK");
        reservedWords.add("BROWSE");
        reservedWords.add("BULK");
        reservedWords.add("BY");
        reservedWords.add("CASCADE");
        reservedWords.add("CASE");
        reservedWords.add("CHECK");
        reservedWords.add("CHECKPOINT");
        reservedWords.add("CLOSE");
        reservedWords.add("CLUSTERED");
        reservedWords.add("COALESCE");
        reservedWords.add("COLLATE");
        reservedWords.add("COLUMN");
        reservedWords.add("COMMIT");
        reservedWords.add("COMPUTE");
        reservedWords.add("CONSTRAINT");
        reservedWords.add("CONTAINS");
        reservedWords.add("CONTAINSTABLE");
        reservedWords.add("CONTINUE");
        reservedWords.add("CONVERT");
        reservedWords.add("CREATE");
        reservedWords.add("CROSS");
        reservedWords.add("CURRENT");
        reservedWords.add("CURRENT_DATE");
        reservedWords.add("CURRENT_TIME");
        reservedWords.add("CURRENT_TIMESTAMP");
        reservedWords.add("CURRENT_USER");
        reservedWords.add("CURSOR");
        reservedWords.add("DATABASE");
        reservedWords.add("DBCC");
        reservedWords.add("DEALLOCATE");
        reservedWords.add("DECLARE");
        reservedWords.add("DEFAULT");
        reservedWords.add("DELETE");
        reservedWords.add("DENY");
        reservedWords.add("DESC");
        reservedWords.add("DISK");
        reservedWords.add("DISTINCT");
        reservedWords.add("DISTRIBUTED");
        reservedWords.add("DOUBLE");
        reservedWords.add("DROP");
        reservedWords.add("DUMMY");
        reservedWords.add("DUMP");
        reservedWords.add("ELSE");
        reservedWords.add("END");
        reservedWords.add("ERRLVL");
        reservedWords.add("ESCAPE");
        reservedWords.add("EXCEPT");
        reservedWords.add("EXEC");
        reservedWords.add("EXECUTE");
        reservedWords.add("EXISTS");
        reservedWords.add("EXIT");
        reservedWords.add("FETCH");
        reservedWords.add("FILE");
        reservedWords.add("FILLFACTOR");
        reservedWords.add("FOR");
        reservedWords.add("FOREIGN");
        reservedWords.add("FREETEXT");
        reservedWords.add("FREETEXTTABLE");
        reservedWords.add("FROM");
        reservedWords.add("FULL");
        reservedWords.add("FUNCTION");
        reservedWords.add("GOTO");
        reservedWords.add("GRANT");
        reservedWords.add("GROUP");
        reservedWords.add("HAVING");
        reservedWords.add("HOLDLOCK");
        reservedWords.add("IDENTITY");
        reservedWords.add("IDENTITYCOL");
        reservedWords.add("IDENTITY_INSERT");
        reservedWords.add("IF");
        reservedWords.add("IN");
        reservedWords.add("INDEX");
        reservedWords.add("INNER");
        reservedWords.add("INSERT");
        reservedWords.add("INTERSECT");
        reservedWords.add("INTO");
        reservedWords.add("IS");
        reservedWords.add("JOIN");
        reservedWords.add("KEY");
        reservedWords.add("KILL");
        reservedWords.add("LEFT");
        reservedWords.add("LIKE");
        reservedWords.add("LINENO");
        reservedWords.add("LOAD");
        reservedWords.add("NATIONAL");
        reservedWords.add("NOCHECK");
        reservedWords.add("NONCLUSTERED");
        reservedWords.add("NOT");
        reservedWords.add("NULL");
        reservedWords.add("NULLIF");
        reservedWords.add("OF");
        reservedWords.add("OFF");
        reservedWords.add("OFFSETS");
        reservedWords.add("ON");
        reservedWords.add("OPEN");
        reservedWords.add("OPENDATASOURCE");
        reservedWords.add("OPENQUERY");
        reservedWords.add("OPENROWSET");
        reservedWords.add("OPENXML");
        reservedWords.add("OPTION");
        reservedWords.add("OR");
        reservedWords.add("ORDER");
        reservedWords.add("OUTER");
        reservedWords.add("OVER");
        reservedWords.add("PERCENT");
        reservedWords.add("PLAN");
        reservedWords.add("PRECISION");
        reservedWords.add("PRIMARY");
        reservedWords.add("PRINT");
        reservedWords.add("PROC");
        reservedWords.add("PROCEDURE");
        reservedWords.add("PUBLIC");
        reservedWords.add("RAISERROR");
        reservedWords.add("READ");
        reservedWords.add("READTEXT");
        reservedWords.add("RECONFIGURE");
        reservedWords.add("REFERENCES");
        reservedWords.add("REPLICATION");
        reservedWords.add("RESTORE");
        reservedWords.add("RESTRICT");
        reservedWords.add("RETURN");
        reservedWords.add("REVOKE");
        reservedWords.add("RIGHT");
        reservedWords.add("ROLLBACK");
        reservedWords.add("ROWCOUNT");
        reservedWords.add("ROWGUIDCOL");
        reservedWords.add("RULE");
        reservedWords.add("SAVE");
        reservedWords.add("SCHEMA");
        reservedWords.add("SELECT");
        reservedWords.add("SESSION_USER");
        reservedWords.add("SET");
        reservedWords.add("SETUSER");
        reservedWords.add("SHUTDOWN");
        reservedWords.add("SOME");
        reservedWords.add("STATISTICS");
        reservedWords.add("SYSTEM_USER");
        reservedWords.add("TABLE");
        reservedWords.add("TEXTSIZE");
        reservedWords.add("THEN");
        reservedWords.add("TO");
        reservedWords.add("TOP");
        reservedWords.add("TRAN");
        reservedWords.add("TRANSACTION");
        reservedWords.add("TRIGGER");
        reservedWords.add("TRUNCATE");
        reservedWords.add("TSEQUAL");
        reservedWords.add("UNION");
        reservedWords.add("UNIQUE");
        reservedWords.add("UPDATE");
        reservedWords.add("UPDATETEXT");
        reservedWords.add("USE");
        reservedWords.add("USER");
        reservedWords.add("VALUES");
        reservedWords.add("VARYING");
        reservedWords.add("VIEW");
        reservedWords.add("WAITFOR");
        reservedWords.add("WHEN");
        reservedWords.add("WHERE");
        reservedWords.add("WHILE");
        reservedWords.add("WITH");
        reservedWords.add("WRITETEXT");
    }
}
