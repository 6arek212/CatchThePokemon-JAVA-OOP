import api.DirectedWeightedGraphAlgorithms;
import api.NodeData;
import implementation.AlgorithmsImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlgorithmsTest {
    static DirectedWeightedGraphAlgorithms alg;

    @BeforeAll
    public static void init() {
        alg = new AlgorithmsImpl();
        alg.load("G1.json");
    }


    @Test
    public void shortestPath() {
        assertEquals(5.350731924801653, alg.shortestPathDist(0, 4));
    }


    @Test
    public void center() {
        assertEquals(8, alg.center().getKey());
    }


    @Test
    public void tsp() {
        List<NodeData> cities = new ArrayList<>();
        cities.add(alg.getGraph().getNode(1));
        cities.add(alg.getGraph().getNode(2));
        cities.add(alg.getGraph().getNode(3));
        cities.add(alg.getGraph().getNode(4));
        cities.add(alg.getGraph().getNode(5));

        List<NodeData> path = alg.tsp(cities);
        for (int i = 0; i < path.size(); i++) {
            assertEquals(i + 1, path.get(i).getKey());
        }
    }


    @Test
    public void icConnected() {
        assertTrue(alg.isConnected());
    }

}
