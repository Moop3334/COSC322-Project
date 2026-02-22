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
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                int position = state[i][j];
                if (position == 0) continue; //skip empty spaces
                else if (position == (whitePieces? 1:2))//This is a queen of our colour, expand it.
                {

                }else{ //this is a blocking tile (either an arrow or an opponent queen)
                    continue; //can probably just skip this too, leaving it separate though just in case.
                }
            }
        }
    }

    public int getHvalue(){
        /*TODO: find the H value of this node (should probably call some other h-value method so that 
        we can experiment with different heuristics if desired.) */
        return 0;
    }
    
}
