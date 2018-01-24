package de.tuberlin.amos.ws17.swit.poi.google;

import de.tuberlin.amos.ws17.swit.poi.PoiType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides Mapping between {@link GoogleType} and {@link PoiType} bidirectional.
 * Created by leand on 17.11.2017.
 */
class GoogleTypeMap extends HashMap<GoogleType, PoiType> {
    //TODO: a multimap would be more appropriate, but that's not too important
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
        this.put(GoogleType.car_repair, PoiType.OF_NO_INTEREST);
        this.put(GoogleType.car_wash, PoiType.INFRASTRUCTURE);
        this.put(GoogleType.casino, PoiType.LEISURE);
        this.put(GoogleType.cemetery, PoiType.HISTORIC);
        this.put(GoogleType.church, PoiType.HISTORIC);
        this.put(GoogleType.city_hall, PoiType.TOURISM);
        this.put(GoogleType.clothing_store, PoiType.SHOPPING);
        this.put(GoogleType.convenience_store, PoiType.SHOPPING);
        this.put(GoogleType.courthouse, PoiType.INFRASTRUCTURE);
        this.put(GoogleType.dentist, PoiType.OF_NO_INTEREST);
        this.put(GoogleType.department_store, PoiType.SHOPPING);
        this.put(GoogleType.doctor, PoiType.OF_NO_INTEREST);
        this.put(GoogleType.electrician, PoiType.OF_NO_INTEREST);
        this.put(GoogleType.electronics_store, PoiType.SHOPPING);
        this.put(GoogleType.embassy, PoiType.INFRASTRUCTURE);
        this.put(GoogleType.fire_station, PoiType.TOURISM);
        this.put(GoogleType.florist, PoiType.SHOPPING);
        this.put(GoogleType.funeral_home, PoiType.OF_NO_INTEREST);
        this.put(GoogleType.furniture_store, PoiType.SHOPPING);
        this.put(GoogleType.gas_station, PoiType.INFRASTRUCTURE);
        this.put(GoogleType.grocery_or_supermarket, PoiType.SHOPPING);
        this.put(GoogleType.gym, PoiType.LEISURE);
        this.put(GoogleType.hair_care, PoiType.LEISURE);
        this.put(GoogleType.hardware_store, PoiType.SHOPPING);
        this.put(GoogleType.hindu_temple, PoiType.INFRASTRUCTURE);
        this.put(GoogleType.home_goods_store, PoiType.SHOPPING);
        this.put(GoogleType.hospital, PoiType.NOT_DEFINED);
        this.put(GoogleType.insurance_agency, PoiType.NOT_DEFINED);
        this.put(GoogleType.jewelry_store, PoiType.SHOPPING);
        this.put(GoogleType.laundry, PoiType.INFRASTRUCTURE);
        this.put(GoogleType.lawyer, PoiType.NOT_DEFINED);
        this.put(GoogleType.library, PoiType.INFRASTRUCTURE);
        this.put(GoogleType.liquor_store, PoiType.SHOPPING);
        this.put(GoogleType.local_government_office, PoiType.INFRASTRUCTURE);
        this.put(GoogleType.locksmith, PoiType.OF_NO_INTEREST);
        this.put(GoogleType.lodging, PoiType.INFRASTRUCTURE);
        this.put(GoogleType.meal_delivery, PoiType.OF_NO_INTEREST);
        this.put(GoogleType.meal_takeaway, PoiType.FOOD);
        this.put(GoogleType.mosque, PoiType.HISTORIC);
        this.put(GoogleType.movie_rental, PoiType.OF_NO_INTEREST);
        this.put(GoogleType.movie_theater, PoiType.LEISURE);
        this.put(GoogleType.moving_company, PoiType.OF_NO_INTEREST);
        this.put(GoogleType.museum, PoiType.LEISURE);
        this.put(GoogleType.night_club, PoiType.LEISURE);
        this.put(GoogleType.painter, PoiType.SHOPPING);
        this.put(GoogleType.park, PoiType.TOURISM);
        this.put(GoogleType.parking, PoiType.INFRASTRUCTURE);
        this.put(GoogleType.pet_store, PoiType.SHOPPING);
        this.put(GoogleType.pharmacy, PoiType.SHOPPING);
        this.put(GoogleType.physiotherapist, PoiType.OF_NO_INTEREST);
        this.put(GoogleType.plumber, PoiType.OF_NO_INTEREST);
        this.put(GoogleType.police, PoiType.INFRASTRUCTURE);
        this.put(GoogleType.post_office, PoiType.INFRASTRUCTURE);
        this.put(GoogleType.real_estate_agency, PoiType.OF_NO_INTEREST);
        this.put(GoogleType.restaurant, PoiType.FOOD);
        this.put(GoogleType.roofing_contractor, PoiType.OF_NO_INTEREST);
        this.put(GoogleType.rv_park, PoiType.INFRASTRUCTURE);
        this.put(GoogleType.school, PoiType.OF_NO_INTEREST);
        this.put(GoogleType.shoe_store, PoiType.SHOPPING);
        this.put(GoogleType.shopping_mall, PoiType.SHOPPING);
        this.put(GoogleType.spa, PoiType.LEISURE);
        this.put(GoogleType.stadium, PoiType.LEISURE);
        this.put(GoogleType.storage, PoiType.OF_NO_INTEREST);
        this.put(GoogleType.store, PoiType.SHOPPING);
        this.put(GoogleType.subway_station, PoiType.INFRASTRUCTURE);
        this.put(GoogleType.supermarket, PoiType.SHOPPING);
        this.put(GoogleType.synagogue, PoiType.HISTORIC);
        this.put(GoogleType.taxi_stand, PoiType.OF_NO_INTEREST);
        this.put(GoogleType.train_station, PoiType.TOURISM);
        this.put(GoogleType.transit_station, PoiType.TOURISM);
        this.put(GoogleType.travel_agency, PoiType.SHOPPING);
        this.put(GoogleType.university, PoiType.HISTORIC);
        this.put(GoogleType.veterinary_care, PoiType.OF_NO_INTEREST);
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
