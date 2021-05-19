package gameParser.entity;

public class StagEntity {
    private String name;
    private String description;

    public StagEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isMovable() {
        return false;
    }


}
