package player;

import java.awt.Point;

import java.util.List;
import java.util.Scanner;


import util.Graph;

// This class holds a state of the game Quoridor.  This holds a copy of all the variables in the game.

public class GameState {
   // Default search algorithm to use when searching for paths in the graph.

    private static final String DEFAULT_SEARCH = "depth-first";


    public int[][] walls;

    private int numberOfPlayers;

    // An array of Player objects, there's one for each Player in the game.
    private Player players[];
    // This number tells us which Player's turn it is.

    private int turn;
   // Holds the information about the walls in a Graph structure.

    public Graph<Point> graph;


    public GameState(int[][] w, Player pls[], int turn, Graph<Point> graph) {
        walls = new int[w.length][w.length];
        for (int i = 0; i < w.length; i++) {
            for (int j = 0; j < w.length; j++) {
                walls[i][j] = w[i][j];
            }
        }
        players = new Player[pls.length];
        for (int i = 0; i < pls.length; i++) {
            players[i] = new Player(i, pls[i].getWalls(), pls[i].getColor());
            players[i].setLocation(pls[i].getLocation());
        }
        this.turn = turn;
        this.graph = graph.clone();
        numberOfPlayers = pls.length;
    }


    // Copy/pasted from Board.  Takes a String representing a move in the GUIString format, then
     // returns true if the move is legal, or false is the move is not.

    public boolean isStringLegal(String input) {
        Point xy = new Point();
        Scanner sc = new Scanner(input);
        String firstCh = sc.next();
        if (firstCh.equals("M")) {
            xy.x = sc.nextInt();
            xy.y = sc.nextInt();
            return isMoveLegal(turn, xy);
        } else if (firstCh.equals("H")) {
            xy.x = sc.nextInt();
            xy.y = sc.nextInt();
            return isHoriWallLegal(turn, xy);
        } else if (firstCh.equals("V")) {
            xy.x = sc.nextInt();
            xy.y = sc.nextInt();
            return isVertWallLegal(turn, xy);
        }
        return false;
    }

    //Called by isStringLegal(String input) when a horizontal wall is being tested for legality.

    private boolean isHoriWallLegal(int player, Point loc) {
        //Tests to see if the Player has walls remaining and whether or not the wall is on the board.
        if (players[player].getWalls() > 0 && loc.x < 8 && loc.x > -1 && loc.y > -1 && loc.y < 8) {
            //Checks to see if a wall is already going through the same space.
            if (walls[loc.x][loc.y] > 0)
                return false;
            if (loc.x < 7)
                if (walls[loc.x+1][loc.y] == 2)
                    return false;
            if (loc.x > 0)
                if (walls[loc.x-1][loc.y] == 2)
                    return false;

            boolean legal = true;

            graph.removeEdge(new Point(loc.x,loc.y), new Point(loc.x,loc.y+1));
            graph.removeEdge(new Point(loc.x+1,loc.y), new Point(loc.x+1,loc.y+1));
            List<Point> path;
            for (int i = 0; i < players.length; i++) {
                path = graph.findPath(DEFAULT_SEARCH, players[i].getLocation(), players[i].goalSet);
                if (path.isEmpty()) {
                    legal = false;
                }
            }
            graph.addEdge(new Point(loc.x,loc.y), new Point(loc.x,loc.y+1));
            graph.addEdge(new Point(loc.x+1,loc.y), new Point(loc.x+1,loc.y+1));
            return legal;
        }

        return false;
    }

    // Called by isStringLegal(String input) when a vertical wall is being tested for legality.

    private boolean isVertWallLegal(int player, Point loc) {
        //Tests to see if the Player has walls remaining and whether or not the wall is on the board.
        if (players[player].getWalls() > 0 && loc.x < 8 && loc.x > -1 && loc.y > -1 && loc.y < 8) {
            //Checks to see if a wall is already going through the same space.
            if (walls[loc.x][loc.y] > 0)
                return false;
            if (loc.y < 7)
                if (walls[loc.x][loc.y+1] == 1)
                    return false;
            if (loc.y > 0)
                if (walls[loc.x][loc.y-1] == 1)
                    return false;

            boolean legal = true;	//if any player would be blocked by the wall, this will be set to false

            graph.removeEdge(new Point(loc.x,loc.y), new Point(loc.x+1,loc.y));
            graph.removeEdge(new Point(loc.x,loc.y+1), new Point(loc.x+1,loc.y+1));
            List<Point> path;
            for (int i = 0; i < players.length; i++) {
                path = graph.findPath(DEFAULT_SEARCH, players[i].getLocation(), players[i].goalSet);
                if (path.isEmpty()) {
                    legal = false;
                }
            }
            graph.addEdge(new Point(loc.x,loc.y), new Point(loc.x+1,loc.y));
            graph.addEdge(new Point(loc.x,loc.y+1), new Point(loc.x+1,loc.y+1));

            return legal;
        }
        return false;
    }

    // Called by isStringLegal to test whether a Player can move to a particular spot.

    private boolean isMoveLegal(int player, Point move) {
        return isMoveLegal(player, move, 0);
    }

    // Called by isMoveLegal(int player, Point move), finds all the possible spots a Player could move to and compares
     // them to the location the Player wants to move to.

