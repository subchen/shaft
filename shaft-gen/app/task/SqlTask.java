package jetbrick.schema.app.task;

import jetbrick.dao.dialect.Dialect;
import jetbrick.schema.app.Task;
import jetbrick.schema.app.model.*;

public class SqlTask extends Task {

    public SqlTask() {
        name = "sql";
    }

    @Override
    public void execute() throws Throwable {
        for (Dialect dialect : getDialects()) {
            schema.setDialect(dialect);

            createTableSql(dialect);
            createIndexSql(dialect);
            createForeignKeySql(dialect);
        }
    }

    private void createTableSql(Dialect dialect) {
        StringBuilder sqls = new StringBuilder();
        for (TableInfo table : schema.getTables()) {
            sqls.append(dialect.sql_table_drop(table.getTableName()) + "\n");

            sqls.append("create table " + dialect.getIdentifier(table.getTableName()) + "(\n");
            for (TableColumn c : table.getColumns()) {
                sqls.append("    ");
                sqls.append(dialect.getIdentifier(c.getColumnName()));
                sqls.append(" ");
                sqls.append(dialect.asSqlType(c.getTypeName(), c.getTypeLength(), c.getTypeScale()));
                sqls.append(c.isNullable() ? "" : " not null");
                sqls.append(",\n");
            }
            sqls.append("    primary key (" + table.getPrimaryKey().getColumn().getColumnName() + ")\n");
            sqls.append(");\n");

            sqls.append(dialect.getSqlStatmentSeparator() + "\n");
        }
        writeFile(dialect.getName() + "/create_table.sql", sqls.toString());
    }

    private void createIndexSql(Dialect dialect) {
        StringBuilder sqls = new StringBuilder();
        for (TableInfo table : schema.getTables()) {
            for (TableIndex index : table.getIndexs()) {
                String format = "create %s index %s on %s (%s);";
                String unique = index.isUnique() ? "unique" : "";
                String sql = String.format(format, unique, index.getName(), table.getTableName(), index.getOrderby());

                sqls.append(sql);
                sqls.append("\n");
            }
        }
        writeFile(dialect.getName() + "/create_index.sql", sqls.toString());
    }

    private void createForeignKeySql(Dialect dialect) {

    }
}
