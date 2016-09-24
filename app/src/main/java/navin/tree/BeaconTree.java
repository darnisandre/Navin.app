package navin.tree;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
    private BeaconMappingDTO mapping;

    public BeaconTree(BeaconMappingDTO mapping) {
        this.mapping = mapping;
        nodes = new HashMap<Long, BeaconNode>();
        for (BeaconDTO dto : mapping.getBeacons()) {
            nodes.put(dto.getId(), new BeaconNode(dto));
        }
        String log="";
        for (ConnectionDTO dto : mapping.getConnections()) {
            BeaconNode nodeA = nodes.get(dto.getBeaconA().getId());
            BeaconNode nodeB = nodes.get(dto.getBeaconB().getId());
            nodeA.addRelation(new BeaconRelation(nodeB, dto.getDistance(),
                    dto.getDistance()));
            nodeB.addRelation(new BeaconRelation(nodeA, dto.getDistance(),
                    Util.getReverseDegree(dto.getDistance())));

            log += nodeA.getBeacon().getId() + " -- " + nodeB.getBeacon().getId() + " [label=" + dto.getDistance() + "];\n";

        }

        Log.i("BeaconTree", log);

    }

    public BeaconNode getNode(long beaconId) {
        return nodes.get(beaconId);
    }

    public List<BeaconNode> getRoute(List<Long> ids, Long startId){
        long time = System.currentTimeMillis();
        /*floyd
        1 let dist be a |V| × |V| array of minimum distances initialized to 8 (infinity)
                2 for each vertex v
        3    dist[v][v] ? 0
        4 for each edge (u,v)
        5    dist[u][v] ? w(u,v)  // the weight of the edge (u,v)
        6 for k from 1 to |V|
                7    for i from 1 to |V|
                8       for j from 1 to |V|
                9          if dist[i][j] > dist[i][k] + dist[k][j]
        10             dist[i][j] ? dist[i][k] + dist[k][j]
        11         end if*/


        Map<Long,Integer> idToPos = new HashMap<Long,Integer>();
        Map<Integer,Long> posToId = new HashMap<Integer,Long>();

        int pos=0;
        for(BeaconNode n: nodes.values()){
            idToPos.put(n.getBeacon().getId(), pos);
            posToId.put(pos, n.getBeacon().getId());
            pos++;
        }

        double[][] distances = new double[nodes.size()][nodes.size()];
        String[][] path = new String[nodes.size()][nodes.size()];
        for(int i=0; i < nodes.size(); i++){
            for (int j=0;j<nodes.size();j++){
                distances[i][j] = Double.MAX_VALUE;
                path[i][j] = "";
                if(i==j) distances[i][j] =0;
            }
        }

        for (ConnectionDTO dto : mapping.getConnections()) {
            distances[idToPos.get(dto.getBeaconA().getId())][idToPos.get(dto.getBeaconB().getId())] = dto.getDistance();
            distances[idToPos.get(dto.getBeaconB().getId())][idToPos.get(dto.getBeaconA().getId())] = dto.getDistance();
        }

        for(int i=0; i < nodes.size(); i++){
            for (int j=0;j<nodes.size();j++){
                for(int k=0;k<nodes.size();k++){
                    if( distances[i][j] > distances[i][k] + distances[k][j]){
                        distances[i][j] = distances[i][k] + distances[k][j];
                        path[i][j] = path[i][k] + posToId.get(k) + "," + path[k][j];
                    }
                }
            }
        }

        Aux a = pass(ids,startId,distances,idToPos);

        for(Long l: a.path){
            Log.i("Pass",l.toString());
        }


        Log.i("BeaconTree","Tempo:" + (System.currentTimeMillis()-time));

        for(BeaconNode n: nodes.values()){
            for(BeaconNode node: nodes.values()){
                Log.i("BeaconTree","De "+ n.getBeacon().getId() + " Ate " + node.getBeacon().getId() + ": " + distances[idToPos.get(n.getBeacon().getId())][idToPos.get(node.getBeacon().getId())]);
                Log.i("BeaconTree","Path de "+ n.getBeacon().getId() + " Ate " + node.getBeacon().getId() + ": " + n.getBeacon().getId() + "," + path[idToPos.get(n.getBeacon().getId())][idToPos.get(node.getBeacon().getId())] + node.getBeacon().getId()) ;
            }
        }


        return null;
    }

    private class Aux{
        public double value;
        public List<Long> path;
    }

    private Aux pass(List<Long> needPass, Long start, double[][] distances, Map<Long,Integer> idToPos){
        if(needPass.isEmpty()) {
            Aux a = new Aux();
            a.value=0;
            a.path = new ArrayList<>();
            return a;
        }
        Aux min = null;
        for(Long l : needPass){
            List<Long> list = new ArrayList<>(needPass);
            list.remove(l);
            Aux val = pass(list,l,distances,idToPos);
            if(min ==null || val.value + distances[idToPos.get(start)][idToPos.get(l)] < min.value ){
                min = val;
                //Soma o atual
                min.value = val.value + distances[idToPos.get(start)][idToPos.get(l)];
                min.path.add(l);
            }
        }
        return min;
    }

}
