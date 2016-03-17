package jetbrick.dao.schema.data.jdbc;

import java.io.Serializable;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import jetbrick.dao.dialect.Dialect;
import jetbrick.dao.orm.Pagelist;
import jetbrick.dao.orm.jdbc.JdbcHelper;
import jetbrick.dao.orm.jdbc.RowMapper;
import jetbrick.dao.schema.data.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

public class JdbcEntityDaoHelper<T extends Entity> implements EntityDaoHelper<T> {
    private static final int LOAD_SOME_BATCH_SIZE = 100;

    protected final JdbcHelper dao;
    protected final Dialect dialect;
    protected final Class<T> entityClass;
    protected final SchemaInfo<T> schema;
    protected final RowMapper<T> rowMapper;
    protected final String tableNameIdentifier;
    protected final String sql_insert;
    protected final String sql_update;
    protected final String sql_delete;
    protected final String sql_select;

    public JdbcEntityDaoHelper(JdbcDaoHelper dao, Class<T> entityClass, SchemaInfo<T> schema, RowMapper<T> rowMapper) {
        this.dao = dao.getJdbcHelper();
        this.dialect = dao.getDialect();
        this.entityClass = entityClass;
        this.schema = schema;
        this.rowMapper = rowMapper;
        this.tableNameIdentifier = dialect.getIdentifier(schema.getTableName());
        this.sql_insert = EntitySqlUtils.get_sql_insert(schema, dialect);
        this.sql_update = EntitySqlUtils.get_sql_update(schema, dialect);
        this.sql_delete = EntitySqlUtils.get_sql_delete(schema, dialect);
        this.sql_select = EntitySqlUtils.get_sql_select_object(schema, dialect);

        LoggerFactory.getLogger(JdbcEntityDaoHelper.class).debug("JdbcEntityDaoHelper init completed: " + entityClass.getName());
    }

    // -------- table ---------------------------------
    @Override
    public boolean tableExist() {
        return dao.tableExist(schema.getTableName());
    }

    @Override
    public int tableCreate() {
        String sql = EntitySqlUtils.get_sql_table_create(schema, dialect);
        return dao.execute(sql);
    }

    @Override
    public int tableDelete() {
        String sql = "drop table " + tableNameIdentifier;
        return dao.execute(sql);
    }

    // -------- save/update/delete ---------------------------------
    @Override
    public int save(T entity) {
        entity.validate();
        entity.generateId();
        return dao.execute(sql_insert, entity.dao_insert_parameters());
    }

    @Override
    public int update(T entity) {
        entity.validate();
        return dao.execute(sql_update, entity.dao_update_parameters());
    }

    @Override
    public int saveOrUpdate(T entity) {
        if (entity.getId() == null) {
            return save(entity);
        } else {
            return update(entity);
        }
    }

    @Override
    public int delete(T entity) {
        return delete(entity.getId());
    }

    @Override
    public int delete(Serializable id) {
        return dao.execute(sql_delete, id);
    }

    // -------- batch save/update/delete ---------------------------------
    @Override
    public void saveAll(Collection<T> entities) {
        if (entities == null || entities.size() == 0) return;

        List<Object[]> parameters = new ArrayList<Object[]>(entities.size());
        for (T entity : entities) {
            entity.generateId();
            entity.validate();
            parameters.add(entity.dao_insert_parameters());
        }
        dao.executeBatch(sql_insert, parameters);
    }

    @Override
    public void updateAll(Collection<T> entities) {
        if (entities == null || entities.size() == 0) return;

        List<Object[]> parameters = new ArrayList<Object[]>(entities.size());
        for (T entity : entities) {
            entity.validate();
            parameters.add(entity.dao_update_parameters());
        }
        dao.executeBatch(sql_update, parameters);
    }

    @Override
    public void saveOrUpdateAll(Collection<T> entities) {
        if (entities == null || entities.size() == 0) return;

        for (T entity : entities) {
            if (entity.getId() == null) {
                save(entity);
            } else {
                update(entity);
            }
        }
    }

    @Override
    public void deleteAll(Collection<T> entities) {
        if (entities == null || entities.size() == 0) return;

        int i = 0;
        Serializable[] ids = new Serializable[entities.size()];
        for (T entity : entities) {
            ids[i++] = entity.getId();
        }

        deleteAll(ids);
    }

