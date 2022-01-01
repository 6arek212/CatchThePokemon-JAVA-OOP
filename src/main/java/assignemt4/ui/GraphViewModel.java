package assignemt4.ui;

import assignemt4.api.DirectedWeightedGraphAlgorithms;
import assignemt4.api.EdgeData;
import assignemt4.api.GeoLocation;
import assignemt4.api.NodeData;
import assignemt4.ex4_java_client.Client;
import assignemt4.impl.AgentDs;
import assignemt4.impl.DirectedWeightedGraphImpl;
import assignemt4.models.Agent;
import assignemt4.models.GeoLocationImpl;
import assignemt4.models.NodeDataImpl;
import assignemt4.models.Pokemon;
import assignemt4.ui.utils.Range2Range;


import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Controller class that handles the view logic
 */
public class GraphViewModel {
    private final static int N_THREADS = 1;

    // UI lists
    private List<NodeData> nodes;
    private List<EdgeData> edges;
    private HashMap<Integer, Agent> agents;
    private List<Pokemon> pokemons;


    private DirectedWeightedGraphAlgorithms algo;
    private ActionListener actionListener;
    private ExecutorService pool;
    private Client client;
    private HashMap<Integer, AgentDs> agentsDs;

    public GraphViewModel(DirectedWeightedGraphAlgorithms graphAlg, ActionListener actionListener, Client client) {
        this.actionListener = actionListener;
        this.algo = graphAlg;
        this.client = client;
        pool = Executors.newFixedThreadPool(N_THREADS);
        init();
        agentsDs = new HashMap<>();
        for (Agent agent : agents.values()) {
            this.agentsDs.put(agent.getId(), new AgentDs(agent, algo));
        }


        client.start();
    }


    private void init() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();

        Iterator<EdgeData> it = this.algo.getGraph().edgeIter();
        while (it.hasNext()) {
            EdgeData ed = it.next();
            this.edges.add(ed);
        }
        Iterator<NodeData> it1 = this.algo.getGraph().nodeIter();
        while (it1.hasNext()) {
            this.nodes.add(it1.next());
        }


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


    public void spin() {
        allocatePokemons();
        updateAgents();
        moveAgents();
    }

    private void updateAgents() {
        HashMap<Integer, Agent> newAgents = Agent.load(client.getAgents());
        for (Agent agent : newAgents.values()) {
            this.agents.get(agent.getId()).setLocation(agent.getLocation());
        }
    }


    private void allocatePokemons() {

        for (Pokemon pok : this.pokemons) {
            int p = (int) (Math.random() * agents.size() - 1);
            //agentsDs[p].addTarget(pok);
        }

    }


    private void moveAgents() {
        for (Agent agent : agents.values()) {
            if (agent.getDest() == -1) {
                //agent.setDest(agentsDs.get(agent.getId()).getNext().getKey());
            }
        }
        this.client.move();
    }

    public DirectedWeightedGraphAlgorithms getAlgo() {
        return algo;
    }

    public NodeData getNode(int key) {
        return this.algo.getGraph().getNode(key);
    }

    public List<NodeData> getNodes() {
        return nodes;
    }

    public List<EdgeData> getEdges() {
        return edges;
    }

    public List<Pokemon> getPokemons() {
        return pokemons;
    }

    public HashMap<Integer, Agent> getAgents() {
        return agents;
    }
}
