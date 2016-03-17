package jetbrick.schema.app.model;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import jetbrick.commons.exception.SystemException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 初始化数据文件
 */
public class BulkFile {
    protected TableInfo table;
    protected String fileName;
    protected String contents;

    public BulkFile(File file, TableInfo table) {
        try {
            List<String> lines = IOUtils.readLines(new FileInputStream(file), "utf-8");
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                sb.append(line).append("\n");
            }

            this.table = table;
            this.fileName = file.getName();
            this.contents = sb.toString();
        } catch (Exception e) {
            throw SystemException.unchecked(e);
        }
    }

    public TableInfo getTable() {
        return table;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContents() {
        return contents;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
