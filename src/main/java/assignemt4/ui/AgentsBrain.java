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

/**
 *  This class represent the agent thinking & navigating through the graph
 */
public class AgentsBrain {
    private DirectedWeightedGraphAlgorithms algo;
    private List<Pokemon> pokemons;

    public AgentsBrain(DirectedWeightedGraphAlgorithms algo, List<Pokemon> pokemons) {
        this.algo = algo;
        this.pokemons = pokemons;
    }


    /**
     *
     * @param agent agent
     * @return the closest pokemon to the given agent
     */
    private Pokemon getClosestPockemon(Agent agent) {
        double min = Double.MAX_VALUE;
        Pokemon picked = null;
        for (Pokemon pokemon : pokemons) {
            if (!pokemon.isAssigned()) {
                double dist = algo.shortestPathDist(agent.getSrc(), pokemon.getEdge().getSrc()) + pokemon.getEdge().getWeight();
                if (dist < min) {
                    min = dist;
                    picked = pokemon;
                }
            }
        }
        return picked;
    }

    /**
     * allocate a pokemon to an agent , every agent will get different pokemon
     * @param agent agent
     */
    public void allocate(Agent agent) {
        Pokemon pokemon = getClosestPockemon(agent);
        if (pokemon != null) {
            if (agent.getSrc() == pokemon.getEdge().getSrc()) {
                agent.setDest(pokemon.getEdge().getDest());
            }else{
                List<NodeData> path = algo.shortestPath(agent.getSrc(), pokemon.getEdge().getSrc());
                path.remove(0);
                pokemon.setAssigned(true);
                if (!path.isEmpty()) {
                    agent.setDest(path.get(0).getKey());
                }
            }
            agent.setCurrentPok(pokemon);
        }
    }



    public void setPokemons(List<Pokemon> pokemons) {
        this.pokemons = pokemons;
    }
}
