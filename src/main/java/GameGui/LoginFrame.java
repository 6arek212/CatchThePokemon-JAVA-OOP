package GameGui;

import api.EdgeData;
import api.NodeData;
import implementation.NodeDataImpl;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * This class represent an opening menu for the game.
 * The menu allows to enter ID number and select the stage at which you want to play.
 */
public class LoginFrame extends JFrame implements ActionListener {

    private static JLabel idLab;
    private static JTextField idField;
    private static JButton loginButton;
    private static ImageIcon logo;
    public boolean isTurn = true;
    public int id;
    public ImageIcon imageIcon ;
    public Image backRounder;

    public void Login(){
        JFrame frame = new JFrame();

        backRounder = new ImageIcon(Toolkit.getDefaultToolkit().getImage(("src\\main\\java\\GameGui\\tools\\pika.png"))).getImage();
        imageIcon = new ImageIcon("src\\main\\java\\GameGui\\tools\\login2.png");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setBackground(Color.yellow);
        frame.add(panel);
        frame.setLocationRelativeTo(null);
        panel.setLayout(null);
        idLab = new JLabel("Enter ID:");
        idLab.setBounds(15, 15, 80, 25);
        panel.add(idLab);
        idField = new JTextField(20);
        idField.setBounds(90, 10, 160, 25);
        idField.setPreferredSize(new Dimension(200,200));
        panel.add(idField);
        loginButton = new JButton("Play");
        loginButton.setBounds(110, 90, 300, 100);
        loginButton.setHorizontalTextPosition(JButton.LEFT);
        loginButton.setFont(new Font("Comic Sans" , Font.ITALIC , 15));
        loginButton.setIcon(imageIcon);
        loginButton.addActionListener(new LoginFrame());
        panel.add(loginButton);
        loginButton.addActionListener(this);
        frame.setIconImage(backRounder);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.id = Integer.parseInt(idField.getText());
        isTurn = false;

    }
}