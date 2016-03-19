package shaft.sync;

import shaft.sync.jdbc.JdbcHelper;

public abstract class UpgradeTask {
    protected String version;
    protected JdbcHelper jdbc;
    protected AuditLogger auditLog;

    public void init(App app) {
        jdbc = new JdbcHelper(app.getDataSource());
        auditLog = app.getAuditLogger();
        version = app.getVersion();
    }

    public abstract void execute();
}
