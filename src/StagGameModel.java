import gameParser.ActionParser;
import gameParser.GraphParser;
import gameParser.action.StagAction;
import gameParser.entity.Artefact;
import gameParser.entity.Location;
import gameParser.entity.Player;
import gameParser.entity.StagEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;

public class StagGameModel {
    private ArrayList<Location> locationList;
    private HashMap<StagEntity, String> entityMap;
    private IdentityHashMap<String, String> pathMap;
    private ArrayList<StagAction> stagActionList;
    private HashSet<String> buildInCommandsSet;
    private HashMap<Artefact, String> inventoryMap;

    public StagGameModel(String entityFilename, String actionFilename) throws IOException {
        // graph
        GraphParser graphParser = new GraphParser(entityFilename);
        locationList = graphParser.getLocationList();
        entityMap = graphParser.getEntityMap();
        pathMap = graphParser.getPathMap();

        // actions
        ActionParser actionParser = new ActionParser(actionFilename);
        stagActionList = actionParser.getStagActionList();

        // stand build-in gameplay commands
        buildInCommandsSet = new HashSet<>();
        setBuildInCommandsSet();

        // inventoryMap
        this.inventoryMap = new HashMap<>();
    }

    private void setBuildInCommandsSet() {
        // lists all of the artefacts currently being carried by the player
        buildInCommandsSet.add("inventory");
        buildInCommandsSet.add("inv");
        // picks up a specified artefact from current location and puts it into player's inventory
        buildInCommandsSet.add("get");
        // puts down an artefact from player's inventory and places it into the current location
        buildInCommandsSet.add("drop");
        // moves from one location to another (if there is a path between the two)
        buildInCommandsSet.add("goto");
        // describes the entities in the current location and lists the paths to other locations
        buildInCommandsSet.add("look");
    }

    public ArrayList<Location> getLocationList() {
        return locationList;
    }

    public HashMap<StagEntity, String> getEntityMap() {
        return entityMap;
    }

    public IdentityHashMap<String, String> getPathMap() {
        return pathMap;
    }

    public ArrayList<StagAction> getStagActionList() {
        return stagActionList;
    }

    public HashSet<String> getBuildInCommandsSet() {
        return buildInCommandsSet;
    }

    public HashMap<Artefact, String> getInventoryMap() {
        return inventoryMap;
    }

    public HashSet<StagEntity> getEntitySet() {
        HashSet<StagEntity> entitySet = new HashSet<>();
        for (StagEntity stagEntity: entityMap.keySet()) {
            entitySet.add(stagEntity);
        }
        return entitySet;
    }

}
