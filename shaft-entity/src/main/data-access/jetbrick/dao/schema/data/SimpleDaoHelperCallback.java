package jetbrick.dao.schema.data;

public interface SimpleDaoHelperCallback<T extends SimpleDaoHelper> {

    public void execute(T dao);

}
