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
    private DirectedWeightedGraphAlgorithms algo;
    private List<Pokemon> pokemons;

    public AgentsBrain(DirectedWeightedGraphAlgorithms algo, List<Pokemon> pokemons, Client client, ActionListener actionListener) {
        this.algo = algo;
        this.pokemons = pokemons;
    }


    private Pokemon getClosestPockemon(Agent agent) {
        double min = Double.MAX_VALUE;
        Pokemon picked = null;
        for (Pokemon pokemon : pokemons) {
            double dist = algo.shortestPathDist(agent.getSrc(), pokemon.getEdge().getSrc()) + pokemon.getEdge().getWeight();
            if (dist < min) {
                min = dist;
                picked = pokemon;
            }
        }
        return picked;
    }


//    private Pokemon getClosestPockemon(Agent agent) {
//        if (agent.getPokemons().isEmpty())
//            return null;
//
//
//        double min = Double.MAX_VALUE;
//        Pokemon picked = null;
//
//        for (Pokemon pokemon : agent.getPokemons()) {
//            double dist = algo.shortestPathDist(agent.getSrc(), pokemon.getEdge().getSrc()) + pokemon.getEdge().getWeight();
//            //&& maxValue < pokemon.getValue()
//            if (dist < min) {
//                min = dist;
//                picked = pokemon;
//            }
//        }
//
//        return picked;
//    }


    public void setNextDest(Agent agent) {
        if (pokemons.isEmpty())
            return;
        Pokemon pokemon = getClosestPockemon(agent);
        if (pokemon != null) {
            agent.setCurrentPok(pokemon);
            List<NodeData> path = algo.shortestPath(agent.getSrc(), pokemon.getEdge().getSrc());
            path.add(algo.getGraph().getNode(pokemon.getEdge().getDest()));
            path.remove(0);
            agent.setCurrentPath(path);
            pokemon.setAssigned(true);
            agent.setDest(path.get(0).getKey());
        } else {
            System.out.println("nullllllllllllllllllllllllllllllllllllll");

        }


//        if (agent.getCurrentPok() != null && !agent.getCurrentPath().isEmpty() && agent.getSrc() == agent.getCurrentPath().get(0).getKey()) {
//            agent.getCurrentPath().remove(0);
//            if (!agent.getCurrentPath().isEmpty())
//                agent.setDest(agent.getCurrentPath().get(0).getKey());
//        }
//
//        if (agent.getCurrentPok() != null && agent.getSrc() == agent.getCurrentPok().getEdge().getDest()) {
//            agent.setCurrentPath(null);
//            agent.setCurrentPok(null);
//        }
//
//        if (agent.getCurrentPok() == null) {
//            Pokemon pokemon = getClosestPockemon(agent);
//            if (pokemon != null) {
//                agent.setCurrentPok(pokemon);
//                List<NodeData> path = algo.shortestPath(agent.getSrc(), pokemon.getEdge().getSrc());
//                path.add(algo.getGraph().getNode(pokemon.getEdge().getDest()));
//                path.remove(0);
//                agent.setCurrentPath(path);
//                pokemon.setAssigned(true);
//                agent.setDest(path.get(0).getKey());
//                agent.getCurrentPok().setCaptured(false);
//
//            }
//            System.out.println("aaaaaaaaaaaaaaaaaa"+ pokemon);
//        }


    }


    public void setPokemons(List<Pokemon> pokemons) {
        this.pokemons = pokemons;
    }
}
