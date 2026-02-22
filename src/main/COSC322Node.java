public class COSC322Node {
    
    public ArrayList<Integer> state;  //The gamestate stored by this node

    public int g; //The cost to reach this game state

    public COSC322Node parent; //parent node

    public COSC322Node(ArrayList<Integer> state, int costToReach){
        this.state = state;
        this.g = costToReach;
    }

    public int getFValueAStar(){
        //Return the A* value f = (g + h) of this node
        return g + getHValue();
    }

    public ArrayList<Cosc322Node> expandNode(boolean whitePieces){
        //TODO: Creates a list of nodes containing all possible moves from this node.
        ArrayList<Cosc322Node> nodes = new ArrayList<Cosc322Node>();

    }

    private ArrayList<Integer> getArrowPlacementsFromMove(int oldQueenPos, int newQueenPos, boolean whitePieces){
        //helper method that generates all of the legal arrow positions from a given queen move
        ArrayList<Integer> legalArrowPositions = new ArrayList<Integer>();

        for(int i = newQueenPos; i > -1; i -= 8){ //look up
            int position = state.get(i);
            if(i != oldQueenPos && position != 0) break; //This position is occupied, and we can go no further (oldQueenPos is not empty in state, but will be after queen move, so we need to ignore it)
            if(!legalArrowPositions.contains(i)) legalArrowPositions.add(i);
        }
        for(int i = newQueenPos; i < 64; i += 8){ //look down
            int position = state.get(i);
            if(i != oldQueenPos && position != 0) break;
            if(!legalArrowPositions.contains(i)) legalArrowPositions.add(i);
        }
        for(int i = newQueenPos - 1; i % 8 != 7; i--){ //look left
            int position = state.get(i);
            if(i != oldQueenPos && position != 0) break; 
            if(!legalArrowPositions.contains(i)) legalArrowPositions.add(i);
        }
        for(int i = newQueenPos + 1; i % 8 != 0; i++){ //look right
            int position = state.get(i);
            if(i != oldQueenPos && position != 0) break;
            if(!legalArrowPositions.contains(i)) legalArrowPositions.add(i);
        }
        for(int i = newQueenPos - 7; i % 8 != 0 && i > -1; i -= 7){//look up-right
            int position = state.get(i);
            if(i != oldQueenPos && position != 0) break;
            if(!legalArrowPositions.contains(i)) legalArrowPositions.add(i);
        }
        for(int i = newQueenPos - 9; i % 8 != 7 && i > -1; i -= 9){//look up-left
            int position = state.get(i);
            if(i != oldQueenPos && position != 0) break;
            if(!legalArrowPositions.contains(i)) legalArrowPositions.add(i);
        }
        for(int i = newQueenPos + 7; i % 8 != 7 && i < 64; i += 7){//look down-left
            int position = state.get(i);
            if(i != oldQueenPos && position != 0) break;
            if(!legalArrowPositions.contains(i)) legalArrowPositions.add(i);
        }
        for(int i = newQueenPos + 9; i % 8 != 0 && i > 64; i += 9){//look down-right
            int position = state.get(i);
            if(i != oldQueenPos && position != 0) break;
            if(!legalArrowPositions.contains(i)) legalArrowPositions.add(i);
        }
        return legalArrowPositions;
    }

    private Cosc322Node generateChildFromAction(int oldQueenPos, int newQueenPos, int arrowPos, boolean whitePieces){
        //helper method that generates a node for a given action based on this node's state
        ArrayList<Integer> newState = new ArrayList<Integer>(state);
        for(int i = 0; i < 64; i++){
            if(i == oldQueenPos) newState.set(i, 0);
            else if(i == newQueenPos) newState.set(i, (whitePieces? 1:2));
            else if(i == arrowPos) newState.set(i, 3); //arrow can be anything other than 0, 1, or 2, it doesn't really matter.
            else newState.set(i, state.get(i));
        }
        return new COSC322Node(newState, g + 1);
    }

    public int getHvalue(){
        /*TODO: find the H value of this node (should probably call some other h-value method so that 
        we can experiment with different heuristics if desired.) */
        return 0;
    }
    
}
