package com.clewi.javachess.game;

import com.clewi.javachess.model.*;
import com.clewi.javachess.pieces.*;
// Remove this import to prevent circular dependency
// import com.clewi.javachess.ui.ChessGUI;
import com.clewi.javachess.util.DebugUtils;
import java.awt.Point;
import java.io.*;
import java.util.*;

public class GameManager {
    private GameState gameState;
    private List<GameStateObserver> observers;
    private Board board;
    private Player whitePlayer;
    private Player blackPlayer;
    private Player currentPlayer;
    private MoveValidator moveValidator;
    private Piece selectedPiece;

    public GameManager() {
        this.observers = new ArrayList<>();
        this.board = new Board();
        this.whitePlayer = new Player(true);
        this.blackPlayer = new Player(false);
        this.currentPlayer = whitePlayer;
        this.gameState = GameState.PLAYING;
        this.moveValidator = new MoveValidator(board);
    }

    public void registerObserver(GameStateObserver observer) {
        observers.add(observer);
    }

    public void makeMove(Move move) {
        DebugUtils.log("GameManager: Attempting move");
        
        // Check if the move is valid according to all chess rules
        if (moveValidator.isValidMove(move)) {
            Piece piece = move.getPiece();
            Point source = move.getSource();
            Point dest = move.getDestination();
            
            // Update pawn's hasMoved status if it's a pawn
            if (piece instanceof Pawn) {
                ((Pawn) piece).setHasMoved();
            }
            
            // Handle captures
            Piece capturedPiece = board.getPiece(dest.x, dest.y);
            if (capturedPiece != null) {
                DebugUtils.logImportant("Capturing " + capturedPiece.getClass().getSimpleName());
                capturedPiece.setCaptured(true);
                handleCapture(capturedPiece);
            }
            
            // Handle en passant captures
            if (move.getMoveType() == MoveType.EN_PASSANT) {
                Piece enPassantCaptured = board.getPiece(dest.x, source.y);
                if (enPassantCaptured != null) {
                    DebugUtils.logImportant("En passant capturing " + enPassantCaptured.getClass().getSimpleName());
                    enPassantCaptured.setCaptured(true);
                    handleCapture(enPassantCaptured);
                }
            }
            
            // Update board
            board.movePiece(move);
            
            // Check for check/checkmate conditions
            boolean opponentInCheck = moveValidator.isKingInCheck(!piece.isWhite());
            if (opponentInCheck) {
                boolean isCheckmate = moveValidator.isCheckmate(!piece.isWhite());
                if (isCheckmate) {
                    DebugUtils.logImportant("CHECKMATE!");
                    gameState = GameState.CHECKMATE;
                } else {
                    DebugUtils.logImportant("CHECK!");
                    gameState = GameState.CHECK;
                }
            } else {
                gameState = GameState.PLAYING;
            }
            
            // Switch player and notify observers
            switchPlayer();
            notifyObservers();
        } else {
            System.out.println("Invalid move attempted");
        }
    }

    private void handleCapture(Piece piece) {
        getCurrentPlayer().addCapturedPiece(piece);
    }

    private boolean isValidMove(Move move) {
        return moveValidator.isValidMove(move);
    }

    private void notifyObservers() {
        for (GameStateObserver observer : observers) {
            observer.onGameStateChanged(new GameStateEvent(this));
        }
    }

    public GameState getGameState() {
        return this.gameState;
    }
    
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getOppositePlayer() {
        return currentPlayer == whitePlayer ? blackPlayer : whitePlayer;
    }
    
    private void switchPlayer() {
        currentPlayer = currentPlayer == whitePlayer ? blackPlayer : whitePlayer;
    }
    
    private boolean isCheckmate(Player player) {
        return moveValidator.isCheckmate(player.isWhite());
    }

    // Add these necessary methods for the UI
    public void selectPiece(int x, int y) {
        Piece piece = board.getPiece(x, y);
        if (piece != null && piece.isWhite() == currentPlayer.isWhite()) {
            selectedPiece = piece;
        }
    }
    
