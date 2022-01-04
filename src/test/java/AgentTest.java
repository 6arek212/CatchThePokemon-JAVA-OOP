import static org.junit.jupiter.api.Assertions.*;

import GameClient.Agent;
import GameClient.Client;
import GameClient.GameWorld;
import GameClient.Pokemon;
import api.*;
import GameClient.utils.Point;
import implementation.AlgorithmsImpl;
import implementation.DirectedWeightedGraphImpl;
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


    public GameWorld gameWorld;
    public Client game;

    public DirectedWeightedGraph graph = new DirectedWeightedGraphImpl();
    public DirectedWeightedGraphAlgorithms algo = new AlgorithmsImpl();
    public List<Pokemon> pokemons = new LinkedList<>();
    public List<Agent> agents = new LinkedList<>();
    public DirectedWeightedGraph _graph;


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

    }

    /**
     * Test the agent setCurrNode setNextNode currEdge etc
     * */
    @Test
    void AgentFunctions() {
        StartGame();

        for (Agent agent : agents) {
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

    }

    /**
     * Test the agent speed
     * */
    @Test
    void getSpeed() {

        for (Agent a : agents) {
            double speed = a.getSpeed();
            a.setSpeed(Double.MAX_VALUE);
            assertEquals(a.getSpeed(), Double.MAX_VALUE);
            assertNotEquals(a.getSpeed(), speed);
        }

    }

    /**
     * Test the agent value
     * */
    @Test
    void getValue() {
        for (Agent a : agents) {
            double value = a.getValue();
            a.setValue(Double.MAX_VALUE);
            assertEquals(a.getValue(), Double.MAX_VALUE);
            assertNotEquals(a.getValue(), value);
        }

    }
    /**
     * Test the agent curr Pokémon
     * */
    @Test
    void get_curr_pokemon() {

            for(Agent a : agents) {
                Pokemon pokemon = pokemons.get(a.getCurrPok());
                Point point = new Point(Integer.MAX_VALUE,Integer.MIN_VALUE,Integer.MAX_VALUE);
                Pokemon newPok = new Pokemon(point,100 , 500 , null);
                pokemons.add(newPok);
                a.setCurrPokemon(pokemons.size());
                assertEquals(pokemons.get(a.getCurrPok()), newPok);
                assertNotEquals(pokemons.get(a.getCurrPok()),pokemon);
            }

    }
    /**
     * Test the agent setPos and getPos
     * */
    @Test
    void getLocation() {


            for(Agent a : agents) {
                GeoLocation aPos = a.getPos();
                GeoLocation newPo = new Point(Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE);
                a.setPos(newPo);
                assertEquals(a.getPos(), newPo);
                assertNotEquals(a.getPos(),aPos);
            }

    }


}