package navin.dto;

import java.io.Serializable;
import java.util.List;

public class CategoryDTO implements Serializable {
	private Long id;
	private String description;
	private String name;
	private List<BeaconDTO> beacons;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<BeaconDTO> getBeacons() {
		return beacons;
	}
	public void setBeacons(List<BeaconDTO> beacons) {
		this.beacons = beacons;
	}
}
