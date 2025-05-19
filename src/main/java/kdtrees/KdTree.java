package kdtrees;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {
    private int size;
    private Node root;

    public KdTree() {
        size = 0;
        root = null;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Point must not be null");
        }
        root = put(root, p, true, 0.0, 0,1.0, 1.0);
    }

    private Node put(Node x, Point2D p, boolean vertical, double xmin, double ymin, double xmax, double ymax) {
        if (x == null) {
            size++;
            return new Node(p, vertical, xmin, ymin, xmax, ymax);
        }

        if (x.point.equals(p)) {
            return x;
        }

        int cmp;
        if (vertical) {
            cmp = Double.compare(p.x(), x.point.x());
            if (cmp < 0) {
                x.lb = put(x.lb, p, false, xmin, ymin, x.point.x(), ymax);
            } else {
                x.rt = put(x.rt, p, false, x.point.x(), ymin, xmax, ymax);
            }
        } else {
            cmp = Double.compare(p.y(), x.point.y());
            if (cmp < 0) {
                x.lb = put(x.lb, p, true, xmin, ymin, xmax, x.point.y());
            } else {
                x.rt = put(x.rt, p, true, xmin, x.point.y(), xmax, ymax);
            }
        }
        return x;
    }

    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Point must not be null");
        }
        return get(root, p, true) != null;
    }

    private Node get(Node x, Point2D p, boolean vertical) {
        if (x == null) {
            return null;
        }

        if (x.point.equals(p)) {
            return x;
        }

        int cmp;
        if (vertical) {
            cmp = Double.compare(p.x(), x.point.x());
            if (cmp < 0) {
                return get(x.lb, p, false);
            }
            return get(x.rt, p, false);
        } else {
            cmp = Double.compare(p.y(), x.point.y());
            if (cmp < 0) {
                return get(x.lb, p, true);
            }
            return get(x.rt, p, true);
        }
    }

    public void draw() {
        Queue<Node> q = new Queue<>();
        q.enqueue(root);
        while (!q.isEmpty()) {
            Node point = q.dequeue();
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.02);
            point.point.draw();

            if (point.vertical) {
                StdDraw.setPenColor(StdDraw.RED);
                StdDraw.setPenRadius(0.01);
                RectHV line = new RectHV(point.point.x(), point.rect.ymin(), point.point.x(), point.rect.ymax());
                line.draw();
            } else {
                StdDraw.setPenColor(StdDraw.BLUE);
                StdDraw.setPenRadius(0.01);
                RectHV line = new RectHV(point.rect.xmin(), point.point.y(), point.rect.xmax(), point.point.y());
                line.draw();
            }

            if (point.lb != null) {
                q.enqueue(point.lb);
            }
            if (point.rt != null) {
                q.enqueue(point.rt);
            }
        }
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException("Rect must not be null");
        }

        Queue<Point2D> points = new Queue<>();
        Queue<Node> q = new Queue<>();
        q.enqueue(root);
        while (!q.isEmpty()) {
            Node p = q.dequeue();

            if (rect.contains(p.point)) {
                points.enqueue(p.point);
            }

            if (p.rect.intersects(rect)) {
                if (p.lb != null) {
                    q.enqueue(p.lb);
                }
                if (p.rt != null) {
                    q.enqueue(p.rt);
                }
            }
        }

        return points;
    }



    public Point2D nearest(Point2D point) {
        if (point == null) {
            throw new IllegalArgumentException("Point must not be null");
        }
        if (root == null) {
            return null;
        }
        Point2D nearest = null;
        double minDistance = Double.MAX_VALUE;
        Stack<Node> stack = new Stack<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            Node node = stack.pop();
            if (node == null) {
                continue;
            }

            if (node.rect.distanceSquaredTo(point) >= minDistance) {
                continue;
            }

            double distance = point.distanceSquaredTo(node.point);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = node.point;
            }

            Node first = node.lb;
            Node second = node.rt;

            if (node.vertical) {
                if (point.x() >= node.point.x()) {
                    first = node.rt;
                    second = node.lb;
                }
            } else {
                if (point.y() >= node.point.y()) {
                    first = node.rt;
                    second = node.lb;
                }
            }

            if (second != null) {
                stack.push(second);
            }

            if (first != null) {
                stack.push(first);
            }
        }

        return nearest;
    }

    private static class Node {
        private final Point2D point;
        private final boolean vertical;
        private final RectHV rect;
        private Node lb;
        private Node rt;

        public Node(Point2D point, boolean vertical, double xmin, double ymin, double xmax, double ymax) {
            this.point = point;
            this.vertical = vertical;
            this.rect = new RectHV(xmin, ymin, xmax, ymax);
        }
    }
}
