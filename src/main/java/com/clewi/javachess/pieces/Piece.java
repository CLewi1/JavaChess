package com.clewi.javachess.pieces;

import java.awt.Point;
import com.clewi.javachess.model.Board;
import java.io.Serializable;

public abstract class Piece implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int x;
    private int y;
    private boolean isCaptured = false;
    final private boolean isWhite;
    private String filePath;
    protected transient Board board;
    
    public Piece(int x, int y, boolean isWhite, String filePath, Board board, boolean isCaptured) {
        this.isWhite = isWhite;
        this.x = x;
        this.y = y;
        this.filePath = filePath;
        this.board = board;
        this.isCaptured = isCaptured;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String path) {
        this.filePath = path;
    }
    
    public boolean isWhite() {
        return isWhite;
    }
    
    public boolean isBlack() {
        return !isWhite;
    }
    
    public boolean isCaptured() {
        return isCaptured;
    }
    
    public void setCaptured(boolean captured) {
        this.isCaptured = captured;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public void setPosition(Point position) {
        this.x = position.x;
        this.y = position.y;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public Point getPosition() {
        return new Point(x, y);
    }
    
    public Board getBoard() {
        return board;
    }
    
    public void setBoard(Board board) {
        this.board = board;
    }
    
    public abstract boolean canMove(int destX, int destY);
    
    protected boolean isWithinBounds(int x, int y) {
        return x >= 0 && x <= 7 && y >= 0 && y <= 7;
    }
    
    protected boolean isOccupiedBySameColor(int x, int y) {
        if (board == null) return false;
        Piece piece = board.getPiece(x, y);
        return piece != null && piece.isWhite() == this.isWhite();
    }
    
    protected boolean isPathClear(int startX, int startY, int endX, int endY) {
        if (board == null) return true;
        
        // Check if path is horizontal, vertical or diagonal
        int deltaX = Integer.compare(endX - startX, 0);
        int deltaY = Integer.compare(endY - startY, 0);
        
        int x = startX + deltaX;
        int y = startY + deltaY;
        
        while (x != endX || y != endY) {
            if (board.getPiece(x, y) != null) {
                return false;
            }
            x += deltaX;
            y += deltaY;
        }
        
        return true;
    }
}
