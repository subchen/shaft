package jetbrick.schema.app.model;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * 一组枚举变量
 */
public class EnumGroup {
    protected int pid;
    protected String identifier;
    protected String description;
    protected EnumGroup parent;
    protected List<EnumItem> items = new ArrayList<EnumItem>();

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EnumGroup getParent() {
        return parent;
    }

    public void setParent(EnumGroup parent) {
        this.parent = parent;
    }

    public List<EnumItem> getItems() {
        return items;
    }

    public void setItems(List<EnumItem> items) {
        this.items = items;
    }

    public boolean isClassGenSupported() {
        if (StringUtils.isNotBlank(identifier)) {
            for (EnumItem en : items) {
                if (StringUtils.isNotBlank(en.getIdentifier())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isIndependence() {
        return parent == null;
    }

    public EnumGroup createChild() {
        EnumGroup info = new EnumGroup();
        info.pid = pid;
        info.identifier = identifier;
        info.description = description;
        info.items = items;
        info.parent = this; // set parent -> this
        return info;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
