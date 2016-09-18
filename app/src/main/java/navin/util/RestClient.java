package navin.util;

import android.content.Context;

import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import navin.dto.BeaconMappingDTO;
import navin.dto.CategoryDTO;
import navin.dto.LocationDTO;
import navin.dto.RouteDTO;

/**
 * Created by Guilherme on 04/09/2016.
 */
public class RestClient {
    private final String restUrl;
    private final RestTemplate restTemplate;

    public RestClient(String restUrl) {
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new CustomGsonHttpMessageConverter());
        this.restUrl = restUrl;
    }
    public List<RouteDTO> getRoutes(final int locationId){
        final String url = restUrl + "route/location/{id}";
        RouteDTO[] routes = restTemplate.getForObject(url, RouteDTO[].class,locationId);
        return Arrays.asList(routes);
    }
    public RouteDTO getRoute(final int id){
        final String url = restUrl + "route/{id}";
        RouteDTO route = restTemplate.getForObject(url, RouteDTO.class,id);
        return route;
    }
    public List<CategoryDTO> getCategories(final int locationId){
        final String url = restUrl + "category/location/{id}";
        CategoryDTO[] categories= restTemplate.getForObject(url, CategoryDTO[].class,locationId);
        return Arrays.asList(categories);
    }
    public BeaconMappingDTO getBeaconMapping(final int locationId){
        final String url = restUrl + "beacon/location/{id}";
        BeaconMappingDTO beaconMapping = restTemplate.getForObject(url, BeaconMappingDTO.class,locationId);
        return beaconMapping;
    }

    public List<LocationDTO> getLocations(final double lat, double longitude){
        final String url = restUrl + "location/{lat}/{long}";
        LocationDTO[] locations = restTemplate.getForObject(url, LocationDTO[].class,lat,longitude);
        return Arrays.asList(locations);
    }
}
