package jetbrick.dao.schema.data.hibernate;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;
import jetbrick.commons.bean.ClassConvertUtils;
import jetbrick.commons.exception.DbError;
import jetbrick.commons.exception.SystemException;
import jetbrick.commons.lang.ObjectHolder;
import jetbrick.dao.dialect.Dialect;
import jetbrick.dao.orm.*;
import jetbrick.dao.orm.Transaction;
import jetbrick.dao.schema.data.SimpleDaoHelper;
import jetbrick.dao.schema.data.SimpleDaoHelperCallback;
import org.apache.commons.beanutils.*;
import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.commons.collections.iterators.SingletonIterator;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import org.hibernate.*;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.classic.QueryTranslatorImpl;
import org.hibernate.jdbc.Work;
import org.slf4j.LoggerFactory;

/**
 * 数据库操作。单例使用
 */
@SuppressWarnings("unchecked")
public class HibernateDaoHelper implements SimpleDaoHelper {
    private static final int LOAD_SOME_BATCH_SIZE = 100;
    private static final boolean ALLOW_NESTED_TRANSACTION = true;

    // 当前线程(事务)
    private final ThreadLocal<HibernateTransaction> transactionHandler = new ThreadLocal<HibernateTransaction>();
    private final LazyInitializer<SessionFactory> sessionFactory;
    private final Dialect dialect;

    public HibernateDaoHelper(LazyInitializer<SessionFactory> sessionFactory, Dialect dialect) {
        this.sessionFactory = sessionFactory;
        this.dialect = dialect;

        if (sessionFactory != null) {
            // not in nested transaction
            LoggerFactory.getLogger(HibernateDaoHelper.class).debug("HibernateDaoHelper init completed.");
        }
    }

    /**
     * 启动一个事务(默认支持子事务)
     */
    @Override
    public Transaction transaction() {
        if (transactionHandler.get() != null) {
            if (ALLOW_NESTED_TRANSACTION) {
                return HibernateNestedTransaction.NOOP;
            }
            throw new SystemException("Can't begin a nested transaction.", DbError.TRANSACTION_ERROR);
        }
        try {
            Session session = sessionFactory.get().openSession();
            HibernateTransaction tx = new HibernateTransaction(session, transactionHandler);
            transactionHandler.set(tx);
            return tx;
        } catch (Throwable e) {
            throw SystemException.unchecked(e, DbError.TRANSACTION_ERROR);
        }
    }

    /**
     * 获取一个当前线程的连接(事务中)，如果没有，则新建一个。
     */
    protected Session getSession() {
        HibernateTransaction tx = transactionHandler.get();
        try {
            if (tx == null) {
                return sessionFactory.get().openSession();
            } else {
                return tx.getSession();
            }
        } catch (Throwable e) {
            throw SystemException.unchecked(e);
        }
    }

    /**
     * 释放一个连接，如果不在 session 不在事务中，则关闭它，否则不处理。
     */
    protected void closeSession(Session session) {
        if (transactionHandler.get() == null) {
            // not in transaction
            if (!session.isOpen()) {
                throw new SystemException("the session is closed.", DbError.TRANSACTION_ERROR);
            }
            session.close();
        }
    }

    public void flush() {
        HibernateTransaction tx = transactionHandler.get();
        if (tx != null) {
            tx.getSession().flush();
        }
    }

    // ----- dialect ---------------------------------------
    @Override
    public Dialect getDialect() {
        return dialect;
    }

    // ----- table ---------------------------------------
    @Override
    public boolean tableExist(final String tableName) {
        final ObjectHolder<Boolean> result = new ObjectHolder<Boolean>();
        execute(new ConnectionCallback() {
            @Override
            public void execute(Connection conn) throws SQLException {
                result.put(JdbcUtils.doGetTableExist(conn, tableName));
            }
        });
        return result.get();
    }

    // ----- execute -----------------------------------------------------
    @Override
    public int execute(String hql, Object... parameters) {
        Session session = getSession();
        try {
            return createQuery(session, hql, parameters).executeUpdate();
        } finally {
            closeSession(session);
        }
    }

    // 作为一个事务运行
    @Override
    public void execute(final ConnectionCallback callback) {
        Transaction tx = transaction();
        try {
            getSession().doWork(new Work() {
                @Override
                public void execute(Connection conn) throws SQLException {
                    callback.execute(conn);
                }
            });
            tx.commit();
        } catch (Throwable e) {
            tx.rollback();
            throw SystemException.unchecked(e);
        } finally {
            tx.close();
        }
    }

