package collinear;

import edu.princeton.cs.algs4.StdDraw;

import java.util.Comparator;

public class Point implements Comparable<Point> {
    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw() {
        StdDraw.point(x, y);
    }

    public void drawTo(Point that) {
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public int compareTo(Point that) {
        if (this.y < that.y) {
            return -1;
        }
        if (this.y > that.y) {
            return 1;
        }
        if (this.x < that.x) {
            return -1;
        }
        if (this.x > that.x) {
            return 1;
        }
        return 0;
    }

    public double slopeTo(Point that) {
        if (this.y == that.y && this.x == that.x) {
            return Double.NEGATIVE_INFINITY;
        }
        if (this.y == that.y) {
            return 0.0;
        }
        if (this.x == that.x) {
            return Double.POSITIVE_INFINITY;
        }
        return (double) (that.y - this.y) / (that.x - this.x);
    }

    public Comparator<Point> slopeOrder() {
        return new BySlopeOrder(this);
    }

    private static class BySlopeOrder implements Comparator<Point> {
        private final Point origin;
        public BySlopeOrder(Point point) {
            this.origin = point;
        }

        public int compare(Point p1, Point p2) {
            double slope1 = origin.slopeTo(p1);
            double slope2 = origin.slopeTo(p2);

            return Double.compare(slope1, slope2);
        }
    }
}
