import gameParser.action.StagAction;
import gameParser.entity.Artefact;
import gameParser.entity.Location;
import gameParser.entity.Player;
import gameParser.entity.StagEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class StagController {
    private StagGameModel gameWorld;
    private Location curLocation;
    private Player curPlayer;

    public StagController(StagGameModel stagGameModel) throws IOException {
        this.gameWorld = stagGameModel;
    }

    public String handleTokens(String[] tokens) {
        // get the player name by the first token, set the curPlayer and curLocationName
        String curPlayerName = tokens[0].split(":")[0];// remove :
        setCurPlayerAndLocation(curPlayerName);
        // Identify command type and handle it
        StringBuffer message = new StringBuffer();
        if (isBuildInCommand(tokens)) {
            message.append(handleBuildInCommand(tokens)).append("\n");
        } else if (isContainTriggers(tokens)) {
            message.append(handleActionCommand(tokens)).append("\n");
        } else {
            message.append("Did not find any build-in commands or trigger words, please enter some real commands.");
        }

        return message.toString();
    }

    /* find where is the curPlayer in the entityMap.
     * And set his/her location. */
    private void setCurPlayerAndLocation(String curPlayName) {
        // If the curPlayer is in the Map, set he/she as curPlayer and get his/her location
        for (StagEntity entity: gameWorld.getEntityMap().keySet()) {
            if (entity.getClassName().equals("Player")) {
                if (entity.getName().equals(curPlayName)) {
                    this.curPlayer =  (Player) entity;
                    // set curLocation
                    String curLocationName = gameWorld.getEntityMap().get(entity);
                    this.curLocation = getLocationByName(curLocationName);
                    return;
                }
            }
        }
        // curPlayName is not in the Map, add it as a new player to the start point
        this.curPlayer = new Player(curPlayName, "");
        this.curLocation = gameWorld.getLocationList().get(0);
        gameWorld.getEntityMap().put(this.curPlayer, this.curLocation.getName());
    }

    private Location getLocationByName(String locationName) {
        for (Location loc: gameWorld.getLocationList()) {
            if (loc.getName().equals(locationName)) {
                return loc;
            }
        }
        return null;
    }

    private boolean isBuildInCommand(String[] tokens) {
        return gameWorld.getBuildInCommandsSet().contains(tokens[1]);
    }

    private String handleBuildInCommand(String[] tokens) {
        String returnMessage = "";
        switch (tokens[1]) {
            case "inventory":
            case "inv":
                returnMessage = handleInv();
                break;
            case "get":
                returnMessage = handleGet(tokens);
                break;
            case "drop":
                returnMessage = handleDrop(tokens);
                break;
            case "goto":
                returnMessage = handleGoto(tokens);
                break;
            case "look":
                returnMessage = handleLook();
                break;
        }
        return returnMessage;
    }

    private String handleInv() {
        boolean isEmpty = true;
        StringBuilder stringBuffer = new StringBuilder("The artefacts in your bag are as follows: ");
        for (Artefact artefact: gameWorld.getInventoryMap().keySet()) {
            if (gameWorld.getInventoryMap().get(artefact).equals(this.curPlayer.getName())) {
                isEmpty = false;
                stringBuffer.append("\n").append(artefact.getDescription());
            }
        }
        if (isEmpty) {
            return "Your bag is empty.";
        }
        return stringBuffer.toString();
    }

    private String handleGet(String[] tokens) {
        if (tokens.length < 3) {
            return "You need to enter the name of the item to be collected.";
        }
        for (StagEntity entity: gameWorld.getEntitySet()) {
            if (entity.getName().equals(tokens[2]) &&
                    gameWorld.getEntityMap().get(entity).equals(this.curLocation.getName())) {
                if (entity.isCollectible()) {
                    // put it to inventoryMap and remove from entityMap
                    gameWorld.getInventoryMap().put((Artefact)entity, this.curPlayer.getName());
                    gameWorld.getEntityMap().remove(entity);
                    return "You picked up a " + entity.getName() + ".";
                } else {
                    return "You can't get the " + entity.getName() + " because it cannot be collected.";
                }
            }
        }
        return "Here is no such thing.";
    }

    private String handleDrop(String[] tokens) {
        if (tokens.length < 3) {
            return "You need to enter the name of the item to be dropped.";
        }
        for (Artefact artefact: gameWorld.getInventoryMap().keySet()) {
            if (artefact.getName().equals(tokens[2]) &&
                    gameWorld.getInventoryMap().get(artefact).equals(this.curPlayer.getName())) {
                // remove it from inventoryMap and put to entityMap
                gameWorld.getInventoryMap().remove(artefact);
                gameWorld.getEntityMap().put(artefact, this.curLocation.getName());
                return "You dropped " + artefact.getName() + ".";
            }
        }
        return "You don't have such item.";
    }

    private String handleGoto(String[] tokens) {
        if (tokens.length < 3) {
            return "You need to enter the name of the location to go.\n";
        }
        if (tokens[2].equals(this.curLocation.getName())) {
            return "You are already at this location.";
        }
        for (String startLocationName: gameWorld.getPathMap().keySet()) {
            if (startLocationName.equals(this.curLocation.getName()) &&
                    gameWorld.getPathMap().get(startLocationName).equals(tokens[2])) {
                String curLocationName = tokens[2];
                this.curLocation = getLocationByName(curLocationName);
                gameWorld.getEntityMap().put(this.curPlayer, curLocationName);
                return handleLook();
            }
        }
        return "There is no path to such location.";
    }

    private String handleLook() {
        StringBuilder stringBuilder = new StringBuilder("You are in ");
        stringBuilder.append(this.curLocation.getDescription()).append(". You can see: ");
        // list entities
        for (StagEntity stagEntity: getAllEntityAtCurLocation()) {
            if (stagEntity.getClassName().equals("Player")) {
                // do not show the curPlayer himself/herself
                if (!stagEntity.getName().equals(curPlayer.getName())) {
                    stringBuilder.append("\nPlayer: ").append(stagEntity.getName());
                }
            } else {
                stringBuilder.append("\n").append(stagEntity.getDescription());
            }
        }
        stringBuilder.append("\n").append("You can access from here: ");
        for (String startLocationName: gameWorld.getPathMap().keySet()) {
            if (startLocationName.equals(this.curLocation.getName())) {
                stringBuilder.append("\n").append(gameWorld.getPathMap().get(startLocationName));
            }
        }
        return stringBuilder.toString();
    }

    private HashSet<StagEntity> getAllEntityAtCurLocation() {
        HashSet<StagEntity> entitySetAtCurLocation = new HashSet<>();
        for (StagEntity stagEntity: gameWorld.getEntitySet()) {
            if (gameWorld.getEntityMap().get(stagEntity).equals(this.curLocation.getName())) {
                entitySetAtCurLocation.add(stagEntity);
            }
        }
        return entitySetAtCurLocation;
    }

    private boolean isContainTriggers(String[] tokens) {
        ArrayList<StagAction> stagActionList = gameWorld.getStagActionList();
        for (int i = 1; i < tokens.length; i++) {
            for (StagAction stagAction: stagActionList) {
                if (stagAction.getTriggers().contains(tokens[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    private String handleActionCommand(String[] tokens) {
        return "";
    }

}
