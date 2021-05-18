package entity;

public class Artefact extends Entity {
    public Artefact(String name, String description) {
        super(name, description);
    }

    @Override
    public boolean isMovable() {
        return true;
    }
}
