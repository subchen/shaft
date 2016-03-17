package jetbrick.dao.schema.data.hibernate;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import jetbrick.commons.xml.XmlNode;
import jetbrick.dao.dialect.Dialect;
import jetbrick.dao.orm.DataSourceUtils;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;

public class HibernateEntity {

    public static final HibernateDaoHelper DAO = new HibernateDaoHelper(getLazySessionFactory(), DataSourceUtils.getDialect());

    private static LazyInitializer<SessionFactory> getLazySessionFactory() {
        return new LazyInitializer<SessionFactory>() {
            @Override
            protected SessionFactory initialize() throws ConcurrentException {

                DataSource dataSource = DataSourceUtils.getDataSource();
                Dialect dialect = DataSourceUtils.getDialect();

                LocalSessionFactoryBuilder builder = new LocalSessionFactoryBuilder(dataSource);
                builder.addResource("jetbrick/dao/schema/upgrade/model/SchemaChecksum.hbm.xml");
                builder.addResource("jetbrick/dao/schema/upgrade/model/SchemaEnum.hbm.xml");
                for (String file : getHbmXmlFileList(dialect)) {
                    builder.addResource(file);
                }

                builder.setProperty(Environment.DIALECT, dialect.getHibernateDialect());
                builder.setProperty(Environment.SHOW_SQL, "true");
                builder.setProperty(Environment.STATEMENT_BATCH_SIZE, "100");

                return builder.buildSessionFactory();
            }
        };
    }

    private static List<String> getHbmXmlFileList(Dialect dialect) {
        List<String> filelist = new ArrayList<String>();

        String file = "/META-INF/schema-hbm-" + dialect.getName() + ".xml";
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
        XmlNode root = XmlNode.create(is);
        for (XmlNode node : root.elements()) {
            filelist.add(node.attribute("file").asString());
        }
        return filelist;
    }
}
