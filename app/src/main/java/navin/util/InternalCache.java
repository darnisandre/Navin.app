package navin.util;

import android.content.Context;
import android.location.Location;

import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import navin.dto.BeaconMappingDTO;
import navin.dto.LocationDTO;
import navin.dto.RouteDTO;

/**
 * Created by Guilherme on 04/09/2016.
 */
public class InternalCache {
    private static final String LOCATION_KEY = "locations";
    private final RestClient restClient;
    private final Context context;

    public InternalCache(String restUrl, Context context) {
        restClient = new RestClient(restUrl);
        this.context = context;
    }

    public List<RouteDTO> getRoutes(final int locationId) {
        return restClient.getRoutes(locationId);
    }

    public RouteDTO getRoute(final int id) {
        return restClient.getRoute(id);
    }

    public BeaconMappingDTO getBeaconMapping(final int locationId) {
        return restClient.getBeaconMapping(locationId);
    }

    public List<LocationDTO> getLocations(final double lat, double longitude) {
        List<LocationDTO> locations= restClient.getLocations(lat, longitude);
        HashMap<Long,LocationDTO> cacheLocations = getCacheLocations();
        for(LocationDTO l : locations){
            LocationDTO cacheLocation = cacheLocations.get(l.getId());
            if(cacheLocation!=null && l.getLastUpdated().after(cacheLocation.getLastUpdated())){
                //TODO remove cache
            }
            cacheLocations.put(l.getId(),l);
        }
        storeCacheLocations(cacheLocations);
        return locations;
    }

    private void storeCacheLocations(HashMap<Long, LocationDTO> cacheLocations) {
        InternalStorage.writeObject(context,LOCATION_KEY,cacheLocations);
    }

    private HashMap<Long,LocationDTO> getCacheLocations(){
        HashMap<Long,LocationDTO>  map = (HashMap<Long,LocationDTO>) InternalStorage.readObject(context,LOCATION_KEY);
        if(map==null){
            map = new HashMap<Long,LocationDTO>();
        }
        return map;
    }
}
