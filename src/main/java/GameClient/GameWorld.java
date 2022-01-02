package GameClient;


import GameClient.utils.Range;
import GameClient.utils.Range2D;
import GameClient.utils.Range2Range;
import api.*;
import GameClient.utils.Point;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.*;


public class GameWorld {
    public static final double EPS1 = 0.001, EPS2 = EPS1 * EPS1;
    private DirectedWeightedGraph g;
    private List<Agent> agents;
    private List<Pokemon> pokemons;
    private List<String> info;


    public GameWorld() {

        info = new ArrayList<>();
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

    public List<String> get_info() {
        return info;
    }


    public static List<Agent> getAgents(String aa, DirectedWeightedGraph gg) {
        ArrayList<Agent> agents = new ArrayList<Agent>();
        try {
            JSONObject jsonObject = new JSONObject(aa);
            JSONArray ags = jsonObject.getJSONArray("Agents");
            for (int i = 0; i < ags.length(); i++) {
                Agent c = new Agent(gg, 0);
                c.update(ags.get(i).toString());
                agents.add(c);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return agents;
    }


    public static ArrayList<Pokemon> json2Pokemons(String fs) {
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
        boolean ans = false;
        double dist = srcPos.distance(destPos);
        double d1 = srcPos.distance(p) + p.distance(destPos);
        if (dist > d1 - EPS2) {
            ans = true;
        }
        return ans;

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
