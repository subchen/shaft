package shaft.dao.dialect;

import jetbrick.util.StringUtils;

public final class PostgreSqlDialect extends Dialect {

    public static final String PRODUCT_NAME = "PostgreSQL";

    @Override
    public String getName() {
        return PRODUCT_NAME;
    }

    @Override
    protected String getQuotedIdentifier(String name) {
        return "\"" + name + "\"";
    }

    @Override
    public String valueEscape(String value) {
        return StringUtils.replace(value, "'", "\\'");
    }

    @Override
    public String getTableDropSQL(String table) {
        return String.format("drop table %s", getIdentifier(table));
    }

    @Override
    public String getTableRenameSQL(String oldName, String newName) {
        return String.format("rename table %s to %s", getIdentifier(oldName), getIdentifier(newName));
    }

    @Override
    public String getColumnAddSQL(String table, String columnDefinition, String columnPosition) {
        return String.format("alter table %s add %s", getIdentifier(table), columnDefinition);
    }

    @Override
    public String getColumnModifySQL(String table, String columnDefinition, String columnPosition) {
        return String.format("alter table %s modify column %s", getIdentifier(table), columnDefinition);
    }

    @Override
    public String getColumnDropSQL(String table, String column) {
        return String.format("alter table %s drop column %s", getIdentifier(table), getIdentifier(column));
    }

    @Override
    public String getPaginationSQL(String sql, int offset, int limit) {
        if (offset > 0) {
            return sql + " limit " + limit + " offset " + offset;
        } else {
            return sql + " limit " + limit;
        }
    }

    @Override
    public boolean supportsSequences() {
        return true;
    }

