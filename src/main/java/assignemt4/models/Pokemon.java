package assignemt4.models;

import assignemt4.api.DirectedWeightedGraph;
import assignemt4.api.EdgeData;
import assignemt4.api.GeoLocation;
import assignemt4.impl.DirectedWeightedGraphImpl;
import assignemt4.json_models.AgentJson;
import assignemt4.json_models.PokemonJson;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Pokemon {
    private double value;
    private int type;
    @SerializedName("pos")
    private GeoLocation location;
    private EdgeData edge;
    private boolean isAssigned;
    private boolean isCaptured;

    public Pokemon(PokemonJson.PokemonJsonInner pokemonJsonInner) {
        this.value = pokemonJsonInner.value;
        this.type = pokemonJsonInner.type;
        this.location = new GeoLocationImpl(pokemonJsonInner.locationString);
        this.edge = null;
        isAssigned = false;
        isCaptured = false;
    }

    public static List<Pokemon> load(String json) {
        try {
            PokemonJson dgj = new Gson().fromJson(json, PokemonJson.class);
            List<Pokemon> pokemons = new ArrayList<>();
            for (int i = 0; i < dgj.getPokemonJsonWrappers().size(); i++) {
                pokemons.add(new Pokemon(dgj.getPokemonJsonWrappers().get(i).getPokemon()));
            }
            return pokemons;
        } catch (Exception e) {
            return null;
        }

    }

    public Pokemon(double value, int type, GeoLocation location) {
        this.value = value;
        this.type = type;
        this.location = location;
    }

    public Boolean isOnTheLine(GeoLocation g1, GeoLocation g2) {
        double dist = g1.distance(g2);
        double a = (g1.y() - g2.y()) / (g1.x() - g2.x());
        double b = g1.y() - a * g1.x();

        double eps = 0.0001;
        double dist1 = g1.distance(location);
        double dist2 = g2.distance(location);

        return Math.abs(location.x() * a + b) <= location.y() + eps && Math.abs(dist1 + dist2) <= dist + eps;
    }


    public void setEdge(DirectedWeightedGraph graph) {
        Iterator<EdgeData> it = graph.edgeIter();
        while (it.hasNext()) {
            EdgeData ed = it.next();
            int src = ed.getSrc();
            int dest = ed.getDest();
            if (isOnTheLine(graph.getNode(src).getLocation(), graph.getNode(dest).getLocation())) {
                if (type > 0 && dest - src > 0) {
                    this.edge = ed;
                    return;
                } else if (type < 0 && dest - src <= 0) {
                    this.edge = ed;
                    return;
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pokemon pokemon = (Pokemon) o;
        return value == pokemon.value && type == pokemon.type && location.equals(pokemon.location) && Objects.equals(edge, pokemon.edge);
    }

    public boolean isCaptured() {
        return isCaptured;
    }

    public void setCaptured(boolean captured) {
        isCaptured = captured;
    }

    public boolean isAssigned() {
        return isAssigned;
    }

    public void setAssigned(boolean assigned) {
        isAssigned = assigned;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, type, location, edge);
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public GeoLocation getLocation() {
        return location;
    }

    public void setLocation(GeoLocation location) {
        this.location = location;
    }

    public EdgeData getEdge() {
        return edge;
    }

    @Override
    public String toString() {
        return "Pokemon{" +
                "value=" + value +
                ", type=" + type +
                ", location=" + location +
                ", edge=" + edge +
                '}';
    }
}
