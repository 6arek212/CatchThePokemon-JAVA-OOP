package assignemt4.impl;

import assignemt4.api.DirectedWeightedGraph;
import assignemt4.api.DirectedWeightedGraphAlgorithms;
import assignemt4.api.EdgeData;
import assignemt4.api.NodeData;
import assignemt4.json_models.DirectedWeightedGraphJson;
import assignemt4.models.NodeDataImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;


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


    @Override
    public double shortestPathDist(int src, int dest) {
        List<NodeData> path = shortestPath(src, dest);
        if (path == null)
            return -1;
        return pathCost(path);
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
        if (graph.nodeSize() == 0 || graph.getNode(dest) == null || graph.getNode(src) == null)
            return null;

        //copy the graph
        DirectedWeightedGraph graph = copy();
        Iterator<NodeData> iterator = graph.nodeIter();

        //init nodes
        iterator.forEachRemaining((NodeData d) -> {
            d.setTag(NodeDataImpl.WHITE);
            if (d.getKey() == src)
                d.setWeight(0);
            else {
                d.setWeight(Double.MAX_VALUE);
            }
        });

        //run the algorithm
        HashMap<Integer, NodeData> dist = new HashMap<>();
        dist.put(src, graph.getNode(src));

        PriorityQueue<NodeData> nodeQ = new PriorityQueue<>(Comparator.comparingDouble(NodeData::getWeight));
        nodeQ.add(graph.getNode(src));

        while (!nodeQ.isEmpty()) {
            NodeData node = nodeQ.poll();
            graph.edgeIter(node.getKey()).forEachRemaining((EdgeData edge) -> {
                NodeData connectedto = graph.getNode(edge.getDest());
                if (connectedto.getTag() == NodeDataImpl.WHITE) {
                    if (node.getWeight() + edge.getWeight() < connectedto.getWeight()) {
                        connectedto.setWeight(node.getWeight() + edge.getWeight());
                        dist.put(connectedto.getKey(), node);
                    }
                    nodeQ.add(connectedto);
                }
            });
            node.setTag(NodeDataImpl.BLACK);
        }


        if (graph.getNode(dest).getWeight() == Double.MAX_VALUE)
            return null;

        // get this graph nodes not the copied graph nodes
        List<NodeData> path = new ArrayList<>();
        NodeData node = graph.getNode(dest);
        while (node.getKey() != src) {
            path.add(0, this.graph.getNode(dist.get(node.getKey()).getKey()));
            node = dist.get(node.getKey());
        }
        path.add(this.graph.getNode(dest));

        return path;
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


    /**
     * @param path list of adjacent nodes
     * @return the sum of the edges weights
     */
    private double pathCost(List<NodeData> path) {
        double sum = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            sum += graph.getEdge(path.get(i).getKey(), path.get(i + 1).getKey()).getWeight();
        }
        return sum;
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
        JsonElement je = JsonParser.parseString(new Gson().toJson(new DirectedWeightedGraphJson(this.graph)));
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
        DirectedWeightedGraph g = DirectedWeightedGraphImpl.load(file);
        if (g != null) {
            this.graph = g;
            return true;
        }
        return false;
    }


}
