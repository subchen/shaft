package jetbrick.schema.app.model;

import java.util.ArrayList;
import java.util.List;
import jetbrick.util.ArrayUtils;
import jetbrick.util.StringUtils;
import jetbrick.util.builder.ToStringBuilder;

public final class Index {
    private String name;
    private boolean unique;
    private List<Column> columns;
    private String sorts;

    public Index() {
        columns = new ArrayList<Column>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public String getSorts() {
        return sorts;
    }

    public void setSorts(String sorts) {
        this.sorts = sorts;
    }

    public String asColumnList() {
        StringBuilder sb = new StringBuilder();
        for (Column c : columns) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(c.getColumnName());
        }
        return sb.toString();
    }

    public String asFieldList() {
        StringBuilder sb = new StringBuilder();
        for (Column c : columns) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(c.getFieldName());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflection(this);
    }
}
