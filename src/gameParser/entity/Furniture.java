package gameParser.entity;

public class Furniture extends StagEntity {
    public Furniture(String name, String description) {
        super(name, description);
    }

    @Override
    public String getClassName() {
        return "Furniture";
    }
}
