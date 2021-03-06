package de.tuberlin.amos.ws17.swit.poi;

import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import se.walkercrou.places.exception.InvalidRequestException;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
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

    @Override
    public void addImages(Collection<PointOfInterest> poisToAddPhotosTo) {
        for(PointOfInterest poi: poisToAddPhotosTo){
            if(poi.getId().equals("63054a42e42879c3b17e1819bc86b79b58fd27b9")){
                InputStream is=MockedPoiService.class.getResourceAsStream("/mockimages/ZoologischerGartenBerlin.jpg");
                try {
                    BufferedImage image = ImageIO.read(is);
                    poi.setImage(image);
                }catch (IOException e){
                    System.err.println("No BufferedImage for: "+is.toString());
                    e.printStackTrace();
                }
            }
            if(poi.getId().equals("6d010effb6cf7c8e092ca0fdb294f8a6256f0828")){
                InputStream is=MockedPoiService.class.getResourceAsStream("/mockimages/Filmpalast.jpg");
                try {
                    BufferedImage image = ImageIO.read(is);
                    poi.setImage(image);
                }catch (IOException e){
                    System.err.println("No BufferedImage for: "+is.toString());
                    e.printStackTrace();
                }
            }
        }
    }


    private List<PointOfInterest> generateSamples(){
        List<PointOfInterest> pois=new ArrayList<>();

        PointOfInterest poi;

        poi=new PointOfInterest("884dde5f4b1dbafe3298faf8c43afa1dc19a2737", "SPAR", new GpsPosition(13.259989,52.5053456));
        poi.setInformationAbstract("Hardenbergplatz X, Berlin");
        pois.add(poi);


        poi=new PointOfInterest("63054a42e42879c3b17e1819bc86b79b58fd27b9", "Zoo Berlin", new GpsPosition(13.33414771,52.5067766));
        poi.setInformationAbstract("Ge??ffnet\nHardenbergplatz Y, Berlin\nBewertung: 4.Y");
        pois.add(poi);


        poi=new PointOfInterest("6d010effb6cf7c8e092ca0fdb294f8a6256f0828", "Delphi Filmpalast", new GpsPosition(13.340322,52.504344));
        poi.setInformationAbstract("Ge??ffnet\nKantstra??e Z, Berlin\nBewertung: 4.X");
        pois.add(poi);

        return pois;
    }

}
