package assignemt4.ui;

import assignemt4.api.DirectedWeightedGraphAlgorithms;
import assignemt4.api.EdgeData;
import assignemt4.api.NodeData;
import assignemt4.ex4_java_client.Client;
import assignemt4.models.Agent;
import assignemt4.models.Pokemon;


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
    private Game game;


    public GraphViewModel( ActionListener actionListener, Client client) {
        this.game = new Game(client,actionListener);
        init();
    }

    private void init() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();

        Iterator<EdgeData> it = this.game.getAlgo().getGraph().edgeIter();
        while (it.hasNext()) {
            EdgeData ed = it.next();
            this.edges.add(ed);
        }
        Iterator<NodeData> it1 = this.game.getAlgo().getGraph().nodeIter();
        while (it1.hasNext()) {
            this.nodes.add(it1.next());
        }
    }


    public DirectedWeightedGraphAlgorithms getAlgo() {
        return game.getAlgo();
    }

    public NodeData getNode(int key) {
        return this.game.getAlgo().getGraph().getNode(key);
    }

    public List<NodeData> getNodes() {
        return nodes;
    }

    public List<EdgeData> getEdges() {
        return edges;
    }

    public List<Pokemon> getPokemons() {
        return game.getPokemons();
    }

    public HashMap<Integer, Agent> getAgents() {
        return game.getAgents();
    }
}
