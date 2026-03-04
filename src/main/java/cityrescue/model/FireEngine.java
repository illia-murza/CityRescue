package cityrescue.model;

import cityrescue.enums.*;

// a fire engine unit that handles fire emergencies
// resolves incidents in 4 ticks on scene (the slowest of all unit types)

public class FireEngine extends Unit {

    // creates a fire engine based at the given station location
    public FireEngine(int id, int homeStationId, int x, int y) {
        super(id, UnitType.FIRE_ENGINE, homeStationId, x, y);
    }

    // fire engines can only handle IncidentType.FIRE incidents
    @Override
    public boolean canHandle(IncidentType type) {
        return type == IncidentType.FIRE;
    }

    // fire engines spend 4 ticks on scene before an incident is resolved
    @Override
    public int getTicksToResolve() {
        return 4;
    }
}