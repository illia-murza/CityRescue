package cityrescue;

import cityrescue.enums.*;
import cityrescue.exceptions.*;
import cityrescue.model.*;

/**
 * CityRescueImpl (Starter)
 *
 * Your task is to implement the full specification.
 * You may add additional classes in any package(s) you like.
 */
public class CityRescueImpl implements CityRescue {

    // TODO: add fields (map, arrays for stations/units/incidents, counters, tick, etc.)
    
	// storage limits
	private static final int MAX_STATIONS  = 20;
    private static final int MAX_UNITS     = 50;
    private static final int MAX_INCIDENTS = 200;
	// default station capacity assigned when a station is first created
	private static final int DEFAULT_STATION_CAPACITY = 5;

	private CityMap map;
    private int tick;

    private Station[] stations;
    private int stationCount;
    private int nextStationId;

    private Unit[] units;
    private int unitCount;
    private int nextUnitId;

    private Incident[] incidents;
    private int incidentCount;
    private int nextIncidentId;

	// direction vectors: N, E, S, W
	// N = y decreases, E = x increases, S = y increases, W = x decreases
    private static final int[] DX = {  0, 1, 0, -1 };
    private static final int[] DY = { -1, 0, 1,  0 };

	// 1. Grid

	// resets all state: clears stations, units, incidents, obstacles, sets tick to 0
	// throws InvalidGridException if width or height is not positive

    @Override
    public void initialise(int width, int height) throws InvalidGridException {
        // TODO: implement
        if (width <= 0 || height <= 0) {
            throw new InvalidGridException(
                "Grid dimensions must be positive, got " + width + "x" + height + ".");
        }
        map = new CityMap(width, height);
        tick = 0;
        stations = new Station[MAX_STATIONS];
        stationCount = 0;
        nextStationId = 1;
        units = new Unit[MAX_UNITS];
        unitCount = 0;
        nextUnitId = 1;
        incidents = new Incident[MAX_INCIDENTS];
        incidentCount = 0;
        nextIncidentId = 1;
	}

    @Override
    public int[] getGridSize() {
        // TODO: implement
        return new int[]{ map.getWidth(), map.getHeight() };
	}
	
    @Override
    public void addObstacle(int x, int y) throws InvalidLocationException {
        // TODO: implement
        map.addObstacle(x, y);
	}

    @Override
    public void removeObstacle(int x, int y) throws InvalidLocationException {
        // TODO: implement
        map.removeObstacle(x, y);
	}

    @Override
    public int addStation(String name, int x, int y) throws InvalidNameException, InvalidLocationException {
        // TODO: implement
		if (name == null || name.trim().isEmpty()) {
            throw new InvalidNameException("Station name must not be blank.");
        }
        if (!map.inBounds(x, y)) {
            throw new InvalidLocationException("Location (" + x + "," + y + ") is out of bounds.");
        }
        if (map.isBlocked(x, y)) {
            throw new InvalidLocationException("Location (" + x + "," + y + ") is blocked.");
        }
        if (stationCount >= MAX_STATIONS) {
            throw new CapacityExceededException(
                "Maximum number of stations (" + MAX_STATIONS + ") reached.");
        }
        int id = nextStationId++;
        stations[stationCount++] = new Station(id, name, x, y, DEFAULT_STATION_CAPACITY);
        return id;
	}
        

    @Override
    public void removeStation(int stationId) throws IDNotRecognisedException, IllegalStateException {
        // TODO: implement
		findStation(stationId);
        int cnt = countUnitsAtStation(stationId);
        if (cnt > 0) {
            throw new IllegalStateException(
                "Station " + stationId + " still has " + cnt + " unit(s); cannot remove.");
        }
        removeStationFromArray(stationId);
	}
        

    @Override
    public void setStationCapacity(int stationId, int maxUnits) throws IDNotRecognisedException, InvalidCapacityException {
        // TODO: implement
        Station s = findStation(stationId);
        if (maxUnits <= 0) {
            throw new InvalidCapacityException("Capacity must be positive.");
        }
        int current = countUnitsAtStation(stationId);
        if (maxUnits < current) {
            throw new InvalidCapacityException(
                "Cannot reduce capacity to " + maxUnits
                + "; station already has " + current + " unit(s).");
        }
        s.setMaxUnits(maxUnits);
    }


