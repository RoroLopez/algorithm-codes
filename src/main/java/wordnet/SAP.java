package wordnet;

import edu.princeton.cs.algs4.Digraph;

import java.util.ArrayList;
import java.util.List;

public class SAP {
    private final CommonAncestorBFS commonAncestorBFS;

    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException("argument is null");
        }
        commonAncestorBFS = new CommonAncestorBFS(G);
    }

    public int length(int v, int w) {
        List<Integer> l = new ArrayList<>();
        l.add(v);
        l.add(w);

        return commonAncestorBFS.getLengthCommonAncestor(l);
    }

    public int ancestor(int v, int w) {
        List<Integer> l = new ArrayList<>();
        l.add(v);
        l.add(w);

        return commonAncestorBFS.getCommonAncestor(l);
    }

    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException("argument is null");
        }
        return commonAncestorBFS.getLengthCommonAncestor(v, w);
    }

    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException("argument is null");
        }
        return commonAncestorBFS.getCommonAncestor(v, w);
    }
}
