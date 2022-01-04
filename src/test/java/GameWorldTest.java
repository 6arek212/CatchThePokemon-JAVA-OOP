import GameClient.Agent;
import GameClient.Client;
import GameClient.GameWorld;
import GameClient.Pokemon;
import api.DirectedWeightedGraph;
import api.DirectedWeightedGraphAlgorithms;
import api.EdgeData;
import api.GeoLocation;
import implementation.AlgorithmsImpl;
import implementation.DirectedWeightedGraphImpl;
import implementation.EdgeDataImpl;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
                 * This class tests the GameWorld class on case 0 -> Graph A0!!
                 * ------------------- Turn jar Case 0 for this test----------------
 */

class GameWorldTest {

    public static GameWorld gameWorld;
    public static Client game;

    public static DirectedWeightedGraph graph = new DirectedWeightedGraphImpl();
    public static DirectedWeightedGraphAlgorithms algo = new AlgorithmsImpl();
    public static List<Pokemon> pokemons = new LinkedList<>();
    public static List<Agent> agents = new LinkedList<>();
    public static DirectedWeightedGraph _graph;


    /**
     * make a connection with the server to get Pokémons , agents , graph
     */
    public void StartGame() {
        this.game = new Client();

        gameWorld = new GameWorld();
        try {
            game.startConnection("127.0.0.1", 6666);
        } catch (IOException e) {
            e.printStackTrace();
        }
        game.addAgent("{\"id\":0}");
        game.start();
        graph = gameWorld.fromJsonToGraphGame(game.getGraph());
        algo.init(graph);
        pokemons = GameWorld.fromJsonStringToPoks(game.getPokemons());
        agents = GameWorld.getAgents(game.getAgents(), graph);
        gameWorld.setPokemons(pokemons);
        gameWorld.setAgents(agents);
        game.start();
    }


    /**
     * Testing fromJsonToGraphGame , getAgents(from json string to list of agents),
     * fromJsonStringToPoks(from json string to list of Pokémon)
     */

    @Test
    void getAgents() {
        StartGame();
        assertTrue(agents.size() > 0);
        assertTrue(pokemons.size() > 0);
        assertNotEquals(graph, null);

    }

    /**
     * Testing setPokemons  and getPokemons
     */

    @Test
    void setPokemons_getPokemons() {

        Pokemon pokemon = new Pokemon(null, Integer.MAX_VALUE, Integer.MAX_VALUE, null);
        List<Pokemon> newList = new LinkedList<>();
        newList.add(pokemon);
        gameWorld.setPokemons(newList);
        assertEquals(gameWorld.getPokemons(), newList);
        assertNotEquals(gameWorld.getPokemons(), pokemons);
        gameWorld.setPokemons(pokemons);
        assertEquals(gameWorld.getPokemons(), pokemons);

    }

    /**
     * Testing setGraph and getGraph
     */
    @Test
    void getGraph() {
        gameWorld.setGraph(graph);
        assertEquals(gameWorld.getGraph(), graph);
        gameWorld.setGraph(null);
        assertEquals(gameWorld.getGraph(), null);
        gameWorld.setGraph(graph);
        assertEquals(gameWorld.getGraph(), graph);
    }

    /**
     * Testing if a point on an edge
     */
    @Test
    void checkOnEdge() {
        gameWorld.setGraph(graph);
        gameWorld.setAgents(agents);
        gameWorld.setPokemons(pokemons);
        EdgeData edge = new EdgeDataImpl(9, 8, 33);
        GeoLocation p1 = graph.getNode(edge.getSrc()).getLocation();
        GeoLocation p2 = graph.getNode(edge.getDest()).getLocation();
        Pokemon p = pokemons.get(0);
        boolean ch = gameWorld.CheckOnEdge(p.getPos(), edge, p.getType(), graph);
        assertEquals(ch, true);

    }

}