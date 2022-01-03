package GameGui;

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

/**
 * This class represent an opening menu for the game.
 * The menu allows to enter ID number and select the stage at which you want to play.
 */
public class LoginFrame extends JFrame implements ActionListener {

    private static JLabel idLabel;
    private static JTextField idField;
    private static JButton lodinButton;
    private static ImageIcon logo;
    public boolean isTurn = true;
    public int id;
    public ImageIcon imageIcon ;
    public Image backRounder;

    public void Login(){
        JFrame frame = new JFrame();
        backRounder = new ImageIcon(Toolkit.getDefaultToolkit().getImage(("src\\main\\java\\GameGui\\tools\\pika.png"))).getImage();

        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        frame.add(panel);
//        frame.setBackground(Color.yellow);
        frame.setLocationRelativeTo(null);
        panel.setLayout(null);
        idLabel = new JLabel("Enter ID:");
        idLabel.setBounds(15, 15, 80, 25);
        panel.add(idLabel);
        idField = new JTextField(20);
        idField.setBounds(90, 10, 160, 25);
        idField.setPreferredSize(new Dimension(200,200));
        panel.add(idField);
        lodinButton = new JButton("Login");
       imageIcon = new ImageIcon("src\\main\\java\\GameGui\\tools\\login.png");
       lodinButton.setIcon(imageIcon);
        lodinButton.setBounds(110, 90, 100, 40);
        lodinButton.addActionListener(new LoginFrame());
        panel.add(lodinButton);
        lodinButton.addActionListener(this);
        frame.setIconImage(backRounder);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.id = Integer.parseInt(idField.getText());
        isTurn = false;

    }
}