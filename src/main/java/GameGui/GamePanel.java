package GameGui;

import GameClient.*;
import GameClient.utils.Range;
import GameClient.utils.Range2D;
import GameClient.utils.Range2Range;
import GameClient.utils.Point;
import api.*;
import implementation.NodeDataImpl;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class GamePanel extends JPanel {
    protected static DirectedWeightedGraph graph;
    private GameWorld gameWorld;
    private Range2Range WorldToFrame;
    private Image image, image2, image3, BackRoundImage;
    private int time, duration = -1, grade, moves, level;
    private JLabel InfoLabel = new JLabel();
    private JButton closeButton = new JButton();

    GamePanel(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        graph = gameWorld.getGraph();
        this.setPreferredSize(new Dimension(1000, 700));
        this.setFocusable(true);
        closeButton.setPreferredSize(new Dimension(100, 20));
        closeButton.setText("Stop Game");
        closeButton.addActionListener(e->{GameRunner.game.stop(); System.exit(0);});
        this.add(InfoLabel);
        this.add(closeButton);
        image = new ImageIcon(Toolkit.getDefaultToolkit().getImage(("src\\main\\java\\GameGui\\tools\\ball.gif"))).getImage();
        image2 = new ImageIcon(Toolkit.getDefaultToolkit().getImage(("src\\main\\java\\GameGui\\tools\\nezu.gif"))).getImage();
        image3 = new ImageIcon(Toolkit.getDefaultToolkit().getImage(("src\\main\\java\\GameGui\\tools\\pika.png"))).getImage();
    }

    private void updateFrame() {
        Range rx = new Range(70, this.getWidth() - 70);
        Range ry = new Range(this.getHeight() - 100, 150);
        Range2D frame = new Range2D(rx, ry);
        WorldToFrame = GameWorld.w2f(graph, frame);

    }

    private void drawPokemons(Graphics2D g) {
        List<Pokemon> pokemons = gameWorld.getPokemons();
        if (pokemons != null) {
            for (Pokemon pokemon : pokemons) {
                int val = (int) pokemon.getValue();
                Point point = pokemon.getPos();
                int r = (int) (0.04 * this.getHeight());
                g.setColor(Color.RED);
                g.setFont(new Font("Arial", Font.BOLD, 16));
                BufferedImage sourceImage = null;
                if (point != null) {
                    GeoLocation fp = WorldToFrame.worldToframe(point);

                    g.drawImage(image, (int) fp.x() - r, (int) fp.y() - r, 2 * r, 2 * r, this);
                }
            }
        }
    }


    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;


        BackRoundImage = new ImageIcon(Toolkit.getDefaultToolkit().getImage(("src\\main\\java\\GameGui\\tools\\back2.jpg"))).getImage();
        updateFrame();
        g2d.drawImage(BackRoundImage, 0, 0, this.getWidth(), this.getHeight(), this);
        for (Iterator<EdgeData> it = graph.edgeIter(); it.hasNext(); ) {
            EdgeData edgeData = it.next();
            g.setColor(Color.BLACK);
            drawEdge(edgeData, g);

        }

        for (Iterator<NodeData> it = graph.nodeIter(); it.hasNext(); ) {
            NodeData nodeData = it.next();
            drawNode((NodeDataImpl) nodeData, 15, g);


        }

        gameData();
        InfoLabel.setText("Level: " + level + "   Timer: " + time + "/" + duration + "   Grade: " + grade + "     Moves: " + moves + "/" + duration * 10 + "");
        drawAgents(g2d);
        drawPokemons(g2d);


    }

    private void gameData() {
        JSONObject infoObject;
        try {
            infoObject = new JSONObject(gameWorld.get_info());
            JSONObject ttt = infoObject.getJSONObject("GameServer");
            grade = (int) ttt.getDouble("grade");
            moves = (int) ttt.getDouble("moves");
            level = ttt.getInt("game_level");
            time = (int) gameWorld.getTimeToend();
            if (duration == -1)
                duration = time + 1;

        } catch (JSONException | NumberFormatException e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }

    private void drawEdge(EdgeData e, Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        GeoLocation s = graph.getNode(e.getSrc()).getLocation();
        GeoLocation d = graph.getNode(e.getDest()).getLocation();
        GeoLocation s0 = this.WorldToFrame.worldToframe((Point) s);
        GeoLocation d0 = this.WorldToFrame.worldToframe((Point) d);
        Line2D line = new Line2D.Double((int) s0.x(), (int) s0.y(), (int) d0.x(), (int) d0.y());
        g2d.setStroke(new BasicStroke(5));
        g2d.fill(line);
        g2d.draw(line);


        if (e.getWeight() != 0) {
            g2d.setFont(new Font("Dialog", Font.PLAIN, 10));
        }


    }

    private final Color[] colors = new Color[]{
            new Color(255, 0, 221),
            new Color(27, 255, 0),
            new Color(0, 255, 247),
            new Color(255, 0, 0),
            new Color(23, 4, 68),
            new Color(23, 4, 68)
    };

    private void drawAgents(Graphics2D g) {
        List<Agent> rs = gameWorld.getAgents();

        g.setStroke(new BasicStroke(3));
        for (Agent ag : rs) {
            g.setColor(colors[ag.getId()]);
            GeoLocation c = ag.getPos();
            Pokemon pokemon = gameWorld.getPokemons().get(ag.getCurrPok());
            int r = (int) (0.03 * this.getHeight());
            if (c != null) {
                GeoLocation fp = WorldToFrame.worldToframe((Point) c);

                g.drawImage(image2, (int) fp.x() - r, (int) fp.y() - r, 2 * r, 2 * r, this);
                g.drawString("" + ag.getId(), (int) fp.x() - 2 * r, (int) fp.y() - 2 * r);

            }
        }
    }


    private void drawNode(NodeDataImpl n, int r, Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        GeoLocation pos = n.getLocation();
        GeoLocation fp = this.WorldToFrame.worldToframe((Point) pos);
        Shape node = new Ellipse2D.Double((int) fp.x() - r, (int) fp.y() - r, 2 * r, 2 * r);
        g.drawImage(image3, (int) fp.x() - r, (int) fp.y() - r, 2 * r, 2 * r, this);
        g.setFont(new Font("David", Font.PLAIN, 15));
        g.drawString("" + n.getKey(), (int) fp.x(), (int) fp.y() - r);

    }

}
