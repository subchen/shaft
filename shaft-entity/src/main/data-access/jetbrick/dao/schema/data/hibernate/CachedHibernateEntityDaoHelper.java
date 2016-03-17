package jetbrick.dao.schema.data.hibernate;

import java.io.Serializable;
import java.util.*;
import jetbrick.dao.orm.Pagelist;
import jetbrick.dao.orm.SqlUtils;
import jetbrick.dao.schema.data.*;
import org.apache.commons.collections.map.ListOrderedMap;

@SuppressWarnings("unchecked")
public class CachedHibernateEntityDaoHelper<T extends Entity> extends HibernateEntityDaoHelper<T> {
    protected final EntityCache<T> cache;

    public CachedHibernateEntityDaoHelper(HibernateDaoHelper dao, Class<T> entityClass, SchemaInfo<T> schema, EntityCache<T> cache) {
        super(dao, entityClass, schema);
        this.cache = cache;
    }

    // -------- save/update/delete ------------------------------------
    @Override
    public int save(T entity) {
        cache.deleteEntityObject();
        int result = super.save(entity);
        cache.addEntity(entity);
        return result;
    }

    @Override
    public int update(T entity) {
        int result = super.update(entity);
        cache.addEntity(entity);
        return result;
    }

    @Override
    public int delete(Serializable id) {
        cache.deleteEntityObject();
        cache.deleteEntity(id);
        return dao.execute(hql_delete, id);
    }

    // -------- batch save/update/delete ---------------------------------
    @Override
    public void saveAll(Collection<T> entities) {
        if (entities == null || entities.size() == 0) return;

        cache.deleteEntityObject();
        super.saveAll(entities);
        cache.addEntities(entities);
    }

    @Override
    public void updateAll(Collection<T> entities) {
        if (entities == null || entities.size() == 0) return;

        super.updateAll(entities);
        cache.addEntities(entities);
    }

    @Override
    public void saveOrUpdateAll(Collection<T> entities) {
        if (entities == null || entities.size() == 0) return;

        cache.deleteEntityObject();
        for (T entity : entities) {
            if (entity.getId() == null) {
                save(entity);
            } else {
                update(entity);
            }
            cache.addEntity(entity);
        }
    }

    @Override
    public int deleteAll(Serializable... ids) {
        cache.deleteEntityObject();
        for (Serializable id : ids) {
            cache.deleteEntity(id);
        }
        return super.deleteAll(ids);
    }

    // -------- load/query ------------------------------------
    @Override
    public T load(Serializable id) {
        T entity = (T) cache.getEntity(id);
        if (entity == null) {
            entity = super.load(id);
            if (entity != null) {
                cache.addEntity(entity);
            }
        }
        return entity;
    }

    @Override
    public List<T> loadSome(Serializable... ids) {
        if (ids == null || ids.length == 0) {
            return Collections.emptyList();
        }

        final List<Serializable> no_cache_ids = new ArrayList<Serializable>();
        final ListOrderedMap result_map = new ListOrderedMap();
        for (int i = 0; i < ids.length; i++) {
            Serializable id = ids[i];
            T entity = cache.getEntity(id);
            result_map.put(id, entity);

            if (entity != null) {
                no_cache_ids.add(id);
            }
        }

        if (no_cache_ids.size() > 0) {
            List<T> no_cache_entities = super.loadSome((Serializable[]) no_cache_ids.toArray());
            for (T entity : no_cache_entities) {
                result_map.put(entity.getId(), entity);
                cache.addEntity(entity);
            }
        }

        return result_map.valueList();
    }

    @Override
    public T queryAsObject(String hql, Object... parameters) {
        Object key = cache.createCacheKey("object", hql, parameters);
        Serializable id = cache.getEntityObjectAsId(key);
        T entity = null;
        if (id == null) {
            entity = (T) dao.queryAsObject(hql, parameters);
            if (entity != null) {
                cache.addEntity(entity);
                cache.addEntityObjectAsId(key, entity.getId());
            }
        } else {
            entity = load(id);
        }
        return entity;
    }

    @Override
    public List<T> queryAsList(String hql, Object... parameters) {
        Object key = cache.createCacheKey("list", hql, parameters);
        Serializable[] ids = cache.getEntityObjectAsIds(key);
        if (ids == null) {
            List<T> entities = (List<T>) dao.queryAsList(hql, parameters);
            cache.addEntityObjectAsList(key, entities);
            return entities;
        } else {
            return loadSome(ids);
        }
    }

    @Override
    public Pagelist<T> queryAsPagelist(Pagelist<T> pagelist, String hql, Object... parameters) {
        if (pagelist.getCount() < 0) {
            Object key = cache.createCacheKey("pagelist-count", hql, parameters);
            Integer count = cache.getEntityObjectAsInt(key);
            if (count == null) {
                String count_sql = SqlUtils.get_sql_select_count(hql);
                count = dao.queryAsInt(count_sql, parameters);
                cache.addEntityObjectAsInt(key, count);
            }
            pagelist.setCount(count);
        }

        if (pagelist.getCount() > 0) {
            Object key = cache.createCacheKey("pagelist-items", hql, parameters);
            Serializable[] ids = cache.getEntityObjectAsIds(key);
            if (ids == null) {
                dao.queryAsPagelist(pagelist, hql, parameters);
                cache.addEntityObjectAsList(key, (List<T>) pagelist.getItems());
            } else {
                List<T> items = loadSome(ids);
                pagelist.setItems(items);
            }
        }

        return pagelist;
    }

    // ----- execute ---------------------------------------
    @Override
    public int execute(String hql, Object... parameters) {
        cache.clear();
        return dao.execute(hql, parameters);
    }
}
