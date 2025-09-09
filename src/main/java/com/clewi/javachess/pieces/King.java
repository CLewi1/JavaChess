package com.clewi.javachess.pieces;

import com.clewi.javachess.model.Board;

public class King extends Piece {

    public King(int x, int y, boolean is_white, String file_path, Board board, boolean is_captured)
    {
        super(x,y,is_white,file_path, board, is_captured, "King", false);
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

        // Check for castling (king moves 2 squares horizontally)
        if (Math.abs(destination_x - x) == 2 && destination_y == y) {
            return canCastle(destination_x > x); // true for kingside, false for queenside
        }

        // Check if the king is moving one square in any direction
        if (!isOneSquareMove(x, y, destination_x, destination_y)) {
            return false;
        }
        
        // Check if destination is occupied by a piece of the same color
        if (isOccupiedBySameColor(destination_x, destination_y)) {
            return false;
        }

        // Check if the destination square is under attack by opponent pieces
        if (isSquareUnderAttack(destination_x, destination_y)) {
            return false;
        }

        // If all the above checks are passed, the king can move to the destination
        return true;
    }

    private boolean isSquareUnderAttack(int x, int y) {
        if (board == null) {
            return false;
        }
        
        // Check if any opponent piece can attack this square
        for (Piece piece : board.getPieces(!this.isWhite())) {
            if (piece.canMove(x, y)) {
                return true;
            }
        }
        return false;
    }

    private boolean isOneSquareMove(int x, int y, int destination_x, int destination_y)
    {
        return Math.abs(destination_x - x) <= 1 && Math.abs(destination_y - y) <= 1;
    }
    
    /**
     * Checks if castling is possible
     * @param isKingside true for kingside (short) castling, false for queenside (long) castling
     * @return true if castling is possible
     */
    private boolean canCastle(boolean isKingside) {
        if (board == null) {
            return false;
        }
        
        // King and rook must not have moved
        if (getHasMoved()) {
            return false;
        }
        
        int kingX = getX();
        int kingY = getY();
        int rookX = isKingside ? 7 : 0;
        int rookY = kingY;
        
        Piece rook = board.getPiece(rookX, rookY);
        if (!(rook instanceof Rook) || rook.getHasMoved()) {
            return false;
        }
        
        // Path between king and rook must be clear
        int direction = isKingside ? 1 : -1;
        int endX = isKingside ? 6 : 2;
        
        for (int x = kingX + direction; x != rookX; x += direction) {
            if (board.getPiece(x, kingY) != null) {
                return false; // Path is blocked
            }
        }
        
        // King cannot be in check, and cannot pass through or end up in check
        if (isSquareUnderAttack(kingX, kingY)) {
            return false; // Currently in check
        }
        
        // Check squares the king passes through and ends up on
        for (int x = kingX + direction; x != endX + direction; x += direction) {
            if (isSquareUnderAttack(x, kingY)) {
                return false; // Would pass through or end up in check
            }
        }
        
        return true;
    }
}
