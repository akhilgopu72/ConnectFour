/** A Player that chooses its moves randomly. */
public class Random extends SynchronousPlayer {
    /* The random-number generator to use. It is not null.
     * Class Random has a method random.nextInt(n) that returns the
     * the next pseudorandom, uniformly distributed int value in the
     * range 0..n-1. */
	private final java.util.Random random; 
	
	/** Constructor: a player that randomly chooses its moves.
	 *  The hash code of seed is the seed to use for the random-number generator. */
	public Random(Turn turn, String seed) {
		super("Random-" + seed);
		random= new java.util.Random(seed.hashCode());
	}

	/** Return a random move for the board as determined by the array of possible moves
	 *  and the next integer generated by the random-number generator.
	 *  Precondition: board is not null and has at least one possible move. */
    public @Override Move getMove(Board board) {
    	if (board.getPossibleMoves().length > 0 && board != null)
    	{
    		int x = random.nextInt(board.getPossibleMoves().length);
    		int y = board.getPossibleMoves()[x].getColumn();
    		return new Move(y);
    	}
		return new Move(0);
    	
	}
}