package ubc.cosc322;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class COSC322Node {

    public int[] state;   // 121-element board (11x11 with row 0 and col 0 as padding)
    public int g;
    public COSC322Node parent;

    public COSC322Node(int[] state, int costToReach) {
        this.state = state;
        this.g = costToReach;
    }

    public ArrayList<COSC322Node> expandNode(boolean whitePieces) {
        ArrayList<COSC322Node> nodes = new ArrayList<>();
        int pieceId = whitePieces ? 2 : 1;

        for (int i = 0; i < 121; i++) {
            if (i % 11 == 0 || i / 11 == 0) continue;
            if (state[i] == pieceId) {
                for (int j : getReachableSquares(i, i)) {
                    for (int k : getReachableSquares(i, j)) {
                        nodes.add(generateChild(i, j, k, pieceId));
                    }
                }
            }
        }
        return nodes;
    }

    public ArrayList<Integer> getReachableSquares(int oldPos, int newPos) {
        ArrayList<Integer> squares = new ArrayList<>();
        int row = newPos / 11;
        int col = newPos % 11;

        int[][] directions = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1}, // up, down, left, right
            {-1, 1}, {-1, -1}, {1, -1}, {1, 1} // up-right, up-left, down-left, down-right
        };

        for (int[] dir : directions) {
            int r = row;
            int c = col;
            while (true) {
                r += dir[0];
                c += dir[1];
                if (r < 1 || r > 10 || c < 1 || c > 10) break;
                
                int next = r * 11 + c;
                if (next != oldPos && state[next] != 0) break;
                squares.add(next);
            }
        }
        return squares;
    }

    private COSC322Node generateChild(int from, int to, int arrow, int pieceId) {
        int[] newState = state.clone();
        newState[from] = 0;
        newState[to] = pieceId;
        newState[arrow] = 3;
        return new COSC322Node(newState, g + 1);
    }

    // Returns territory score from the perspective of whitePieces player.
    // Positive = we control more territory, negative = opponent controls more.
    public int evaluate(boolean whitePieces) {
        return territoryScore(whitePieces);
    }

    // BFS flood fill from all queens simultaneously.
    // A square belongs to the side that reaches it first.
    // Score = our squares - opponent squares.
    private int territoryScore(boolean whitePieces) {
        int[] wDist = new int[121];
        int[] bDist = new int[121];
        final int INF = Integer.MAX_VALUE / 2;

        for (int i = 0; i < 121; i++) {
            wDist[i] = INF;
            bDist[i] = INF;
        }

        Queue<Integer> wQueue = new LinkedList<>();
        Queue<Integer> bQueue = new LinkedList<>();

        for (int i = 0; i < 121; i++) {
            if (i % 11 == 0 || i / 11 == 0) continue;
            if (state[i] == 2) { wDist[i] = 0; wQueue.add(i); }
            else if (state[i] == 1) { bDist[i] = 0; bQueue.add(i); }
        }

        bfsFlood(wDist, wQueue);
        bfsFlood(bDist, bQueue);

        int wTerritory = 0, bTerritory = 0;
        for (int i = 0; i < 121; i++) {
            if (i % 11 == 0 || i / 11 == 0) continue;
            if (state[i] != 0) continue; // occupied or burned
            if (wDist[i] < bDist[i]) wTerritory++;
            else if (bDist[i] < wDist[i]) bTerritory++;
        }

        return whitePieces ? wTerritory - bTerritory : bTerritory - wTerritory;
    }

    // Step-by-step BFS in 8 directions (treats queens and arrows as walls).
    private void bfsFlood(int[] dist, Queue<Integer> queue) {
        int[] steps = {-11, 11, -1, 1, -10, -12, 10, 12};

        while (!queue.isEmpty()) {
            int pos = queue.poll();
            for (int step : steps) {
                int next = pos + step;
                if (next < 0 || next >= 121) continue;
                if (next % 11 == 0 || next / 11 == 0) continue;
                if (state[next] != 0) continue; // blocked by queen or arrow
                if (dist[next] > dist[pos] + 1) {
                    dist[next] = dist[pos] + 1;
                    queue.add(next);
                }
            }
        }
    }
}