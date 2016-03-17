package jetbrick.schema.app.model.methods;

import java.util.ArrayList;
import java.util.List;
import jetbrick.schema.app.model.TableColumn;
import jetbrick.schema.app.model.TableInfo;
import org.apache.commons.lang3.StringUtils;

public class TableInfoUtils {

    public static boolean pkIsNumeric(TableInfo t) {
        return Number.class.isAssignableFrom(t.getPrimaryKey().getColumn().getFieldClass());
    }

    public static TableColumn pk(TableInfo t) {
        return t.getPrimaryKey().getColumn();
    }

    public static String fullClassName(TableInfo t) {
        return t.getSchema().getPackageName() + ".data." + t.getTableClass();
    }

    public static List<TableColumn> unpkColumns(TableInfo t) {
        List<TableColumn> list = new ArrayList<TableColumn>(t.getColumns().size() - 1);
        for (TableColumn c : t.getColumns()) {
            if (!c.isPrimaryKey()) {
                list.add(c);
            }
        }
        return list;
    }

    public static String fieldlist(TableInfo t, boolean pkFirst) {
        List<String> names = new ArrayList<String>();
        for (TableColumn c : t.getColumns()) {
            if (!c.isPrimaryKey()) {
                names.add(c.getFieldName());
            }
        }
        String pk = t.getPrimaryKey().getColumn().getFieldName();
        if (pkFirst) {
            names.add(0, pk);
        } else {
            names.add(pk);
        }
        return StringUtils.join(names, ", ");
    }

    public static String hbmXmlFullPath(TableInfo t) {
        String path = StringUtils.replace(t.getSchema().getPackageName(), ".", "/");
        return path + "/data/hbm_" + t.getSchema().getDialect().getName() + "/" + t.getTableClass() + ".hbm.xml";
    }

    public static String hbmTableName(TableInfo t) {
        return t.getSchema().getDialect().getIdentifier(t.getTableName());
    }
}
