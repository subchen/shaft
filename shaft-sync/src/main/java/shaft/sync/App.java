package shaft.sync;

import shaft.sync.jdbc.DriverManagerDataSource;
import shaft.sync.task.schema.SchemaUpgradeTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public final class App {
    private final Logger log = LoggerFactory.getLogger(App.class);
    private final List<UpgradeTask> tasks;
    private final AuditLogger auditLog;
    private DataSource dataSource;
    private String version;

    public App() {
        tasks = new ArrayList<>(4);
        auditLog = new AuditLogger();
    }


    public void addTask(UpgradeTask task) {
        this.tasks.add(task);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public AuditLogger getAuditLogger() {
        return auditLog;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void execute() {
        log.info("DBUPS App starting ...");
        try (auditLog) {
            for (UpgradeTask task : tasks) {
                task.init(this);
                task.execute();
            }
        }
        log.info("DBUPS App completed.");
    }

    public static void main(String args[]) {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setUrl(System.getProperty("ds.url"));
        ds.setUsername(System.getProperty("ds.username"));
        ds.setPassword(System.getProperty("ds.password"));
        ds.setDriverClassName(System.getProperty("ds.driverClassName"));

        SchemaUpgradeTask schemaUpgradeTask = new SchemaUpgradeTask();
        //schemaUpgradeTask.addSchemaHook(new AuditLogSchemaHook());

        App app = new App();
        app.setVersion("1.0.0");
        app.setDataSource(ds);
        app.addTask(schemaUpgradeTask);
        //app.addTask(new BulkUpgradeTask());
        app.execute();
    }

}
