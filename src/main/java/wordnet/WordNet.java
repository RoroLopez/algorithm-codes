package wordnet;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.RedBlackBST;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class WordNet {
    private final SAP sap;
    private final RedBlackBST<String, List<Integer>> synsetIds;
    private final Map<Integer, List<String>> synsetMap;

    /*
    Ideas of data structures:
    - A hashset to map every synset ID to the corresponding noun
    - A RB-BST for storing the nouns in NlogN order and retrieving in logN order
        The key is the noun and the value are the synset IDs associated to the noun

      Corner case: What happens if there's more than one noun associated to the synset ID?
      - For hashset, store the id and the list of nouns in a single entry
      - For RB-BST, associate the same synsetID to the same nouns that appear in a single row
     */

    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException("argument is null");
        }
        try {
            In synsetsIn = new In(synsets);
            String line;
            synsetIds = new RedBlackBST<>();
            synsetMap = new HashMap<>();

            while ((line = synsetsIn.readLine()) != null) {
                String[] synsetFields = line.split(",");
                List<String> nouns = getNouns(synsetFields[1]);

                synsetMap.put(Integer.parseInt(synsetFields[0]), nouns);

                for (String noun : nouns) {
                    List<Integer> synset = synsetIds.get(noun);
                    if (synset == null) {
                        synset = new ArrayList<>();
                    }
                    synset.add(Integer.parseInt(synsetFields[0]));
                    synsetIds.put(noun, synset);
                }
            }

            Digraph g = new Digraph(synsetMap.size());

            In hypernymsIn = new In(hypernyms);
            while ((line = hypernymsIn.readLine()) != null) {
                String[] hypernymFields = line.split(",");
                if (hypernymFields.length > 1) {
                    for (int i = 1; i < hypernymFields.length; i++) {
                        g.addEdge(Integer.parseInt(hypernymFields[0]), Integer.parseInt(hypernymFields[i]));
                    }
                }
            }

            if (!isRootedDAG(g)) {
                throw new IllegalArgumentException("graph is not a rooted DAG");
            }

            DirectedCycle directedCycle = new DirectedCycle(g);
            if (directedCycle.hasCycle()) {
                throw new IllegalArgumentException("graph contains a direct cycle");
            }

            sap = new SAP(g);
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("invalid input format in Digraph constructor", e);
        }
    }

    private List<String> getNouns(String nouns) {
        String[] separatedNouns = nouns.split("\\s");
        return Arrays.asList(separatedNouns);
    }

    private boolean isRootedDAG(Digraph digraph) {
        List<Integer> roots = new ArrayList<>();
        for (int v = 0; v < digraph.V(); v++) {
            if (digraph.outdegree(v) == 0) {
                roots.add(v);
            }
        }
        return roots.size() == 1;
    }

    public Iterable<String> nouns() {
        return synsetIds.keys();
    }

    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException("argument is null");
        }
        return synsetIds.contains(word);
    }

    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new IllegalArgumentException("argument is null");
        }
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("nounA and/or nounB are not supported");
        }
        List<Integer> idsA = synsetIds.get(nounA);
        List<Integer> idsB = synsetIds.get(nounB);

        return sap.length(idsA, idsB);
    }

    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new IllegalArgumentException("argument is null");
        }
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("nounA and/or nounB are not supported");
        }
        List<Integer> idsA = synsetIds.get(nounA);
        List<Integer> idsB = synsetIds.get(nounB);

        int ancestor = sap.ancestor(idsA, idsB);
        List<String> nouns = synsetMap.get(ancestor);

        StringBuilder result = new StringBuilder();
        for (String noun : nouns) {
            result.append(noun).append(" ");
        }
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }
}
