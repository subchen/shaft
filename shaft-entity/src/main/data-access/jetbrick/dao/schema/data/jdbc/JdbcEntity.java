package jetbrick.dao.schema.data.jdbc;

import jetbrick.dao.orm.DataSourceUtils;
import jetbrick.dao.orm.jdbc.JdbcHelper;

public class JdbcEntity {

    public static final JdbcDaoHelper DAO = new JdbcDaoHelper(new JdbcHelper(DataSourceUtils.getDataSource()));

}
