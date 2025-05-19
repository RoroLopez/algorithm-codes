package percolation;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private final WeightedQuickUnionUF wuf;
    private final int[][] gridSites;
    private int openSites = 0;
    private final int n;

    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be greater than 0");
        }
        this.n = n;
        wuf = new WeightedQuickUnionUF(n * n + 2);
        gridSites = new int[n][n];
    }

    private int map2dCoordinatesTo1d(int row, int col) {
        return row * n + col;
    }

    public void open(int row, int col) {
        if (row < 1 || col < 1 && row > n || col > n) {
            throw new IllegalArgumentException("index must be greater than 0 and lesser than n+1");
        }
        row = row - 1;
        col = col - 1;
        if (gridSites[row][col] == 0) {
            gridSites[row][col] = 1;
            openSites++;

            if (row == 0) {
                wuf.union(0, map2dCoordinatesTo1d(row, col) + 1);
            }
            if (row == n - 1) {
                wuf.union(n * n + 1, map2dCoordinatesTo1d(row, col) + 1);
            }
            if (col - 1 >= 0 && gridSites[row][col - 1] == 1) {
                wuf.union(map2dCoordinatesTo1d(row, col) + 1, map2dCoordinatesTo1d(row, col - 1) + 1);
            }
            if (col + 1 < n && gridSites[row][col + 1] == 1) {
                wuf.union(map2dCoordinatesTo1d(row, col) + 1, map2dCoordinatesTo1d(row, col + 1) + 1);
            }
            if (row - 1 >= 0 && gridSites[row - 1][col] == 1) {
                wuf.union(map2dCoordinatesTo1d(row, col) + 1, map2dCoordinatesTo1d(row - 1, col) + 1);
            }
            if (row + 1 < n && gridSites[row + 1][col] == 1) {
                wuf.union(map2dCoordinatesTo1d(row, col) + 1, map2dCoordinatesTo1d(row + 1, col) + 1);
            }
        }
    }

    public boolean isOpen(int row, int col) {
        if (row < 1 || col < 1 && row > n || col > n) {
            throw new IllegalArgumentException("index must be greater than 0 and lesser than n+1");
        }
        row = row - 1;
        col = col - 1;
        return gridSites[row][col] == 1;
    }

    public boolean isFull(int row, int col) {
        if (row < 1 || col < 1 && row > n || col > n) {
            throw new IllegalArgumentException("index must be greater than 0 and lesser than n+1");
        }
        row = row - 1;
        col = col - 1;
        return wuf.find(0) == wuf.find(map2dCoordinatesTo1d(row, col) + 1);
    }

    public int numberOfOpenSites() {
        return openSites;
    }

    public boolean percolates() {
        return wuf.find(0) == wuf.find(n * n + 1);
    }

}
