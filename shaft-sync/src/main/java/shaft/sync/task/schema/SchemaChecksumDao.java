package shaft.sync.task.schema;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import shaft.dao.DbHelper;
import shaft.sync.task.schema.model.SchemaChecksum;

public final class SchemaChecksumDao {
    private final DbHelper dao;

    public SchemaChecksumDao(DbHelper dao) {
        this.dao = dao;
    }

    public void ensureCreate() throws SQLException {
        if (!dao.getMetadata().tableExist("_schema_checksum_")) {
            String sql = "create table _schema_checksum_ ("
                    + "  name varchar(50) not null,"
                    + "  checksum char(32) not null,"
                    + "  timestamp datetime not null,"
                    + "  version varchar(20),"
                    + "  primary key (name)"
                    + ")";
            dao.executeUpdate(sql);
        }
    }

    public List<SchemaChecksum> list() {
        String sql = "select * from _schema_checksum_";
        return dao.executeQuery(sql, (rs) -> {
            List<SchemaChecksum> results = new ArrayList<>(64);
            while (rs.next()) {
                results.add(mapping(rs));
            }
            return results;
        });
    }

    public void save(SchemaChecksum info) {
        String sql = "insert into _schema_checksum_ (name, checksum, timestamp, version) values (?,?,?,?)";
        dao.executeUpdate(sql, (ps) -> {
            ps.setString(1, info.getName());
            ps.setString(2, info.getChecksum());
            ps.setTimestamp(3, info.getTimestamp());
            ps.setString(4, info.getVersion());
        });
    }

    public void update(SchemaChecksum info) {
        String sql = "update _schema_checksum_ set checksum=?, timestamp=?, version=?) where name=?";
        dao.executeUpdate(sql, (ps) -> {
            ps.setString(1, info.getChecksum());
            ps.setTimestamp(2, info.getTimestamp());
            ps.setString(3, info.getVersion());
            ps.setString(4, info.getName());
        });
    }

    public void delete(SchemaChecksum info) {
        String sql = "delete from _schema_checksum_ where name=?";
        dao.executeUpdate(sql, (ps) -> {
            ps.setString(1, info.getName());
        });
    }

    private SchemaChecksum mapping(ResultSet rs) throws SQLException {
        SchemaChecksum info = new SchemaChecksum();
        info.setName(rs.getString("name"));
        info.setChecksum(rs.getString("checksum"));
        info.setTimestamp(rs.getTimestamp("timestamp"));
        info.setVersion(rs.getString("version"));
        return info;
    }

}
