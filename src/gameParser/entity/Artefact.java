package gameParser.entity;

public class Artefact extends StagEntity {
    public Artefact(String name, String description) {
        super(name, description);
    }

    @Override
    public boolean isMovable() {
        return true;
    }
}
