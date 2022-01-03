package GameGui;

import GameClient.Client;
import GameClient.GameWorld;
import api.DirectedWeightedGraph;
import api.DirectedWeightedGraphAlgorithms;
import implementation.AlgorithmsImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameFrame extends JFrame {
    protected JRadioButton[] buttons;
    public GamePanel gamePanel;
    private JMenuBar menuBar;


    public GameFrame(GameWorld gameWorld) {


        gamePanel = new GamePanel(gameWorld);

        this.add(gamePanel);
        Image imageIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(("src\\main\\java\\GameGui\\tools\\pika.png"))).getImage();
        this.setTitle("Pokemon Game");
        this.setIconImage(imageIcon);
        this.setSize(1000, 700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setResizable(true);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

    }


}
