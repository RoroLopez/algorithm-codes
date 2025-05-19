package burrows;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    public static void encode() {
        char[] sequence = initializeSequence();
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            int pos = getPosition(sequence, c);
            BinaryStdOut.write(pos, 8);
            System.arraycopy(sequence, 0, sequence, 1, pos);
            sequence[0] = c;
        }
        BinaryStdOut.close();
    }

    public static void decode() {
        char[] sequence = initializeSequence();
        while (!BinaryStdIn.isEmpty()) {
            int pos = BinaryStdIn.readInt(8);
            char c = getChar(sequence, pos);
            BinaryStdOut.write(c);
            System.arraycopy(sequence, 0, sequence, 1, pos);
            sequence[0] = c;
        }
        BinaryStdOut.close();
    }

    private static int getPosition(char[] sequence, char c) {
        for (int i = 0; i < sequence.length; i++) {
            if (sequence[i] == c) {
                return i;
            }
        }
        return -1;
    }

    private static char getChar(char[] sequence, int position) {
        for (int i = 0; i < sequence.length; i++) {
            if (i == position) {
                return sequence[i];
            }
        }
        return '\0';
    }

    private static char[] initializeSequence() {
        char[] ascii = new char[256];
        for (int i = 0; i < 256; i++) {
            ascii[i] = (char) i;
        }
        return ascii;
    }

    public static void main(String[] args) {
        if (args[0].equals("-")) {
            encode();
        }
        else if (args[0].equals("+")) {
            decode();
        } else {
            throw new IllegalArgumentException("Illegal command line argument");
        }
    }
}
