package maxflowmincut;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseballElimination {
    private final Map<String, Integer> teamsToIndex;
    private final Map<Integer, String> indexToTeam;
    private final int[] losses;
    private final int[] wins;
    private final int[] remaining;
    private final int[][] games;
    private int gameVertices = 0;

    public BaseballElimination(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("argument is null");
        }
        In in = new In(filename);
        String line;
        int totalTeams = Integer.parseInt(in.readLine());
        teamsToIndex = new HashMap<>();
        indexToTeam = new HashMap<>();
        losses = new int[totalTeams];
        wins = new int[totalTeams];
        remaining = new int[totalTeams];
        games = new int[totalTeams][totalTeams];
        int i = 0;
        while ((line = in.readLine()) != null) {
            int j = 0;
            String[] fields = line.split("\\s+");
            String[] cleanFields = cleanFields(fields);
            teamsToIndex.put(cleanFields[0], i);
            indexToTeam.put(i, cleanFields[0]);
            wins[i] = Integer.parseInt(cleanFields[1]);
            losses[i] = Integer.parseInt(cleanFields[2]);
            remaining[i] = Integer.parseInt(cleanFields[3]);

            while (j < totalTeams) {
                games[i][j] = Integer.parseInt(cleanFields[4 + j++]);
            }
            i += 1;
        }
    }

    private String[] cleanFields(String[] fields) {
        List<String> fieldsBuffer = new ArrayList<>();
        for (String field : fields) {
            if (!field.isEmpty()) {
                fieldsBuffer.add(field);
            }
        }
        return fieldsBuffer.toArray(new String[0]);
    }

    public int numberOfTeams() {
        return teamsToIndex.size();
    }

    public Iterable<String> teams() {
        return teamsToIndex.keySet();
    }

    public int wins(String team) {
        if (!teamsToIndex.containsKey(team)) {
            throw new IllegalArgumentException("invalid team");
        }
        return wins[teamsToIndex.get(team)];
    }

    public int losses(String team) {
        if (!teamsToIndex.containsKey(team)) {
            throw new IllegalArgumentException("invalid team");
        }
        return losses[teamsToIndex.get(team)];
    }

    public int remaining(String team) {
        if (!teamsToIndex.containsKey(team)) {
            throw new IllegalArgumentException("invalid team");
        }
        return remaining[teamsToIndex.get(team)];
    }

    public int against(String team1, String team2) {
        if (!teamsToIndex.containsKey(team1) || !teamsToIndex.containsKey(team2)) {
            throw new IllegalArgumentException("invalid team");
        }
        return games[teamsToIndex.get(team1)][teamsToIndex.get(team2)];
    }

    public boolean isEliminated(String team) {
        if (!teamsToIndex.containsKey(team)) {
            throw new IllegalArgumentException("invalid team");
        }
        if (isTriviallyEliminated(team)) {
            return true;
        }
        FlowNetwork network = getNetwork(team);
        FordFulkerson maxflow = new FordFulkerson(network, 0, network.V() - 1);
        boolean eliminated = false;

        for (FlowEdge e : network.adj(0)) {
            if (e.residualCapacityTo(e.to()) != 0) {
                eliminated = true;
                break;
            }
        }
        return eliminated;
    }

    private FlowNetwork getNetwork(String team) {
        int V = 0;
        int teamIndex = teamsToIndex.get(team);
        int gamesVertices = 0;
        List<FlowEdge> edges = new ArrayList<>();
        for (int i = 0; i < numberOfTeams(); i++) {
            if (i == teamIndex) {
                continue;
            }
            for (int j = i + 1; j < numberOfTeams(); j++) {
                if (j == teamIndex) {
                    continue;
                }
                gamesVertices++;
                FlowEdge edge = new FlowEdge(0, ++V, games[i][j]);
                edges.add(edge);
            }
        }
        V += numberOfTeams() - 1 + 2;
        this.gameVertices = gamesVertices;
        FlowNetwork net = new FlowNetwork(V);
        Map<Integer, Integer> nodesToTeam = new HashMap<>();
        int continuationVertex = gameVertices;
        for (int i = 0; i < numberOfTeams(); i++) {
            if (i == teamIndex) {
                continue;
            }
            nodesToTeam.put(i, ++continuationVertex);
        }
        gamesVertices = 1;
        for (int i = 0; i < numberOfTeams(); i++) {
            if (i == teamIndex) {
                continue;
            }
            for (int j = i + 1; j < numberOfTeams(); j++) {
                if (j == teamIndex) {
                    continue;
                }
                FlowEdge edge1 = new FlowEdge(gamesVertices, nodesToTeam.get(i), Double.POSITIVE_INFINITY);
                FlowEdge edge2 = new FlowEdge(gamesVertices++, nodesToTeam.get(j), Double.POSITIVE_INFINITY);
                edges.add(edge1);
                edges.add(edge2);
            }
        }
        for (int i = 0; i < numberOfTeams(); i++) {
            if (i == teamIndex) {
                continue;
            }
            FlowEdge edge = new FlowEdge(nodesToTeam.get(i), V - 1, Math.abs(wins[teamIndex] + remaining[teamIndex] - wins[i]));
            edges.add(edge);
        }
        for (FlowEdge edge : edges) {
            net.addEdge(edge);
        }
        return net;
    }

    private boolean isTriviallyEliminated(String team) {
        int maxWins = Arrays.stream(wins).max().getAsInt();
        int teamMaxWins = wins[teamsToIndex.get(team)] + remaining[teamsToIndex.get(team)];
        return teamMaxWins < maxWins;
    }

    public Iterable<String> certificateOfElimination(String team) {
        if (!teamsToIndex.containsKey(team)) {
            throw new IllegalArgumentException("invalid team");
        }
        int teamIndex = teamsToIndex.get(team);
        FlowNetwork network = getNetwork(team);
        FordFulkerson maxflow = new FordFulkerson(network, 0, network.V() - 1);

        Map<Integer, String> nodesToTeam = new HashMap<>();
        int continuationVertex = gameVertices;
        for (int i = 0; i < numberOfTeams(); i++) {
            if (i == teamIndex) {
                continue;
            }
            nodesToTeam.put(++continuationVertex, indexToTeam.get(i));
        }

        Bag<String> subset = new Bag<>();
        for (int i : nodesToTeam.keySet()) {
            if (maxflow.inCut(i)) {
                subset.add(nodesToTeam.get(i));
            }
        }
        return subset.isEmpty() ? null : subset;
    }
}
