package shaft.sync;

import junit.framework.TestCase;
import org.junit.Test;

import shaft.dao.DbHelper;
import shaft.dao.ds.DriverManagerDataSource;

public class ShaftSyncAppTest extends TestCase {

    @Test
    public void testExecute() throws Exception {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setUrl(System.getProperty("ds.url"));
        ds.setUsername(System.getProperty("ds.username"));
        ds.setPassword(System.getProperty("ds.password"));
        ds.setDriverClassName(System.getProperty("ds.driverClassName"));

        ShaftSyncApp app = new ShaftSyncApp(new DbHelper(ds));
        app.execute();
    }
}
