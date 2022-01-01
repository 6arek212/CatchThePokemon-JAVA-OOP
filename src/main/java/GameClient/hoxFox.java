//package GameClient;
//
//
//import GameClient.utils.Point;
//import api.DirectedWeightedGraph;
//import api.DirectedWeightedGraphAlgorithms;
//import api.NodeData;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import implementation.AlgorithmsImpl;
//import implementation.jsonToGraphGame;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.util.*;
//
//import static java.lang.Thread.sleep;
//
//
//public class hoxFox implements Runnable {
//
//
//    private static DirectedWeightedGraph g;
//    //    DirectedWeightedGraphAlgorithms aglo;
//    private static GameWorld gameWorld;
//    //    private static Client game;
//    private static HashMap<Integer, List<NodeData>> patht = new HashMap<Integer, List<NodeData>>();
//    private static PriorityQueue<double[]> sp = new PriorityQueue<>(Comparator.comparingDouble(o -> (o[2])));
//    //priority Queue for shortest dis agent and pokemon using
//    private static PriorityQueue<List<Double>> listPriorityQueue = new PriorityQueue<>(Comparator.comparingDouble(o -> (o.get(2))));
//    private static double ms = 100;
//    private static HashMap<Integer, Integer> ageToPok = new HashMap<>();
//
//    public static void main(String[] args) {
//        Thread GameRun = new Thread(new hoxFox());
//        GameRun.start();
//
//    }
//
//
//    @Override
//    public void run() {
//        Client game = new Client();
//        try {
//            game.startConnection("127.0.0.1", 6666);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
////        game.login(id);
//        GsonBuilder builder = new GsonBuilder();
//        builder.registerTypeAdapter(DirectedWeightedGraph.class, new jsonToGraphGame());
//        Gson gson = builder.create();
//        g = gson.fromJson(game.getGraph(), DirectedWeightedGraph.class);
//        initGame(game);
//        DirectedWeightedGraphAlgorithms ga = new AlgorithmsImpl(g);
//        ga.init(g);//now we can do algo on the game  graph
//        game.addAgent("{\"id\":0}");
////        game.startGame();
//
//        game.start();
//
//        int dtt;
//
//        while (game.isRunning().equals("true")) {
//            moveAgents(game);
//            dtt = isCloseToPok(gameWorld.getPokemons(), gameWorld.getAgents()) ? 20 : 120;
//            try {
////                Thread.sleep((long) ms);
//                Thread.sleep(dtt);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//
//        }
////        game.stop();
//    }
//
//
//    public void moveAgents(Client game) {
//        game.move();
////        double p, a;
////        int src, dest;
//
//        var agents = gameWorld.getAgents(game.getAgents(), g);
//        var pokemons = gameWorld.json2Pokemons(game.getPokemons());
//        DirectedWeightedGraphAlgorithms Aglo = new AlgorithmsImpl(g);
//        gameWorld.setAgents(agents);
//        gameWorld.setPokemons(pokemons);
//        gameWorld.setGraph(g);
//        Aglo.init(g);
//        computeDistance(pokemons, agents, g);
//        int count = 0;
//        while (!listPriorityQueue.isEmpty() && count < agents.size()) {
//
//            List<Double> minAgentPok = listPriorityQueue.poll();
//            //getting the pokemon index in pokemons list
//            double p = minAgentPok.get(0);
//            double a = minAgentPok.get(1);
//            Pokemon pokemon = pokemons.get((int) p);
//            Agent agent = agents.get((int) a);
//
//            //if the agent is not moving and noOne Took the pokemon
//            if (!agent.isMoving() && !pokemon.isCatched()) {
//                int dest;
////                int src = agent.getSrcNode();
//                int src = agents.get((int) a).getSrcNode();
//                dest = pokemon.getEdge().getSrc();
//                //the Dest Node On the Pokemon Edge
//                NodeData LastDestNode = g.getNode(pokemon.getEdge().getDest());
//                List<NodeData> path = new ArrayList<>();
//                agent.setCurrPokemon((int) p);
//                ageToPok.put((int) a, (int) p);
//                System.out.println(src);
//                System.out.println(dest);
//                if (src != dest) {
//                    path = Aglo.shortestPath(src, dest);
//
//                }
//
//                path.add(path.size(), LastDestNode);
//                agent.setAgentCurrPath(path);
//                System.out.println(a);
//                patht.put((int)a, path);
//                count++;
//                pokemon.setCatched(true);
//                agent.set_isMoving(true);
//            }
//
//
//        }
//        listPriorityQueue.clear();
//        nextDis(game, pokemons, agents);
//
//    }
//
//    public boolean isCloseToPok(List<Pokemon> pokemons, List<Agent> agents) {
//
//        for (Agent agent : agents) {
//            Pokemon pokemon = pokemons.get(agent.getCurrPok());
//            if (agent.getCurrEdge() == pokemon.getEdge() &&
//                    agent.getPos().distance(pokemon.getPos()) < 0.001 * agent.getSpeed())
//                return true;
//
//
//        }
//
//        return false;
//    }
//
//
//    //for each agent attach to him his path
////    private static void nextDis(Client game, List<Pokemon> poks, List<Agent> age) {
////        double min = Integer.MAX_VALUE;
////        double sum = 0;
////        double avg = Integer.MAX_VALUE;
////        int count = 0;
////        for (int i = 0; i < age.size(); i++) { // pass the agents
////
////            Agent a = age.get(i); //get the actual agent
////            List<NodeData> path = a.getAgentCurrPath();
////            int id = a.getId(); //get the agent id (same like j index)
////            System.out.println("--->" + path);
////            if (!path.isEmpty()) { // if its path isn't empty
////                int nxt = path.remove(0).getKey();//remove and get the next node from the agent path list
////                a.setNextNode(nxt); //set the next node to be it
////                a.computeTime(100, poks); //computes the exact time we should wait in order this agent will "eat" the pokemon
////
////                if (path.isEmpty()) { //if after it the agent have no path anymore= the next step is to it the pokemon
////                    if (a.getTime() < min) { // get the time we have computed and check if its lower than the min
////                        min = a.getTime(); // if its lower, now the min will be the time
////                    }
////                } else { //if the agent has more nodes in its path after the removing
////                    count++; //up the counter
////                    sum += a.getTime(); //add the agent time to the sum
////                    avg = sum / count;
////
////                }
////
//////                game.chooseNextEdge(id, nxt);// let the game know what the next destination node of the agent
////
////                game.chooseNextEdge("{\"agent_id\":0, \"next_node_id\": " + nxt + "}");
////                System.out.println("Agent: " + id + ", val: " + a.getValue() + " turned from node: " + a.getSrcNode() + " to node: " + nxt + " in speed: " + a.getSpeed());
////
////            }
////
////				/*if(count!=0) // if none of the agents needed to eat in its next step
////					avg = sum / count; // computes the average time of all the agents we counted*/
////            ms = Math.min(min, avg);//update the time that we need to wait to the next move to be the minimum
////
////
////        }
////    }
//    private static void nextDis(Client game, List<Pokemon> poks, List<Agent> age) {
//        double min = Integer.MAX_VALUE;
//        double sum = 0;
//        double avg = Integer.MAX_VALUE;
//        int count = 0;
//        for (int j = 0; j < age.size(); j++) { // pass the agents
//
//            Agent a = age.get(j); //get the actual agent
//            int id = a.getId(); //get the agent id (same like j index)
//            System.out.println("--->" + patht.get(j));
//            if (!patht.get(j).isEmpty()) { // if its path isn't empty
//                int nxt = patht.get(id).remove(0).getKey();//remove and get the next node from the agent path list
//                a.setNextNode(nxt); //set the next node to be it
//                a.computeTime(100, poks, ageToPok); //computes the exact time we should wait in order this agent will "eat" the pokemon
//
//                if (patht.get(j).isEmpty()) { //if after it the agent have no path anymore= the next step is to it the pokemon
//                    if (a.getTime() < min) { // get the time we have computed and check if its lower than the min
//                        min = a.getTime(); // if its lower, now the min will be the time
//                    }
//                } else { //if the agent has more nodes in its path after the removing
//                    count++; //up the counter
//                    sum += a.getTime(); //add the agent time to the sum
//                    avg = sum / count;
//
//                }
//
////                game.chooseNextEdge(id, nxt);// let the game know what the next destination node of the agent
//
//                game.chooseNextEdge("{\"agent_id\":0, \"next_node_id\": " + nxt + "}");
//
//                System.out.println("Agent: " + id + ", val: " + a.getValue() + " turned from node: " + a.getSrcNode() + " to node: " + nxt + " in speed: " + a.getSpeed());
//
//            }
//
//				/*if(count!=0) // if none of the agents needed to eat in its next step
//					avg = sum / count; // computes the average time of all the agents we counted*/
//            ms = Math.min(min, avg);//update the time that we need to wait to the next move to be the minimum
//
//
//        }
//    }
//
//
//    public void computeDistance(List<Pokemon> pokemons, List<Agent> agents, DirectedWeightedGraph g) {
//        gameWorld.updatePokemonsEdges(pokemons);
//
//        double dist, srcToDestPokEdge, locToSrcAgent;
//        DirectedWeightedGraphAlgorithms algo = new AlgorithmsImpl(g);
//        for (int i = 0; i < pokemons.size(); i++) {
//            Pokemon pokemon = pokemons.get(i);
//
//            for (int j = 0; j < agents.size(); j++) {
//                Agent agent = agents.get(j);
//                List<Double> agentToPok = new ArrayList<>();
//                agentToPok.add(0, (double) i);
//                agentToPok.add(1, (double) j);
//                int src, dest;
//                src = agent.getSrc();
//                dest = pokemon.getEdge().getSrc();
//
//                if (src == dest) {
//                    dist = agent.getPos().distance(pokemon.getPos()) / agent.getSpeed();
//                    agentToPok.add(2, dist);
//
//                } else {
//                    Point srcEdge = (Point) g.getNode(pokemon.getEdge().getSrc()).getLocation();
//                    Point destEdge = (Point) g.getNode(pokemon.getEdge().getDest()).getLocation();
//                    Point srcAg = (Point) g.getNode(agent.getSrc()).getLocation();
//
//                    srcToDestPokEdge = srcEdge.distance(destEdge);
//                    locToSrcAgent = srcAg.distance(agent.getPos());
//                    dist = algo.shortestPathDist(src, dest) - srcToDestPokEdge + locToSrcAgent;
//                    agentToPok.add(2, dist);
//
//                }
//                listPriorityQueue.add(agentToPok);
//
//            }
//
//        }
//
//
//    }
//
//
//    private void initGame(Client game) {
//        String pokz = game.getPokemons();
//        gameWorld = new GameWorld();
//        gameWorld.setGraph(g);
//        gameWorld.setPokemons(GameWorld.json2Pokemons(pokz));
////        _win = new MyFrame("test Ex2");
////        _win.setGame(game);
////        _win.setSize(1000, 700);
////        _win.update(_ar);
////
////        _win.show();
//        String info = game.getInfo();
//        JSONObject line;
//        try {
//            line = new JSONObject(info);
//            JSONObject ttt = line.getJSONObject("GameServer");
//            int rs = ttt.getInt("agents");
//            System.out.println(info);
//            System.out.println(game.getPokemons());
//            ArrayList<Pokemon> cl_fs = GameWorld.json2Pokemons(game.getPokemons());
//            cl_fs.sort(Comparator.comparingInt(o -> (int) o.getValue()));
//            gameWorld.updatePokemonsEdges(cl_fs);
//
//
//            for (int a = 0; a < rs; a++) {
//                Pokemon c = cl_fs.get(a);
//                int sn = c.getEdge().getSrc();
////                path.put(a, new LinkedList<>());
//                game.addAgent("{\"id\":" + a + "}");
//                game.addAgent("{\"id\":2}");
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//}
