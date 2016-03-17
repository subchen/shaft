package jetbrick.schema.app.reader;

import java.io.File;
import jetbrick.commons.xml.XmlNode;
import jetbrick.schema.app.model.BulkFile;
import jetbrick.schema.app.model.Schema;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BulkFileUtils {
    private static final Logger log = LoggerFactory.getLogger(BulkFileUtils.class);

    public static void mappingBulkList(Schema schema, XmlNode root) {
        for (XmlNode node : root.selectNodes("bulk-list/bulk")) {
            String xml = node.attribute("file").asString();
            File fileXml = new File(schema.getSchemaFile().getParentFile(), xml);
            mappingBulkFile(schema, fileXml);
        }
    }

    // read bulk
    private static void mappingBulkFile(Schema schema, File file) {
        log.debug("bulk file = " + file);

        String name = StringUtils.substringBeforeLast(file.getName(), ".");
        BulkFile bulk = new BulkFile(file, schema.getTable(name));
        schema.getBulkFiles().add(bulk);
    }
}
