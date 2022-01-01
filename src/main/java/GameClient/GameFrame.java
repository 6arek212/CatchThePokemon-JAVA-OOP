//package GameClient;
//
//import javax.swing.*;
//
//package gameClient;
//
//
//import GameClient.utils.Range2Range;
//import api.*;
//import GameClient.utils.Point;
//import GameClient.utils.Range;
//import GameClient.utils.Range2D;
//
//import javax.imageio.ImageIO;
//import javax.swing.*;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.FileInputStream;
//import java.util.Iterator;
//import java.util.List;
//
///**
// * This class represents a very simple GUI class to present a
// * game on a graph - you are welcome to use this class - yet keep in mind
// * that the code is not well written in order to force you improve the
// * code and not to take it "as is".
// */
//public class MyFrame extends JFrame {
//    private GameWorld _ar;
//    private Client game;
//    private Range2Range _w2f;
//
//
//    MyFrame(String a) {
//        super(a);
//        setResizable(true);
//        setDefaultCloseOperation(EXIT_ON_CLOSE);
//    }
//
//    /**
//     * update the frame of the game
//     */
//    public void update(GameWorld ar) {
//
//        this._ar = ar;
//        updateFrame();
//    }
//
//    /**
//     * get the game that we design
//     */
//    public Client getGame() {
//        return game;
//    }
//
//    /**
//     * set the game that we will design it
//     */
//    public void setGame(Client game) {
//        this.game = game;
//    }
//
//    /**
//     * update the graph frame
//     */
//    private void updateFrame() {
//        Range rx = new Range(70, this.getWidth() - 70);
//        Range ry = new Range(this.getHeight() - 100, 150);
//        Range2D frame = new Range2D(rx, ry);
//        DirectedWeightedGraph g = _ar.getGraph();
//        _w2f = GameWorld.w2f(g, frame);
//    }
//
//    public void paintComponents(Graphics g) {
//        int w = this.getWidth();
//        int h = this.getHeight();
//        updateFrame();
//
//        g.clearRect(0, 0, w, h);
//
//        drawPokemons(g);
//        drawGraph(g);
//        drawAgants(g);
//
//        drawInfo(g);
//
//
//    }
//
//    public void paint(Graphics g) {
//        int w = this.getWidth();
//        int h = this.getHeight();
//        Image img = this.createImage(w, h);
//        Graphics gr = img.getGraphics();
//
//        paintComponents(gr);
//        g.drawImage(img, 0, 0, this);
//
//
//    }
//
//    private void drawInfo(Graphics g) {
//        List<String> str = _ar.get_info();
//        String dt = "none";
//        for (int i = 0; i < str.size(); i++) {
//            g.drawString(str.get(i) + " dt: " + dt, 100, 60 + i * 20);
//
//        }
//
//        drawTimer(g);
//
//
//    }
//
//    private void drawGraph(Graphics g) {
//        DirectedWeightedGraph gg = _ar.getGraph();
//        Iterator<NodeData> iter = gg.getV().iterator();
//
//        for()
//
//
//        while (iter.hasNext()) {
//            node_data n = iter.next();
//            g.setColor(Color.blue);
//            drawNode(n, 5, g);
//            Iterator<edge_data> itr = gg.getE(n.getKey()).iterator();
//            while (itr.hasNext()) {
//                edge_data e = itr.next();
//                g.setColor(Color.gray);
//                drawEdge(e, g);
//            }
//        }
//    }
//
//    private void drawPokemons(Graphics g) {
//        List<CL_Pokemon> fs = _ar.getPokemons();
//        if (fs != null) {
//            Iterator<CL_Pokemon> itr = fs.iterator();
//
//            while (itr.hasNext()) {
//
//                CL_Pokemon f = itr.next();
//                Point3D c = f.getLocation();
//                int r = 10;
//                BufferedImage sourceImage = null;
//                if (f.getType() < 0) {
//                    try {
//                        String s = "src/gameClient/orangepc.png";
//                        FileInputStream file = new FileInputStream(s);
//
//                        sourceImage = ImageIO.read(file);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    try {
//                        String s = "src/gameClient/greenpc.png";
//                        FileInputStream file = new FileInputStream(s);
//
//                        sourceImage = ImageIO.read(file);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    //g.setColor(Color.green);
//                }
//                if (c != null) {
//
//                    geo_location fp = this._w2f.world2frame(c);
//                    ;
//
//                    Image img = sourceImage.getScaledInstance(4 * r, 4 * r, Image.SCALE_SMOOTH);
//                    g.drawImage(img, (int) fp.x() - r, (int) fp.y() - r, this);
//                }
//            }
//        }
//    }
//
//    private void drawAgants(Graphics g) {
//        List<CL_Agent> rs = _ar.getAgents();
//        //Iterator<OOP_Point3D> itr = rs.iterator();
//        String[] columns = new String[]{
//                "Id", "Value"
//        };
//        Object[][] data = new Object[rs.size()][2];
//
//
//        g.setColor(Color.red);
//        int i = 0;
//        while (rs != null && i < rs.size()) {
//            geo_location c = rs.get(i).getLocation();
//            int r = 8;
//
//            BufferedImage sourceImage = null;
//            if (c != null) {
//                try {
//                    String s = "src/gameClient/agent1.png";
//                    FileInputStream file = new FileInputStream(s);
//
//                    sourceImage = ImageIO.read(file);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                Image img = sourceImage.getScaledInstance(3 * r, 5 * r, Image.SCALE_SMOOTH);
//
//                geo_location fp = this._w2f.world2frame(c);
//                g.drawImage(img, (int) fp.x() - r, (int) fp.y() - r, this);
//                String idd = "" + rs.get(i).getID();
//                g.drawString(idd, (int) fp.x(), (int) fp.y() - 3 * r);
//
//
//                data[i][0] = i;
//                data[i][1] = rs.get(i).getValue();
//                //g.fillOval((int)fp.x()-r, (int)fp.y()-r, 2*r, 2*r);
//                i++;
//            }
//        }
//
//
//        JTable table = new JTable(data, columns);
//        JScrollPane js = new JScrollPane(table);
//
//        this.getContentPane().add(js);
//        js.setVisible(true);
//    }
//
//    private void drawNode(NodeData n, int r, Graphics g) {
//        GeoLocation pos = n.getLocation();
//        GeoLocation fp = this._w2f.world2frame(pos);
//        g.fillOval((int) fp.x() - r, (int) fp.y() - r, 2 * r, 2 * r);
//        g.drawString("" + n.getKey(), (int) fp.x(), (int) fp.y() - 4 * r);
//    }
//
//    private void drawEdge(edge_data e, Graphics g) {
//        directed_weighted_graph gg = _ar.getGraph();
//        geo_location s = gg.getNode(e.getSrc()).getLocation();
//        geo_location d = gg.getNode(e.getDest()).getLocation();
//        geo_location s0 = this._w2f.world2frame(s);
//        geo_location d0 = this._w2f.world2frame(d);
//        g.drawLine((int) s0.x(), (int) s0.y(), (int) d0.x(), (int) d0.y());
//        //	g.drawString(""+n.getKey(), fp.ix(), fp.iy()-4*r);
//    }
//
//    public void drawTimer(Graphics g) {
//        g.setColor(Color.black);
//        g.setFont(new Font("Monospaced", Font.PLAIN, 15));
//        double time = Integer.parseInt(game.timeToEnd()) / 1000.0;
//        g.drawString("time to end: " + time, 10, this.getHeight() - 15);
//
//
//    }
//
//
//}
