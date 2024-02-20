package player;


import java.awt.Point;


public abstract class NextMove {

    private Point xy;

    public abstract void setGoalLine();
    public abstract Point up();

    // Returns the location directly below the Player as a Point.  If that location is off the board, null is returned.

    public Point down() {
        Point p;
        if (xy.y < 8) {
            p = new Point(xy.x, xy.y+1);
        } else
            p = null;
        return p;
    }

    // Returns the location directly to the left of the Player as a Point.  If that location is off the board, null is returned.

    public Point left() {
        Point p;
        if (xy.x > 0) {
            p = new Point(xy.x-1, xy.y);
        } else
            p = null;
        return p;
    }

    // Returns the location directly to the right of the Player as a Point.  If that location is off the board, null is returned.

    public Point right() {
        Point p;
        if (xy.x < 8) {
            p = new Point(xy.x+1, xy.y);
        } else
            p = null;
        return p;
    }


}
