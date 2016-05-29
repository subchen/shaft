package shaft.sync.schema;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import jetbrick.util.ClassLoaderUtils;

/**
 * 从 JSON 文件中读取所有的 schema 信息
 */
public final class SchemaUtils {

    public static List<Table> getSchemaInfoList() throws IOException {
        List<Table> schemaList = new ArrayList<>(64);

        ObjectMapper mapper = new ObjectMapper();
        InputStream is = ClassLoaderUtils.getResourceAsStream("shaft/schema.json");
        Schema schema = mapper.readValue(is, Schema.class);

        for (String file : schema.getTables()) {
            schemaList.add(getSchemaInfo(file, mapper));
        }
        return schemaList;
    }

    private static Table getSchemaInfo(String file, ObjectMapper mapper) throws IOException {
        InputStream is = ClassLoaderUtils.getResourceAsStream("shaft/" + file);
        return mapper.readValue(is, Table.class);
    }

}
