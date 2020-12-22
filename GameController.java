import model.BGMPlayer;
import model.ChessPiece;
import model.GameData;
import model.TestMusic;
import util.RandomUtil;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GameController {
    String[] rankingList = new String[3];
    int ranking;

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    private int currentPlayer;
    private boolean moveForward = true;
    GameFrame gameFrame;

    public int getBattleColor() {
        return battleColor;
    }

    public int getBattleIndex() {
        return battleIndex;
    }

    private int battleColor;
    private int battleIndex;



    protected GameData data;

    private BGMPlayer bgmPlayer ;

    private Thread bgmThread ;

    private boolean isBattle;

    public void setRolledTime(int rolledTime) {
        this.rolledTime = rolledTime;
    }

    public void setGameFrame(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
    }

    public int getRolledTime() {
        return rolledTime;
    }

    private int rolledTime;


    public boolean isBattle() {
        return isBattle;
    }




    public GameController(GameData data) {
        currentPlayer = 0;
        rolledTime = 0;
        this.data = data;
    }


    public void initializeGame() {
        currentPlayer = 0;
        isBattle = false;
        openBGM();
    }

    public void openBGM() {
        bgmPlayer = new BGMPlayer(data, Integer.MAX_VALUE, "src/sounds/bgm.wav");
        bgmThread = new Thread(bgmPlayer);
        bgmThread.start();
    }

    /**
     * @return the bgmThread
     */
    public void setBgmThread() {
        bgmThread.stop();
    }

    /**
     * @param bgmThread the bgmThread to set
     */
    public void setBgmThread(Thread bgmThread) {
        this.bgmThread = bgmThread;
    }

    /**
     * @return the bgmPlayer
     */
    public BGMPlayer getBgmPlayer() {
        return bgmPlayer;
    }

    /**
     * @param bgmPlayer the bgmPlayer to set
     */
    public void setBgmPlayer(BGMPlayer bgmPlayer) {
        this.bgmPlayer = bgmPlayer;
    }

    public int rollDice() {
        if (rolledTime == 0 || rolledTime == 1) {
            rolledTime++;
            return RandomUtil.nextInt(1, 6);
        } else {
            return -1;
        }
    }


    public void nextPlayer() {
        rolledTime = 0;
        gameFrame.setSteps(-1);
        do {
            currentPlayer = (currentPlayer + 1) % 4;
        } while (gameFrame.getPlayerNames()[currentPlayer] == null);

        gameFrame.getSelectSteps().removeAllItems();
        gameFrame.getSelectSteps().setVisible(false);
        gameFrame.getSelectStepsButton().setVisible(false);

        gameFrame.remove(gameFrame.getLabel1());
        gameFrame.remove(gameFrame.getLabel2());

        gameFrame.setStatusLabel(String.format("[%s] Please roll the dice", gameFrame.getPlayerNames()[currentPlayer]));
        gameFrame.getPieceMove().clear();
        gameFrame.setMoreRollChance(0);
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public List<ChessPiece> findPiece(int color, int index) {
        List<ChessPiece> list = new ArrayList<>();
        for (ChessPiece a : gameFrame.getPieceList()) {
            if (a.getPlayer() == currentPlayer && a.getColor() == color && a.getIndex() == index)
                list.add(a);
        }
        return list;
    }


    public List<ChessPiece> attackerPieces(int color, int index) {
        List<ChessPiece> list = new ArrayList<>();
        for (ChessPiece piece : gameFrame.getPieceList()) {
            if (piece.getPlayer() == getCurrentPlayer() && piece.getColor() == color && piece.getIndex() == index)
                list.add(piece);
        }
        return list;
    }

    public List<ChessPiece> opponentPieces(int color, int index) {
        List<ChessPiece> list = new ArrayList<>();
        for (ChessPiece piece : gameFrame.getPieceList()) {
            if (piece.getPlayer() != getCurrentPlayer() && piece.getColor() == color && piece.getIndex() == index)
                list.add(piece);
        }
        return list;
    }


    //判断某个位置有无敌机并打回敌机
    public void checkAndAttackOpponentBack(int color, int index) {
        for (ChessPiece a : opponentPieces(color, index)) {
            gameFrame.showMessage(String.format("[%s] 被打回机库", gameFrame.getPlayerNames()[a.getPlayer()]));
            if (a.getPlayer() != currentPlayer) {
                gameFrame.setChess(a, a.getInitialColor(), a.getInitialIndex(), 1);
                gameFrame.getPlayers()[currentPlayer].setEatNumber(gameFrame.getPlayers()[currentPlayer].getEatNumber() + 1);
                TestMusic sound =new TestMusic("src/sounds/crash.wav");
                InputStream stream =new ByteArrayInputStream(sound.getSamples());
                sound.play(stream);
            }
        }
    }

    public void nextLocation(ChessPiece piece) {
        if (moveForward) {
            //check whether go to the center
            if (piece.getIndex() >= 17 && piece.getColor() == piece.getPlayer()) {
                int color = piece.getColor();
                int index = piece.getIndex();
                for (ChessPiece m : findPiece(color, index))
                    gameFrame.setChess(m, color, index + 1, 1);
                for (ChessPiece n : findPiece(piece.getColor(), piece.getIndex())) {
                    gameFrame.setChess(n, piece.getColor(), piece.getIndex(), gameFrame.numberOfChess(piece.getColor(), piece.getIndex()));
                }
            } else if (piece.getIndex() == 4) {
                int color = piece.getColor();
                int index = piece.getIndex();
                for (ChessPiece m : findPiece(color, index))
                    gameFrame.setChess(m, color - 1 < 0 ? 3 : color - 1, 8, 1);
                for (ChessPiece n : findPiece(piece.getColor(), piece.getIndex())) {
                    gameFrame.setChess(n, piece.getColor(), piece.getIndex(), gameFrame.numberOfChess(piece.getColor(), piece.getIndex()));
                }
            } else {
                int nextColor = piece.getColor() != 3 ? piece.getColor() + 1 : 0;
                final int[] indexOrder = {14, 11, 8, 5, 15, 12, 9, 6, 16, 13, 10, 7, 17};

                //find the index of the next location
                int nextIndex = 0;
                for (int i = 0; i < 13; i++) {
                    if (indexOrder[i] == piece.getIndex()) {
                        nextIndex = i != 12 ? indexOrder[i + 1] : indexOrder[0];
                    }
                }
                int color = piece.getColor();
                int index = piece.getIndex();
                for (ChessPiece m : findPiece(color, index))
                    gameFrame.setChess(m, nextColor, nextIndex, 1);
                for (ChessPiece n : findPiece(piece.getColor(), piece.getIndex())) {
                    gameFrame.setChess(n, piece.getColor(), piece.getIndex(), gameFrame.numberOfChess(piece.getColor(), piece.getIndex()));
                }
            }
        } else {
            //move backward
            if (piece.getIndex() > 17) {
                int color = piece.getColor();
                int index = piece.getIndex();
                for (ChessPiece m : findPiece(color, index))
                    gameFrame.setChess(m, color, index - 1, 1);
                for (ChessPiece n : findPiece(piece.getColor(), piece.getIndex())) {
                    gameFrame.setChess(n, piece.getColor(), piece.getIndex(), gameFrame.numberOfChess(piece.getColor(), piece.getIndex()));
                }
            } else {
                int nextColor = piece.getColor() != 0 ? piece.getColor() - 1 : 3;
                final int[] indexOrder = {17, 7, 10, 13, 16, 6, 9, 12, 15, 5, 8, 11, 14};

                //find the index of the next location
                int nextIndex = 0;
                for (int i = 0; i < 13; i++) {
                    if (indexOrder[i] == piece.getIndex()) {
                        nextIndex = i != 12 ? indexOrder[i + 1] : indexOrder[0];
                    }
                }
                int color = piece.getColor();
                int index = piece.getIndex();
                for (ChessPiece m : findPiece(color, index))
                    gameFrame.setChess(m, nextColor, nextIndex, 1);
                for (ChessPiece n : findPiece(piece.getColor(), piece.getIndex())) {
                    gameFrame.setChess(n, piece.getColor(), piece.getIndex(), gameFrame.numberOfChess(piece.getColor(), piece.getIndex()));
                }
            }
        }
    }

    public void setBattle(boolean battle) {
        isBattle = battle;
    }

    public void moveChessPiece(ChessPiece piece, int steps) throws InterruptedException {
        Runnable r = () -> {
            try {
                for (int i = 0; i < steps; i++) {
                    nextLocation(piece);
                    Thread.sleep(300);

                    if (piece.getIndex() == 23 && i != steps - 1) {
                        moveForward = false;
                    }
                }
                if (piece.getIndex() == 23){
                    TestMusic sound =new TestMusic("src/sounds/arrive.wav");
                    InputStream stream =new ByteArrayInputStream(sound.getSamples());
                    sound.play(stream);
                }
                moveForward = true;
                if (piece.getPlayer() == piece.getColor() && piece.getIndex() < 17) {
                    if (piece.getIndex() == 9) {
                        TestMusic sound =new TestMusic("src/sounds/jump.wav");
                        InputStream stream =new ByteArrayInputStream(sound.getSamples());
                        sound.play(stream);
                        checkAndAttackOpponentBack(piece.getColor(), piece.getIndex());
                        int color = piece.getColor();
                        int index = piece.getIndex();
                        for (ChessPiece m : findPiece(color, index)) {
                            gameFrame.setChess(m, color, 12, 1);
                        }
                        for (ChessPiece n : findPiece(piece.getColor(), piece.getIndex())) {
                            gameFrame.setChess(n, piece.getColor(), piece.getIndex(), gameFrame.numberOfChess(piece.getColor(), piece.getIndex()));
                            Thread.sleep(300);
                        }

                        checkAndAttackOpponentBack(piece.getPlayer() - 2 >= 0 ? piece.getPlayer() - 2 : piece.getPlayer() + 2, 20);
                        checkAndAttackOpponentBack(piece.getColor(), piece.getIndex());



                    } else {
                        TestMusic sound =new TestMusic("src/sounds/jump.wav");
                        InputStream stream =new ByteArrayInputStream(sound.getSamples());
                        sound.play(stream);
                        checkAndAttackOpponentBack(piece.getColor(), piece.getIndex());
                        int color = piece.getColor();
                        int index = piece.getIndex();
                        for (ChessPiece m : findPiece(color, index)) {
                            gameFrame.setChess(m, color, index + 1, 1);
                        }
                        for (ChessPiece n : findPiece(piece.getColor(), piece.getIndex())) {
                            gameFrame.setChess(n, piece.getColor(), piece.getIndex(), gameFrame.numberOfChess(piece.getColor(), piece.getIndex()));
                            Thread.sleep(300);
                        }


                        checkAndAttackOpponentBack(piece.getColor(), piece.getIndex());
                        if (piece.getIndex() == 9) {
                            checkAndAttackOpponentBack(piece.getColor(), piece.getIndex());
                            int color1 = piece.getColor();
                            int index1 = piece.getIndex();
                            for (ChessPiece m : findPiece(color1, index1)) {
                                gameFrame.setChess(m, color1, 12, 1);
                            }
                            for (ChessPiece n : findPiece(piece.getColor(), piece.getIndex())) {
                                gameFrame.setChess(n, piece.getColor(), piece.getIndex(), gameFrame.numberOfChess(piece.getColor(), piece.getIndex()));
                                Thread.sleep(300);
                            }

                            checkAndAttackOpponentBack(piece.getPlayer() - 2 >= 0 ? piece.getPlayer() - 2 : piece.getPlayer() + 2, 20);
                            checkAndAttackOpponentBack(piece.getColor(), piece.getIndex());

                        }
                    }

                } else {
                    if (opponentPieces(piece.getColor(), piece.getIndex()).size() != 0) {
                        battleColor = piece.getColor();
                        battleIndex = piece.getIndex();
                        gameFrame.setStatusLabel(String.format("BATTLE:%s  %d   VS   %d  %s", gameFrame.getPlayerNames()[currentPlayer], attackerPieces(piece.getColor(), piece.getIndex()).size(), opponentPieces(piece.getColor(), piece.getIndex()).size(), gameFrame.getPlayerNames()[opponentPieces(piece.getColor(), piece.getIndex()).get(0).getPlayer()]));
                        isBattle = true;
                        rolledTime = 0;
                        gameFrame.remove(gameFrame.getLabel1());
                        gameFrame.remove(gameFrame.getLabel2());
                    }
                }
                int index = 23;
                for (ChessPiece m : findPiece(currentPlayer, index)) {
                    m.setIcon(new ImageIcon(String.format("src/pictures/%s crown.png", gameFrame.getPlayerNames()[m.getPlayer()])));
                    m.setBounds(gameFrame.getGrid()[m.getInitialColor()][m.getInitialIndex()].getX(), gameFrame.getGrid()[m.getInitialColor()][m.getInitialIndex()].getY(), 29, 29);
                }

                if (judgeWin()) {
                    TestMusic sound =new TestMusic("src/sounds/victory.wav");
                    InputStream stream =new ByteArrayInputStream(sound.getSamples());
                    sound.play(stream);
                    gameFrame.showMessage(String.format("[%s] get No.%d", gameFrame.getPlayerNames()[currentPlayer], ++ranking));
                    rankingList[ranking - 1] = gameFrame.getPlayerNames()[currentPlayer];
                    gameFrame.getPlayerNames()[currentPlayer] = null;
                }
                if (checkGameOver()) {
                    gameFrame.showMessage(String.format("Game over!  Ranking:  No.1:%s  No.2:%s  No.3:%s", rankingList[0], rankingList[1], rankingList[2]));
                }
                if (!gameFrame.isMoreChance() && !isBattle) {
                    nextPlayer();
                }
                gameFrame.setMoreChance(false);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        Thread t = new Thread(r);
        t.start();
    }

    public boolean judgeWin() {
        for (ChessPiece b : gameFrame.getPieceList()) {
            if (b.getPlayer() == currentPlayer && b.getIndex() != 23)
                return false;
        }
        return true;
    }

    public boolean checkGameOver() {
        int finish = 0;
        for (String player : gameFrame.getPlayerNames()) {
            if (player == null) {
                finish++;
            }
        }
        return finish == 3;
    }

    public boolean onlyOnePlayer(int color, int index) {
        List<ChessPiece> list = findAllPieceOnGrid(color, index);
        int player = list.get(0).getPlayer();
        for (ChessPiece piece : list) {
            if (piece.getPlayer() != player)
                return false;
        }
        return true;
    }

    public List<ChessPiece> findAllPieceOnGrid(int color, int index) {
        List<ChessPiece> list = new ArrayList<>();
        for (ChessPiece piece : gameFrame.getPieceList()) {
            if (piece.getColor() == color && piece.getIndex() == index)
                list.add(piece);
        }
        return list;
    }
}
