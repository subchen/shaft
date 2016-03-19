package jetbrick.dao.schema.upgrade;

import jetbrick.dao.schema.data.*;

public interface SchemaHook {

    public void whenTableCreated(SchemaInfo<? extends Entity> table);

    public void whenColumnCreated(SchemaColumn column);

}
