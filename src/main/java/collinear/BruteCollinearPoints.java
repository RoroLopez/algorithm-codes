package collinear;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BruteCollinearPoints {
    private final LineSegment[] segments;
    public BruteCollinearPoints(Point[] points) {
        if (points == null || hasNull(points)) {
            throw new IllegalArgumentException("Array and individual points cannot be null");
        }
        Point[] pointsCopy = points.clone();
        Arrays.sort(pointsCopy);
        if (hasRepeatedPoint(pointsCopy)) {
            throw new IllegalArgumentException("Points cannot be repeated");
        }
        List<LineSegment> segs = new ArrayList<>();
        for (int p = 0; p < pointsCopy.length - 3; p++) {
            for (int q = p + 1; q < pointsCopy.length; q++) {
                for (int r = q + 1; r < pointsCopy.length; r++) {
                    for (int s = r + 1; s < pointsCopy.length; s++) {
                        double slope1 = pointsCopy[p].slopeTo(pointsCopy[q]);
                        double slope2 = pointsCopy[p].slopeTo(pointsCopy[r]);
                        double slope3 = pointsCopy[p].slopeTo(pointsCopy[s]);
                        if (slope1 == slope2 && slope1 == slope3 && slope2 == slope3) {
                            segs.add(new LineSegment(pointsCopy[p], pointsCopy[s]));
                        }
                    }
                }
            }
        }

        segments = segs.toArray(new LineSegment[0]);
    }

    private boolean hasNull(Point[] points) {
        for (Point p : points) {
            if (p == null) {
                return true;
            }
        }
        return false;
    }

    private boolean hasRepeatedPoint(Point[] points) {
        for (int i = 0; i < points.length - 1; i++) {
            if (points[i].compareTo(points[i+1]) == 0) {
                return true;
            }
        }
        return false;
    }

    public int numberOfSegments() {
        return segments.length;
    }

    public LineSegment[] segments() {
        return segments.clone();
    }
}