    private boolean isMoveLegal(int player, Point move, int rec) {
        // Check for invalid player index
        if (player < 0 || player >= players.length) {
            throw new IllegalArgumentException("Invalid player index");
        }

        Player currentPlayer = players[player];

        // Check for invalid recursion depth
        if (rec < 0) {
            throw new IllegalArgumentException("Invalid recursion depth");
        }

        Point[] adjacentSpaces = new Point[4];
        adjacentSpaces[0] = currentPlayer.up();
        adjacentSpaces[1] = currentPlayer.down();
        adjacentSpaces[2] = currentPlayer.left();
        adjacentSpaces[3] = currentPlayer.right();

        for (int i = 0; i < adjacentSpaces.length; i++) {
            if (adjacentSpaces[i] != null) {
                if (!isBlocked(currentPlayer.getLocation(), adjacentSpaces[i])) {
                    int PID = PlayerOnSpace(adjacentSpaces[i]);

                    // Check for invalid player ID
                    if (PID < -1 || PID >= players.length) {
                        throw new IllegalArgumentException("Invalid player ID");
                    }

                    if (PID >= 0) {
                        // Check for a valid move recursively
                        if (isMoveLegal(PID, move, rec + 1)) {
                            return true;
                        }
                    } else if (adjacentSpaces[i].equals(move)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }



    // Determines whether or not a wall is between two locations.  Assumes the two locations are right next to

    public boolean isBlocked(Point p1, Point p2) {
        int smaller = -1; //
        // if the two spaces are in the same column
        if (p1.x == p2.x) {
            smaller = Math.min(p1.y,p2.y); //finds the point that's higher up
            if (p1.x < 8)
                if (walls[p1.x][smaller] == 2)
                    return true;
            if (p1.x > 0)
                if (walls[p1.x-1][smaller] == 2)
                    return true;
        }
        // if the two spaces are in the same row
        else if (p1.y == p2.y) {
            smaller = Math.min(p1.x,p2.x); //finds the space to the left
            if (p1.y < 8)
                if (walls[smaller][p1.y] == 1)
                    return true;
            if (p1.y > 0)
                if (walls[smaller][p1.y-1] == 1)
                    return true;
        }

        return false;
    }

    // This method determines whether a Player is currently on a particular space.

    private int PlayerOnSpace(Point p) {
        for (int i = 0; i < players.length; i++) {
            if (p.getLocation().equals(players[i].getLocation())) {
                return i;
            }
        }
        return -1;
    }



    // Returns a new State which shows what the Board should be after the move has been made.

    public GameState move(String move) {
        GameState newState = clone();
        newState.readStringFromGUI(move);
        return newState;
    }

   // Called by the move method to make the necessary changes to newState before returning it.

    private void readStringFromGUI(String input) {
        Point xy = new Point();
        Scanner sc = new Scanner(input);
        String firstCh = sc.next();
        if (firstCh.equals("M")) {
            xy.x = sc.nextInt();
            xy.y = sc.nextInt();
            move(xy);
        } else {
            //addAllJumps(false);
            if (firstCh.equals("H")) {
                xy.x = sc.nextInt();
                xy.y = sc.nextInt();
                placeHoriWall(xy);
            } else if (firstCh.equals("V")) {
                xy.x = sc.nextInt();
                xy.y = sc.nextInt();
                placeVertWall(xy);
            }
        }
        //addAllJumps(true);
    }

    // Called by readStringFromGUI(String input) when a horizontal wall needs to be placed.

    private void placeHoriWall(Point xy) {
        if (players[turn].getWalls() > 0) {
            walls[xy.x][xy.y] = 2;
            graph.removeEdge(new Point(xy.x,xy.y), new Point(xy.x,xy.y+1));
            graph.removeEdge(new Point(xy.x+1,xy.y), new Point(xy.x+1,xy.y+1));
            players[turn].decrementWall();
            nextTurn();
        }
    }

    // Called by readStringFromGUI(String input) when a vertical wall needs to be placed.

    private void placeVertWall(Point xy) {
        if (players[turn].getWalls() > 0) {
            walls[xy.x][xy.y] = 1;
            graph.removeEdge(new Point(xy.x,xy.y), new Point(xy.x+1,xy.y));
            graph.removeEdge(new Point(xy.x,xy.y+1), new Point(xy.x+1,xy.y+1));
            players[turn].decrementWall();
            nextTurn();
        }
    }

    // Called by readStringFromGUI(String input) when Player needs to be moved.  Moves the current Player

    private void move(Point p) {
        players[turn].setLocation(p);
        nextTurn();
    }

   //Returns an int representing the next Player.  If a player has been removed

    public int getNextPlayerNum() {
        int num = turn;

            num = (num + players.length + 1) % players.length;

        return num;
    }




    // Returns a reference to the Player Object which is moved previously.  If that Player has

    public Player getPrevPlayer() {
        int num = turn;

        num = (num + players.length - 1) % players.length;

        return players[num];
    }

   // Called after any move is made to increment turn;

    private void nextTurn() {
        turn = getNextPlayerNum();
    }
   // Returns an array representing the locations of the walls.

    public int[][] getWalls() {
        return walls;
    }

   // Returns a reference to the array containing the information about each Player.

    public Player[] getPlayerArray() {
        return players;
    }

    public Player getCurrentPlayer() {
        return players[turn];
    }

    // Returns the turn number.
    public int getTurn() {
        return turn;
    }


    // Returns true if the specified Player has any walls left.  Otherwise, it returns false.

    public boolean hasWalls(int player) {
        return players[player].hasWalls();
    }

    // Returns the number of walls the specified Player has left.

    public int numberOfWalls(int player){
        return players[player].getWalls();
    }

    //Returns an int representing the current Player's type.



    public GameState clone() {
        return new GameState(walls, players, turn, graph);
    }

}
