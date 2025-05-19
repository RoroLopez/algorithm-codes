package wordnet;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CommonAncestorBFS {
    private final boolean[] marked;
    private final int[] distTo;
    private final Digraph G;
    private Queue<Integer> changes;

    public CommonAncestorBFS(Digraph G) {
        this.marked = new boolean[G.V()];
        this.distTo = new int[G.V()];
        this.G = new Digraph(G);
        this.changes = new Queue<>();

        for (int v = 0; v < G.V(); v++) {
            this.distTo[v] = Integer.MAX_VALUE;
        }
    }

    public int getCommonAncestor(Iterable<Integer> sources) {
        this.validateVertices(sources);

        Queue<Integer> q = new Queue<>();
        Set<Integer> visited = new HashSet<>();
        int ancestor = -1;
        boolean found = false;

        for(int s : sources) {
            q.enqueue(s);
            if (!visited.contains(s)) {
                visited.add(s);
            } else {
                ancestor = s;
                found = true;
            }
        }

        while (!q.isEmpty() && !found) {
            int v = q.dequeue();
            for (int w : G.adj(v)) {
                if (!visited.contains(w)) {
                    q.enqueue(w);
                    visited.add(w);
                } else {
                    found = true;
                    ancestor = w;
                }
            }
        }
        return ancestor;
    }

    public int getLengthCommonAncestor(Iterable<Integer> sources) {
        this.validateVertices(sources);

        int ancestor = getCommonAncestor(sources);
        if (ancestor == -1) {
            return -1;
        }
        int distance = 0;
        Queue<Integer> q = new Queue<>();

        for(int s : sources) {
            this.marked[s] = true;
            this.distTo[s] = 0;
            q.enqueue(s);
            this.changes.enqueue(s);
            while(!q.isEmpty()) {
                int v = q.dequeue();
                for(int w : G.adj(v)) {
                    if (!this.marked[w]) {
                        this.distTo[w] = this.distTo[v] + 1;
                        this.marked[w] = true;
                        q.enqueue(w);
                        this.changes.enqueue(w);
                    }
                }
            }
            distance += distTo(ancestor);
            resetChanges();
        }
        return distance;
    }

    public int getCommonAncestor(Iterable<Integer> v, Iterable<Integer> w) {
        this.validateVertices(v);
        this.validateVertices(w);

        int ancestor = -1;
        Queue<Integer> q = new Queue<>();
        Map<Integer, String> identifiers = new HashMap<>();
        Set<Integer> visitedA = new HashSet<>();
        Set<Integer> visitedB = new HashSet<>();
        boolean found = false;

        for(int s : v) {
            identifiers.put(s, "A");
            visitedA.add(s);
            q.enqueue(s);
        }

        for(int s : w) {
            identifiers.put(s, "B");
        }

        for (Map.Entry<Integer, String> entry : identifiers.entrySet()) {
            if (entry.getValue().equals("B")) {
                if (!visitedA.contains(entry.getKey())) {
                    visitedB.add(entry.getKey());
                    q.enqueue(entry.getKey());
                } else {
                    found = true;
                    ancestor = entry.getKey();
                }
            }
        }

        while (!q.isEmpty() && !found) {
            int current = q.dequeue();
            for (int adj : G.adj(current)) {
                if (identifiers.get(current).equals("A")) {
                    if (!visitedB.contains(adj)) {
                        if (!visitedA.contains(adj)) {
                            q.enqueue(adj);
                            visitedA.add(adj);
                        }
                    } else {
                        found = true;
                        ancestor = adj;
                    }
                    identifiers.put(adj, "A");
                } else {
                    if (!visitedA.contains(adj)) {
                        if (!visitedB.contains(adj)) {
                            q.enqueue(adj);
                            visitedB.add(adj);
                        }
                    } else {
                        found = true;
                        ancestor = adj;
                    }
                    identifiers.put(adj, "B");
                }
            }
        }
        return ancestor;
    }

    public int getLengthCommonAncestor(Iterable<Integer> v, Iterable<Integer> w) {
        this.validateVertices(v);
        this.validateVertices(w);

        int ancestor = getCommonAncestor(v, w);
        if (ancestor == -1) {
            return -1;
        }
        int distance = 0;
        Queue<Integer> q = new Queue<>();

        distance = getDistance(v, ancestor, distance, q);
        resetChanges();

        distance = getDistance(w, ancestor, distance, q);
        return distance;
    }

    private int getDistance(Iterable<Integer> w, int ancestor, int distance, Queue<Integer> q) {
        for(int s : w) {
            this.marked[s] = true;
            this.distTo[s] = 0;
            q.enqueue(s);
            this.changes.enqueue(s);
        }

        while(!q.isEmpty()) {
            int current = q.dequeue();
            for(int adj : G.adj(current)) {
                if (!this.marked[adj]) {
                    this.distTo[adj] = this.distTo[current] + 1;
                    this.marked[adj] = true;
                    q.enqueue(adj);
                    this.changes.enqueue(adj);
                }
            }
        }
        distance += distTo(ancestor);
        return distance;
    }

    private int distTo(int v) {
        this.validateVertex(v);
        return this.distTo[v];
    }

    private void resetChanges() {
        if (!this.changes.isEmpty()) {
            for (int i : changes) {
                this.marked[i] = false;
                this.distTo[i] = Integer.MAX_VALUE;
            }
            this.changes = new Queue<>();
        }
    }

    private void validateVertex(int v) {
        int V = this.marked.length;
        if (v < 0 || v >= V) {
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
        }
    }

    private void validateVertices(Iterable<Integer> vertices) {
        if (vertices == null) {
            throw new IllegalArgumentException("argument is null");
        } else {
            int vertexCount = 0;
            for(Integer v : vertices) {
                ++vertexCount;
                if (v == null) {
                    throw new IllegalArgumentException("vertex is null");
                }
                this.validateVertex(v);
            }
            if (vertexCount == 0) {
                throw new IllegalArgumentException("zero vertices");
            }
        }
    }
}