    @Override
    public int deleteAll(Serializable... ids) {
        if (ids == null || ids.length == 0) {
            return 0;
        }
        String values = StringUtils.repeat("?", ",", ids.length);
        String sql = "delete from " + tableNameIdentifier + " where id in (" + values + ")";
        return dao.execute(sql, (Object[]) ids);
    }

    @Override
    public int deleteAllEx(String name, Object value) {
        String sql = "delete from " + tableNameIdentifier + " where " + getColumnNameIdentifier(name) + "=?";
        return execute(sql, value);
    }

    // -------- load ---------------------------------
    @Override
    public T load(Serializable id) {
        return dao.queryAsObject(rowMapper, sql_select, id);
    }

    @Override
    public T loadEx(String name, Object value) {
        String sql = "select * from " + tableNameIdentifier + " where " + getColumnNameIdentifier(name) + "=?";
        return queryAsObject(sql, value);
    }

    // 如果数量超过 LOAD_SOME_BATCH_SIZE， 分批进行 load
    @Override
    public List<T> loadSome(Serializable... ids) {
        if (ids == null || ids.length == 0) {
            return Collections.<T> emptyList();
        }
        if (ids.length <= LOAD_SOME_BATCH_SIZE) {
            return loadSome(ids, 0, LOAD_SOME_BATCH_SIZE);
        }

        List<T> items = new ArrayList<T>(ids.length);
        int offset = 0;
        while (offset < ids.length) {
            List<T> some = loadSome(ids, offset, LOAD_SOME_BATCH_SIZE);
            items.addAll(some);
            offset += LOAD_SOME_BATCH_SIZE;
        }
        return items;
    }

    // load 固定大小的 内容 (从 offset开始最大载入limit数量)
    private List<T> loadSome(Serializable[] ids, int offset, int limit) {
        Serializable[] some_ids = ids;
        if (offset > 0 || limit < ids.length) {
            int length = Math.min(limit, ids.length - offset);
            some_ids = new Serializable[length];
            System.arraycopy(ids, offset, some_ids, 0, some_ids.length);
        }

        String values = StringUtils.repeat("?", ",", some_ids.length);
        String sql = "select * from " + tableNameIdentifier + " where id in (" + values + ")";
        return dao.queryAsList(rowMapper, sql, (Object[]) some_ids);
    }

    @Override
    public List<T> loadSomeEx(String name, Object value, String... sorts) {
        //@formatter:off
		String sql = "select * from " + tableNameIdentifier 
				   + " where " + getColumnNameIdentifier(name) + "=?"
				   + get_sql_sort_part(sorts);
		//@formatter:on
        return queryAsList(sql, value);
    }

    @Override
    public List<T> loadAll(String... sorts) {
        String sql = "select * from " + tableNameIdentifier + get_sql_sort_part(sorts);
        return queryAsList(sql);
    }

    // -------- query ---------------------------------
    @Override
    public T queryAsObject(String sql, Object... parameters) {
        return dao.queryAsObject(rowMapper, sql, parameters);
    }

    @Override
    public List<T> queryAsList(String sql, Object... parameters) {
        return dao.queryAsList(rowMapper, sql, parameters);
    }

    @Override
    public Pagelist<T> queryAsPagelist(Pagelist<T> pagelist, String sql, Object... parameters) {
        return dao.queryAsPagelist(pagelist, rowMapper, sql, parameters);
    }

    @Override
    public Pagelist<T> queryAsPagelist(HttpServletRequest request, String sql, Object... parameters) {
        return queryAsPagelist(new Pagelist<T>(request), sql, parameters);
    }

    // ----- execute ---------------------------------------
    @Override
    public int execute(String sql, Object... parameters) {
        return dao.execute(sql, parameters);
    }

    // ----- sql gen ---------------------------------------------
    private String get_sql_sort_part(String... sorts) {
        if (sorts == null || sorts.length == 0) {
            return "";
        }
        for (int i = 0; i < sorts.length; i++) {
            String part[] = StringUtils.split(sorts[i], " ");
            part[0] = getColumnNameIdentifier(part[0]);
            sorts[i] = StringUtils.join(part, " ");
        }
        return " order by " + StringUtils.join(sorts, ",");
    }

    private String getColumnNameIdentifier(String name) {
        if (name.indexOf("_") == -1) {
            // maybe fieldName
            SchemaColumn sc = schema.getColumn(name);
            if (sc != null) {
                name = sc.getColumnName();
            }
        }
        return dialect.getIdentifier(name);
    }
}
