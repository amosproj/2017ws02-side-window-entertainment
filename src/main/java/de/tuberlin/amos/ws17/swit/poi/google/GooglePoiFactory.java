package de.tuberlin.amos.ws17.swit.poi.google;

import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import se.walkercrou.places.Place;
import se.walkercrou.places.Status;

import java.util.*;

/**
 *
 */
class GooglePoiFactory {


    List<GooglePoi> createPOIsfromPlace(Collection<Place> places){
        List<GooglePoi> pois=new ArrayList<>();

        for(Place place: places){
            pois.add(createPOIfromPlace(place));
        }

        return pois;
    }

    GooglePoi createPOIfromPlace(Place place){

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


    private Set<GoogleType> getTypes(Place p){
        Set<GoogleType> types= new HashSet<>();

        for(String type: p.getTypes()){

            try {
                types.add(GoogleType.valueOf(type));

            } catch (IllegalArgumentException ex) {
                System.err.println("Enum of places Api does not match current google enums for enum: " +type);
                ex.printStackTrace();
            }
        }

        return types;
    }

}
