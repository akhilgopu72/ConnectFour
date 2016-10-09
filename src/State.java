import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/** An instance represents a potential state of a game of Connect Four. */
public class State {
 private final Turn ai;     // The AI's turn.
    private final Board board; // The current Board layout.
    private final Turn player; // It is player's turn to make a move.

    /** Map all possible moves from this state's board to the states
     *  that would result from making those moves by this state's player.
     *  This is null iff this state has not been expanded yet.
     *  Keys are sorted, so that moves in earlier columns are placed earlier in the map. */
    private SortedMap<Move,State> children= null; // will not point to a map containing null keys or values

    private int value; // How desirable this State is for the AI.

    /** Constructor: a game State consisting of a board and a player who will move next.
     *  ai indicates which turn is the AI's turn. */
    public State(Turn ai, Board board, Turn player) {
        this.ai= ai;
        this.board= board;
        this.player= player;
    }
    
    /** Indicate whether this state has been expanded or not. */
    public boolean isExpanded() {
     return children != null;
    }
    
    /** Return the child resulting from move.
     *  Precondition: move is a possible move of this board's state
     *  Precondition: this state is expanded */
    public State getChild(Move move) {
     return children.get(move);
    }
    
    
    /** Return the preferred move for this state's player on this state's board.
     *  If there are multiple such moves, return the one with the left-most column.
     *  Precondition: this state has been expanded.
     *  Precondition: minimax has been calculated for this state and its descendants
     *  Precondition: this state's board has at least one possible move 
     * @return */
    public Move getPreferredMove() {

    	if (this.isExpanded() && 
    			board.getPossibleMoves().length > 0 
    				)
    	{
        	this.computeMinimax();
        	for (Move x : board.getPossibleMoves())
        	{
        		if (this.getChild(x).value == value )
        		{
        			System.out.println();
        			return x;
        		}
        	}
        		
        	
        }
    	return board.getPossibleMoves()[0];
    		
    }
    
    /** If depth = zero, this does nothing.
     *  For depth > zero:
     *     1. If this state does not yet have children, first create child
     *        states corresponding to each move this state's player could make on
     *        this state's board.
     *     2. Expand this state's children up to depth-1.

     *  Precondition: depth >= 0. */
    public void expandUpTo(int depth) {

    	if (depth > 0)
    	{
    		int n = 0;
    		children = new TreeMap<Move, State>();
    		Move[] moves = board.getPossibleMoves();
    		for (Move x : moves)
    		{
    			children.put(x, new State(ai, new Board (board, player, x), player.getNext()));
    		}
    		Set set = children.entrySet();
    	    Iterator iterator = set.iterator();
    		while (iterator.hasNext())
    		{
    			Map.Entry<Move, State> child = (Map.Entry<Move, State>) iterator.next();
    			child.getValue().expandUpTo(depth -1);
    		}
    	}
    		
    }
    
    
    /** Compute and store the value of this state in field value.
     *    1. If this state's board has a connect four, its value is
     *       the maximum or minimum int value depending on who wins.
     *    2. If this state's board is full, its value is 0.
     *    3. If this state is not expanded, its value is the value of the board.
     *    4. Otherwise, this state's value is its player's preferred value of its
     *       child states' values. */
    public void computeMinimax() {

    	if (this.isExpanded() == false)
    		value = this.computeBoardValue();
    	if(board.hasConnectFour() == ai)
    		value = Integer.MAX_VALUE;
    	else if (board.hasConnectFour() != null)
    		value = Integer.MIN_VALUE;
    	if(board.isFull())
    		value = 0;
    	if (this.isExpanded())
    	{
    		
    	Set set = children.entrySet();
    	Iterator iterator = set.iterator();
    	while (iterator.hasNext())
    		{
    			Map.Entry<Move, State> child = (Map.Entry<Move, State>) iterator.next();
    			child.getValue().computeMinimax();
    			Set set2 = children.entrySet();
    	    	Iterator iterator2 = set.iterator();
    	    	value = ((Map.Entry<Move, State>) iterator2.next()).getValue().value;
    	    	while (iterator2.hasNext())
    	    		{
    	    			Map.Entry<Move, State> child2 = (Map.Entry<Move, State>) iterator2.next();
    	    			value = preferredValue(value, child2.getValue().value);
    	    		}
    		}
    	}
    	
    		
    	}
    	
    
    
    /** Compute the preferred value for this state's player. */
    private int preferredValue(int v1, int v2) {
     return player == ai ? Math.max(v1, v2) : Math.min(v1, v2);
    }

    /** Evaluate the desirability of this state's board for the AI. */
    private int computeBoardValue() {
        // Store in sum the value of this state's board. 
        int sum= 0;
        for (Iterable<? extends Board.Location> fourinarow : Board.getFourInARows())
            for (Board.Location loc : fourinarow)
                sum+= !loc.isOccupied(board) ? 0 : loc.getPlayer(board) == ai ? 1 : -1;
        return sum;
    }

    /** Return a String representation of this State. */
    public @Override String toString() {
        return toString(0,"");
    }

    /** Return a string that contains a representation of this board indented
     *  with string indent (expected to be a string of blank characters) followed
     *  by a similar representation of all its children,
     *  indented an additional indent characters. depth is the depth of this state. */
    private String toString(int depth, String indent) {
        String str= indent + (player == ai ? "AI" : "Opponent") +
                " will play next on the board below as " + player.getInitial() + "\n";
        str= str + indent + "Value: " + value + "\n";
        str= str + board.toString(indent) + "\n";
        if (children != null && children.size() > 0) {
            str= str + indent + "Children at depth "+ (depth+1) + ":\n" +
                    indent + "----------------\n";

            for (State s : children.values())
                str= str + s.toString(depth+1,indent + "   ");
        }
        return str;
    }
}
