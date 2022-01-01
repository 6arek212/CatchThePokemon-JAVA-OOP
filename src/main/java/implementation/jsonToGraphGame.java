package implementation;


import GameClient.utils.Point;
import api.DirectedWeightedGraph;
import api.NodeData;
import com.google.gson.*;


import java.lang.reflect.Type;


public class jsonToGraphGame implements JsonDeserializer<DirectedWeightedGraph> {
    @Override
    public DirectedWeightedGraph deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject graphJson = jsonElement.getAsJsonObject();
        DirectedWeightedGraph g = new DirectedWeightedGraphImpl();
        JsonArray jsonNodes = graphJson.get("Nodes").getAsJsonArray();
        JsonArray edgesjson = graphJson.get("Edges").getAsJsonArray();
        for (int i = 0; i < jsonNodes.size(); i++) {
            JsonObject nodej = jsonNodes.get(i).getAsJsonObject();
            int key = nodej.get("id").getAsInt();

            String pos = nodej.getAsJsonObject().get("pos").getAsString();
            String[] p = pos.split(",");
            double x = Double.parseDouble(p[0]);
            double y = Double.parseDouble(p[1]);
            double z = Double.parseDouble(p[2]);
            Point poi = new Point(x, y, z);
            NodeData n = new NodeDataImpl(key, poi);
            g.addNode(n);

        }
        for (int i = 0; i < edgesjson.size(); i++) {
            JsonObject edge = edgesjson.get(i).getAsJsonObject();
            int src = edge.getAsJsonObject().get("src").getAsInt();
            int dest = edge.getAsJsonObject().get("dest").getAsInt();
            double w = edge.getAsJsonObject().get("w").getAsDouble();
            g.connect(src, dest, w);
        }

        return g;
    }
}
