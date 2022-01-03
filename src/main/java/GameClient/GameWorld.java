package GameClient;


import GameClient.utils.Range;
import GameClient.utils.Range2D;
import GameClient.utils.Range2Range;
import api.*;
import GameClient.utils.Point;
import implementation.AlgorithmsImpl;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class GameWorld {
    public static final double EPS1 = 0.001 * 0.001;
    private DirectedWeightedGraph g;
    private List<Agent> agents;
    private List<Pokemon> pokemons;
    private String info;
    private long timeToend;


    public GameWorld() {

        info = "";
    }

    public void setTimeToend(long timeToend) {
        this.timeToend = timeToend;
    }

    public long getTimeToend() {
        return this.timeToend;
    }

    public void setPokemons(List<Pokemon> pokemons) {
        this.pokemons = pokemons;
    }

    public void setAgents(List<Agent> agents) {
        this.agents = agents;
    }

    public void setGraph(DirectedWeightedGraph g) {
        this.g = g;
    }

    public List<Agent> getAgents() {
        return agents;
    }

    public List<Pokemon> getPokemons() {
        return pokemons;
    }

    public DirectedWeightedGraph getGraph() {
        return g;
    }

    public String get_info() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }


    public static List<Agent> getAgents(String aa, DirectedWeightedGraph gg) {
        ArrayList<Agent> agents = new ArrayList<Agent>();
        try {
            JSONObject jsonObject = new JSONObject(aa);
            System.out.println(aa);
            JSONArray ags = jsonObject.getJSONArray("Agents");
            for (int i = 0; i < ags.length(); i++) {
                Agent c = new Agent(gg, 0);
                c.updateAgent(ags.get(i).toString());
                agents.add(c);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return agents;
    }


    public static ArrayList<Pokemon> fromJsonStringToPoks(String fs) {
        ArrayList<Pokemon> pokemons = new ArrayList<Pokemon>();
        try {
            JSONObject jsonObject = new JSONObject(fs);
            JSONArray ags = jsonObject.getJSONArray("Pokemons");
            for (int i = 0; i < ags.length(); i++) {
                JSONObject pp = ags.getJSONObject(i);
                JSONObject pk = pp.getJSONObject("Pokemon");
                int t = pk.getInt("type");
                double v = pk.getDouble("value");
                String p = pk.getString("pos");
                Pokemon pokemon = new Pokemon(new Point(p), t, v, 0, null);

                pokemons.add(pokemon);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return pokemons;
    }

    //finding pokemons edges
    public void updatePokemonsEdges(List<Pokemon> pokemons) {


        for (Iterator<NodeData> it = g.nodeIter(); it.hasNext(); ) {
            NodeData node = it.next();
            for (Iterator<EdgeData> iter = g.edgeIter(node.getKey()); iter.hasNext(); ) {
                EdgeData edge = iter.next();
                for (int i = 0; i < pokemons.size(); i++) {
                    Pokemon pokemon = pokemons.get(i);
                    if (CheckOnEdge(pokemon.getPos(), edge, pokemon.getType(), g)) {

                        pokemon.setEdge(edge);

                    }


                }


            }


        }


    }


    // load the json file
    public DirectedWeightedGraph fromJsonToGraph(String json) {
        DirectedWeightedGraphAlgorithms algo = new AlgorithmsImpl();
        fromJsonStringToFile(json);
        algo.load("A.json");
        return algo.getGraph();
    }

    // make json string(the server give to us) into json file
    private void fromJsonStringToFile(String json) {
        try {
            String str = (new JSONObject(json)).toString(4);
            File file = new File("C:\\Users\\97254\\IdeaProjects\\CatchThePokemon-OOP-Assignment-4-JAVA\\src\\main\\java\\data\\A.json");
            FileWriter myWriter = new FileWriter(file);
            myWriter.write(str);
            myWriter.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }


    //check if pokemon on edge with using distance equation
    private static boolean CheckOnEdge(GeoLocation p, EdgeData e, int type, DirectedWeightedGraph g) {
        int src = g.getNode(e.getSrc()).getKey();
        int dest = g.getNode(e.getDest()).getKey();
        if (type < 0 && dest > src) {
            return false;
        }
        if (type > 0 && src > dest) {
            return false;
        }
        GeoLocation srcPos = g.getNode(src).getLocation();
        GeoLocation destPos = g.getNode(dest).getLocation();
        //the dis of the edge
        double dist = srcPos.distance(destPos);
        //from the edge src to p + from p to edge dest
        double d1 = srcPos.distance(p) + p.distance(destPos);
        if (dist > d1 - EPS1) {
            return true;
        }
        return false;

    }

    // ----------------------Graph Range---------------------------
    private static Range2D GraphRange(DirectedWeightedGraph g) {
        Iterator<NodeData> itr = g.nodeIter();
        double x0 = 0, x1 = 0, y0 = 0, y1 = 0;
        boolean first = true;
        while (itr.hasNext()) {
            GeoLocation p = itr.next().getLocation();
            if (first) {
                x0 = p.x();
                x1 = x0;
                y0 = p.y();
                y1 = y0;
                first = false;
            } else {
                if (p.x() < x0) {
                    x0 = p.x();
                }
                if (p.x() > x1) {
                    x1 = p.x();
                }
                if (p.y() < y0) {
                    y0 = p.y();
                }
                if (p.y() > y1) {
                    y1 = p.y();
                }
            }
        }
        Range xr = new Range(x0, x1);
        Range yr = new Range(y0, y1);
        return new Range2D(xr, yr);
    }


    public static Range2Range w2f(DirectedWeightedGraph g, Range2D frame) {
        Range2D world = GraphRange(g);
        Range2Range ans = new Range2Range(world, frame);
        return ans;
    }

}
