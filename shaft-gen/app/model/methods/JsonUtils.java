package jetbrick.schema.app.model.methods;

import com.alibaba.fastjson.JSON;

public class JsonUtils {

    public static String toJSON(Object object) {
        return JSON.toJSONString(object);
    }
}
