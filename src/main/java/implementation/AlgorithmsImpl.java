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


    private DirectedWeightedGraph g;
    private DirectedWeightedGraph reverseGraph;


    //    public AlgorithmsImpl(DirectedWeightedGraph g) {
//        this.g = g;
//
//    }
    public AlgorithmsImpl() {


    }

    @Override
    public void init(DirectedWeightedGraph g) {
        this.g = g;

    }

    @Override
    public DirectedWeightedGraph getGraph() {

        return this.g;
    }

    @Override
    public DirectedWeightedGraph copy() {


        DirectedWeightedGraph new_graph = new DirectedWeightedGraphImpl();
        for (Iterator<NodeData> it = this.g.nodeIter(); it.hasNext(); ) {
            NodeData NodeCopy = it.next();

            for (Iterator<EdgeData> iter = this.g.edgeIter(NodeCopy.getKey()); iter.hasNext(); ) {
                EdgeData EdgeCopy = iter.next();
                new_graph.connect(NodeCopy.getKey(), EdgeCopy.getDest(), EdgeCopy.getWeight());

            }


        }
        for (Iterator<NodeData> it = this.g.nodeIter(); it.hasNext(); ) {
            NodeData nodeCopy = it.next();
            NodeData newNode = this.g.getNode(nodeCopy.getKey());
            new_graph.addNode(newNode);
            String newInfo = nodeCopy.getInfo();
            new_graph.getNode(nodeCopy.getKey()).setInfo(newInfo);

        }

        return new_graph;

    }

    @Override
    public boolean isConnected() {


        if (SCC().size() == 1)
            return true;


        return false;
    }


    public List<Set<NodeData>> SCC() {

        Deque<NodeData> stack = new ArrayDeque<>();
        Set<NodeData> visited = new HashSet<>();


        for (Iterator<NodeData> it = g.nodeIter(); it.hasNext(); ) {
            NodeData node = it.next();
            if (visited.contains(node)) {
                continue;
            }

            DFS(node, visited, stack);
        }

        this.reverseGraph = reverse();
        visited.clear();
        List<Set<NodeData>> components = new ArrayList<>();
        while (!stack.isEmpty()) {

            NodeData n = reverseGraph.getNode(stack.poll().getKey());
            if (visited.contains(n)) {
                continue;
            }
            //storing the one scc
            Set<NodeData> set = new HashSet<>();

            DFSForReverseGraph(n, visited, set);
            components.add(set);


        }


        return components;
    }

    //second DFS on reversed Graph
    private void DFSForReverseGraph(NodeData n, Set<NodeData> visited, Set<NodeData> set) {
        visited.add(n);
        set.add(n);
        for (Iterator<EdgeData> it = reverseGraph.edgeIter(n.getKey()); it.hasNext(); ) {
            EdgeData e = it.next();
            NodeData curr = reverseGraph.getNode(e.getDest());
            if (visited.contains(curr)) {
                continue;
            }
            DFSForReverseGraph(curr, visited, set);
        }


    }

    private DirectedWeightedGraph reverse() {
        DirectedWeightedGraph reverseGraph = new DirectedWeightedGraphImpl();

        for (Iterator<EdgeData> it = g.edgeIter(); it.hasNext(); ) {
            EdgeData edge = it.next();
//            System.out.println("No reverse: " + edge);

            reverseGraph.addNode(g.getNode(edge.getSrc()));
            reverseGraph.addNode(g.getNode(edge.getDest()));
            reverseGraph.connect(edge.getDest(), edge.getSrc(), edge.getWeight());

        }
        for (Iterator<NodeData> it = g.nodeIter(); it.hasNext(); ) {
            NodeData nodeData = it.next();
            reverseGraph.addNode(nodeData);

        }

        return reverseGraph;
    }

    //First DFS
    private void DFS(NodeData node, Set<NodeData> visited, Deque<NodeData> stack) {

        visited.add(node);
        for (Iterator<EdgeData> it = g.edgeIter(node.getKey()); it.hasNext(); ) {

            EdgeData e = it.next();

            NodeData curr = g.getNode(e.getDest());
            if (visited.contains(curr)) {
                continue;
            }
            DFS(curr, visited, stack);
        }

        stack.offerFirst(node);

    }

    @Override
    public List<NodeData> shortestPath(int src, int dest) {
        HashMap<Integer, NodeData> dist = new HashMap<Integer, NodeData>();
        for (Iterator<NodeData> it = g.nodeIter(); it.hasNext(); ) {
            NodeData nodeData = it.next();
            nodeData.setWeight(Integer.MAX_VALUE);
        }
        HashMap<Integer, LinkedList<NodeData>> shortestPathMap = new HashMap<>();
        for (Iterator<NodeData> it = g.nodeIter(); it.hasNext(); ) {
            NodeData nodeData = it.next();
            shortestPathMap.put(nodeData.getKey(), new LinkedList<>());
        }
        NodeData start = g.getNode(src);
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
            for (Iterator<EdgeData> it = (g.edgeIter(curr)); it.hasNext(); ) {
                EdgeData n = it.next();
                int neighbor = n.getDest();
                double distanceToNeighbor = g.getEdge(curr, neighbor).getWeight() + currWeight;
                NodeData neighborNode = g.getNode(neighbor);
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
        for (Iterator<NodeData> it = g.nodeIter(); it.hasNext(); ) {
            NodeData nodeData = it.next();
            nodeData.setWeight(Integer.MAX_VALUE);
        }
        NodeData start = g.getNode(src);
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
            for (Iterator<EdgeData> it = g.edgeIter(curr); it.hasNext(); ) {
                EdgeData e = it.next();
                Integer neighbor=e.getDest();
                double distanceToNeighbor = g.getEdge(curr, neighbor).getWeight() + currWeight;
                NodeData neighborNode=g.getNode(neighbor);
                if (distanceToNeighbor < neighborNode.getWeight() ) {
                    neighborNode.setWeight(distanceToNeighbor);
                    dist.put(neighbor, neighborNode);
                }
            }
        }

        double distance = g.getNode(dest).getWeight();

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


    public double maxInThePath(NodeData nodeData) {
        double max = Integer.MIN_VALUE;

        for (Iterator<NodeData> it = g.nodeIter(); it.hasNext(); ) {
            NodeData node = it.next();
            if (nodeData.getKey() != node.getKey()) {
                double shortestP = shortestPathDist(nodeData.getKey(), node.getKey());
                if (max < shortestP) {
                    max = shortestP;


                }

            }
        }
        return max;
    }

    @Override
    public NodeData center() {
        if (g.nodeSize() == 0) {
            return null;
        }

        double min = Integer.MAX_VALUE;
        NodeData ans = null;

        for (Iterator<NodeData> it = g.nodeIter(); it.hasNext(); ) {
            NodeData node = it.next();
            double temp = maxInThePath(node);
            if (temp < min) {
                min = temp;
                ans = node;

            }

        }
        return ans;
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
            System.out.println("src: " + src + ", dest: " + dest + " -> " + tmp);
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
        JsonElement je = JsonParser.parseString(new Gson().toJson(new fromJsonToGraph(this.g)));
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
        this.g = DirectedWeightedGraphImpl.load(file);
        if (this.g != null)
            return true;
        return false;
    }




    public Double getRouteCost(List<NodeData> nodes) {
        double tempCost = 0;
        // Add route costs

        for (int i = 0; i < nodes.size() - 1; i++) {
            tempCost += g.getEdge(nodes.get(i).getKey(), nodes.get(i + 1).getKey()).getWeight();

        }
        return tempCost;
    }


}

