package com.clewi.javachess.model;

import com.clewi.javachess.pieces.*;
import com.clewi.javachess.util.DebugUtils;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Board {
    private Piece[][] squares;
    private static final int BOARD_SIZE = 8;
    private List<Piece> whitePieces;
    private List<Piece> blackPieces;
    private static List<Move> moveHistory = new ArrayList<>();
    
    /**
     * Creates a new board with all pieces in their starting positions.
     */
    public Board() {
        this(true);
    }
    
    /**
     * Creates a new board, optionally with pieces in starting positions.
     * @param withPieces If true, initializes the board with pieces in starting positions.
     */
    public Board(boolean withPieces) {
        squares = new Piece[BOARD_SIZE][BOARD_SIZE];
        whitePieces = new ArrayList<>();
        blackPieces = new ArrayList<>();
        
        if (withPieces) {
            initializeBoard();
        }
    }

    private void initializeBoard() {
        // Set up white pieces
        setupPieces(true);
        
        // Set up black pieces
        setupPieces(false);
    }
    
    private void setupPieces(boolean isWhite) {
        int pawnRow = isWhite ? 6 : 1;
        int pieceRow = isWhite ? 7 : 0;
        List<Piece> pieces = isWhite ? whitePieces : blackPieces;
        
        // Setup pawns
        for (int i = 0; i < BOARD_SIZE; i++) {
            Pawn pawn = new Pawn(i, pawnRow, isWhite, "Pawn.png", this, false);
            pawn.setBoard(this); // Explicitly set the board
            pieces.add(pawn);
            squares[i][pawnRow] = pawn;
        }
        
        // Setup rooks
        Rook rook1 = new Rook(0, pieceRow, isWhite, "Rook.png", this, false);
        Rook rook2 = new Rook(7, pieceRow, isWhite, "Rook.png", this, false);
        rook1.setBoard(this);
        rook2.setBoard(this);
        pieces.add(rook1);
        pieces.add(rook2);
        squares[0][pieceRow] = rook1;
        squares[7][pieceRow] = rook2;
        
        // Setup knights
        Knight knight1 = new Knight(1, pieceRow, isWhite, "Knight.png", this, false);
        Knight knight2 = new Knight(6, pieceRow, isWhite, "Knight.png", this, false);
        knight1.setBoard(this);
        knight2.setBoard(this);
        pieces.add(knight1);
        pieces.add(knight2);
        squares[1][pieceRow] = knight1;
        squares[6][pieceRow] = knight2;
        
        // Setup bishops
        Bishop bishop1 = new Bishop(2, pieceRow, isWhite, "Bishop.png", this, false);
        Bishop bishop2 = new Bishop(5, pieceRow, isWhite, "Bishop.png", this, false);
        bishop1.setBoard(this);
        bishop2.setBoard(this);
        pieces.add(bishop1);
        pieces.add(bishop2);
        squares[2][pieceRow] = bishop1;
        squares[5][pieceRow] = bishop2;
        
        // Setup queen & king
        Queen queen = new Queen(3, pieceRow, isWhite, "Queen.png", this, false);
        King king = new King(4, pieceRow, isWhite, "King.png", this, false);
        queen.setBoard(this);
        king.setBoard(this);
        pieces.add(queen);
        pieces.add(king);
        squares[3][pieceRow] = queen;
        squares[4][pieceRow] = king;
    }

    public Piece getPiece(Point position) {
        return squares[position.x][position.y];
    }
    
    public Piece getPiece(int x, int y) {
        if (x < 0 || x >= BOARD_SIZE || y < 0 || y >= BOARD_SIZE) {
            return null;
        }
        return squares[x][y];
    }

    public void movePiece(Move move) {
        Point source = move.getSource();
        Point dest = move.getDestination();
        Piece piece = move.getPiece();
        move.setWasPieceHasMoved(piece.getHasMoved());
        
        // Handle en passant capture
        if (move.getMoveType() == MoveType.EN_PASSANT) {
            // For en passant, the captured pawn is at (dest.x, source.y)
            Piece capturedPawn = squares[dest.x][source.y];
            if (capturedPawn != null) {
                if (capturedPawn.isWhite()) {
                    whitePieces.remove(capturedPawn);
                } else {
                    blackPieces.remove(capturedPawn);
                }
                squares[dest.x][source.y] = null; // Remove from board
                capturedPawn.setCaptured(true);
                move.setEnPassantCapturedPiece(capturedPawn);
                DebugUtils.log("En passant capture: " + capturedPawn.getClass().getSimpleName() + " at " + dest.x + "," + source.y);
            }
        }

        // Handle castling
        if (move.getMoveType() == MoveType.CASTLE) {
            King king = (King) piece;
            boolean isKingside = dest.x > source.x; // Moving right = kingside
            
            if (king.isWhite()) {
                if (isKingside) {
                    // Kingside castling: move rook from h1 (7,7) to f1 (5,7)
                    Piece rook = squares[7][7];
                    if (rook instanceof Rook) {
                        squares[5][7] = rook;
                        squares[7][7] = null;
                        rook.setPosition(new Point(5, 7));
                        rook.setHasMoved();
                    }
                } else {
                    // Queenside castling: move rook from a1 (0,7) to d1 (3,7)
                    Piece rook = squares[0][7];
                    if (rook instanceof Rook) {
                        squares[3][7] = rook;
                        squares[0][7] = null;
                        rook.setPosition(new Point(3, 7));
                        rook.setHasMoved();
                    }
                }
            } else {
                if (isKingside) {
                    // Kingside castling: move rook from h8 (7,0) to f8 (5,0)
                    Piece rook = squares[7][0];
                    if (rook instanceof Rook) {
                        squares[5][0] = rook;
                        squares[7][0] = null;
                        rook.setPosition(new Point(5, 0));
                        rook.setHasMoved();
                    }
                } else {
                    // Queenside castling: move rook from a8 (0,0) to d8 (3,0)
                    Piece rook = squares[0][0];
                    if (rook instanceof Rook) {
                        squares[3][0] = rook;
                        squares[0][0] = null;
                        rook.setPosition(new Point(3, 0));
                        rook.setHasMoved();
                    }
                }
            }
            DebugUtils.log("Castling: " + (isKingside ? "Kingside" : "Queenside") + " for " + (king.isWhite() ? "White" : "Black"));
        }
        
        // Remove piece at destination if there is one (normal capture)
        Piece capturedPiece = squares[dest.x][dest.y];
        if (capturedPiece != null) {
            if (capturedPiece.isWhite()) {
                whitePieces.remove(capturedPiece);
            } else {
                blackPieces.remove(capturedPiece);
            }
            move.setCapturedPiece(capturedPiece);
            capturedPiece.setCaptured(true);
        }
        
        // Move the piece
        squares[source.x][source.y] = null;
        // Handle promotion: if pawn reaches last rank, replace with chosen piece
        if (move.getMoveType() == MoveType.PROMOTION && piece instanceof Pawn) {
            boolean isWhite = piece.isWhite();
            String choice = move.getPromotionChoice();
            if (choice == null) {
                choice = "Queen"; // default promotion
            }

            // Remove pawn from tracked pieces
            if (piece.isWhite()) {
                whitePieces.remove(piece);
            } else {
                blackPieces.remove(piece);
            }

            // Create the promoted piece
            Piece promoted = null;
            switch (choice.toLowerCase()) {
                case "queen":
                    promoted = new Queen(dest.x, dest.y, isWhite, "Queen.png", this, false);
                    break;
                case "rook":
                    promoted = new Rook(dest.x, dest.y, isWhite, "Rook.png", this, false);
                    break;
                case "bishop":
                    promoted = new Bishop(dest.x, dest.y, isWhite, "Bishop.png", this, false);
                    break;
                case "knight":
                    promoted = new Knight(dest.x, dest.y, isWhite, "Knight.png", this, false);
                    break;
                default:
                    promoted = new Queen(dest.x, dest.y, isWhite, "Queen.png", this, false);
            }

            if (promoted != null) {
                promoted.setBoard(this);
                promoted.setHasMoved();
                move.setPromotedPiece(promoted);
                squares[dest.x][dest.y] = promoted;
                // Track promoted piece
                trackPiece(promoted);
            }
        } else {
            squares[dest.x][dest.y] = piece;
            piece.setPosition(dest);
            piece.setHasMoved();
        }

        moveHistory.add(move);
    }

    /**
     * Undo the last move (uses move metadata stored in Move)
     * @param move The move to undo
     */
    public void undoLastMove(Move move) {
        if (move == null) return;

        Point source = move.getSource();
        Point dest = move.getDestination();
        Piece piece = move.getPiece();

        // If a promotion occurred, remove promoted piece and restore pawn
        if (move.getMoveType() == MoveType.PROMOTION && move.getPromotedPiece() != null) {
            Piece promoted = move.getPromotedPiece();
            // remove promoted piece from board and tracked pieces
            squares[dest.x][dest.y] = null;
            if (promoted.isWhite()) {
                whitePieces.remove(promoted);
            } else {
                blackPieces.remove(promoted);
            }
            // restore pawn
            squares[source.x][source.y] = piece;
            piece.setPosition(source);
            piece.setCaptured(false);
            trackPiece(piece);
        } else {
            // Handle castling undo
            if (move.getMoveType() == MoveType.CASTLE) {
                // Undo king movement
                squares[dest.x][dest.y] = null;
                squares[source.x][source.y] = piece;
                piece.setPosition(source);

                // Undo rook movement
                boolean isKingside = dest.x > source.x; // Moving right = kingside
                if (piece.isWhite()) {
                    if (isKingside) {
                        // Undo kingside castling: move rook from f1 (5,7) back to h1 (7,7)
                        Piece rook = squares[5][7];
                        if (rook instanceof Rook) {
                            squares[7][7] = rook;
                            squares[5][7] = null;
                            rook.setPosition(new Point(7, 7));
                        }
                    } else {
                        // Undo queenside castling: move rook from d1 (3,7) back to a1 (0,7)
                        Piece rook = squares[3][7];
                        if (rook instanceof Rook) {
                            squares[0][7] = rook;
                            squares[3][7] = null;
                            rook.setPosition(new Point(0, 7));
                        }
                    }
                } else {
                    if (isKingside) {
                        // Undo kingside castling: move rook from f8 (5,0) back to h8 (7,0)
                        Piece rook = squares[5][0];
                        if (rook instanceof Rook) {
                            squares[7][0] = rook;
                            squares[5][0] = null;
                            rook.setPosition(new Point(7, 0));
                        }
                    } else {
                        // Undo queenside castling: move rook from d8 (3,0) back to a8 (0,0)
                        Piece rook = squares[3][0];
                        if (rook instanceof Rook) {
                            squares[0][0] = rook;
                            squares[3][0] = null;
                            rook.setPosition(new Point(0, 0));
                        }
                    }
                }
            } else {
                // Handle en passant restoration
                if (move.getEnPassantCapturedPiece() != null) {
                    Piece ep = move.getEnPassantCapturedPiece();
                    squares[ep.getX()][ep.getY()] = ep;
                    ep.setCaptured(false);
                    trackPiece(ep);
                }

                // Restore captured piece at destination
                if (move.getCapturedPiece() != null) {
                    Piece cap = move.getCapturedPiece();
                    squares[dest.x][dest.y] = cap;
                    cap.setCaptured(false);
                    trackPiece(cap);
                } else {
                    squares[dest.x][dest.y] = null;
                }

                // Put moving piece back
                squares[source.x][source.y] = piece;
                piece.setPosition(source);
            }
        }

        // restore hasMoved flag for the moving piece to its original state
        piece.setHasMoved(move.wasPieceHasMoved());

        // For castling, also restore hasMoved flag for the rook (which should also be false for first-time castling)
        if (move.getMoveType() == MoveType.CASTLE) {
            // Find the rook that was moved during castling and reset its hasMoved flag
            boolean isKingside = move.getDestination().x > move.getSource().x;
            Point rookPosition;
            if (piece.isWhite()) {
                rookPosition = isKingside ? new Point(7, 7) : new Point(0, 7);
            } else {
                rookPosition = isKingside ? new Point(7, 0) : new Point(0, 0);
            }
            
            Piece rook = squares[rookPosition.x][rookPosition.y];
            if (rook instanceof Rook) {
                rook.setHasMoved(false); // Rook should not have moved before castling
            }
        }

        // Remove this move from move history
        if (!moveHistory.isEmpty()) {
            moveHistory.remove(moveHistory.size() - 1);
        }
    }
    
    public void updatePiecePosition(Piece piece, Point source, Point dest) {
        DebugUtils.log("Board: Updating piece position from " + 
                       source.x + "," + source.y + " to " + 
                       dest.x + "," + dest.y);
        
        // Remove from source
        if (source.x >= 0 && source.x < BOARD_SIZE && 
            source.y >= 0 && source.y < BOARD_SIZE) {
            squares[source.x][source.y] = null;
        }
        
        // Add to destination
        if (dest.x >= 0 && dest.x < BOARD_SIZE && 
            dest.y >= 0 && dest.y < BOARD_SIZE) {
            squares[dest.x][dest.y] = piece;
        }
        
        // Print board state for debugging
        DebugUtils.printBoard(this);
    }
    
    public void undoMove(Piece piece, Point source, Point dest, Piece capturedPiece) {
        // Remove piece from destination
        squares[dest.x][dest.y] = capturedPiece;
        
        // Put piece back at source
        squares[source.x][source.y] = piece;
        
        // Reset piece position
        piece.setX(source.x);
        piece.setY(source.y);
        
        DebugUtils.log("Undoing move: " + piece.getClass().getSimpleName() + 
                      " from " + dest.x + "," + dest.y + " back to " + source.x + "," + source.y);
    }
    
    public List<Piece> getPieces(boolean isWhite) {
        return isWhite ? whitePieces : blackPieces;
    }
    
    public King findKing(boolean isWhite) {
        List<Piece> pieces = isWhite ? whitePieces : blackPieces;
        for (Piece piece : pieces) {
            if (piece instanceof King) {
                return (King) piece;
            }
        }
        return null;
    }

    public void setSquare(int x, int y, Piece piece) {
        if (x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE) {
            squares[x][y] = piece;
        }
    }

    /**
     * Clears all pieces from the board.
     */
    public void clear() {
        for (int y = 0; y < BOARD_SIZE; y++) {
            for (int x = 0; x < BOARD_SIZE; x++) {
                squares[x][y] = null;
            }
        }
        whitePieces.clear();
        blackPieces.clear();
    }
    
    /**
     * Adds a piece to the tracked pieces list.
     */
    public void trackPiece(Piece piece) {
        if (piece != null) {
            List<Piece> pieces = piece.isWhite() ? whitePieces : blackPieces;
            if (!pieces.contains(piece)) {
                pieces.add(piece);
            }
        }
    }

    public static Move getLastMove() {
        if (moveHistory.isEmpty()) {
            return null;
        }
        return moveHistory.get(moveHistory.size() - 1);
    }

    public boolean isEnPassantPossible(Pawn adjacentPawn) {
        // get last move
        Move lastMove = getLastMove();
        if (lastMove == null) {
            return false;
        }

        // get moved piece
        Piece movedPiece = lastMove.getPiece();

        // check if the last piece was a pawn and is the same color
        if (!(movedPiece instanceof Pawn && movedPiece.isWhite() == adjacentPawn.isWhite())) {
            DebugUtils.log("Last moved piece is not a pawn of the same color");
            return false;
        }

        // check if the adjacent pawn is the one that was moved
        if (movedPiece.getX() != adjacentPawn.getX() || movedPiece.getY() != adjacentPawn.getY()) {
            DebugUtils.log("Adjacent pawn is not the moved piece");
            return false;
        }

        // Check if the last move was a pawn moving two squares forward
        Point source = lastMove.getSource();
        Point dest = lastMove.getDestination();
        if (source.x == dest.x && Math.abs(source.y - dest.y) == 2) {
            DebugUtils.log("En Passant is possible");
            return true;
        }

        return false;

    }

    public static List<Move> getMoveHistory() {
        return moveHistory;
    }
}
