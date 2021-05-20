package gameParser;

import com.alexmerz.graphviz.*;
import com.alexmerz.graphviz.objects.*;
import gameParser.entity.*;
import gameParser.entity.Character;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;

public class GraphParser {
    private final ArrayList<Location> locationList;
    private final HashMap<StagEntity, String> entityMap;
    private final IdentityHashMap<String, String> pathMap;

    public GraphParser(String entityFilename) throws IOException {
        this.locationList = new ArrayList<>();
        this.entityMap = new HashMap<>();
        this.pathMap = new IdentityHashMap<>();
        FileReader reader = new FileReader(entityFilename);
        try {
            setDataFromDot(reader);
        } catch (ParseException e) {
            System.err.println(e);
        }
    }

    private void setDataFromDot(FileReader reader) throws ParseException{
        Parser parser = new Parser();
        parser.parse(reader);
        ArrayList<Graph> graphs = parser.getGraphs();
        ArrayList<Graph> subGraphs = graphs.get(0).getSubgraphs();
        // first subGraphs, contains location and paths
        for (Graph g: subGraphs) {
            // locations
            ArrayList<Graph> locations = g.getSubgraphs();
            for (Graph location: locations) {
                setLocationList(location);
                // all entities
                ArrayList<Graph> allEntities = location.getSubgraphs();
                for (Graph entities: allEntities) {
                    setEntityMap(entities, location);
                }
            }
            // paths
            ArrayList<Edge> paths = g.getEdges();
            for (Edge path: paths) {
                setPathMap(path);
            }
        }
    }

    /* Get locationName and description to init location.
     * Set entityMap. */
    private void setLocationList(Graph location) {
        Node nLoc = location.getNodes(false).get(0);
        String locationName = nLoc.getId().getId();
        String description = nLoc.getAttribute("description");
        Location newLocation = new Location(locationName, description);
        locationList.add(newLocation);
    }

    /* Get entitiesType to identify different entities.
     * Get locationName to set entities ownership.
     * Get itemId and description to init entities
     * Set entityMap. */
    private void setEntityMap(Graph entities, Graph location) {
        String entitiesType = entities.getId().getId();
        String locationName = location.getNodes(false).get(0).getId().getId();
        ArrayList<Node> nodesEnt = entities.getNodes(false);
        for (Node nEnt : nodesEnt) {
            String itemId = nEnt.getId().getId();
            String description = nEnt.getAttribute("description");
            switch (entitiesType) {
                case "artefacts":
                    Artefact artefact = new Artefact(itemId, description);
                    entityMap.put(artefact, locationName);
                    break;
                case "furniture":
                    Furniture furniture = new Furniture(itemId, description);
                    entityMap.put(furniture, locationName);
                    break;
                case "characters":
                    Character character = new Character(itemId, description);
                    entityMap.put(character, locationName);
                    break;
            }
        }
    }

    /* Get start location name and end location name
     * Set pathMap. */
    private void setPathMap(Edge path) {
        String startLocationName = path.getSource().getNode().getId().getId();
        String endLocationName = path.getTarget().getNode().getId().getId();
        pathMap.put(startLocationName, endLocationName);
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

}
