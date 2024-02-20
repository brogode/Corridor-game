package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import player.Board;


public class QBoard extends JFrame implements ActionListener{


    public final static Color BUTTON_DEFAULT_COLOR = new Color(220,220,220);
    public final static int boardLength = 9;

    private Board board;

    public JButton[][] tiles = new JButton[9][9];
    public JButton[][] wallVert = new JButton[8][9];
    public JButton[][] wallHor = new JButton[9][8];

    private JPanel buttonPanel;
    private JPanel statusPanel;
    private JLabel statusLabel;

    //this is the constructor that should probably always be used, probably
    public QBoard(Board b) {
        super();
        board = b;
        initialize();
    }

    // I just took everything out of the old default constructor and pasted it in this method
    private void initialize() {
        setName("Quoridor Board");
        setTitle("Quoridor Board");
        setSize(512, 356);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        buttonPanel = new JPanel();
        statusPanel = new JPanel();
        statusLabel = new JLabel();
        buttonPanel.setLayout(null);
        statusPanel.setLayout(new BorderLayout());

        initializeButtons();
        buttonPanel.setSize(356, 356);
        statusPanel.setPreferredSize(new Dimension(100, 356));
        setStatus();
        statusPanel.add(statusLabel);

        add (buttonPanel);
        add (statusPanel, BorderLayout.EAST);

        setVisible(true);
    }


    private void initializeButtons(){
        int fromTop = 0;
        boolean border = false;
        for(int i = 0;i < 17;i++){ //Row
            int fromLeft = 0;
            for (int j = 0; j < 17; j++) { // Column
                if(!border){
                    if(j%2 == 0){
                        JButton button = new JButton(""); // sets the text
                        button.setName("M " + j/2 + " " + i/2);
                        button.addActionListener(this);
                        button.setRolloverEnabled(true);
                        button.setBackground(BUTTON_DEFAULT_COLOR);

                        buttonPanel.add(button);

                        Insets insets = buttonPanel.getInsets();
                        button.setBounds(fromLeft + insets.left, fromTop + insets.top, 25, 25);
                        fromLeft += 26;

                        tiles[j/2][i/2]=button;
                    }
                    else{
                        JButton button = new JButton(""); // sets the text
                        button.setName("V " + j/2 + " " + i/2);
                        button.addActionListener(this);
                        button.setRolloverEnabled(true);
                        button.setBackground(BUTTON_DEFAULT_COLOR);

                        buttonPanel.add(button);

                        Insets insets = buttonPanel.getInsets();
                        button.setBounds(fromLeft + insets.left, fromTop + insets.top, 10, 25);
                        fromLeft += 11;
                        wallVert[j/2][i/2]=button;
                    }
                }
                if (border){
                    JButton button = new JButton(""); // sets the text
                    button.setName("H " + j/2 + " " + i/2);
                    button.addActionListener(this);
                    button.setRolloverEnabled(true);
                    button.setBackground(BUTTON_DEFAULT_COLOR);

                    buttonPanel.add(button);

                    Insets insets = buttonPanel.getInsets();
                    Dimension size = button.getPreferredSize();
                    button.setBounds(fromLeft + insets.left, fromTop + insets.top, 25, 10);
                    fromLeft += 37;
                    j++;
                    wallHor[j/2][i/2]=button;
                }
            }
            if(!border){ // Fix Spacing
                fromTop += 26;
            }else{
                fromTop += 11;
            }
            border = !border;
        }
    }

    public void setStatus(){
        StringBuffer sb = new StringBuffer();
        sb.append("<html><p align=center>");
        sb.append("Player 0: " + board.numberOfWalls(0));
        sb.append("<br>");
        sb.append("Player 1: " + board.numberOfWalls(1));
        sb.append("<br>");
        if(board.getNumOfPlayers()>2){
            sb.append("Player 2: " + board.numberOfWalls(2)); //should format
            sb.append("<br>");
            sb.append("Player 3: " + board.numberOfWalls(3));
            sb.append("<br>");
        }
        sb.append("<br>");
        sb.append("It is currently <br> Player " + (board.getTurn()+1) + " turn");

        sb.append("</p></html>");

        statusLabel.setText(sb.toString());
    }

    public void actionPerformed(ActionEvent action) {

            String move = ((JButton) action.getSource()).getName();
            if (board.isStringLegal(move)) {
                System.out.println(((JButton) action.getSource()).getName());

                board.readStringFromGUI(move);
            }

    }

    /// Changes the color of a chosen space to a new color.

    public void setColorOfSpace(Point p, Color c) {
        tiles[p.x][p.y].setBackground(c);
    }

    //Takes a "point" from the wallHor 2D array and changes the color

    public void setHoriWallColor(Point p, Color c) {
        wallHor[p.x][p.y].setBackground(c);
    }

   // Takes a "point" from the wallVert 2D array and changes the color

    public void setVertWallColor(Point p, Color c) {
        wallVert[p.x][p.y].setBackground(c);
    }

    //Returns the Color of the horizontal wall at the specified postion.

    public Color getHoriWallColor(Point p) {
        return wallHor[p.x][p.y].getBackground();
    }

    //Returns the Color of the vertical wall at the specified postion.

    public Color getVertWallColor(Point p) {
        return wallVert[p.x][p.y].getBackground();
    }

    public static void main(String[] args) {
        //Board b = new Board(true);
    }
}
