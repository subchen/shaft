package shaft.sync.schema;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class SchemaUtilTest {

    @Test
    public void testGetSchemaInfoList() throws IOException {
        List<Table> schemaList = SchemaUtils.getSchemaInfoList();
        Assert.assertEquals(1, schemaList.size());
    }
}
