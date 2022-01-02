package assignemt4.ui;

import assignemt4.api.DirectedWeightedGraphAlgorithms;
import assignemt4.api.EdgeData;
import assignemt4.api.NodeData;
import assignemt4.ex4_java_client.Client;
import assignemt4.models.Agent;
import assignemt4.models.Pokemon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
                actionListener.actionEvent(new UIEvents.UpdateUi());
            }
            brain.clearTasks();
            client.stop();
        }).start();
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
        client.move();
        for (Agent agent : agents.values()) {
            if (agent.getDest() == -1) {
                System.out.println(agents);
                agent.setDest(brain.getNext(agent));
                client.chooseNextEdge("{\"agent_id\":" + agent.getId() + " , next_node_id :" + agent.getDest() + "}");
            }
        }
    }


    private double timeToGetToPokemon() {
        double min = Double.MAX_VALUE;

        for (Agent agent : agents.values()) {


        }

        return min;
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
