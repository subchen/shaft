package jetbrick.schema.app.reader;

import java.util.ArrayList;
import java.util.List;
import jetbrick.commons.lang.CamelCaseUtils;
import jetbrick.schema.app.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理一对多关系
 */
public class OneToManyUtils {
    private static final Logger log = LoggerFactory.getLogger(OneToManyUtils.class);
    private static final List<Object[]> cache = new ArrayList<Object[]>();

    //将加到缓存队列里面
    protected static void add(TableColumn c, String refTable, String importName, String exportName) {
        cache.add(new Object[] { c, refTable, importName, exportName });
    }

    //在所有的表都加载完成后，在同一处理 one-to-many 关系
    protected static void process(Schema schema) {
        log.debug("Starting process one-to-many after tables loaded.");
        for (Object[] item : cache) {
            TableColumn column = (TableColumn) item[0];
            TableInfo reference = schema.getTable((String) item[1]);
            String importName = (String) item[2];
            String exportName = (String) item[3];

            if (reference == null) {
                String error = String.format("Reference table [%s] is not found for column [%s.%s].", item[1], column.getTable().getTableName(), column.getColumnName());
                throw new RuntimeException(error);
            }

            // import relation 
            {
                OneToManyRelation rel = new OneToManyRelation();
                if (importName == null || "auto".equals(importName)) {
                    importName = reference.getTableName();
                }
                importName = "get" + CamelCaseUtils.toCapitalizeCamelCase(importName);
                rel.setName(importName);
                rel.setColumn(column);
                rel.setReference(reference);
                column.getTable().getImportedRelations().add(rel);
            }

            // export relation
            if (exportName != null) {
                OneToManyRelation rel = new OneToManyRelation();
                if ("auto".equals(exportName)) {
                    exportName = column.getTable().getTableName();
                }
                exportName = "get" + CamelCaseUtils.toCapitalizeCamelCase(exportName) + "List";
                rel.setName(exportName);
                rel.setColumn(column);
                rel.setReference(reference);
                reference.getExportedRelations().add(rel);
            }
        }
    }

}
