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


    private void handlePokemon(Agent agent, Pokemon pokemon) {
        new Thread(
                () -> {
                    NodeData nextNode = this.algo.getGraph().getNode(pokemon.getEdge().getDest());
                    double dist = agent.getLocation().distance(nextNode.getLocation());
                    List<Pokemon> ps = pokemons.stream().filter((Pokemon p) -> p.equals(pokemon)).collect(Collectors.toList());

                    while (!ps.isEmpty()) {
                        double time = pokemon.getEdge().getWeight() * agent.getLocation().distance(pokemon.getLocation()) / dist;
                        try {
                            Thread.sleep((int) (time * 1000 + EPS));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        client.move();
                        ps = pokemons.stream().filter((Pokemon p) -> p.equals(pokemon)).collect(Collectors.toList());
                    }

                    double time = pokemon.getEdge().getWeight() * agent.getLocation().distance(nextNode.getLocation()) / dist;

                    try {
                        Thread.sleep((int) (time * 1000 + EPS));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    client.move();
                    System.out.println("Eat that Pokimon " + pokemons + "  " + pokemon);
                }
        ).start();
    }


    private void moveAfter(Agent agent, double w) {
        new Thread(
                ()->{
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            System.out.println(agent);
                            client.move();
                            actionListener.actionEvent(new UIEvents.UpdateUi());
                            System.out.println("moved !");
                        }
                    }, (long) (w * 1000 + EPS));
                }
        ).start();
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


    public int getNext(Agent agent) {
        if (pokemons.isEmpty())
            return -1;

        List<NodeData> path;
        boolean flag = false;

        Pokemon pokemon = getClosestPockemon(agent);
        agent.setCurrentPok(pokemon);

        if (agent.getSrc() != pokemon.getEdge().getSrc())
            path = algo.shortestPath(agent.getSrc(), pokemon.getEdge().getSrc());
        else {
            path = algo.shortestPath(agent.getSrc(), pokemon.getEdge().getDest());
            flag = true;
        }

        if (path.isEmpty() || path.size() == 1)
            return -1;

        double w = algo.getGraph().getEdge(agent.getSrc(), path.get(1).getKey()).getWeight();

        if (flag)
            moveAfter(agent, w);
        else
            handlePokemon(agent,pokemon);

        return path.get(1).getKey();
    }


    public void clearTasks() {
        this.executor.shutdownNow();
    }

    public synchronized void setPokemons(List<Pokemon> pokemons) {
        this.pokemons = pokemons;
    }
}
