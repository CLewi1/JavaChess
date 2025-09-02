package com.clewi.javachess.pieces;

import java.awt.Point;
import com.clewi.javachess.model.Board;

public class Knight extends Piece {

    public Knight(int x, int y, boolean is_white, String file_path, Board board, boolean is_captured)
    {
        super(x, y, is_white, file_path, board, is_captured);
    }

    @Override
    public boolean canMove(int destination_x, int destination_y)
    {
        // Get the current position of the knight
        int x = this.getX();
        int y = this.getY();
        
        // Check if the destination is within the bounds of the board
        if (!isWithinBounds(destination_x, destination_y)) {
            return false;
        }

        // Check if the knight is moving in an L shape
        boolean is_L_shape = (Math.abs(destination_x - x) == 2 && Math.abs(destination_y - y) == 1) || 
                            (Math.abs(destination_x - x) == 1 && Math.abs(destination_y - y) == 2);

        // only move in an L shape
        if (!is_L_shape) {
            return false;
        }

        // Check if destination is occupied by a piece of the same color
        if (isOccupiedBySameColor(destination_x, destination_y)) {
            return false;
        }
        
        // Knights can jump over pieces, so we don't need path checking

        return true;
    }
}