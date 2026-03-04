package cityrescue.model;

// represents an emergency station on the city map
// stations can hold up to maxUnits vehicles
public class Station {

    private final int id;
    private String name;
    private final int x;
    private final int y;
    private int maxUnits;

    // creates a new station
    public Station(int id, String name, int x, int y, int maxUnits) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.maxUnits = maxUnits;
    }

    // return the station's unique ID
    public int getId() { return id; }

    // return the station name
    public String getName() { return name; }

    // return the grid x-coordinate
    public int getX() { return x; }

    // return the grid y-coordinate
    public int getY() { return y; }

    // return the maximum number of units allowed
    public int getMaxUnits() { return maxUnits; }

    // updates the station capacity
    public void setMaxUnits(int maxUnits) { this.maxUnits = maxUnits; }
}