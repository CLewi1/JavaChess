package com.clewi.javachess.pieces;

import com.clewi.javachess.Board;

public class King extends Piece {

    private boolean has_moved = false;

    public King(int x, int y, boolean is_white, String file_path, Board board, boolean is_captured)
    {
        super(x,y,is_white,file_path, board, is_captured);
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
        // Get the current position of the king
        int x = this.getX();
        int y = this.getY();
        
        // Check if the destination is within the bounds of the board
        if(destination_x < 0 || destination_x > 7 || destination_y < 0 || destination_y > 7)
        {
            return false;
        }

        // Check if the king is moving one square in any direction
        boolean is_one_square = Math.abs(destination_x - x) <= 1 && Math.abs(destination_y - y) <= 1;

        // only move one square
        if(!is_one_square)
        {
            return false;
        }

        // Check if the king is moving to a square that is occupied by a piece of the same color
        if(Board.getPiece(destination_x, destination_y) != null && Board.getPiece(destination_x, destination_y).isWhite() == this.isWhite())
        {
            return false;
        }

        // Check if king is in check 
        if(isKingInCheck(this.isWhite(), x, y))
        {
            // Check if king is moving into check
            if(isKingInCheck(this.isWhite(), destination_x, destination_y))
            {
                return false;
            }

            // 
        }





        // If all the above checks are passed, the king can move to the destination
        return true;
    }

        public static boolean isKingInCheck(boolean isWhiteKing, int destination_x, int destination_y) {
        // Iterate over all the pieces on the board
            for(int x = 0; x < 8; x++) {
                for(int y = 0; y < 8; y++) {
                    Piece piece = Board.getPiece(x, y);
                    if(piece != null && piece.isWhite() != isWhiteKing) {
                        // Check if the piece can attack the king
                        if(piece.canMove(destination_x, destination_y)) {
                            System.out.println("King in check");
                            return true;
                        }

                    } 
                            
                }
            }
            return false;
        }

        // is king in checkmate
        public boolean checkmate(boolean isWhiteKing) {
           // verify if king in check
            if (isWhiteKing) {
                for (Piece king : Board.White_Pieces) {
                    if (king instanceof King) {
                        for (Piece attackingPiece : Board.Black_Pieces) {
                            if (attackingPiece.canMove(king.getX(), king.getY())) {
                                // king is in check
                                for (int x = 0; x < 8; x++) {
                                    for (int y = 0; y < 8; y++) {
                                        if (king.canMove(x, y)) {
                                            // king can move to a safe square
                                            return false;
                                        }
                                        else {
                                            // king cannot move to a safe square
                                            // check if a piece can block the attack
                                            for (Piece defPiece : Board.White_Pieces) {
                                                if (defPiece.canBlock()) {
                                                    // piece can stop the attack
                                                    return false;
                                                }
                                            }
                                        }
                                    }
                                }
                                


                                return true;
                            }
                        }
                    }
                }
            }
            else {
                for (Piece king : Board.Black_Pieces) {
                    if (king instanceof King) {
                        for (Piece attackingPiece : Board.White_Pieces) {
                            if (attackingPiece.canMove(king.getX(), king.getY())) {
                                // king is in check
                                for (int x = 0; x < 8; x++) {
                                    for (int y = 0; y < 8; y++) {
                                        if (king.canMove(x, y)) {
                                            // king can move to a safe square
                                            return false;
                                        }
                                        else {
                                            // king cannot move to a safe square
                                            // check if a piece can block the attack
                                            for (Piece defPiece : Board.Black_Pieces) {
                                                if (defPiece.canBlock()) {
                                                    // piece can stop the attack
                                                    return false;
                                                }
                                            }
                                        }
                                    }
                                }
                                



                                return true;
                            }
                        }
                    }
                }
            }
            return false;
    }
}
