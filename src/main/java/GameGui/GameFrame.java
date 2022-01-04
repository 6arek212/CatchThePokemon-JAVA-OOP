package GameGui;


import GameClient.GameWorld;
import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    public GamePanel gamePanel;


    public GameFrame(GameWorld gameWorld) {
        gamePanel = new GamePanel(gameWorld);
        this.add(gamePanel);
        Image imageIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(("src\\main\\java\\GameGui\\tools\\pika.png"))).getImage();
        this.setTitle("Pokemon Game");
        this.setIconImage(imageIcon);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setResizable(true);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

    }


}
