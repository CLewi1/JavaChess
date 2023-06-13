package chessgui.pieces;

import java.util.List;
import java.awt.Point;

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

        // check if king is in check
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
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
                                if (line.contains(new Point(destination_x, destination_y))) {
                                    return true;
                                }

                                // check if queen can take the attacking piece
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
        return true;
    }

    
}
