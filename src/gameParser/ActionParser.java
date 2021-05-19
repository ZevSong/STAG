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

    public ActionParser(String actionFilename) {
        this.stagActionList = new ArrayList<>();
        try {
            FileReader reader = new FileReader(actionFilename);
            setStagActionList(reader);
        } catch (IOException | ParseException e) {
            System.out.println(e);
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
            String narration = getHashSetFromJOSN(action, "narration").toString();
            StagAction stagAction = new StagAction(triggers, subjects, consumed, produced, narration);
            stagActionList.add(stagAction);
        }
    }

    private HashSet<String> getHashSetFromJOSN(JSONObject action, String key) {
        JSONArray jsonArray = (JSONArray) action.get(key);
        HashSet<String> hashSet = new HashSet<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            hashSet.add(jsonArray.get(i).toString());
        }
        return hashSet;
    }

    public ArrayList<StagAction> getStagActionList() {
        return stagActionList;
    }

}
