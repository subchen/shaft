package jetbrick.dao.schema.data.hibernate;

import jetbrick.dao.orm.Transaction;

/**
 * Hibernate 子事务
 */
public class HibernateNestedTransaction implements Transaction {
    // 由于 Hibernate 不支持子事务，所以返回一个空操作
    public static final Transaction NOOP = new HibernateNestedTransaction();

    private HibernateNestedTransaction() {
    }

    @Override
    public void commit() {
    }

    @Override
    public void rollback() {
    }

    @Override
    public void close() {
    }
}
