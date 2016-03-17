package jetbrick.schema.app;

import java.io.File;
import java.util.*;
import jetbrick.schema.app.model.Schema;
import jetbrick.schema.app.reader.SchemaXmlFileReader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 主程序入口 main application
 */
public class SchemaGenerateApp {
    protected static final Logger log = LoggerFactory.getLogger(SchemaGenerateApp.class);
    protected final Map<String, Task> tasks = new HashMap<String, Task>();
    protected final Schema schema;

    public SchemaGenerateApp(String schemaFilename) {
        log.debug("schemaFilename = " + schemaFilename);

        lookupTasks();

        SchemaXmlFileReader reader = new SchemaXmlFileReader(new File(schemaFilename));
        schema = reader.getSchema();
    }

    private void lookupTasks() {
        String taskPackageName = Task.class.getPackage().getName() + ".task";
        Set<String> taskClasses = ClassFinder.getClasses(taskPackageName, false);
        try {
            for (String taskClass : taskClasses) {
                Task task = (Task) Class.forName(taskClass).newInstance();
                tasks.put(task.getName().toLowerCase(), task);
            }
        } catch (Throwable e) {
            throw SystemException.unchecked(e);
        }
    }

    public void taskgen() throws Throwable {
        String[] taskNames = StringUtils.split(schema.getProperty("task.name"), ",");
        for (String name : taskNames) {
            Task task = tasks.get(name.trim().toLowerCase());
            if (task == null) {
                throw new RuntimeException("unknow task: " + name);
            }

            File outputdir = new File(schema.getProperty("output.path"));

            task.setSchema(schema);
            task.setOutputdir(outputdir);
            task.clean();
            task.execute();
        }
    }

    public static void main(String[] args) throws Throwable {
        SchemaGenerateApp app;

        if (args.length == 1) {
            String configFilename = args[0];
            app = new SchemaGenerateApp(configFilename);
        } else {
            if (new File("schema/schema.xml").exists()) {
                app = new SchemaGenerateApp("schema/schema.xml");
            } else {
                app = new SchemaGenerateApp("src/test/resources/schema/schema.xml");
            }
        }

        app.taskgen();
    }

}
