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

    public ArrayList<Cosc322Node> expandNode(){
        //TODO: Creates a list of nodes containing all possible moves from this node.
    }

    public int getHvalue(){
        /*TODO: find the H value of this node (should probably call some other h-value method so that 
        we can experiment with different heuristics if desired.) */
        return 0;
    }
    
}
