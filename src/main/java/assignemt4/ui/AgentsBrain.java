package assignemt4.ui;

import assignemt4.api.DirectedWeightedGraphAlgorithms;
import assignemt4.api.NodeData;
import assignemt4.ex4_java_client.Client;
import assignemt4.models.Agent;
import assignemt4.models.Pokemon;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class AgentsBrain {
    private static final double EPS = 0.001;

    private DirectedWeightedGraphAlgorithms algo;
    private List<Pokemon> pokemons;
    private Client client;
    private ActionListener actionListener;

    public AgentsBrain(DirectedWeightedGraphAlgorithms algo, List<Pokemon> pokemons, Client client, ActionListener actionListener) {
        this.algo = algo;
        this.pokemons = pokemons;
        this.client = client;
        this.actionListener = actionListener;
    }

    private void m(Pokemon pokemon, Agent agent, double dist) {

    }


    private void handlePokemon(Agent agent, Pokemon pokemon) {
        new Thread(() -> {
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
        }).start();
    }


    private void moveAfter(Agent agent, double w) {
        new Thread(() -> {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    client.move();
                    System.out.println(client.getAgents());
                    actionListener.actionEvent(new UIEvents.UpdateUi());
                    System.out.println("moved !");
                }
            }, (int) Math.ceil(w * 1000 + EPS));
        }).start();
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
        System.out.println(":Aaaaaa");

        List<NodeData> path;
        boolean flag = false;

        Pokemon pokemon = getClosestPockemon(agent);
        System.out.println(pokemon);

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
            handlePokemon(agent, pokemon);
        else
            moveAfter(agent, w);

        return path.get(1).getKey();
    }


    public synchronized void setPokemons(List<Pokemon> pokemons) {
        this.pokemons = pokemons;
    }
}