	@Override
    public int[] getStationIds() {
        int[] ids = new int[stationCount];
        for (int i = 0; i < stationCount; i++) ids[i] = stations[i].getId();
        sortAscending(ids);
        return ids;
    }

		
    public int addUnit(int stationId, UnitType type) throws IDNotRecognisedException, InvalidUnitException, IllegalStateException {
        // TODO: implement
        Station s = findStation(stationId);
        if (type == null) {
            throw new InvalidUnitException("Unit type must not be null.");
        }
        int current = countUnitsAtStation(stationId);
        if (current >= s.getMaxUnits()) {
            throw new IllegalStateException(
                "Station " + stationId + " is at full capacity (" + s.getMaxUnits() + ").");
        }
        if (unitCount >= MAX_UNITS) {
            throw new CapacityExceededException("Maximum number of units (" + MAX_UNITS + ") reached.");
        }
        int id = nextUnitId++;
        units[unitCount++] = createUnit(id, type, stationId, s.getX(), s.getY());
        return id;
    }


    @Override
    public void decommissionUnit(int unitId) throws IDNotRecognisedException, IllegalStateException {
        // TODO: implement
		Unit u = findUnit(unitId);
        UnitStatus st = u.getStatus();
        if (st == UnitStatus.EN_ROUTE || st == UnitStatus.AT_SCENE) {
            throw new IllegalStateException(
                "Unit " + unitId + " cannot be decommissioned while " + st + ".");
        }
        removeUnitFromArray(unitId);
    }


    @Override
    public void transferUnit(int unitId, int newStationId) throws IDNotRecognisedException, IllegalStateException {
        // TODO: implement
		Unit u    = findUnit(unitId);
        Station d = findStation(newStationId);
        if (u.getStatus() != UnitStatus.IDLE) {
            throw new IllegalStateException(
                "Unit " + unitId + " must be IDLE to transfer; currently " + u.getStatus() + ".");
        }
        int current = countUnitsAtStation(newStationId);
        if (current >= d.getMaxUnits()) {
            throw new IllegalStateException(
                "Destination station " + newStationId + " is at full capacity.");
        }
        u.setHomeStationId(newStationId);
        u.setX(d.getX());
        u.setY(d.getY());
    }


    @Override
    public void setUnitOutOfService(int unitId, boolean outOfService) throws IDNotRecognisedException, IllegalStateException {
        // TODO: implement
		Unit u = findUnit(unitId);
        if (outOfService) {
            if (u.getStatus() != UnitStatus.IDLE) {
                throw new IllegalStateException(
                    "Unit " + unitId + " must be IDLE to set OUT_OF_SERVICE; currently "
                    + u.getStatus() + ".");
            }
            u.setStatus(UnitStatus.OUT_OF_SERVICE);
        } else {
            if (u.getStatus() != UnitStatus.OUT_OF_SERVICE) {
                throw new IllegalStateException(
                    "Unit " + unitId + " is not OUT_OF_SERVICE; currently " + u.getStatus() + ".");
            }
            u.setStatus(UnitStatus.IDLE);
        }
    }


    @Override
    public int[] getUnitIds() {
        // TODO: implement
		int[] ids = new int[unitCount];
        for (int i = 0; i < unitCount; i++) ids[i] = units[i].getId();
        sortAscending(ids);
        return ids;
    }

    @Override
    public String viewUnit(int unitId) throws IDNotRecognisedException {
        // TODO: implement
		return findUnit(unitId).format();
	}


    @Override
    public int reportIncident(IncidentType type, int severity, int x, int y) throws InvalidSeverityException, InvalidLocationException {
        // TODO: implement
		if (severity < 1 || severity > 5) {
            throw new InvalidSeverityException("Severity must be 1-5; got " + severity + ".");
        }
        if (!map.inBounds(x, y)) {
            throw new InvalidLocationException("Location (" + x + "," + y + ") is out of bounds.");
        }
        if (map.isBlocked(x, y)) {
            throw new InvalidLocationException("Location (" + x + "," + y + ") is blocked.");
        }
        if (incidentCount >= MAX_INCIDENTS) {
            throw new CapacityExceededException(
                "Maximum number of incidents (" + MAX_INCIDENTS + ") reached.");
        }
        int id = nextIncidentId++;
        incidents[incidentCount++] = new Incident(id, type, severity, x, y);
        return id;
    }	


    @Override
    public void cancelIncident(int incidentId) throws IDNotRecognisedException, IllegalStateException {
        // TODO: implement
		Incident inc = findIncident(incidentId);
        IncidentStatus st = inc.getStatus();
        if (st != IncidentStatus.REPORTED && st != IncidentStatus.DISPATCHED) {
            throw new IllegalStateException(
                "Incident " + incidentId + " cannot be cancelled; status is " + st + ".");
        }
        if (st == IncidentStatus.DISPATCHED) {
            Unit u = findUnitById(inc.getAssignedUnitId());
            if (u != null) {
                u.setStatus(UnitStatus.IDLE);
                u.setAssignedIncidentId(-1);
                u.setWorkTicksRemaining(0);
            }
        }
        inc.setStatus(IncidentStatus.CANCELLED);
        inc.setAssignedUnitId(-1);
    }
        

