package cityrescue.model;

import cityrescue.enums.*;

// abstract base class for all emergency vehicles
// subclasses define which incident types they can handle and how long resolution takes

public abstract class Unit {

    private final int id;
    private final UnitType type;
    private int homeStationId;
    private int x;
    private int y;
    private UnitStatus status;
    // ID of the incident this unit is handling, or -1 if none
    private int assignedIncidentId;

    // remaining ticks of on-scene work. Decremented each tick while AT_SCENE
    // when it reaches 0 the incident is resolved

    private int workTicksRemaining;

    // creates a new unit based at the given station location
    protected Unit(int id, UnitType type, int homeStationId, int x, int y) {
        this.id = id;
        this.type = type;
        this.homeStationId = homeStationId;
        this.x = x;
        this.y = y;
        this.status = UnitStatus.IDLE;
        this.assignedIncidentId = -1;
        this.workTicksRemaining = 0;
    }

    // return true iff this unit can respond to that incident
    public abstract boolean canHandle(IncidentType type);

    // returns the number of ticks this unit needs to resolve an incident once on scene
    public abstract int getTicksToResolve();

    // return the unit's unique ID
    public int getId() { return id; }

    // return the unit type enum
    public UnitType getType() { return type; }

    // return the home station ID
    public int getHomeStationId() { return homeStationId; }

    // updates the home station ID (used on transfer)
    public void setHomeStationId(int homeStationId) { this.homeStationId = homeStationId; }

    // return current x grid position
    public int getX() { return x; }

    // sets the x grid position
    public void setX(int x) { this.x = x; }

    // return current y grid position
    public int getY() { return y; }

    // sets the y grid position
    public void setY(int y) { this.y = y; }

    // return current operational status
    public UnitStatus getStatus() { return status; }

    //updates the operational status
    public void setStatus(UnitStatus status) { this.status = status; }

    // return the assigned incident ID, or -1 if unassigned
    public int getAssignedIncidentId() { return assignedIncidentId; }

    // sets the assigned incident ID. Use -1 to clear
    public void setAssignedIncidentId(int assignedIncidentId) { this.assignedIncidentId = assignedIncidentId; }

    // return remaining on-scene work ticks
    public int getWorkTicksRemaining() { return workTicksRemaining; }

    // sets the remaining on-scene work ticks
    public void setWorkTicksRemaining(int workTicksRemaining) { this.workTicksRemaining = workTicksRemaining; }

    // return unit description
    public String format() {
        String incidentPart = (assignedIncidentId == -1) ? "-" : String.valueOf(assignedIncidentId);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("U#%d TYPE=%s HOME=%d LOC=(%d,%d) STATUS=%s INCIDENT=%s",
                id, type, homeStationId, x, y, status, incidentPart));
        if (status == UnitStatus.AT_SCENE) {
            sb.append(" WORK=").append(workTicksRemaining);
        }
        return sb.toString();
    }
}