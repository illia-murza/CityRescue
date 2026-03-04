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

    



}