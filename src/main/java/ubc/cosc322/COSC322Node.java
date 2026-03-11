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
        int pieceId = whitePieces ? 1 : 2;

        for (int i = 0; i < 121; i++) {
            if (state[i] == pieceId) {
                for (int j : getReachableSquares(-100, i)) {
                    for (int k : getReachableSquares(i, j)) {
                        nodes.add(generateChild(i, j, k, pieceId));
                    }
                }
            }
        }
        return nodes;
    }

    // Returns all squares reachable from newPos in queen-move lines.
    // oldPos is treated as empty (the queen vacated it); pass -100 if no old position.
    public ArrayList<Integer> getReachableSquares(int oldPos, int newPos) {
        ArrayList<Integer> squares = new ArrayList<>();

        for (int i = newPos - 11; i >= 0; i -= 11) {          // up
            if (i % 11 == 0 || i / 11 == 0) continue;
            if (i != oldPos && state[i] != 0) break;
            squares.add(i);
        }
        for (int i = newPos + 11; i < 121; i += 11) {          // down
            if (i % 11 == 0 || i / 11 == 0) continue;
            if (i != oldPos && state[i] != 0) break;
            squares.add(i);
        }
        for (int i = newPos - 1; i % 11 != 0 && i >= 0; i--) { // left
            if (i % 11 == 0 || i / 11 == 0) continue;
            if (i != oldPos && state[i] != 0) break;
            squares.add(i);
        }
        for (int i = newPos + 1; i % 11 != 0 && i < 121; i++) { // right
            if (i % 11 == 0 || i / 11 == 0) continue;
            if (i != oldPos && state[i] != 0) break;
            squares.add(i);
        }
        for (int i = newPos - 10; i % 11 != 0 && i >= 0; i -= 10) {  // up-right
            if (i % 11 == 0 || i / 11 == 0) continue;
            if (i != oldPos && state[i] != 0) break;
            squares.add(i);
        }
        for (int i = newPos - 12; i % 11 != 10 && i >= 0; i -= 12) { // up-left
            if (i % 11 == 0 || i / 11 == 0) continue;
            if (i != oldPos && state[i] != 0) break;
            squares.add(i);
        }
        for (int i = newPos + 10; i % 11 != 10 && i < 121; i += 10) { // down-left
            if (i % 11 == 0 || i / 11 == 0) continue;
            if (i != oldPos && state[i] != 0) break;
            squares.add(i);
        }
        for (int i = newPos + 12; i % 11 != 0 && i < 121; i += 12) {  // down-right
            if (i % 11 == 0 || i / 11 == 0) continue;
            if (i != oldPos && state[i] != 0) break;
            squares.add(i);
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
            if (state[i] == 1) { wDist[i] = 0; wQueue.add(i); }
            else if (state[i] == 2) { bDist[i] = 0; bQueue.add(i); }
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