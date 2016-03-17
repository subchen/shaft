package jetbrick.dao.schema.data;

import java.io.Serializable;
import jetbrick.dao.id.JdbcSequenceIdProvider;
import jetbrick.dao.id.SequenceIdProvider;
import jetbrick.dao.orm.DataSourceUtils;
import jetbrick.dao.schema.validator.Validator;
import jetbrick.dao.schema.validator.ValidatorException;
import com.alibaba.fastjson.JSONAware;
import com.alibaba.fastjson.JSONObject;

/**
 * 数据库表的对象的基类
 */
@SuppressWarnings("serial")
public abstract class Entity implements Serializable, Cloneable, JSONAware {

    public static final Serializable[] EMPTY_ID_ARRAY = new Serializable[0];

    protected static final SequenceIdProvider SEQ_PROVIDER = new JdbcSequenceIdProvider(DataSourceUtils.getDataSource());

    //------ id -----------------------------------------
    protected Serializable id;

    public Serializable getId() {
        return id;
    }

    //生成并返回ID
    public abstract Serializable generateId();

    //------ dao ---------------------------------
    public abstract int save();

    public abstract int update();

    public abstract int saveOrUpdate();

    public abstract int delete();

    public abstract Object[] dao_insert_parameters();

    public abstract Object[] dao_update_parameters();

    //------ validate -----------------------------------------
    public abstract void validate();

    protected void validate(SchemaColumn column, Object value) {
        if (value == null || "".equals(value)) {
            if (column.isNullable()) return;
            throw new ValidatorException("%s(%s) must be not empty.", column.getDisplayName(), column.getFieldName());
        }
        for (Validator v : column.getValidators()) {
            v.validate(column.getFieldName(), value);
        }
    }

    //------ default value -----------------------------------------
    public abstract void makeDefaults();

    //------ json -----------------------------------------
    public abstract JSONObject toJSONObject();

    @Override
    public String toJSONString() {
        return toJSONObject().toString();
    }

    @Override
    public String toString() {
        return toJSONObject().toString();
    }

    //------ end -----------------------------------------
}
