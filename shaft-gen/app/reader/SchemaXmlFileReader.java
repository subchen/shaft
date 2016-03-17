package jetbrick.schema.app.reader;

import java.io.File;
import jetbrick.commons.exception.SystemException;
import jetbrick.commons.xml.XmlNode;
import jetbrick.schema.app.model.Schema;

/**
 * 从 schema.xml 文件中读取 schema 定义
 */
public class SchemaXmlFileReader {
    private Schema schema = new Schema();

    public SchemaXmlFileReader(File fileXml) {
        try {
            mappingSchemaXml(fileXml);
        } catch (Throwable e) {
            throw SystemException.unchecked(e);
        }
    }

    public Schema getSchema() {
        return schema;
    }

    // read a schema from xml
    private void mappingSchemaXml(File schemaXml) throws Exception {
        XmlNode root = XmlNode.create(schemaXml);
        schema.setSchemaFile(schemaXml);

        for (XmlNode node : root.selectNodes("properties/property")) {
            String name = node.attribute("name").asString();
            String value = node.attribute("value").asString();
            schema.getProperties().setProperty(name, value);
        }

        for (XmlNode node : root.selectNodes("aliases/alias")) {
            String from = node.attribute("from").asString();
            String to = node.attribute("to").asString();
            schema.getTypeNameAlias().setProperty(from, to);
        }

        EnumFileUtils.mappingEnumGroupList(schema, root);

        TableFileUtils.mappingTableList(schema, root);

        BulkFileUtils.mappingBulkList(schema, root);

    }

}
