package com.clewi.javachess.util;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Bresenham {
    
    public static List<Point> findLine(Point[][] grid, int x0, int y0, int x1, int y1) {
        List<Point> line = new ArrayList<>();
        
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        
        int err = dx - dy;
        int e2;
        
        while (true) {
            // Add current point to the line
            if (x0 >= 0 && x0 < grid.length && y0 >= 0 && y0 < grid[0].length) {
                line.add(grid[x0][y0]);
            }
            
            // Check if we've reached the end point
            if (x0 == x1 && y0 == y1) {
                break;
            }
            
            e2 = 2 * err;
            if (e2 > -dy) {
                err = err - dy;
                x0 = x0 + sx;
            }
            
            if (e2 < dx) {
                err = err + dx;
                y0 = y0 + sy;
            }
        }
        
        return line;
    }
}