    // 作为一个事务运行
    @Override
    public void execute(final SimpleDaoHelperCallback<SimpleDaoHelper> callback) {
        Transaction tx = transaction();
        try {
            final Session session = getSession();
            HibernateDaoHelper dao = new HibernateDaoHelper(null, dialect) {
                @Override
                public HibernateTransaction transaction() {
                    throw new SystemException("the session is in nested transaction.", DbError.TRANSACTION_ERROR);
                }

                @Override
                protected Session getSession() {
                    return session;
                }

                @Override
                protected void closeSession(Session session) {
                }

                @Override
                public void flush() {
                    session.flush();
                }
            };
            callback.execute(dao);
            tx.commit();
        } catch (Throwable e) {
            tx.rollback();
            throw SystemException.unchecked(e);
        } finally {
            tx.close();
        }
    }

    // ----- save/update/delete -----------------------------------------------------
    public Serializable save(Object entity) {
        Session session = getSession();
        try {
            return session.save(entity);
        } finally {
            closeSession(session);
        }
    }

    public void update(Object entity) {
        Session session = getSession();
        try {
            session.update(entity);
        } finally {
            closeSession(session);
        }
    }

    public void saveOrUpdate(Object entity) {
        Session session = getSession();
        try {
            session.saveOrUpdate(entity);
        } finally {
            closeSession(session);
        }
    }

    public void delete(Object entity) {
        Session session = getSession();
        try {
            session.delete(entity);
        } finally {
            closeSession(session);
        }
    }

    public void delete(Class<?> clazz, Serializable id) {
        Session session = getSession();
        try {
            Object entity = session.get(clazz, id);
            if (entity != null) {
                session.delete(entity);
            }
        } finally {
            closeSession(session);
        }
    }

    // ----- batch save/update/delete -----------------------------------------------------
    public void saveAll(Collection<?> entities) {
        Session session = getSession();
        try {
            for (Object entity : entities) {
                session.save(entity);
            }
            session.flush();
        } finally {
            closeSession(session);
        }
    }

    public void updateAll(Collection<?> entities) {
        Session session = getSession();
        try {
            for (Object entity : entities) {
                session.update(entity);
            }
            session.flush();
        } finally {
            closeSession(session);
        }
    }

    public void saveOrUpdateAll(Collection<?> entities) {
        Session session = getSession();
        try {
            for (Object entity : entities) {
                session.saveOrUpdate(entity);
            }
            session.flush();
        } finally {
            closeSession(session);
        }
    }

    public void deleteAll(Collection<?> entities) {
        Session session = getSession();
        try {
            for (Object entity : entities) {
                session.delete(entity);
            }
            session.flush();
        } finally {
            closeSession(session);
        }
    }

    public int deleteAll(Class<?> clazz, String name, Object value) {
        String hql = "delete from " + clazz.getName() + " where " + name + "=?";
        if (value == null) {
            hql = "delete from " + clazz.getName() + " where " + name + " is null";
        } else if (value instanceof Object[]) {
            hql = "delete from " + clazz.getName() + " where " + name + " in (:list0)";
        } else if (value instanceof Collection) {
            hql = "delete from " + clazz.getName() + " where " + name + " in (:list0)";
        }
        Iterator<?> parameters = (value == null) ? null : new SingletonIterator(value);

        Session session = getSession();
        try {
            return createQueryByIterator(session, hql, parameters).executeUpdate();
        } finally {
            closeSession(session);
        }
    }

    // ----- query -----------------------------------------------------

    public <T> T load(Class<T> clazz, Serializable id) {
        Session session = getSession();
        try {
            return (T) session.get(clazz, id);
        } finally {
            closeSession(session);
        }
    }

    public <T> T load(Class<T> clazz, String name, Object value) {
        String hql = "from " + clazz.getName() + " where " + name + "=?";
        if (value == null) {
            hql = "from " + clazz.getName() + " where " + name + " is null";
        } else if (value instanceof Object[]) {
            hql = "from " + clazz.getName() + " where " + name + " in (:list0)";
        } else if (value instanceof Collection) {
            hql = "from " + clazz.getName() + " where " + name + " in (:list0)";
        }
        Iterator<?> parameters = (value == null) ? null : new SingletonIterator(value);

        Session session = getSession();
        try {
            return (T) createQueryByIterator(session, hql, parameters).setMaxResults(1).uniqueResult();
        } finally {
            closeSession(session);
        }
    }

