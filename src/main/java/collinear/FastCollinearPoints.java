package collinear;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FastCollinearPoints {
    private final LineSegment[] segments;
    public FastCollinearPoints(Point[] points) {
        List<LineSegment> segs = new ArrayList<>();
        for (Point p : points) {
            Point[] pointsCopy = points.clone();
            Arrays.sort(pointsCopy, p.slopeOrder());
            for (int first = 1, last = 2; last < pointsCopy.length;) {
                int ocurrences = 0;
                double slope = p.slopeTo(pointsCopy[first]);
                while (last < pointsCopy.length && Double.compare(slope, p.slopeTo(pointsCopy[last])) == 0) {
                    last++;
                    ocurrences++;
                }
                if (ocurrences >= 2) {
                    Point[] segment = getPoints(pointsCopy, p, first, last);
                    /*
                    This line ensures only a single segment will be added and no repeats.
                    The logic behind is that repeated segments will all have the same natural order sort by sorting
                    these repeated points. Since the slope order sort will go through all the points, only one permutation
                    of these points will begin with p as origin which ensures a single addition of the set of segments.
                     */
                    if (segment[0].compareTo(p) == 0) {
                        segs.add(new LineSegment(segment[0], segment[1]));
                    }
                }
                first = last++;
            }
        }
        segments = segs.toArray(new LineSegment[0]);
    }

    private Point[] getPoints(Point[] points, Point p, int start, int last) {
        List<Point> res = new ArrayList<>();
        res.add(p);
        for (int i = start; i < last; i++) {
            res.add(points[i]);
        }
        Point[] segment = res.toArray(new Point[0]);
        Arrays.sort(segment);
        return new Point[] {segment[0], segment[segment.length - 1]};
    }

    public int numberOfSegments() {
        return segments.length;
    }

    public LineSegment[] segments() {
        return segments.clone();
    }
}
