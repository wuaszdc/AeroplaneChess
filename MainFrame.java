import model.ChessPiece;
import model.GameData;
import model.Player;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class MainFrame extends JFrame {
    JLayeredPane layeredPane;
    JPanel jp;
    GameData data;
    private int[] color;
    private int[] index;

    public GameController getController() {
        return controller;
    }

    GameController controller;


    public MainFrame() throws FileNotFoundException {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Image icon = new ImageIcon("src/pictures/icon.jfif").getImage();
        setIconImage(icon);
        layeredPane = new JLayeredPane();
        jp = new JPanel();
        JLabel label = new JLabel(new ImageIcon("src/pictures/background.jpg"));
        jp.setBounds(0, 0, 1400, 905);
        jp.add(label);
        layeredPane.add(jp, JLayeredPane.DEFAULT_LAYER);
        this.setLayeredPane(layeredPane);
        setSize(1400, 905);
        setLayout(null);
        JLabel text = new JLabel("飞行棋");
        Font f = new Font("楷体", Font.BOLD + Font.ITALIC, 60);
        text.setFont(f);
        text.setBounds(600, 0, 300, 100);
        layeredPane.add(text, JLayeredPane.PALETTE_LAYER);
        JButton button1 = new JButton("新游戏");
        Font font = new Font("楷体", Font.BOLD, 30);
        button1.setFont(font);
        layeredPane.add(button1, JLayeredPane.PALETTE_LAYER);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                dispose();
                System.setProperty("sun.java2d.win.uiScaleX", "96dpi");
                System.setProperty("sun.java2d.win.uiScaleY", "96dpi");
                data = new GameData();

                int i = JOptionPane.showConfirmDialog(null, "是否打开游戏声音？", "温馨提示", JOptionPane.YES_NO_OPTION);
                if (i == 0)
                    data.setPlayBGM(true);
                controller = new GameController(data);
                GameFrame mainFrame = new GameFrame(controller);
                controller.setGameFrame(mainFrame);
                controller.initializeGame();
                mainFrame.setIconImage(icon);
                mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                JButton button = new JButton("背景音乐");
                button.setFont(new Font("楷体", Font.BOLD, 26));
                button.setBounds(1015, 409, 150, 30);
                mainFrame.add(button);
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                                controller.setBgmThread();
                            }
                });
                mainFrame.setVisible(true);
            }
        });
        button1.setBounds(600, 100, 200, 100);
        JButton button2 = new JButton("读取存档");
        button2.setFont(font);
        layeredPane.add(button2, JLayeredPane.PALETTE_LAYER);
        button2.setBounds(600, 250, 200, 100);
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                FileSystemView fsv = FileSystemView.getFileSystemView();

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(fsv.getHomeDirectory());
                fileChooser.setDialogTitle("请选择要上传的文件...");
                fileChooser.setApproveButtonText("确定");
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                int result = fileChooser.showOpenDialog(null);
                String path = null;
                if (JFileChooser.APPROVE_OPTION == result) {
                    path=fileChooser.getSelectedFile().getPath();
                }
                boolean canReload = false;
                Scanner sc = null;
                try {
                    sc = new Scanner(new File(path));
                } catch (FileNotFoundException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                }
                int currentPlayer = sc.nextInt();
                int[] color = new int[16];
                int[] index = new int[16];
                for (int i = 0; i < 16; i++) {
                    if (!sc.hasNext())
                        break;
                    color[i] = sc.nextInt();
                    if (!sc.hasNext())
                        break;
                    index[i] = sc.nextInt();
                }
                if (!sc.hasNext())
                    canReload = true;
                if (canReload) {
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
                    mainFrame.setIconImage(icon);
                    mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                    JButton button = new JButton("背景音乐");
                    button.setFont(new Font("楷体", Font.BOLD, 26));
                    button.setBounds(1015, 409, 150, 30);
                    mainFrame.add(button);
                    button.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            controller.setBgmThread();
                        }
                    });
                    mainFrame.setVisible(true);
                    controller.setCurrentPlayer(currentPlayer);
                    for (int j = 0; j < 16; j++) {
                        mainFrame.setChess(mainFrame.getPieceList().get(j), color[j], index[j], 1);
                    }
                    for (ChessPiece a : mainFrame.getPieceList()) {
                        int number = 0;
                        for (ChessPiece b : mainFrame.getPieceList()) {
                            if (b.getColor() == a.getColor() && b.getIndex() == a.getIndex() && b.getPlayer() == a.getPlayer())
                                number++;
                        }
                        mainFrame.setChess(a, a.getColor(), a.getIndex(), number);
                    }
                } else
                    JOptionPane.showMessageDialog(null, "存档损坏");
            }
        });
        JButton button3 = new JButton("排行榜");
        button3.setFont(font);
        button3.setBounds(600, 400, 200, 100);
        layeredPane.add(button3, JLayeredPane.PALETTE_LAYER);
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                JFrame frame = new JFrame("排行榜");
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                Image icon = new ImageIcon("src/pictures/icon.jfif").getImage();
                frame.setIconImage(icon);
                layeredPane = new JLayeredPane();
                jp = new JPanel();
                JLabel label = new JLabel(new ImageIcon("src/pictures/background.jpg"));
                jp.setBounds(0, 0, 1400, 905);
                jp.add(label);
                layeredPane.add(jp, JLayeredPane.DEFAULT_LAYER);
                frame.setLayeredPane(layeredPane);
                frame.setSize(1400, 905);
                frame.setLayout(null);
                JTextField textField11 = new JTextField("排名");
                textField11.setEditable(false);
                Font font = new Font("楷体", Font.BOLD, 30);
                textField11.setFont(font);
                layeredPane.add(textField11, JLayeredPane.PALETTE_LAYER);
                textField11.setBounds(480, 40, 70, 60);
                JTextField textField12 = new JTextField("昵称");
                textField12.setEditable(false);
                textField12.setFont(font);
                layeredPane.add(textField12, JLayeredPane.PALETTE_LAYER);
                textField12.setBounds(550, 40, 200, 60);
                JTextField textField23 = new JTextField("单局吃子数");
                textField23.setEditable(false);
                textField23.setFont(font);
                layeredPane.add(textField23, JLayeredPane.PALETTE_LAYER);
                textField23.setBounds(750, 40, 170, 60);
                Player[] players = new Player[GameFrame.getPlayerArrayList().size()];
                for (int i = 0; i < GameFrame.getPlayerArrayList().size(); i++) {
                    players[i] = GameFrame.getPlayerArrayList().get(i);
                }
                Arrays.sort(players);
                for (int i = 0; i < 10 && i < players.length; i++) {
                    JTextField textField = new JTextField(String.format("%d", i + 1));
                    textField.setEditable(false);
                    textField.setFont(font);
                    layeredPane.add(textField, JLayeredPane.PALETTE_LAYER);
                    textField.setBounds(480, 100 + 60 * i, 70, 60);
                    JTextField textField1 = new JTextField(players[i].getName());
                    textField1.setEditable(false);
                    textField1.setFont(font);
                    layeredPane.add(textField1, JLayeredPane.PALETTE_LAYER);
                    textField1.setBounds(550, 100 + 60 * i, 200, 60);
                    JTextField textField2 = new JTextField(String.format("%d", players[i].getEatNumber()));
                    textField2.setEditable(false);
                    textField2.setFont(font);
                    layeredPane.add(textField2, JLayeredPane.PALETTE_LAYER);
                    textField2.setBounds(750, 100 + 60 * i, 170, 60);
                }

                JButton mainFrameButton = new JButton("主界面");
                mainFrameButton.setFont(new Font("楷体", Font.BOLD, 26));
                mainFrameButton.setBounds(625, 800, 150, 50);
                layeredPane.add(mainFrameButton, JLayeredPane.PALETTE_LAYER);
                mainFrameButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        frame.dispose();
                        MainFrame mainFrame = null;
                        try {
                            mainFrame = new MainFrame();
                        } catch (FileNotFoundException fileNotFoundException) {
                            fileNotFoundException.printStackTrace();
                        }
                        mainFrame.setVisible(true);
                    }
                });
                frame.setVisible(true);
            }
        });
        JButton button4 = new JButton("游戏规则");
        button4.setFont(font);
        button4.setBounds(600, 550, 200, 100);
        layeredPane.add(button4, JLayeredPane.PALETTE_LAYER);
        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                JFrame frame = new JFrame("游戏规则");
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                Image icon = new ImageIcon("src/pictures/icon.jfif").getImage();
                frame.setIconImage(icon);
                layeredPane = new JLayeredPane();
                jp = new JPanel();
                JLabel label = new JLabel(new ImageIcon("src/pictures/background.jpg"));
                jp.setBounds(0, 0, 1470, 905);
                jp.add(label);
                layeredPane.add(jp, JLayeredPane.DEFAULT_LAYER);
                frame.setLayeredPane(layeredPane);
                frame.setSize(1470, 905);
                frame.setLayout(null);

                ImageIcon imageIcon = new ImageIcon("src/pictures/游戏规则.png");
                JLabel jLabel = new JLabel(imageIcon);
                jLabel.setBounds(0,0, 1470, 698);
                layeredPane.add(jLabel, JLayeredPane.PALETTE_LAYER);

                JButton mainFrameButton = new JButton("主界面");
                mainFrameButton.setFont(new Font("楷体", Font.BOLD, 26));
                mainFrameButton.setBounds(625, 800, 150, 50);
                layeredPane.add(mainFrameButton, JLayeredPane.PALETTE_LAYER);
                mainFrameButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        frame.dispose();
                        MainFrame mainFrame = null;
                        try {
                            mainFrame = new MainFrame();
                        } catch (FileNotFoundException fileNotFoundException) {
                            fileNotFoundException.printStackTrace();
                        }
                        mainFrame.setVisible(true);
                    }
                });
                frame.setVisible(true);
            }
        });
    }

    public static void main(String[] args) throws FileNotFoundException {
        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}