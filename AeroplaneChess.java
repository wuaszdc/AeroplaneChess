import model.GameData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class AeroplaneChess {
    public static void main(String[] args) {
        System.setProperty("sun.java2d.win.uiScaleX", "96dpi");
        System.setProperty("sun.java2d.win.uiScaleY", "96dpi");
        GameData data = new GameData();

        int i = JOptionPane.showConfirmDialog(null, "是否打开游戏声音？", "温馨提示", JOptionPane.YES_NO_OPTION);
        if (i == 0)
            data.setPlayBGM(true);
        GameController controller = new GameController(data);
        GameFrame mainFrame = new GameFrame(controller);
        controller.setGameFrame(mainFrame);
        controller.initializeGame();

        Image icon = new ImageIcon("src/pictures/icon.jfif").getImage();
        mainFrame.setIconImage(icon);

        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }
}
