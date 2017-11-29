package de.tuberlin.amos.ws17.swit.poi.google;

import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.poi.PoiType;

import java.util.Set;


/**
 * A Circle that can be used to find its containing POIs.
 */
public class CircleSearchGeometry {

    private int radiusInMeters;
    private GpsPosition center;
    private Set<GoogleType> googletypes=null;
    private Set<PoiType> poiTypes=null;

    CircleSearchGeometry(int radiusInMeters, GpsPosition center, Set<GoogleType> googletypes, Set<PoiType> poiTypes) {
        this.radiusInMeters = radiusInMeters;
        this.center = center;
        this.googletypes = googletypes;
        this.poiTypes = poiTypes;
    }

    public int getRadiusInMeters() {
        return radiusInMeters;
    }

    public void setRadiusInMeters(int radiusInMeters) {
        this.radiusInMeters = radiusInMeters;
    }

    public GpsPosition getCenter() {
        return center;
    }

    public void setCenter(GpsPosition center) {
        this.center = center;
    }

    public Set<GoogleType> getGoogletypes() {
        return googletypes;
    }

    public void setGoogletypes(Set<GoogleType> googletypes) {
        this.googletypes = googletypes;
    }

    public Set<PoiType> getPoiTypes() {
        return poiTypes;
    }

    public void setPoiTypes(Set<PoiType> poiTypes) {
        this.poiTypes = poiTypes;
    }

    @Override
    public String toString() {
        return "CircleSearchGeometry{" +
                "radiusInMeters=" + radiusInMeters +
                ", center=" + center +
                ", googletypes=" + googletypes +
                ", poiTypes=" + poiTypes +
                '}';
    }
}
