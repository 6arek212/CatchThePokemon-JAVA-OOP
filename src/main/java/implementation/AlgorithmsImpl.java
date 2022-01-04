package implementation;

import java.awt.Point;

import GameClient.utils.Range2Range;
import json_impl.fromJsonToGraph;
import api.DirectedWeightedGraph;
import api.DirectedWeightedGraphAlgorithms;
import api.EdgeData;
import api.NodeData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.nio.file.Paths;
import java.sql.Array;
import java.util.*;
import java.io.*;
import java.util.concurrent.ArrayBlockingQueue;


public class AlgorithmsImpl implements DirectedWeightedGraphAlgorithms {
    private DirectedWeightedGraph graph;

    public AlgorithmsImpl() {
        this.graph = new DirectedWeightedGraphImpl();
    }

    public AlgorithmsImpl(DirectedWeightedGraph graph) {
        this.graph = new DirectedWeightedGraphImpl(graph);
    }

    public AlgorithmsImpl(String file) {
        if (!load(Paths.get("").toAbsolutePath() + "/src/main/java/assignment2/data/" + file))
            throw new RuntimeException("Error something went wrong while loading json file");
    }


    @Override
    public void init(DirectedWeightedGraph g) {
        this.graph = new DirectedWeightedGraphImpl(g);
    }

    @Override
    public DirectedWeightedGraph getGraph() {
        return this.graph;
    }

    @Override
    public DirectedWeightedGraph copy() {
        return new DirectedWeightedGraphImpl(this.graph);
    }


    /**
     * get the transpose of the graph
     * complexity  O(V+E)
     */
    private DirectedWeightedGraph reverse(DirectedWeightedGraph graph) {
        DirectedWeightedGraph reverseGraph = new DirectedWeightedGraphImpl();
        Iterator<EdgeData> it = graph.edgeIter();
        while (it.hasNext()) {
            EdgeData edge = it.next();
            reverseGraph.addNode(graph.getNode(edge.getSrc()));
            reverseGraph.addNode(graph.getNode(edge.getDest()));
            reverseGraph.connect(edge.getDest(), edge.getSrc(), edge.getWeight());
        }

        Iterator<NodeData> iterator = graph.nodeIter();
        while (iterator.hasNext()) {
            NodeData nodeData = iterator.next();
            reverseGraph.addNode(new NodeDataImpl(nodeData));
        }
        return reverseGraph;
    }


    /**
     * DFS scan
     *
     * @param g         directed graph
     * @param node      the starting node
     * @param nodesList to store the path
     */
    private void dfsVisit(DirectedWeightedGraph g, NodeData node, List<NodeData> nodesList) {
        node.setTag(NodeDataImpl.GRAY);
        g.edgeIter(node.getKey()).forEachRemaining((EdgeData ed) -> {
            NodeData nd = g.getNode(ed.getDest());
            if (nd.getTag() == NodeDataImpl.WHITE) {
                dfsVisit(g, nd, nodesList);
            }
        });
        nodesList.add(0, node);
        node.setTag(NodeDataImpl.BLACK);
    }

    private List<NodeData> dfs(DirectedWeightedGraph g, List<NodeData> nodesList, boolean reversed) {
        for (NodeData d : nodesList) {
            d.setTag(NodeDataImpl.WHITE);
        }

        List<NodeData> p = new ArrayList<>();
        for (NodeData d : nodesList) {
            if (d.getTag() == NodeDataImpl.WHITE)
                dfsVisit(g, d, p);
            if (reversed)
                return p;
        }
        return p;
    }


    /**
     * check if there is only one strongly connected component
     * Algorithm: two DFS call's after the first one invert the edges
     * complexity : O(V+E)
     *
     * @return is the graph connected True , False
     */
    @Override
    public boolean isConnected() {
        DirectedWeightedGraph graph = copy();
        Iterator<NodeData> iterator = graph.nodeIter();
        List<NodeData> nodes = new ArrayList<>();
        while (iterator.hasNext()) {
            nodes.add(iterator.next());
        }
        return dfs(reverse(graph), dfs(graph, nodes, false), true).size() == graph.nodeSize();
    }



    /**
     * Dijkstra algorithm O(|V|^2)
     *
     * @param src  - start node
     * @param dest - end (target) node
     * @return path for the shortest path between src and dest
     */

