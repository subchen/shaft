package shaft.sync;

import shaft.sync.jdbc.JdbcHelper;

public abstract class UpgradeTask {
    protected String version;
    protected JdbcHelper jdbc;

    public void init(App app) {
        jdbc = new JdbcHelper(app.getDbHelper());
        version = app.getProperty(Features.APP_VERSION);
    }

    public abstract void execute();
}