    @Override
    public void escalateIncident(int incidentId, int newSeverity) throws IDNotRecognisedException, InvalidSeverityException, IllegalStateException {
        // TODO: implement
        incident incident = incidents.get(incidentId);
		if (incident == null) {
			throw new IDNotRecognisedException("ID not found");
				}
		if (newSeverity < 1 || newSeverity > 5) {
			throw new InvalidSeverityException("Severity must be between 1 and 5");
		}
		
		if (newSeverity <= incident.severity) {
        	throw new IllegalStateException("New severity must be higher than current severity");
		}
		
		incident.severity = newSeverity;
		incidents.put(incidentId, incident);
	}		
		

    @Override
    public int[] getIncidentIds() {
        // TODO: implement
    	int[] ids = new int[incidentCount];
        for (int i = 0; i < incidentCount; i++) ids[i] = incidents[i].getId();
        sortAscending(ids);
        return ids;
    }


    @Override
    public String viewIncident(int incidentId) throws IDNotRecognisedException {
        // TODO: implement
		return findIncident(incidentId).format();
	}
        

    @Override
    public void dispatch() {
        // TODO: implement
		for (Incident incident : incidents.values()) {
			if (assignments.containsKey(incident.id)) {  //checks if the incident has already been assigned 
            	continue;
			}
			for (Unit unit : units.values()) {
					if (!assignments.containsValue(unit.id)) { //checks if unit is available
						assignments.put(incident.id, unit.id); //assigns unit to the incident
						break;
					}
			}
		}
	}


    @Override
    public void tick() {
        // TODO: implement
        tick++;

        int[] sortedUnitIds = getUnitIds();

        // Step 1 — move EN_ROUTE units
        for (int uid : sortedUnitIds) {
            Unit u = findUnitById(uid);
            if (u == null || u.getStatus() != UnitStatus.EN_ROUTE) continue;
            Incident inc = findIncidentById(u.getAssignedIncidentId());
            if (inc == null) continue;
            moveUnit(u, inc.getX(), inc.getY());
        }

        // Step 2 — mark arrivals
        for (int uid : sortedUnitIds) {
            Unit u = findUnitById(uid);
            if (u == null || u.getStatus() != UnitStatus.EN_ROUTE) continue;
            Incident inc = findIncidentById(u.getAssignedIncidentId());
            if (inc == null) continue;
            if (u.getX() == inc.getX() && u.getY() == inc.getY()) {
                u.setStatus(UnitStatus.AT_SCENE);
                u.setWorkTicksRemaining(u.getTicksToResolve());
                inc.setStatus(IncidentStatus.IN_PROGRESS);
            }
        }

        // Step 3 — process on-scene work
        for (int uid : sortedUnitIds) {
            Unit u = findUnitById(uid);
            if (u == null || u.getStatus() != UnitStatus.AT_SCENE) continue;
            if (u.getWorkTicksRemaining() > 0) {
                u.setWorkTicksRemaining(u.getWorkTicksRemaining() - 1);
            }
        }

        // Step 4 — resolve completed incidents (ascending incidentId)
        int[] sortedIncIds = getIncidentIds();
        for (int incId : sortedIncIds) {
            Incident inc = findIncidentById(incId);
            if (inc == null || inc.getStatus() != IncidentStatus.IN_PROGRESS) continue;
            Unit u = findUnitById(inc.getAssignedUnitId());
            if (u != null && u.getWorkTicksRemaining() == 0) {
                inc.setStatus(IncidentStatus.RESOLVED);
                u.setStatus(UnitStatus.IDLE);
                u.setAssignedIncidentId(-1);
            }
        }
    }

    @Override
    public String getStatus() {
        // TODO: implement
        StringBuilder sb = new StringBuilder();
        sb.append("TICK=").append(tick).append("\n");
        sb.append("STATIONS=").append(stationCount)
          .append(" UNITS=").append(unitCount)
          .append(" INCIDENTS=").append(incidentCount)
          .append(" OBSTACLES=").append(map.countObstacles())
          .append("\n");

        sb.append("INCIDENTS\n");
        for (int id : getIncidentIds()) {
            Incident inc = findIncidentById(id);
            if (inc != null) sb.append(inc.format()).append("\n");
        }

        sb.append("UNITS\n");
        for (int id : getUnitIds()) {
            Unit u = findUnitById(id);
            if (u != null) sb.append(u.format()).append("\n");
        }

        return sb.toString();
    }
}
