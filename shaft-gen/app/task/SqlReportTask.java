package jetbrick.schema.app.task;

import java.util.Map;
import jetbrick.dao.dialect.Dialect;
import jetbrick.schema.app.Task;

public class SqlReportTask extends Task {

    public SqlReportTask() {
        name = "report";
    }

    @Override
    public void execute() throws Throwable {
        for (Dialect dialect : getDialects()) {
            schema.setDialect(dialect);

            Map<String, Object> context = getTemplateContext();
            writeFile("schema.html.jetx", dialect.getName() + "_schema.html", context);
        }
    }

}
