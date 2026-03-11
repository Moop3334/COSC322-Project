package ubc.cosc322;

import java.util.ArrayList;
import java.util.Comparator;

public class COSC322AI {

    private static final long TIME_LIMIT_MS = 8000; // 8 seconds per move
    private boolean timeUp;

    // Accepts ArrayList<Integer> from the game client, converts internally to int[].
    public COSC322Node FindMove(boolean white, ArrayList<Integer> gameState) {
        int[] board = new int[121];
        for (int i = 0; i < 121; i++) board[i] = gameState.get(i);

        COSC322Node root = new COSC322Node(board, 0);
        ArrayList<COSC322Node> children = root.expandNode(white);
        if (children.isEmpty()) return null;

        // Initial move ordering at root: evaluate once and sort best-first.
        // evaluate() caches its result on the node, so this is not wasted work.
        children.sort(Comparator.comparingInt(c -> -c.evaluate(white)));

        COSC322Node bestMove = children.get(0);
        bestMove.parent = root;
        long deadline = System.currentTimeMillis() + TIME_LIMIT_MS;

        for (int depth = 1; ; depth++) {
            timeUp = false;
            COSC322Node result = searchAtDepth(root, children, depth, white, deadline);
            if (!timeUp && result != null) {
                bestMove = result;
            }
            if (timeUp || System.currentTimeMillis() >= deadline) break;
        }

        return bestMove;
    }

    private COSC322Node searchAtDepth(COSC322Node root, ArrayList<COSC322Node> children,
                                      int depth, boolean white, long deadline) {
        COSC322Node bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        for (COSC322Node child : children) {
            if (System.currentTimeMillis() >= deadline) {
                timeUp = true;
                break;
            }
            child.parent = root;
            int score = minimax(child, depth - 1, alpha, beta, false, white, deadline);
            if (score > bestScore) {
                bestScore = score;
                bestMove = child;
            }
            if (bestScore > alpha) alpha = bestScore;
        }

        return bestMove;
    }

    // maximizing=true → our turn; maximizing=false → opponent's turn
    private int minimax(COSC322Node node, int depth, int alpha, int beta,
                        boolean maximizing, boolean weAreWhite, long deadline) {
        if (System.currentTimeMillis() >= deadline) {
            timeUp = true;
            return 0;
        }

        boolean currentPlayerIsWhite = maximizing ? weAreWhite : !weAreWhite;
        ArrayList<COSC322Node> children = node.expandNode(currentPlayerIsWhite);

        if (depth == 0 || children.isEmpty()) {
            return node.evaluate(weAreWhite);
        }

        if (maximizing) {
            // Sort best-for-us moves first to raise alpha quickly.
            // evaluate() is cached, so this sort is cheap after the first call.
            children.sort(Comparator.comparingInt(c -> -c.evaluate(weAreWhite)));
            int maxEval = Integer.MIN_VALUE;
            for (COSC322Node child : children) {
                if (timeUp) break;
                int eval = minimax(child, depth - 1, alpha, beta, false, weAreWhite, deadline);
                if (eval > maxEval) maxEval = eval;
                if (maxEval > alpha) alpha = maxEval;
                if (beta <= alpha) break;
            }
            return maxEval;
        } else {
            // Sort worst-for-us (best-for-opponent) moves first to lower beta quickly.
            children.sort(Comparator.comparingInt(c -> c.evaluate(weAreWhite)));
            int minEval = Integer.MAX_VALUE;
            for (COSC322Node child : children) {
                if (timeUp) break;
                int eval = minimax(child, depth - 1, alpha, beta, true, weAreWhite, deadline);
                if (eval < minEval) minEval = eval;
                if (minEval < beta) beta = minEval;
                if (beta <= alpha) break;
            }
            return minEval;
        }
    }
}