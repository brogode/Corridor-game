package player;

import java.awt.Color;
import java.awt.Point;


import java.util.Scanner;
import java.util.concurrent.Semaphore;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import util.Graph;

import main.QBoard;


public class Board {
    public final static Color BUTTON_DEFAULT_COLOR = new Color(220,220,220);
    public static final Color WALL_COLOR = Color.black;  //This will eventually be switched to brown

    GameState currentState;		// holds the state of the game in a single variable.
    private QBoard gui;			// allows board to communicate with the gui

    public Semaphore sem;		// used to tell the ai when it's turn is, needs a better name
    public JFrame winFrame;		// holds the message when a player wins the game
    private boolean netPlay; 	// If you are playing on the net.
    private String moveForNetwork;		//
    private boolean networkMadeLastMove;
    public Semaphore moveMadeForNetwork;




   //This is the constructor that is used called in the Quoridor class to create a new game.

    public Board(int numOfPlayers, Color[] colArray) {
        Player[] players = new Player[numOfPlayers];
        int pl = players.length;
        for (int i = 0; i < pl; i++) {
            players[i] = new Player(i, 20/pl, colArray[i]);

        }
        initialize(players);
    }

    //Using for network Play, Designed for Move Server


    private void initialize(Player[] pls) {
        int[][] walls = new int[8][8];
        currentState = new GameState(walls, pls,0, initializeGraph());
        //initializeAIIfNeeded();
        newGUI();
        if(netPlay){
            return;
        }else{
            requestMove();
        }
    }

