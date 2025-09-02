package com.clewi.javachess.pieces;

import com.clewi.javachess.model.Board;
import com.clewi.javachess.game.MoveValidator;

public class King extends Piece {

    private boolean has_moved = false;
    private static final int BOARD_MIN = 0;
    private static final int BOARD_MAX = 7;

    public King(int x, int y, boolean is_white, String file_path, Board board, boolean is_captured)
    {
        super(x,y,is_white,file_path, board, is_captured);
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
        if (board == null) {
            return false;
        }
        
        // Get the current position of the king
        int x = this.getX();
        int y = this.getY();
        
        // Check if the destination is within the bounds of the board
        if (!isWithinBounds(destination_x, destination_y)) {
            return false;
        }

        // Check if the king is moving one square in any direction
        if (!isOneSquareMove(x, y, destination_x, destination_y)) {
            return false;
        }
        
        // Check if destination is occupied by a piece of the same color
        if (isOccupiedBySameColor(destination_x, destination_y)) {
            return false;
        }

        // We don't check for check here since that's done in MoveValidator
        
        // If all the above checks are passed, the king can move to the destination
        return true;
    }

    private boolean isOneSquareMove(int x, int y, int destination_x, int destination_y)
    {
        return Math.abs(destination_x - x) <= 1 && Math.abs(destination_y - y) <= 1;
    }
}
