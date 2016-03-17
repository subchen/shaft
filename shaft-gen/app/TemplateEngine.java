package jetbrick.schema.app;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;
import jetbrick.commons.exception.SystemException;
import jetbrick.template.*;
import jetbrick.template.resource.loader.ClasspathResourceLoader;
import org.apache.commons.lang3.StringUtils;

public class TemplateEngine {
    private static final JetEngine engine = new JetEngine(getEngineConfig());

    private static Properties getEngineConfig() {
        //@formatter:off
        String[] methods = new String[] {
            "jetbrick.schema.app.model.methods.JsonUtils",
            "jetbrick.schema.app.model.methods.ChecksumUtils",
            "jetbrick.schema.app.model.methods.TableInfoUtils",
            "jetbrick.schema.app.model.methods.TableColumnUtils",
        };
        String[] functions = new String[] {
            "jetbrick.schema.app.model.methods.JsonUtils",
        };
        //@formatter:on

        Properties config = new Properties();
        config.setProperty(JetConfig.IMPORT_PACKAGES, "jetbrick.schema.app.model");
        config.setProperty(JetConfig.IMPORT_METHODS, StringUtils.join(methods, ","));
        config.setProperty(JetConfig.IMPORT_FUNCTIONS, StringUtils.join(functions, ","));
        config.setProperty(JetConfig.IMPORT_VARIABLES, "Schema schema, TableInfo table");
        config.setProperty(JetConfig.TEMPLATE_LOADER, ClasspathResourceLoader.class.getName());
        config.setProperty(JetConfig.TEMPLATE_PATH, "/");
        return config;
    }

    public static String apply(String file, Map<String, Object> context) {
        try {
            JetTemplate template = engine.getTemplate(file);
            StringWriter writer = new StringWriter();
            template.render(context, writer);
            return writer.toString();
        } catch (Exception e) {
            throw SystemException.unchecked(e);
        }
    }
}
