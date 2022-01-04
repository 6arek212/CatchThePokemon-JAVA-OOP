import static org.junit.jupiter.api.Assertions.*;

import GameClient.Agent;
import GameClient.Client;
import GameClient.GameWorld;
import GameClient.Pokemon;
import api.*;
import GameClient.utils.Point;
import implementation.AlgorithmsImpl;
import implementation.DirectedWeightedGraphImpl;
import implementation.EdgeDataImpl;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;


/**
 * This class tests the Agent class on case 0 -> Graph A0!!
 */

class AgentTest {
    DirectedWeightedGraph graph = new DirectedWeightedGraphImpl();
    DirectedWeightedGraphAlgorithms algo = new AlgorithmsImpl();


    /**
     * Test the agent setCurrNode setNextNode currEdge etc
     */
    @Test
    void AgentFunctions() {

        algo.load("A.json");
        graph = algo.getGraph();
        System.out.println(graph);
        Agent agent = new Agent(graph, 0);
        int node = 1;
        agent.setCurrNode(node);
        NodeData nodeData = graph.getNode(1);
        NodeData nodeB = graph.getNode(2);
        assertEquals(agent.getCurrNode(), nodeData);
        assertNotEquals(agent.getCurrNode(), nodeB);
        boolean iEdge = agent.setNextNode(2);
        boolean noEdge = agent.setNextNode(1000);
        assertEquals(iEdge, true);
        assertEquals(noEdge, false);
        EdgeData edgeData = agent.getCurrEdge();
        EdgeData testEdge = graph.getEdge(1, 5);
        assertEquals(edgeData, testEdge);


    }

    /**
     * Test the agent speed
     */
    @Test
    void getSpeed() {
        algo.load("A.json");
        graph = algo.getGraph();

        Agent a = new Agent(graph, 0);
        double speed = a.getSpeed();
        a.setSpeed(Double.MAX_VALUE);
        assertEquals(a.getSpeed(), Double.MAX_VALUE);
        assertNotEquals(a.getSpeed(), speed);


    }

    /**
     * Test the agent value
     */
    @Test
    void getValue() {
        algo.load("A.json");
        graph = algo.getGraph();
        Agent a = new Agent(graph, 0);
        double value = a.getValue();
        a.setValue(Double.MAX_VALUE);
        assertEquals(a.getValue(), Double.MAX_VALUE);
        assertNotEquals(a.getValue(), value);


    }

    /**
     * Test the agent curr Pokémon
     */
    @Test
    void get_curr_pokemon() {


        Pokemon p = new Pokemon(new Point(31, 35, 45), 100, 100, new EdgeDataImpl(100, 100, 100));
        List<Pokemon> pokemons = new LinkedList<>();
        pokemons.add(p);
        algo.load("A.json");
        graph = algo.getGraph();
        Agent a = new Agent(graph, 0);
        a.setCurrPokemon(0);
        Point point = new Point(Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
        Pokemon newPok = new Pokemon(point, 100, 500, null);
        pokemons.add(newPok);
        a.setCurrPokemon(1);
        assertEquals(pokemons.get(a.getCurrPok()), newPok);
        assertNotEquals(pokemons.get(a.getCurrPok()), p);


    }

    /**
     * Test the agent setPos and getPos
     */
    @Test
    void getLocation() {
        algo.load("A.json");
        graph = algo.getGraph();
        GeoLocation p = new Point(31, 35, 45);
        Agent a = new Agent(graph, 0);
        a.setPos(p);
        GeoLocation aPos = a.getPos();
        GeoLocation newPo = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
        a.setPos(newPo);
        assertEquals(a.getPos(), newPo);
        assertNotEquals(a.getPos(), aPos);


    }

    /**
     * Test the agent setAgentCurrPath and getAgentCurrPath
     */
    @Test
    void setAgentpath_getAgentpath() {
        algo.load("A.json");
        graph = algo.getGraph();
        EdgeData edgeData = graph.getEdge(0,1);
        Agent a = new Agent(graph, 0);
        a.setCurrNode(0);
        a.setNextNode(edgeData.getDest());
        a.setCurrPokemon(2);
        LinkedList<NodeData> path = (LinkedList<NodeData>) algo.shortestPath(a.getSrc(), a.getCurrEdge().getSrc());
        a.setAgentCurrPath(path);
        assertEquals(path, a.getAgentCurrPath());


    }


}