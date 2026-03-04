package cityrescue.model;

import cityrescue.enums.*;

// an ambulance unit that handles medical emergencies
// resolves incidents in 2 ticks on scene (the fastest of all unit types)

public class Ambulance extends Unit {

    // creates an ambulance based at the given station location
    public Ambulance(int id, int homeStationId, int x, int y) {
        super(id, UnitType.AMBULANCE, homeStationId, x, y);
    }

    // ambulances can only handle IncidentType.MEDICAL incidents
    @Override
    public boolean canHandle(IncidentType type) {
        return type == IncidentType.MEDICAL;
    }

    // ambulances spend 2 ticks on scene before an incident is resolved
    @Override
    public int getTicksToResolve() {
        return 2;
    }
}