    public void deselectPiece() {
        selectedPiece = null;
    }
    
    public Piece getSelectedPiece() {
        return selectedPiece;
    }
    
    public Board getBoard() {
        return board;
    }
    
    /**
     * Resets the game to its initial state.
     */
    public void resetGame() {
        // Create a new board and reset pieces
        this.board = new Board();
        this.moveValidator = new MoveValidator(board);
        
        // Reset players
        this.whitePlayer = new Player(true);
        this.blackPlayer = new Player(false);
        this.currentPlayer = whitePlayer; // White always starts
        
        // Reset game state
        this.gameState = GameState.PLAYING;
        this.selectedPiece = null;
        
        // Notify observers about the reset
        notifyObservers();
    }

    /**
     * Saves the current game state to a file
     * @param file The file to save to
     * @return true if save was successful
     */
    public boolean saveGame(File file) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            // Create save data from current game state
            GameSaveData saveData = createSaveData();
            
            // Write the save data to the file
            out.writeObject(saveData);
            
            DebugUtils.logImportant("Game saved successfully to: " + file.getPath());
            return true;
        } catch (IOException e) {
            DebugUtils.logImportant("Error saving game: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Loads a game state from a file
     * @param file The file to load from
     * @return true if load was successful
     */
    public boolean loadGame(File file) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            // Read save data from file
            GameSaveData saveData = (GameSaveData) in.readObject();
            
            // Restore game state from save data
            restoreFromSaveData(saveData);
            
            // Notify observers about state change
            notifyObservers();
            
            DebugUtils.logImportant("Game loaded successfully from: " + file.getPath());
            return true;
        } catch (IOException | ClassNotFoundException e) {
            DebugUtils.logImportant("Error loading game: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private GameSaveData createSaveData() {
        // Get the current board state
        Piece[][] boardState = new Piece[8][8];
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                boardState[x][y] = board.getPiece(x, y);
            }
        }
        
        boolean isWhiteTurn = currentPlayer == whitePlayer;
        List<Piece> whiteCapturedPieces = new ArrayList<>(whitePlayer.getCapturedPieces());
        List<Piece> blackCapturedPieces = new ArrayList<>(blackPlayer.getCapturedPieces());
        
        return new GameSaveData(boardState, isWhiteTurn, gameState, 
                                whiteCapturedPieces, blackCapturedPieces);
    }
    
    private void restoreFromSaveData(GameSaveData saveData) {
        // Create a new empty board (no default pieces)
        this.board = new Board(false);
        
        // Restore pieces to their positions
        Piece[][] boardState = saveData.getBoardState();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece piece = boardState[x][y];
                if (piece != null) {
                    // Update piece's board reference
                    piece.setBoard(board);
                    
                    // Add piece to board
                    board.setSquare(x, y, piece);
                    
                    // Make sure piece is tracked in the appropriate list
                    board.trackPiece(piece);
                }
            }
        }
        
        // Restore player information
        this.whitePlayer = new Player(true);
        this.blackPlayer = new Player(false);
        
        // Add captured pieces
        if (saveData.getWhiteCapturedPieces() != null) {
            saveData.getWhiteCapturedPieces().forEach(p -> {
                p.setBoard(board);  // Ensure board reference
                whitePlayer.addCapturedPiece(p);
            });
        }

        if (saveData.getBlackCapturedPieces() != null) {
            saveData.getBlackCapturedPieces().forEach(p -> {
                p.setBoard(board);  // Ensure board reference
                blackPlayer.addCapturedPiece(p);
            });
        }

        // Restore current player based on turn
        this.currentPlayer = saveData.isWhiteTurn() ? whitePlayer : blackPlayer;
        
        // Restore game state
        this.gameState = saveData.getGameState();
        
        // Reset other components
        this.selectedPiece = null;
        this.moveValidator = new MoveValidator(board);
    }
}