    // 如果数量超过 LOAD_SOME_BATCH_SIZE， 分批进行 load
    public <T> List<T> loadSome(Class<T> clazz, String name, Serializable... ids) {
        if (ids == null || ids.length == 0) {
            return Collections.<T> emptyList();
        }
        if (ids.length <= LOAD_SOME_BATCH_SIZE) {
            return loadSome(clazz, name, ids, 0, LOAD_SOME_BATCH_SIZE);
        }

        Session session = getSession();
        try {
            List<T> items = new ArrayList<T>(ids.length);
            int offset = 0;
            while (offset < ids.length) {
                List<T> some = loadSome(session, clazz, name, ids, offset, LOAD_SOME_BATCH_SIZE);
                items.addAll(some);
                offset += LOAD_SOME_BATCH_SIZE;
            }
            return items;
        } finally {
            closeSession(session);
        }
    }

    // load 固定大小的 内容 (从 offset开始最大载入limit数量)
    private <T> List<T> loadSome(Session session, Class<T> clazz, String name, Serializable[] ids, int offset, int limit) {
        Serializable[] some_ids = ids;
        if (offset > 0 || limit < ids.length) {
            int length = Math.min(limit, ids.length - offset);
            some_ids = new Serializable[length];
            System.arraycopy(ids, offset, some_ids, 0, some_ids.length);
        }

        String values = StringUtils.repeat("?", ",", some_ids.length);
        String hql = "from " + clazz.getName() + " where " + name + " in (" + values + ")";
        return (List<T>) createQuery(session, hql, (Object[]) some_ids).list();
    }

    public <T> List<T> loadAll(Class<T> clazz, String... sorts) {
        String hql = "from " + clazz.getName() + get_hql_sort_part(sorts);
        return (List<T>) queryAsList(hql);
    }

    public Object queryAsObject(String hql, Object... parameters) {
        Session session = getSession();
        try {
            return createQuery(session, hql, parameters).setMaxResults(1).uniqueResult();
        } finally {
            closeSession(session);
        }
    }

    @Override
    public Integer queryAsInt(String hql, Object... parameters) {
        return queryAsObjectCast(Integer.class, hql, parameters);
    }

    @Override
    public Long queryAsLong(String hql, Object... parameters) {
        return queryAsObjectCast(Long.class, hql, parameters);
    }

    @Override
    public String queryAsString(String hql, Object... parameters) {
        return queryAsObjectCast(String.class, hql, parameters);
    }

    @Override
    public Boolean queryAsBoolean(String hql, Object... parameters) {
        return queryAsObjectCast(Boolean.class, hql, parameters);
    }

    @Override
    public Date queryAsDate(String hql, Object... parameters) {
        return queryAsObjectCast(Date.class, hql, parameters);
    }

