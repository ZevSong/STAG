package gameParser.entity;

public class Player extends StagEntity {
    public Player(String name, String description) {
        super(name, description);
    }

    @Override
    public String getClassName() {
        return "Player";
    }
}
