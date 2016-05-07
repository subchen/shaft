package shaft.sync.jdbc;

import shaft.dao.DbHelper;
import shaft.dao.metadata.DbColumn;
import shaft.dao.metadata.DbTable;

public final class JdbcHelper {
    private final DbHelper dao;

    public JdbcHelper(DbHelper dao) {
        this.dao = dao;
    }

    public void tableCreate(DbTable table) {
    }

    public void tableDelete(String tableName) {
    }

    public void columnCreate(DbColumn column, DbColumn lastColumn) {
    }

    public void columnUpdate(DbColumn column) {
    }

    public void columnDelete(DbColumn column) {
    }
}
