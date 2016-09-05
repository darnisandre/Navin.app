package navin.dto;

import java.io.Serializable;

public class GateDTO implements Serializable {
	private Long id;
	private String description;
	private BeaconDTO beaconA;
	private BeaconDTO beaconB;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

}