    // creates a graph containing 81 nodes, each representing a space on the board, and add edges between
    // nodes representing spaces directly adjacent to each other
    private Graph initializeGraph() {
        Graph graph = new Graph<Point>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                graph.addNode(new Point(i,j));
            }
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 9; j++) {
                graph.addEdge(new Point(i, j), new Point(i + 1, j));
            }
        }



        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 9; j++) {
                graph.addEdge(new Point(j, i), new Point(j, i + 1));
            }
        }
        return graph;
    }


    // Returns the number of walls the specified Player has left.

    public int numberOfWalls(int player){
        return currentState.numberOfWalls(player);
    }

   // This method returns a player.GameState Object which contains a copy of all the necessary variables in the game.

    public GameState getCurrentState() {
        return currentState;
        //return new player.GameState(walls, players, turn, graph);
    }



 // This method will convert a String containing a move in the format we've been using in our gui into one
     // matching the format that will be used when we send moves over a network.

    public String convertGUIStringToNetString(String guiString) {
        Player[] players = currentState.getPlayerArray();
        Scanner sc = new Scanner(guiString);
        String firstChar = sc.next();
        String netString = "";
        int x = Integer.parseInt(sc.next());
        int y = Integer.parseInt(sc.next());

        if (firstChar.charAt(0) == 'M') {
            netString += ("MOVE M (" + players[currentState.getTurn()].getY() + ", " + players[currentState.getTurn()].getX() + ")");
            netString += " (" + y + ", " + x + ")";
        }

        if (firstChar.charAt(0) == 'V') {
            netString += ("MOVE W (" + y + ", " + (x+1) + ")");
            netString += " (" + (y+2) + ", " + (x+1) + ")";
        }

        if (firstChar.charAt(0) == 'H') {
            netString += ("MOVE W (" + (y+1) + ", " + x + ")");
            netString += " (" + (y+1) + ", " + (x+2) + ")";
        }

        return netString;
    }


   // This method converts a "Net String" to a "GUI String" so that it can be processed correctly.

    public String convertNetStringToGUIString(String netStr) {
        System.out.println("Recieve string to translate : " + netStr);
        String netString = removePunctuation(netStr);
        Scanner sc = new Scanner(netString);

        //sc.next();						\
        String firstChar = sc.next();

        String GUIString = "";

        //needed to determine if a wall is horizontal or vertical
        sc.next();
        int x2 = Integer.parseInt(sc.next());

        int y = Integer.parseInt(sc.next());
        int x = Integer.parseInt(sc.next());

        if (firstChar.charAt(0) == 'M') {
            GUIString = "M " + x + " " + y;
        }

        if (firstChar.charAt(0) == 'W') {
            //if it's a vertical wall
            if (x == x2)
                GUIString = "V " + (x-1) + " " + (y-2);
                //otherwise it must be horizontal
            else
                GUIString = "H " + (x-2) + " " + (y-1);
        }

        return GUIString;
    }

    // This method takes a String and replaces all of the parentheses and commas with spaces.

    private String removePunctuation(String oldStr) {
        String newStr = oldStr;
        newStr = newStr.replace('(', ' ');
        newStr = newStr.replace(')', ' ');
        newStr = newStr.replace(',', ' ');
        newStr = newStr.replace('<', ' ');
        newStr = newStr.replace('>', ' ');
        return newStr;
    }

   // Method which takes in a GUIString representing a move and determines whether or not it is legal.

    public boolean isStringLegal(String input) {
        return getCurrentState().isStringLegal(input);
    }

    public int[][] getWallArray() {
        return currentState.getWalls();
    }

  // Returns the number associated with the Player whose turn it is.

    public int getTurn() {
        return currentState.getTurn();
    }




    // makes a new board appear on screen and sets default locations of the players
    public void newGUI() {
        Player players[] = currentState.getPlayerArray();
        gui = new QBoard(this);
        for (int i = 0; i < players.length; i++) {

                gui.setColorOfSpace(players[i].getLocation(), players[i].getColor());
        }
    }

   // Reads in a String representing a move and makes it.  Don't call this without calling the isStringLegal method

    public void readStringFromGUI(String input) {
        //Player[] players = currentState.getPlayerArray();
        showMoves(currentState.getCurrentPlayer(),false);
        System.out.println("before if netplay, input is :" + input);

        gui.setColorOfSpace(currentState.getCurrentPlayer().getLocation(), BUTTON_DEFAULT_COLOR);
        //makes the actual move
        currentState = currentState.move(input);
        gui.setColorOfSpace(currentState.getPrevPlayer().getLocation(), currentState.getPrevPlayer().getColor());
        gui.setColorOfSpace(currentState.getCurrentPlayer().getLocation(), currentState.getCurrentPlayer().getColor());

        //enableAndChangeColor(players[getTurn()].getLocation(), false, players[getTurn()].getColor());
        if(!netPlay)
            if(hasWon()){
                winWindow();
                return;
            }

        gui.setStatus();
        if (input.startsWith("V") || input.startsWith("H"))
            updateWalls();
        if(!netPlay)
            requestMove();

    }

    public boolean hasWon(){
        if(currentState.getPrevPlayer().hasWon()){
            return true;
        }
        return false;
    }

    //Returns true if the specified Player has one.  Otherwise, it returns false.

    public boolean hasWon(int player) {
        return currentState.getPlayerArray()[player].hasWon();
    }

    public void winWindow(){
        JOptionPane.showMessageDialog(winFrame,
                "Player " + (currentState.getPrevPlayer().getPlayerID()) + " has won!");

    }

    public void winWindow(int winner){
        JOptionPane.showMessageDialog(winFrame,
                "Player " + winner + " has won!");

    }


    // Reads in a String representing a move from over the network and makes the move.

    public String readStringFromNet(String input) {
        moveForNetwork = "";
        moveMadeForNetwork = new Semaphore(0);

        if(input.contains("MOVE?")){
            networkMadeLastMove = false;
            requestMove();
            System.out.println("before while !mmfn");

            moveMadeForNetwork.acquireUninterruptibly();

            System.out.println("get pass while");
            return convertGUIStringToNetString(moveForNetwork);
        }else if(input.contains("WINNER")){

        }else if(input.contains("REMOVE")){

        }
        else{
            networkMadeLastMove = true;
            input = convertNetStringToGUIString(input);
            readStringFromGUI(input);
        }
        return "";
    }

    // Determines whether a move from over the network was legal.

    public boolean isStringFromNetLegal(String input) {
        input = convertNetStringToGUIString(input);
        return isStringLegal(input);
    }


   // Called after a move is made.  Prompts the next Player to make their move.

    private void requestMove() {
        Player[] players = currentState.getPlayerArray();

        showMoves(players[getTurn()], true);


    }

    // This method makes the gui show the walls.

    private void updateWalls() {
        int[][] walls = currentState.getWalls();
        Player[] players = currentState.getPlayerArray();
        int turn = (getTurn() + players.length - 1) % players.length;
        for (int i = 0; i < walls.length; i++)
            for (int j = 0; j < walls.length; j++) {
                if (walls[i][j] == 1 && gui.getVertWallColor(new Point(i, j)).equals(BUTTON_DEFAULT_COLOR)) {
                    gui.setVertWallColor(new Point(i,j), players[turn].getColor());
                    gui.setVertWallColor(new Point(i,j+1), players[turn].getColor());
                }else if (walls[i][j] == 2 && gui.getHoriWallColor(new Point(i, j)).equals(BUTTON_DEFAULT_COLOR)) {
                    gui.setHoriWallColor(new Point(i,j), players[turn].getColor());
                    gui.setHoriWallColor(new Point(i+1,j), players[turn].getColor());
                }
            }

    }

    // this method can show the available moves a player can make if b is true, this needs to be called again with
    // b being false to stop showing the moves a player could make.
    private void showMoves(Player pl, boolean b) {
        showMoves(pl, b, 0);
    }



    // rec is the number of times a recursive call was made it should probably get a better name
    private void showMoves(Player pl, boolean b, int rec) {
        Player[] players = currentState.getPlayerArray();
        if (rec >= players.length)
            return;
        Color c;
        if (b == true) {
            int turn = currentState.getTurn();
            int re = Math.min((players[turn].getColor().getRed() + 255)/2, 240);
            int gr = Math.min((players[turn].getColor().getGreen() + 255)/2, 240);
            int bl = Math.min((players[turn].getColor().getBlue() + 255)/2, 240);
            c = new Color(re, gr, bl);
        } else {
            c = BUTTON_DEFAULT_COLOR;
        }

        Point[] adjacentSpaces = new Point[4];
        adjacentSpaces[0] = pl.up();
        adjacentSpaces[1] = pl.down();
        adjacentSpaces[2] = pl.left();
        adjacentSpaces[3] = pl.right();

        for (int i = 0; i < adjacentSpaces.length; i++) {
            if (adjacentSpaces[i] != null) {
                if (!isBlocked(pl.getLocation(), adjacentSpaces[i])) {
                    int PID = PlayerOnSpace(adjacentSpaces[i]);
                    if (PID >= 0)
                        showMoves(players[PID], b, rec+1);
                    else
                        enableAndChangeColor(adjacentSpaces[i], b, c);
                }
            }
        }

        enableAndChangeColor(pl.getLocation(), false, pl.getColor());
    }

    //Returns true if a wall is between two adjacent spaces.

    private boolean isBlocked(Point p1, Point p2) {
        return currentState.isBlocked(p1, p2);
    }

  // Returns an int containing the ID of the Player currently on the space passed in.

    private int PlayerOnSpace(Point p) {
        Player[] players = currentState.getPlayerArray();
        for (int i = 0; i < players.length; i++) {
            if (p.getLocation().equals(players[i].getLocation())) {
                return i;
            }
        }
        return -1;
    }

    private void enableAndChangeColor(Point p, boolean b, Color c) {
        gui.setColorOfSpace(p, c);
    }

    public int getNumOfPlayers(){
        return currentState.getPlayerArray().length;
    }

    public static void main(String[] args) {
        //new Board(true);
        Color[] DEFAULT_COLORS = {Color.blue, Color.red, Color.green, Color.yellow};
        Board play = new Board(2, DEFAULT_COLORS);
    }
}
