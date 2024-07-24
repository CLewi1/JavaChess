package com.clewi.javachess.pieces;

import java.util.List;
import java.awt.Point;

import com.clewi.javachess.Board;

public class Piece {
    private int x;
    private int y;
    public static boolean is_captured = false;
    final private boolean is_white;
    private String file_path;
    public Board board;
    
    public Piece(int x, int y, boolean is_white, String file_path, Board board, boolean is_captured)
    {
        this.is_white = is_white;
        this.x = x;
        this.y = y;
        this.file_path = file_path;
        this.board = board;
        Piece.is_captured = is_captured;
    }
    
    public String getFilePath()
    {
        return file_path;
    }
    
    public void setFilePath(String path)
    {
        this.file_path = path;
    }
    
    public boolean isWhite()
    {
        return is_white;
    }
    
    public boolean isBlack()
    {
        return !is_white;
    }
    
    public void setX(int x)
    {
        this.x = x;
    }
    
    public void setY(int y)
    {
        this.y = y;
    }
    
    public int getX()
    {
        return x;
    }
    
    public int getY()
    {
        return y;
    }
    
    public boolean canMove(int destination_x, int destination_y)
    {
        return false;
    }

    public boolean canBlock() {
        // check if piece can block check
        
        // find all attacked squares (algoritm)

        // see if piece can move to any of squares, if so return true

        // see if piece can take attacking piece, if so return true



        return false;
    }
}
