package navin.tree;

import android.util.Log;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

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
    private class PathNode{
        public double value;
        public List<Long> path;
    }

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
                    dto.getDirection()));
            nodeB.addRelation(new BeaconRelation(nodeA, dto.getDistance(),
                    Util.getReverseDegree(dto.getDirection())));

            log += nodeA.getBeacon().getId() + " -- " + nodeB.getBeacon().getId() + " [label=" + dto.getDistance() + "];\n";

        }

        Log.i("BeaconTree", log);

    }


    public BeaconRelation getRelation(long beaconA, long beaconB){
        BeaconNode beacon = getNode(beaconA);
        if(beacon == null) return null;
        for(BeaconRelation r : beacon.getBeacons()){
            if(r.getBeacon().getBeacon().getId().equals(beaconB)){
                return r;
            }
        }
        return null;
    }

    public BeaconNode getNode(long beaconId) {
        return nodes.get(beaconId);
    }

    public List<BeaconNode> getRoute(List<Long> ids, Long startId) {
        long time = System.nanoTime();

        Map<Long,Integer> idToPos = new HashMap<Long,Integer>();
        Map<Integer,Long> posToId = new HashMap<Integer,Long>();

        int pos=0;
        for(BeaconNode n: nodes.values()){
            idToPos.put(n.getBeacon().getId(), pos);
            posToId.put(pos, n.getBeacon().getId());
            pos++;
        }

        double[][] distances = new double[nodes.size()][nodes.size()];
        Integer[][] next = new Integer[nodes.size()][nodes.size()];
        for(int i=0; i < nodes.size(); i++){
            for (int j=0;j<nodes.size();j++){
                distances[i][j] = Double.MAX_VALUE;
                if(i==j) distances[i][j] =0;
            }
        }

        for (ConnectionDTO dto : mapping.getConnections()) {
            int posA = idToPos.get(dto.getBeaconA().getId()), posB = idToPos.get(dto.getBeaconB().getId());
            distances[posA][posB] = dto.getDistance();
            distances[posB][posA] = dto.getDistance();
            next[posA][posB] = posB;
            next[posB][posA] = posA;
        }


        for(int k=0; k < nodes.size(); k++){
            for (int i=0;i<nodes.size();i++){
                for(int j=0;j<nodes.size();j++){
                    if( distances[i][j] > distances[i][k] + distances[k][j]){
                        distances[i][j] = distances[i][k] + distances[k][j];
                        next[i][j] = next[i][k];
                    }
                }
            }
        }

        Long atual = startId;
        List<Long> pass = new ArrayList<>(ids);
        List<Long> tspPath = new ArrayList<Long>(ids.size());
        pass.remove(atual);
        tspPath.add(atual); //Inicial

        while(!pass.isEmpty()){
            Long id = null;
            double maxDist = Double.MIN_VALUE;
            //Descobre qual o próximo beacon a ser adicionado na rota
            for(Long toPass: pass){
                //
                double dist = Double.MAX_VALUE;
                for(Long i : tspPath){
                    double thisDist = distances[idToPos.get(toPass)][idToPos.get(i)];
                    if(thisDist < dist){
                        dist = thisDist;
                    }
                }

                if(dist > maxDist){
                    maxDist = dist;
                    id = toPass;
                }
            }

            //Descobre a posição em que custa menos
            Double acrecimoMinimo = Double.MAX_VALUE;
            Integer acrecimoMinimoPos = null;
            for(int i=1; i< tspPath.size();i++){
                Long node1 = tspPath.get(i-1);
                Long node2 = tspPath.get(i);
                double acrescimoUsandoI = distances[idToPos.get(node1)][idToPos.get(id)] +
                        distances[idToPos.get(id)][idToPos.get(node2)]
                        - distances[idToPos.get(node1)][idToPos.get(node2)];
                if(acrecimoMinimo> acrescimoUsandoI){
                    acrecimoMinimo = acrescimoUsandoI;
                    acrecimoMinimoPos = i;
                }

            }
            if(acrecimoMinimo> distances[idToPos.get(tspPath.get(tspPath.size()-1))][idToPos.get(id)]){
                acrecimoMinimo = distances[idToPos.get(tspPath.get(tspPath.size()-1))][idToPos.get(id)];
                acrecimoMinimoPos = tspPath.size();
            }
            //Adiciona na rota na posição que custa menos
            if(acrecimoMinimoPos!=null){
                tspPath.add(acrecimoMinimoPos,id);
                pass.remove(id);
            }
        }


        List<BeaconNode> beaconsPath = new ArrayList<BeaconNode>();

        beaconsPath.add(getNode(tspPath.get(0)));
        for(int i=1; i < tspPath.size();i++){
            int u = idToPos.get(tspPath.get(i-1));
            int v = idToPos.get(tspPath.get(i));
            while(u!=v){
                u = next[u][v];
                beaconsPath.add(getNode(posToId.get(u)));
            }
        }

        String a = "";
        for(BeaconNode n: beaconsPath){
            a+= n.getBeacon().getId() + ",";
        }

        Log.i("TspHeuristic:Path", "Rota cientifica & " +  startId + " & " + a.replaceAll(",$","") + " & " + String.valueOf((System.nanoTime()-time)/1000000.0)+ "ms \\tabularnewline");

        return beaconsPath;
    }







}
