package navin.util;

import android.content.Context;
import android.util.Log;

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

    private <T> T getForObject(String url, Class<T> responseType, Object... urlVariables){
        try {
            return restTemplate.getForObject(url, responseType, urlVariables);
        }catch (Exception e){
            Log.e("RestClient","Problema de conexão",e);
            return null;
        }
    }

    public List<RouteDTO> getRoutes(final int locationId){
        final String url = restUrl + "route/location/{id}";
        RouteDTO[] routes = getForObject(url, RouteDTO[].class,locationId);
        return Util.asList(routes);
    }
    public RouteDTO getRoute(final int id){
        final String url = restUrl + "route/{id}";
        RouteDTO route = getForObject(url, RouteDTO.class,id);
        return route;
    }
    public List<CategoryDTO> getCategories(final int locationId){
        final String url = restUrl + "category/location/{id}";
        CategoryDTO[] categories= getForObject(url, CategoryDTO[].class,locationId);
        return Arrays.asList(categories);
    }
    public BeaconMappingDTO getBeaconMapping(final int locationId){
        final String url = restUrl + "beacon/location/{id}";
        BeaconMappingDTO beaconMapping = getForObject(url, BeaconMappingDTO.class,locationId);
        return beaconMapping;
    }

    public List<LocationDTO> getLocations(final double lat, double longitude){
        final String url = restUrl + "location/{lat}/{long}";
        LocationDTO[] locations = getForObject(url, LocationDTO[].class,lat,longitude);
        return Util.asList(locations);
    }
    public LocationDTO getLocation(final Long id){
        final String url = restUrl + "location/{id}";
        LocationDTO location = getForObject(url, LocationDTO.class,id);
        return location;
    }
    public List<LocationDTO> getLocations(){
        final String url = restUrl + "location/all";
        LocationDTO[] locations = getForObject(url, LocationDTO[].class);
        return Util.asList(locations);
    }
}
