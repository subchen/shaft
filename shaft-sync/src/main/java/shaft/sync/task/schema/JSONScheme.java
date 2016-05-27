package shaft.sync.task.schema;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import shaft.dao.metadata.DbTable;

/**
 * 从 JSON 文件中读取所有的 schema 信息
 */
public final class JSONScheme {

    public static List<DbTable> getSchemaInfoList() {
        List<DbTable> schemaList = new ArrayList<>(64);


        List<String> fileList = null;
        for (String file : fileList) {
            schemaList.add(getSchemaInfo(file));
        }
        return schemaList;
    }

    private static DbTable getSchemaInfo(String file) {
        DbTable.Builder builder = new DbTable.Builder();

        return builder.build();
    }

    private Object unmarshal(File file) {
        ObjectMapper mapper = new ObjectMapper();
        //mapper.readValue(file, Table.class);
    }

    public static void main(String args[]) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = JSONScheme.class.getResourceAsStream("/model/userinfo.json");
        Table table = mapper.readValue(is, Table.class);
        System.out.println(table);
    }

    public static class Table {
        private String name;

        @JsonProperty("class")
        private String clazz;

        private List<Column> columns;
    }

    public static class Column {
        private String name;
        private String type;
        private boolean required;
    }
}
