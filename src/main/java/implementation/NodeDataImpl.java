package implementation;


import GameClient.utils.Point;
import GameClient.utils.Range2Range;
import api.GeoLocation;
import api.NodeData;
import json_impl.jsonDataNode;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class NodeDataImpl implements NodeData {
    private int key;
    private double weight;
    static final int WHITE = 1, GRAY = -1, BLACK = 0;
    private int tag;
    private GeoLocation GeoLoc;
    private String info;
    private Shape visualNode;


    private static final int r =5;


    public NodeDataImpl(int key, GeoLocation GeoLoc) {
        this.key = key;
        this.weight = 0;
        this.tag = WHITE;
        this.GeoLoc = GeoLoc;


        this.info = "";
    }
    public NodeDataImpl(GeoLocation GeoLoc , Range2Range WorldToFrame) {

        this.GeoLoc = GeoLoc;
        Point fp = WorldToFrame.worldToframe((Point) this.GeoLoc);
        this.visualNode = new Ellipse2D.Double((int) fp.x() - r, (int) fp.y() - r, 2 * r, 2 * r);


    }

    public NodeDataImpl( jsonDataNode nd ) {
        this.key = nd.getKey();
        this.tag = NodeDataImpl.WHITE;
        this.GeoLoc = new Point(nd.getLocationString());

        this.info = "";
    }
    public NodeDataImpl(NodeData node) {
        this.key = node.getKey();
        this.tag = node.getTag();
        this.info = node.getInfo();
    }

    public Shape getVisualNode(){
        return this.visualNode;

    }

    @Override
    public int getKey() {
        return this.key;
    }

    @Override
    public GeoLocation getLocation() {
        return this.GeoLoc;
    }

    @Override
    public void setLocation(GeoLocation p) {
        this.GeoLoc = p;
    }

    @Override
    public double getWeight() {
        return this.weight;
    }

    @Override
    public void setWeight(double w) {
        this.weight = w;
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


    public  String toString(){

        return "key: " +this.key;
    }
}