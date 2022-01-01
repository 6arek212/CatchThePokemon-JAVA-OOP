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

public class Pokemon {
    private double value;
    private int type;
    @SerializedName("pos")
    private GeoLocation location;
    private EdgeData edge;

    public Pokemon(PokemonJson.PokemonJsonInner pokemonJsonInner) {
        this.value = pokemonJsonInner.value;
        this.type = pokemonJsonInner.type;
        this.location = new GeoLocationImpl(pokemonJsonInner.locationString);
        this.edge = null;
    }

    public static List<Pokemon> load(String json) {
        PokemonJson dgj = new Gson().fromJson(json, PokemonJson.class);
        List<Pokemon> pokemons = new ArrayList<>();
        for (int i = 0; i < dgj.getPokemonJsonWrappers().size(); i++) {
            pokemons.add(new Pokemon(dgj.getPokemonJsonWrappers().get(i).getPokemon()));
        }
        return pokemons;
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

        double eps = 0.001;
        double dist1 = g1.distance(location);
        double dist2 = g2.distance(location);

        return Math.abs(location.x() * a + b) <= location.y() + eps && Math.abs(dist1 + dist2) <= dist + eps;
    }

    public void setEdge(DirectedWeightedGraph graph) {
        Iterator<EdgeData> it = graph.edgeIter();
        while (it.hasNext()) {
            EdgeData ed = it.next();
            if (isOnTheLine(graph.getNode(ed.getSrc()).getLocation(), graph.getNode(ed.getDest()).getLocation())) {
                this.edge = ed;
                return;
            }
        }
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
