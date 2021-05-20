import gameParser.action.StagAction;
import gameParser.entity.Artefact;
import gameParser.entity.Location;
import gameParser.entity.Player;
import gameParser.entity.StagEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class StagController {
    private final StagGameModel gameWorld;
    private Location curLocation;
    private Player curPlayer;

    public StagController(StagGameModel stagGameModel) throws IOException {
        this.gameWorld = stagGameModel;
    }

    public String handleTokens(String playerName, String[] tokens) {
        // get the player name by the first token, set the curPlayer and curLocationName
        setCurPlayerAndLocation(playerName);
        // Identify command type and handle it
        StringBuilder message = new StringBuilder();
        if (isBuildInCommand(tokens)) {
            message.append(handleBuildInCommand(tokens)).append("\n");
        } else if (isContainTriggers(tokens)) {
            message.append(handleActionCommand(tokens)).append("\n");
        } else {
            message.append("Did not find any build-in commands or trigger words, please enter some real commands.");
        }
        // check curPlayer healthLevel
        if (curPlayer.getHealthLevel() <= 0) {
            message.append("Your health level <= 0, you died and all the items in your pocket are dropped.\n");
            message.append("You are resurrected at the starting point.");
            resetCurPlayer();
        }
        // update curPlayer
        gameWorld.getEntityMap().put(curPlayer, curLocation.getName());

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
        for (String token: tokens) {
            if (gameWorld.getBuildInCommandsSet().contains(token)) {
                return true;
            }
        }
        return false;
    }

    // switch to different build-in command cases
    private String handleBuildInCommand(String[] tokens) {
        if (isTokensContained(tokens, "inventory") || isTokensContained(tokens, "inv")) {
            return handleInv();
        } else if (isTokensContained(tokens, "get")) {
            return handleGet(tokens);
        } else if (isTokensContained(tokens, "drop")) {
            return handleDrop(tokens);
        } else if (isTokensContained(tokens, "goto")) {
            return handleGoto(tokens);
        }else if (isTokensContained(tokens, "look")) {
            return handleLook();
        }
        return null;
    }

    // return the information of curPlayer's inventory
    private String handleInv() {
        boolean isEmpty = true; // used to check if the curPlayer's inventory is empty
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

    /* get command, pick the item for curPlayer
     * should enter word [get] and [ArtefactName] */
    private String handleGet(String[] tokens) {
        if (tokens.length < 2) {
            return "You need to enter the name of the item to be collected.";
        }
        for (StagEntity entity: gameWorld.getEntitySet()) {
            for (String token: tokens) {
                if (entity.getName().equals(token) &&
                        gameWorld.getEntityMap().get(entity).equals(this.curLocation.getName())) {
                    if (entity.isCollectible()) {
                        // put it to inventoryMap and remove from entityMap
                        gameWorld.getInventoryMap().put((Artefact)entity, this.curPlayer.getName());
                        // do not just remove the key-value, in case it maybe reProduced
                        gameWorld.getEntityMap().replace(entity, "");
                        return "You picked up a " + entity.getName() + ".";
                    } else {
                        return "You can't get the " + entity.getName() + " because it cannot be collected.";
                    }
                }
            }
        }
        return "Here is no such thing.";
    }

    /* drop command, drop the item for curPlayer
     * should enter word [drop] and [ArtefactName] */
    private String handleDrop(String[] tokens) {
        if (tokens.length < 2) {
            return "You need to enter the name of the item to be dropped.";
        }
        for (String token: tokens) {
            for (Artefact artefact: gameWorld.getInventoryMap().keySet()) {
                if (artefact.getName().equals(token) &&
                        gameWorld.getInventoryMap().get(artefact).equals(this.curPlayer.getName())) {
                    // remove it from inventoryMap and put to entityMap
                    gameWorld.getInventoryMap().remove(artefact);
                    gameWorld.getEntityMap().put(artefact, this.curLocation.getName());
                    return "You dropped " + artefact.getName() + ".";
                }
            }
        }
        return "You don't have such item.";
    }

    /* goto command, move the curPlayer to next location
     * also auto do the look command to show surroundings of next location
     * should enter goto [nextLocationName] */
    private String handleGoto(String[] tokens) {
        if (tokens.length < 2) {
            return "You need to enter the name of the location to go.\n";
        }
        for (String token: tokens) {
            for (String startLocationName: gameWorld.getPathMap().keySet()) {
                if (startLocationName.equals(this.curLocation.getName()) &&
                        gameWorld.getPathMap().get(startLocationName).equals(token)) {
                    this.curLocation = getLocationByName(token);
                    gameWorld.getEntityMap().put(this.curPlayer, token);
                    return handleLook();
                }
            }
        }
        return "There is no path to such location.";
    }

    /* look command, show surroundings of next location
     * should enter look */
    private String handleLook() {
        StringBuilder stringBuilder = new StringBuilder("You are in ");
        stringBuilder.append(this.curLocation.getDescription()).append(". You can see: ");
        // list entities
        for (StagEntity stagEntity: getAllEntityAtCurLocation()) {
            if (stagEntity.getClassName().equals("Player")) {
                // do not show the curPlayer himself/herself
                if (!stagEntity.getName().equals(curPlayer.getName())) {
                    stringBuilder.append("\nPlayer: ").append(stagEntity.getName()).append("\tHealthLevel: ").append(curPlayer.getHealthLevel());
                }
            } else {
                stringBuilder.append("\n").append(stagEntity.getDescription());
            }
        }
        // show the next accessible path
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
        for (String token: tokens) {
            for (StagAction stagAction: gameWorld.getStagActionList()) {
                if (stagAction.getTriggers().contains(token)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String handleActionCommand(String[] tokens) {
        StringBuilder stringBuffer = new StringBuilder();
        // find possibleActions
        ArrayList<StagAction> possibleActionList = getPossibleActionList(tokens);
        // no possible action
        if (possibleActionList.size() == 0) {
            return "Command need contains at least one trigger word and one subject for actions, please try again.";
        }
        // check conditions and run
        stringBuffer.append(handlePossibleActions(possibleActionList));
        return stringBuffer.toString();
    }

    /* check if tokens contain at least one trigger word and one subject for actions,
     * and return possible actions. */
    private ArrayList<StagAction> getPossibleActionList(String[] tokens) {
        ArrayList<StagAction> possibleActionList = new ArrayList<>();
        boolean isTriggerWordContained = false;
        boolean isSubjectContained = false;
        for (StagAction stagAction: gameWorld.getStagActionList()) {
            // in tokens, at least one trigger word for this action
            for (String triggerWord: stagAction.getTriggers()) {
                if (isTokensContained(tokens, triggerWord)) {
                   isTriggerWordContained = true;
                   break;
                }
            }
            // in tokens, at least one subject for this action
            for (String subject: stagAction.getSubjects()) {
                if (isTokensContained(tokens, subject)) {
                    isSubjectContained = true;
                    break;
                }
            }
            if (isTriggerWordContained && isSubjectContained) {
                possibleActionList.add(stagAction);
            }
        }
        return possibleActionList;
    }

    private boolean isTokensContained(String[] tokens, String keyword) {
        for (String token: tokens) {
            if (token.equals(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String handlePossibleActions(ArrayList<StagAction> possibleActions) {
        // check each action
        for (StagAction stagAction: possibleActions) {
            int counter = 0;
            // check if subjects all in location and pocket, and count
            for (String subject: stagAction.getSubjects()) {
                if (isInCurLocationOrPocket(subject)) {
                    counter++;
                }
            }
            // get enough subjects
            if (counter == stagAction.getSubjects().size()) {
                return doAction(stagAction); // each command could only trigger one action
            }
        }
        // If it runs to this point, no action is executed
        return "You can not trigger that action because there is not enough subjects.";
    }

    private boolean isInCurLocationOrPocket(String entityName) {
        for (StagEntity stagEntity: gameWorld.getEntitySet()) {
            if (stagEntity.getName().equals(entityName)) {
                if (gameWorld.getEntityMap().get(stagEntity).equals(this.curLocation.getName())) {
                    return true;
                }
            }
        }
        for (Artefact artefact: gameWorld.getInventoryMap().keySet()) {
            if (artefact.getName().equals(entityName)) {
                if (gameWorld.getInventoryMap().get(artefact).equals(this.curPlayer.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private String doAction(StagAction stagAction) {
        doConsumedAction(stagAction);
        doProducedAction(stagAction);
        return stagAction.getNarration();
    }

    /* do consume, there are 3 types of consumed: Location, health, other */
    private void doConsumedAction(StagAction stagAction) {
        for (String consumed: stagAction.getConsumed()) {
            // check if is location or health
            if (isLocation(consumed)) {
                // remove all path to that location
                for (String startLocation : gameWorld.getPathMap().keySet()) {
                    if (gameWorld.getPathMap().get(startLocation).equals(consumed)) {
                        gameWorld.getPathMap().remove(startLocation);
                    }
                }
            } else if (consumed.equals("health")) {
                curPlayer.loseHealth(1);
            } else {
                // not location or health
                removeFromWorldAndPocket(consumed);
            }
        }
    }

    private void removeFromWorldAndPocket(String name) {
        for (StagEntity stagEntity: gameWorld.getEntitySet()) {
            if (stagEntity.getName().equals(name)) {
                // do not just remove it, in case it be reProduced
                gameWorld.getEntityMap().replace(stagEntity, "");
                return;
            }
        }
        for (Artefact artefact: gameWorld.getInventoryMap().keySet()) {
            if (artefact.getName().equals(name)) {
                gameWorld.getInventoryMap().remove(artefact);
                return;
            }
        }
    }

    private boolean isLocation(String str) {
        for (Location location: gameWorld.getLocationList()) {
            if (location.getName().equals(str)) {
                return true;
            }
        }
        return false;
    }

    /* do product, there are 3 types of produced: Location, health, other */
    private void doProducedAction(StagAction stagAction) {
        for (String produced: stagAction.getProduced()) {
            // check if is location or health
            if (isLocation(produced)) {
                // new String to make sure key is repeatable
                gameWorld.getPathMap().put(new String(this.curLocation.getName()), produced);
            } else if (produced.equals("health")) {
                curPlayer.improveHealth(1);
            } else {
                // not location or health
                for (StagEntity stagEntity: gameWorld.getEntityMap().keySet()) {
                    if (stagEntity.getName().equals(produced)) {
                        gameWorld.getEntityMap().put(stagEntity, this.curLocation.getName());
                        break;
                    }
                }
            }
        }
    }

    /* reset curPlayer
     * 8 drop all the item at curLocation,
     * reset curPlayer's health lever, and move to start point */
    private void resetCurPlayer() {
        //drop items
        for (Artefact artefact: gameWorld.getInventoryMap().keySet()) {
            if (gameWorld.getInventoryMap().get(artefact).equals(curPlayer.getName())) {
                gameWorld.getEntityMap().put(artefact, curLocation.getName());
                gameWorld.getInventoryMap().remove(artefact);
            }
        }
        // reset health lever, move to start point
        curPlayer.resetHealth();
        curLocation = gameWorld.getLocationList().get(0);
    }

}
