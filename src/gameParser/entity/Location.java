package gameParser.entity;

public class Location extends StagEntity {
    public Location(String name, String description) {
        super(name, description);
    }

    @Override
    public String getClassName() {
        return "Location";
    }
}
