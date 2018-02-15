package de.tuberlin.amos.ws17.swit.poi;

import de.tuberlin.amos.ws17.swit.common.DebugLog;
import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.common.PointOfInterest;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

public class PoisInSightFinder {
    private int metersInDrivinDirection;
    private int metersAgainstDrivingDirection;
    private int metersBraod;

    public PoisInSightFinder(int metersInDrivinDirection, int metersAgainstDrivingDirection, int metersBraod) {
        this.metersInDrivinDirection = metersInDrivinDirection;
        this.metersAgainstDrivingDirection = metersAgainstDrivingDirection;
        this.metersBraod = metersBraod;
    }

    /**
     * Calculates which POIs lie in a four-sided figure right to the car. The length of the sides are defined in the constructor of the {@link PoisInSightFinder}.
     * @param oldPosition as the previous position
     * @param currentPosition as the current position
     * @param pois as the pois to process
     * @return the pois within the defined area
     */
    public  Map<PointOfInterest, Float> getPoisInViewAngle(GpsPosition oldPosition, GpsPosition currentPosition, Collection<PointOfInterest> pois){

        if(oldPosition==null || currentPosition==null)
            getPoisInRange(currentPosition, pois, -1);
        else if(oldPosition.equals(currentPosition)){
            getPoisInRange(currentPosition, pois, -1);
        }

        Polygon sight=calculateSight(currentPosition, oldPosition);
        DebugLog.log(DebugLog.SOURCE_MAPS_POI,"Calculated sight polygon (int values): "+
                sight.getBounds2D().toString()+
        " ...");

        Map<PointOfInterest, Float> inViewRange=new HashMap<>();

        GpsPosition inDrivingDirection=GeographicCalculator.expandByMeters(oldPosition, currentPosition, metersInDrivinDirection);

        DebugLog.log(DebugLog.SOURCE_MAPS_POI,"...number of POIs BEFORE sight filtering: "+
                pois.size()+
        " ...");

        for(PointOfInterest p: pois){

            if(sight.contains(p.getGpsPosition().getX(), p.getGpsPosition().getY())){
                float distance=inDrivingDirection.distanceTo(p.getGpsPosition());
                inViewRange.put(p, distance);
            }
        }
        //sort asc
        inViewRange=sortByComparator(inViewRange, true);
        DebugLog.log(DebugLog.SOURCE_MAPS_POI,"...and AFTER sight filtering: "+inViewRange.size());

        return inViewRange;
    }

    /**
     * Calculate which pois are in range the default range is specified by the sum of the edges.
     * @param currentPosition as the position of the car
     * @param pois as the pois to be thinned
     * @param range the maxrange, will be replaced by a default if 0
     */
    private void getPoisInRange(GpsPosition currentPosition, Collection<PointOfInterest> pois, int range) {
        Map<PointOfInterest, Float> poisWithDistance = calculateDistances(currentPosition, pois);
        Map<PointOfInterest, Float> poisInRange = new HashMap<>();

        int maxDistance=range;

        if(range<0)
            maxDistance=metersAgainstDrivingDirection+metersBraod+metersInDrivinDirection;

        for(PointOfInterest poi: poisWithDistance.keySet()){
            if(poisWithDistance.get(poi)<=maxDistance)
                poisInRange.put(poi, poisWithDistance.get(poi));
        }
    }

    public Map<PointOfInterest, Float> calculateDistances(GpsPosition currentPosition, Collection<PointOfInterest> pois){
        Map<PointOfInterest, Float> poisWithDistance=new HashMap<>();
        for(PointOfInterest poi:pois){
            poisWithDistance.put(poi, currentPosition.distanceTo(poi.getGpsPosition()));
        }
        //sort asc
        poisWithDistance=sortByComparator(poisWithDistance, true);
        return poisWithDistance;
    }

    public Polygon calculateSight(GpsPosition currentPosition, GpsPosition oldPosition){
        Polygon sight=new Polygon();

        GpsPosition inDrivingDirection=GeographicCalculator.expandByMeters(oldPosition, currentPosition, metersInDrivinDirection);
        GpsPosition againstDrivingDirection=GeographicCalculator.expandInverseByMeters(oldPosition, currentPosition, metersAgainstDrivingDirection);

        GpsPosition inDrivingDirectionBroad=GeographicCalculator.expandNormalByMeters(oldPosition, currentPosition, inDrivingDirection, metersBraod);
        GpsPosition againstDrivingDirectionBroad=GeographicCalculator.expandNormalByMeters(oldPosition, currentPosition, againstDrivingDirection, metersBraod);

        sight.addPoint(inDrivingDirection.getX(), inDrivingDirection.getY());
        sight.addPoint(againstDrivingDirection.getX(), againstDrivingDirection.getY());
        sight.addPoint(againstDrivingDirectionBroad.getX(), againstDrivingDirectionBroad.getY());
        sight.addPoint(inDrivingDirectionBroad.getX(), inDrivingDirectionBroad.getY());

        return sight;
    }

    private static <T extends Comparable> Map<PointOfInterest, T> sortByComparator(Map<PointOfInterest, T> unsortMap, final boolean order)
    {

        List<Entry<PointOfInterest, T>> list = new LinkedList<>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<PointOfInterest, T>>()
        {
            public int compare(Entry<PointOfInterest, T> o1,
                               Entry<PointOfInterest, T> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<PointOfInterest, T> sortedMap = new LinkedHashMap<>();
        for (Entry<PointOfInterest, T> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}
