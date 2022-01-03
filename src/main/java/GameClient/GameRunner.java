package GameClient;


import GameClient.utils.Point;
import GameClient.utils.Point;
import GameGui.GameFrame;
import api.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import implementation.AlgorithmsImpl;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

import static java.lang.Thread.sleep;


public class GameRunner implements Runnable {


    private static DirectedWeightedGraph g;
    private static GameWorld gameWorld;
    private static GameFrame gameFrame;
    //priority Queue for shortest dis agent and pokemon using
    private static PriorityQueue<List<Double>> listPriorityQueue = new PriorityQueue<>(Comparator.comparingDouble(o -> (o.get(2))));
    private static double ms = 100;
    private static int agentSize;

    public static void main(String[] args) {
        Thread GameRun = new Thread(new GameRunner());
        GameRun.start();

    }


    @Override
    public void run() {
        Client game = new Client();
        try {
            game.startConnection("127.0.0.1", 6666);
        } catch (IOException e) {
            e.printStackTrace();
        }
        gameWorld = new GameWorld();
        g = gameWorld.fromJsonToGraph(game.getGraph());
        DirectedWeightedGraphAlgorithms ga = new AlgorithmsImpl();
        ga.init(g);//now we can do algo on the game  graph
        initGame(game);
        game.addAgent("{\"id\":0}");
        game.start();
        while (game.isRunning().equals("true")) {
            moveAgents(game);
            gameFrame.repaint();
//            if (agentSize > 1) {
//                ms = isCloseToPok(gameWorld.getPokemons(), gameWorld.getAgents()) ? 20 : 120;
//            }
            try {

                Thread.sleep((long) ms);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }


    public void moveAgents(Client game) {
        game.move();

        int src, dest;

        var agents = gameWorld.getAgents(game.getAgents(), g);
        var pokemons = gameWorld.fromJsonStringToPoks(game.getPokemons());
        DirectedWeightedGraphAlgorithms Aglo = new AlgorithmsImpl();
        gameWorld.setAgents(agents);
        gameWorld.setPokemons(pokemons);
        gameWorld.setGraph(g);
        Aglo.init(g);
        computeDistance(pokemons, agents, g);
        int count = 0;
        while (!listPriorityQueue.isEmpty() && count < agents.size()) {
            System.out.println(listPriorityQueue);

            List<Double> minAgentPok = listPriorityQueue.poll();
            //getting the pokemon index in pokemons list
            double p = minAgentPok.get(0);
            double a = minAgentPok.get(1);
            Pokemon pokemon = pokemons.get((int) p);
            Agent agent = agents.get((int) a);
            //if the agent is not moving and noOne Took the pokemon
            if (!agent.isMoving() && !pokemon.isCatched()) {
                src = agents.get((int) a).getSrc();
                dest = pokemon.getEdge().getSrc();
                NodeData LastDestNode = g.getNode(pokemon.getEdge().getDest());
                LinkedList<NodeData> path = new LinkedList<>();
                agent.setCurrPokemon((int) p);
                if (src != dest) {
                    path = (LinkedList<NodeData>) Aglo.shortestPath(src, dest);
                }

                path.addLast(LastDestNode);
                agent.setAgentCurrPath(path);

                count++;
                pokemon.setCatched(true);
                agent.setIsMoving(true);
            }


        }
        listPriorityQueue.clear();
        nextDestination(game, pokemons, agents);

    }

    //if the agent close to his pok increase the move
    public boolean isCloseToPok(List<Pokemon> pokemons, List<Agent> agents) {

        for (Agent agent : agents) {
            Pokemon pokemon = pokemons.get(agent.getCurrPok());
            if (agent.getCurrEdge() == pokemon.getEdge() &&
                    agent.getPos().distance(pokemon.getPos()) < 0.3 && agent.getSpeed() > 2)
                return true;


        }

        return false;
    }
 // estimate time for an agent to reach his Pokémon using motion equation
    public static double estimateTime(List<Pokemon> pokemons, Agent agent) {
        var e = agent.getCurrEdge();
        var p = pokemons.get(agent.getCurrPok());
        var pos = agent.getPos();
        var speed = agent.getSpeed();
        var w = e.getWeight();
        double estimatedTime= 100;

        if (e != null) {
            GeoLocation dest = g.getNode(e.getDest()).getLocation();
            GeoLocation src = g.getNode(e.getSrc()).getLocation();
            double de = src.distance(dest);
            double dist = pos.distance(dest);

            //if the agent and the pokemon on the same edge dist is the destination between them
            if (p.getEdge().getSrc() == e.getSrc() && p.getEdge().getDest() == e.getDest()) {
                dist = pos.distance(p.getPos());
            }
            //motion equation 
            double n = dist / de;
            double dt = w * n / speed;
            //to millis seconds
            estimatedTime = (1000.0 * dt);
        }

        return estimatedTime;

    }

    //    for each agent attach to him his path
    private static void nextDestination(Client game, List<Pokemon> poks, List<Agent> age) {
        double timeAv = 0;
        for (int i = 0; i < age.size(); i++) {

            Agent agent = age.get(i);
            LinkedList<NodeData> path = agent.getAgentCurrPath();
            if (!path.isEmpty()) {
                int next = path.removeFirst().getKey();
                agent.setNextNode(next);
                timeAv = estimateTime(poks, agent);
                game.chooseNextEdge("{\"agent_id\":" + i + ", \"next_node_id\": " + next + "}");
                System.out.println(agent);

            }
            ms = timeAv;
        }

    }

    //compute distance between each agent and pok [pok , agent , dist]
    // to give the best agent to a pok
    public void computeDistance(List<Pokemon> pokemons, List<Agent> agents, DirectedWeightedGraph g) {
        gameWorld.updatePokemonsEdges(pokemons);
        int src, dest;
        double dist, srcToDestPokEdge, locToSrcAgent;
        DirectedWeightedGraphAlgorithms algo = new AlgorithmsImpl();
        algo.init(g);
        for (int i = 0; i < pokemons.size(); i++) {
            Pokemon pokemon = pokemons.get(i);
            for (int j = 0; j < agents.size(); j++) {
                Agent agent = agents.get(j);
                List<Double> agentToPok = new ArrayList<>();
                agentToPok.add(0, (double) i);
                agentToPok.add(1, (double) agent.getId());

                src = agent.getSrc();
                dest = pokemon.getEdge().getSrc();

                if (src == dest) {
                    dist = agent.getPos().distance(pokemon.getPos()) / agent.getSpeed();
                    agentToPok.add(2, dist);

                } else {
                    Point srcEdge = (Point) g.getNode(pokemon.getEdge().getSrc()).getLocation();
                    Point destEdge = (Point) g.getNode(pokemon.getEdge().getDest()).getLocation();
                    Point srcAg = (Point) g.getNode(agent.getSrc()).getLocation();

                    srcToDestPokEdge = srcEdge.distance(destEdge);
                    locToSrcAgent = srcAg.distance(agent.getPos());
                    dist = algo.shortestPathDist(src, dest) - srcToDestPokEdge + locToSrcAgent;
                    agentToPok.add(2, dist);

                }

                listPriorityQueue.add(agentToPok);

            }

        }


    }


    private void initGame(Client game) {
        String pokz = game.getPokemons();
        gameWorld.setGraph(g);
        gameWorld.setPokemons(GameWorld.fromJsonStringToPoks(pokz));
        String info = game.getInfo();
        gameWorld.setInfo(info);
        gameFrame = new GameFrame(gameWorld);
        System.out.println(gameWorld.getAgents());

        gameFrame.repaint();
        JSONObject infoObject;
        try {
            infoObject = new JSONObject(info);
            JSONObject GameServer = infoObject.getJSONObject("GameServer");
            agentSize = GameServer.getInt("agents");

            ArrayList<Pokemon> p = GameWorld.fromJsonStringToPoks(game.getPokemons());
            p.sort(Comparator.comparingInt(o -> (int) o.getValue()));
            gameWorld.updatePokemonsEdges(p);
            System.out.println(agentSize);
            for (int i = 0; i < agentSize; i++) {
                game.addAgent("{\"id\":" + i + "}");
            }
            gameWorld.setAgents(GameWorld.getAgents(game.getAgents(), g));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
