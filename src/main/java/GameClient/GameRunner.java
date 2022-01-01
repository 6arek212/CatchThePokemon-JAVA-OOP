package GameClient;


import GameClient.utils.Point;
import api.DirectedWeightedGraph;
import api.DirectedWeightedGraphAlgorithms;
import api.NodeData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import implementation.AlgorithmsImpl;
import implementation.jsonToGraphGame;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

import static java.lang.Thread.sleep;


public class GameRunner implements Runnable {


    private static DirectedWeightedGraph g;
    //    DirectedWeightedGraphAlgorithms aglo;
    private static GameWorld gameWorld;
    //    private static Client game;
    private static PriorityQueue<double[]> sp = new PriorityQueue<>(Comparator.comparingDouble(o -> (o[2])));
    //priority Queue for shortest dis agent and pokemon using
    private static PriorityQueue<List<Double>> listPriorityQueue = new PriorityQueue<>(Comparator.comparingDouble(o -> (o.get(2))));
    private static double ms = 100;

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
//        game.login(id);
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(DirectedWeightedGraph.class, new jsonToGraphGame());
        Gson gson = builder.create();
        g = gson.fromJson(game.getGraph(), DirectedWeightedGraph.class);
        initGame(game);
        DirectedWeightedGraphAlgorithms ga = new AlgorithmsImpl(g);
        ga.init(g);//now we can do algo on the game  graph
        game.addAgent("{\"id\":0}");
//        game.startGame();

        game.start();

        int dtt;

        while (game.isRunning().equals("true")) {
            moveAgents(game);
            dtt = isCloseToPok(gameWorld.getPokemons(), gameWorld.getAgents()) ? 20 : 120;
            try {
                Thread.sleep(dtt);

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
//        game.stop();
    }


    public void moveAgents(Client game) {
        game.move();

        int src, dest;

        var agents = gameWorld.getAgents(game.getAgents(), g);
        var pokemons = gameWorld.json2Pokemons(game.getPokemons());
        DirectedWeightedGraphAlgorithms Aglo = new AlgorithmsImpl(g);
        gameWorld.setAgents(agents);
        gameWorld.setPokemons(pokemons);
        gameWorld.setGraph(g);
        Aglo.init(g);
        computeDistance(pokemons, agents, g);
        int count = 0;
        while (!listPriorityQueue.isEmpty() && count < agents.size()) {

            List<Double> minAgentPok = listPriorityQueue.poll();
            //getting the pokemon index in pokemons list
            double p = minAgentPok.get(0);
            double a = minAgentPok.get(1);
            Pokemon pokemon = pokemons.get((int) p);
            Agent agent = agents.get((int) a);

            //if the agent is not moving and noOne Took the pokemon
            if (!agent.isMoving() && !pokemon.isCatched()) {

//                int src = agent.getSrcNode();
                src = agents.get((int) a).getSrcNode();
                dest = pokemon.getEdge().getSrc();
                //the Dest Node On the Pokemon Edge
                NodeData LastDestNode = g.getNode(pokemon.getEdge().getDest());
                List<NodeData> path = new ArrayList<>();
                agent.setCurrPokemon((int) p);
                if (src != dest) {
                    path = Aglo.shortestPath(src, dest);

                }

                path.add(path.size(), LastDestNode);
                agent.setAgentCurrPath(path);

                count++;
                pokemon.setCatched(true);
                agent.set_isMoving(true);
            }


        }
        listPriorityQueue.clear();
        nextDis(game, pokemons, agents);

    }

    //if the agent close to his pok increase the move
    public boolean isCloseToPok(List<Pokemon> pokemons, List<Agent> agents) {

        for (Agent agent : agents) {
            Pokemon pokemon = pokemons.get(agent.getCurrPok());
            if (agent.getCurrEdge() == pokemon.getEdge() &&
                    agent.getPos().distance(pokemon.getPos()) < 0.001 * agent.getSpeed())
                return true;


        }

        return false;
    }


    //    for each agent attach to him his path
    private static void nextDis(Client game, List<Pokemon> poks, List<Agent> age) {

        for (int i = 0; i < age.size(); i++) { // pass the agents

            Agent agent = age.get(i); //get the actual agent
            List<NodeData> path = agent.getAgentCurrPath();
            int id = agent.getId(); //get the agent id (same like j index)
            System.out.println("--->" + path);
            if (!path.isEmpty()) { // if its path isn't empty
                int next = path.remove(0).getKey();//remove and get the next node from the agent path list
                agent.setNextNode(next); //set the next node to be it

                game.chooseNextEdge("{\"agent_id\":0, \"next_node_id\": " + next + "}");
                System.out.println("Agent: " + id + ", value: " + agent.getValue() + " srcNode: " + agent.getSrcNode() + " destNode: " + next + " agent Speed: " + agent.getSpeed());

            }

        }
    }


    public void computeDistance(List<Pokemon> pokemons, List<Agent> agents, DirectedWeightedGraph g) {
        gameWorld.updatePokemonsEdges(pokemons);

        double dist, srcToDestPokEdge, locToSrcAgent;
        DirectedWeightedGraphAlgorithms algo = new AlgorithmsImpl(g);
        for (int i = 0; i < pokemons.size(); i++) {
            Pokemon pokemon = pokemons.get(i);
            System.out.println(pokemon);
            for (int j = 0; j < agents.size(); j++) {
                Agent agent = agents.get(j);
                List<Double> agentToPok = new ArrayList<>();
                agentToPok.add(0, (double) i);
                agentToPok.add(1, (double) j);
                int src, dest;
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
        gameWorld = new GameWorld();
        gameWorld.setGraph(g);
        gameWorld.setPokemons(GameWorld.json2Pokemons(pokz));

        String info = game.getInfo();
        JSONObject line;
        try {
            line = new JSONObject(info);
            JSONObject ttt = line.getJSONObject("GameServer");
            int agentsSize = ttt.getInt("agents");
            ArrayList<Pokemon> cl_fs = GameWorld.json2Pokemons(game.getPokemons());
            cl_fs.sort(Comparator.comparingInt(o -> (int) o.getValue()));
            gameWorld.updatePokemonsEdges(cl_fs);


            for (int i = 0; i < agentsSize; i++) {
                Pokemon c = cl_fs.get(i);
                int sn = c.getEdge().getSrc();
                game.addAgent("{\"id\":" + i + "}");
                game.addAgent("{\"id\":2}");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
