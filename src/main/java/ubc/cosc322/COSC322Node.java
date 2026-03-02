package ubc.cosc322;
import java.util.ArrayList;
public class COSC322Node {
    
    public ArrayList<Integer> state;  //The gamestate stored by this node

    public int g; //The cost to reach this game state

    public COSC322Node parent; //parent node

    public COSC322Node(ArrayList<Integer> state, int costToReach){
        this.state = state;
        this.g = costToReach;
    }

    public int getFValueAStar(boolean whitePieces){
        //Return the A* value f = (g + h) of this node
        return g + getHValue(whitePieces);
    }

    public ArrayList<COSC322Node> expandNode(boolean whitePieces){
        //Creates a list of nodes containing all possible moves from this node.
        ArrayList<COSC322Node> nodes = new ArrayList<COSC322Node>();

        for (int i = 0; i < 121; i++){
            if(state.get(i) == (whitePieces? 1:2)){ //These are our queens, find all possible moves for each.
                ArrayList<Integer> queenMoves = getArrowPlacementsFromMove(-100, i, whitePieces); //This is a little hacky, but passing in an impossible oldQueenPos allows the arrow function to be reused for getting all possible queen moves
                for(Integer j: queenMoves){ //For each possible move, get all possible arrow placements
                    ArrayList<Integer> arrowPlacements = getArrowPlacementsFromMove(i, j, whitePieces);
                    for (Integer k: arrowPlacements){
                        nodes.add(generateChildFromAction(i, j, k, whitePieces)); //Finally, create a node for each complete move, and append it to our list.
                    }
                }
            }
        }
        return nodes;

    }

    public ArrayList<Integer> getArrowPlacementsFromMove(int oldQueenPos, int newQueenPos, boolean whitePieces){
        //helper method that generates all of the legal arrow positions from a given queen move
        ArrayList<Integer> legalArrowPositions = new ArrayList<Integer>();

        for(int i = newQueenPos - 11; i >= 0; i -= 11){ //look up
            if(i % 11 == 0 || i/11 == 0) continue; //skip padding
            int position = state.get(i);
            if(i != oldQueenPos && position != 0) break; //This position is occupied, and we can go no further (oldQueenPos is not empty in state, but will be after queen move, so we need to ignore it)
            if(!legalArrowPositions.contains(i)) legalArrowPositions.add(i);
        }
        for(int i = newQueenPos + 11; i < 121; i += 11){ //look down
            if(i % 11 == 0 || i/11 == 0) continue; //skip padding
            int position = state.get(i);
            if(i != oldQueenPos && position != 0) break;
            if(!legalArrowPositions.contains(i)) legalArrowPositions.add(i);
        }
        for(int i = newQueenPos - 1; i % 11 != 0 && i >= 0; i--){ //look left
            if(i % 11 == 0 || i/11 == 0) continue; //skip padding
            int position = state.get(i);
            if(i != oldQueenPos && position != 0) break; 
            if(!legalArrowPositions.contains(i)) legalArrowPositions.add(i);
        }
        for(int i = newQueenPos + 1; i % 11 != 0 && i < 121; i++){ //look right
            if(i % 11 == 0 || i/11 == 0) continue; //skip padding
            int position = state.get(i);
            if(i != oldQueenPos && position != 0) break;
            if(!legalArrowPositions.contains(i)) legalArrowPositions.add(i);
        }
        for(int i = newQueenPos - 10; i % 11 != 0 && i >= 0; i -= 10){//look up-right
            if(i % 11 == 0 || i/11 == 0) continue; //skip padding
            int position = state.get(i);
            if(i != oldQueenPos && position != 0) break;
            if(!legalArrowPositions.contains(i)) legalArrowPositions.add(i);
        }
        for(int i = newQueenPos - 12; i % 11 != 10 && i >= 0; i -= 12){//look up-left
            if(i % 11 == 0 || i/11 == 0) continue; //skip padding
            int position = state.get(i);
            if(i != oldQueenPos && position != 0) break;
            if(!legalArrowPositions.contains(i)) legalArrowPositions.add(i);
        }
        for(int i = newQueenPos + 10; i % 11 != 10 && i < 121; i += 10){//look down-left
            if(i % 11 == 0 || i/11 == 0) continue; //skip padding
            int position = state.get(i);
            if(i != oldQueenPos && position != 0) break;
            if(!legalArrowPositions.contains(i)) legalArrowPositions.add(i);
        }
        for(int i = newQueenPos + 12; i % 11 != 0 && i < 121; i += 12){//look down-right
            if(i % 11 == 0 || i/11 == 0) continue; //skip padding
            int position = state.get(i);
            if(i != oldQueenPos && position != 0) break;
            if(!legalArrowPositions.contains(i)) legalArrowPositions.add(i);
        }
        return legalArrowPositions;
    }

    private COSC322Node generateChildFromAction(int oldQueenPos, int newQueenPos, int arrowPos, boolean whitePieces){
        //helper method that generates a node for a given action based on this node's state
        ArrayList<Integer> newState = new ArrayList<Integer>(state);
        newState.set(oldQueenPos, 0);
        newState.set(newQueenPos, (whitePieces? 1:2));
        newState.set(arrowPos, 3); //arrow can be anything other than 0, 1, or 2, it doesn't really matter.
        return new COSC322Node(newState, g + 1);
    }

    public int getHValue(boolean whitePieces){
        // Calling our mobility heuristic.
        return calculateMobilityHeuristic(whitePieces);
    }

    private int calculateMobilityHeuristic(boolean whitePieces) {
        int myMobility = 0;
        int opponentMobility = 0;
        
        int myPlayerId = whitePieces ? 1 : 2;
        int opponentPlayerId = whitePieces ? 2 : 1;

        for (int i = 0; i < 121; i++) {
            int piece = state.get(i);
            
            if (piece == myPlayerId) {
                myMobility += getArrowPlacementsFromMove(-100, i, whitePieces).size();
            } else if (piece == opponentPlayerId) {
                opponentMobility += getArrowPlacementsFromMove(-100, i, !whitePieces).size();
            }
        }
        
        return opponentMobility - myMobility;
    }
    
}
