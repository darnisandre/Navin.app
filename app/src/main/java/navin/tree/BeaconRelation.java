package navin.tree;

import navin.dto.BeaconDTO;

/**
 * Created by Guilherme on 11/09/2016.
 */
public class BeaconRelation {
    private BeaconNode beacon;
    private double distance;
    private double degree;

    public BeaconRelation(BeaconNode beacon, double distance, double degree) {
        this.beacon = beacon;
        this.distance = distance;
        this.degree = degree;
    }

    public double getDegree() {
        return degree;
    }

    public void setDegree(double degree) {
        this.degree = degree;
    }

    public BeaconNode getBeacon() {
        return beacon;
    }

    public void setBeacon(BeaconNode beacon) {
        this.beacon = beacon;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
