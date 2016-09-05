package navin.dto;

import java.io.Serializable;

public class ConnectionDTO implements Serializable {
	private Long id;
	private BeaconDTO beaconA;
	private BeaconDTO beaconB;
	private Double distance;
	private Double direction;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public BeaconDTO getBeaconA() {
		return beaconA;
	}
	public void setBeaconA(BeaconDTO beaconA) {
		this.beaconA = beaconA;
	}
	public BeaconDTO getBeaconB() {
		return beaconB;
	}
	public void setBeaconB(BeaconDTO beaconB) {
		this.beaconB = beaconB;
	}
	public Double getDistance() {
		return distance;
	}
	public void setDistance(Double distance) {
		this.distance = distance;
	}
	public Double getDirection() {
		return direction;
	}
	public void setDirection(Double direction) {
		this.direction = direction;
	}
	
	
}
