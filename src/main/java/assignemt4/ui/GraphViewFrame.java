package assignemt4.ui;


import assignemt4.api.DirectedWeightedGraphAlgorithms;
import assignemt4.ex4_java_client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GraphViewFrame extends JFrame {

    private final static int WIDTH = 1000;
    private final static int HEIGHT = 700;

    private JLabel numberOfNodes;
    private JLabel numberOfEdges;
    private GraphViewPanel panel;

    public GraphViewFrame(Client client) {
        this.panel = new GraphViewPanel( client);
        panel.setBackground(Color.BLACK);
        //initLabels();
        add(panel);
        initJframe();
    }


    /**
     * initialize JFrame
     */
    private void initJframe() {
        this.setTitle("Graph");
        this.setSize(WIDTH, HEIGHT);
        this.setLocationRelativeTo(null);
        this.setBackground(Color.BLACK);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(true);
        this.setLocationRelativeTo(null);
    }


    private void initLabels() {
        numberOfNodes = new JLabel();
        numberOfEdges = new JLabel();
        numberOfNodes.setBounds(16, 16, GraphViewPanel.padding + 16, 10);
        numberOfEdges.setBounds(16, 32, GraphViewPanel.padding + 16, 20);
        numberOfEdges.setForeground(Color.WHITE);
        numberOfNodes.setForeground(Color.WHITE);
        add(numberOfEdges);
        add(numberOfNodes);
    }




}
