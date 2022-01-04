import GameClient.Agent;
import GameClient.Client;
import GameClient.GameWorld;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Make sure the server is up
 *
 */
public class ServerDataParseTest {

    @Test
    public void testLoadAgents() {
        Client client = new Client();
        try {
            client.startConnection("127.0.0.1", 6666);
        } catch (IOException e) {
            e.printStackTrace();
        }
        GameWorld gameWorld = new GameWorld();
        List<Agent> agents = GameWorld.getAgents(client.getAgents(), gameWorld.fromJsonToGraphGame(client.getGraph()));
        assertFalse(agents.isEmpty());
    }


}
