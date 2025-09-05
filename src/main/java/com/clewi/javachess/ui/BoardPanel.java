package com.clewi.javachess.ui;

import com.clewi.javachess.game.GameManager;
import com.clewi.javachess.model.*;
import com.clewi.javachess.pieces.Piece;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.clewi.javachess.model.Board;
import com.clewi.javachess.pieces.Pawn;

public class BoardPanel extends JPanel {
    private static final int SQUARE_SIZE = 65;
    private static final Color LIGHT_SQUARE = new Color(240, 217, 181);
    private static final Color DARK_SQUARE = new Color(181, 136, 99);
    private static final Color SELECTED_SQUARE = new Color(106, 135, 222, 150);
    
    private final GameManager gameManager;
    private Point selectedSquare;
    private Map<String, Image> pieceImages = new HashMap<>();

    public BoardPanel(GameManager gameManager) {
        this.gameManager = gameManager;
        this.setPreferredSize(new Dimension(SQUARE_SIZE * 8, SQUARE_SIZE * 8));
        this.addMouseListener(createMouseAdapter());
        loadImages();
        setFocusable(true);
        requestFocusInWindow();
    }

    private void loadImages() {
        try {
            String[] pieces = {"King", "Queen", "Bishop", "Knight", "Rook", "Pawn"};
            for (String piece : pieces) {
                String whitePath = "lib/white_pieces/" + piece + ".png";
                String blackPath = "lib/black_pieces/" + piece + ".png";
                
                Image whiteImage = ImageIO.read(new File(whitePath));
                Image blackImage = ImageIO.read(new File(blackPath));
                
                pieceImages.put("white_" + piece, whiteImage);
                pieceImages.put("black_" + piece, blackImage);
            }
        } catch (IOException e) {
            System.out.println("Error loading piece images: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw the board squares
        drawBoard(g);
        
        // Draw the selected square highlight if any
        if (selectedSquare != null) {
            g.setColor(SELECTED_SQUARE);
            g.fillRect(selectedSquare.x * SQUARE_SIZE, 
                      selectedSquare.y * SQUARE_SIZE, 
                      SQUARE_SIZE, SQUARE_SIZE);
        }
        
        // Draw the pieces
        drawPieces(g);
    }

    private void drawBoard(Graphics g) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                boolean isLightSquare = (row + col) % 2 == 0;
                g.setColor(isLightSquare ? LIGHT_SQUARE : DARK_SQUARE);
                g.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }

    private void drawPieces(Graphics g) {
        Board board = gameManager.getBoard();
        
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(col, row);
                if (piece != null) {
                    String colorPrefix = piece.isWhite() ? "white_" : "black_";
                    String pieceType = piece.getClass().getSimpleName();
                    Image pieceImage = pieceImages.get(colorPrefix + pieceType);
                    
                    if (pieceImage != null) {
                        g.drawImage(pieceImage, 
                                   col * SQUARE_SIZE, 
                                   row * SQUARE_SIZE, 
                                   SQUARE_SIZE, 
                                   SQUARE_SIZE, 
                                   null);
                    }
                }
            }
        }
    }

    public void refresh() {
        repaint();
    }

    private MouseAdapter createMouseAdapter() {
        return new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int col = e.getX() / SQUARE_SIZE;
                int row = e.getY() / SQUARE_SIZE;
                
                System.out.println("Mouse clicked at: " + col + ", " + row);
                handleSquareClick(new Point(col, row));
            }
        };
    }

    private void handleSquareClick(Point clickedSquare) {
        // Debug output
        System.out.println("Handling click at: " + clickedSquare.x + ", " + clickedSquare.y);
        
        // Make sure the click is within bounds
        if (clickedSquare.x < 0 || clickedSquare.x > 7 || clickedSquare.y < 0 || clickedSquare.y > 7) {
            System.out.println("Click out of bounds");
            return;
        }
        
        Piece clickedPiece = gameManager.getBoard().getPiece(clickedSquare.x, clickedSquare.y);
        Piece selectedPiece = gameManager.getSelectedPiece();
        
        // If no piece is selected yet
        if (selectedPiece == null) {
            // Try to select a piece of the current player's color
            if (clickedPiece != null && clickedPiece.isWhite() == gameManager.getCurrentPlayer().isWhite()) {
                System.out.println("Selecting piece: " + clickedPiece.getClass().getSimpleName());
                gameManager.selectPiece(clickedSquare.x, clickedSquare.y);
                selectedSquare = clickedSquare;
                repaint();
            } else {
                System.out.println("Cannot select: no piece or wrong color");
            }
        } 
        // If a piece is already selected
        else {
            Point selectedPosition = new Point(selectedPiece.getX(), selectedPiece.getY());
            
            // If clicking on the same piece, deselect it
            if (selectedPosition.x == clickedSquare.x && selectedPosition.y == clickedSquare.y) {
                System.out.println("Deselecting piece");
                gameManager.deselectPiece();
                selectedSquare = null;
                repaint();
                return;
            }
            
            // Try to move the selected piece
            System.out.println("Attempting move from " + selectedPosition.x + "," + selectedPosition.y + 
                              " to " + clickedSquare.x + "," + clickedSquare.y);
            
            // Create a move
            MoveType moveType = determineMoveType(selectedPiece, selectedPosition, clickedSquare);
            Move move = new Move(selectedPosition, clickedSquare, selectedPiece, moveType);
            
            // Process the move through the game manager
            gameManager.makeMove(move);

            // Print last move details
            Move lastMove = Board.getLastMove();
            if (lastMove != null) {
                System.out.println("Last move: " + lastMove);
            }

            // Deselect piece after move attempt
            gameManager.deselectPiece();
            selectedSquare = null;
            repaint();
        }
    }
    
    private MoveType determineMoveType(Piece selectedPiece, Point source, Point dest) {
        Piece destPiece = gameManager.getBoard().getPiece(dest.x, dest.y);
        
        // Check for en passant
        if (selectedPiece instanceof Pawn) {
            int direction = selectedPiece.isWhite() ? -1 : 1;
            // En passant: diagonal move to empty square
            if (Math.abs(dest.x - source.x) == 1 && dest.y == source.y + direction && destPiece == null) {
                Piece adjacentPiece = gameManager.getBoard().getPiece(dest.x, source.y);
                if (adjacentPiece instanceof Pawn &&
                    adjacentPiece.isWhite() != selectedPiece.isWhite()) {
                    if (gameManager.getBoard().isEnPassantPossible((Pawn) adjacentPiece)) {
                        return MoveType.EN_PASSANT;
                    }
                }
            }
        }
        
        // Normal capture or move
        return destPiece != null ? MoveType.CAPTURE : MoveType.NORMAL;
    }
}
