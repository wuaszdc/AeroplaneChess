
import model.ChessPiece;
import model.Player;
import model.Square;
import model.TestMusic;

import java.io.*;
import java.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class GameFrame extends JFrame {
    private final JLabel statusLabel = new JLabel();


    public void setStatusLabel(String s) {
        statusLabel.setText(s);
    }

    private final GameController controller;

    private final JPanel panel;
    private int dice1;
    private int dice2;
    private int steps;
    private final JComboBox<Integer> selectSteps;
    private JLabel label1;
    private JLabel label2;
    private Handler handler;

    private static ArrayList<Player> playerArrayList = new ArrayList<>();

    public static ArrayList<Player> getPlayerArrayList() {
        return playerArrayList;
    }

    private Player[] players = new Player[4];

    public boolean isMoreChance() {
        return moreChance;
    }

    public void setMoreChance(boolean moreChance) {
        this.moreChance = moreChance;
    }

    private boolean moreChance = false;

    DiceSelectorComponent diceSelectorComponent;

    public void setMoreRollChance(int moreRollChance) {
        this.moreRollChance = moreRollChance;
    }

    private int moreRollChance = 0;

    public List<ChessPiece> getPieceMove() {
        return pieceMove;
    }

    private List<ChessPiece> pieceMove;

    public void setSteps(int steps) {
        this.steps = steps;
    }


    public List<ChessPiece> getPieceList() {
        return pieceList;
    }

    private final List<ChessPiece> pieceList = new ArrayList<>();
    JButton selectStepsButton = new JButton("select");


    public Square[][] getGrid() {
        return grid;
    }

    private final Square[][] grid = new Square[4][24];

    public String[] getPlayerNames() {
        return PLAYER_NAMES;
    }

    private final String[] PLAYER_NAMES = {"Yellow", "Blue", "Red", "Green"};

    public void saveGame() throws Exception {
        String s = JOptionPane.showInputDialog("请输入文件名");
        File file = new File("src/" +
                s +
                ".txt");
        FileWriter fw = new FileWriter(file);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fw.write(String.format("%d\r", controller.getCurrentPlayer()));
            for (ChessPiece piece : pieceList) {
                fw.write(String.format("%d %d\r", piece.getColor(), piece.getIndex()));
            }
            fw.flush();
            fw.close();
            JOptionPane.showMessageDialog(this, "存档成功");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void initialChessPiece(ChessPiece piece, int color, int index) {
        piece.setBounds(grid[color][index].getX(), grid[color][index].getY(), 29, 29);
        piece.setColor(color);
        piece.setIndex(index);
        panel.add(piece);
    }

    public void setChess(ChessPiece piece, int color, int index, int number) {
        piece.setIcon(new ImageIcon(String.format("src/pictures/%s %d %d.png", PLAYER_NAMES[piece.getPlayer()], grid[color][index].getDirection(), number)));
        piece.setBounds(grid[color][index].getX(), grid[color][index].getY(), 29, 29);
        piece.setColor(color);
        piece.setIndex(index);
    }

    private ChessPiece player1piece1;
    private ChessPiece player1piece2;
    private ChessPiece player1piece3;
    private ChessPiece player1piece4;
    private ChessPiece player2piece1;
    private ChessPiece player2piece2;
    private ChessPiece player2piece3;
    private ChessPiece player2piece4;
    private ChessPiece player3piece1;
    private ChessPiece player3piece2;
    private ChessPiece player3piece3;
    private ChessPiece player3piece4;
    private ChessPiece player4piece1;
    private ChessPiece player4piece2;
    private ChessPiece player4piece3;
    private ChessPiece player4piece4;

    public void showMessage(String s) {
        JOptionPane.showMessageDialog(this, s);
    }

    public int numberOfChess(int color, int index) {
        int number = 0;
        for (ChessPiece piece : pieceList) {
            if (piece.getPlayer() == controller.getCurrentPlayer() && piece.getColor() == color && piece.getIndex() == index)
                number++;
        }
        return number;
    }

    private class Handler extends MouseAdapter {
        ChessPiece piece;

        @Override
        public void mouseClicked(MouseEvent e) {
            if (!controller.checkGameOver()) {
                piece = (ChessPiece) e.getSource();
                if (controller.getRolledTime() == 2 && piece.getIndex() != 23) {
                    if (piece.getPlayer() == controller.getCurrentPlayer() && piece.getIndex() <= 3) {
                        if (dice1 == 6 || dice2 == 6) {


                            //先把棋子移动到出发点再把点上原有棋子图像更新
                            setChess(piece, piece.getColor(), 4, 1);
                            TestMusic sound = new TestMusic("src/sounds/landoff.wav");
                            InputStream stream = new ByteArrayInputStream(sound.getSamples());
                            sound.play(stream);
                            int number = numberOfChess(piece.getColor(), 4);
                            for (ChessPiece n : pieceList) {
                                if (n.getPlayer() == controller.getCurrentPlayer() && n.getIndex() == 4)
                                    setChess(n, piece.getColor(), 4, number);
                            }
                            remove(label1);
                            remove(label2);

                            if (dice1 + dice2 >= 10 && moreRollChance < 2 && !controller.judgeWin()) {
                                statusLabel.setText(String.format("[%s] One more chance!", PLAYER_NAMES[controller.getCurrentPlayer()]));
                                dice1 = dice2 = steps = -1;
                                selectSteps.removeAllItems();
                                selectSteps.setVisible(false);
                                selectStepsButton.setVisible(false);

                                controller.setRolledTime(0);
                                pieceMove.add(piece);
                                moreRollChance++;
                            } else
                                controller.nextPlayer();
                        }
                    } else if (piece.getPlayer() == controller.getCurrentPlayer() && steps != -1) {
                        try {
                            controller.moveChessPiece(piece, steps);
                            if (dice1 + dice2 >= 10 && moreRollChance < 2 && !controller.judgeWin()) {
                                statusLabel.setText(String.format("[%s] One more chance!", PLAYER_NAMES[controller.getCurrentPlayer()]));
                                moreChance = true;
                                dice1 = dice2 = steps = -1;
                                selectSteps.removeAllItems();
                                selectSteps.setVisible(false);
                                selectStepsButton.setVisible(false);
                                remove(label1);
                                remove(label2);
                                controller.setRolledTime(0);
                                for (ChessPiece a : pieceList) {
                                    if (a.getColor() == piece.getColor() && a.getIndex() == piece.getIndex())
                                        if (!pieceMove.contains(a))
                                            pieceMove.add(a);
                                }
                                moreRollChance++;
                            }
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                    }
                }
            }
        }
    }


    private boolean canMove(int player, int dice1, int dice2) {
        for (ChessPiece piece : pieceList) {
            if (piece.getPlayer() == player && piece.getIndex() > 3 && piece.getIndex() != 23)
                return true;
            else if (dice1 == 6 || dice2 == 6)
                return true;
        }
        return false;
    }

    private boolean judge() {
        for (ChessPiece piece : pieceList) {
            if (piece.getPlayer() == controller.getCurrentPlayer() && piece.getIndex() >= 4)
                return true;
        }
        return false;
    }

    public JComboBox<Integer> getSelectSteps() {
        return selectSteps;
    }

    public JButton getSelectStepsButton() {
        return selectStepsButton;
    }

    public void selectSteps() {
        if (judge()) {
            List<Integer> choice = new ArrayList<>();
            selectStepsButton.setVisible(true);
            choice.add(dice1);
            choice.add(dice2);
            if (dice1 + dice2 <= 12)
                choice.add(dice1 + dice2);

            choice.add(Math.abs(dice1 - dice2));
            if (dice1 * dice2 <= 12)
                choice.add(dice1 * dice2);
            int m = dice1;
            int n = dice2;
            if (n > m) {
                int a = m;
                m = n;
                n = a;
            }
            if (m % n == 0)
                choice.add(m / n);
            Collections.sort(choice);
            List<Integer> list = new ArrayList<>();
            for (Integer a : choice) {
                if (!list.contains(a)) {
                    list.add(a);
                }
            }
            Collections.sort(list);

            for (Integer a : list) {
                selectSteps.addItem(a);
            }
            selectSteps.setVisible(true);
            selectStepsButton.addActionListener((e) -> {
                steps = Integer.parseInt(Objects.requireNonNull(selectSteps.getSelectedItem()).toString());
                statusLabel.setText(String.format("[%s] You selected %d", PLAYER_NAMES[controller.getCurrentPlayer()], steps));
                selectStepsButton.setVisible(false);
                selectSteps.setVisible(false);
                if (steps == 0) {
                    if (dice1 + dice2 >= 10 && moreRollChance < 2) {
                        statusLabel.setText(String.format("[%s] One more chance!", PLAYER_NAMES[controller.getCurrentPlayer()]));
                        dice1 = dice2 = steps = -1;
                        moreChance = true;
                        selectSteps.removeAllItems();
                        remove(label1);
                        remove(label2);
                        controller.setRolledTime(0);
                        moreRollChance++;
                    } else {
                        dice1 = dice2 = steps = -1;
                        selectSteps.removeAllItems();
                        controller.nextPlayer();
                    }
                }
            });
        }

    }

    public void newGame() {
        controller.initializeGame();
        dice1 = dice2 = steps = -1;
        placeInitialPieces();
        statusLabel.setText(String.format("[%s] Please roll the dice", PLAYER_NAMES[controller.getCurrentPlayer()]));
    }

    public JLabel getLabel1() {
        return label1;
    }

    public JLabel getLabel2() {
        return label2;
    }

    public Handler getHandler() {
        return handler;
    }

    public Player[] getPlayers() {
        return players;
    }

    public GameFrame(GameController controller) {
        for (int i = 0; i < 4; i++) {
            String name = JOptionPane.showInputDialog("请输入您的昵称");
            Player player = new Player(name);
            playerArrayList.add(player);
            players[i] = player;
        }
        dice1 = dice2 = steps = -1;
        handler = new Handler();
        setTitle("Aeroplane Chess");
        setSize(1400, 905);
        setLocationRelativeTo(null);
        setLayout(null);
        this.controller = controller;

        ImageIcon picture = new ImageIcon("src/pictures/ChessBoard.jpg");
        JLabel chessBoard = new JLabel(picture);
        chessBoard.setBounds(0, 0, 852, 863);

        panel = (JPanel) this.getContentPane();
        panel.setOpaque(false);
        panel.setLayout(null);
        getLayeredPane().setLayout(null);
        getLayeredPane().add(chessBoard, new Integer(Integer.MIN_VALUE));
        statusLabel.setLocation(875, 651);
        statusLabel.setFont(statusLabel.getFont().deriveFont(18.0f));
        statusLabel.setSize(600, 20);
        add(statusLabel);
        statusLabel.setText(String.format("[%s] Please roll the dice", PLAYER_NAMES[controller.getCurrentPlayer()]));
        diceSelectorComponent = new DiceSelectorComponent();
        diceSelectorComponent.setLocation(870, 700);
        add(diceSelectorComponent);


        selectStepsButton.setLocation(1162, 750);
        selectStepsButton.setSize(90, 30);
        selectStepsButton.setVisible(false);
        selectSteps = new JComboBox<>();
        selectSteps.setLocation(1000, 750);
        selectSteps.setSize(80, 25);
        selectSteps.setVisible(false);
        add(selectSteps);
        add(selectStepsButton);


        JButton button = new JButton();
        button.setBounds(1162, 700, 55, 37);
        button.setFont(button.getFont().deriveFont(18.0f));

        ImageIcon ii = new ImageIcon("src/pictures/骰子.png");
        Image temp = ii.getImage().getScaledInstance(30, 30, ii.getImage().SCALE_DEFAULT);
        ii = new ImageIcon(temp);
        button.setIcon(ii);
        button.setToolTipText("Roll");
        add(button);

        button.addActionListener((e) -> {
            if (!controller.checkGameOver()) {
                if (!controller.isBattle()) {
                    if (diceSelectorComponent.isRandomDice()) {
                        if (controller.getRolledTime() == 0) {
                            dice1 = controller.rollDice();
                            label1 = new JLabel();
                            label1.setSize(100, 100);
                            label1.setLocation(885, 500);
                            add(label1);
                            Runnable r = () -> {
                                try {
                                    String s;
                                    for (int i = 0; i <= 3; i++) {
                                        s = "src/pictures/dice/dice_action_" + i + ".png";
                                        ImageIcon image = new ImageIcon(s);
                                        label1.setIcon(image);
                                        repaint();
                                        Thread.sleep(100);
                                    }
                                    s = "src/pictures/dice/dice_" + dice1 + ".png";
                                    ImageIcon image = new ImageIcon(s);
                                    label1.setIcon(image);
                                    repaint();
                                    Thread.sleep(100);
                                } catch (InterruptedException ignored) {

                                }
                            };
                            Thread t = new Thread(r);
                            t.start();
                            statusLabel.setText(String.format("[%s] Rolled a %c (%d), please roll the second dice",
                                    PLAYER_NAMES[controller.getCurrentPlayer()], '\u267F' + dice1, dice1));

                        } else if (controller.getRolledTime() == 1) {
                            dice2 = controller.rollDice();
                            label2 = new JLabel();
                            label2.setSize(100, 100);
                            label2.setLocation(1050, 500);
                            add(label2);
                            Runnable r1 = () -> {
                                try {
                                    String s;
                                    for (int i = 0; i <= 3; i++) {
                                        s = "src/pictures/dice/dice_action_" + i + ".png";
                                        ImageIcon image = new ImageIcon(s);
                                        label2.setIcon(image);
                                        repaint();
                                        Thread.sleep(100);
                                    }
                                    s = "src/pictures/dice/dice_" + dice2 + ".png";
                                    ImageIcon image = new ImageIcon(s);
                                    label2.setIcon(image);
                                    repaint();
                                    Thread.sleep(100);
                                } catch (InterruptedException ignored) {
                                }
                            };
                            Thread t1 = new Thread(r1);
                            t1.start();
                            statusLabel.setText(String.format("[%s] Rolled %c (%d) and %c (%d)",
                                    PLAYER_NAMES[controller.getCurrentPlayer()], '\u267F' + dice1, dice1, '\u267F' + dice2, dice2));
                            if (dice1 + dice2 >= 10 && moreRollChance == 2) {
                                showMessage("连续三次大于等于10，返回机库！");
                                for (ChessPiece p : pieceMove) {
                                    setChess(p, p.getInitialColor(), p.getInitialIndex(), 1);
                                }
                                controller.nextPlayer();
                            } else if (canMove(controller.getCurrentPlayer(), dice1, dice2))
                                selectSteps();
                        } else {
                            JOptionPane.showMessageDialog(this, String.format("You have already rolled %c (%d) and %c (%d)", '\u267F' + dice1, dice1, '\u267F' + dice2, dice2));
                        }
                    } else {
                        if (controller.getRolledTime() == 0) {
                            dice1 = (int) diceSelectorComponent.getSelectedDice();
                            label1 = new JLabel();
                            label1.setSize(100, 100);
                            label1.setLocation(885, 500);
                            add(label1);
                            Runnable r = () -> {
                                try {
                                    String s;
                                    for (int i = 0; i <= 3; i++) {
                                        s = "src/pictures/dice/dice_action_" + i + ".png";
                                        ImageIcon image = new ImageIcon(s);
                                        label1.setIcon(image);
                                        repaint();
                                        Thread.sleep(100);
                                    }
                                    s = "src/pictures/dice/dice_" + dice1 + ".png";
                                    ImageIcon image = new ImageIcon(s);
                                    label1.setIcon(image);
                                    repaint();
                                    Thread.sleep(100);
                                } catch (InterruptedException ignored) {

                                }
                            };
                            Thread t = new Thread(r);
                            t.start();
                            controller.setRolledTime(controller.getRolledTime() + 1);
                        } else if (controller.getRolledTime() == 1) {
                            dice2 = (int) diceSelectorComponent.getSelectedDice();
                            label2 = new JLabel();
                            label2.setSize(100, 100);
                            label2.setLocation(1050, 500);
                            add(label2);
                            Runnable r1 = () -> {
                                try {
                                    String s;
                                    for (int i = 0; i <= 3; i++) {
                                        s = "src/pictures/dice/dice_action_" + i + ".png";
                                        ImageIcon image = new ImageIcon(s);
                                        label2.setIcon(image);
                                        repaint();
                                        Thread.sleep(100);
                                    }
                                    s = "src/pictures/dice/dice_" + dice2 + ".png";
                                    ImageIcon image = new ImageIcon(s);
                                    label2.setIcon(image);
                                    repaint();
                                    Thread.sleep(100);
                                } catch (InterruptedException ignored) {

                                }
                            };
                            Thread t1 = new Thread(r1);
                            t1.start();
                            controller.setRolledTime(controller.getRolledTime() + 1);

                            if (dice1 + dice2 >= 10 && moreRollChance == 2) {
                                TestMusic sound = new TestMusic("src/sounds/defeat.wav");
                                InputStream stream = new ByteArrayInputStream(sound.getSamples());
                                sound.play(stream);
                                showMessage("连续三次大于等于10，返回机库！");
                                for (ChessPiece p : pieceMove) {
                                    setChess(p, p.getInitialColor(), p.getInitialIndex(), 1);
                                }
                                remove(label1);
                                remove(label2);
                                controller.nextPlayer();
                            } else if (canMove(controller.getCurrentPlayer(), dice1, dice2))
                                selectSteps();
                        } else {
                            JOptionPane.showMessageDialog(this, String.format("You have selected %d and %d", dice1, dice2));
                        }
                    }
                    if (controller.getRolledTime() == 2 && !canMove(controller.getCurrentPlayer(), dice1, dice2)) {
                        JOptionPane.showMessageDialog(this, "无可以移动的棋子");
                        if (dice1 + dice2 >= 10) {
                            moreRollChance++;
                            statusLabel.setText(String.format("[%s] One more chance!", PLAYER_NAMES[controller.getCurrentPlayer()]));
                            remove(label1);
                            remove(label2);
                            controller.setRolledTime(0);
                            dice1 = dice2 = steps = -1;
                        } else {
                            controller.nextPlayer();
                        }
                    }
                } else {
                    if (diceSelectorComponent.isRandomDice()) {
                        if (controller.getRolledTime() == 0) {
                            dice1 = controller.rollDice();
                            label1 = new JLabel();
                            label1.setSize(100, 100);
                            label1.setLocation(885, 500);
                            add(label1);
                            Runnable r = () -> {
                                try {
                                    String s;
                                    for (int i = 0; i <= 3; i++) {
                                        s = "src/pictures/dice/dice_action_" + i + ".png";
                                        ImageIcon image = new ImageIcon(s);
                                        label1.setIcon(image);
                                        repaint();
                                        Thread.sleep(100);
                                    }
                                    s = "src/pictures/dice/dice_" + dice1 + ".png";
                                    ImageIcon image = new ImageIcon(s);
                                    label1.setIcon(image);
                                    repaint();
                                    Thread.sleep(100);
                                } catch (InterruptedException ignored) {

                                }
                            };
                            Thread t = new Thread(r);
                            t.start();
                        } else if (controller.getRolledTime() == 1) {
                            dice2 = controller.rollDice();
                            label2 = new JLabel();
                            label2.setSize(100, 100);
                            label2.setLocation(1050, 500);
                            add(label2);
                            Runnable r1 = () -> {
                                try {
                                    String s;
                                    for (int i = 0; i <= 3; i++) {
                                        s = "src/pictures/dice/dice_action_" + i + ".png";
                                        ImageIcon image = new ImageIcon(s);
                                        label2.setIcon(image);
                                        repaint();
                                        Thread.sleep(100);
                                    }
                                    s = "src/pictures/dice/dice_" + dice2 + ".png";
                                    ImageIcon image = new ImageIcon(s);
                                    label2.setIcon(image);
                                    repaint();
                                    Thread.sleep(100);
                                } catch (InterruptedException ignored) {

                                }
                            };
                            Thread t1 = new Thread(r1);
                            t1.start();

                        }
                    } else {
                        if (controller.getRolledTime() == 0) {
                            dice1 = (int) diceSelectorComponent.getSelectedDice();
                            label1 = new JLabel();
                            label1.setSize(100, 100);
                            label1.setLocation(885, 500);
                            add(label1);
                            Runnable r = () -> {
                                try {
                                    String s;
                                    for (int i = 0; i <= 3; i++) {
                                        s = "src/pictures/dice/dice_action_" + i + ".png";
                                        ImageIcon image = new ImageIcon(s);
                                        label1.setIcon(image);
                                        repaint();
                                        Thread.sleep(100);
                                    }
                                    s = "src/pictures/dice/dice_" + dice1 + ".png";
                                    ImageIcon image = new ImageIcon(s);
                                    label1.setIcon(image);
                                    repaint();
                                    Thread.sleep(100);
                                } catch (InterruptedException ignored) {

                                }
                            };
                            Thread t = new Thread(r);
                            t.start();
                            controller.setRolledTime(controller.getRolledTime() + 1);
                        } else if (controller.getRolledTime() == 1) {
                            dice2 = (int) diceSelectorComponent.getSelectedDice();
                            label2 = new JLabel();
                            label2.setSize(100, 100);
                            label2.setLocation(1050, 500);
                            add(label2);
                            Runnable r1 = () -> {
                                try {
                                    String s;
                                    for (int i = 0; i <= 3; i++) {
                                        s = "src/pictures/dice/dice_action_" + i + ".png";
                                        ImageIcon image = new ImageIcon(s);
                                        label2.setIcon(image);
                                        repaint();
                                        Thread.sleep(100);
                                    }
                                    s = "src/pictures/dice/dice_" + dice2 + ".png";
                                    ImageIcon image = new ImageIcon(s);
                                    label2.setIcon(image);
                                    repaint();
                                    Thread.sleep(100);
                                } catch (InterruptedException ignored) {

                                }
                            };
                            Thread t1 = new Thread(r1);
                            t1.start();
                            controller.setRolledTime(controller.getRolledTime() + 1);
                        }
                    }
                    if (controller.getRolledTime() == 2) {
                        if (dice1 > dice2) {
                            JOptionPane.showMessageDialog(this, String.format("%d  :  %d", dice1, dice2));
                            setChess(controller.opponentPieces(controller.getBattleColor(), controller.getBattleIndex()).get(0),
                                    controller.opponentPieces(controller.getBattleColor(), controller.getBattleIndex()).get(0).getInitialColor(),
                                    controller.opponentPieces(controller.getBattleColor(), controller.getBattleIndex()).get(0).getInitialIndex(), 1);

                            players[controller.getCurrentPlayer()].setEatNumber(players[controller.getCurrentPlayer()].getEatNumber() + 1);
                            int number1 = 0;
                            int number2 = 0;
                            for (ChessPiece piece : pieceList) {
                                if (piece.getColor() == controller.getBattleColor() && piece.getIndex() == controller.getBattleIndex() && piece.getPlayer() == controller.getCurrentPlayer())
                                    number1++;
                                if (piece.getColor() == controller.getBattleColor() && piece.getIndex() == controller.getBattleIndex() && piece.getPlayer() != controller.getCurrentPlayer())
                                    number2++;
                            }
                            //重画棋子
                            for (ChessPiece piece : pieceList) {
                                if (piece.getColor() == controller.getBattleColor() && piece.getIndex() == controller.getBattleIndex() && piece.getPlayer() == controller.getCurrentPlayer())
                                    setChess(piece, piece.getColor(), piece.getIndex(), number1);
                                if (piece.getColor() == controller.getBattleColor() && piece.getIndex() == controller.getBattleIndex() && piece.getPlayer() != controller.getCurrentPlayer())
                                    setChess(piece, piece.getColor(), piece.getIndex(), number2);
                            }
                            TestMusic sound = new TestMusic("src/sounds/crash.wav");
                            InputStream stream = new ByteArrayInputStream(sound.getSamples());
                            sound.play(stream);

                        } else if (dice1 < dice2) {
                            JOptionPane.showMessageDialog(this, String.format("%d  :  %d", dice1, dice2));
                            setChess(controller.attackerPieces(controller.getBattleColor(), controller.getBattleIndex()).get(0),
                                    controller.attackerPieces(controller.getBattleColor(), controller.getBattleIndex()).get(0).getInitialColor(),
                                    controller.attackerPieces(controller.getBattleColor(), controller.getBattleIndex()).get(0).getInitialIndex(), 1);

                            int winner = 0;
                            for (ChessPiece piece : pieceList) {
                                if (piece.getPlayer() != controller.getCurrentPlayer() && piece.getColor() == controller.getBattleColor() && piece.getIndex() == controller.getBattleIndex()) {
                                    winner = piece.getPlayer();
                                }
                            }
                            players[winner].setEatNumber(players[winner].getEatNumber() + 1);
                            int number1 = 0;
                            int number2 = 0;
                            for (ChessPiece piece : pieceList) {
                                if (piece.getColor() == controller.getBattleColor() && piece.getIndex() == controller.getBattleIndex() && piece.getPlayer() == controller.getCurrentPlayer())
                                    number1++;
                                if (piece.getColor() == controller.getBattleColor() && piece.getIndex() == controller.getBattleIndex() && piece.getPlayer() != controller.getCurrentPlayer())
                                    number2++;
                            }
                            //重画棋子
                            for (ChessPiece piece : pieceList) {
                                if (piece.getColor() == controller.getBattleColor() && piece.getIndex() == controller.getBattleIndex() && piece.getPlayer() == controller.getCurrentPlayer())
                                    setChess(piece, piece.getColor(), piece.getIndex(), number1);
                                if (piece.getColor() == controller.getBattleColor() && piece.getIndex() == controller.getBattleIndex() && piece.getPlayer() != controller.getCurrentPlayer())
                                    setChess(piece, piece.getColor(), piece.getIndex(), number2);
                            }
                            TestMusic sound = new TestMusic("src/sounds/crash.wav");
                            InputStream stream = new ByteArrayInputStream(sound.getSamples());
                            sound.play(stream);

                        } else
                            JOptionPane.showMessageDialog(this, String.format("%d  :  %d   平局", dice1, dice2));
                        if (!controller.onlyOnePlayer(controller.getBattleColor(), controller.getBattleIndex()))
                            statusLabel.setText(String.format("BATTLE:%s  %d   VS   %d  %s", PLAYER_NAMES[controller.getCurrentPlayer()], controller.attackerPieces(controller.getBattleColor(), controller.getBattleIndex()).size(), controller.opponentPieces(controller.getBattleColor(), controller.getBattleIndex()).size(), PLAYER_NAMES[controller.opponentPieces(controller.getBattleColor(), controller.getBattleIndex()).get(0).getPlayer()]));
                    }

                    if (controller.getRolledTime() == 2) {
                        controller.setRolledTime(0);
                        remove(label1);
                        remove(label2);
                    }
                    if (controller.onlyOnePlayer(controller.getBattleColor(), controller.getBattleIndex())) {
                        controller.setBattle(false);
                        controller.nextPlayer();
                    }
                }

            } else {
                showMessage("Game is over!");
            }
        });

        pieceMove = new ArrayList<>();


        JButton newGameButton = new JButton();
        newGameButton.setFont(new Font("楷体", Font.BOLD, 26));
        newGameButton.setSize(150, 30);
        newGameButton.setText("新游戏");
        newGameButton.setLocation(1015, 109);
        add(newGameButton);
        newGameButton.addActionListener((e) -> newGame());

        JButton mainFrameButton = new JButton("主界面");
        mainFrameButton.setFont(new Font("楷体", Font.BOLD, 26));
        mainFrameButton.setBounds(1015, 209, 150, 30);
        add(mainFrameButton);
        mainFrameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                MainFrame mainFrame = null;
                try {
                    mainFrame = new MainFrame();
                } catch (FileNotFoundException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                }
                mainFrame.setVisible(true);
            }
        });

        JButton loadButton = new JButton("存档");
        loadButton.setFont(new Font("楷体", Font.BOLD, 26));
        loadButton.setBounds(1015, 309, 150, 30);
        add(loadButton);
        loadButton.addActionListener((e) -> {
            try {
                saveGame();
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        });

        initGrid();
        player1piece1 = new ChessPiece(0, "src/pictures/Yellow 0 1.png", 0, 0);
        initialChessPiece(player1piece1, 0, 0);
        panel.add(player1piece1);
        pieceList.add(player1piece1);
        player1piece1.addMouseListener(handler);
        player1piece2 = new ChessPiece(0, "src/pictures/Yellow 0 1.png", 0, 1);
        initialChessPiece(player1piece2, 0, 1);
        panel.add(player1piece2);
        pieceList.add(player1piece2);
        player1piece2.addMouseListener(handler);
        player1piece3 = new ChessPiece(0, "src/pictures/Yellow 0 1.png", 0, 2);
        initialChessPiece(player1piece3, 0, 2);
        panel.add(player1piece3);
        pieceList.add(player1piece3);
        player1piece3.addMouseListener(handler);
        player1piece4 = new ChessPiece(0, "src/pictures/Yellow 0 1.png", 0, 3);
        initialChessPiece(player1piece4, 0, 3);
        panel.add(player1piece4);
        pieceList.add(player1piece4);
        player1piece4.addMouseListener(handler);
        player2piece1 = new ChessPiece(1, "src/pictures/Blue 1 1.png", 1, 0);
        initialChessPiece(player2piece1, 1, 0);
        panel.add(player2piece1);
        pieceList.add(player2piece1);
        player2piece1.addMouseListener(handler);
        player2piece2 = new ChessPiece(1, "src/pictures/Blue 1 1.png", 1, 1);
        initialChessPiece(player2piece2, 1, 1);
        panel.add(player2piece2);
        pieceList.add(player2piece2);
        player2piece2.addMouseListener(handler);
        player2piece3 = new ChessPiece(1, "src/pictures/Blue 1 1.png", 1, 2);
        initialChessPiece(player2piece3, 1, 2);
        panel.add(player2piece3);
        pieceList.add(player2piece3);
        player2piece3.addMouseListener(handler);
        player2piece4 = new ChessPiece(1, "src/pictures/Blue 1 1.png", 1, 3);
        initialChessPiece(player2piece4, 1, 3);
        panel.add(player2piece4);
        pieceList.add(player2piece4);
        player2piece4.addMouseListener(handler);
        player3piece1 = new ChessPiece(2, "src/pictures/Red 2 1.png", 2, 0);
        initialChessPiece(player3piece1, 2, 0);
        panel.add(player3piece1);
        pieceList.add(player3piece1);
        player3piece1.addMouseListener(handler);
        player3piece2 = new ChessPiece(2, "src/pictures/Red 2 1.png", 2, 1);
        initialChessPiece(player3piece2, 2, 1);
        panel.add(player3piece2);
        pieceList.add(player3piece2);
        player3piece2.addMouseListener(handler);
        player3piece3 = new ChessPiece(2, "src/pictures/Red 2 1.png", 2, 2);
        initialChessPiece(player3piece3, 2, 2);
        panel.add(player3piece3);
        pieceList.add(player3piece3);
        player3piece3.addMouseListener(handler);
        player3piece4 = new ChessPiece(2, "src/pictures/Red 2 1.png", 2, 3);
        initialChessPiece(player3piece4, 2, 3);
        panel.add(player3piece4);
        pieceList.add(player3piece4);
        player3piece4.addMouseListener(handler);
        player4piece1 = new ChessPiece(3, "src/pictures/Green 3 1.png", 3, 0);
        initialChessPiece(player4piece1, 3, 0);
        panel.add(player4piece1);
        pieceList.add(player4piece1);
        player4piece1.addMouseListener(handler);
        player4piece2 = new ChessPiece(3, "src/pictures/Green 3 1.png", 3, 1);
        initialChessPiece(player4piece2, 3, 1);
        panel.add(player4piece2);
        pieceList.add(player4piece2);
        player4piece2.addMouseListener(handler);
        player4piece3 = new ChessPiece(3, "src/pictures/Green 3 1.png", 3, 2);
        initialChessPiece(player4piece3, 3, 2);
        panel.add(player4piece3);
        pieceList.add(player4piece3);
        player4piece3.addMouseListener(handler);
        player4piece4 = new ChessPiece(3, "src/pictures/Green 3 1.png", 3, 3);
        initialChessPiece(player4piece4, 3, 3);
        panel.add(player4piece4);
        pieceList.add(player4piece4);
        player4piece4.addMouseListener(handler);

        panel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.printf("%d,%d", e.getX(), e.getY());
                System.out.println();
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        Font font = new Font("楷体", Font.BOLD, 20);
        JTextField textField1 = new JTextField(players[0].getName());
        textField1.setEditable(false);
        textField1.setFont(font);
        textField1.setBounds(218, 19, 50, 50);
        panel.add(textField1);
        JTextField textField2 = new JTextField(players[1].getName());
        textField2.setEditable(false);
        textField2.setFont(font);
        textField2.setBounds(783, 215, 50, 50);
        panel.add(textField2);
        JTextField textField3 = new JTextField(players[2].getName());
        textField3.setEditable(false);
        textField3.setFont(font);
        textField3.setBounds(588, 781, 50, 50);
        panel.add(textField3);
        JTextField textField4 = new JTextField(players[3].getName());
        textField4.setEditable(false);
        textField4.setFont(font);
        textField4.setBounds(21, 586, 50, 50);
        panel.add(textField4);
    }

    public void initGrid() {
        grid[1][15] = new Square(179, 249, 0);
        grid[0][5] = new Square(133, 249, 0);
        grid[1][6] = new Square(647, 249, 0);
        grid[2][16] = new Square(693, 249, 0);

        grid[0][18] = new Square(134, 411, 0);
        grid[0][19] = new Square(180, 411, 0);
        grid[0][20] = new Square(226, 411, 0);
        grid[0][21] = new Square(272, 411, 0);
        grid[0][22] = new Square(319, 411, 0);
        grid[0][23] = new Square(373, 411, 0);

        grid[2][23] = new Square(454, 411, 2);
        grid[2][22] = new Square(507, 411, 2);
        grid[2][21] = new Square(553, 411, 2);
        grid[2][20] = new Square(599, 411, 2);
        grid[2][19] = new Square(645, 411, 2);
        grid[2][18] = new Square(691, 411, 2);

        grid[2][5] = new Square(692, 572, 2);
        grid[3][15] = new Square(646, 572, 2);
        grid[3][6] = new Square(178, 572, 2);
        grid[0][16] = new Square(132, 572, 2);

        grid[1][10] = new Square(505, 756, 2);
        grid[2][7] = new Square(458, 756, 2);
        grid[3][17] = new Square(412, 756, 2);
        grid[0][14] = new Square(365, 756, 2);
        grid[1][11] = new Square(319, 756, 2);

        grid[3][10] = new Square(321, 65, 0);
        grid[0][7] = new Square(367, 65, 0);
        grid[1][17] = new Square(414, 65, 0);
        grid[2][14] = new Square(460, 65, 0);
        grid[3][11] = new Square(507, 65, 0);

        grid[0][0] = new Square(70, 67, 0);
        grid[0][1] = new Square(143, 67, 0);
        grid[0][2] = new Square(143, 137, 0);
        grid[0][3] = new Square(70, 137, 0);
        grid[0][4] = new Square(20, 235, 0);

        grid[1][0] = new Square(687, 67, 1);
        grid[1][1] = new Square(757, 67, 1);
        grid[1][2] = new Square(757, 140, 1);
        grid[1][3] = new Square(687, 140, 1);
        grid[1][4] = new Square(589, 19, 1);

        grid[3][8] = new Square(85, 270, 0);
        grid[2][12] = new Square(229, 270, 0);
        grid[0][9] = new Square(598, 270, 0);
        grid[3][13] = new Square(742, 270, 1);

        grid[1][8] = new Square(742, 551, 2);
        grid[0][12] = new Square(598, 551, 2);
        grid[2][9] = new Square(227, 551, 2);
        grid[1][13] = new Square(84, 551, 3);

        grid[2][0] = new Square(683, 684, 2);
        grid[2][1] = new Square(756, 684, 2);
        grid[2][2] = new Square(756, 755, 2);
        grid[2][3] = new Square(683, 755, 2);
        grid[2][4] = new Square(804, 586, 2);

        grid[1][18] = new Square(412, 132, 1);
        grid[1][19] = new Square(412, 178, 1);
        grid[1][20] = new Square(412, 224, 1);
        grid[1][21] = new Square(412, 271, 1);
        grid[1][22] = new Square(412, 317, 1);
        grid[1][23] = new Square(412, 371, 1);

        grid[1][5] = new Square(577, 133, 1);
        grid[2][15] = new Square(577, 180, 1);
        grid[2][6] = new Square(577, 643, 1);
        grid[3][16] = new Square(577, 690, 1);

        grid[0][10] = new Square(759, 320, 1);
        grid[1][7] = new Square(759, 366, 1);
        grid[2][17] = new Square(759, 411, 1);
        grid[3][14] = new Square(759, 457, 1);
        grid[0][11] = new Square(759, 503, 1);

        grid[0][8] = new Square(556, 83, 1);
        grid[3][12] = new Square(556, 229, 1);
        grid[1][9] = new Square(556, 593, 1);
        grid[0][13] = new Square(556, 739, 2);

        grid[2][8] = new Square(270, 739, 3);
        grid[1][12] = new Square(270, 593, 3);
        grid[3][9] = new Square(270, 229, 3);
        grid[2][13] = new Square(270, 83, 0);

        grid[3][5] = new Square(249, 689, 3);
        grid[0][15] = new Square(249, 642, 3);
        grid[0][6] = new Square(249, 179, 3);
        grid[1][16] = new Square(249, 132, 3);

        grid[2][10] = new Square(67, 502, 3);
        grid[3][7] = new Square(67, 456, 3);
        grid[0][17] = new Square(67, 411, 3);
        grid[1][14] = new Square(67, 366, 3);
        grid[2][11] = new Square(67, 320, 3);

        grid[3][18] = new Square(413, 691, 3);
        grid[3][19] = new Square(413, 644, 3);
        grid[3][20] = new Square(413, 598, 3);
        grid[3][21] = new Square(413, 552, 3);
        grid[3][22] = new Square(413, 506, 3);
        grid[3][23] = new Square(413, 452, 3);

        grid[3][0] = new Square(69, 681, 3);
        grid[3][1] = new Square(139, 681, 3);
        grid[3][2] = new Square(139, 754, 3);
        grid[3][3] = new Square(69, 754, 3);
        grid[3][4] = new Square(241, 817, 3);
    }

    public void placeInitialPieces() {
        for (ChessPiece piece : pieceList) {
            setChess(piece, piece.getInitialColor(), piece.getInitialIndex(), 1);
        }
    }
}