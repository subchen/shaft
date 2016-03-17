package jetbrick.schema.app.model;

public class PrimaryKey {
    protected String name;
    protected TableColumn column;
    protected String sequence; // support oracle sequance (gencode in *.hbm.xml)

    public PrimaryKey() {
        name = "pk";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setColumn(TableColumn column) {
        this.column = column;
    }

    public TableColumn getColumn() {
        return column;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

}
