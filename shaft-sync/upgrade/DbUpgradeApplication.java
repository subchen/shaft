package jetbrick.dao.schema.upgrade;

import jetbrick.dao.schema.upgrade.task.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbUpgradeApplication {
    private final Logger log = LoggerFactory.getLogger(DbUpgradeApplication.class);

    public void execute() {
        execute(null);
    }

    public void execute(SchemaHook schemaHook) {
        log.info("DbUpgradeApplication starting...");
        UpgradeLogger fileLog = new UpgradeLogger();
        try {
            doExecuteTask(new SchemaTableUpgradeTask(fileLog, schemaHook));
            doExecuteTask(new SchemaEnumUpgradeTask(fileLog));
            doExecuteTask(new SchemaBulkUpgradeTask(fileLog));
        } finally {
            fileLog.close();
        }
        log.info("DbUpgradeApplication completed.");
    }

    private void doExecuteTask(UpgradeTask task) {
        task.initialize();
        if (task.isRequired()) {
            task.execute();
        }
        task.destory();
    }
}
