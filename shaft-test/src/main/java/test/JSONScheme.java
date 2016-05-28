package test;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

import jetbrick.util.ClassLoaderUtils;

/**
 * 从 JSON 文件中读取所有的 schema 信息
 */
public final class JSONScheme {

    public static void main(String args[]) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = ClassLoaderUtils.getResourceAsStream("model/userinfo.json");
        Table table = mapper.readValue(is, Table.class);
        System.out.println(table);
    }

}
