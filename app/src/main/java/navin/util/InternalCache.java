package navin.util;

import android.content.Context;
import android.location.Location;

import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import navin.dto.BeaconMappingDTO;
import navin.dto.CategoryDTO;
import navin.dto.LocationDTO;
import navin.dto.RouteDTO;

/**
 * Created by Guilherme on 04/09/2016.
 */
public class InternalCache {
    private static final String LOCATION_KEY = "locations";
    private static final String ROUTES_KEY = "routes_%s";
    private static final String CATEGORIES_KEY = "categories_%s";
    private static final String ROUTE_KEY = "route_%s";
    private static final String BEACON_MAPPING_KEY = "beacon_mapping_%s";

    private final RestClient restClient;
    private final Context context;

    public InternalCache(String restUrl, Context context) {
        restClient = new RestClient(restUrl);
        this.context = context;
    }

    public List<RouteDTO> getRoutes(final int locationId) {
        String routesKey = String.format(ROUTES_KEY, locationId);
        List<RouteDTO> routes = (List<RouteDTO>) InternalStorage.readObject(context, routesKey);
        if(routes == null){
            routes = restClient.getRoutes(locationId);
            InternalStorage.writeObject(context, routesKey, routes);
        }
        return routes;
    }

    public void refreshRoutes(final int locationId) {
        String routesKey = String.format(ROUTES_KEY, locationId);
        List<RouteDTO> routes = null;
        if(routes == null){
            routes = restClient.getRoutes(locationId);
            InternalStorage.writeObject(context, routesKey, routes);
        }
    }

    public List<CategoryDTO> getCategories(final int locationId){
        String routeKey = String.format(CATEGORIES_KEY, locationId);
        List<CategoryDTO> categories = (List<CategoryDTO>) InternalStorage.readObject(context, routeKey);
        if(categories == null){
            categories = restClient.getCategories(locationId);
            InternalStorage.writeObject(context, routeKey, categories);
        }
        return categories;
    }

    public RouteDTO getRoute(final int id) {
        String routeKey = String.format(ROUTE_KEY, id);
        RouteDTO route = (RouteDTO) InternalStorage.readObject(context, routeKey);
        if(route == null){
            route = restClient.getRoute(id);
            InternalStorage.writeObject(context, routeKey, route);
        }
        return route;
    }

    public BeaconMappingDTO getBeaconMapping(final int locationId) {
        String beaconMappingKey = String.format(BEACON_MAPPING_KEY, locationId);
        BeaconMappingDTO mapping = (BeaconMappingDTO) InternalStorage.readObject(context, beaconMappingKey);
        if(mapping == null){
            mapping = restClient.getBeaconMapping(locationId);
            InternalStorage.writeObject(context, beaconMappingKey, mapping);
        }
        return mapping;
    }

    public List<LocationDTO> getLocations(final double lat, double longitude) {
        List<LocationDTO> locations= restClient.getLocations(lat, longitude);
        if(locations!=null){
            HashMap<Long,LocationDTO> cacheLocations = getCacheLocations();
            for(LocationDTO l : locations){
                LocationDTO cacheLocation = cacheLocations.get(l.getId());
                if(cacheLocation != null && l.getLastUpdated().after(cacheLocation.getLastUpdated())){
                    cleanCacheLocation(l.getId());
                }
                cacheLocations.put(l.getId(),l);
            }
            storeCacheLocations(cacheLocations);
        }
        return locations;
    }
    public LocationDTO getLocation(final Long id) {
        HashMap<Long,LocationDTO> cacheLocations = getCacheLocations();
        if(cacheLocations.get(id)!=null){
            return cacheLocations.get(id);
        }
        LocationDTO location= restClient.getLocation(id);
        return location;
    }

    public List<LocationDTO> getLocations() {
        List<LocationDTO> locations= restClient.getLocations();
        if(locations!=null){
            HashMap<Long,LocationDTO> cacheLocations = getCacheLocations();
            for(LocationDTO l : locations){
                LocationDTO cacheLocation = cacheLocations.get(l.getId());
                if(cacheLocation != null && l.getLastUpdated().after(cacheLocation.getLastUpdated())){
                    cleanCacheLocation(l.getId());
                }
                cacheLocations.put(l.getId(),l);
            }
            storeCacheLocations(cacheLocations);
        }
        return locations;
    }

    private void cleanCacheLocation(Long locationId) {
        InternalStorage.deleteObject(context,String.format(BEACON_MAPPING_KEY, locationId));
        List<RouteDTO> routes = (List<RouteDTO>) InternalStorage.readObject(context, String.format(ROUTES_KEY, locationId));
        if(routes != null){
            for(RouteDTO route : routes){
                InternalStorage.deleteObject(context, String.format(ROUTE_KEY, route.getId()));
            }
        }
        InternalStorage.deleteObject(context, String.format(ROUTES_KEY, locationId));
        InternalStorage.deleteObject(context, String.format(CATEGORIES_KEY, locationId));
    }

    private void storeCacheLocations(HashMap<Long, LocationDTO> cacheLocations) {
        InternalStorage.writeObject(context, LOCATION_KEY, cacheLocations);
    }

    private HashMap<Long, LocationDTO> getCacheLocations(){
        HashMap<Long, LocationDTO>  map = (HashMap<Long, LocationDTO>) InternalStorage.readObject(context, LOCATION_KEY);
        if(map==null){
            map = new HashMap<Long, LocationDTO>();
        }
        return map;
    }
}
