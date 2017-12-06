package de.tuberlin.amos.ws17.swit.poi;

import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.common.PointOfInterest;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PoisInSightFinder {
    private int metersInDrivinDirection;
    private int metersAgainstDrivingDirection;
    private int metersBraod;

    public PoisInSightFinder(int metersInDrivinDirection, int metersAgainstDrivingDirection, int metersBraod) {
        this.metersInDrivinDirection = metersInDrivinDirection;
        this.metersAgainstDrivingDirection = metersAgainstDrivingDirection;
        this.metersBraod = metersBraod;
    }

    public  Map<PointOfInterest, Float> getPoisInViewAngle(GpsPosition oldPosition, GpsPosition currentPosition, Collection<PointOfInterest> pois){

        Polygon sight=calculateSight(currentPosition, oldPosition);
        Map<PointOfInterest, Float> inViewRange=new HashMap<>();

        GpsPosition inDrivingDirection=GeographicCalculator.expandByMeters(oldPosition, currentPosition, metersInDrivinDirection);

        for(PointOfInterest p: pois){

            if(sight.contains(p.getGpsPosition().getX(), p.getGpsPosition().getY())){
                float distance=inDrivingDirection.distanceTo(p.getGpsPosition());
                inViewRange.put(p, distance);
            }
        }

        return inViewRange;
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
}
