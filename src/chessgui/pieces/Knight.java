package chessgui.pieces;

import chessgui.Board;

public class Knight extends Piece {

    public Knight(int x, int y, boolean is_white, String file_path, Board board, boolean is_captured)
    {
        super(x,y,is_white,file_path, board, is_captured);
    }

    // Recursive helper function to check if a knight can move to the given destination
    private boolean canMoveHelper(int destination_x, int destination_y, int remaining_moves) {
        // Get the current position of the knight
        int x = this.getX();
        int y = this.getY();
        
        // Check if the destination is within the bounds of the board
        if(destination_x < 0 || destination_x > 7 || destination_y < 0 || destination_y > 7)
        {
            return false;
        }

        // Check if the knight is moving in an L shape
        boolean is_L_shape = (Math.abs(destination_x - x) == 2 && Math.abs(destination_y - y) == 1) || (Math.abs(destination_x - x) == 1 && Math.abs(destination_y - y) == 2);

        // only move in an L shape
        if(!is_L_shape)
        {
            return false;
        }

        // Check if the piece at the destination is an opponent
        if(Board.getPiece(destination_x, destination_y) != null)
        {
            if(Board.getPiece(destination_x, destination_y).isWhite() && this.isWhite())
            {
                return false;
            }
            if(!Board.getPiece(destination_x, destination_y).isWhite() && !this.isWhite())
            {
                return false;
            }
        }

        // Check if this is the last move we need to check
        if (remaining_moves == 1) {
            return true;
        }

        // Try moving to all possible positions recursively
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                if (Math.abs(i) + Math.abs(j) == 3) {
                    int next_x = x + i;
                    int next_y = y + j;
                    if (canMoveHelper(next_x, next_y, remaining_moves - 1)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    @Override
    public boolean canMove(int destination_x, int destination_y)
    {
        // Try moving to the destination in 3 or fewer moves recursively
        return canMoveHelper(destination_x, destination_y, 3);
    }
}