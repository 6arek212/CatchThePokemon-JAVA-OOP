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
        this.setTitle("Graph");
        this.setSize(1000, 700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
//        setComponents();
        this.setResizable(true);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

    }

//    private void setComponents() {
//        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));
//        this.menuBar = new JMenuBar();
//        menuBar.setMaximumSize(new Dimension(150, 40));
//        this.setJMenuBar(menuBar);
//
//
//    }


}
