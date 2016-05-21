package shaft.dao.dialect;

import jetbrick.util.StringUtils;

public final class H2Dialect extends Dialect {

    public static final String PRODUCT_NAME = "H2";

    @Override
    public String getProductName() {
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
        return String.format("drop table if exists %s", getIdentifier(table));
    }

    @Override
    public String getTableRenameSQL(String oldName, String newName) {
        return String.format("alter table %s rename to %s", getIdentifier(oldName), getIdentifier(newName));
    }

    @Override
    public String getColumnAddSQL(String table, String columnDefinition, String columnPosition) {
        String sql = String.format("alter table %s add column %s", getIdentifier(table), columnDefinition);
        if (columnPosition != null) {
            sql = sql + " " + columnPosition;
        }
        return sql;
    }

    @Override
    public String getColumnModifySQL(String table, String columnDefinition, String columnPosition) {
        String sql = String.format("alter table %s alter column %s", getIdentifier(table), columnDefinition);
        if (columnPosition != null) {
            sql = sql + " " + columnPosition;
        }
        return sql;
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
    public boolean supportsColumnPosition() {
        return true;
    }

    @Override
    public boolean supportsSequences() {
        return true;
    }

    @Override
    protected void initializeReservedWords() {
        reservedWords.add("CROSS");
        reservedWords.add("CURRENT_DATE");
        reservedWords.add("CURRENT_TIME");
        reservedWords.add("CURRENT_TIMESTAMP");
        reservedWords.add("DISTINCT");
        reservedWords.add("EXCEPT");
        reservedWords.add("EXISTS");
        reservedWords.add("FALSE");
        reservedWords.add("FOR");
        reservedWords.add("FROM");
        reservedWords.add("FULL");
        reservedWords.add("GROUP");
        reservedWords.add("HAVING");
        reservedWords.add("INNER");
        reservedWords.add("INTERSECT");
        reservedWords.add("IS");
        reservedWords.add("JOIN");
        reservedWords.add("LIKE");
        reservedWords.add("LIMIT");
        reservedWords.add("MINUS");
        reservedWords.add("NATURAL");
        reservedWords.add("NOT");
        reservedWords.add("NULL");
        reservedWords.add("ON");
        reservedWords.add("ORDER");
        reservedWords.add("PRIMARY");
        reservedWords.add("ROWNUM");
        reservedWords.add("SELECT");
        reservedWords.add("SYSDATE");
        reservedWords.add("SYSTIME");
        reservedWords.add("SYSTIMESTAMP");
        reservedWords.add("TODAY");
        reservedWords.add("TRUE");
        reservedWords.add("UNION");
        reservedWords.add("UNIQUE");
        reservedWords.add("WHERE");
    }
}
