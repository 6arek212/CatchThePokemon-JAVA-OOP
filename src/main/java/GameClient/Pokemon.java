package GameClient;


import GameClient.utils.Point;
import api.EdgeData;
import api.GeoLocation;

import java.util.Objects;

public class Pokemon {


    private double value;
    private int type;
    private Point pos;
    //curr edge the pokemon on it
    private EdgeData edge;

    // if the pok been catched by an agent
    private boolean isCatched;


    public Pokemon(Point pos, int type, double value, double s, EdgeData e) {
        this.pos = pos;
        this.type = type;
        this.value = value;
        this.edge = e;

    }

    public boolean isCatched() {

        return this.isCatched;
    }

    public void setCatched(boolean isCatched) {

        this.isCatched = isCatched;
    }

    public int getType() {

        return this.type;
    }


    public double getValue() {
        return this.value;
    }

    public Point getPos() {
        return this.pos;
    }


    public void setEdge(EdgeData edge) {
        this.edge = edge;
    }

    public EdgeData getEdge() {
        return this.edge;
    }

    public String toString() {

        return "Pokemon{" +
                "edge=" + edge +
                ",value=" + value +
                ",type=" + type +
                ",pos=" + pos +
                '}';

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pokemon pokemon = (Pokemon) o;
        return type == pokemon.type && Objects.equals(pos, pokemon.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, pos);
    }
}
