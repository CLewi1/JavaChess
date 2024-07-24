package com.clewi.javachess.pieces;

import java.util.List;
import java.awt.Point;

import com.clewi.javachess.Board;

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
        
        // check if pawn has moved
        if (this.getY() != 1 && this.getY() != 6){
            has_moved = true;
        }

        // Check if the pawn is moving forward or diagonally forward
        boolean is_forward = (destination_x == x && destination_y == y + direction);
        boolean is_diagonal = (destination_x == x + 1 || destination_x == x - 1) && destination_y == y + direction;
        boolean first_move = (destination_x == x && destination_y == y + 2 * direction && !has_moved);
        
        
        // Check if the pawn is attacking
        if(is_diagonal)
        {
            // Check if there is a piece of the opposite color at the destination
            if(Board.getPiece(destination_x, destination_y) == null || Board.getPiece(destination_x, destination_y).isWhite() == this.isWhite())
            {
                return false;
            }
        }
        // Check if there is a piece in the way
        else if(Board.getPiece(destination_x, destination_y) != null)
        {
            return false;
        }

        else {
        // check if king is in check
        for (int i = 0; i < 8; i++)
        {   for (int j = 0; j < 8; j++) {
                if (Board.getPiece(i, j) != null) {
                    if (Board.getPiece(i, j).isWhite() == this.isWhite()) {
                        if (Board.getPiece(i, j) instanceof King) {
                            if (King.isKingInCheck(this.isWhite(), i, j)) {

                                // if cannot take the piece that is checking the king, return false
                                Piece attacking = null;
                                for (int l = 0; l < 8; l++) {
                                    for (int m = 0; m < 8; m++) {
                                        if (Board.getPiece(l, m) != null) {
                                            if (Board.getPiece(l, m).isWhite() != this.isWhite()) {
                                                if (Board.getPiece(l, m).canMove(i, j)) {
                                                    attacking = Board.getPiece(l, m);
                                                }
                                            } 
                                        }
                                    }
                                }

                                // create grid of points
                                Point[][] grid = new Point[8][8]; 
                                for (int p = 0; p < 8; p++) {
                                    for (int q = 0; q < 8; q++) {
                                        grid[p][q] = new Point(p, q);
                                    }
                                }

                                // find line between king and attacking piece
                                List<Point> line = Bresenham.findLine(grid, i, j, attacking.getX(), attacking.getY());

                                // check if queen can move to any of the squares in the line
                                if (line.contains(new Point(destination_x, destination_y)) || (line.contains(new Point(destination_x, destination_y))) && first_move) {
                                    return true;
                                }

                                // check if can take the attacking piece
                                if (destination_x == attacking.getX() && destination_y == attacking.getY()) {
                                    return true; 


                                }

                                return false; // can't move if king is in check and none of the other conditions are met
                            }
                        }
                    }
                }
            }
        }
    }

        if (first_move && Board.getPiece(destination_x, destination_y) == null && Board.getPiece(destination_x, destination_y - direction) == null)
        {
            // find king position
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
