package assignemt4.ui;

import assignemt4.api.DirectedWeightedGraphAlgorithms;
import assignemt4.api.NodeData;
import assignemt4.ex4_java_client.Client;
import assignemt4.models.Agent;
import assignemt4.models.Pokemon;

import java.awt.print.PageFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

public class AgentsBrain {
    private static final double EPS = 0.001;
    private final static int N_THREADS = 1;

    private DirectedWeightedGraphAlgorithms algo;
    private List<Pokemon> pokemons;
    private Client client;
    private ActionListener actionListener;
    private ExecutorService executor;

    public AgentsBrain(DirectedWeightedGraphAlgorithms algo, List<Pokemon> pokemons, Client client, ActionListener actionListener) {
        this.algo = algo;
        this.pokemons = pokemons;
        this.client = client;
        this.executor = Executors.newFixedThreadPool(N_THREADS);
        this.actionListener = actionListener;
    }


    private Pokemon getClosestPockemon(Agent agent) {
        double min = Double.MAX_VALUE;
        Pokemon picked = null;
        double maxValue = Double.MIN_VALUE;
        for (Pokemon pokemon : pokemons) {
            double dist = algo.shortestPathDist(agent.getSrc(), pokemon.getEdge().getSrc()) + pokemon.getEdge().getWeight();
            if (dist < min && maxValue < pokemon.getValue()) {
                maxValue = pokemon.getValue();
                min = dist;
                picked = pokemon;
            }
        }
        return picked;
    }


    public void setNextDest(Agent agent) {
        if (pokemons.isEmpty())
            return;
        if (agent.getCurrentPok() != null && !agent.getCurrentPath().isEmpty() && agent.getSrc() == agent.getCurrentPath().get(0).getKey()) {
            agent.getCurrentPath().remove(0);
        }

        if (agent.getCurrentPok() != null && agent.getSrc() == agent.getCurrentPok().getEdge().getDest()) {
            agent.setCurrentPath(null);
            agent.setCurrentPok(null);
        }

        if (agent.getCurrentPok() == null) {
            Pokemon pokemon = getClosestPockemon(agent);
            agent.setCurrentPok(pokemon);
            List<NodeData> path = algo.shortestPath(agent.getSrc(), pokemon.getEdge().getSrc());
            path.add(algo.getGraph().getNode(pokemon.getEdge().getDest()));
            path.remove(0);
            agent.setCurrentPath(path);
            pokemon.setAssigned(true);
        }
    }


    public void clearTasks() {
        this.executor.shutdownNow();
    }

    public void setPokemons(List<Pokemon> pokemons) {
        this.pokemons = pokemons;
    }
}
