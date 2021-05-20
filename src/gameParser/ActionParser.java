package gameParser;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import gameParser.action.StagAction;
import org.json.simple.parser.ParseException;

public class ActionParser {
    private final ArrayList<StagAction> stagActionList;

    public ActionParser(String actionFilename) throws IOException {
        this.stagActionList = new ArrayList<>();
        FileReader reader = new FileReader(actionFilename);
        try {
            setStagActionList(reader);
        } catch (ParseException e) {
            System.err.println(e);
        }
    }

    private void setStagActionList(FileReader reader) throws IOException, ParseException{
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(reader);
        JSONArray actions = (JSONArray) jsonObject.get("actions");
        for (Object object : actions) {
            JSONObject action = (JSONObject) object;
            HashSet<String> triggers = getHashSetFromJOSN(action, "triggers");
            HashSet<String> subjects = getHashSetFromJOSN(action, "subjects");
            HashSet<String> consumed = getHashSetFromJOSN(action, "consumed");
            HashSet<String> produced = getHashSetFromJOSN(action, "produced");
            String narration = action.get("narration").toString();
            StagAction stagAction = new StagAction(triggers, subjects, consumed, produced, narration);
            stagActionList.add(stagAction);
        }
    }

    private HashSet<String> getHashSetFromJOSN(JSONObject action, String key) {
        JSONArray jsonArray = (JSONArray) action.get(key);
        HashSet<String> hashSet = new HashSet<>();
        for (Object o : jsonArray) {
            hashSet.add(o.toString());
        }
        return hashSet;
    }

    public ArrayList<StagAction> getStagActionList() {
        return stagActionList;
    }

}