        @Override
    public List<NodeData> shortestPath(int src, int dest) {
        HashMap<Integer, NodeData> dist = new HashMap<Integer, NodeData>();
        for (Iterator<NodeData> it = graph.nodeIter(); it.hasNext(); ) {
            NodeData nodeData = it.next();
            nodeData.setWeight(Integer.MAX_VALUE);
        }
        HashMap<Integer, LinkedList<NodeData>> shortestPathMap = new HashMap<>();
        for (Iterator<NodeData> it = graph.nodeIter(); it.hasNext(); ) {
            NodeData nodeData = it.next();
            shortestPathMap.put(nodeData.getKey(), new LinkedList<>());
        }
        NodeData start = graph.getNode(src);
        start.setWeight(0);
        dist.put(src, start);
        while (!dist.isEmpty()) {
            NodeData currentNode = this.getMin(dist);
            if (currentNode.getKey() == dest) {
                break;
            }
            int curr = currentNode.getKey();
            double currWeight= currentNode.getWeight();
            dist.remove(curr);
            LinkedList<NodeData> currentNodePath;
            currentNodePath = shortestPathMap.get(curr);
            for (Iterator<EdgeData> it = (graph.edgeIter(curr)); it.hasNext(); ) {
                EdgeData n = it.next();
                int neighbor = n.getDest();
                double distanceToNeighbor = graph.getEdge(curr, neighbor).getWeight() + currWeight;
                NodeData neighborNode = graph.getNode(neighbor);
                if (distanceToNeighbor < neighborNode.getWeight()) {
                    LinkedList<NodeData> currentNeighborPath = new LinkedList<>(currentNodePath);
                    currentNeighborPath.add(neighborNode);
                    shortestPathMap.put(neighborNode.getKey(), currentNeighborPath);
                    neighborNode.setWeight(distanceToNeighbor);
                    dist.put(neighborNode.getKey(), neighborNode);
                }
            }
        }

        return shortestPathMap.get(dest);
    }

    @Override
    public double shortestPathDist(int src, int dest) {
        HashMap<Integer, NodeData> dist = new HashMap<Integer, NodeData>();
        for (Iterator<NodeData> it = graph.nodeIter(); it.hasNext(); ) {
            NodeData nodeData = it.next();
            nodeData.setWeight(Integer.MAX_VALUE);
        }
        NodeData start = graph.getNode(src);
        start.setWeight(0);
        dist.put(src, start);
        while (!dist.isEmpty()) {
            NodeData currentNode = this.getMin(dist);
            if (currentNode.getKey() == dest) {
                break;
            }
            int curr = currentNode.getKey();
            double currWeight = currentNode.getWeight();
            dist.remove(curr);
            for (Iterator<EdgeData> it = graph.edgeIter(curr); it.hasNext(); ) {
                EdgeData e = it.next();
                Integer neighbor=e.getDest();
                double distanceToNeighbor = graph.getEdge(curr, neighbor).getWeight() + currWeight;
                NodeData neighborNode=graph.getNode(neighbor);
                if (distanceToNeighbor < neighborNode.getWeight() ) {
                    neighborNode.setWeight(distanceToNeighbor);
                    dist.put(neighbor, neighborNode);
                }
            }
        }

        double distance = graph.getNode(dest).getWeight();

        return distance;
    }

    private NodeData getMin(HashMap<Integer, NodeData> nodesMap) {
        NodeData minNode = null;
        double min = Integer.MAX_VALUE;
        for (NodeData node : nodesMap.values()) {
            if (node.getWeight() <= min) {
                minNode = node;
                min = node.getWeight();
            }
        }

        return minNode;
    }
    @Override
    public NodeData center() {
        if (!isConnected() || graph.nodeSize() == 0)
            return null;
        Iterator<NodeData> t = graph.nodeIter();
        double currentMin = Integer.MAX_VALUE;
        int node = -1;

        while (t.hasNext()) {
            NodeData nd = t.next();
            double distance = getMaxDistance(nd.getKey());
            if (distance < currentMin) {
                currentMin = distance;
                node = nd.getKey();
            }
        }

        return graph.getNode(node);
    }


    /**
     * assumes the graph is strongly connected , get the max distance from the node src
     *
     * @param src the node source key
     * @return max distance from this node to any other
     */
    private double getMaxDistance(int src) {
        double nodeMaxDist = Integer.MIN_VALUE;

        Iterator<NodeData> it = graph.nodeIter();
        while (it.hasNext()) {
            double d = shortestPathDist(src, it.next().getKey());
            if (nodeMaxDist < d) {
                nodeMaxDist = d;
            }
        }

        return nodeMaxDist;
    }



    //Time Complexity: Worst Case n*(E+V*Log(V))
    @Override
    public List<NodeData> tsp(List<NodeData> cities) {

        //copy of targets to do adjustments
        List<NodeData> targetTo = new ArrayList<NodeData>(cities);
        List<NodeData> res = new ArrayList<NodeData>();
        int src = targetTo.get(0).getKey();
        if (cities.size() == 1)
            return shortestPath(src, src);

        int dest = targetTo.get(1).getKey();

        while (!targetTo.isEmpty()) {
            //make sure last node don`t appear twice in the result
            if (!res.isEmpty() && res.get(res.size() - 1).getKey() == src)
                res.remove(res.size() - 1);


            List<NodeData> tmp = shortestPath(src, dest);
            //remove all cities we already visited
            targetTo.removeAll(tmp);
            res.addAll(tmp);
            //set the src and dest for the next loop
            if (!targetTo.isEmpty()) {
                src = dest;
                dest = targetTo.get(0).getKey();
            }

        }
        return res;
    }



    @Override
    public boolean save(String file) {
        boolean state = true;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement je = JsonParser.parseString(new Gson().toJson(new fromJsonToGraph(this.graph)));
        String prettyJsonString = gson.toJson(je);
        try {
            FileWriter fr = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fr);
            bw.write(prettyJsonString);
            bw.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
            state = false;
        }
        return state;
    }

    @Override
    public boolean load(String file) {
        this.graph = DirectedWeightedGraphImpl.load(file);
        if (this.graph != null)
            return true;
        return false;
    }
}

