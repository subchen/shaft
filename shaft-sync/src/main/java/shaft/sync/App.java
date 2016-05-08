package shaft.sync;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import shaft.dao.ds.DriverManagerDataSource;
import shaft.sync.task.schema.SchemaUpgradeTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import shaft.dao.DbHelper;
import shaft.dao.ds.DriverManagerDataSource;
import shaft.sync.task.bulk.BulkUpgradeTask;
import shaft.sync.task.schema.SchemaUpgradeTask;

public final class App {
    private final Logger log = LoggerFactory.getLogger(App.class);
    private final Properties props = new Properties();
    private final List<UpgradeTask> tasks;
    private DbHelper dao;

    public App() {
        // default config
        props.setProperty(Features.COLUMN_UPDATE, "false");
        props.setProperty(Features.COLUMN_DELETE, "true");
        props.setProperty(Features.BULK_DELETE, "false");

        tasks = Arrays.asList(
            new SchemaUpgradeTask(),
            new BulkUpgradeTask()
        );
    }

    public DbHelper getDbHelper() {
        return dao;
    }

    public void setDbHelper(DbHelper dao) {
        this.dao = dao;
    }

    public void setProperty(String name, String value) {
        props.setProperty(name, value);
    }

    public String getProperty(String name) {
        return props.getProperty(name);
    }
 
    public void execute() {
        log.info("shaft-sync starting ...");
        for (UpgradeTask task : tasks) {
            task.init(this);
            task.execute();
        }
        log.info("shaft-sync completed.");
    }

    public static void main(String args[]) {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setUrl(System.getProperty("ds.url"));
        ds.setUsername(System.getProperty("ds.username"));
        ds.setPassword(System.getProperty("ds.password"));
        ds.setDriverClassName(System.getProperty("ds.driverClassName"));

        App app = new App();
        app.setProperty(Features.APP_VERSION, "1.0.0");
        app.setDbHelper(new DbHelper(ds));
        //app.addSchemaHook(new SchemaHook_1_1());
        app.execute();
    }

}
