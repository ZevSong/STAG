package gameParser.entity;

public class Character extends StagEntity {
    public Character(String name, String description) {
        super(name, description);
    }

    @Override
    public String getClassName() {
        return "Character";
    }
}
