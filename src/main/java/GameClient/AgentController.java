package GameClient;

import GameClient.utils.Point;
import GameGui.GameFrame;
import api.DirectedWeightedGraph;
import api.DirectedWeightedGraphAlgorithms;
import api.GeoLocation;
import api.NodeData;
import implementation.AlgorithmsImpl;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class AgentController {


    private static DirectedWeightedGraph graph;
    private static GameWorld gameWorld;
    private static GameFrame gameFrame;
    private static PriorityQueue<List<Double>> listPriorityQueue = new PriorityQueue<>(Comparator.comparingDouble(o -> (o.get(2))));
    private static double ms = 100;
    private static int agentSize;
    public static Client game;
    public static long id = -1;

    public AgentController(DirectedWeightedGraph graph, GameWorld gameWorld, GameFrame gameFrame) {
        this.gameWorld = gameWorld;
        this.gameFrame = gameFrame;
        this.graph = graph;

    }


    public void moveAgents(Client game) {
        game.move();

        var agents = GameWorld.getAgents(game.getAgents(), graph);
        var pokemons = GameWorld.fromJsonStringToPoks(game.getPokemons());
        DirectedWeightedGraphAlgorithms Algo = new AlgorithmsImpl();
        gameWorld.setAgents(agents);
        gameWorld.setPokemons(pokemons);
        gameWorld.setGraph(graph);
        Algo.init(graph);
        computeDistance(pokemons, agents, graph);
        assignPokemons(pokemons, agents, Algo);
        nextDestination(game, pokemons, agents);

    }

    public void assignPokemons(List<Pokemon> pokemons, List<Agent> agents, DirectedWeightedGraphAlgorithms Algo) {
        int src, dest;
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
            if (!agent.isMoving() && !pokemon.isAssigned()) {
                src = agents.get((int) a).getSrc();
                dest = pokemon.getEdge().getSrc();
                NodeData LastDestNode = graph.getNode(pokemon.getEdge().getDest());
                LinkedList<NodeData> path = new LinkedList<>();
                agent.setCurrPokemon((int) p);
                if (src != dest) {
                    path = (LinkedList<NodeData>) Algo.shortestPath(src, dest);
                }
                path.addLast(LastDestNode);
                agent.setAgentCurrPath(path);
                count++;
                pokemon.setAssigned(true);
                agent.setIsMoving(true);
            }
        }
        listPriorityQueue.clear();


    }

    // estimate time for an agent to reach his Pokémon using motion equation
    public static double estimateTime(List<Pokemon> pokemons, Agent agent) {
        var e = agent.getCurrEdge();
        var p = pokemons.get(agent.getCurrPok());
        var agentPos = agent.getPos();
        var speed = agent.getSpeed();
        var w = e.getWeight();
        double estimatedTime = 100;

        if (e != null) {
            GeoLocation dest = graph.getNode(e.getDest()).getLocation();
            GeoLocation src = graph.getNode(e.getSrc()).getLocation();
            double de = src.distance(dest);
            double dist = agentPos.distance(dest);

            //if the agent and the pokemon on the same edge dist is the destination between them
            if (p.getEdge().getSrc() == e.getSrc() && p.getEdge().getDest() == e.getDest()) {
                dist = agentPos.distance(p.getPos());
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
    private static void nextDestination(Client game, List<Pokemon> pokemons, List<Agent> agents) {
        double timeAv = 0;
        for (int i = 0; i < agents.size(); i++) {

            Agent agent = agents.get(i);
            LinkedList<NodeData> path = agent.getAgentCurrPath();
            if (!path.isEmpty()) {
                int next = path.removeFirst().getKey();
                agent.setNextNode(next);
                timeAv = estimateTime(pokemons, agent);
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



}