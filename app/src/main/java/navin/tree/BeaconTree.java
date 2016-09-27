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


    public List<BeaconNode> getRouteTspHeuristic(List<Long> ids, Long startId) {
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


        for(int k=0; k < nodes.size(); k++){
            for (int i=0;i<nodes.size();i++){
                for(int j=0;j<nodes.size();j++){
                    if( distances[i][j] > distances[i][k] + distances[k][j]){
                        distances[i][j] = distances[i][k] + distances[k][j];
                        path[i][j] = path[i][k] + posToId.get(k) + "," + path[k][j];
                    }
                }
            }
        }

        Long atual = startId;
        List<Long> pass = new ArrayList<>(ids);
        pass.add(atual);
        List<Long> tspPath = new ArrayList<Long>(ids.size());
        while(!pass.isEmpty()){
            Long next = null;
            Double min = Double.MAX_VALUE;
            for(Long toPass: pass){
                Double costToPass = distances[idToPos.get(atual)][idToPos.get(toPass)];
                if(min > costToPass){
                    min = costToPass;
                    next = toPass;
                }
            }
            pass.remove(next);
            tspPath.add(next);
            atual = next;
        }

        String fullPath=tspPath.get(0).toString() + ",";
        for(int i=1; i < tspPath.size();i++){
            fullPath += path[idToPos.get(tspPath.get(i-1))][idToPos.get(tspPath.get(i))] + tspPath.get(i) + ",";
        }

        Log.i("TspHeuristic:FullPath", fullPath);
        Log.i("TspHeuristic:Tempo",String.valueOf(System.nanoTime()-time));

        List<BeaconNode> beaconsPath = new ArrayList<BeaconNode>();
        for(String s: fullPath.split(",")){
            beaconsPath.add(getNode(new Long(s)));
        }
        return beaconsPath;
    }

    public List<BeaconNode> getRoute(List<Long> ids, Long startId){
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


        for(int k=0; k < nodes.size(); k++){
            for (int i=0;i<nodes.size();i++){
                for(int j=0;j<nodes.size();j++){
                    if( distances[i][j] > distances[i][k] + distances[k][j]){
                        distances[i][j] = distances[i][k] + distances[k][j];
                        path[i][j] = path[i][k] + posToId.get(k) + "," + path[k][j];
                    }
                }
            }
        }

        PathNode a = pass(ids,startId,distances,idToPos);

        String fullPath=a.path.get(0).toString() + ",";
        for(int i=1; i < a.path.size();i++){
            fullPath += path[idToPos.get(a.path.get(i-1))][idToPos.get(a.path.get(i))] + a.path.get(i) + ",";
        }

        Log.i("getRoute:FullPath", fullPath);

        Log.i("getRoute:Tempo",String.valueOf(System.nanoTime()-time));

        /*for(BeaconNode n: nodes.values()){
            for(BeaconNode node: nodes.values()){
                Log.i("BeaconTree","De "+ n.getBeacon().getId() + " Ate " + node.getBeacon().getId() + ": " + distances[idToPos.get(n.getBeacon().getId())][idToPos.get(node.getBeacon().getId())]);
                Log.i("BeaconTree","Path de "+ n.getBeacon().getId() + " Ate " + node.getBeacon().getId() + ": " + n.getBeacon().getId() + "," + path[idToPos.get(n.getBeacon().getId())][idToPos.get(node.getBeacon().getId())] + node.getBeacon().getId()) ;
            }
        }*/

        List<BeaconNode> beaconsPath = new ArrayList<BeaconNode>();
        for(String s: fullPath.split(",")){
            beaconsPath.add(getNode(new Long(s)));
        }
        return beaconsPath;
    }

    private PathNode pass(List<Long> needPass, Long start, double[][] distances, Map<Long,Integer> idToPos){
        if(needPass.isEmpty()) {
            PathNode a = new PathNode();
            a.value=0;
            a.path = new ArrayList<>();
            a.path.add(start);
            return a;
        }
        PathNode min = null;
        for(Long l : needPass){
            List<Long> list = new ArrayList<>(needPass);
            list.remove(l);
            PathNode val = pass(list,l,distances,idToPos);
            if(min ==null || val.value + distances[idToPos.get(start)][idToPos.get(l)] < min.value ){
                min = val;
                //Soma o atual
                min.value = val.value + distances[idToPos.get(start)][idToPos.get(l)];
            }
        }
        min.path.add(0,start);
        return min;
    }



















    private class State {
        public Long atual;
        public String path;
        public double percorrido;
        public boolean[] passed;
    }

    public List<BeaconNode> getRouteA(List<Long> ids, Long startId) {
        long time = System.nanoTime();
        PriorityQueue<State> temp = new PriorityQueue<State>(10, new Comparator<State>() {
            @Override
            public int compare(State o1, State o2) {
                // TODO Auto-generated method stub
                return  new Double(( o1.percorrido) - ( o2.percorrido)).intValue();
            }
        });

        Map<Long,Integer> idToPos = new HashMap<Long,Integer>();
        int pos =0;
        for(Long n: ids){
            idToPos.put(n, pos);
            pos++;
        }

        State init = new State();
        init.path=String.valueOf(startId);
        init.atual = startId;
        init.percorrido=0;
        init.passed = new boolean[ids.size()];
        temp.add(init);

        State result = null;
        while (!temp.isEmpty()) {
            //Current state
            State current = temp.poll();

            for (BeaconRelation r : getNode(current.atual).getBeacons()){
                State next = new State();
                next.atual = r.getBeacon().getBeacon().getId();
                next.path = current.path + "," + r.getBeacon().getBeacon().getId();
                next.percorrido = current.percorrido + r.getDistance();
                next.passed = current.passed.clone();
                if(idToPos.get(r.getBeacon().getBeacon().getId())!=null){
                    next.passed[idToPos.get(r.getBeacon().getBeacon().getId())] = true;
                    boolean finish = true;
                    for(boolean b: next.passed){
                        if(!b){
                           finish=false;
                            break;
                        }
                    }
                    if(finish){
                        result = next;
                        //FINISH
                        temp.clear();
                        break;
                    }
                }
                temp.add(next);
            }
        }


        Log.i("A*:Path",result.path);
        Log.i("A*:Tempo",String.valueOf(System.nanoTime()-time));

        List<BeaconNode> beaconsPath = new ArrayList<BeaconNode>();
        for(String s: result.path.split(",")){
            beaconsPath.add(getNode(new Long(s)));
        }
        return beaconsPath;
    }







    }
