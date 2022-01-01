package GameGui;

import GameClient.Agent;
import GameClient.GameWorld;
import GameClient.Pokemon;
import GameClient.utils.Range;
import GameClient.utils.Range2D;
import GameClient.utils.Range2Range;
import GameClient.utils.Point;
import api.*;
import implementation.AlgorithmsImpl;
import implementation.EdgeDataImpl;

import implementation.NodeDataImpl;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;

public class GamePanel extends JPanel {
    protected static DirectedWeightedGraph graph;
    private GameWorld gameWorld;

    private Range2Range WorldToFrame;
    private GameFrame frame;

    private static boolean isEnabled;
    private final int NODE_SIZE = 10; // need to be even
    private final int ARROW_SIZE = NODE_SIZE - 2;
    private final JCheckBox show_pokemons_values = new JCheckBox("pokemons values");
    private Image image, image2;

    GamePanel(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        graph = gameWorld.getGraph();
        this.setPreferredSize(new Dimension(700, 700));
        this.setFocusable(true);


        image = new ImageIcon(Toolkit.getDefaultToolkit().getImage(("C:\\Users\\97254\\IdeaProjects\\CatchThePokemon-OOP-Assignment-4-JAVA\\src\\main\\java\\GameGui\\ball.gif"))).getImage();
        image2 = new ImageIcon(Toolkit.getDefaultToolkit().getImage(("C:\\Users\\97254\\IdeaProjects\\CatchThePokemon-OOP-Assignment-4-JAVA\\src\\main\\java\\GameGui\\nezu.gif"))).getImage();



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
            for (Pokemon f : pokemons) {
                int val = (int) f.getValue();
                Point c = f.getPos();
                int r = (int) (0.04 * this.getHeight());
                g.setColor(Color.RED);
                g.setFont(new Font("Arial", Font.BOLD, 16));
                BufferedImage sourceImage = null;
                if (c != null) {
                    GeoLocation fp = WorldToFrame.worldToframe(c);

                    g.drawImage(image, (int) fp.x() - r, (int) fp.y() - r, 2 * r, 2 * r, this);
                    if (show_pokemons_values.isSelected())
                        g.drawString("" + val, (int) fp.x() - 2 * r, (int) fp.y() - 2 * r);
                }
            }
        }
    }


    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        updateFrame();
        Iterator<NodeData> iter = graph.nodeIter();
        while (iter.hasNext()) {
            NodeDataImpl n = (NodeDataImpl) iter.next();


            n.setNodeState(Color.RED);
            drawNode((NodeDataImpl) n, 7, g);
            Iterator<EdgeData> itr = graph.edgeIter(n.getKey());
            while (itr.hasNext()) {
                EdgeData e = itr.next();


                drawEdge(e, g);
            }
        }
        drawAgents(g2d);
        drawPokemons(g2d);


    }

    private void drawEdge(EdgeData e, Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        GeoLocation s = graph.getNode(e.getSrc()).getLocation();
        GeoLocation d = graph.getNode(e.getDest()).getLocation();
        GeoLocation s0 = this.WorldToFrame.worldToframe((Point) s);
        GeoLocation d0 = this.WorldToFrame.worldToframe((Point) d);
        Line2D line = new Line2D.Double((int) s0.x(), (int) s0.y(), (int) d0.x(), (int) d0.y());
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(Color.ORANGE);
        g2d.fill(line);
        g2d.draw(line);
        g.setColor(Color.yellow);
//        g.fillRect(((int) (s0.x() + 7 * d0.x()) / 8 - ARROW_SIZE / 2), (int) ((s0.y() + 7 * d0.y()) / 8 - ARROW_SIZE / 2), ARROW_SIZE, ARROW_SIZE);

        if (e.getWeight() != 0) {
            g2d.setFont(new Font("Dialog", Font.PLAIN, 10));
        }


    }

    private final Color[] _agents_colors = new Color[]{
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
            g.setColor(_agents_colors[ag.getId()]);
            List<NodeData> path = ag.getAgentCurrPath();
            for (int i = 0; i < path.size() - 1; i++) {

                EdgeData e = new EdgeDataImpl(path.get(i).getKey(), path.get(i + 1).getKey(), 0);
                drawEdge(e, g);
            }
            GeoLocation c = ag.getPos();
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
        g2d.setColor(n.getNodeState());
        n.setVisualNode(node);
        g2d.fill(node);
        g.setFont(new Font("David", Font.PLAIN, 15));
        g.drawString("" + n.getKey(), (int) fp.x(), (int) fp.y() - 4 * r);


    }


}
