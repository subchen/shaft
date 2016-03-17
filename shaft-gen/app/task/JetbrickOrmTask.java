package jetbrick.schema.app.task;

import java.util.Map;
import jetbrick.schema.app.Task;
import jetbrick.schema.app.model.TableInfo;

public class JetbrickOrmTask extends Task {

    public JetbrickOrmTask() {
        name = "orm";
    }

    @Override
    public void execute() throws Throwable {
        // output pojo file for each table.
        for (TableInfo table : schema.getTables()) {
            Map<String, Object> context = getTemplateContext();
            context.put("table", table);

            writeFile("pojo.java.jetx", getPackagePath() + "/data/" + table.getTableClass() + ".java", context);
        }
    }

}
