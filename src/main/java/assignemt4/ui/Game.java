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
import java.io.IOException;
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
    public int maxMoves;
    public boolean status;

    public Game(Client client, ActionListener actionListener) {
        this.client = client;
        this.actionListener = actionListener;
        this.algo = new AlgorithmsImpl();
        init();
        this.brain = new AgentsBrain(algo, this.pokemons);
        start();
    }

    /**
     * Initialize the game , get Graph , get Agents , get Pokemons
     */
    private void init() {
        client.login("123");
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


    /**
     * game starting point on a separate thread
     */
    public void start() {
        this.status = true;
        new Thread(this::startGame);

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        Runnable helloRunnable = () -> {
            if (client.isRunning().equals("true"))
                actionListener.actionEvent(new UIEvents.Labels(info, maxMoves, (Integer.parseInt(client.timeToEnd()) / 1000)));
            else
                executor.shutdownNow();
        };
        executor.scheduleAtFixedRate(helloRunnable, 0, 1, TimeUnit.SECONDS);
    }

    public void stopTheGame() {
        this.status = false;
    }


    /**
     * starts the  game
     */
    private void startGame() {
        client.start();
        this.maxMoves = (Integer.parseInt(client.timeToEnd()) / 1000) * 10;
        System.out.println("Game starting ....... ");
        System.out.println("\nMax Moves is :" + maxMoves + "\n\n");

        while (client.isRunning().equals("true") && info.getMoves() <= maxMoves && status) {
            updateAgents();
            updatePokemons();
            actionListener.actionEvent(new UIEvents.UpdateUi());
            allocateAgents();
            long sleepTime;
            sleepTime = (long) (getProperWaitingTime() * 1000);
            if (sleepTime > Integer.parseInt(client.timeToEnd()))
                break;
            waitAndMode(sleepTime);
            this.info = Info.load(client.getInfo());
        }
        this.status = false;
        this.info = Info.load(client.getInfo());
        System.out.println(this.info);
        try {
            client.stop();
            client.stopConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * wait and send a move to the server
     *
     * @param time time to wait
     */
    private void waitAndMode(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (client.isRunning().equals("true"))
            client.move();
    }

    /**
     * get the proper waiting
     *
     * @return
     */
    private double getProperWaitingTime() {
        double min = Double.MAX_VALUE;
        for (Agent agent : agents.values()) {
            if (agent.getDest() != -1) {
                NodeData node = this.algo.getGraph().getNode(agent.getSrc());
                NodeData node2 = this.algo.getGraph().getNode(agent.getDest());
                EdgeData ed = algo.getGraph().getEdge(node.getKey(), node2.getKey());
                double dist = node.getLocation().distance(node2.getLocation());
                double currentDist;

                if (agent.getCurrentPok() != null && agent.isOnPokemonEdge()) {
                    currentDist = Math.abs(agent.getLocation().distance(agent.getCurrentPok().getLocation()));
                    agent.setCurrentPok(null);
                } else {
                    currentDist = Math.abs(agent.getLocation().distance(node2.getLocation()));
                }

                double timeForEdge = (currentDist * (ed.getWeight() / agent.getSpeed())) / dist;
                if (timeForEdge < min) {
                    min = timeForEdge;
                }
            }
        }
        return min;
    }

    /**
     * allocate pokemons for every agent
     */
    private void allocateAgents() {
        for (Agent agent : agents.values()) {
            brain.allocate(agent);
            client.chooseNextEdge("{\"agent_id\":" + agent.getId() + ", \"next_node_id\":" + agent.getDest() + "}");
        }
    }


    /**
     * update the agents from the server
     */
    private void updateAgents() {
        HashMap<Integer, Agent> newAgents = Agent.load(client.getAgents());
        for (Agent agent : newAgents.values()) {
            this.agents.get(agent.getId()).setLocation(agent.getLocation());
            this.agents.get(agent.getId()).setDest(agent.getDest());
            this.agents.get(agent.getId()).setSrc(agent.getSrc());
            this.agents.get(agent.getId()).setSpeed(agent.getSpeed());
        }
    }

    /**
     * update the pokemons from the server
     */
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
