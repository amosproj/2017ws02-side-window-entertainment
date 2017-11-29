package de.tuberlin.amos.ws17.swit.poi.google;


import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.poi.PoiType;
import de.tuberlin.amos.ws17.swit.poi.GeographicCalculator;

import java.util.Set;

/**
 * A Factory for creating {@link CircleSearchGeometry}s or {@link MultiCircleSearchGeometry}s.
 *  How the resulting geometries look like is being defined in the constructor of the {@link SearchGeometryFactory}
 */
public class SearchGeometryFactory {


    private double radiusMultiplicator=1.5;
    private int firstRadius=70;
    private int size=3;
    private Set<GoogleType> googletypes=null;//TODO
    private Set<PoiType> poiTypes=null;//TODO

    public int getFirstRadius() {
        return firstRadius;
    }

    /**
     * The default constructor. Uses the default factory settings.
     * */
    SearchGeometryFactory(){}

    /**
     * Creates a new instance of {@link SearchGeometryFactory}, constructs {@link MultiCircleSearchGeometry}s as being defined by the given arguments.
     * @param radiusMultiplicator as the multiplicator for the previos radius
     * @param firstRadius as the first radius in meter
     * @param amountOfCircles as the size of the {@link MultiCircleSearchGeometry}
     * @param googletypes as the {@link GoogleType}s to be looked up
     * @param poiTypes as the {@link PoiType}s to be looked up
     */
    public SearchGeometryFactory(double radiusMultiplicator, int firstRadius, int amountOfCircles, Set<GoogleType> googletypes, Set<PoiType> poiTypes) {
        this.radiusMultiplicator = radiusMultiplicator;
        this.firstRadius = firstRadius;
        this.size = amountOfCircles;
        this.googletypes = googletypes;
        this.poiTypes = poiTypes;
    }

    /**
     * Create a cone for two given coordinates, i.e. the current and a previous one for the driving direction of the vehicle.
     * @param c1 the first coordinate (historic of gps-track)
     * @param c2 the middel coordinate (could be current position)
     * @return the resulting {@link MultiCircleSearchGeometry} that can be handed over to the {@link GooglePoiLoader}
     */
    public MultiCircleSearchGeometry createSearchCirclesForDirectedCoordinates(GpsPosition c1, GpsPosition c2) {

        MultiCircleSearchGeometry multiCircleSearchGeometry=new MultiCircleSearchGeometry();

        double lastRadius=firstRadius/radiusMultiplicator;
        int lastDistance=0;

        //expand
        for(int i=0; i<size; i++){

            //update
            int currentRadius=(int) (lastRadius*radiusMultiplicator);
            double currentDistance=currentRadius+(lastDistance*1.5);

            GpsPosition newCenter= GeographicCalculator.expandByMeters(c1, c2, currentDistance);

            CircleSearchGeometry csg=createSearchCircle(currentRadius, newCenter);
            multiCircleSearchGeometry.add(csg);

            //for next iteration in for-loop
            lastRadius=currentRadius;

        }

        return multiCircleSearchGeometry;

    }
    public CircleSearchGeometry createSearchCircle(int currentRadius, GpsPosition center) {

        return new CircleSearchGeometry(currentRadius, center, null, null);

    }


}
