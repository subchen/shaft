package jetbrick.dao.schema.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import jetbrick.dao.orm.Pagelist;

/**
 * 单表操作
 */
public interface EntityDaoHelper<T extends Entity> {

    // -------- table ---------------------------------
    public boolean tableExist();

    public int tableCreate();

    public int tableDelete();

    // -------- save/update/delete ---------------------------------
    public int save(T entity);

    public int update(T entity);

    public int saveOrUpdate(T entity);

    public int delete(T entity);

    public int delete(Serializable id);

    // -------- batch save/update/delete ---------------------------------
    public void saveAll(Collection<T> entities);

    public void updateAll(Collection<T> entities);

    public void saveOrUpdateAll(Collection<T> entities);

    public void deleteAll(Collection<T> entities);

    public int deleteAll(Serializable... ids);

    public int deleteAllEx(String name, Object value);

    // -------- load ---------------------------------
    public T load(Serializable id);

    public T loadEx(String name, Object value);

    public List<T> loadSome(Serializable... ids);

    public List<T> loadSomeEx(String name, Object value, String... sorts);

    public List<T> loadAll(String... sorts);

    // -------- query ---------------------------------
    public T queryAsObject(String sql, Object... parameters);

    public List<T> queryAsList(String sql, Object... parameters);

    public Pagelist<T> queryAsPagelist(Pagelist<T> pagelist, String sql, Object... parameters);

    public Pagelist<T> queryAsPagelist(HttpServletRequest request, String sql, Object... parameters);

    // ----- execute ---------------------------------------
    public int execute(String sql, Object... parameters);

}
