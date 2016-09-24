package navin.tree;

import com.estimote.sdk.Beacon;

import java.util.ArrayList;
import java.util.List;

import navin.dto.BeaconDTO;

/**
 * Created by Guilherme on 11/09/2016.
 */
public class BeaconNode {
    private BeaconDTO beacon;
    List<BeaconRelation> beacons;


    public BeaconNode (BeaconDTO beacon){
        this.beacon = beacon;
        beacons = new ArrayList<BeaconRelation>();
    }

    public BeaconDTO getBeacon() {
        return beacon;
    }

    public void setBeacon(BeaconDTO beacon) {
        this.beacon = beacon;
    }

    public List<BeaconRelation> getBeacons() {
        return beacons;
    }

    public void setBeacons(List<BeaconRelation> beacons) {
        this.beacons = beacons;
    }

    public void addRelation(BeaconRelation relation){
        beacons.add(relation);
    }

}
