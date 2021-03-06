package implementation;

import api.EdgeData;
import json_impl.jsonEdgeData;

import java.util.Objects;


public class EdgeDataImpl implements EdgeData {

    private int src;
    private int dest;
    private double weight;
    private String info;
    private int tag;

    public EdgeDataImpl(jsonEdgeData ed) {
        this.src = ed.getSrc();
        this.dest = ed.getDest();
        this.weight = ed.getWeight();
        this.info = null;
    }




    public EdgeDataImpl(int src, int dest, double weight) {
        this.src = src;
        this.dest = dest;
        this.weight = weight;
        this.info = null;
    }

    public EdgeDataImpl(EdgeData e) {
        this.src = e.getSrc();
        this.dest = e.getDest();
        this.weight = e.getWeight();
        this.info = e.getInfo();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdgeDataImpl edgeData = (EdgeDataImpl) o;
        return src == edgeData.src && dest == edgeData.dest && Double.compare(edgeData.weight, weight) == 0 && tag == edgeData.tag && Objects.equals(info, edgeData.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(src, dest, weight, info, tag);
    }

    @Override
    public int getSrc() {
        return this.src;
    }

    @Override
    public int getDest() {
        return this.dest;
    }

    @Override
    public double getWeight() {
        return this.weight;
    }

    @Override
    public String getInfo() {
        return this.info;
    }

    @Override
    public void setInfo(String s) {
        this.info = s;
    }

    @Override
    public int getTag() {
        return this.tag;
    }

    @Override
    public void setTag(int t) {
        this.tag = t;
    }

    @Override
    public String toString() {
        return "EdgeDataImpl{" +
                "src=" + src +
                ", dest=" + dest +
                ", weight=" + weight +
                ", info='" + info + '\'' +
                ", tag=" + tag +
                '}';
    }
}

