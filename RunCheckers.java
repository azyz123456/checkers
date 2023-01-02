package org.cis1200.checkers;

/*
 * CIS 120 HW09 - Checkers Demo
 * (c) University of Pennsylvania
 * Created by Bayley Tuch, Sabrina Green, and Nicolas Corona in Fall 2020.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class sets up the top-level frame and widgets for the GUI.
 * <p>
 * This game adheres to a Model-View-Controller design framework. This
 * framework is very effective for turn-based games. We STRONGLY
 * recommend you review these lecture slides, starting at slide 8,
 * for more details on Model-View-Controller:
 * https://www.seas.upenn.edu/~cis120/current/files/slides/lec37.pdf
 * <p>
 * In a Model-View-Controller framework, Game initializes the view,
 * implements a bit of controller functionality through the reset
 * button, and then instantiates a GameBoard. The GameBoard will
 * handle the rest of the game's view and controller functionality, and
 * it will instantiate a Checkers object to serve as the game's model.
 */
public class RunCheckers implements Runnable {

    public void run() {
        // NOTE: the 'final' keyword denotes immutability even for local variables.

        // Top-level frame in which game components live
        final JFrame frame = new JFrame("Checkers");

        // Status panel
        final JPanel status_panel = new JPanel();
        frame.add(status_panel, BorderLayout.SOUTH);
        final JLabel status = new JLabel("Setting up...");
        status_panel.add(status);

        final JLabel blackPieces = new JLabel("Setting up...");
        final JLabel whitePieces = new JLabel("Setting up...");

        // Game board
        final GameBoard board = new GameBoard(status, blackPieces, whitePieces);
        frame.add(board, BorderLayout.CENTER);

        // Top panel
        final JPanel control_panel = new JPanel();
        frame.add(control_panel, BorderLayout.NORTH);
        JPanel panel = new JPanel();
        panel.setSize(450, 200);
        GridLayout layout = new GridLayout(3, 2);
        layout.setHgap(10);
        layout.setVgap(10);
        panel.setLayout(layout);
        final JButton reset = new JButton("Reset");
        final JButton instructions = new JButton("Instructions");
        final JButton save = new JButton("Save");
        final JButton load = new JButton("Load");
        reset.addActionListener(e -> board.reset());
        panel.add(reset);
        panel.add(instructions);
        panel.add(save);
        save.addActionListener(e -> board.save());
        panel.add(load);
        load.addActionListener(e -> board.load());
        panel.add(blackPieces);
        panel.add(whitePieces);
        control_panel.add(panel);

        //open window for instructions
        instructions.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Create a method named "createFrame()", and set up an new frame there
                // Call createFrame()
                createFrame();
            }
        });

        // Put the frame on the screen
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Start the game
        board.reset();
    }

    public static void createFrame() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Instructions");
                frame.setSize(500, 500);
                JOptionPane.showMessageDialog(frame, "Welcome to Checkers!\n" +
                        "\n" +
                        "This is a two-player game played on an 8x8 board. " +
                        "There is a black player " +
                        "and a red player. "
                        + "\n" +
                        "Both players start with 12 pieces. The objective of " +
                        "the game is to capture " +
                        "all the pieces of "
                        + "\n" +
                        "the other player." +
                        "\n" + "\n" +
                        "Each player alternates turns, with the black player " +
                        "starting first. In one " +
                        "turn, each player "
                        + "\n" +
                        "can move one space only, either diagonally left or " +
                        "diagonally right. However," +
                        " jumps are also "
                        + "\n" +
                        "possible. Jumps allow the player to capture the piece" +
                        " of the opposite color " +
                        "that they “jump” "
                        + "\n" +
                        "across. If jump(s) are possible, then the player must " +
                        "make a jump. If multiple" +
                        " jumps are " +
                        "\n" +
                        "possible in one path, then the player must make all of" +
                        " the jumps - they cannot " +
                        "stop midway. " +
                        "\n" +
                        "\n" +
                        "When pieces cross to the other side of the board, they " +
                        "become kings. Unlike " +
                        "regular pieces, "
                        + "\n" +
                        "they can move both up and down, and can capture pieces " +
                        "by jumping up and down. " +
                        "However, if a "
                        + "\n" +
                        "king is making multiple jumps in one path, the jumps " +
                        "must be all up or all " +
                        "down.\n" +
                        "\n" +
                        "The game ends when one player runs out of pieces, at " +
                        "which point the other " +
                        "player declares "
                        + "\n" +
                        "victory.\n" +
                        "\n" +
                        "Pieces that are able to move are highlighted yellow. " +
                        "The selected piece " +
                        "is " + "\n" +
                        "highlighted green. Available moves for the selected " +
                        "piece are given as " +
                        "hollow green circles." +
                        " " + "\n" +
                        "Red kings are gold in color with red circular markings," +
                        " and black kings " +
                        "are gold in color " +
                        "\n" +
                        "with black circular markings.\n");
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            }
        });
    }


}