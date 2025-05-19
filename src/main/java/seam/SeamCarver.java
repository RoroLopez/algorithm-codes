package seam;

import edu.princeton.cs.algs4.Picture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SeamCarver {
    private boolean isHorizontal;
    private int[][] pixels;

    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("picture argument is null");
        }
        pixels = new int[picture.height()][picture.width()];
        for (int col = 0; col < picture.width(); col++) {
            for (int row = 0; row < picture.height(); row++) {
                pixels[row][col] = picture.getARGB(col, row); //getRGB for grader
            }
        }
    }

    public Picture picture() {
        Picture picture = new Picture(width(), height());
        for (int col = 0; col < width(); col++) {
            for (int row = 0; row < height(); row++) {
                picture.setARGB(col, row, pixels[row][col]); //setRGB for grader
            }
        }
        return picture;
    }

    public int width() {
        return pixels[0].length;
    }

    public int height() {
        return pixels.length;
    }

    public double energy(int x, int y) {
        if (!arePixelsValid(x, y)) {
            throw new IllegalArgumentException("x and y must be between 0 and width - 1 and height - 1 respectively");
        }
        if (isBorderPixel(x, y)) {
            return 1000;
        }

        int leftPixel = pixels[y][x-1];
        int rightPixel = pixels[y][x+1];
        int topPixel = pixels[y-1][x];
        int bottomPixel = pixels[y+1][x];

        double xGradient = calculateGradient(
                getRComponent(leftPixel),
                getGComponent(leftPixel),
                getBComponent(leftPixel),
                getRComponent(rightPixel),
                getGComponent(rightPixel),
                getBComponent(rightPixel)
        );

        double yGradient = calculateGradient(
                getRComponent(topPixel),
                getGComponent(topPixel),
                getBComponent(topPixel),
                getRComponent(bottomPixel),
                getGComponent(bottomPixel),
                getBComponent(bottomPixel)
        );
        return Math.sqrt(xGradient + yGradient);
    }

    private boolean arePixelsValid(int x, int y) {
        return x >= 0 && x < width() && y >= 0 && y < height();
    }

    private double calculateGradient(int r1, int g1, int b1, int r2, int g2, int b2) {
        int r = r2 - r1;
        int g = g2 - g1;
        int b = b2 - b1;
        return Math.pow(r, 2) + Math.pow(g, 2) + Math.pow(b, 2);
    }

    private boolean isBorderPixel(int x, int y) {
        return x == 0 || y == 0 || x == width() - 1 || y == height() - 1;
    }

    private int getRComponent(int argb) {
        return (argb >> 16) & 0xFF;
    }

    private int getGComponent(int argb) {
        return (argb >> 8) & 0xFF;
    }

    private int getBComponent(int argb) {
        return argb & 0xFF;
    }

    public int[] findHorizontalSeam() {
        isHorizontal = true;
        return findVerticalSeam();
    }

    private int map2dCoordinatesTo1d(int row, int col, int colSize) {
        return row * colSize + col;
    }

    private int map1dCoordinatesTo2dX(int index, int colSize) {
        return index / colSize;
    }

    private int map1dCoordinatesTo2dY(int index, int colSize) {
        return index % colSize;
    }

    public int[] findVerticalSeam() {
        double[] energy = new double[height()*width()];
        int[] edgeTo = new int[width()*height()];
        double[] distTo = new double[width()*height()];
        int n;
        if (isHorizontal) {
            n = width()*height() - height();
        } else {
            n = height()*width() - width();
        }
        List<Integer> seam = new ArrayList<>();

        if (isHorizontal) {
            for (int col = 0; col < width(); col++) {
                for (int row = 0; row < height(); row++) {
                    int index = map2dCoordinatesTo1d(col, row, height());
                    energy[index] = energy(col, row);
                }
            }
            for (int s = 0; s < height(); s++) {
                distTo[s] = 1000;
            }
            for (int col = 1; col < width(); col++) {
                for (int row = 0; row < height(); row++) {
                    int index = map2dCoordinatesTo1d(col, row, height());
                    distTo[index] = Double.POSITIVE_INFINITY;
                }
            }

            for (int i = 0; i < n; i++) {
                int x = map1dCoordinatesTo2dX(i, height());
                int y = map1dCoordinatesTo2dY(i, height());

                if (y - 1 >= 0 && y + 1 < height()) {
                    int right = map2dCoordinatesTo1d(x+1, y+1, height());
                    int center = map2dCoordinatesTo1d(x+1, y, height());
                    int left = map2dCoordinatesTo1d(x+1, y-1, height());

                    relax(i, center, distTo, edgeTo, energy);
                    relax(i, right, distTo, edgeTo, energy);
                    relax(i, left, distTo, edgeTo, energy);
                }
                else if (y + 1 < height()) {
                    int right = map2dCoordinatesTo1d(x+1, y+1, height());
                    int center = map2dCoordinatesTo1d(x+1, y, height());

                    relax(i, center, distTo, edgeTo, energy);
                    relax(i, right, distTo, edgeTo, energy);
                } else {
                    int left = map2dCoordinatesTo1d(x+1, y-1, height());
                    int center = map2dCoordinatesTo1d(x+1, y, height());

                    relax(i, center, distTo, edgeTo, energy);
                    relax(i, left, distTo, edgeTo, energy);
                }
            }

            int index = getMinimumIndexOfLastRow(distTo, width()*height() - height(), width()*height());
            int y = map1dCoordinatesTo2dY(index, height());
            int x = map1dCoordinatesTo2dX(index, height());
            while (x != 0) {
                seam.add(y);
                index = edgeTo[index];
                y = map1dCoordinatesTo2dY(index, height());
                x = map1dCoordinatesTo2dX(index, height());
            }
            seam.add(y);
        } else {
            for (int row = 0; row < height(); row++) {
                for (int col = 0; col < width(); col++) {
                    energy[map2dCoordinatesTo1d(row, col, width())] = energy(col, row);
                }
            }
            for (int s = 0; s < width(); s++) {
                distTo[s] = 1000;
            }
            for (int row = 1; row < height(); row++) {
                for (int col = 0; col < width(); col++) {
                    distTo[map2dCoordinatesTo1d(row, col, width())] = Double.POSITIVE_INFINITY;
                }
            }
            for (int i = 0; i < n; i++) {
                int x = map1dCoordinatesTo2dX(i, width());
                int y = map1dCoordinatesTo2dY(i, width());

                if (y - 1 >= 0 && y + 1 < width()) {
                    int right = map2dCoordinatesTo1d(x+1, y+1, width());
                    int center = map2dCoordinatesTo1d(x+1, y, width());
                    int left = map2dCoordinatesTo1d(x+1, y-1, width());

                    relax(i, center, distTo, edgeTo, energy);
                    relax(i, right, distTo, edgeTo, energy);
                    relax(i, left, distTo, edgeTo, energy);
                }
                else if (y + 1 < width()) {
                    int right = map2dCoordinatesTo1d(x+1, y+1, width());
                    int center = map2dCoordinatesTo1d(x+1, y, width());

                    relax(i, center, distTo, edgeTo, energy);
                    relax(i, right, distTo, edgeTo, energy);
                } else {
                    int left = map2dCoordinatesTo1d(x+1, y-1, width());
                    int center = map2dCoordinatesTo1d(x+1, y, width());
                    relax(i, center, distTo, edgeTo, energy);
                    relax(i, left, distTo, edgeTo, energy);
                }
            }

            int index = getMinimumIndexOfLastRow(distTo, width()*height() - width(), width()*height());
            int y = map1dCoordinatesTo2dY(index, width());
            int x = map1dCoordinatesTo2dX(index, width());
            while (x != 0) {
                seam.add(y);
                index = edgeTo[index];
                y = map1dCoordinatesTo2dY(index, width());
                x = map1dCoordinatesTo2dX(index, width());
            }
            seam.add(y);
        }
        Collections.reverse(seam);
        isHorizontal = false;
        return seam.stream().mapToInt(i -> i).toArray();
    }

    private void relax(int from, int to, double[] distTo, int[] edgeTo, double[] energy) {
        if (distTo[to] > distTo[from] + energy[to]) {
            distTo[to] = distTo[from] + energy[to];
            edgeTo[to] = from;
        }
    }

    private int getMinimumIndexOfLastRow(double[] distances, int start, int end) {
        double min = Double.POSITIVE_INFINITY;
        int index = -1;
        for (int i = start; i < end; i++) {
            if (distances[i] < min) {
                min = distances[i];
                index = i;
            }
        }
        return index;
    }

    public void removeHorizontalSeam(int[] seam) {
        if (height() <= 1) {
            throw new IllegalArgumentException("can't remove horizontal seam");
        }
        if (seam == null) {
            throw new IllegalArgumentException("seam argument is null");
        }
        if (!isSeamValid(seam) || seam.length != width() || !isSeamRangeValidHorizontal(seam)) {
            throw new IllegalArgumentException("seam argument is invalid");
        }
        int[][] newPixels = new int[height()-1][width()];

        for (int j = 0; j < width(); j++) {
            for (int i = 0; i < seam[j]; i++) {
                newPixels[i][j] = pixels[i][j];
            }
            for (int i = seam[j] + 1; i < height(); i++) {
                newPixels[i - 1][j] = pixels[i][j];
            }
        }

        pixels = newPixels;
    }

    public void removeVerticalSeam(int[] seam) {
        if (width() <= 1) {
            throw new IllegalArgumentException("can't remove vertical seam");
        }
        if (seam == null) {
            throw new IllegalArgumentException("seam argument is null");
        }
        if (!isSeamValid(seam) || seam.length != height() || !isSeamRangeValidVertical(seam)) {
            throw new IllegalArgumentException("seam argument is invalid");
        }

        int[][] newPixels = new int[height()][width()-1];

        for (int i = 0; i < height(); i++) {
            System.arraycopy(pixels[i], 0, newPixels[i], 0, seam[i]);
            if (seam[i] < width() - 1) {
                System.arraycopy(pixels[i], seam[i] + 1, newPixels[i], seam[i], width() - seam[i] - 1);
            }
        }

        pixels = newPixels;
    }

    private boolean isSeamValid(int[] seam) {
        for (int i = 0; i < seam.length - 1; i++) {
            if (Math.abs(seam[i] - seam[i+1]) > 1) {
                return false;
            }
        }
        return true;
    }

    private boolean isSeamRangeValidHorizontal(int[] seam) {
        for (int i : seam) {
            if (i < 0 || i >= height()) {
                return false;
            }
        }
        return true;
    }

    private boolean isSeamRangeValidVertical(int[] seam) {
        for (int i : seam) {
            if (i < 0 || i >= width()) {
                return false;
            }
        }
        return true;
    }
}
