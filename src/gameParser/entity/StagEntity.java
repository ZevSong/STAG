package gameParser.entity;

public class StagEntity {
    private final String name;
    private final String description;

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

    public String getClassName() {
        return "StagEntity";
    }

    public boolean isCollectible() {
        return false;
    }


}
