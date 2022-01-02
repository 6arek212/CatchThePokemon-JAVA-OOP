package assignemt4.models;

import assignemt4.api.GeoLocation;
import assignemt4.api.NodeData;
import assignemt4.impl.DirectedWeightedGraphImpl;
import assignemt4.json_models.AgentJson;
import assignemt4.json_models.DirectedWeightedGraphJson;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Agent {
    private int id;
    private double value;
    private int src;
    private int dest;
    private int speed;
    private GeoLocation location;
    private Pokemon currentPok;
    private List<NodeData> currentPath;

    public Agent(AgentJson.AgentJsonInner agentJson) {
        this.id = agentJson.id;
        this.value = agentJson.value;
        this.src = agentJson.src;
        this.dest = agentJson.dest;
        this.speed = agentJson.speed;
        this.location = new GeoLocationImpl(agentJson.locationStr);
    }

    public Agent(int id, double value, int src, int dest, int speed, GeoLocation location) {
        this.id = id;
        this.value = value;
        this.src = src;
        this.dest = dest;
        this.speed = speed;
        this.location = location;
    }


    public static HashMap<Integer, Agent> load(String json) {
        AgentJson dgj = new Gson().fromJson(json, AgentJson.class);
        HashMap<Integer, Agent> agents = new HashMap<>();
        for (int i = 0; i < dgj.getAgents().size(); i++) {
            Agent agent = new Agent(dgj.getAgents().get(i).getAgent());
            agents.put(agent.getId(), agent);
        }
        return agents;
    }

    public void setCurrentPath(List<NodeData> currentPath) {
        this.currentPath = currentPath;
    }

    public List<NodeData> getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPok(Pokemon currentPok) {
        this.currentPok = currentPok;
    }

    public Pokemon getCurrentPok() {
        return currentPok;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getSrc() {
        return src;
    }

    public void setSrc(int src) {
        this.src = src;
    }

    public int getDest() {
        return dest;
    }

    public void setDest(int dest) {
        this.dest = dest;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public GeoLocation getLocation() {
        return location;
    }

    public void setLocation(GeoLocation location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Agent{" +
                "id=" + id +
                ", value=" + value +
                ", src=" + src +
                ", dest=" + dest +
                ", speed=" + speed +
                ", location=" + location +
                '}';
    }
}