    @Override
    protected void initializeReservedWords() {
        reservedWords.add("ADMIN");
        reservedWords.add("ALIAS");
        reservedWords.add("ALL");
        reservedWords.add("ALLOCATE");
        reservedWords.add("ANALYSE");
        reservedWords.add("ANALYZE");
        reservedWords.add("AND");
        reservedWords.add("ANY");
        reservedWords.add("ARE");
        reservedWords.add("ARRAY");
        reservedWords.add("AS");
        reservedWords.add("ASC");
        reservedWords.add("AUTHORIZATION");
        reservedWords.add("BINARY");
        reservedWords.add("BLOB");
        reservedWords.add("BOTH");
        reservedWords.add("BREADTH");
        reservedWords.add("CALL");
        reservedWords.add("CASCADED");
        reservedWords.add("CASE");
        reservedWords.add("CAST");
        reservedWords.add("CATALOG");
        reservedWords.add("CHECK");
        reservedWords.add("CLOB");
        reservedWords.add("COLLATE");
        reservedWords.add("COLLATION");
        reservedWords.add("COLUMN");
        reservedWords.add("COMPLETION");
        reservedWords.add("CONNECT");
        reservedWords.add("CONNECTION");
        reservedWords.add("CONSTRAINT");
        reservedWords.add("CONSTRUCTOR");
        reservedWords.add("CONTINUE");
        reservedWords.add("CORRESPONDING");
        reservedWords.add("CREATE");
        reservedWords.add("CROSS");
        reservedWords.add("CUBE");
        reservedWords.add("CURRENT");
        reservedWords.add("CURRENT_DATE");
        reservedWords.add("CURRENT_PATH");
        reservedWords.add("CURRENT_ROLE");
        reservedWords.add("CURRENT_TIME");
        reservedWords.add("CURRENT_TIMESTAMP");
        reservedWords.add("CURRENT_USER");
        reservedWords.add("DATE");
        reservedWords.add("DEFAULT");
        reservedWords.add("DEFERRABLE");
        reservedWords.add("DEPTH");
        reservedWords.add("DEREF");
        reservedWords.add("DESC");
        reservedWords.add("DESCRIBE");
        reservedWords.add("DESCRIPTOR");
        reservedWords.add("DESTROY");
        reservedWords.add("DESTRUCTOR");
        reservedWords.add("DETERMINISTIC");
        reservedWords.add("DIAGNOSTICS");
        reservedWords.add("DICTIONARY");
        reservedWords.add("DISCONNECT");
        reservedWords.add("DISTINCT");
        reservedWords.add("DO");
        reservedWords.add("DYNAMIC");
        reservedWords.add("ELSE");
        reservedWords.add("END");
        reservedWords.add("END-EXEC");
        reservedWords.add("EQUALS");
        reservedWords.add("EVERY");
        reservedWords.add("EXCEPT");
        reservedWords.add("EXCEPTION");
        reservedWords.add("EXEC");
        reservedWords.add("FALSE");
        reservedWords.add("FIRST");
        reservedWords.add("FOR");
        reservedWords.add("FOREIGN");
        reservedWords.add("FOUND");
        reservedWords.add("FREE");
        reservedWords.add("FROM");
        reservedWords.add("FULL");
        reservedWords.add("GENERAL");
        reservedWords.add("GO");
        reservedWords.add("GOTO");
        reservedWords.add("GRANT");
        reservedWords.add("GROUP");
        reservedWords.add("GROUPING");
        reservedWords.add("HAVING");
        reservedWords.add("HOST");
        reservedWords.add("IDENTITY");
        reservedWords.add("IGNORE");
        reservedWords.add("ILIKE");
        reservedWords.add("IN");
        reservedWords.add("INDICATOR");
        reservedWords.add("INITIALIZE");
        reservedWords.add("INITIALLY");
        reservedWords.add("INNER");
        reservedWords.add("INTERSECT");
        reservedWords.add("INTO");
        reservedWords.add("IS");
        reservedWords.add("ISNULL");
        reservedWords.add("ITERATE");
        reservedWords.add("JOIN");
        reservedWords.add("LARGE");
        reservedWords.add("LAST");
        reservedWords.add("LATERAL");
        reservedWords.add("LEADING");
        reservedWords.add("LEFT");
        reservedWords.add("LESS");
        reservedWords.add("LIKE");
        reservedWords.add("LIMIT");
        reservedWords.add("LOCALTIME");
        reservedWords.add("LOCALTIMESTAMP");
        reservedWords.add("LOCATOR");
        reservedWords.add("MAP");
        reservedWords.add("MODIFIES");
        reservedWords.add("MODIFY");
        reservedWords.add("MODULE");
        reservedWords.add("NATURAL");
        reservedWords.add("NCLOB");
        reservedWords.add("NEW");
        reservedWords.add("NOT");
        reservedWords.add("NOTNULL");
        reservedWords.add("NULL");
        reservedWords.add("OBJECT");
        reservedWords.add("OFF");
        reservedWords.add("OFFSET");
        reservedWords.add("OLD");
        reservedWords.add("ON");
        reservedWords.add("ONLY");
        reservedWords.add("OPEN");
        reservedWords.add("OPERATION");
        reservedWords.add("OR");
        reservedWords.add("ORDER");
        reservedWords.add("ORDINALITY");
        reservedWords.add("OUTER");
        reservedWords.add("OUTPUT");
        reservedWords.add("PAD");
        reservedWords.add("PARAMETER");
        reservedWords.add("PARAMETERS");
        reservedWords.add("PLACING");
        reservedWords.add("POSTFIX");
        reservedWords.add("PREFIX");
        reservedWords.add("PREORDER");
        reservedWords.add("PRESERVE");
        reservedWords.add("PRIMARY");
        reservedWords.add("PUBLIC");
        reservedWords.add("READS");
        reservedWords.add("RECURSIVE");
        reservedWords.add("REF");
        reservedWords.add("REFERENCES");
        reservedWords.add("REFERENCING");
        reservedWords.add("RESULT");
        reservedWords.add("RETURN");
        reservedWords.add("RIGHT");
        reservedWords.add("ROLE");
        reservedWords.add("ROLLUP");
        reservedWords.add("ROUTINE");
        reservedWords.add("ROWS");
        reservedWords.add("SAVEPOINT");
        reservedWords.add("SCOPE");
        reservedWords.add("SEARCH");
        reservedWords.add("SECTION");
        reservedWords.add("SELECT");
        reservedWords.add("SESSION_USER");
        reservedWords.add("SETS");
        reservedWords.add("SIZE");
        reservedWords.add("SOME");
        reservedWords.add("SPACE");
        reservedWords.add("SPECIFIC");
        reservedWords.add("SPECIFICTYPE");
        reservedWords.add("SQL");
        reservedWords.add("SQLCODE");
        reservedWords.add("SQLERROR");
        reservedWords.add("SQLEXCEPTION");
        reservedWords.add("SQLSTATE");
        reservedWords.add("SQLWARNING");
        reservedWords.add("STATE");
        reservedWords.add("STATIC");
        reservedWords.add("STRUCTURE");
        reservedWords.add("SYSTEM_USER");
        reservedWords.add("TABLE");
        reservedWords.add("TERMINATE");
        reservedWords.add("THAN");
        reservedWords.add("THEN");
        reservedWords.add("TIMEZONE_HOUR");
        reservedWords.add("TIMEZONE_MINUTE");
        reservedWords.add("TO");
        reservedWords.add("TRAILING");
        reservedWords.add("TRANSLATION");
        reservedWords.add("TRUE");
        reservedWords.add("UNDER");
        reservedWords.add("UNION");
        reservedWords.add("UNIQUE");
        reservedWords.add("UNNEST");
        reservedWords.add("USER");
        reservedWords.add("USING");
        reservedWords.add("VALUE");
        reservedWords.add("VARIABLE");
        reservedWords.add("VERBOSE");
        reservedWords.add("WHEN");
        reservedWords.add("WHENEVER");
        reservedWords.add("WHERE");
    }
}
