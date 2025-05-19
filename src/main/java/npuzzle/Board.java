package npuzzle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {
    private final int[] board;
    private final Coordinate[] goalBoard;
    private final int n;

    public Board(int[][] tiles) {
        n = tiles.length;
        board = new int[n * n];
        int counter = 0;
        for (int[] row : tiles) {
            for (int value : row) {
                board[counter++] = value;
            }
        }
        goalBoard = createGoalBoard();
    }

    private Coordinate[] createGoalBoard() {
        Coordinate[] coordinates = new Coordinate[n * n - 1];
        for (int i = 0; i < n*n - 1; i++) {
            int x = i / n;
            int y = i % n;
            coordinates[i] = new Coordinate(x, y);
        }
        return coordinates;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(n).append("\n");
        for (int i = 0; i < n*n; i++) {
            if (i != 0 && i % n == 0) {
                s.append("\n");
            }
            s.append(String.format("%2d ", board[i]));
        }
        return s.toString();
    }

    public int dimension() {
        return n;
    }

    public int hamming() {
        int wrongPosition = 0;
        for (int i = 0; i < n*n - 1; i++) {
            if (board[i] != i + 1) {
                wrongPosition++;
            }
        }
        return wrongPosition;
    }

    public int manhattan() {
        int totalDistance = 0;
        for (int i = 0; i < n*n; i++) {
            if (board[i] != 0) {
                int boardX = i / n;
                int boardY =  i % n;
                totalDistance += Math.abs(boardX - goalBoard[board[i] - 1].x) + Math.abs(boardY - goalBoard[board[i] - 1].y);
            }
        }
        return totalDistance;
    }

    public boolean isGoal() {
        return hamming() == 0;
    }

    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        Board that = (Board) y;
        return Arrays.equals(board, that.board);
    }

    public Iterable<Board> neighbors() {
        int x = 0;
        int y = 0;
        int zeroIndex = 0;
        for (int i = 0; i < n*n; i++) {
            if (board[i] == 0) {
                x = i / n;
                y = i % n;
                zeroIndex = i;
                break;
            }
        }
        Coordinate zeroLocation = new Coordinate(x, y);
        List<Board> neighbors = new ArrayList<>();

        if (zeroLocation.y - 1 >= 0) {
            int left = map2dCoordinatesTo1d(zeroLocation.x, zeroLocation.y - 1);
            int[] leftBoard = board.clone();
            neighbors.add(swapTiles(leftBoard, left, zeroIndex));
        }

        if (zeroLocation.y + 1 < n) {
            int right = map2dCoordinatesTo1d(zeroLocation.x, zeroLocation.y + 1);
            int[] rightBoard = board.clone();
            neighbors.add(swapTiles(rightBoard, right, zeroIndex));
        }

        if (zeroLocation.x - 1 >= 0) {
            int top = map2dCoordinatesTo1d(zeroLocation.x - 1, zeroLocation.y);
            int[] topBoard = board.clone();
            neighbors.add(swapTiles(topBoard, top, zeroIndex));
        }

        if (zeroLocation.x + 1 < n) {
            int bottom = map2dCoordinatesTo1d(zeroLocation.x + 1, zeroLocation.y);
            int[] bottomBoard = board.clone();
            neighbors.add(swapTiles(bottomBoard, bottom, zeroIndex));
        }

        return neighbors;
    }

    private Board swapTiles(int[] board, int index1, int index2) {
        int tmp = board[index1];
        board[index1] = board[index2];
        board[index2] = tmp;

        int[][] newBoard = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                newBoard[i][j] = board[i * n + j];
            }
        }
        return new Board(newBoard);
    }

    private int map2dCoordinatesTo1d(int row, int col) {
        return row * n + col;
    }

    public Board twin() {
        int[] twinBoard = board.clone();
        int changeIndex = 0;
        while (twinBoard[changeIndex] == 0 || twinBoard[changeIndex+1] == 0) {
            changeIndex++;
        }
        return swapTiles(twinBoard, changeIndex, changeIndex+1);
    }

    private static class Coordinate {
        int x;
        int y;

        public Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
