package GameClient;

import GameClient.Pokemon;
import GameClient.utils.Point;
import api.DirectedWeightedGraph;
import api.EdgeData;
import api.GeoLocation;
import api.NodeData;
import org.json.JSONObject;
//import java.awt.*;
import java.awt.font.ImageGraphicAttribute;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represent an Agent on the Game
 */


public class Agent {
    private int id;

    private double speed;
    private DirectedWeightedGraph g;
    private double value;
    //if find pok then move should always be false
    private boolean isMoving;
    //position of the agent
    private GeoLocation pos;
    private LinkedList<NodeData> agentCurrPath;
    //the current Pokémon agent is going to
    private int currPok;
    private EdgeData currEdge;
    private NodeData currNode;

    public void setCurrPokemon(int currPok) {
        this.currPok = currPok;
    }

    public int getCurrPok() {
        return this.currPok;
    }

    public void addToPath(NodeData node) {
        agentCurrPath.add(node);

    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setAgentCurrPath(LinkedList<NodeData> path) {
        this.agentCurrPath = path;
    }


    public LinkedList<NodeData> getAgentCurrPath() {
        return this.agentCurrPath;
    }


    public Agent(DirectedWeightedGraph g, int start_node) {
        this.g = g;
        value = 0;
        currNode = g.getNode(start_node);
        pos = currNode.getLocation();
        id = -1;
        speed = 0;
    }


    public boolean isMoving() {

        return isMoving;
    }

    public void setCurrNode(int src) {
        this.currNode = g.getNode(src);
    }

    public NodeData getCurrNode() {
        return this.currNode;
    }

    public int getId() {
        return this.id;
    }

    public GeoLocation getPos() {
        return pos;
    }

    public EdgeData getCurrEdge() {
        return this.currEdge;
    }

    public double getSpeed() {
        return this.speed;
    }

    public void setIsMoving(boolean isMoving) {
        this.isMoving = isMoving;
    }

    public int getSrc() {
        return this.currNode.getKey();
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return this.value;
    }

    public void setPos(GeoLocation pos) {
        this.pos = pos;
    }

    public boolean setNextNode(int dest) {
        int src = this.currNode.getKey();
        this.currEdge = g.getEdge(src, dest);
        if (currEdge != null) {
            return true;
        }

        currEdge = null;
        return false;
    }


    public void updateAgent(String json) {
        JSONObject strObject;
        try {
            strObject = new JSONObject(json);
            JSONObject AgentObject = strObject.getJSONObject("Agent");
            int id = AgentObject.getInt("id");
            if (id == this.getId() || this.getId() == -1) {
                if (this.getId() == -1) {
                    this.id = id;
                }
                this.setSpeed(AgentObject.getDouble("speed"));
                String p = AgentObject.getString("pos");
                Point point = new Point(p);
                this.setPos(point);
                this.setCurrNode(AgentObject.getInt("src"));
                this.setNextNode(AgentObject.getInt("dest"));
                this.setValue(AgentObject.getDouble("value"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String toString() {

        return "Agent{" +
                "id=" + this.id +
                ",value=" + this.value +
                ",pos=" + this.pos +
                ",currPok=" + this.currPok +
                '}';

    }

}