    protected <T> T queryAsObjectCast(Class<T> clazz, String hql, Object... parameters) {
        Session session = getSession();
        try {
            Object result = createQuery(session, hql, parameters).setMaxResults(1).uniqueResult();
            return (result == null) ? null : (T) ConvertUtils.convert(result, clazz);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public <T> T[] queryAsArray(Class<T> arrayComponentClass, String hql, Object... parameters) {
        List<Object[]> list = (List<Object[]>) queryAsList(hql, parameters);
        int size = list == null ? 0 : list.size();
        T[] array = (T[]) Array.newInstance(arrayComponentClass, size);
        for (int i = 0; i < size; i++) {
            array[i] = ClassConvertUtils.convert(list.get(i)[0], arrayComponentClass);
        }
        return array;
    }

    public <T> List<T> queryAsList(Class<T> clazz, String name, Object value, String... sorts) {
        String hql = "from " + clazz.getName() + " where " + name + "=?";
        if (value == null) {
            hql = "from " + clazz.getName() + " where " + name + " is null";
        } else if (value instanceof Object[]) {
            hql = "from " + clazz.getName() + " where " + name + " in (:list0)";
        } else if (value instanceof Collection) {
            hql = "from " + clazz.getName() + " where " + name + " in (:list0)";
        }
        hql = hql + get_hql_sort_part(sorts);
        Iterator<?> parameters = (value == null) ? null : new SingletonIterator(value);

        Session session = getSession();
        try {
            return (List<T>) createQueryByIterator(session, hql, parameters).list();
        } finally {
            closeSession(session);
        }
    }

    public List<?> queryAsList(String hql, Object... parameters) {
        Session session = getSession();
        try {
            return createQuery(session, hql, parameters).list();
        } finally {
            closeSession(session);
        }
    }

    public List<?> queryAsList(int max, String hql, Object... parameters) {
        Session session = getSession();
        try {
            Query query = createQuery(session, hql, parameters);
            query.setFirstResult(0);
            query.setMaxResults(max);
            return query.list();
        } finally {
            closeSession(session);
        }
    }

    public <T> Pagelist<T> queryAsPagelist(Pagelist<T> pagelist, Class<T> clazz, String... sorts) {
        String hql = "from " + clazz.getName() + get_hql_sort_part(sorts);
        return queryAsPagelist(pagelist, hql);
    }

    public <T> Pagelist<T> queryAsPagelist(Pagelist<T> pagelist, String hql, Object... parameters) {
        Session session = getSession();
        try {
            if (pagelist.getCount() < 0) {
                String hql_count = SqlUtils.get_sql_select_count(hql);
                Query query = createQuery(session, hql_count, parameters);
                int count = ((Number) query.uniqueResult()).intValue();
                pagelist.setCount(count);
            }

            List<T> items = Collections.EMPTY_LIST;
            if (pagelist.getCount() > 0) {
                Query query = createQuery(session, hql, parameters);
                query.setFirstResult(pagelist.getFirstResult());
                query.setMaxResults(pagelist.getPageSize());
                items = query.list();
            }
            pagelist.setItems(items);
            return pagelist;
        } finally {
            closeSession(session);
        }
    }

    private Query createQuery(Session session, String hql, Object... parameters) {
        if (parameters == null) {
            return createQueryByIterator(session, hql, null);
        }

        if (parameters.length == 1) {
            Object value = parameters[0];
            Class<? extends Object> clazz = value.getClass();
            if (ClassUtils.isAssignable(clazz, Map.class)) {
                return createQueryByMap(session, hql, (Map<String, Object>) value);
            } else if (ClassUtils.isAssignable(clazz, Collection.class)) {
                return createQueryByIterator(session, hql, new ArrayIterator(parameters));
            } else if (clazz.isPrimitive() || clazz.getName().startsWith("java.")) {
                return createQueryByIterator(session, hql, new ArrayIterator(parameters));
            } else {
                return createQueryByMap(session, hql, new BeanMap(value));
            }
        } else {
            return createQueryByIterator(session, hql, new ArrayIterator(parameters));
        }
    }

    private Query createQueryByMap(Session session, String hql, Map<String, Object> parameters) {
        Query query = createQueryByHql(session, hql);
        if (parameters != null) {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                String key = entry.getKey();
                String regex = "\\:" + key + "(\\s|\\)|$)";
                Pattern p = Pattern.compile(regex);
                if (p.matcher(hql).find()) {
                    Object value = entry.getValue();
                    if (value == null) {
                        query.setParameter(key, null);
                    } else if (value instanceof Object[]) {
                        query.setParameterList(key, (Object[]) value);
                    } else if (value instanceof Collection) {
                        query.setParameterList(key, (Collection<?>) value);
                    } else {
                        query.setParameter(key, value);
                    }
                }
            }
        }
        return query;
    }

    private Query createQueryByIterator(Session session, String hql, Iterator<?> parameters) {
        Query query = createQueryByHql(session, hql);
        if (parameters != null) {

            Iterator<?> iterator = (Iterator<?>) parameters;
            int index = 0;
            while (iterator.hasNext()) {
                Object parameter = iterator.next();
                if (parameter != null) {
                    if (parameter instanceof Object[]) {
                        query.setParameterList("list" + index, (Object[]) parameter);
                    } else if (parameter instanceof Collection) {
                        query.setParameterList("list" + index, (Collection<?>) parameter);
                    } else {
                        query.setParameter(index, parameter);
                    }
                } else {
                    query.setParameter(index, null);
                }
                index++;
            }
        }
        return query;
    }

    private Query createQueryByHql(Session session, String hql) {
        if (hql.startsWith("sql:")) {
            return session.createSQLQuery(hql.substring(4));
        } else {
            return session.createQuery(hql);
        }
    }

    private String get_hql_sort_part(String... sorts) {
        if (sorts == null || sorts.length == 0) {
            return "";
        }
        return " order by " + StringUtils.join(sorts, ",");
    }

    public String translateHQL(String hql) {
        QueryTranslatorImpl queryTranslator = new QueryTranslatorImpl(hql, hql, Collections.EMPTY_MAP, (SessionFactoryImplementor) sessionFactory);
        queryTranslator.compile(Collections.EMPTY_MAP, false);
        return queryTranslator.getSQLString();
    }

    public void unlazy(Object entity) {
        Hibernate.initialize(entity);
    }

    public void unlazy(Collection<?> entities, String attrNames) {
        for (Object entity : entities) {
            unlazy(entity, attrNames);
        }
    }

    public void unlazy(Object entity, String attrNames) {
        for (String name : attrNames.split(",")) {
            try {
                Object value = PropertyUtils.getProperty(entity, StringUtils.trim(name));

                if (value == null) {
                    return;
                } else {
                    Hibernate.initialize(value);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public int nextval(String name) {
        String hql = "sql: select " + name + ".nextval from dual";
        return queryAsInt(hql).intValue();
    }

}
