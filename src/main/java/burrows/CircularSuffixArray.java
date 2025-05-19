package burrows;

import edu.princeton.cs.algs4.StdOut;

import java.util.TreeMap;

public class CircularSuffixArray {
    private final int[] index;
    private final String text;

    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException("argument is null");
        }
        text = s;
        int n = text.length();
        index = new int[n];
        TreeMap<Suffix, Integer> sortedSuffixes = new TreeMap<>();
        for (int i = 0; i < n; i++) {
            sortedSuffixes.put(new Suffix(i, text), i);
        }

        int counter = 0;
        for (Integer i : sortedSuffixes.values()) {
            index[counter++] = i;
        }
    }

    public int length() {
        return text.length();
    }

    public int index(int i) {
        if (i < 0 || i >= length()) {
            throw new IllegalArgumentException("invalid range of i");
        }
        return index[i];
    }

    private static class Suffix implements Comparable<Suffix> {
        private final int start;
        private final String str;

        public Suffix(int start, String str) {
            this.start = start;
            this.str = str;
        }

        public char charAt(int i) {
            return str.charAt((start+i) % str.length());
        }

        public int compareTo(Suffix that) {
            if (this == that) return 0;
            for (int i = 0; i < str.length(); i++) {
                if (this.charAt(i) < that.charAt(i)) return -1;
                if (this.charAt(i) > that.charAt(i)) return 1;
            }
            return 0;
        }
    }

    public static void main(String[] args) {
        String text = "ABRACADABRA!";
        CircularSuffixArray suffixArray = new CircularSuffixArray(text);
        StdOut.println(suffixArray.length());
        for (int i = 0; i < text.length(); i++) {
            StdOut.println(suffixArray.index(i));
        }
    }
}
