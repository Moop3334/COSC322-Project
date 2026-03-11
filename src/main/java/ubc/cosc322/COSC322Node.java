package ubc.cosc322;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Queue;

public class COSC322Node {

    public int[] state;   // 121-element board (11x11 with row 0 and col 0 as padding)
    public int g;
    public COSC322Node parent;

    // Cached evaluation scores to avoid redundant recomputation during sorting.
    private int cachedEvalWhite = Integer.MIN_VALUE;
    private int cachedEvalBlack = Integer.MIN_VALUE;

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

        // Up (decreasing index by 11)
        for (int i = newPos - 11; i > 0; i -= 11) {
            if (i % 11 == 0) break;          // hit left-padding column → stop
            if (i / 11 == 0) break;          // hit top-padding row    → stop
            if (i != oldPos && state[i] != 0) break;
            squares.add(i);
        }
        for (int i = newPos + 11; i < 121; i += 11) {
            if (i % 11 == 0) break;
            if (i / 11 == 0) break;
            if (i != oldPos && state[i] != 0) break;
            squares.add(i);
        }
        for (int i = newPos - 1; i > 0; i--) {
            if (i % 11 == 0) break;          // wrapped to previous row → stop
            if (i / 11 == 0) break;
            if (i != oldPos && state[i] != 0) break;
            squares.add(i);
        }
        for (int i = newPos + 1; i < 121; i++) {
            if (i % 11 == 0) break;          // wrapped to next row → stop
            if (i / 11 == 0) break;
            if (i != oldPos && state[i] != 0) break;
            squares.add(i);
        }
        for (int i = newPos - 10; i > 0; i -= 10) {
            if (i % 11 == 0) break;
            if (i / 11 == 0) break;
            if (i != oldPos && state[i] != 0) break;
            squares.add(i);
        }
        // Up-left (step -12)
        for (int i = newPos - 12; i > 0; i -= 12) {
            if (i % 11 == 0) break;
            if (i / 11 == 0) break;
            if (i != oldPos && state[i] != 0) break;
            squares.add(i);
        }
        // Down-left (step +10)
        for (int i = newPos + 10; i < 121; i += 10) {
            if (i % 11 == 0) break;
            if (i / 11 == 0) break;
            if (i != oldPos && state[i] != 0) break;
            squares.add(i);
        }
        // Down-right (step +12)
        for (int i = newPos + 12; i < 121; i += 12) {
            if (i % 11 == 0) break;
            if (i / 11 == 0) break;
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

    /**
     * Returns territory score from the perspective of the given player.
     * Result is cached to avoid recomputing during move ordering sorts.
     */
    public int evaluate(boolean whitePieces) {
        if (whitePieces) {
            if (cachedEvalWhite == Integer.MIN_VALUE)
                cachedEvalWhite = territoryScore(true);
            return cachedEvalWhite;
        } else {
            if (cachedEvalBlack == Integer.MIN_VALUE)
                cachedEvalBlack = territoryScore(false);
            return cachedEvalBlack;
        }
    }

    // BFS flood fill from all queens simultaneously.
    // A square belongs to the side that reaches it first.
    // Score = our squares - opponent squares.
    private int territoryScore(boolean whitePieces) {
        final int INF = Integer.MAX_VALUE / 2;
        int[] wDist = new int[121];
        int[] bDist = new int[121];
        java.util.Arrays.fill(wDist, INF);
        java.util.Arrays.fill(bDist, INF);

        Queue<Integer> wQueue = new ArrayDeque<>();
        Queue<Integer> bQueue = new ArrayDeque<>();

        for (int i = 1; i < 121; i++) {
            if (i % 11 == 0 || i / 11 == 0) continue;
            if (state[i] == 1) { wDist[i] = 0; wQueue.add(i); }
            else if (state[i] == 2) { bDist[i] = 0; bQueue.add(i); }
        }

        bfsFloodQueenRay(wDist, wQueue);
        bfsFloodQueenRay(bDist, bQueue);

        int wTerritory = 0, bTerritory = 0;
        for (int i = 1; i < 121; i++) {
            if (i % 11 == 0 || i / 11 == 0) continue;
            if (state[i] != 0) continue;  // skip occupied/burned squares
            if (wDist[i] < bDist[i]) wTerritory++;
            else if (bDist[i] < wDist[i]) bTerritory++;
        }

        return whitePieces ? wTerritory - bTerritory : bTerritory - wTerritory;
    }

    /**
     * Queen-ray BFS: from each frontier square, expand along all 8 queen-move
     * rays (not just immediate neighbours). Each ray counts as distance+1 per
     * square reached along it, so nearer queens still win contested squares.
     * This correctly models Amazons territory.
     */
    private void bfsFloodQueenRay(int[] dist, Queue<Integer> queue) {
        final int[] STEPS = {-11, 11, -1, 1, -10, -12, 10, 12};

        while (!queue.isEmpty()) {
            int pos = queue.poll();
            for (int step : STEPS) {
                // Walk the full ray from pos
                for (int next = pos + step; ; next += step) {
                    if (next <= 0 || next >= 121) break;
                    if (next % 11 == 0 || next / 11 == 0) break;
                    if (state[next] != 0) break;  // blocked by queen or arrow
                    int newDist = dist[pos] + 1;
                    if (newDist < dist[next]) {
                        dist[next] = newDist;
                        queue.add(next);
                    }
                    // continue along the ray even if this cell already had a better dist,
                    // because further cells might still improve via this path
                }
            }
        }
    }
}