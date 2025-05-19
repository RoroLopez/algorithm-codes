package npuzzle;

import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;

public class Solver {
    private final int moves;
    private final Stack<Board> backtrack = new Stack<>();

    /*
    One way to cache manhattan distance:
    Calculate the manhattan distance for the initial node and store either in this code block
    or the search node.
    After the loop starts, check the priority of the search node after delMin():
        if the priority decreased then the manhattan distance decreases
        if the priority increased, then the manhattan distance increases
    This has to take into account the fact that priority has to subtract the number of moves to check the two cases
    above.
     */
    public Solver(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException("npuzzle.Board must not be null");
        }

        SearchNode initialSearchNode = new SearchNode(0, initial, null);
        SearchNode twinInitialSearchNode = new SearchNode(0, initial.twin(), null);

        MinPQ<SearchNode> pq = new MinPQ<>();
        MinPQ<SearchNode> twinPQ = new MinPQ<>();

        pq.insert(initialSearchNode);
        twinPQ.insert(twinInitialSearchNode);

        SearchNode currentSearchNode = pq.delMin();
        SearchNode currentTwinSearchNode = twinPQ.delMin();

        while (currentSearchNode.manhattanDistance != 0 && currentTwinSearchNode.manhattanDistance != 0) {
            Iterable<Board> neighbors = currentSearchNode.board.neighbors();
            Iterable<Board> twinNeighbors = currentTwinSearchNode.board.neighbors();

            for (Board board : neighbors) {
                if (currentSearchNode.previous != null && board.equals(currentSearchNode.previous.board)) {
                    continue;
                }
                pq.insert(new SearchNode(currentSearchNode.moves + 1, board, currentSearchNode));
            }

            for (Board board : twinNeighbors) {
                if (currentTwinSearchNode.previous != null && board.equals(currentTwinSearchNode.previous.board)) {
                    continue;
                }
                twinPQ.insert(new SearchNode(currentSearchNode.moves + 1, board, currentTwinSearchNode));
            }

            currentSearchNode = pq.delMin();
            currentTwinSearchNode = twinPQ.delMin();
        }

        if (currentSearchNode.manhattanDistance == 0) {
            moves = currentSearchNode.moves;
            while (currentSearchNode != null) {
                backtrack.push(currentSearchNode.board);
                currentSearchNode = currentSearchNode.previous;
            }
        } else {
            moves = -1;
        }
    }

    public boolean isSolvable() {
        return moves != -1;
    }

    public int moves() {
        return moves;
    }

    public Iterable<Board> solution() {
        if (isSolvable()) {
            return backtrack;
        }
        return null;
    }

    private static class SearchNode implements Comparable<SearchNode> {
        private final SearchNode previous;
        private final int priority;
        private final int moves;
        private final Board board;
        private final int manhattanDistance;

        public SearchNode(int moves, Board board, SearchNode previousNode) {
            this.previous = previousNode;
            this.board = board;
            this.manhattanDistance = board.manhattan();
            this.moves = moves;
            this.priority = moves + manhattanDistance;
        }

        public int compareTo(SearchNode other) {
            return this.priority - other.priority;
        }
    }
}
