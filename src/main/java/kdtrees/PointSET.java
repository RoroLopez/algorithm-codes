package kdtrees;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;

public class PointSET {
    private final SET<Point2D> set;

    public PointSET() {
        this.set = new SET<>();
    }

    public boolean isEmpty() {
        return set.isEmpty();
    }

    public int size() {
        return set.size();
    }

    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Point must not be null");
        }
        set.add(p);
    }

    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Point must not be null");
        }
        return set.contains(p);
    }

    public void draw() {
        for (Point2D p : set) {
            p.draw();
        }
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException("Rectangle must not be null");
        }
        Queue<Point2D> points = new Queue<>();
        for (Point2D p : set) {
            if (rect.contains(p)) {
                points.enqueue(p);
            }
        }
        return points;
    }

    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Point must not be null");
        }
        double minDistance = Integer.MAX_VALUE;
        Point2D champion = null;
        for (Point2D point : set) {
            double distance = point.distanceSquaredTo(p);
            if (distance < minDistance) {
                minDistance = distance;
                champion = point;
            }
        }
        return champion;
    }
}
