package wordnet;

import java.util.HashMap;
import java.util.Map;

public class Outcast {
    private final WordNet wordNet;

    public Outcast(WordNet wordnet) {
        if (wordnet == null) {
            throw new IllegalArgumentException("argument is null");
        }
        this.wordNet = wordnet;
    }

    public String outcast(String[] nouns) {
        Map<String, Integer> distances = new HashMap<>();
        for (String noun : nouns) {
            int distance = 0;
            for (int i = 0; i < nouns.length; i++) {
                distance += wordNet.distance(noun, nouns[i]);
            }
            distances.put(noun, distance);
        }

        String outcast = "";
        int max = -1;
        for (String noun : distances.keySet()) {
            if (distances.get(noun) > max) {
                max = distances.get(noun);
                outcast = noun;
            }
        }
        return outcast;
    }
}
