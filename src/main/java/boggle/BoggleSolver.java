package boggle;

import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.TST;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BoggleSolver {
    private int m, n;
    private final TST<Integer> dictionary;
    /*
    Plan for solving the DFS in Boggle Solver:
        1. Have the marked array for keeping track of already visited nodes
        2. Have to iterate every single node as a source
        3. Base case:
            3a. When all neighbor nodes are marked as TRUE

    Plan for the prefix search operation (in conjunction with DFS)
        1. Need to dynamically building and deleting the string based of the characters
        2. Need to create a data structure of all the dictionary words that supports the prefix operation (Trie/TST?)
            2a. May need to create my own data structure for the prefix-operation efficiently?
     */

    public BoggleSolver(String[] dictionary) {
        this.dictionary = new TST<>();
        int i = 0;
        for (String word : dictionary) {
            this.dictionary.put(word, i++);
        }
    }

    public Iterable<String> getAllValidWords(BoggleBoard board) {
        Set<String> words = new HashSet<>();
        Set<String> seenWords = new HashSet<>();
        this.m = board.rows();
        this.n = board.cols();
        boolean[][] marked = new boolean[m][n];
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                dfs(board, i, j, marked, words, seenWords, sb);
            }
        }
        return words;
    }

    public int scoreOf(String word) {
        if (this.dictionary.contains(word)) {
            int wordLen = word.length();
            if (wordLen >= 8) {
                return 11;
            } else if (wordLen == 7) {
                return 5;
            } else if (wordLen == 6) {
                return 3;
            } else if (wordLen == 5) {
                return 2;
            } else if (wordLen == 4 || wordLen == 3) {
                return 1;
            } else {
                return 0;
            }
        }
        return 0;
    }

    private void dfs(BoggleBoard board, int i, int j, boolean[][] marked, Set<String> words, Set<String> seenWords, StringBuilder sb) {
        if (i < 0 || i >= m || j < 0 || j >= n) {
            return;
        }
        if (marked[i][j]) {
            return;
        }
        marked[i][j] = true;
        boolean specialCase = false;
        if (board.getLetter(i, j) == 'Q') {
            specialCase = true;
            sb.append(board.getLetter(i, j)).append("U");
        } else {
            sb.append(board.getLetter(i, j));
        }
        Queue<String> keys = (Queue<String>) this.dictionary.keysWithPrefix(sb.toString());
        if (keys.isEmpty()) {
            marked[i][j] = false;
            if (specialCase) {
                sb.delete(sb.length() - 2, sb.length());
            } else {
                sb.deleteCharAt(sb.length() - 1);
            }
            return;
        }
        if (!seenWords.contains(sb.toString()) && this.dictionary.contains(sb.toString()) && sb.length() >= 3) {
            words.add(sb.toString());
            seenWords.add(sb.toString());
        }
        List<int[]> neighbors = getNeighbours(i, j);
        for (int[] neighbor : neighbors) {
            dfs(board, neighbor[0], neighbor[1], marked, words, seenWords, sb);
        }

        marked[i][j] = false;
        if (specialCase) {
            sb.delete(sb.length() - 2, sb.length());
        } else {
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    private List<int[]> getNeighbours(int i, int j) {
        List<int[]> neighbours = new ArrayList<>();
        if (i - 1 >= 0) {
            int[] neighbour = new int[2];
            neighbour[0] = i - 1;
            neighbour[1] = j;
            neighbours.add(neighbour);
        }
        if (j - 1 >= 0) {
            int[] neighbour = new int[2];
            neighbour[0] = i;
            neighbour[1] = j - 1;
            neighbours.add(neighbour);
        }
        if (i + 1 < this.m) {
            int[] neighbour = new int[2];
            neighbour[0] = i + 1;
            neighbour[1] = j;
            neighbours.add(neighbour);
        }
        if(j + 1 < this.n) {
            int[] neighbour = new int[2];
            neighbour[0] = i;
            neighbour[1] = j + 1;
            neighbours.add(neighbour);
        }
        if (i - 1 >= 0 && j - 1 >= 0) {
            int[] neighbour = new int[2];
            neighbour[0] = i - 1;
            neighbour[1] = j - 1;
            neighbours.add(neighbour);
        }
        if (i - 1 >= 0 && j + 1 < this.n) {
            int[] neighbour = new int[2];
            neighbour[0] = i - 1;
            neighbour[1] = j + 1;
            neighbours.add(neighbour);
        }
        if (i + 1 < this.m && j + 1 < this.n) {
            int[] neighbour = new int[2];
            neighbour[0] = i + 1;
            neighbour[1] = j + 1;
            neighbours.add(neighbour);
        }
        if (i + 1 < this.m && j - 1 >= 0) {
            int[] neighbour = new int[2];
            neighbour[0] = i + 1;
            neighbour[1] = j - 1;
            neighbours.add(neighbour);
        }
        return neighbours;
    }
}
