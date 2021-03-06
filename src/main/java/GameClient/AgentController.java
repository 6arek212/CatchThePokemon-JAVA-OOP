package GameClient;

import GameClient.utils.Point;
import GameGui.GameFrame;
import api.*;
import implementation.AlgorithmsImpl;

import java.util.*;

/**
 * This class represent an overall algorithm for the game
 */
public class AgentController {

    private static DirectedWeightedGraph graph;
    private static GameWorld gameWorld;
    private static PriorityQueue<List<Double>> listPriorityQueue = new PriorityQueue<>(Comparator.comparingDouble(o -> (o.get(2))));
    public static double dt = 100;
    public static long id = -1;

    public AgentController(DirectedWeightedGraph graph, GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        this.graph = graph;

    }


    /**
     * Move agents in their best assigned path
     */
    public void moveAgents(Client game) {
        game.move();

        var agents = GameWorld.getAgents(game.getAgents(), graph);
        var pokemons = GameWorld.fromJsonStringToPoks(game.getPokemons());
        DirectedWeightedGraphAlgorithms Algo = new AlgorithmsImpl();
        gameWorld.setAgents(agents);
        gameWorld.setPokemons(pokemons);
        gameWorld.setGraph(graph);
        Algo.init(graph);
        addAllCouples(pokemons, agents, graph);
        assignPokemons(pokemons, agents, Algo);
        nextDestination(game, pokemons, agents);

    }


    /**
     * Take the best due (agent,Pokémon) and assign them together
     * and update the agent the shortest path to this Pokémon
     */
    public void assignPokemons(List<Pokemon> pokemons, List<Agent> agents, DirectedWeightedGraphAlgorithms Algo) {
        int src, dest;
        int count = 0;
        while (!listPriorityQueue.isEmpty() && count < agents.size()) {
            List<Double> minAgentPok = listPriorityQueue.poll();
            //getting the pokemon index in pokemons list
            double p = minAgentPok.get(0);
            double a = minAgentPok.get(1);
            Pokemon pokemon = pokemons.get((int) p);
            Agent agent = agents.get((int) a);
            //if the agent is not moving and noOne Took the pokemon
            if (!agent.isMoving() && !pokemon.isAssigned()) {
                src = agents.get((int) a).getSrc();
                EdgeData e = pokemon.getEdge();
                if ((e.getDest() > e.getSrc() && pokemon.getType() == 1 || e.getDest() < e.getSrc() && pokemon.getType() == -1) && getRatio(pokemon.getPos(), e, graph) > 0.13) {
                    dest = e.getSrc();
                } else {
                    dest = e.getDest();
                }
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


    /**
     * Giving the next destination for every agent according
     * to his current shortest path
     */
    private static void nextDestination(Client game, List<Pokemon> pokemons, List<Agent> agents) {
        double timeAv;
        double min = Integer.MAX_VALUE;
        for (int i = 0; i < agents.size(); i++) {

            Agent agent = agents.get(i);
            LinkedList<NodeData> path = agent.getAgentCurrPath();
            if (!path.isEmpty()) {
                int next = path.removeFirst().getKey();
                agent.setNextNode(next);

                //estimating time for each agent and take the min time
                timeAv = estimateTime(pokemons, agent);
                if (timeAv < min) {
                    min = timeAv;
                }
                game.chooseNextEdge("{\"agent_id\":" + i + ", \"next_node_id\": " + next + "}");
            }
            dt = min;

        }
    }


    /**
     * Compute distance between each agent and Pokémon [pok , agent , dist]
     * and add them to Priority Queue to take the Pokémon and agent with the least dist
     */
    public void addAllCouples(List<Pokemon> pokemons, List<Agent> agents, DirectedWeightedGraph g) {
        gameWorld.updatePokemonsEdges(pokemons);
        DirectedWeightedGraphAlgorithms algo = new AlgorithmsImpl();
        algo.init(g);
        for (int i = 0; i < pokemons.size(); i++) {
            Pokemon pokemon = pokemons.get(i);
            for (int j = 0; j < agents.size(); j++) {
                estimateDistance(agents.get(j), pokemon, i, algo);
            }
        }
    }

    public void estimateDistance(Agent agent, Pokemon pokemon, int i, DirectedWeightedGraphAlgorithms algo) {
        int src, dest;
        double dist, srcToDestPokEdge, locToSrcAgent;
        List<Double> agentToPok = new ArrayList<>();
        agentToPok.add(0, (double) i);
        agentToPok.add(1, (double) agent.getId());
        src = agent.getSrc();
        EdgeData e = pokemon.getEdge();
        if ((e.getDest() > e.getSrc() && pokemon.getType() == 1 || e.getDest() < e.getSrc() && pokemon.getType() == -1) && getRatio(pokemon.getPos(), e, graph) > 0.13) {
            dest = e.getSrc();
        } else {
            dest = e.getDest();
        }
        if (src == dest) {
            dist = agent.getPos().distance(pokemon.getPos()) / agent.getSpeed();
            agentToPok.add(2, dist);

        } else {
            Point srcEdge = (Point) algo.getGraph().getNode(pokemon.getEdge().getSrc()).getLocation();
            Point destEdge = (Point) algo.getGraph().getNode(pokemon.getEdge().getDest()).getLocation();
            Point srcAg = (Point) algo.getGraph().getNode(agent.getSrc()).getLocation();
            srcToDestPokEdge = srcEdge.distance(destEdge);
            locToSrcAgent = srcAg.distance(agent.getPos());
            dist = algo.shortestPathDist(src, dest) - srcToDestPokEdge + locToSrcAgent;
            agentToPok.add(2, dist);
        }

        listPriorityQueue.add(agentToPok);

    }


    /**
     * Estimate time for specific  agent the Pokémon assign to him
     * using motion equation
     */
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
    /**
     *  Returns approximately where is the position of a point on an edge
     *  (for the Pokémon)
     */
    public static double getRatio(Point point, EdgeData e, DirectedWeightedGraph g) {
        if (e == null)
            return -1;
        int src = e.getSrc();
        int dest = e.getDest();
        double ratio = g.getNode(src).getLocation().distance(point) / g.getNode(src).getLocation().distance(g.getNode(dest).getLocation());
        return ratio;
    }
}
