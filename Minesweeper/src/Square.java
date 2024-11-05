public class Square {
    SquareHiddenValue squareHiddenValue;
    SquareState squareState;
    
    private Square[] neighbors;

    public int minesAround = 0;

    /**
     * Constructs this square and sets this squareHiddenValue as EMPTY and this squareState as DEFAULT.
     */
    public Square () {
        this.squareHiddenValue = SquareHiddenValue.EMPTY;
        this.squareState = SquareState.DEFAULT;
    }
    /**
     * Sets the neighboring squares of the this square.
     * @param neighbors List of neighbors around this square.
     */
    public void neighborSquares(Square[] neighbors) {
        this.neighbors = neighbors;
    }

    /**
     * Gives the neighboring squares of this square.
     * @return List of neighbors around this square.
     */
    public Square[] getNeighbors() {
        return this.neighbors;
    }

    /**
     * Reveals the squares around this square, and if any square of those has no mines around it this method is applied to it too, recursively.
     */
    public void recursiveReveal() {
        for (Square i : this.neighbors) {

            //If square state is DEFAULT.
            if (i.squareState == SquareState.DEFAULT) {
                i.squareState = SquareState.REVEALED;
                Board.revealedSquares++;
                
                if (i.minesAround == 0) {
                    i.recursiveReveal();
                }

            //If square state is FLAGGED.
            } else if (i.squareState == SquareState.FLAGGED) {
                i.squareState = SquareState.REVEALED;
                Board.flaggedSquares--;
                Board.revealedSquares++;

                if (i.minesAround == 0) {
                    i.recursiveReveal();
                }
            }

            
        }
    }
}