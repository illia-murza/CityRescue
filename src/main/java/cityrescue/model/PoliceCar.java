package cityrescue.model;

import cityrescue.enums.*;

// a police car unit that handles crime incidents
// resolves incidents in 3 ticks on scene

public class PoliceCar extends Unit {

    // creates a police car based at the given station location
    public PoliceCar(int id, int homeStationId, int x, int y) {
        super(id, UnitType.POLICE_CAR, homeStationId, x, y);
    }

    // police cars can only handle IncidentType.CRIME incidents
    @Override
    public boolean canHandle(IncidentType type) {
        return type == IncidentType.CRIME;
    }

    // police cars spend 3 ticks on scene before an incident is resolved
    @Override
    public int getTicksToResolve() {
        return 3;
    }
}