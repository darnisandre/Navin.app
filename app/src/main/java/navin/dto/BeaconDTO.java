package navin.dto;

import java.io.Serializable;
import java.util.List;

public class BeaconDTO implements Serializable {
	private Long id;
	private String description;
	private String uuid;
	private Long major;
	private Long minor;
	private BeaconTypeDTO type;
	private ConfigurationDTO configuration;
	private List<RouteDTO> routes;
	private List<CategoryDTO> categories;

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

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Long getMajor() {
		return major;
	}

	public void setMajor(Long major) {
		this.major = major;
	}

	public Long getMinor() {
		return minor;
	}

	public void setMinor(Long minor) {
		this.minor = minor;
	}

	public BeaconTypeDTO getType() {
		return type;
	}

	public void setType(BeaconTypeDTO type) {
		this.type = type;
	}

	public ConfigurationDTO getConfiguration() {
		return configuration;
	}

	public void setConfiguration(ConfigurationDTO configuration) {
		this.configuration = configuration;
	}

	public List<RouteDTO> getRoutes() {
		return routes;
	}

	public void setRoutes(List<RouteDTO> routes) {
		this.routes = routes;
	}

	public List<CategoryDTO> getCategories() {
		return categories;
	}

	public void setCategories(List<CategoryDTO> categories) {
		this.categories = categories;
	}

}
