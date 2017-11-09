package de.tuberlin.amos.ws17.swit.image_analysis;

import com.google.api.services.vision.v1.model.BoundingPoly;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.LocationInfo;

import java.util.List;
import java.util.stream.Collectors;

public class LandmarkResult {

    // can be used for Google Knowledge Graph Search API
    private String id;

    // Entity textual name, expressed in its locale language.
    private String name;

    // Overall score of the result. Range [0, 1].
    private Float score;

    // location of entity, can be multiple (location of landmark + location where image was taken)
    private List<GPSLocation> locations;

    // Image region of landmark on image
    private BoundingPoly boundlingPoly;

    public LandmarkResult(String id, String name, Float score, List<GPSLocation> locations, BoundingPoly boundlingPoly) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.locations = locations;
        this.boundlingPoly = boundlingPoly;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public List<GPSLocation> getLocations() {
        return locations;
    }

    public BoundingPoly getBoundlingPoly() {
        return boundlingPoly;
    }

    public static LandmarkResult fromEntityAnnotation(EntityAnnotation annotation) {
        return new LandmarkResult(annotation.getMid(),
                annotation.getDescription(),
                annotation.getScore(),
                annotation.getLocations().stream().map(GPSLocation::fromLocationInfo).collect(Collectors.toList()),
                annotation.getBoundingPoly());
    }

    // adjust the score of result by providing current gps location
    public void adjustScore(LocationInfo currentLocation) {
        // TODO: create formula which takes current location into consideration
    }
}
