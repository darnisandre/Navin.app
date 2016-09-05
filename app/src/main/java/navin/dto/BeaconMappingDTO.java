package navin.dto;

import java.io.Serializable;
import java.util.List;

public class BeaconMappingDTO implements Serializable {

	private List<BeaconDTO> beacons;
	private List<ConnectionDTO> connections;
	private List<GateDTO> gates;
	private LocationDTO location;
	public List<BeaconDTO> getBeacons() {
		return beacons;
	}

	public void setBeacons(List<BeaconDTO> beacons) {
		this.beacons = beacons;
	}

	public List<ConnectionDTO> getConnections() {
		return connections;
	}

	public void setConnections(List<ConnectionDTO> connections) {
		this.connections = connections;
	}

	public List<GateDTO> getGates() {
		return gates;
	}

	public void setGates(List<GateDTO> gates) {
		this.gates = gates;
	}

	public LocationDTO getLocation() {
		return location;
	}

	public void setLocation(LocationDTO location) {
		this.location = location;
	}


}
