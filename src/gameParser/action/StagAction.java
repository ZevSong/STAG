package gameParser.action;

import java.util.HashSet;

public class StagAction {
    private HashSet<String> triggers;
    private HashSet<String> subjects;
    private HashSet<String> consumed;
    private HashSet<String> produced;
    private String narration;

    public StagAction(HashSet<String> triggers, HashSet<String> subjects,
                      HashSet<String> consumed, HashSet<String> produced,
                      String narration) {
        this.triggers = triggers;
        this.subjects = subjects;
        this.consumed = consumed;
        this.produced = produced;
        this.narration = narration;
    }

    public HashSet<String> getTriggers() {
        return triggers;
    }

    public HashSet<String> getSubjects() {
        return subjects;
    }

    public HashSet<String> getConsumed() {
        return consumed;
    }

    public HashSet<String> getProduced() {
        return produced;
    }

    public String getNarration() {
        return narration;
    }


}
