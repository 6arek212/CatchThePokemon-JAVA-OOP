package GameClient;


import GameClient.utils.Point;
import api.DirectedWeightedGraph;
import api.EdgeData;
import api.GeoLocation;
import api.NodeData;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.*;

public class Agent {

    private int id;
    private double value;
    private int src;
    private int dest;
    private double speed;
    private EdgeData currEdge;
    private NodeData currNode;
    private double time;
    private Pokemon currPokemon;
    //    private Queue<NodeData> queue;
    private GeoLocation pos;
    //if find pok then move
    private boolean isMoving;
    //the graph agent playing on
    private DirectedWeightedGraph g;
    //current agent shortest path to a pokemon
    //so we can do choose next for each agent
    private List<NodeData> agentCurrPath;
    //current pokemon that agent trying to catch
    private int currPok;


    public Agent(DirectedWeightedGraph g, int startNode) {
        this.g = g;
        this.value = 0;
        currNode = g.getNode(startNode);
        this.pos = currNode.getLocation();
        this.id = -1;
        this.speed = 0;
        this.agentCurrPath = new ArrayList<>();


    }

    public void setCurrPokemon(int currPok) {
        this.currPok = currPok;
    }

    public int getCurrPok() {
        return this.currPok;
    }

    public void addToPath(NodeData node) {
        agentCurrPath.add(node);

    }

    public void setAgentCurrPath(List<NodeData> path) {
        this.agentCurrPath = path;
    }


    public List<NodeData> getAgentCurrPath() {
        return this.agentCurrPath;
    }


    public int getSrc() {
        return this.src;
    }

    public int getDest() {
        return this.dest;
    }

    public boolean get_isMoving() {
        return this.isMoving;
    }

    public void set_isMoving(boolean isMoving) {

        this.isMoving = isMoving;
    }


    public double getSpeed() {
        return this.speed;
    }


    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getSrcNode() {
        return this.currNode.getKey();
    }

    public GeoLocation getPos() {
        return this.pos;
    }

    public int getNextNode() {
        return this.currEdge.getDest();

    }


    public int getId() {
        return this.id;
    }

    //if the current edge is between curr node and next node
    public boolean setNextNode(int dest) {
        int src = this.currNode.getKey();
        if (this.g.getEdge(src, dest) != null) {
            this.currNode = g.getNode(dest);
            return true;

        }
        return false;
    }

    public void setCurrNode(int src) {
        this.currNode = g.getNode(src);

    }

    public EdgeData getCurrEdge() {
        return this.currEdge;
    }

    public double getValue() {
        return this.value;
    }

    public boolean isMoving() {
        return this.currEdge != null;
    }

    //update agent according to the game
    public void update(String json) {
        JsonObject line;
        try {
            JsonParser parser = new JsonParser();
            line = (JsonObject) parser.parse(json);
            JsonObject _agent = line.getAsJsonObject("Agent");
            int id = _agent.get("id").getAsInt();
            if (id == this.getId() || this.getId() == -1) {
                if (this.id == -1) {
                    this.id = id;
                }
                double speed = _agent.get("speed").getAsDouble();
                String pos = _agent.get("pos").getAsString();
                GeoLocation pp = new Point(pos);
                int src = _agent.get("src").getAsInt();
                int dest = _agent.get("dest").getAsInt();
                double value = _agent.get("value").getAsDouble();
                this.pos = pp;
                this.setCurrNode(src);

                this.speed = speed;
                this.setNextNode(dest);
                this.value = value;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String toString() {


        return "Agent{" +
                "id=" + this.id +
                ",speed=" + this.speed +
                ",Pos=" + this.pos +
                ",Edge=" + this.currEdge +
                ",Node= " + this.currNode + '}';

    }

    // estimate time for agent to do the next move
    public void estimateTime() {

    }

    public double getTime() {
        return this.time;
    }

    public void setTime(double time) {
        this.time = time;
    }


}
