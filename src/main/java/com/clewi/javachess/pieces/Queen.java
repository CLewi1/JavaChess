package com.clewi.javachess.pieces;

import java.util.List;
import java.awt.Point;

import com.clewi.javachess.model.Board;

public class Queen extends Piece {

    public Queen(int x, int y, boolean is_white, String file_path, Board board, boolean is_captured)
    {
        super(x,y,is_white,file_path, board, is_captured);
    }
    
    @Override
    public boolean canMove(int destination_x, int destination_y)
    {
        // Check if board reference exists
        if (board == null) {
            return false;
        }

        // Get the current position of the queen
        int x = this.getX();
        int y = this.getY();
        
        // Check if the destination is within the bounds of the board
        if (!isWithinBounds(destination_x, destination_y)) {
            return false;
        }

        // Check if the queen is moving diagonally, horizontally, or vertically
        boolean movingDiagonally = Math.abs(destination_x - x) == Math.abs(destination_y - y);
        boolean movingHorizontally = y == destination_y;
        boolean movingVertically = x == destination_x;

        if (!movingHorizontally && !movingVertically && !movingDiagonally) {
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
