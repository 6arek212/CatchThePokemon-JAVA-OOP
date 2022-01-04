import GameClient.GameRunner;
import GameGui.LoginFrame;

public class Ex4 {
    public static void main(String[] args) {
        GameRunner start;
        if (args.length == 1) {
            start = new GameRunner(Integer.parseInt(args[0]));
        } else {
            LoginFrame login = new LoginFrame();
            login.Login();
            while (login.isTurn) {
                System.out.print("");
            }
            start = new GameRunner(login.id);
        }
        Thread GameRun = new Thread(start);
        GameRun.start();
    }
}
