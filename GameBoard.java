package org.cis1200.checkers;

/*
 * CIS 120 HW09 - Checkers Demo
 * (c) University of Pennsylvania
 * Created by Bayley Tuch, Sabrina Green, and Nicolas Corona in Fall 2020.
 */

import javax.swing.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;

/**
 * This class instantiates a Checkers object, which is the model for the game.
 * As the user clicks the game board, the model is updated. Whenever the model
 * is updated, the game board repaints itself and updates its status JLabel to
 * reflect the current state of the model.
 * <p>
 * This game adheres to a Model-View-Controller design framework. This
 * framework is very effective for turn-based games. We STRONGLY
 * recommend you review these lecture slides, starting at slide 8,
 * for more details on Model-View-Controller:
 * https://www.seas.upenn.edu/~cis120/current/files/slides/lec37.pdf
 * <p>
 * In a Model-View-Controller framework, GameBoard stores the model as a field
 * and acts as both the controller (with a MouseListener) and the view (with
 * its paintComponent method and the status JLabel).
 */
@SuppressWarnings("serial")
public class GameBoard extends JPanel {

    private Checkers ttt; // model for the game
    private JLabel status; // current status text
    private JLabel blackPieces;
    private JLabel whitePieces;

    // Game constants
    public static final int BOARD_WIDTH = 450;
    public static final int BOARD_HEIGHT = 450;

    /**
     * Initializes the game board.
     */
    public GameBoard(JLabel statusInit, JLabel blackInit, JLabel whiteInit) {
        // creates border around the court area, JComponent method
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Enable keyboard focus on the court area. When this component has the
        // keyboard focus, key events are handled by its key listener.
        setFocusable(true);

        ttt = new Checkers(); // initializes model for the game
        status = statusInit; // initializes the status JLabel
        blackPieces = blackInit;
        whitePieces = whiteInit;

        /*
         * Listens for mouse clicks. Updates the model, then updates the game
         * board based off of the updated model.
         */
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                Point p = e.getPoint();
                int r = (p.y - 30) / 50;
                int c = (p.x - 30) / 50;
                List<Integer> l = new ArrayList<>();
                l.add(r);
                l.add(c);

                if (ttt.getPiecesMovable().contains(l)) {
                    //set selected player to the player at r,c
                    ttt.setPieceSelected(ttt.getPlayer(r, c), r, c);
                } else if (ttt.getPlayer(r, c) == 0 && ttt.getPieceSelected() != null) {
                    ttt.movePiece(r, c);
                }
                updateStatus(); // updates the status JLabel
                repaint(); // repaints the game board
            }
        });
    }

    /**
     * (Re-)sets the game to its initial state.
     */
    public void reset() {
        ttt.reset();
        status.setText("Black's Turn");
        blackPieces.setText("<html>Number of Black Pieces: " + ttt.getNumBlack() + "</html>");
        whitePieces.setText("<html>Number of Red Pieces: " + ttt.getNumWhite() + "</html>");
        repaint();
        // Makes sure this component has keyboard/mouse focus
        requestFocusInWindow();
    }

    public void save() {
        ttt.writeStateToFile();
        status.setText("Game saved!");
        repaint();
    }

    public void load() {
        ttt.readStateFromFile("state.txt");
        status.setText("Game loaded!");
        repaint();
    }

    /**
     * Updates the JLabel to reflect the current state of the game.
     */
    private void updateStatus() {
        if (ttt.getCurrentPlayer()) {
            status.setText("Black's Turn");
        } else {
            status.setText("Red's Turn");
        }
        blackPieces.setText("<html>Number of Black Pieces: " + ttt.getNumBlack() + "</html>");
        whitePieces.setText("<html>Number of Red Pieces: " + ttt.getNumWhite() + "</html>");
        int winner = ttt.checkWinner();
        if (winner == 1) {
            status.setText("Black wins!");
        } else if (winner == 2) {
            status.setText("Red wins!");
        }
    }

    /**
     * Draws the game board.
     * <p>
     * There are many ways to draw a game board. This approach
     * will not be sufficient for most games, because it is not
     * modular. All of the logic for drawing the game board is
     * in this method, and it does not take advantage of helper
     * methods. Consider breaking up your paintComponent logic
     * into multiple methods or classes, like Mushroom of Doom.
     */

    public void highlightPiece(Graphics g) {
        if (ttt.getPieceSelected() == null) {
            return;
        }
        int r = ttt.getPieceSelected()[1];
        int c = ttt.getPieceSelected()[2];
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(5));
        g.drawOval(27 + 50 * c, 27 + 50 * r, 45, 45);
    }

    public void highlightMovablePieces(Graphics g) {
        List<List<Integer>> l = ttt.getPiecesMovable();
        g.setColor(Color.yellow);
        for (List<Integer> ls : l) {
            int r = ls.get(0);
            int c = ls.get(1);
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(5));
            g2.drawOval(27 + 50 * c, 27 + 50 * r, 45, 45);
        }
    }

    public void highlightAvailableMoves(Graphics g) {
        int[] p = ttt.getPieceSelected();
        if (p == null) {
            return;
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                List<Integer> box = new ArrayList<>();
                box.add(i);
                box.add(j);
                if (ttt.isAvailableMove(box)) {
                    g.setColor(Color.GREEN);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setStroke(new BasicStroke(1));
                    g.drawOval(29 + 50 * j, 29 + 50 * i, 40, 40);
                }
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        boolean white = true;
        for (int i = 0; i < 8; i++) {
            white = !white;
            for (int j = 0; j < 8; j++) {
                if (white) {
                    g.setColor(new Color(239, 231, 219));
                } else {
                    g.setColor(new Color(181, 108, 29));
                }
                g.fillRect(j * 50 + 25, i * 50 + 25, 50, 50);
                white = !white;
            }
        }

        // Draws checkers onto the board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int player = ttt.getPlayer(i, j);
                if (player == 1) {
                    g.setColor(Color.BLACK);
                    g.fillOval(30 + 50 * j, 30 + 50 * i, 40, 40);
                    g.drawOval(30 + 50 * j, 30 + 50 * i, 40, 40);
                } else if (player == 2) {
                    g.setColor(Color.RED);
                    g.fillOval(30 + 50 * j, 30 + 50 * i, 40, 40);
                    g.drawOval(30 + 50 * j, 30 + 50 * i, 40, 40);
                } else if (player == 3) {
                    g.setColor(Color.BLACK);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setStroke(new BasicStroke(3));
                    g2.drawOval(30 + 50 * j, 30 + 50 * i, 40, 40);
                    g2.drawOval(35 + 50 * j, 35 + 50 * i, 30, 30);
                    g2.drawOval(40 + 50 * j, 40 + 50 * i, 20, 20);
                    g.setColor(new Color(212, 175, 55, 150));
                    g.fillOval(30 + 50 * j, 30 + 50 * i, 40, 40);
                } else if (player == 4) {
                    g.setColor(Color.RED);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setStroke(new BasicStroke(3));
                    g2.drawOval(30 + 50 * j, 30 + 50 * i, 40, 40);
                    g2.drawOval(35 + 50 * j, 35 + 50 * i, 30, 30);
                    g2.drawOval(40 + 50 * j, 40 + 50 * i, 20, 20);
                    g.setColor(new Color(212, 175, 55, 150));
                    g.fillOval(30 + 50 * j, 30 + 50 * i, 40, 40);
                }
            }
        }
        highlightMovablePieces(g);
        highlightAvailableMoves(g);
        highlightPiece(g);
    }

    /**
     * Returns the size of the game board.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    }
}
