package jetbrick.schema.app.reader;

import java.io.File;
import jetbrick.commons.lang.CamelCaseUtils;
import jetbrick.commons.xml.XmlNode;
import jetbrick.schema.app.model.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnumFileUtils {
    private static final Logger log = LoggerFactory.getLogger(EnumFileUtils.class);

    public static void mappingEnumGroupList(Schema schema, XmlNode root) {
        for (XmlNode node : root.selectNodes("enum-list/enum-group")) {
            String xml = node.attribute("file").asString();
            File fileXml = new File(schema.getSchemaFile().getParentFile(), xml);
            mappingEnumGroupFile(schema, fileXml);
        }
    }

    // read enum from file
    private static void mappingEnumGroupFile(Schema schema, File fileXml) {
        log.debug("enum file = " + fileXml);

        XmlNode root = XmlNode.create(fileXml);
        for (XmlNode node : root.elements()) {
            createSchemaEnumGroup(schema, node);
        }
    }

    protected static EnumGroup createSchemaEnumGroup(Schema schema, XmlNode node) {
        Integer pid = node.attribute("pid").asInt();
        if (pid == null) {
            String error = String.format("Missing group attribute for enum-group node.");
            throw new RuntimeException(error);
        }

        EnumGroup g = new EnumGroup();
        g.setPid(pid);
        String identifier = node.attribute("class").asString();
        if (StringUtils.isNotBlank(identifier)) {
            identifier = CamelCaseUtils.toCapitalizeCamelCase(identifier);
            g.setIdentifier(identifier);
        }
        g.setDescription(node.attribute("desc").asString());

        for (XmlNode n : node.elements()) {
            EnumItem en = new EnumItem();
            en.setPid(pid);

            Integer id = n.attribute("id").asInt();

            en.setId(pid * 1000 + id);
            en.setName(n.attribute("name").asString());
            identifier = n.attribute("var").asString();
            if (StringUtils.isNotBlank(identifier)) {
                identifier = "e" + CamelCaseUtils.toCapitalizeCamelCase(identifier);
                en.setIdentifier(identifier);
            }
            g.getItems().add(en);
        }
        schema.addEnumGroup(g);
        return g;
    }
}
