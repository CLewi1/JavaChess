package com.clewi.javachess.game;

import com.clewi.javachess.model.*;
import com.clewi.javachess.pieces.*;
import com.clewi.javachess.util.DebugUtils;

import java.awt.Point;
import java.io.*;
import java.util.*;

public class GameManager {
    // Game state management
    private GameState gameState;
    private List<GameStateObserver> observers;
    private List<Piece> whiteCapturedPieces;
    private List<Piece> blackCapturedPieces;
    private Board board;
    private Player whitePlayer;
    private Player blackPlayer;
    private Player currentPlayer;

    // Move management
    private MoveValidator moveValidator;
    private Piece selectedPiece;
    private List<DisplayableMove> loadedMoveHistory;

    // Clock-related fields
    private Integer whiteSecondsRemaining;
    private Integer blackSecondsRemaining;
    private int incrementSeconds;
    private boolean clockEnabled = false;
    private boolean whiteClockActive = false;
    private boolean clocksRunning = false;
    private Integer whiteSecondsAtTurnStart;
    private Integer blackSecondsAtTurnStart;
    
    public GameManager() {
        this.observers = new ArrayList<>();
        this.board = new Board();
        this.whitePlayer = new Player(true);
        this.blackPlayer = new Player(false);
        this.currentPlayer = whitePlayer;
        this.gameState = GameState.PLAYING;
        this.moveValidator = new MoveValidator(board);
        this.loadedMoveHistory = new ArrayList<>();
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

            // Save Clock State Before Move
            // Save the time values as they were at the START of this player's turn (if available),
            // otherwise fall back to the current remaining values.
            move.setWhiteSecondsBefore(this.whiteSecondsAtTurnStart != null ? this.whiteSecondsAtTurnStart : this.whiteSecondsRemaining);
            move.setBlackSecondsBefore(this.blackSecondsAtTurnStart != null ? this.blackSecondsAtTurnStart : this.blackSecondsRemaining);
            move.setWhiteClockActiveBefore(this.whiteClockActive);
            move.setClocksRunningBefore(this.clocksRunning);

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

            if (clockEnabled && !clocksRunning) {
                // Only start clocks after the first move
                clocksRunning = true;
                DebugUtils.logImportant("Clocks started.");
            }


            switchClocks();
        } else {
            System.out.println("Invalid move attempted");
        }
    }

    private void handleCapture(Piece piece) {
        getCurrentPlayer().addCapturedPiece(piece);
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
     * Get the move history for display.
     * During active gameplay, returns the actual move history.
     * After loading a game, returns the loaded move history for display.
     */
    public List<?> getDisplayMoveHistory() {
        if (!loadedMoveHistory.isEmpty()) {
            return loadedMoveHistory;
        }
        return Board.getMoveHistory();
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
        this.loadedMoveHistory = new ArrayList<>(); // Clear loaded move history
        
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
        whiteCapturedPieces = new ArrayList<>(whitePlayer.getCapturedPieces());
        blackCapturedPieces = new ArrayList<>(blackPlayer.getCapturedPieces());
        DebugUtils.logImportant("Saving move history with " + Board.getMoveHistory().size() + " moves.");
        
        // Convert Move objects to MoveData objects for serialization
        List<MoveData> moveHistoryData = new ArrayList<>();
        for (Move move : Board.getMoveHistory()) {
            moveHistoryData.add(convertMoveToData(move));
        }

        return new GameSaveData(boardState, isWhiteTurn, gameState,
                                whiteCapturedPieces, blackCapturedPieces, moveHistoryData,
                                whiteSecondsRemaining, blackSecondsRemaining);
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
        
        // Reconstruct actual Move objects so undo works after loading.
        List<Move> reconstructed = reconstructMovesFromData(saveData.getMoveHistory());
        Board.setMoveHistory(reconstructed);
        
        // Convert saved MoveData to displayable moves for the UI
        this.loadedMoveHistory = new ArrayList<>();
        if (saveData.getMoveHistory() != null) {
            for (MoveData moveData : saveData.getMoveHistory()) {
                loadedMoveHistory.add(new DisplayableMove(moveData));
            }
            DebugUtils.logImportant("Restored " + loadedMoveHistory.size() + " moves for display");
        }

        // Restore clock times
        this.whiteSecondsRemaining = saveData.getWhiteSecondsRemaining();
        this.blackSecondsRemaining = saveData.getBlackSecondsRemaining();
        if (whiteSecondsRemaining != null && blackSecondsRemaining != null) {
            this.clockEnabled = true;
            this.whiteClockActive = saveData.isWhiteTurn();
            this.clocksRunning = false;
            // When loading, treat the saved remaining times as the start-of-turn samples so undos restore sensibly.
            this.whiteSecondsAtTurnStart = this.whiteSecondsRemaining;
            this.blackSecondsAtTurnStart = this.blackSecondsRemaining;
        }
    }

    /**
     * Reconstruct Move objects from saved MoveData so undo/board history works after loading.
     * Best-effort mapping: prefers the piece found at the destination; falls back to searching
     * for a piece of the same type and color on the board.
     */
    private List<Move> reconstructMovesFromData(List<MoveData> moveDataList) {
        List<Move> reconstructed = new ArrayList<>();
        if (moveDataList == null) return reconstructed;

        for (MoveData md : moveDataList) {
            Point src = md.getSource();
            Point dst = md.getDestination();

            // Try to find the moving piece at the destination (final board state).
            Piece movingPiece = null;
            if (dst != null) {
                movingPiece = board.getPiece(dst.x, dst.y);
            }

            // Fallback: find a piece of the same type and color on the board.
            if (movingPiece == null && md.getPieceType() != null) {
                boolean isWhite = md.isWhite();
                for (Piece p : board.getPieces(isWhite)) {
                    if (p.getClass().getSimpleName().equals(md.getPieceType())) {
                        movingPiece = p;
                        break;
                    }
                }
            }

            // If we still couldn't find a piece, log and skip this move (defensive).
            if (movingPiece == null) {
                DebugUtils.logImportant("Could not locate moving piece for move " + md + " — skipping reconstruction entry.");
                continue;
            }

            // Construct Move using correct constructor ordering: (source, destination, piece, moveType, promotionChoice)
            Move m = new Move(src, dst, movingPiece, md.getMoveType(), md.getPromotionChoice());
            m.setWasPieceHasMoved(md.wasPieceHasMoved());

            // Attach captured piece by matching class name in captured lists
            if (md.getCapturedPieceType() != null) {
                List<Piece> capList = movingPiece.isWhite() ? blackPlayer.getCapturedPieces() : whitePlayer.getCapturedPieces();
                for (Piece p : capList) {
                    if (p.getClass().getSimpleName().equals(md.getCapturedPieceType())) {
                        m.setCapturedPiece(p);
                        break;
                    }
                }
            }

            // Attach en-passant captured piece if present
            if (md.getEnPassantCapturedPieceType() != null) {
                List<Piece> capList = movingPiece.isWhite() ? blackPlayer.getCapturedPieces() : whitePlayer.getCapturedPieces();
                for (Piece p : capList) {
                    if (p.getClass().getSimpleName().equals(md.getEnPassantCapturedPieceType())) {
                        m.setEnPassantCapturedPiece(p);
                        break;
                    }
                }
            }

            // Attach promoted piece (promoted piece should be on board at dst)
            if (md.getPromotedPieceType() != null && dst != null) {
                Piece promoted = board.getPiece(dst.x, dst.y);
                if (promoted != null && promoted.getClass().getSimpleName().equals(md.getPromotedPieceType())) {
                    m.setPromotedPiece(promoted);
                }
            }

            // No per-move clock metadata is stored in MoveData currently.
            // Restore per-move clock metadata (if available) so undo can reinstate clocks after load
            if (md.getWhiteSecondsBefore() != null) m.setWhiteSecondsBefore(md.getWhiteSecondsBefore());
            if (md.getBlackSecondsBefore() != null) m.setBlackSecondsBefore(md.getBlackSecondsBefore());
            if (md.getWhiteClockActiveBefore() != null) m.setWhiteClockActiveBefore(md.getWhiteClockActiveBefore());
            if (md.getClocksRunningBefore() != null) m.setClocksRunningBefore(md.getClocksRunningBefore());

            reconstructed.add(m);
        }

        return reconstructed;
    }

    public boolean undoMove() {
        Move lastMove = Board.getLastMove();
        if (lastMove == null) {
            return false; // No moves to undo
        }

        // Undo the move on the board
        board.undoLastMove(lastMove);

        // Restore clock state
        Integer whiteBefore = lastMove.getWhiteSecondsBefore();
        Integer blackBefore = lastMove.getBlackSecondsBefore();
        Boolean activeBefore = lastMove.getWhiteClockActiveBefore();
        Boolean runningBefore = lastMove.getClocksRunningBefore();

        if (whiteBefore != null && blackBefore != null && activeBefore != null && runningBefore != null) {
            this.whiteSecondsRemaining = whiteBefore;
            this.blackSecondsRemaining = blackBefore;
            this.whiteClockActive = activeBefore;
            this.clocksRunning = runningBefore;
            this.whiteSecondsAtTurnStart = this.whiteSecondsRemaining;
            this.blackSecondsAtTurnStart = this.blackSecondsRemaining;
            DebugUtils.logImportant("Clock state restored on undo.");
        } else {
            undoClockSwitch();
            DebugUtils.logImportant("No clock state to restore on undo. Falling back to switch.");
        }

        // Switch back to the previous player
        switchPlayer();

        // Update game state (assume we're back to playing unless we need to check for check)
        boolean currentPlayerInCheck = moveValidator.isKingInCheck(currentPlayer.isWhite());
        if (currentPlayerInCheck) {
            boolean isCheckmate = moveValidator.isCheckmate(currentPlayer.isWhite());
            if (isCheckmate) {
                gameState = GameState.CHECKMATE;
            } else {
                gameState = GameState.CHECK;
            }
        } else {
            gameState = GameState.PLAYING;
        }

        // Restore captured pieces if needed
        if (lastMove.getCapturedPiece() != null) {
            Piece captured = lastMove.getCapturedPiece();
            // Remove from current player's captured pieces
            getCurrentPlayer().removeCapturedPiece(captured);
        }

        if (lastMove.getEnPassantCapturedPiece() != null) {
            Piece enPassantCaptured = lastMove.getEnPassantCapturedPiece();
            // Remove from current player's captured pieces  
            getCurrentPlayer().removeCapturedPiece(enPassantCaptured);
        }

        // Notify observers of the change
        notifyObservers();
        
        return true;
    }
    
    /**
     * Converts a Move object to a MoveData object for serialization
     */
    private MoveData convertMoveToData(Move move) {
        MoveData moveData = new MoveData(
            move.getSource(),
            move.getDestination(),
            move.getPiece().getClass().getSimpleName(),
            move.getPiece().isWhite(),
            move.getMoveType(),
            move.getPromotionChoice()
        );
        
        // Set additional data for undo functionality
        if (move.getCapturedPiece() != null) {
            moveData.setCapturedPieceType(move.getCapturedPiece().getClass().getSimpleName());
        }
        if (move.getEnPassantCapturedPiece() != null) {
            moveData.setEnPassantCapturedPieceType(move.getEnPassantCapturedPiece().getClass().getSimpleName());
        }
        if (move.getPromotedPiece() != null) {
            moveData.setPromotedPieceType(move.getPromotedPiece().getClass().getSimpleName());
        }
        moveData.setWasPieceHasMoved(move.wasPieceHasMoved());
        // Store clock metadata for undo restoration
        moveData.setWhiteSecondsBefore(move.getWhiteSecondsBefore());
        moveData.setBlackSecondsBefore(move.getBlackSecondsBefore());
        moveData.setWhiteClockActiveBefore(move.getWhiteClockActiveBefore());
        moveData.setClocksRunningBefore(move.getClocksRunningBefore());
        
        return moveData;
    }

    /**
     * Clock-related methods
    */

    public void initClocks(int secondsPerPlayer, int incrementSeconds) {
        this.whiteSecondsRemaining = secondsPerPlayer;
        this.blackSecondsRemaining = secondsPerPlayer;
        this.incrementSeconds = incrementSeconds;
        this.clockEnabled = true;
        this.whiteClockActive = true;
        this.clocksRunning = false;

        // At the start, both players' "turn start" times are their initial allocations.
        this.whiteSecondsAtTurnStart = this.whiteSecondsRemaining;
        this.blackSecondsAtTurnStart = this.blackSecondsRemaining;
    }

    public void disableClocks() {
        this.clockEnabled = false;
        this.whiteSecondsRemaining = null;
        this.blackSecondsRemaining = null;
        this.whiteSecondsAtTurnStart = null;
        this.blackSecondsAtTurnStart = null;
    }

    public boolean isClockEnabled() {
        return clockEnabled;
    }

    public Integer getWhiteSecondsRemaining() {
        return whiteSecondsRemaining;
    }

    public Integer getBlackSecondsRemaining() {
        return blackSecondsRemaining;
    }

    // Called every second by UI Timer. Returns true if timeout occurred.
    public boolean tick() {
        if (!clockEnabled) return false;
        if (!clocksRunning) return false;

        if (whiteClockActive) {
            whiteSecondsRemaining = Math.max(0, whiteSecondsRemaining - 1);
            if (whiteSecondsRemaining == 0) {
                handleTimeout(false); // white timed out -> black wins
                return true;
            }
        } else {
            blackSecondsRemaining = Math.max(0, blackSecondsRemaining - 1);
            if (blackSecondsRemaining == 0) {
                handleTimeout(true); // black timed out -> white wins
                return true;
            }
        }
        return false;
    }

    private void handleTimeout(boolean whiteWins) {
        clockEnabled = false;
        gameState = GameState.TIMEOUT;
        DebugUtils.logImportant((whiteWins ? "White" : "Black") + " wins on time!");
        notifyObservers();
    }

    public void switchClocks() {
        if (!clockEnabled) return;
        if (whiteClockActive) {
            // White just moved, add increment
            whiteSecondsRemaining += incrementSeconds;
        } else {
            // Black just moved, add increment
            blackSecondsRemaining += incrementSeconds;
        }
        // Flip active clock
        whiteClockActive = !whiteClockActive;

        // Record the remaining time as the start-of-turn time for the player who is now active
        if (whiteClockActive) {
            whiteSecondsAtTurnStart = whiteSecondsRemaining;
        } else {
            blackSecondsAtTurnStart = blackSecondsRemaining;
        }
    }

    public void undoClockSwitch() {
        if (!clockEnabled) return;
        whiteClockActive = !whiteClockActive;
        // Remove increment added during switch
        if (whiteClockActive) {
            whiteSecondsRemaining = Math.max(0, whiteSecondsRemaining - incrementSeconds);
            // Update start-of-turn sample
            whiteSecondsAtTurnStart = whiteSecondsRemaining;
        } else {
            blackSecondsRemaining = Math.max(0, blackSecondsRemaining - incrementSeconds);
            blackSecondsAtTurnStart = blackSecondsRemaining;
        }
    }

    public static String formatTime(int seconds) {
        int s = Math.max(0, seconds);
        int m = s / 60;
        int sec = s % 60;
        return String.format("%02d:%02d", m, sec);
    }

    public boolean areClocksRunning() {
        return clocksRunning;
    }


    



}
