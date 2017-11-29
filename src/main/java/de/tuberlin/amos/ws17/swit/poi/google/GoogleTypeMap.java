package de.tuberlin.amos.ws17.swit.poi.google;

import de.tuberlin.amos.ws17.swit.poi.PoiType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides Mapping between {@link GoogleType} and {@link PoiType} bidirectional.
 * Created by leand on 17.11.2017.
 */
class GoogleTypeMap extends HashMap<GoogleType, PoiType> {

    //TODO: missing mappings
    {
        this.put(GoogleType.accounting, PoiType.INFRASTRUCTURE);
        this.put(GoogleType.airport, PoiType.TRANSPORT);
        this.put(GoogleType.amusement_park, PoiType.LEISURE);
        this.put(GoogleType.aquarium, PoiType.LEISURE);
        this.put(GoogleType.art_gallery, PoiType.LEISURE);
        this.put(GoogleType.atm, PoiType.INFRASTRUCTURE);
        this.put(GoogleType.bakery, PoiType.FOOD);
        this.put(GoogleType.bank, PoiType.INFRASTRUCTURE);
        this.put(GoogleType.bar, PoiType.FOOD);
        this.put(GoogleType.beauty_salon, PoiType.SHOPPING);
        this.put(GoogleType.bicycle_store, PoiType.SHOPPING);
        this.put(GoogleType.book_store, PoiType.SHOPPING);
        this.put(GoogleType.bowling_alley, PoiType.LEISURE);
        this.put(GoogleType.bus_station, PoiType.TRANSPORT);
        this.put(GoogleType.cafe, PoiType.FOOD);
        this.put(GoogleType.campground, PoiType.INFRASTRUCTURE);
        this.put(GoogleType.car_dealer, PoiType.SHOPPING);
        this.put(GoogleType.car_rental, PoiType.TRANSPORT);
        this.put(GoogleType.car_repair, PoiType.INFRASTRUCTURE);
        this.put(GoogleType.car_wash, PoiType.INFRASTRUCTURE);
        this.put(GoogleType.casino, PoiType.LEISURE);
        this.put(GoogleType.cemetery, PoiType.HISTORIC);
        this.put(GoogleType.church, PoiType.HISTORIC);
        this.put(GoogleType.city_hall, PoiType.TOURISM);
        this.put(GoogleType.clothing_store, PoiType.SHOPPING);
        this.put(GoogleType.convenience_store, PoiType.SHOPPING);
        this.put(GoogleType.courthouse, PoiType.NOT_DEFINED);
        this.put(GoogleType.dentist, PoiType.NOT_DEFINED);
        this.put(GoogleType.department_store, PoiType.NOT_DEFINED);
        this.put(GoogleType.doctor, PoiType.NOT_DEFINED);
        this.put(GoogleType.electrician, PoiType.OF_NO_INTEREST);
        this.put(GoogleType.electronics_store, PoiType.SHOPPING);
        this.put(GoogleType.embassy, PoiType.INFRASTRUCTURE);
        this.put(GoogleType.fire_station, PoiType.INFRASTRUCTURE);
        this.put(GoogleType.florist, PoiType.SHOPPING);
        this.put(GoogleType.funeral_home, PoiType.OF_NO_INTEREST);
        this.put(GoogleType.furniture_store, PoiType.SHOPPING);
        this.put(GoogleType.gas_station, PoiType.INFRASTRUCTURE);
        this.put(GoogleType.gym, PoiType.LEISURE);
        this.put(GoogleType.hair_care, PoiType.LEISURE);
        this.put(GoogleType.hardware_store, PoiType.SHOPPING);
        this.put(GoogleType.hindu_temple, PoiType.INFRASTRUCTURE);
        this.put(GoogleType.home_goods_store, PoiType.NOT_DEFINED);
        this.put(GoogleType.hospital, PoiType.NOT_DEFINED);
        this.put(GoogleType.insurance_agency, PoiType.NOT_DEFINED);
        this.put(GoogleType.jewelry_store, PoiType.NOT_DEFINED);
        this.put(GoogleType.laundry, PoiType.NOT_DEFINED);
        this.put(GoogleType.lawyer, PoiType.NOT_DEFINED);
        this.put(GoogleType.library, PoiType.NOT_DEFINED);
        this.put(GoogleType.liquor_store, PoiType.NOT_DEFINED);
        this.put(GoogleType.local_government_office, PoiType.NOT_DEFINED);
        this.put(GoogleType.locksmith, PoiType.NOT_DEFINED);
        this.put(GoogleType.lodging, PoiType.NOT_DEFINED);
        this.put(GoogleType.meal_delivery, PoiType.NOT_DEFINED);
        this.put(GoogleType.meal_takeaway, PoiType.NOT_DEFINED);
        this.put(GoogleType.mosque, PoiType.NOT_DEFINED);
        this.put(GoogleType.movie_rental, PoiType.NOT_DEFINED);
        this.put(GoogleType.movie_theater, PoiType.NOT_DEFINED);
        this.put(GoogleType.moving_company, PoiType.NOT_DEFINED);
        this.put(GoogleType.museum, PoiType.NOT_DEFINED);
        this.put(GoogleType.night_club, PoiType.NOT_DEFINED);
        this.put(GoogleType.painter, PoiType.NOT_DEFINED);
        this.put(GoogleType.park, PoiType.NOT_DEFINED);
        this.put(GoogleType.parking, PoiType.NOT_DEFINED);
        this.put(GoogleType.pet_store, PoiType.NOT_DEFINED);
        this.put(GoogleType.pharmacy, PoiType.NOT_DEFINED);
        this.put(GoogleType.physiotherapist, PoiType.NOT_DEFINED);
        this.put(GoogleType.plumber, PoiType.NOT_DEFINED);
        this.put(GoogleType.police, PoiType.NOT_DEFINED);
        this.put(GoogleType.post_office, PoiType.NOT_DEFINED);
        this.put(GoogleType.real_estate_agency, PoiType.NOT_DEFINED);
        this.put(GoogleType.restaurant, PoiType.NOT_DEFINED);
        this.put(GoogleType.roofing_contractor, PoiType.NOT_DEFINED);
        this.put(GoogleType.rv_park, PoiType.NOT_DEFINED);
        this.put(GoogleType.school, PoiType.NOT_DEFINED);
        this.put(GoogleType.shoe_store, PoiType.NOT_DEFINED);
        this.put(GoogleType.shopping_mall, PoiType.NOT_DEFINED);
        this.put(GoogleType.spa, PoiType.NOT_DEFINED);
        this.put(GoogleType.stadium, PoiType.NOT_DEFINED);
        this.put(GoogleType.storage, PoiType.NOT_DEFINED);
        this.put(GoogleType.store, PoiType.NOT_DEFINED);
        this.put(GoogleType.subway_station, PoiType.NOT_DEFINED);
        this.put(GoogleType.synagogue, PoiType.NOT_DEFINED);
        this.put(GoogleType.taxi_stand, PoiType.NOT_DEFINED);
        this.put(GoogleType.train_station, PoiType.NOT_DEFINED);
        this.put(GoogleType.transit_station, PoiType.NOT_DEFINED);
        this.put(GoogleType.travel_agency, PoiType.NOT_DEFINED);
        this.put(GoogleType.university, PoiType.NOT_DEFINED);
        this.put(GoogleType.veterinary_care, PoiType.NOT_DEFINED);
        this.put(GoogleType.zoo, PoiType.LEISURE);
    }

    Set<GoogleType> getKeysByValue(PoiType value) {
        return this.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), value))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
}
