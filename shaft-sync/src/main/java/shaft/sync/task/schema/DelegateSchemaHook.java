package shaft.sync.task.schema;

import java.util.ArrayList;
import java.util.List;

import shaft.dao.metadata.DbTable;

public final class DelegateSchemaHook implements SchemaHook {
    private final List<SchemaHook> hookList = new ArrayList<>(8);

    public void add(SchemaHook hook) {
        hookList.add(hook);
    }

    public void tableCreateBefore(DbTable table) {
        for (int i = 0; i < hookList.size(); i++) {
            hookList.get(i).tableCreateBefore(table);
        }
    }

    public void tableCreateAfter(DbTable table) {
        for (int i = 0; i < hookList.size(); i++) {
            hookList.get(i).tableCreateAfter(table);
        }
    }
}
