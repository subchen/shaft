package shaft.entity;

public interface SimpleDaoHelperCallback<T extends SimpleDaoHelper> {

    public void execute(T dao);

}
