package com.clewi.javachess.model;

import com.clewi.javachess.pieces.Piece;
import java.io.Serializable;
import java.util.List;

/**
 * Contains all the data needed to save and restore a game state.
 * This class and all its field types must be serializable.
 */
public class GameSaveData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Piece[][] boardState;
    private boolean isWhiteTurn;
    private GameState gameState;
    private List<Piece> whiteCapturedPieces;
    private List<Piece> blackCapturedPieces;
    
    public GameSaveData(Piece[][] boardState, boolean isWhiteTurn, 
                        GameState gameState, 
                        List<Piece> whiteCapturedPieces, 
                        List<Piece> blackCapturedPieces) {
        this.boardState = boardState;
        this.isWhiteTurn = isWhiteTurn;
        this.gameState = gameState;
        this.whiteCapturedPieces = whiteCapturedPieces;
        this.blackCapturedPieces = blackCapturedPieces;
    }
    
    public Piece[][] getBoardState() {
        return boardState;
    }
    
    public boolean isWhiteTurn() {
        return isWhiteTurn;
    }
    
    public GameState getGameState() {
        return gameState;
    }
    
    public List<Piece> getWhiteCapturedPieces() {
        return whiteCapturedPieces;
    }
    
    public List<Piece> getBlackCapturedPieces() {
        return blackCapturedPieces;
    }
}
