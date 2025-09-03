package com.clewi.javachess.pieces;

import java.awt.Point;
import com.clewi.javachess.model.Board;

public class Rook extends Piece {

    private boolean has_moved = false;

    public Rook(int x, int y, boolean is_white, String file_path, Board board, boolean is_captured)
    {
        super(x, y, is_white, file_path, board, is_captured);
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
        // Get the current position of the rook
        int x = this.getX();
        int y = this.getY();
        
        // Check if the destination is within the bounds of the board
        if (!isWithinBounds(destination_x, destination_y)) {
            return false;
        }

        // Check if the rook is moving horizontally or vertically
        boolean is_horizontal = y == destination_y && x != destination_x;
        boolean is_vertical = x == destination_x && y != destination_y;

        if (!is_horizontal && !is_vertical) {
            return false;
        }

        // Check if the path is clear
        if (!isPathClear(x, y, destination_x, destination_y)) {
            return false;
        }
        
        // Check if destination is occupied by a piece of the same color
        if (isOccupiedBySameColor(destination_x, destination_y)) {
            return false;
        }

        return true;
    }
}
