package com.java;

import org.elasticsearch.geometry.utils.Geohash;

import java.util.*;

public class FindGeoHashCodes {

    public static void main(String[] args) {

        int thresholdLayer = 5;

        String memberGeoHashCode = Geohash.stringEncode(-106.42,31.74, 5);

        Cell rootCell = new Cell(memberGeoHashCode, true);

        Set<String> geoHashCodesWithin10MiRad = findAllGeoHashCodesWithinThreshold(rootCell, thresholdLayer);
        System.out.println(geoHashCodesWithin10MiRad.size());
    }

    static Set<String> findAllGeoHashCodesWithinThreshold(Cell rootCell, int thresholdLayer){
        Set<String> resultSet = new HashSet<>();

        resultSet.add(rootCell.getGeoHashCode());

        if(thresholdLayer == 0){
            return resultSet;
        }

        int layer = 1;

        Queue<Cell> queue = new LinkedList<>();

        var neighbors = getAll8Neighbors(rootCell);

        addNewNeighbors(resultSet, queue, neighbors);

        queue.add(new Cell("*", false));
        layer++;

        Cell cell;

        while(layer <= thresholdLayer){

            cell = queue.poll();

            if(!Objects.isNull(cell) && cell.geoHashCode.equals("*")){
                layer++;
                queue.add(new Cell("*", false));
                cell = queue.poll();
            }

            if(layer > thresholdLayer)
                break;

            if(!Objects.isNull(cell) && !cell.isMiddle) {
                neighbors = getAll8Neighbors(cell);
                addNewNeighbors(resultSet, queue, neighbors);
            }

        }
        return resultSet;
    }

    private static void addNewNeighbors(Set<String> resultSet, Queue<Cell> queue, List<Cell> neighbors) {
        for(var neighbor : neighbors){
            if(!resultSet.contains(neighbor.getGeoHashCode())){
                queue.add(neighbor);
                resultSet.add(neighbor.getGeoHashCode());
            }
        }
    }

    static List<Cell> getAll8Neighbors(Cell cell){

        List<Cell> neighborsList = new ArrayList<>();

        Collection<? extends CharSequence> neighbors = Geohash.getNeighbors(cell.getGeoHashCode());

        Object[] list = neighbors.toArray();

        String northWest = (String) list[0];
        neighborsList.add(new Cell(northWest, false));

        String north = (String) list[1];
        neighborsList.add(new Cell(north, true));

        String northEast = (String) list[2];
        neighborsList.add(new Cell(northEast, false));

        String west = (String) list[3];
        neighborsList.add(new Cell(west, true));

        String east = (String) list[4];
        neighborsList.add(new Cell(east, true));

        String southWest = (String) list[5];
        neighborsList.add(new Cell(southWest, false));

        String south = (String) list[6];
        neighborsList.add(new Cell(south, true));

        String southEast = (String) list[7];
        neighborsList.add(new Cell(southEast, false));

        return neighborsList;
    }
}

class Cell{

    String geoHashCode;

    boolean isMiddle;

    Cell(String geoHashCode, boolean isMiddle){
        this.geoHashCode = geoHashCode;
        this.isMiddle = isMiddle;
    }

    public String getGeoHashCode() {
        return geoHashCode;
    }
}
