package chessgui.pieces;

import chessgui.Board;

public class Pawn extends Piece {

    private boolean has_moved;
    
    public Pawn(int x, int y, boolean is_white, String file_path, Board board, boolean is_captured)
    {
        super(x,y,is_white,file_path, board, is_captured);
        has_moved = false;
    }
    
    public void setHasMoved(boolean has_moved)
    {
        this.has_moved = has_moved;
    }
    
    public boolean getHasMoved()
    {
        return has_moved;
    }
    
    @Override
    public boolean canMove(int destination_x, int destination_y)
    {
        // Remember: A pawn may only move towards the oponent's side of the board.
        // If the pawn has not moved yet in the game, for its first move it can 
        // move two spaces forward. Otherwise, it may only move one space. 
        // When not attacking it may only move straight ahead.
        // When attacking it may only move space diagonally forward

        // Get the current position of the pawn
        int x = this.getX();
        int y = this.getY();
        
        // Determine the direction the pawn is moving (up or down the board)
        int direction = this.isWhite() ? -1 : 1;
        
        // Check if the destination is within the bounds of the board
        if(destination_x < 0 || destination_x > 7 || destination_y < 0 || destination_y > 7)
        {
            return false;
        }
        
        // Check if the pawn is moving forward or diagonally forward
        boolean is_forward = (destination_x == x && destination_y == y + direction);
        boolean is_diagonal = (destination_x == x + 1 || destination_x == x - 1) && destination_y == y + direction;
        boolean first_move = (destination_x == x && destination_y == y + 2 * direction);
        
        if (first_move && !has_moved && Board.getPiece(destination_x, destination_y) == null){
            return true;
        }
        else if (!is_forward && !is_diagonal)
        {
            return false;
        }
        // The move is valid
        return true;
    }
}
