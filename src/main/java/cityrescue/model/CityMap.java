package cityrescue.model;

import cityrescue.exceptions.*;

// represents the 2d grid city map

public class CityMap {
    private int width;
    private int height;
    private boolean[][] blocked;

    // initialises the map with the given dimensions
    public CityMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.blocked = new boolean[width][height];
    }

    // return the grid width
    public int getWidth() { return width; }

    // return the grid height
    public int getHeight() { return height; }

    // returns true iff (x,y) is within grid bounds
    public boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    // returns true iff (x,y) is in bounds and not blocked
    public boolean isPassable(int x, int y) {
        return inBounds(x, y) && !blocked[x][y];
    }

    // returns true iff (x,y) is blocked
    public boolean isBlocked(int x, int y) {
        return inBounds(x, y) && blocked[x][y];
    }

    // marks the cell (x,y) as blocked
    public void addObstacle(int x, int y) throws InvalidLocationException {
        if (!inBounds(x, y)) throw new InvalidLocationException("Location (" + x + "," + y + ") is out of bounds.");
        blocked[x][y] = true;
    }

    //marks the cell (x,y) as unblocked
    public void removeObstacle(int x, int y) throws InvalidLocationException {
        if (!inBounds(x, y)) throw new InvalidLocationException("Location (" + x + "," + y + ") is out of bounds.");
        blocked[x][y] = false;
    }

    // counts the total number of obstacle cells
    public int countObstacles() {
        int count = 0;
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                if (blocked[x][y]) count++;
        return count;
    }

    // Manhattan distance between two points
    public static int manhattan(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }
}