package org.cis1200.checkers;

/**
 * CIS 120 HW09 - Checkers Demo
 * (c) University of Pennsylvania
 * Created by Bayley Tuch, Sabrina Green, and Nicolas Corona in Fall 2020.
 */

import java.io.*;
import java.util.*;

/**
 * This class is a model for Checkers.
 * <p>
 * This game adheres to a Model-View-Controller design framework.
 * This framework is very effective for turn-based games. We
 * STRONGLY recommend you review these lecture slides, starting at
 * slide 8, for more details on Model-View-Controller:
 * https://www.seas.upenn.edu/~cis120/current/files/slides/lec36.pdf
 * <p>
 * This model is completely independent of the view and controller.
 * This is in keeping with the concept of modularity! We can play
 * the whole game from start to finish without ever drawing anything
 * on a screen or instantiating a Java Swing object.
 * <p>
 * Run this file to see the main method play a game of Checkers,
 * visualized with Strings printed to the console.
 */
public class Checkers {

    private int[][] board;
    private boolean blackPlayer;
    private boolean gameOver;
    private int[] pieceSelected = null;
    private int numBlack;
    private int numWhite;
    private Map<String, List<Integer>> rightUpJumpsPossible;
    private Map<String, List<Integer>> rightDownJumpsPossible;
    private Map<String, List<Integer>> leftUpJumpsPossible;
    private Map<String, List<Integer>> leftDownJumpsPossible;

    private List<List<Integer>> stepsPossible;
    private int numLeftJumps;
    private int numRightJumps;

    private List<List<Integer>> piecesMovable;


    /**
     * Constructor sets up game state.
     */
    public Checkers() {
        reset();
    }

