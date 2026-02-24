package ubc.cosc322;
import java.util.ArrayList;

public class COSC322AI {

    //These can be used for persistence if needed, I'm not convinced that we need them but they are provided regardless.
    public ArrayList<Integer> currentpos;

    public int currentCost;

    //public boolean whitePieces;

    /*Takes a boolean determining whether we play white pieces or black, and the current board state (This may need to change to an action and 
    store the game state internally, but I'm not sure, the documentation is hard to read.) Should return a move to make in this position.
    */
    public COSC322Node FindMove(boolean white, ArrayList<Integer> gameState){
        COSC322Node root = new COSC322Node(gameState, 0); //Initialise current position as root.
        //TODO: Perform A* search

        ArrayList<COSC322Node> open = new ArrayList<COSC322Node>();
        open.add(root);
        ArrayList<COSC322Node> closed = new ArrayList<COSC322Node>();

        int maxIterations = 10000;
        int iterations = 0;

        while (!open.isEmpty() && iterations < maxIterations) {
            iterations++;
            COSC322Node q = open.get(0);
            int bestIndex = 0;
            for (int i = 1; i < open.size(); i++) {
                if (q.getFValueAStar(white) > open.get(i).getFValueAStar(white)) {
                    q = open.get(i);
                    bestIndex = i;
                }
            }
            if(isGoalState(q, white)) {
                return findCurrentMove(q, root);
                //figure out move returning later
            }

            open.remove(bestIndex);
            closed.add(q);

            ArrayList<COSC322Node> children = q.expandNode(white);
            for (COSC322Node child : children) {
                if (listContains(child, closed)) {
                    continue;
                }

                int openStateIndex = indexInList(child, open);
                if (!listContains(child, open)) {
                    child.parent = q;
                    open.add(child);
                } else if (child.getFValueAStar(white) < open.get(openStateIndex).getFValueAStar(white)) {
                    child.parent = q;
                    open.set(openStateIndex, child);
                }
            }
        }

        return root.expandNode(white).get(0);
    }

    private boolean isGoalState(COSC322Node node, boolean white) {
        // Goal: opponent has no legal moves
        int opponentId = white ? 2 : 1;

        for (int i = 0; i < 64; i++) {
            if (node.state.get(i) == opponentId) {
                ArrayList<Integer> moves = node.getArrowPlacementsFromMove(-100, i, !white);
                if (!moves.isEmpty()) {
                    return false; // Opponent has at least one move
                }
            }
        }
        return true;
    }

    private boolean listContains(COSC322Node node, ArrayList<COSC322Node> list){
        for (COSC322Node n : list) {
            if (n.state.equals(node.state)) {
                return true;
            }
        }
        return false;
    }

    private int indexInList(COSC322Node node, ArrayList<COSC322Node> list){
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).state.equals(node.state)) {
                return i;
            }
        }
        return -1;
    }

    //Keeps looping until it finds an ancestor node of the goal state who's parent is the current board state and returns that ancestor
    private COSC322Node findCurrentMove(COSC322Node gs, COSC322Node root) {
        if (!gs.parent.equals(root)) {
            findCurrentMove(gs.parent, root);
        } else {
            return gs;
        }
        return null;
    }
}
