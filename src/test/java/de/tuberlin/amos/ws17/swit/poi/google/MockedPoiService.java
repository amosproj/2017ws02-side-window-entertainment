package de.tuberlin.amos.ws17.swit.poi.google;

import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import de.tuberlin.amos.ws17.swit.poi.PoiService;
import de.tuberlin.amos.ws17.swit.poi.PoiType;
import se.walkercrou.places.exception.InvalidRequestException;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A simulation for the {@link PoiService} returning real Google POIs.
 */
public class MockedPoiService implements PoiService<PointOfInterest> {

    @Override
    public List<PointOfInterest> loadPlaceForCircle(GpsPosition center, int radius) throws InvalidRequestException {


        return generateSamples();
    }

    @Override
    public List<PointOfInterest> loadPlaceForCircleAndPoiType(GpsPosition center, int radius, PoiType... types) throws InvalidRequestException {
        return generateSamples();
    }

    private List<PointOfInterest> generateSamples(){
        List<PointOfInterest> pois=new ArrayList<>();

        PointOfInterest poi;

        poi=new PointOfInterest("884dde5f4b1dbafe3298faf8c43afa1dc19a2737", "SPAR", new GpsPosition(13.259989,52.5053456));
        poi.setInformationAbstract("Hardenbergplatz X, Berlin");
        pois.add(poi);


        poi=new PointOfInterest("63054a42e42879c3b17e1819bc86b79b58fd27b9", "Zoo Berlin", new GpsPosition(13.33414771,52.5067766));
        poi.setInformationAbstract("Geöffnet\nHardenbergplatz Y, Berlin\nBewertung: 4.Y");
        InputStream is=MockedPoiService.class.getResourceAsStream("/images/ZoologischerGartenBerlin.jpg");
        try {
            BufferedImage image = ImageIO.read(is);
            poi.setImage(image);
        }catch (IOException e){
            System.err.println("No BufferedImage for: "+is.toString());
            e.printStackTrace();
        }
        pois.add(poi);


        poi=new PointOfInterest("6d010effb6cf7c8e092ca0fdb294f8a6256f0828", "Delphi Filmpalast", new GpsPosition(13.340322,52.504344));
        poi.setInformationAbstract("Geöffnet\nKantstraße Z, Berlin\nBewertung: 4.X");
        is=MockedPoiService.class.getResourceAsStream("/images/Filmpalast.jpg");
        try {
            BufferedImage image = ImageIO.read(is);
            poi.setImage(image);
        }catch (IOException e){
            System.err.println("No BufferedImage for: "+is.toString());
            e.printStackTrace();
        }
        pois.add(poi);

        return pois;
    }

}
