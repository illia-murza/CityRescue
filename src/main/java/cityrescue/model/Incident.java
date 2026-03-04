package cityrescue.model;

import cityrescue.enums.*;

// represents an emergency incident in the city
// tracks the incident's location, severity, lifecycle status and assigned unit
public class Incident {
    
    private final int id;
    private final IncidentType type;
    private int severity;
    private final int x;
    private final int y;
    private IncidentStatus status;
    // ID of the unit assigned to this incident, or -1 if none
    private int assignedUnitId;

    // creates a new incident in reported status
    public Incident(int id, IncidentType type, int severity, int x, int y) {
        this.id = id;
        this.type = type;
        this.severity = severity;
        this.x = x;
        this.y = y;
        this.status = IncidentStatus.REPORTED;
        this.assignedUnitId = -1;
    }

    // return the incident's unique ID
    public int getId() { return id; }

    // return the incident type
    public IncidentType getType() { return type; }

    // return the curent severity
    public int getSeverity() { return severity; }

    // sets the severity level
    public void setSeverity(int severity) { this.severity = severity; }

    // return the grid x-coordinate
    public int getX() { return x; }

    // return the grid y-coordinate
    public int getY() { return y; }

    // return the current lifecycle status
    public IncidentStatus getStatus() { return status; }

    // updates the lifecycle status
    public void setStatus(IncidentStatus status) { this.status = status; }

    // return the assigned unit's ID, or -1 if unassigned
    public int getAssignedUnitId() { return assignedUnitId; }

    // sets the assigned unit ID. Use -1 to clear the assignment
    public void setAssignedUnitId(int assignedUnitId) { this.assignedUnitId = assignedUnitId; }

    //produces the canonical display string for this incident
    //return incident description
    public String format() {
        String unitPart = (assignedUnitId == -1) ? "-" : String.valueOf(assignedUnitId);
        return String.format("I#%d TYPE=%s SEV=%d LOC=(%d,%d) STATUS=%s UNIT=%s",
                id, type, severity, x, y, status, unitPart);
    }
}
