package jetbrick.schema.app.model;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 索引
 */
public class TableIndex {
    protected String name;
    protected List<TableColumn> columns = new ArrayList<TableColumn>();
    protected boolean unique;
    protected String sorts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TableColumn> getColumns() {
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

    public String columnlist() {
        StringBuffer sb = new StringBuffer();
        for (TableColumn c : columns) {
            if (sb.length() > 0) sb.append(",");
            sb.append(c.getColumnName());
        }
        return sb.toString();
    }

    public String fieldlist() {
        StringBuffer sb = new StringBuffer();
        for (TableColumn c : columns) {
            if (sb.length() > 0) sb.append(",");
            sb.append(c.getFieldName());
        }
        return sb.toString();
    }

    public String getOrderby() {
        String[] sort = StringUtils.isBlank(sorts) ? ArrayUtils.EMPTY_STRING_ARRAY : StringUtils.split(sorts, ",");
        int i = 0;

        StringBuffer sb = new StringBuffer();
        for (TableColumn c : columns) {
            if (sb.length() > 0) sb.append(",");
            sb.append(c.getColumnName());

            if (sort.length > i) {
                sb.append(" " + sort[i++].trim());
            }
        }
        return sb.toString();
    }
}
