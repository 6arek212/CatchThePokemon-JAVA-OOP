package assignemt4.ui;

import assignemt4.api.DirectedWeightedGraphAlgorithms;
import assignemt4.api.EdgeData;
import assignemt4.api.NodeData;
import assignemt4.ex4_java_client.Client;
import assignemt4.models.Agent;
import assignemt4.models.Pokemon;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Game {
    private Client client;
    private HashMap<Integer, Agent> agents;
    private List<Pokemon> pokemons;
    private DirectedWeightedGraphAlgorithms algo;
    private AgentsBrain brain;
    private ActionListener actionListener;

    public Game(Client client, DirectedWeightedGraphAlgorithms algo, ActionListener actionListener) {
        this.client = client;
        this.algo = algo;
        this.actionListener = actionListener;
        init();
        this.brain = new AgentsBrain(algo, this.pokemons, client, actionListener);
        startGame();
    }

    private void startGame() {
        new Thread(() -> {
            client.start();
            while (client.isRunning().equals("true")) {
                updateAgents();
                updatePokemons();
                moveAgents();
                double min = getMin();

                int sleepTime ;
                double x = isOnPokEdgePokemon();

                if (isOnPokEdgePokemon() != Double.MAX_VALUE){
                    sleepTime = (int ) Math.ceil(x *1000 + 0.001);
                }else{
                    sleepTime = (int ) Math.ceil(getMin() *1000 + 0.001);
                }


                System.out.println(sleepTime);

                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                client.move();
                actionListener.actionEvent(new UIEvents.UpdateUi());
            }
            brain.clearTasks();
            client.stop();
        }).start();
    }

    private double getMin() {
        double min = Double.MAX_VALUE;
        for (Agent agent : agents.values()) {
            double w = algo.getGraph().getEdge(agent.getSrc() , agent.getCurrentPath().get(0).getKey()).getWeight();
            if (agent.getCurrentPath() != null && w < min) {
                min = w;
            }
        }
        return min;
    }


    private double isOnPokEdgePokemon() {
        for (Agent agent : agents.values()) {
            if (agent.getCurrentPok() != null && agent.getSrc() == agent.getCurrentPok().getEdge().getSrc()) {
                NodeData nextNode = this.algo.getGraph().getNode(agent.getCurrentPok().getEdge().getDest());
                double dist = agent.getLocation().distance(nextNode.getLocation());
                double time = agent.getCurrentPok().getEdge().getWeight() * agent.getLocation().distance(agent.getCurrentPok().getLocation()) / dist;
                return time;
            }
        }
        return Double.MAX_VALUE;
    }


    private boolean isNearPokemon() {
        for (Agent agent : agents.values()) {
            if (agent.getCurrentPok() != null && agent.getLocation().distance(agent.getCurrentPok().getLocation()) <= 0.001) {
                return true;
            }
        }
        return false;
    }


    private void init() {
        client.addAgent("{\"id\":0}");
        this.agents = Agent.load(client.getAgents());
        System.out.println(this.agents.values());

        this.pokemons = Pokemon.load(client.getPokemons());
        for (Pokemon p : pokemons) {
            p.setEdge(this.algo.getGraph());
        }

        System.out.println(this.pokemons);
        String isRunningStr = client.isRunning();
        System.out.println(isRunningStr);
    }


    private void moveAgents() {
        for (Agent agent : agents.values()) {
            if (agent.getDest() == -1) {
                brain.setNextDest(agent);
                System.out.println(agent.getCurrentPath().get(0).getKey());
                client.chooseNextEdge("{\"agent_id\":" + agent.getId() + " , next_node_id :" + agent.getCurrentPath().get(0).getKey() + "}");
            }
        }
    }


    private void updateAgents() {
        HashMap<Integer, Agent> newAgents = Agent.load(client.getAgents());
        for (Agent agent : newAgents.values()) {
            this.agents.get(agent.getId()).setLocation(agent.getLocation());
            this.agents.get(agent.getId()).setDest(agent.getDest());
            this.agents.get(agent.getId()).setSrc(agent.getSrc());
        }
    }

    private void updatePokemons() {
        List<Pokemon> ps = Pokemon.load(client.getPokemons());
        if (ps == null)
            return;
        this.pokemons = ps;
        for (Pokemon p : pokemons) {
            p.setEdge(this.algo.getGraph());
        }
        this.brain.setPokemons(pokemons);
    }

    public List<Pokemon> getPokemons() {
        return pokemons;
    }

    public HashMap<Integer, Agent> getAgents() {
        return agents;
    }
}
