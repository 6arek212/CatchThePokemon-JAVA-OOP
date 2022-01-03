import GameClient.utils.Point;
import api.DirectedWeightedGraph;
import implementation.DirectedWeightedGraphImpl;
import implementation.NodeDataImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DirectedGraphTest {



    @Test
    public void addNode() {
        DirectedWeightedGraph graph = new DirectedWeightedGraphImpl();
        graph.addNode(new NodeDataImpl(0, new Point(2, 3, 0)));
        assertEquals(1, graph.nodeSize());
    }

    @Test
    public void deleteNode() {
        DirectedWeightedGraph graph = new DirectedWeightedGraphImpl();
        graph.addNode(new NodeDataImpl(0, new Point(2, 3, 0)));
        graph.removeNode(0);
        assertEquals(0, graph.nodeSize());
    }


    @Test
    public void addEdge() {
        DirectedWeightedGraph graph = new DirectedWeightedGraphImpl();
        graph.addNode(new NodeDataImpl(0, new Point(2, 3, 0)));
        graph.addNode(new NodeDataImpl(1, new Point(2, 3, 0)));
        graph.connect(0, 1, 5);
        assertEquals(1, graph.edgeSize());
    }

    @Test
    public void removeEdge() {
        DirectedWeightedGraph graph = new DirectedWeightedGraphImpl();
        graph.addNode(new NodeDataImpl(0, new Point(2, 3, 0)));
        graph.addNode(new NodeDataImpl(1, new Point(2, 3, 0)));
        graph.connect(0, 1, 5);
        graph.removeEdge(0, 1);
        assertEquals(0, graph.edgeSize());
    }
}