    /**
     * playTurn allows players to play a turn. Returns true if the move is
     * successful and false if a player tries to play in a location that is
     * taken or after the game has ended. If the turn is successful and the game
     * has not ended, the player is changed. If the turn is unsuccessful or the
     * game has ended, the player is not changed.
     *
     * @return whether the turn was successful
     */
    public void writeStateToFile() {
        String filePath = "state.txt";
        File myObj = new File(filePath);
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter(myObj, false));
            for (int[] row : board) { //write board
                for (int player : row) {
                    bw.write(player + "");
                }
                bw.write("\n");
            }
            bw.write(blackPlayer + "\n");
            bw.write(gameOver + "\n");
            bw.write(numBlack + "\n");
            bw.write(numWhite + "\n");
            bw.close();
        } catch (IOException e) {
            System.out.println("IOException caught");
        }
    }

    public void readStateFromFile(String filePath) {
        BufferedReader br = null;
        try {
            if (filePath == null) {
                throw new IllegalArgumentException();
            }
            File f = new File(filePath);
            if (!f.exists()) {
                throw new IllegalArgumentException();
            }
            br = new BufferedReader(new FileReader(filePath));
        } catch (IOException e) {
            System.out.print("IOException caught");
        }
        try {
            for (int i = 0; i < 8; i++) { //read the board
                String str = br.readLine();
                for (int j = 0; j < 8; j++) {
                    board[i][j] = Character.getNumericValue(str.charAt(j));
                }
            }
            blackPlayer = br.readLine().equals("true");
            gameOver = br.readLine().equals("true");
            numBlack = Integer.valueOf(br.readLine());
            numWhite = Integer.valueOf(br.readLine());
        } catch (IOException e) {
            System.out.println("IOException caught");
        }
        numLeftJumps = 0;
        numRightJumps = 0;
        piecesMovable = new ArrayList<>();
        pieceSelected = null;
        if (blackPlayer) {
            checkCanJump(1);
        } else {
            checkCanJump(2);
        }
    }

    public void setNumBlack(int black) { //for testing
        numBlack = black;
    }

    public void setPlayer(int r, int c, int player) { //for testing
        board[r][c] = player;
    }

    public void setNumWhite(int white) { //for testing
        numWhite = white;
    }

    public int checkWinner() {
        if (numBlack == 0) {
            gameOver = true;
            return 2;
        }
        if (numWhite == 0) {
            gameOver = true;
            return 1;
        }
        return 0;
    }

    /**
     * printGameState prints the current game state
     * for debugging.
     */
    public void printGameState() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(board[i][j]);
                if (j < 2) {
                    System.out.print(" | ");
                }
            }
            if (i < 2) {
                System.out.println("\n---------");
            }
        }
    }

    /**
     * reset (re-)sets the game state to start a new game.
     */
    public void reset() {
        board = new int[8][8];

        for (int i = 1; i < 8; i += 2) {
            board[5][i - 1] = 1;
            board[6][i] = 1;
            board[7][i - 1] = 1;
            board[0][i] = 2;
            board[1][i - 1] = 2;
            board[2][i] = 2;
        }

        blackPlayer = true;
        gameOver = false;
        numBlack = 12;
        numWhite = 12;
        rightUpJumpsPossible = new HashMap<>();
        rightDownJumpsPossible = new HashMap<>();
        leftUpJumpsPossible = new HashMap<>();
        leftDownJumpsPossible = new HashMap<>();
        numLeftJumps = 0;
        numRightJumps = 0;
        piecesMovable = new ArrayList<>();
        pieceSelected = null;
        checkCanJump(1);
        stepsPossible = new ArrayList<>();
    }

    /**
     * getCurrentPlayer is a getter for the player
     * whose turn it is in the game.
     *
     * @return true if it's Player 1's turn,
     * false if it's Player 2's turn.
     */
    public boolean getCurrentPlayer() {
        return blackPlayer;
    }

    public List<List<Integer>> getPiecesMovable() {
        return piecesMovable;
    }

    public int getPlayer(int r, int c) {
        if (r >= 0 && r < 8 && c >= 0 && c < 8) {
            return board[r][c];
        }
        return -1;
    }

    public int[] getPieceSelected() {
        return pieceSelected;
    }

    public void printMap(Map<String, List<Integer>> m) {
        for (Map.Entry<String, List<Integer>> el : m.entrySet()) {
            System.out.println("(" + el.getValue().get(0) + ", " + el.getValue().get(1) + ") ");
        }
    }

    public void checkCanJump(int player) {
        Checkers alias = this;
        int num = alias.getPlayer(0,0);
        System.out.println(num);
        boolean canJump = false;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == player || board[i][j] == player + 2) {
                    checkAvailableMovesForPiece(board[i][j], i, j);
                    if (!leftUpJumpsPossible.isEmpty() || !leftDownJumpsPossible.isEmpty() ||
                            !rightDownJumpsPossible.isEmpty() || !rightUpJumpsPossible.isEmpty()) {
                        canJump = true;
                        List<Integer> l = new ArrayList<>();
                        l.add(i);
                        l.add(j);
                        piecesMovable.add(l);
                    }
                }
            }
        }
        if (!canJump) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (board[i][j] == player || board[i][j] == player + 2) {
                        stepsPossible = new ArrayList<>();
                        if (board[i][j] == 1 || board[i][j] == 2) {
                            getAvailableSteps(player, i, j);
                        } else {
                            getKingAvailableSteps(i, j);
                        }

                        if (!stepsPossible.isEmpty()) {
                            List<Integer> l = new ArrayList<>();
                            l.add(i);
                            l.add(j);
                            piecesMovable.add(l);
                        }
                    }
                }
            }
        }
    }

    public void setGameOver() { //for testing
        gameOver = true;
    }

    public void setPieceSelected(int player, int r, int c) {
        if (gameOver) {
            return;
        }
        int[] arr = {player, r, c};
        pieceSelected = arr;
        checkAvailableMovesForPiece(player, r, c);
        System.out.print("stepsPossible: ");
        for (List<Integer> l : stepsPossible) {
            for (Integer i : l) {
                System.out.print(i + " ");
            }
            System.out.print(", ");
        }
        System.out.println();
        System.out.println("left down jumps possible: ");
        printMap(leftDownJumpsPossible);
        System.out.println("left up jumps possible: ");
        printMap(leftUpJumpsPossible);
        System.out.println("right up jumps possible: ");
        printMap(rightUpJumpsPossible);
        System.out.println("right down jumps possible: ");
        printMap(rightDownJumpsPossible);
    }

    /**
     * This main method illustrates how the model is completely independent of
     * the view and controller. We can play the game from start to finish
     * without ever creating a Java Swing object.
     * <p>
     * This is modularity in action, and modularity is the bedrock of the
     * Model-View-Controller design framework.
     * <p>
     * Run this file to see the output of this method in your console.
     */
    public static void main(String[] args) {
        Checkers t = new Checkers();
        t.printGameState();
        System.out.println();
        System.out.println();
        System.out.println("Winner is: " + t.checkWinner());
    }

    public void capture(int r, int c, String move, Map<String, List<Integer>> map) {
        for (Map.Entry<String, List<Integer>> mapElement : map.entrySet()) {
            String key = mapElement.getKey();
            int row = mapElement.getValue().get(0);
            int col = mapElement.getValue().get(1);
            if ((move.contains("U") && key.contains("U"))
                    || (move.contains("D") && key.contains("D"))) {
                if (key.contains("DL")) {
                    board[row - 1][col + 1] = 0;
                } else if (key.contains("DR")) {
                    board[row - 1][col - 1] = 0;
                } else if (key.contains("UR")) {
                    board[row + 1][col - 1] = 0;
                } else if (key.contains("UL")) {
                    board[row + 1][col + 1] = 0;
                }
                if (pieceSelected[0] == 1 || pieceSelected[0] == 3) {
                    numWhite--;
                } else {
                    numBlack--;
                }
            }
        }
    }

    public void capturePieces(int r, int c) {
        String move = "";
        //capture all the pieces on the way to the new location  of (r,c)

        if (gameOver) {
            return;
        }

        List<Integer> coordinates = new ArrayList<>();
        coordinates.add(r);
        coordinates.add(c);
        Map<String, List<Integer>> map;
        if (leftUpJumpsPossible.containsValue(coordinates)) {
            map = leftUpJumpsPossible;
        } else if (leftDownJumpsPossible.containsValue(coordinates)) {
            map = leftDownJumpsPossible;
        } else if (rightUpJumpsPossible.containsValue(coordinates)) {
            map = rightUpJumpsPossible;
        } else if (rightDownJumpsPossible.containsValue(coordinates)) {
            map = rightDownJumpsPossible;
        } else {
            return;
        }
        for (Map.Entry<String, List<Integer>> el : map.entrySet()) {
            if (el.getValue().equals(coordinates)) {
                move = el.getKey(); // "DL", "UL", etc. + #
                System.out.println("move: " + move);
            }
        }
        capture(r, c, move, map);
    }

    public boolean isAvailableMove(List<Integer> l) {
        return leftDownJumpsPossible.containsValue(l)
                || rightDownJumpsPossible.containsValue(l) ||
                leftUpJumpsPossible.containsValue(l) ||
                rightUpJumpsPossible.containsValue(l) || stepsPossible.contains(l);
    }

    public List<Integer> findLastJump(Map<String, List<Integer>> m, boolean up) {
        //if down, then the last jump is the int list with largest row
        //if up, the last jump is the int list with smallest row

        int min = 8;
        int max = -1;
        int[] ans = new int[2];

        for (Map.Entry<String, List<Integer>> el : m.entrySet()) {
            int row = el.getValue().get(0);
            if (row < min) {
                min = row;
                if (up) {
                    ans[0] = row;
                    ans[1] = el.getValue().get(1);
                }
            }
            if (row > max) {
                max = row;
                if (!up) {
                    ans[0] = row;
                    ans[1] = el.getValue().get(1);
                }
            }
        }
        List<Integer> lAns = new ArrayList<>();
        lAns.add(ans[0]);
        lAns.add(ans[1]);
        return lAns;
    }

    public void movePiece(int r, int c) { //moves the piece to the new location.
        if (gameOver) {
            return;
        }
        capturePieces(r, c);
        List<Integer> l = new ArrayList<>();
        l.add(r);
        l.add(c);
        if (isAvailableMove(l)) {
            board[pieceSelected[1]][pieceSelected[2]] = 0;

            //find the last jump for double jumps
            int i = r;
            int j = c;
            if (leftUpJumpsPossible.containsValue(l)) {
                i = findLastJump(leftUpJumpsPossible, true).get(0);
                j = findLastJump(leftUpJumpsPossible, true).get(1);
            } else if (rightUpJumpsPossible.containsValue(l)) {
                i = findLastJump(rightUpJumpsPossible, true).get(0);
                j = findLastJump(rightUpJumpsPossible, true).get(1);
            } else if (leftDownJumpsPossible.containsValue(l)) {
                i = findLastJump(leftDownJumpsPossible, false).get(0);
                j = findLastJump(leftDownJumpsPossible, false).get(1);
            } else if (rightDownJumpsPossible.containsValue(l)) {
                i = findLastJump(rightDownJumpsPossible, false).get(0);
                j = findLastJump(rightDownJumpsPossible, false).get(1);
            }
            board[i][j] = pieceSelected[0];

            if (blackPlayer && r == 0) {
                makeKing(r, c);
            } else if (!blackPlayer && r == 7) {
                makeKing(r, c);
            }
            resetVars();
        }
        System.out.println("numBlack " + numBlack + " numWhite " + numWhite);
    }

    public void makeKing(int r, int c) {
        if (r == 7) {
            board[r][c] = 4;
        } else if (r == 0) {
            board[r][c] = 3;
        }
    }

    public int getNumBlack() {
        return numBlack;
    }

    public int getNumWhite() {
        return numWhite;
    }

    public void resetVars() {
        //change to other player's turn
        blackPlayer = !blackPlayer;
        pieceSelected = null;
        leftUpJumpsPossible = new HashMap<>();
        leftDownJumpsPossible = new HashMap<>();
        rightUpJumpsPossible = new HashMap<>();
        rightDownJumpsPossible = new HashMap<>();
        numLeftJumps = 0;
        numRightJumps = 0;
        piecesMovable = new ArrayList<>();
        stepsPossible = new ArrayList<>();

        //check pieces movable
        if (blackPlayer) {
            checkCanJump(1);
        } else {
            checkCanJump(2);
        }
    }

    public boolean checkDifferentColor(int player1, int player2) {
        if (player1 == 0 || player2 == 0) {
            return false;
        }
        if (player1 == 1 || player1 == 3) {
            return player2 == 2 || player2 == 4;
        } else {
            return player2 == 1 || player2 == 3;
        }
    }

    public Map<String, List<Integer>> checkLeftDownJumps(
            int player, int r, int c, Map<String, List<Integer>> ans) {
        if (player != 1) { //player can jump down
            if (r + 2 < 8) {
                if (c - 2 >= 0) {
                    int playerToEat = board[r + 1][c - 1];
                    if (checkDifferentColor(player, playerToEat)) {
                        if (board[r + 2][c - 2] == 0) {
                            //available jump down left
                            System.out.println("Reached available jump down left");
                            numLeftJumps++;
                            List<Integer> l = new ArrayList<>();
                            l.add(r + 2);
                            l.add(c - 2);
                            ans.put("DL" + numLeftJumps, l);
                            checkRightDownJumps(player, r + 2, c - 2, ans);
                            checkLeftDownJumps(player, r + 2, c - 2, ans);
                        }
                    }
                }
            }
        }
        return ans;
    }


    public Map<String, List<Integer>> checkRightDownJumps(
            int player, int r, int c, Map<String, List<Integer>> ans) {
        if (player != 1) {
            if (r + 2 < 8) {
                if (c + 2 < 8) {
                    int playerToEat = board[r + 1][c + 1];
                    if (checkDifferentColor(player, playerToEat)) {
                        if (board[r + 2][c + 2] == 0) {
                            //available jump right down
                            System.out.println("Reached available jump right down");
                            numRightJumps++;
                            List<Integer> l = new ArrayList<>();
                            l.add(r + 2);
                            l.add(c + 2);
                            ans.put("DR" + numRightJumps, l);
                            checkRightDownJumps(player, r + 2, c + 2, ans);
                            checkLeftDownJumps(player, r + 2, c + 2, ans);
                        }
                    }
                }
            }
        }
        return ans;
    }

    public Map<String, List<Integer>> checkRightUpJumps(
            int player, int r, int c, Map<String, List<Integer>> ans) {
        if (player != 2) {
            if (r - 2 >= 0) {
                if (c + 2 < 8) {
                    int playerToEat = board[r - 1][c + 1];
                    if (checkDifferentColor(player, playerToEat)) {
                        if (board[r - 2][c + 2] == 0) {
                            //available jump right  up
                            System.out.println("Reached available jump right up");
                            numRightJumps++;
                            List<Integer> l = new ArrayList<>();
                            l.add(r - 2);
                            l.add(c + 2);
                            ans.put("UR" + numRightJumps, l);
                            checkRightUpJumps(player, r - 2, c + 2, ans);
                            checkLeftUpJumps(player, r - 2, c + 2, ans);
                        }
                    }
                }
            }
        }

        return ans;
    }

    public Map<String, List<Integer>> checkLeftUpJumps(
            int player, int r, int c, Map<String, List<Integer>> ans) {
        if (player != 2) { //can jump up
            if (r - 2 >= 0) {
                if (c - 2 >= 0) {
                    int playerToEat = board[r - 1][c - 1];
                    if (checkDifferentColor(player, playerToEat)) {
                        if (board[r - 2][c - 2] == 0) {
                            //available jump up left
                            System.out.println("Reached available jump up left");
                            numLeftJumps++;
                            List<Integer> l = new ArrayList<>();
                            l.add(r - 2);
                            l.add(c - 2);
                            ans.put("UL" + numLeftJumps, l);
                            checkRightUpJumps(player, r - 2, c - 2, ans);
                            checkLeftUpJumps(player, r - 2, c - 2, ans);
                        }
                    }
                }
            }
        }
        return ans;
    }

    public void getKingAvailableSteps(int r, int c) {
        getAvailableSteps(1, r, c);
        getAvailableSteps(2, r, c);
    }

    public void getAvailableSteps(int player, int r, int c) {
        if (player == 1) {
            //piece is black
            if ((r - 1) >= 0 && (r - 1) < 8 && (c - 1) >= 0 && (c - 1) < 8) {
                if (board[r - 1][c - 1] == 0) {
                    List<Integer> l = new ArrayList<>();
                    l.add(r - 1);
                    l.add(c - 1);
                    stepsPossible.add(l);
                }
            }
            if ((r - 1) >= 0 && (r - 1) < 8 && (c + 1) >= 0 && (c + 1) < 8) {
                if (board[r - 1][c + 1] == 0) {
                    List<Integer> l = new ArrayList<Integer>();
                    l.add(r - 1);
                    l.add(c + 1);
                    stepsPossible.add(l);
                }
            }
        } else if (player == 2) { //piece is white
            if ((r + 1) >= 0 && (r + 1) < 8 && (c - 1) >= 0 && (c - 1) < 8) {
                if (board[r + 1][c - 1] == 0) {
                    List<Integer> l = new ArrayList<>();
                    l.add(r + 1);
                    l.add(c - 1);
                    stepsPossible.add(l);
                }
            }
            if ((r + 1) >= 0 && (r + 1) < 8 && (c + 1) >= 0 && (c + 1) < 8) {
                if (board[r + 1][c + 1] == 0) {
                    List<Integer> l = new ArrayList<Integer>();
                    l.add(r + 1);
                    l.add(c + 1);
                    stepsPossible.add(l);
                }
            }
        }
    }

    public void checkAvailableMovesForPiece(int player, int r, int c) {
        stepsPossible = new ArrayList<>();
        Map<String, List<Integer>> lu = new HashMap<>();
        Map<String, List<Integer>> ld = new HashMap<>();
        Map<String, List<Integer>> ru = new HashMap<>();
        Map<String, List<Integer>> rd = new HashMap<>();
        leftUpJumpsPossible = checkLeftUpJumps(player, r, c, lu);
        leftDownJumpsPossible = checkLeftDownJumps(player, r, c, ld);
        rightUpJumpsPossible = checkRightUpJumps(player, r, c, ru);
        rightDownJumpsPossible = checkRightDownJumps(player, r, c, rd);
        if (leftUpJumpsPossible.isEmpty() && leftDownJumpsPossible.isEmpty()
                && rightDownJumpsPossible.isEmpty() && rightUpJumpsPossible.isEmpty()) {
            if (player == 1 || player == 2) {
                getAvailableSteps(player, r, c);
            } else {
                getKingAvailableSteps(r, c);
            }
        }
    }
}
