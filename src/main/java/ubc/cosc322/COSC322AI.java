package ubc.cosc322;
import ygraph.ai.smartfox.games.amazons.AmazonsBoard;
import java.util.ArrayList;

public class COSC322AI {

    //These can be used for persistence if needed, I'm not convinced that we need them but they are provided regardless.
    public ArrayList<Integer> currentpos;

    public int currentCost;

    public boolean whitePieces;

    /*Takes a boolean determining whether we play white pieces or black, and the current board state (This may need to change to an action and 
    store the game state internally, but I'm not sure, the documentation is hard to read.) Should return a move to make in this position.
    */
    public void FindMove(boolean white, ArrayList<Integer> gameState){
        COSC322Node root = new COSC322Node(gameState, 0); //Initialise current position as root.

        //TODO: Perform A* search
    }
}
