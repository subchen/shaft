package shaft.dao.dialect;

import jetbrick.util.StringUtils;

public final class OracleDialect extends Dialect {

    public static final String PRODUCT_NAME = "Oracle";

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
        return StringUtils.replace(value, "'", "''");
    }

    @Override
    public String getTableDropSQL(String table) {
        return String.format("drop table %s", getIdentifier(table));
    }

    @Override
    public String getTableRenameSQL(String oldName, String newName) {
        return String.format("alter table %s rename to %s", getIdentifier(oldName), getIdentifier(newName));
    }

    @Override
    public String getColumnAddSQL(String table, String columnDefinition, String columnPosition) {
        return String.format("alter table %s add column %s", getIdentifier(table), columnDefinition);
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
        //@formatter:off
        sql = "select * from ("
            + "  select t.*, ROWNUM row from ("
            +      sql
            + "  ) t where ROWNUM <= " + (offset + limit) + ")";
        //@formatter:on
        if (offset > 0) {
            sql = sql + " where row > " + offset;
        }
        return sql;
    }

    @Override
    public boolean supportsSequences() {
        return true;
    }

    @Override
    protected void initializeReservedWords() {
        reservedWords.add("ACCESS");
        reservedWords.add("ADD");
        reservedWords.add("ALL");
        reservedWords.add("ALTER");
        reservedWords.add("AND");
        reservedWords.add("ANY");
        reservedWords.add("AS");
        reservedWords.add("ASC");
        reservedWords.add("AUDIT");
        reservedWords.add("BETWEEN");
        reservedWords.add("BY");
        reservedWords.add("CHAR");
        reservedWords.add("CHECK");
        reservedWords.add("CLUSTER");
        reservedWords.add("COLUMN");
        reservedWords.add("COMMENT");
        reservedWords.add("COMPRESS");
        reservedWords.add("CONNECT");
        reservedWords.add("CREATE");
        reservedWords.add("CURRENT");
        reservedWords.add("DATE");
        reservedWords.add("DECIMAL");
        reservedWords.add("DEFAULT");
        reservedWords.add("DELETE");
        reservedWords.add("DESC");
        reservedWords.add("DISTINCT");
        reservedWords.add("DROP");
        reservedWords.add("ELSE");
        reservedWords.add("EXCLUSIVE");
        reservedWords.add("EXISTS");
        reservedWords.add("FILE");
        reservedWords.add("FLOAT");
        reservedWords.add("FOR");
        reservedWords.add("FROM");
        reservedWords.add("GRANT");
        reservedWords.add("GROUP");
        reservedWords.add("HAVING");
        reservedWords.add("IDENTIFIED");
        reservedWords.add("IMMEDIATE");
        reservedWords.add("IN");
        reservedWords.add("INCREMENT");
        reservedWords.add("INDEX");
        reservedWords.add("INITIAL");
        reservedWords.add("INSERT");
        reservedWords.add("INTEGER");
        reservedWords.add("INTERSECT");
        reservedWords.add("INTO");
        reservedWords.add("IS");
        reservedWords.add("LEVEL");
        reservedWords.add("LIKE");
        reservedWords.add("LOCK");
        reservedWords.add("LONG");
        reservedWords.add("MAXEXTENTS");
        reservedWords.add("MINUS");
        reservedWords.add("MLSLABEL");
        reservedWords.add("MODE");
        reservedWords.add("MODIFY");
        reservedWords.add("NOAUDIT");
        reservedWords.add("NOCOMPRESS");
        reservedWords.add("NOT");
        reservedWords.add("NOWAIT");
        reservedWords.add("NULL");
        reservedWords.add("NUMBER");
        reservedWords.add("OF");
        reservedWords.add("OFFLINE");
        reservedWords.add("ON");
        reservedWords.add("ONLINE");
        reservedWords.add("OPTION");
        reservedWords.add("OR");
        reservedWords.add("ORDER");
        reservedWords.add("PCTFREE");
        reservedWords.add("PRIOR");
        reservedWords.add("PRIVILEGES");
        reservedWords.add("PUBLIC");
        reservedWords.add("RAW");
        reservedWords.add("RENAME");
        reservedWords.add("RESOURCE");
        reservedWords.add("REVOKE");
        reservedWords.add("ROW");
        reservedWords.add("ROWID");
        reservedWords.add("ROWNUM");
        reservedWords.add("ROWS");
        reservedWords.add("SELECT");
        reservedWords.add("SESSION");
        reservedWords.add("SET");
        reservedWords.add("SHARE");
        reservedWords.add("SIZE");
        reservedWords.add("SMALLINT");
        reservedWords.add("START");
        reservedWords.add("SUCCESSFUL");
        reservedWords.add("SYNONYM");
        reservedWords.add("SYSDATE");
        reservedWords.add("TABLE");
        reservedWords.add("THEN");
        reservedWords.add("TO");
        reservedWords.add("TRIGGER");
        reservedWords.add("UID");
        reservedWords.add("UNION");
        reservedWords.add("UNIQUE");
        reservedWords.add("UPDATE");
        reservedWords.add("USER");
        reservedWords.add("VALIDATE");
        reservedWords.add("VALUES");
        reservedWords.add("VARCHAR");
        reservedWords.add("VARCHAR2");
        reservedWords.add("VIEW");
        reservedWords.add("WHENEVER");
        reservedWords.add("WHERE");
        reservedWords.add("WITH");
    }
}
