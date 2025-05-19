package burrows;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    public static void transform() {
        while (!BinaryStdIn.isEmpty()) {
            String s = BinaryStdIn.readString();
            int first = 0;
            StringBuilder bwt = new StringBuilder();
            CircularSuffixArray suffixArray = new CircularSuffixArray(s);
            int n = suffixArray.length();
            for (int i = 0; i < n; i++) {
                if (suffixArray.index(i) == 0) {
                    first = i;
                }
                bwt.append(s.charAt((suffixArray.index(i) + n - 1) % n));
            }
            BinaryStdOut.write(first);
            BinaryStdOut.write(bwt.toString());
        }
        BinaryStdOut.close();
    }

    public static void inverseTransform() {
        while (!BinaryStdIn.isEmpty()) {
            int first = BinaryStdIn.readInt();
            String s = BinaryStdIn.readString();

            char[] t = s.toCharArray();
            int[] next = getNextArray(t);
            StringBuilder originalString = getStringBuilder(next, s, first);
            BinaryStdOut.write(originalString.toString());
        }
        BinaryStdOut.close();
    }

    private static StringBuilder getStringBuilder(int[] next, String s, int first) {
        StringBuilder sortedString = new StringBuilder();
        for (int n : next) {
            sortedString.append(s.charAt(n));
        }
        StringBuilder originalString = new StringBuilder();
        originalString.append(sortedString.charAt(first));
        int nextValue = next[first];
        while (originalString.length() != s.length()) {
            originalString.append(sortedString.charAt(nextValue));
            nextValue = next[nextValue];
        }
        return originalString;
    }

    private static int[] getNextArray(char[] t) {
        int R = 256;
        int N = t.length;
        int[] count = new int[R];
        int[] aux = new int[N];
        for (char value : t) {
            count[value]++;
        }
        for (int r = 1; r < R; r++) {
            count[r] += count[r-1];
        }
        for (int i = N - 1; i >= 0; i--) {
            char c = t[i];
            int sortedPos = count[c] - 1;
            aux[sortedPos] = i;
            count[c]--;
        }
        return aux;
    }

    public static void main(String[] args) {
        if (args[0].equals("-")) {
            transform();
        }
        else if (args[0].equals("+")) {
            inverseTransform();
        } else {
            throw new IllegalArgumentException("Illegal command line argument");
        }
    }
}
