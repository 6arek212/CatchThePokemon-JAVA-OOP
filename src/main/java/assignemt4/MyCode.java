package assignemt4; /**
 * @author AchiyaZigi
 * A trivial example for starting the server and running all needed commands
 */

import assignemt4.api.DirectedWeightedGraph;
import assignemt4.ex4_java_client.Client;
import assignemt4.impl.AlgorithmsImpl;
import assignemt4.impl.DirectedWeightedGraphImpl;
import assignemt4.json_models.DirectedWeightedGraphJson;
import assignemt4.ui.GraphViewFrame;

import java.io.IOException;
import java.util.Scanner;

public class MyCode {
    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.startConnection("127.0.0.1", 6666);
        } catch (IOException e) {
            e.printStackTrace();
        }
        GraphViewFrame view = new GraphViewFrame(client);
    }
}
