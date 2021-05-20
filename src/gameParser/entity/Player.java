package gameParser.entity;

public class Player extends StagEntity {
    private int healthLevel;
    public Player(String name, String description) {
        super(name, description);
        // set the default health level is 3
        this.healthLevel = 3;
    }

    @Override
    public String getClassName() {
        return "Player";
    }

    // input how much health wanna to improve
    public void improveHealth(int improveLevel) {
        this.healthLevel += improveLevel;
    }

    // input how much health wanna to loose
    public void loseHealth(int loseLevel) {
        this.healthLevel -= loseLevel;
    }

    public int getHealthLevel() {
        return healthLevel;
    }

    public void resetHealth() {
        this.healthLevel = 3;
    }
}
