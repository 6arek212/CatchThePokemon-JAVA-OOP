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
    private DirectedWeightedGraphAlgorithms algo;
    private Game game;


    public GraphViewModel(DirectedWeightedGraphAlgorithms graphAlg, ActionListener actionListener, Client client) {
        this.algo = graphAlg;
        init();
        this.game = new Game(client,algo,actionListener);
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
        return game.getPokemons();
    }

    public HashMap<Integer, Agent> getAgents() {
        return game.getAgents();
    }
}
