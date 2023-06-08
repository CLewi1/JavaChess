package chessgui.pieces;

import chessgui.Board;

public class Bishop extends Piece {

    public Bishop(int x, int y, boolean is_white, String file_path, Board board, boolean is_captured)
    {
        super(x,y,is_white,file_path, board, is_captured);
    }
    
    @Override
    public boolean canMove(int destination_x, int destination_y)
    {
        
        // Get the current position of the bishop
        int x = this.getX();
        int y = this.getY();
        
        // Check if the destination is within the bounds of the board
        if(destination_x < 0 || destination_x > 7 || destination_y < 0 || destination_y > 7)
        {
            return false;
        }

        // Check if the bishop is moving diagonally
        boolean is_diagonal = Math.abs(destination_x - x) == Math.abs(destination_y - y);

        // only move diagonally
        if(!is_diagonal)
        {
            return false;
        }

        // Check if there is a piece in the way
        int x_direction = (destination_x - x) > 0 ? 1 : -1;
        int y_direction = (destination_y - y) > 0 ? 1 : -1;
        int x_position = x + x_direction;
        int y_position = y + y_direction;
        while(x_position != destination_x && y_position != destination_y)
        {
            if(Board.getPiece(x_position, y_position) != null)
            {
                return false;
            }
            x_position += x_direction;
            y_position += y_direction;
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

        return true;
    }
}
