package assignemt4.impl;

import assignemt4.api.DirectedWeightedGraphAlgorithms;
import assignemt4.api.NodeData;
import assignemt4.models.Agent;
import assignemt4.models.Pokemon;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AgentDs {
    private List<PokemonDs> targets;
    private Agent agent;
    private NodeData current;
    private DirectedWeightedGraphAlgorithms algo;
    private Comparator<PokemonDs> comparator;

    public AgentDs(Agent agent, DirectedWeightedGraphAlgorithms algo) {
        this.algo = algo;
        this.targets = new ArrayList<>();
        this.agent = agent;
        this.current = null;
        this.comparator = (PokemonDs t1, PokemonDs t2) -> {
            if (t1.dist < t2.dist)
                return 1;
            else if (t1.dist > t2.dist)
                return -1;
            return 0;
        };
    }

    public void addTarget(PokemonDs pokemon) {
        this.targets.add(pokemon);
        this.targets.sort(this.comparator);
    }

    public NodeData getNext() {
        if (targets.isEmpty())
            return null;
        if (agent.getSrc() != -1)
            return this.current;

        List<NodeData> path = this.algo.shortestPath(this.agent.getSrc(), this.targets.get(0).getPokemon().getEdge().getSrc());
        if (path.size() == 1) {
            return null;
        }

        this.current = path.get(1);
        return this.current;
    }


    class PokemonDs {
        private Pokemon pokemon;
        private int dist;

        public PokemonDs(Pokemon pokemon, int dist) {
            this.pokemon = pokemon;
            this.dist = dist;
        }

        public Pokemon getPokemon() {
            return pokemon;
        }

        public void setPokemon(Pokemon pokemon) {
            this.pokemon = pokemon;
        }

        public int getDist() {
            return dist;
        }

        public void setDist(int dist) {
            this.dist = dist;
        }
    }
}
