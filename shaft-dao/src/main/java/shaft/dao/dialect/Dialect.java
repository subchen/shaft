package shaft.dao.dialect;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public abstract class Dialect {
    private static final Map<String, Dialect> dialectMap = new HashMap<>();
    private static final Set<String> isoReservedWords = new HashSet<>(512);
    protected final Set<String> reservedWords = new HashSet<>(256);

    static {
        initReservedWordsSql92();
        initReservedWordsSql99();

        dialectMap.put(MysqlDialect.PRODUCT_NAME, new MysqlDialect());
        dialectMap.put(H2Dialect.PRODUCT_NAME, new H2Dialect());
        dialectMap.put(PostgreSqlDialect.PRODUCT_NAME, new PostgreSqlDialect());
        dialectMap.put(OracleDialect.PRODUCT_NAME, new OracleDialect());
        dialectMap.put(SQLServerDialect.PRODUCT_NAME, new SQLServerDialect());
    }

    public static Dialect create(String name) throws IllegalArgumentException {
        Dialect dialect = dialectMap.get(name);
        if (dialect == null) {
            throw new IllegalArgumentException("Unsupported database " + name);
        }
        return dialect;
    }

    public Dialect() {
        initializeReservedWords();
    }

    /**
     * 返回 DatabaseMetadata.getProductName()
     */
    public abstract String getName();

    /**
     * 数据库是否支持 Sequence
     */
    public boolean supportsSequences() {
        return false;
    }

    /**
     * 是否支持在添加字段的时候，指定字段位置
     */
    public boolean supportsColumnPosition() {
        return false;
    }

    /**
     * 哪些数据库字段运行指定长度.
     * @param type 具体的数据库字段类型
     */
    public boolean supportsColumnLength(String type) {
        Set<String> columnSet = new HashSet<>();
        columnSet.add("char");
        columnSet.add("nchar");
        columnSet.add("varchar");
        columnSet.add("nvarchar");
        columnSet.add("varchar2");
        columnSet.add("nvarchar2");
        columnSet.add("number");
        columnSet.add("numeric");
        columnSet.add("dec");
        columnSet.add("decimal");
        return columnSet.contains(type.toLowerCase());
    }

    /**
     * 哪些数据库字段运行指定精度.
     * @param type 具体的数据库字段类型
     */
    public boolean supportsColumnScale(String type) {
        Set<String> columnSet = new HashSet<>();
        columnSet.add("number");
        columnSet.add("numeric");
        columnSet.add("dec");
        columnSet.add("decimal");
        return columnSet.contains(type.toLowerCase());
    }

    /**
     * 将字段名/表名与SQL保留字冲突的名称进行 Wrapper
     */
    public String getIdentifier(String name) {
        String upperCaseName = name.toUpperCase();
        if (isoReservedWords.contains(upperCaseName)) {
            return getQuotedIdentifier(name);
        }
        if (reservedWords.contains(upperCaseName)) {
            return getQuotedIdentifier(name);
        }
        return name;
    }

    /**
     * 将字段名/表名进行 Wrapper
     */
    protected abstract String getQuotedIdentifier(String name);

    /**
     * SQL 语句中的字符串值，比如编码单引号(')为('')或者(\')
     */
    public abstract String valueEscape(String value);

    public abstract String getTableDropSQL(String table);

    public abstract String getTableRenameSQL(String oldName, String newName);

    public abstract String getColumnAddSQL(String table, String columnDefinition, String columnPosition);

    public abstract String getColumnModifySQL(String table, String columnDefinition, String columnPosition);

    public abstract String getColumnDropSQL(String table, String column);

    /**
     * 获取分页 SQL
     *
     * @param sql    原始 sql
     * @param offset 分页开始记录（从 0 开始, 等价于 (pageNo-1)*pageSize）
     * @param limit  返回数量
     * @return null if not supported
     */
    @Nullable
    public abstract String getPaginationSQL(String sql, int offset, int limit);

    /**
     * 初始化数据库专有保留字
     */
    protected abstract void initializeReservedWords();

    private static void initReservedWordsSql92() {
        isoReservedWords.add("ABSOLUTE");
        isoReservedWords.add("ACTION");
        isoReservedWords.add("ADD");
        isoReservedWords.add("ALL");
        isoReservedWords.add("ALLOCATE");
        isoReservedWords.add("ALTER");
        isoReservedWords.add("AND");
        isoReservedWords.add("ANY");
        isoReservedWords.add("ARE");
        isoReservedWords.add("AS");
        isoReservedWords.add("ASC");
        isoReservedWords.add("ASSERTION");
        isoReservedWords.add("AT");
        isoReservedWords.add("AUTHORIZATION");
        isoReservedWords.add("AVG");
        isoReservedWords.add("BEGIN");
        isoReservedWords.add("BETWEEN");
        isoReservedWords.add("BIT");
        isoReservedWords.add("BIT_LENGTH");
        isoReservedWords.add("BOTH");
        isoReservedWords.add("BY");
        isoReservedWords.add("CASCADE");
        isoReservedWords.add("CASCADED");
        isoReservedWords.add("CASE");
        isoReservedWords.add("CAST");
        isoReservedWords.add("CATALOG");
        isoReservedWords.add("CHAR");
        isoReservedWords.add("CHARACTER");
        isoReservedWords.add("CHARACTER_LENGTH");
        isoReservedWords.add("CHAR_LENGTH");
        isoReservedWords.add("CHECK");
        isoReservedWords.add("CLOSE");
        isoReservedWords.add("COALESCE");
        isoReservedWords.add("COLLATE");
        isoReservedWords.add("COLLATION");
        isoReservedWords.add("COLUMN");
        isoReservedWords.add("COMMIT");
        isoReservedWords.add("CONNECT");
        isoReservedWords.add("CONNECTION");
        isoReservedWords.add("CONSTRAINT");
        isoReservedWords.add("CONSTRAINTS");
        isoReservedWords.add("CONTINUE");
        isoReservedWords.add("CONVERT");
        isoReservedWords.add("CORRESPONDING");
        isoReservedWords.add("COUNT");
        isoReservedWords.add("CREATE");
        isoReservedWords.add("CROSS");
        isoReservedWords.add("CURRENT");
        isoReservedWords.add("CURRENT_DATE");
        isoReservedWords.add("CURRENT_TIME");
        isoReservedWords.add("CURRENT_TIMESTAMP");
        isoReservedWords.add("CURRENT_USER");
        isoReservedWords.add("CURSOR");
        isoReservedWords.add("DATE");
        isoReservedWords.add("DAY");
        isoReservedWords.add("DEALLOCATE");
        isoReservedWords.add("DEC");
        isoReservedWords.add("DECIMAL");
        isoReservedWords.add("DECLARE");
        isoReservedWords.add("DEFAULT");
        isoReservedWords.add("DEFERRABLE");
        isoReservedWords.add("DEFERRED");
        isoReservedWords.add("DELETE");
        isoReservedWords.add("DESC");
        isoReservedWords.add("DESCRIBE");
        isoReservedWords.add("DESCRIPTOR");
        isoReservedWords.add("DIAGNOSTICS");
        isoReservedWords.add("DISCONNECT");
        isoReservedWords.add("DISTINCT");
        isoReservedWords.add("DOMAIN");
        isoReservedWords.add("DOUBLE");
        isoReservedWords.add("DROP");
        isoReservedWords.add("ELSE");
        isoReservedWords.add("END");
        isoReservedWords.add("END-EXEC");
        isoReservedWords.add("ESCAPE");
        isoReservedWords.add("EXCEPT");
        isoReservedWords.add("EXCEPTION");
        isoReservedWords.add("EXEC");
        isoReservedWords.add("EXECUTE");
        isoReservedWords.add("EXISTS");
        isoReservedWords.add("EXTERNAL");
        isoReservedWords.add("EXTRACT");
        isoReservedWords.add("FALSE");
        isoReservedWords.add("FETCH");
        isoReservedWords.add("FIRST");
        isoReservedWords.add("FLOAT");
        isoReservedWords.add("FOR");
        isoReservedWords.add("FOREIGN");
        isoReservedWords.add("FOUND");
        isoReservedWords.add("FROM");
        isoReservedWords.add("FULL");
        isoReservedWords.add("GET");
        isoReservedWords.add("GLOBAL");
        isoReservedWords.add("GO");
        isoReservedWords.add("GOTO");
        isoReservedWords.add("GRANT");
        isoReservedWords.add("GROUP");
        isoReservedWords.add("HAVING");
        isoReservedWords.add("HOUR");
        isoReservedWords.add("IDENTITY");
        isoReservedWords.add("IMMEDIATE");
        isoReservedWords.add("IN");
        isoReservedWords.add("INDICATOR");
        isoReservedWords.add("INITIALLY");
        isoReservedWords.add("INNER");
        isoReservedWords.add("INPUT");
        isoReservedWords.add("INSENSITIVE");
        isoReservedWords.add("INSERT");
        isoReservedWords.add("INT");
        isoReservedWords.add("INTEGER");
        isoReservedWords.add("INTERSECT");
        isoReservedWords.add("INTERVAL");
        isoReservedWords.add("INTO");
        isoReservedWords.add("IS");
        isoReservedWords.add("ISOLATION");
        isoReservedWords.add("JOIN");
        isoReservedWords.add("KEY");
        isoReservedWords.add("LANGUAGE");
        isoReservedWords.add("LAST");
        isoReservedWords.add("LEADING");
        isoReservedWords.add("LEFT");
        isoReservedWords.add("LEVEL");
        isoReservedWords.add("LIKE");
        isoReservedWords.add("LOCAL");
        isoReservedWords.add("LOWER");
        isoReservedWords.add("MATCH");
        isoReservedWords.add("MAX");
        isoReservedWords.add("MIN");
        isoReservedWords.add("MINUTE");
        isoReservedWords.add("MODULE");
        isoReservedWords.add("MONTH");
        isoReservedWords.add("NAMES");
        isoReservedWords.add("NATIONAL");
        isoReservedWords.add("NATURAL");
        isoReservedWords.add("NCHAR");
        isoReservedWords.add("NEXT");
        isoReservedWords.add("NO");
        isoReservedWords.add("NOT");
        isoReservedWords.add("NULL");
        isoReservedWords.add("NULLIF");
        isoReservedWords.add("NUMERIC");
        isoReservedWords.add("OCTET_LENGTH");
        isoReservedWords.add("OF");
        isoReservedWords.add("ON");
        isoReservedWords.add("ONLY");
        isoReservedWords.add("OPEN");
        isoReservedWords.add("OPTION");
        isoReservedWords.add("OR");
        isoReservedWords.add("ORDER");
        isoReservedWords.add("OUTER");
        isoReservedWords.add("OUTPUT");
        isoReservedWords.add("OVERLAPS");
        isoReservedWords.add("PAD");
        isoReservedWords.add("PARTIAL");
        isoReservedWords.add("POSITION");
        isoReservedWords.add("PRECISION");
        isoReservedWords.add("PREPARE");
        isoReservedWords.add("PRESERVE");
        isoReservedWords.add("PRIMARY");
        isoReservedWords.add("PRIOR");
        isoReservedWords.add("PRIVILEGES");
        isoReservedWords.add("PROCEDURE");
        isoReservedWords.add("PUBLIC");
        isoReservedWords.add("READ");
        isoReservedWords.add("REAL");
        isoReservedWords.add("REFERENCES");
        isoReservedWords.add("RELATIVE");
        isoReservedWords.add("RESTRICT");
        isoReservedWords.add("REVOKE");
        isoReservedWords.add("RIGHT");
        isoReservedWords.add("ROLLBACK");
        isoReservedWords.add("ROWS");
        isoReservedWords.add("SCHEMA");
        isoReservedWords.add("SCROLL");
        isoReservedWords.add("SECOND");
        isoReservedWords.add("SECTION");
        isoReservedWords.add("SELECT");
        isoReservedWords.add("SESSION");
        isoReservedWords.add("SESSION_USER");
        isoReservedWords.add("SET");
        isoReservedWords.add("SIZE");
        isoReservedWords.add("SMALLINT");
        isoReservedWords.add("SOME");
        isoReservedWords.add("SPACE");
        isoReservedWords.add("SQL");
        isoReservedWords.add("SQLCODE");
        isoReservedWords.add("SQLERROR");
        isoReservedWords.add("SQLSTATE");
        isoReservedWords.add("SUBSTRING");
        isoReservedWords.add("SUM");
        isoReservedWords.add("SYSTEM_USER");
        isoReservedWords.add("TABLE");
        isoReservedWords.add("TEMPORARY");
        isoReservedWords.add("THEN");
        isoReservedWords.add("TIME");
        isoReservedWords.add("DATETIME");
        isoReservedWords.add("TIMEZONE_HOUR");
        isoReservedWords.add("TIMEZONE_MINUTE");
        isoReservedWords.add("TO");
        isoReservedWords.add("TRAILING");
        isoReservedWords.add("TRANSACTION");
        isoReservedWords.add("TRANSLATE");
        isoReservedWords.add("TRANSLATION");
        isoReservedWords.add("TRIM");
        isoReservedWords.add("TRUE");
        isoReservedWords.add("UNION");
        isoReservedWords.add("UNIQUE");
        isoReservedWords.add("UNKNOWN");
        isoReservedWords.add("UPDATE");
        isoReservedWords.add("UPPER");
        isoReservedWords.add("USAGE");
        isoReservedWords.add("USER");
        isoReservedWords.add("USING");
        isoReservedWords.add("VALUE");
        isoReservedWords.add("VALUES");
        isoReservedWords.add("VARCHAR");
        isoReservedWords.add("VARYING");
        isoReservedWords.add("VIEW");
        isoReservedWords.add("WHEN");
        isoReservedWords.add("WHENEVER");
        isoReservedWords.add("WHERE");
        isoReservedWords.add("WITH");
        isoReservedWords.add("WORK");
        isoReservedWords.add("WRITE");
        isoReservedWords.add("YEAR");
        isoReservedWords.add("ZONE");
    }

    private static void initReservedWordsSql99() {
        isoReservedWords.add("ADMIN");
        isoReservedWords.add("AFTER");
        isoReservedWords.add("AGGREGATE");
        isoReservedWords.add("ALIAS");
        isoReservedWords.add("ARRAY");
        isoReservedWords.add("BEFORE");
        isoReservedWords.add("BINARY");
        isoReservedWords.add("BLOB");
        isoReservedWords.add("BOOLEAN");
        isoReservedWords.add("BREADTH");
        isoReservedWords.add("CALL");
        isoReservedWords.add("CLASS");
        isoReservedWords.add("CLOB");
        isoReservedWords.add("COMPLETION");
        isoReservedWords.add("CONSTRUCTOR");
        isoReservedWords.add("CUBE");
        isoReservedWords.add("CURRENT_PATH");
        isoReservedWords.add("CURRENT_ROLE");
        isoReservedWords.add("CYCLE");
        isoReservedWords.add("DATA");
        isoReservedWords.add("DEPTH");
        isoReservedWords.add("DEREF");
        isoReservedWords.add("DESTROY");
        isoReservedWords.add("DESTRUCTOR");
        isoReservedWords.add("DETERMINISTIC");
        isoReservedWords.add("DICTIONARY");
        isoReservedWords.add("DYNAMIC");
        isoReservedWords.add("EACH");
        isoReservedWords.add("EQUALS");
        isoReservedWords.add("EVERY");
        isoReservedWords.add("FREE");
        isoReservedWords.add("FUNCTION");
        isoReservedWords.add("GENERAL");
        isoReservedWords.add("GROUPING");
        isoReservedWords.add("HOST");
        isoReservedWords.add("IGNORE");
        isoReservedWords.add("INITIALIZE");
        isoReservedWords.add("INOUT");
        isoReservedWords.add("ITERATE");
        isoReservedWords.add("LARGE");
        isoReservedWords.add("LATERAL");
        isoReservedWords.add("LESS");
        isoReservedWords.add("LIMIT");
        isoReservedWords.add("LOCALTIME");
        isoReservedWords.add("LOCALTIMESTAMP");
        isoReservedWords.add("LOCATOR");
        isoReservedWords.add("MAP");
        isoReservedWords.add("MODIFIES");
        isoReservedWords.add("MODIFY");
        isoReservedWords.add("NCLOB");
        isoReservedWords.add("NEW");
        isoReservedWords.add("NONE");
        isoReservedWords.add("OBJECT");
        isoReservedWords.add("OFF");
        isoReservedWords.add("OLD");
        isoReservedWords.add("OPERATION");
        isoReservedWords.add("ORDINALITY");
        isoReservedWords.add("OUT");
        isoReservedWords.add("PARAMETER");
        isoReservedWords.add("PARAMETERS");
        isoReservedWords.add("PATH");
        isoReservedWords.add("POSTFIX");
        isoReservedWords.add("PREFIX");
        isoReservedWords.add("PREORDER");
        isoReservedWords.add("READS");
        isoReservedWords.add("RECURSIVE");
        isoReservedWords.add("REF");
        isoReservedWords.add("REFERENCING");
        isoReservedWords.add("RESULT");
        isoReservedWords.add("RETURN");
        isoReservedWords.add("RETURNS");
        isoReservedWords.add("ROLE");
        isoReservedWords.add("ROLLUP");
        isoReservedWords.add("ROUTINE");
        isoReservedWords.add("ROW");
        isoReservedWords.add("SAVEPOINT");
        isoReservedWords.add("SCOPE");
        isoReservedWords.add("SEARCH");
        isoReservedWords.add("SEQUENCE");
        isoReservedWords.add("SETS");
        isoReservedWords.add("SPECIFIC");
        isoReservedWords.add("SPECIFICTYPE");
        isoReservedWords.add("SQLEXCEPTION");
        isoReservedWords.add("SQLWARNING");
        isoReservedWords.add("START");
        isoReservedWords.add("STATE");
        isoReservedWords.add("STATEMENT");
        isoReservedWords.add("STATIC");
        isoReservedWords.add("STRUCTURE");
        isoReservedWords.add("TERMINATE");
        isoReservedWords.add("THAN");
        isoReservedWords.add("TREAT");
        isoReservedWords.add("TRIGGER");
        isoReservedWords.add("UNDER");
        isoReservedWords.add("UNNEST");
        isoReservedWords.add("VARIABLE");
        isoReservedWords.add("WITHOUT");
    }

}
