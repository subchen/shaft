package shaft.sync.task.schema;

import shaft.dao.metadata.DbColumn;
import shaft.dao.metadata.DbTable;

public interface SchemaHook {

    default void tableCreateBefore(DbTable table) {
    }

    default void tableCreateAfter(DbTable table) {
    }

    default void tableUpdateAfter(DbTable table) {
    }

    default void tableDeleteBefore(DbTable table) {
    }

    default void tableDeleteAfter(DbTable table) {
    }

    default void columnCreateAfter(DbColumn column) {
    }

    default void columnUpdateAfter(DbColumn column) {
    }

    default void columnDeleteBefore(DbColumn column) {
    }

    default void columnDeleteAfter(DbColumn column) {
    }

    default void start() {
    }

    default void done() {
    }
}
