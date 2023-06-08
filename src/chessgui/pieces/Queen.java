package chessgui.pieces;

import chessgui.Board;

public class Queen extends Piece {

    public Queen(int x, int y, boolean is_white, String file_path, Board board, boolean is_captured)
    {
        super(x,y,is_white,file_path, board, is_captured);
    }
    
    @Override
    public boolean canMove(int destination_x, int destination_y)
    {
        // Get the current position of the queen
        int x = this.getX();
        int y = this.getY();

        // Check if the destination is within the bounds of the board
        if(destination_x < 0 || destination_x > 7 || destination_y < 0 || destination_y > 7)
        {
            return false;
        }

        // Check if the queen is moving forward, backward, sideways, or diagonally
        boolean is_forward_or_backward = destination_x == x && destination_y != y;
        boolean is_sideways = destination_x != x && destination_y == y;
        boolean is_diagonal = Math.abs(destination_x - x) == Math.abs(destination_y - y);

        // only move forward, backward, sideways, or diagonally
        if(!is_forward_or_backward && !is_sideways && !is_diagonal)
        {
            return false;
        }

        // Check if there is a piece in the way
        int x_direction = Integer.compare(destination_x, x);
        int y_direction = Integer.compare(destination_y, y);
        int x_position = x + x_direction;
        int y_position = y + y_direction;
        while(x_position != destination_x || y_position != destination_y)
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
