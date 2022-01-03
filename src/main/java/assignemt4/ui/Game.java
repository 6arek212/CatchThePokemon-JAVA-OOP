package assignemt4.ui;

import assignemt4.api.DirectedWeightedGraphAlgorithms;
import assignemt4.api.EdgeData;
import assignemt4.api.NodeData;
import assignemt4.ex4_java_client.Client;
import assignemt4.impl.AlgorithmsImpl;
import assignemt4.impl.DirectedWeightedGraphImpl;
import assignemt4.models.Agent;
import assignemt4.models.Info;
import assignemt4.models.Pokemon;

import javax.xml.crypto.Data;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Game {
    private Client client;
    private HashMap<Integer, Agent> agents;
    private List<Pokemon> pokemons;
    private DirectedWeightedGraphAlgorithms algo;
    private AgentsBrain brain;
    private ActionListener actionListener;
    private Info info;

    public Game(Client client, ActionListener actionListener) {
        this.client = client;
        this.actionListener = actionListener;
        this.algo = new AlgorithmsImpl();
        init();
        this.brain = new AgentsBrain(algo, this.pokemons, client, actionListener);
        new Thread(this::startGame).start();
    }

    private void init() {
        this.algo.init(DirectedWeightedGraphImpl.load(client.getGraph()));
        Info info = Info.load(client.getInfo());
        System.out.println("agents " + info.getAgents());
        for (int i = 0; i < info.getAgents(); i++) {
            int node = (int) (Math.random() * (algo.getGraph().nodeSize() - 1));
            client.addAgent("{\"id\":" + node + "}");
        }

        this.agents = Agent.load(client.getAgents());
        System.out.println(this.agents.values());

        this.pokemons = Pokemon.load(client.getPokemons());
        for (Pokemon p : pokemons) {
            p.setEdge(this.algo.getGraph());
        }

        System.out.println(this.pokemons);
        String isRunningStr = client.isRunning();
        System.out.println(isRunningStr);
        this.info = Info.load(client.getInfo());
        System.out.println(this.info);
    }


    private void startGame() {
        client.start();
        int cnt = 0;
        while (client.isRunning().equals("true")) {
            this.info = Info.load(client.getInfo());
            updateAgents();
            updatePokemons();
//            allocatePokemons();
            moveAgents();

            long sleepTime;
            double min = getMin2() * 1000;
            double c = closestOnPokEdgePokemon() * 1000;
            sleepTime = (long) Math.min(min, c);
            moveAndWait(sleepTime);
            cnt++;
            System.out.println("moved " + cnt);
            actionListener.actionEvent(new UIEvents.UpdateUi());
        }

        System.out.println(this.info);
    }

    private void moveAndWait(long time) {
        client.move();
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private double getMinEdge() {
        double min = Double.MAX_VALUE;
        Iterator<EdgeData> it = algo.getGraph().edgeIter();
        while (it.hasNext()) {
            EdgeData ed = it.next();
            if (ed.getWeight() < min) {
                min = ed.getWeight();
            }
        }
        return min;
    }

    private double getMin() {
        double min = Double.MAX_VALUE;
        for (Agent agent : agents.values()) {
            double w = algo.getGraph().getEdge(agent.getSrc(), agent.getDest()).getWeight();
            if (agent.getDest() != -1 && w < min) {
                min = w;
            }
        }
        return min;
    }


    private double getMin2() {
        double min = Double.MAX_VALUE;
        for (Agent agent : agents.values()) {
            if (agent.getDest() != -1) {
                NodeData node = this.algo.getGraph().getNode(agent.getSrc());
                NodeData node2 = this.algo.getGraph().getNode(agent.getDest());
                EdgeData ed = algo.getGraph().getEdge(node.getKey(), node2.getKey());

                double dist = node.getLocation().distance(node2.getLocation());
                double currentDist = agent.getLocation().distance(node2.getLocation());
                double timeForEdge = currentDist * (ed.getWeight() / agent.getSpeed()) / dist;

                if (agent.getDest() != -1 && timeForEdge < min) {
                    min = timeForEdge;
                }
            }
        }
        return min;
    }


    private double closestOnPokEdgePokemon() {
        double min = Double.MAX_VALUE;
        for (Agent agent : agents.values()) {
            if (agent.getCurrentPok() != null && agent.getSrc() == agent.getCurrentPok().getEdge().getSrc() && !agent.getCurrentPok().isCaptured()) {
                NodeData firstNode = algo.getGraph().getNode(agent.getSrc());
                NodeData secondNode = this.algo.getGraph().getNode(agent.getCurrentPok().getEdge().getDest());

                double timeForEdge = agent.getCurrentPok().getEdge().getWeight() / agent.getSpeed();

                double dist = firstNode.getLocation().distance(secondNode.getLocation());
                double time = (timeForEdge * agent.getLocation().distance(agent.getCurrentPok().getLocation()) / dist);
                agent.getCurrentPok().setCaptured(true);
                if (time < min) {
                    min = time;
                }
            }
        }
        return min;
    }


    private boolean isOnPokEdgePokemon() {
        for (Agent agent : agents.values()) {
            if (agent.getCurrentPok() != null && agent.getSrc() == agent.getCurrentPok().getEdge().getSrc()) {
                return true;
            }
        }
        return false;
    }


    private boolean isNearPokemon() {
        for (Agent agent : agents.values()) {
            if (agent.getCurrentPok() != null && agent.getLocation().distance(agent.getCurrentPok().getLocation()) <= 0.0001) {
                return true;
            }
        }
        return false;
    }


//    private void allocatePokemons() {
//        for (Pokemon pokemon : pokemons) {
//            findBestAgent(pokemon).getPokemons().add(pokemon);
//        }
//    }
//
//    private Agent findBestAgent(Pokemon pokemon) {
//        Agent picked = null;
//        double min = Double.MAX_VALUE;
//
//        for (Agent agent : agents.values()) {
//            double dist = algo.shortestPathDist(agent.getSrc(), pokemon.getEdge().getSrc()) + pokemon.getEdge().getWeight();
//            if (dist < min) {
//                min = dist;
//                picked = agent;
//            }
//        }
//        return picked;
//    }


    private void moveAgents() {
        for (Agent agent : agents.values()) {
            if (agent.getDest() == -1) {
                brain.setNextDest(agent);
                client.chooseNextEdge("{\"agent_id\":" + agent.getId() + " , next_node_id :" + agent.getDest() + "}");
            }
        }
    }


    private void updateAgents() {
        HashMap<Integer, Agent> newAgents = Agent.load(client.getAgents());
        for (Agent agent : newAgents.values()) {
            this.agents.get(agent.getId()).setLocation(agent.getLocation());
            this.agents.get(agent.getId()).setDest(agent.getDest());
            this.agents.get(agent.getId()).setSrc(agent.getSrc());
            this.agents.get(agent.getId()).setSpeed(agent.getSpeed());
        }
    }

    private void updatePokemons() {
        List<Pokemon> ps = Pokemon.load(client.getPokemons());
        if (ps == null)
            return;
        this.pokemons = ps;
        for (Pokemon p : pokemons) {
            p.setEdge(this.algo.getGraph());
        }
        this.brain.setPokemons(pokemons);
    }

    public List<Pokemon> getPokemons() {
        return pokemons;
    }

    public HashMap<Integer, Agent> getAgents() {
        return agents;
    }

    public DirectedWeightedGraphAlgorithms getAlgo() {
        return algo;
    }
}
