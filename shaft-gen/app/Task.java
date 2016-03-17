package jetbrick.schema.app;

import java.io.File;
import java.io.IOException;
import java.util.*;
import jetbrick.commons.exception.SystemException;
import jetbrick.dao.dialect.Dialect;
import jetbrick.schema.app.model.Schema;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 抽象任务
 */
public abstract class Task {
    protected static final Logger log = LoggerFactory.getLogger(Task.class);

    protected String name;
    protected Schema schema;
    protected File outputdir;

    public String getName() {
        return name;
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public File getOutputdir() {
        return outputdir;
    }

    public void setOutputdir(File outputdir) {
        this.outputdir = outputdir;
    }

    public String getPackagePath() {
        return StringUtils.replaceChars(schema.getPackageName(), ".", "/");
    }

    protected List<Dialect> getDialects() {
        List<Dialect> results = new ArrayList<Dialect>();
        String[] dialects = StringUtils.split(schema.getProperty("task.dialect"), ",");
        for (String name : dialects) {
            name = name.trim();
            Dialect dialect = Dialect.getDialect(name);
            if (dialect == null) {
                throw new SystemException("Dialect is not support: %s", name);
            }
            results.add(dialect);
        }
        return results;
    }

    protected Map<String, Object> getTemplateContext() {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("schema", schema);
        return context;
    }

    protected void writeFile(String filename, String contents) {
        File output = new File(outputdir, name + "/" + filename);
        output.getParentFile().mkdirs();
        log.debug(name + " = " + output);

        try {
            FileUtils.writeStringToFile(output, contents, "utf-8");
        } catch (IOException e) {
            throw SystemException.unchecked(e);
        }
    }

    protected void writeFile(String templateName, String filename, Map<String, Object> context) {
        try {
            templateName = "/config/" + name + "/" + templateName;
            String str = TemplateEngine.apply(templateName, context);
            str = StringUtils.replace(str, "#^", "#");
            str = StringUtils.replace(str, "$^", "$");
            writeFile(filename, str);
        } catch (Throwable e) {
            throw SystemException.unchecked(e);
        }
    }

    public void clean() {
        try {
            FileUtils.deleteDirectory(new File(outputdir, name));
        } catch (IOException e) {
            throw SystemException.unchecked(e);
        }
    }

    /**
     * 任务执行入口函数
     */
    public abstract void execute() throws Throwable;

}
