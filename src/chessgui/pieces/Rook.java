package chessgui.pieces;

import chessgui.Board;

public class Rook extends Piece {

    public Rook(int x, int y, boolean is_white, String file_path, Board board, boolean is_captured)
    {
        super(x,y,is_white,file_path, board, is_captured);
    }
    
    @Override
    public boolean canMove(int destination_x, int destination_y)
    {
        // Get the current position of the rook
        int x = this.getX();
        int y = this.getY();
        
        // Check if the destination is within the bounds of the board
        if(destination_x < 0 || destination_x > 7 || destination_y < 0 || destination_y > 7)
        {
            return false;
        }

        // Check if the rook is moving forward, backward, or sideways
        boolean is_forward_or_backward = destination_x == x && destination_y != y;
        boolean is_sideways = destination_x != x && destination_y == y;

        // only move forward, backward, or sideways
        if(!is_forward_or_backward && !is_sideways)
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
    Piece piece = Board.getPiece(destination_x, destination_y);
    if(piece != null)
    {
        if(piece.isWhite() && this.isWhite())
        {
            return false;
        }
        if(!piece.isWhite() && !this.isWhite())
        {
            return false;
        }
    }

        return true;
    }
}
