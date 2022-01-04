import GameClient.Agent;
import GameClient.Client;
import GameClient.GameWorld;
import GameClient.Pokemon;
import GameClient.utils.Point;
import api.DirectedWeightedGraph;
import api.DirectedWeightedGraphAlgorithms;
import api.EdgeData;
import implementation.AlgorithmsImpl;
import implementation.DirectedWeightedGraphImpl;
import implementation.EdgeDataImpl;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
 * This class tests the Pokémon class on case 0 -> Graph A0!!
 */


class PokemonTest {


    /**
     * Testing getEdge and setEdge functions on Pokémon class
     */
    @Test
    void getEdge_setEdge() {
            Pokemon p = new Pokemon(new Point(31 , 35 , 45) , 100 , 100 ,new EdgeDataImpl(100,100,100));
            EdgeData oldEdge = p.getEdge();
            EdgeData newEdge = new EdgeDataImpl(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
            p.setEdge(newEdge);
            assertEquals(p.getEdge(), newEdge);
            assertNotEquals(p.getEdge(), oldEdge);


    }


    /**
     * Testing setPosition and getPosition functions on Pokémon class
     */
    @Test
    void getPos_setPos() {

            Pokemon p = new Pokemon(new Point(31 , 35 , 45) , 100 , 100 ,new EdgeDataImpl(100,100,100));
            Point oldP = p.getPos();
            Point newP = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
            p.setPos(newP);
            assertEquals(p.getPos(), newP);
            assertNotEquals(p.getPos(), oldP);


    }

    /**
     * Testing setValue and getValue functions on Pokémon class
     */
    @Test
    void getValue_setValue() {
            Pokemon p = new Pokemon(new Point(31 , 35 , 45) , 100 , 100 ,new EdgeDataImpl(100,100,100));
            double oldVal = p.getValue();
            p.setValue(Integer.MAX_VALUE);
            assertEquals(p.getValue(), Integer.MAX_VALUE);
            assertNotEquals(p.getValue(), oldVal);


    }

    @Test
    void isAssigned_setAssigned() {
            Pokemon p = new Pokemon(new Point(31 , 35 , 45) , 100 , 100 ,new EdgeDataImpl(100,100,100));
            p.setAssigned(true);
            assertEquals(p.isAssigned(), true);


    }


}