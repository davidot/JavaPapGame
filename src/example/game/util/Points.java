package example.game.util;

import java.awt.Point;

/**
 * Utilities class for often used Point operations
 * @author davidot
 */
public class Points {

    /**
     * Gets all the points directly around the point given
     * @param p the middle
     *
     * @return the surrounding points
     */
    public static Point[] getPointsAround(Point p) {
        return new Point[] {new Point(p.x - 1, p.y), new Point(p.x, p.y + 1),
                new Point(p.x, p.y - 1), new Point(p.x + 1, p.y)};
    }

    /**
     * Gets all the points directly around the point given
     * @param x the x coordinate of the middle
     * @param y the y coordinate of the middle
     *
     * @return the surrounding points
     */
    public static Point[] getPointsAround(int x, int y) {
        return new Point[] {new Point(x, y - 1), new Point(x + 1, y), new Point(x, y + 1),
                new Point(x - 1, y)};
    }

    /**
     * Gets all the points directly around the point given and the diagonal points around
     * @param p the middle
     *
     * @return the surrounding points
     */
    public static Point[] getPointsAroundDiagonal(Point p) {
        return new Point[] {new Point(p.x - 1, p.y - 1), new Point(p.x, p.y - 1),
                new Point(p.x + 1, p.y - 1), new Point(p.x - 1, p.y), new Point(p.x + 1, p.y),
                new Point(p.x - 1, p.y + 1), new Point(p.x, p.y + 1), new Point(p.x + 1, p.y + 1)};
    }


    //i know Point has a distance method but i want to be able to use it regardless of whether i
    // have points or ints

    /**
     * Get the distance between two points, based on the pythagorean theorem
     * @param a one point
     * @param b the other point
     *
     * @return the distance between the points
     */
    public static double getDistance(Point a, Point b) {
        if(a.equals(b)) {
            return 0;
        }
        return getDistance(a.x, a.y, b.x, b.y);
    }


    /**
     * Get the distance between two points, based on the pythagorean theorem
     * @param x the x value of point 1
     * @param y the y value of point 1
     * @param p point 2
     *
     * @return the distance in between these points
     */
    public static double getDistance(int x, int y, Point p) {
        return getDistance(x, y, p.x, p.y);
    }

    /**
     * Get the distance between two points, based on the pythagorean theorem
     * @param x1 the x value of point 1
     * @param y1 the y value of point 1
     * @param x2 the x value of point 2
     * @param y2 the y value of point 2
     *
     * @return the distance in between these points
     */
    public static double getDistance(int x1, int y1, int x2, int y2) {
        double px = x1 - x2;
        double py = y1 - y2;
        return Math.sqrt(px * px + py * py);
    }


}
