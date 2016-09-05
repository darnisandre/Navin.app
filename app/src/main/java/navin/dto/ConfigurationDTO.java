package navin.dto;

import java.io.Serializable;

public class ConfigurationDTO implements Serializable {
	private Long id;
	private String description;
	private LocationDTO location;
	
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
	public LocationDTO getLocation() {
		return location;
	}
	public void setLocation(LocationDTO location) {
		this.location = location;
	}
}
