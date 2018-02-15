package de.tuberlin.amos.ws17.swit.poi.google;

import de.tuberlin.amos.ws17.swit.common.DebugLog;
import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import org.apache.commons.lang3.EnumUtils;
import se.walkercrou.places.Place;
import se.walkercrou.places.Status;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A Factory to create {@link GooglePoi}s from other datatypes
 */
class GooglePoiFactory {

    private List<String> forbiddenInPlacenames;

    /**
     * Create a {@link GooglePoiFactory} that creates new Pois after some implemented rules.
     * @param forbiddenInPlacenames remove all POIs cointaining a String that is being contained here
     */
    GooglePoiFactory(List<String> forbiddenInPlacenames) {

        this.forbiddenInPlacenames = forbiddenInPlacenames;
    }

    private <T extends Collection<? extends PointOfInterest>> void removeForbiddenInPlacename(T places){
        if(places!=null&&forbiddenInPlacenames!=null) {
            Set<PointOfInterest> poisToRemove = new HashSet<>();
            for (PointOfInterest poi : places) {
                for (String forbiddenInName : forbiddenInPlacenames) {
                    if (poi.getName().toLowerCase().contains(forbiddenInName.toLowerCase()))
                        poisToRemove.add(poi);
                }
            }
            DebugLog.log(DebugLog.SOURCE_MAPS_POI,"GooglePoiFactory: Removed "+poisToRemove.size()+
                    " of former " +places.size()+
                    " POIs with names " +
                    poisToRemove.stream().map(mo -> mo.getName()).collect(Collectors.toList()).toString()+
                    "\naccording to forbidden names: "+ forbiddenInPlacenames.toString()
            );

            places.removeAll(poisToRemove);


        }
    }


    List<GooglePoi> createPOIsfromPlace(Collection<Place> places){
        List<GooglePoi> pois=new ArrayList<>();

        for(Place place: places){
            pois.add(createPOIfromPlace(place));
        }

        removeForbiddenInPlacename(pois);
        return pois;
    }

    /**
     * Create a {@link GooglePoi} by passing a {@link Place}
     * @param place as the output of the currently used Google Places API
     *
     * @return the created GooglePoi
     */
    private GooglePoi createPOIfromPlace(Place place){

        GooglePoi poi=new GooglePoi(
                place.getPlaceId(),
                place.getName(),
                new GpsPosition(place.getLongitude(), place.getLatitude()),
                getTypes(place)
        );
        poi.setInformationAbstract(constructInformationAbstract(place));
        if(place.getPhotos()!=null)
            if(!place.getPhotos().isEmpty())
                poi.setPhotoreference(place.getPhotos().get(0));

        return poi;
    }

    /**
     * A method that defines what information provided by Google Places informationAbstract
     * of a {@link de.tuberlin.amos.ws17.swit.common.PointOfInterest} could contain
     * @param place as the Locations abstract to create
     * @return the String for the information abstract
     */
    private String constructInformationAbstract(Place place){
        String info="";

        if (place.getWebsite()!=null)
            info += "\nWebsite: " + place.getWebsite();
        if (place.getAddress()!=null)
            info += "\nAdress: " + place.getAddress();
        if (place.getStatus()!=null)
            if (place.getStatus()!= Status.NONE)
                info += "\nStatus: " + place.getStatus();
        if (place.getRating()!=-1.0)
            info += "\nRating: " + place.getRating();

        return info.replaceFirst("\n", "");
    }


    /**
     * Translate the types of the used API into a Set of {@link GoogleType}s
     * @param p as the POI of which the translation is to be done
     * @return a Set of the successfully translated types
     */
    private Set<GoogleType> getTypes(Place p){
        Set<GoogleType> types= new HashSet<>();

        for(String type: p.getTypes()){

            if(EnumUtils.isValidEnum(GoogleType.class, type)){
                types.add(GoogleType.valueOf(type));

            }else{

                types.add(GoogleType.UNKNOWN);
                System.err.println("Enum of places Api does not match current google enums for enum: " +type+
                        "\n...to solve this issue add it to the Enum 'GoogleType'.");
            }

        }

        return types;
    }

}
