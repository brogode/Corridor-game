package player;

import java.awt.Color;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

/**
 * This class stores all the required information about a Player.
 */

public   class Player extends NextMove {
   // starting X values of the Players.

    public final static int X[] = {4, 4};
  // starting Y values of the Players.

    public final static int Y[] = {0, 8};
    // color holds the default Colors of the Players.

    public final static Color[] color = {Color.blue, Color.red};




    // holds the location of the player on the board.

    private Point xy;
    // Holds a Player's ID number.

    private int playerID;
    // Holds the number of walls a Player has left.

    private int walls;
    // Holds the Player's Color.

    private Color col;
   // Holds all of the spaces a Player can move to to win the game.

    private Point[] goalLine;

    /**
     * Holds the goalLine as a set.
     */
    public Set<Point> goalSet;



    public Player(int ID, int startingWalls, Color c) {
        playerID = ID;
        xy = new Point(X[playerID], Y[playerID]);
        setGoalLine();
        setStartingWalls(startingWalls);
        setColor(c);

    }

    //Returns an int representing a Player's ID.

    public int getPlayerID() {
        return playerID;
    }

     // Method called in the constructor to set the goalLine and goalSet.
    @Override
    public void setGoalLine() {
        goalLine = new Point[9];
        goalSet = new HashSet<Point>();
        int temp = 0;
        if (xy.x == 8) {
            temp = 0;
            for (int i = 0; i < goalLine.length; i++) {
                goalLine[i] = new Point(temp, i);
                goalSet.add(goalLine[i]);
            }
        } else if (xy.x == 0) {
            temp = 8;
            for (int i = 0; i < goalLine.length; i++) {
                goalLine[i] = new Point(temp, i);
                goalSet.add(goalLine[i]);
            }
        } else if (xy.y == 8) {
            temp = 0;
            for (int i = 0; i < goalLine.length; i++) {
                goalLine[i] = new Point(i, temp);
                goalSet.add(goalLine[i]);
            }
        } else if (xy.y == 0) {
            temp = 8;
            for (int i = 0; i < goalLine.length; i++) {
                goalLine[i] = new Point(i, temp);
                goalSet.add(goalLine[i]);
            }
        }
    }


     // Checks to see if a Player is has reached their goal.

    public boolean hasWon() {
        for (int i = 0; i < goalLine.length; i++) {
            if (goalSet.contains(xy)) {
                return true;
            }
        }
        return false;
    }

     // Called in the constructor to set the Player's Color.
    private void setColor(Color c) {
        col = c;
    }

     // Returns the Player's Color.
    public Color getColor() {
        return col;
    }


     //Called in the constructor to set the starting walls for the Player.
    private void setStartingWalls(int startingWalls) {
        walls = startingWalls;
    }

     // Returns the number of walls the Player has left.
    public int getWalls() {
        return walls;
    }

    // Decrements the number of walls the Player has left by one.
    public boolean decrementWall() {
        if (walls > 0){
            walls--;
            return true;
        }
        return false;
    }

   // Sets the location of the Player.
    public void setLocation(int i, int j) {
        xy.setLocation(i, j);
    }

    //Sets the location of the Player.
    //loc is the location where the Player wants to move.

    public void setLocation(Point loc) {
        xy.setLocation(loc);
    }

    // Returns the x-coordinate of the Player.

    public int getX() {
        return xy.x;
    }

    //Returns the y-coordinate of the Player.

    public int getY() {
        return xy.y;
    }

    //Returns the location of the Player as a Point Object.

    public Point getLocation() {
        return new Point(xy.x, xy.y);
    }

    // Returns the location directly above the Player as a Point.  If that location is off the board, null is returned.
    @Override
    public Point up() {
        Point p;
        if (xy.y > 0) {
            p = new Point(xy.x, xy.y-1);
        } else
            p = null;
        return p;
    }

    // Returns the location directly below the Player as a Point.  If that location is off the board, null is returned.
    @Override
    public Point down() {
        Point p;
        if (xy.y < 8) {
            p = new Point(xy.x, xy.y+1);
        } else
            p = null;
        return p;
    }

    // Returns the location directly to the left of the Player as a Point.  If that location is off the board, null is returned.
    @Override
    public Point left() {
        Point p;
        if (xy.x > 0) {
            p = new Point(xy.x-1, xy.y);
        } else
            p = null;
        return p;
    }

    // Returns the location directly to the right of the Player as a Point.  If that location is off the board, null is returned.
    @Override
    public Point right() {
        Point p;
        if (xy.x < 8) {
            p = new Point(xy.x+1, xy.y);
        } else
            p = null;
        return p;
    }


    // Determines whether or not the Player has any walls left.

    public boolean hasWalls() {
        if (walls > 0)
            return true;
        return false;
    }

    public Player clone() {
        Player clone = new Player(playerID, walls, col);
        clone.setLocation(getLocation());
        return clone;
    }

    public String toString(){
        return "Player " + (playerID + 1);
    }

}
