package com.clewi.javachess.model;

import com.clewi.javachess.pieces.Piece;
import java.awt.Point;

public class Move {
    private final Point source;
    private final Point destination;
    private final Piece piece;
    private final MoveType moveType;
    private final String promotionChoice;

    // Data stored for undo functionality
    private transient Piece capturedPiece;
    private transient Piece enPassantCapturedPiece;
    private transient Piece promotedPiece;
    private transient Boolean wasPieceHasMoved;
    private Integer whiteSecondsBefore;
    private Integer blackSecondsBefore;
    private Boolean whiteClockActiveBefore;
    private Boolean clocksRunningBefore;

    public Move(Point source, Point destination, Piece piece, MoveType moveType) {
        this(source, destination, piece, moveType, null);
    }

    public Move(Point source, Point destination, Piece piece, MoveType moveType, String promotionChoice) {
        this.source = source;
        this.destination = destination;
        this.piece = piece;
        this.moveType = moveType;
        this.promotionChoice = promotionChoice;
    }

    public Point getSource() {
        return source;
    }

    public Point getDestination() {
        return destination;
    }

    public Piece getPiece() {
        return piece;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public String getPromotionChoice() {
        return promotionChoice;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    public void setCapturedPiece(Piece p) {
        this.capturedPiece = p;
    }

    public Piece getEnPassantCapturedPiece() {
        return enPassantCapturedPiece;
    }

    public void setEnPassantCapturedPiece(Piece p) {
        this.enPassantCapturedPiece = p;
    }

    public Piece getPromotedPiece() {
        return promotedPiece;
    }

    public void setPromotedPiece(Piece p) {
        this.promotedPiece = p;
    }

    public Boolean wasPieceHasMoved() {
        return wasPieceHasMoved;
    }

    public void setWasPieceHasMoved(Boolean v) {
        this.wasPieceHasMoved = v;
    }

    public Integer getWhiteSecondsBefore() {
        return whiteSecondsBefore;
    }

    public void setWhiteSecondsBefore(Integer seconds) {
        this.whiteSecondsBefore = seconds;
    }

    public Integer getBlackSecondsBefore() {
        return blackSecondsBefore;
    }

    public void setBlackSecondsBefore(Integer seconds) {
        this.blackSecondsBefore = seconds;
    }

    public Boolean getWhiteClockActiveBefore() {
        return whiteClockActiveBefore;
    }

    public void setWhiteClockActiveBefore(Boolean active) {
        this.whiteClockActiveBefore = active;
    }

    public Boolean getClocksRunningBefore() {
        return clocksRunningBefore;
    }

    public void setClocksRunningBefore(Boolean running) {
        this.clocksRunningBefore = running;
    }

    @Override
    public String toString() {
        return piece.getAbbreviation() +
                (moveType == MoveType.CAPTURE ? "x" : "") +
                (char) ('a' + source.x) + (8 - source.y) +
                (char) ('a' + destination.x) + (8 - destination.y) +
                (moveType == MoveType.CASTLE ? " castling" : "") +
                (moveType == MoveType.PROMOTION ? " promoting=" + (promotionChoice != null ? promotionChoice : "?") : "");
    }
}
