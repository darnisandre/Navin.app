package navin.tree;

import java.util.HashMap;
import java.util.Map;

import navin.dto.BeaconDTO;
import navin.dto.BeaconMappingDTO;
import navin.dto.ConnectionDTO;
import navin.util.Util;

/**
 * Created by Guilherme on 11/09/2016.
 */
public class BeaconTree {
    private Map<Long, BeaconNode> nodes;

    public BeaconTree(BeaconMappingDTO mapping) {
        nodes = new HashMap<Long, BeaconNode>();
        for (BeaconDTO dto : mapping.getBeacons()) {
            nodes.put(dto.getId(), new BeaconNode(dto));
        }
        for (ConnectionDTO dto : mapping.getConnections()) {
            BeaconNode nodeA = nodes.get(dto.getBeaconA());
            BeaconNode nodeB = nodes.get(dto.getBeaconB());
            nodeA.addRelation(new BeaconRelation(nodeB, dto.getDistance(),
                    dto.getDistance()));
            nodeB.addRelation(new BeaconRelation(nodeA, dto.getDistance(),
                    Util.getReverseDegree(dto.getDistance())));
        }
    }

    public BeaconNode getNode(long beaconId) {
        return nodes.get(beaconId);
    }



}
