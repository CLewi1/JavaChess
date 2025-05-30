package com.clewi.javachess.pieces;
/**
 ** Java Program to Implement Bresenham Line Algorithm
 **/
 

import java.util.ArrayList;
import java.util.List;
import java.awt.Point;
 
/** Class Bresenham **/
public class Bresenham 
{
    /** function findLine() - to find that belong to line connecting the two points **/ 
    public static List<Point> findLine(Point[][] grid, int x0, int y0, int x1, int y1) 
    {                    
        List<Point> line = new ArrayList<Point>();
 
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
 
        int sx = x0 < x1 ? 1 : -1; 
        int sy = y0 < y1 ? 1 : -1; 
 
        int err = dx-dy;
        int e2;
 
        while (true) 
        {
            line.add(grid[x0][y0]);
 
            if (x0 == x1 && y0 == y1) 
                break;
 
            e2 = 2 * err;
            if (e2 > -dy) 
            {
                err = err - dy;
                x0 = x0 + sx;
            }
 
            if (e2 < dx) 
            {
                err = err + dx;
                y0 = y0 + sy;
            }
        }                                
        return line;
    }
 
    /** function plot() - to visualize grid **/
    public void plot(Point[][] grid, List<Point> line)
    {
        int rows = grid.length;
        int cols = grid[0].length;
 
        System.out.println("\nPlot : \n");
 
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                if (line.contains(grid[i][j]))
                    System.out.print("*");
                else
                    System.out.print("X");
            }
            System.out.println();
        }
    }  
}