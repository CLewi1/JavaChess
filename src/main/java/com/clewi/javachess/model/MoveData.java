package com.clewi.javachess.model;

import java.io.Serializable;
import java.awt.Point;

/**
 * Serializable representation of a chess move that doesn't contain object references.
 * Used for saving/loading move history without serialization issues.
 */
public class MoveData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final Point source;
    private final Point destination;
    private final String pieceType;
    private final boolean isWhite;
    private final MoveType moveType;
    private final String promotionChoice;
    
    // Additional data needed for undo functionality
    private String capturedPieceType;
    private String enPassantCapturedPieceType;
    private String promotedPieceType;
    private Boolean wasPieceHasMoved;
    
    // Clock metadata captured at the start of the move (used to restore clocks on undo)
    private Integer whiteSecondsBefore;
    private Integer blackSecondsBefore;
    private Boolean whiteClockActiveBefore;
    private Boolean clocksRunningBefore;
    
    public MoveData(Point source, Point destination, String pieceType, boolean isWhite, 
                   MoveType moveType, String promotionChoice) {
        this.source = source;
        this.destination = destination;
        this.pieceType = pieceType;
        this.isWhite = isWhite;
        this.moveType = moveType;
        this.promotionChoice = promotionChoice;
    }
    
    // Getters
    public Point getSource() { return source; }
    public Point getDestination() { return destination; }
    public String getPieceType() { return pieceType; }
    public boolean isWhite() { return isWhite; }
    public MoveType getMoveType() { return moveType; }
    public String getPromotionChoice() { return promotionChoice; }
    
    public String getCapturedPieceType() { return capturedPieceType; }
    public void setCapturedPieceType(String capturedPieceType) { 
        this.capturedPieceType = capturedPieceType; 
    }
    
    public String getEnPassantCapturedPieceType() { return enPassantCapturedPieceType; }
    public void setEnPassantCapturedPieceType(String enPassantCapturedPieceType) { 
        this.enPassantCapturedPieceType = enPassantCapturedPieceType; 
    }
    
    public String getPromotedPieceType() { return promotedPieceType; }
    public void setPromotedPieceType(String promotedPieceType) { 
        this.promotedPieceType = promotedPieceType; 
    }
    
    public Boolean wasPieceHasMoved() { return wasPieceHasMoved; }
    public void setWasPieceHasMoved(Boolean wasPieceHasMoved) { 
        this.wasPieceHasMoved = wasPieceHasMoved; 
    }

    public Integer getWhiteSecondsBefore() { return whiteSecondsBefore; }
    public void setWhiteSecondsBefore(Integer whiteSecondsBefore) { this.whiteSecondsBefore = whiteSecondsBefore; }

    public Integer getBlackSecondsBefore() { return blackSecondsBefore; }
    public void setBlackSecondsBefore(Integer blackSecondsBefore) { this.blackSecondsBefore = blackSecondsBefore; }

    public Boolean getWhiteClockActiveBefore() { return whiteClockActiveBefore; }
    public void setWhiteClockActiveBefore(Boolean whiteClockActiveBefore) { this.whiteClockActiveBefore = whiteClockActiveBefore; }

    public Boolean getClocksRunningBefore() { return clocksRunningBefore; }
    public void setClocksRunningBefore(Boolean clocksRunningBefore) { this.clocksRunningBefore = clocksRunningBefore; }
    
    @Override
    public String toString() {
        return pieceType + " from " + (char)('a' + source.x) + (8 - source.y) + 
               " to " + (char)('a' + destination.x) + (8 - destination.y);
    }
}
