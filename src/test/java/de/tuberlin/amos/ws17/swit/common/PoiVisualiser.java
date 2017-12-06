package de.tuberlin.amos.ws17.swit.common;

import java.awt.Polygon;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Provides a pretty dirty and improvised way of getting a Kml to visualize {@link PointOfInterest}s {@link GpsPosition}s and the search geometry.
 */
public class PoiVisualiser {

    private final static String postBody1TillName = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n" +
            "  <Document>\n" +
            "    <name>";
    private final static String postBody2FromNameToFolders = "</name>\n <open>1</open>\n <description>Returned POIs</description>\n <Style id=\"downArrowIcon\">\n <IconStyle>\n <Icon>\n <href>http://maps.google.com/mapfiles/kml/pal4/icon28.png</href>\n </Icon>\n </IconStyle>\n </Style>\n <Style id=\"globeIcon\">\n <IconStyle>\n <Icon>\n <href>http://maps.google.com/mapfiles/kml/pal3/icon19.png</href>\n </Icon>\n </IconStyle>\n <LineStyle>\n <width>2</width>\n </LineStyle>\n </Style>\n <Style id=\"transPurpleLineGreenPoly\">\n <LineStyle>\n <color>7fff00ff</color>\n <width>4</width>\n </LineStyle>\n <PolyStyle>\n <color>7f00ff00</color>\n </PolyStyle>\n </Style>\n <Style id=\"yellowLineGreenPoly\">\n <LineStyle>\n <color>7f00ffff</color>\n <width>4</width>\n </LineStyle>\n <PolyStyle>\n <color>7f00ff00</color>\n </PolyStyle>\n </Style>\n <Style id=\"thickBlackLine\">\n <LineStyle>\n <color>87000000</color>\n <width>10</width>\n </LineStyle>\n </Style>\n <Style id=\"redLineBluePoly\">\n <LineStyle>\n <color>ff0000ff</color>\n </LineStyle>\n <PolyStyle>\n <color>ffff0000</color>\n </PolyStyle>\n </Style>\n <Style id=\"blueLineRedPoly\">\n <LineStyle>\n <color>ffff0000</color>\n </LineStyle>\n <PolyStyle>\n <color>ff0000ff</color>\n </PolyStyle>\n </Style>\n <Style id=\"transRedPoly\">\n <LineStyle>\n <width>1.5</width>\n </LineStyle>\n <PolyStyle>\n <color>7d0000ff</color>\n </PolyStyle>\n </Style>\n <Style id=\"transBluePoly\">\n <LineStyle>\n <width>1.5</width>\n </LineStyle>\n <PolyStyle>\n <color>7dff0000</color>\n </PolyStyle>\n </Style>\n <Style id=\"transGreenPoly\">\n <LineStyle>\n <width>1.5</width>\n </LineStyle>\n <PolyStyle>\n <color>7d00ff00</color>\n </PolyStyle>\n </Style>\n <Style id=\"transYellowPoly\">\n <LineStyle>\n <width>1.5</width>\n </LineStyle>\n <PolyStyle>\n <color>7d00ffff</color>\n </PolyStyle>\n </Style>\n <Style id=\"noDrivingDirections\">\n <BalloonStyle>\n <text><![CDATA[\n <b>$[name]</b>\n <br /><br />\n $[description]\n ]]></text>\n </BalloonStyle>\n </Style>";
    private final static String preBody4FromFoldersToEnd = "\n  </Document>\n</kml>";

    private final static String folderOpenTag = "\n" +
            "    <Folder>";
    private final static String folderClosingTag = "\n" +
            "    </Folder>";

    public final static String getKmlForPois(String description, Collection<PointOfInterest> pois, Collection<GpsPosition> path, GpsPosition currentPostition, Polygon searchRange) {

        String kml = postBody1TillName
                + stripNonValidXMLCharacters(description)
                + postBody2FromNameToFolders;

        PointOfInterest currentPosition=new PointOfInterest("CurrentPostion", "Current position", currentPostition);
        List<PointOfInterest> cur=new ArrayList<>();
        cur.add(currentPosition);

        if (pois != null) {
            if (pois.isEmpty() == false) {
                kml += getPoiFolder(pois);
            }
        }
        if (pois != null) {
            if (pois.isEmpty() == false) {
                kml += getPoiFolder(cur);
            }
        }
        if (path != null) {
            if (path.isEmpty() == false) {
                kml += getTrackFolder(path);
            }
        }
        if (searchRange != null) {
            if (searchRange.npoints > 2) {
                kml += getPolygonFolder(searchRange);
            }
        }


        kml += preBody4FromFoldersToEnd;

        return kml;
    }

    private static String getPoiFolder(Collection<PointOfInterest> pois) {

        String folder;
        String placemarks = "";
        List<GpsPosition> positions = new ArrayList<>();

        for (PointOfInterest poi : pois) {

            positions.add(poi.getGpsPosition());

            placemarks += "\n<Placemark>\n <name>"
                    + stripNonValidXMLCharacters(poi.getName())
                    + "</name>\n <description>"
                    + stripNonValidXMLCharacters(poi.getInformationAbstract())
                    + "</description>\n <Point>\n <coordinates>"
                    + poi.getGpsPosition().getLongitude() + "," + poi.getGpsPosition().getLatitude() +
                    ",0</coordinates>\n </Point>\n </Placemark>";
        }

        folder = folderOpenTag;
        folder += getLookAtTag(positions);
        folder += placemarks;
        folder += folderClosingTag;

        return folder;

    }

    private static String getTrackFolder(Collection<GpsPosition> path) {
        String folder;

        String lineString = "<LineString>\n" +
                "          <tessellate>1</tessellate>\n" +
                "          <coordinates> ";

        for (GpsPosition position : path) {
            lineString += position.getLongitude() + "," + position.getLatitude() + ",0\n";
        }
        lineString += " </coordinates>\n" +
                "        </LineString>\n" +
                "      </Placemark>";


        folder = folderOpenTag;
        folder += "<name>Paths</name>\n" +
                "      <visibility>1</visibility>\n" +
                "      <description>The given Gps Coordinates</description>\n" +
                "      <Placemark>\n" +
                "        <name>Tessellated</name>\n" +
                "        <visibility>1</visibility>\n" +
                "        <description></description>\n" +
                "        ";
        folder += getLookAtTag(path);
        folder += lineString;
        folder += folderClosingTag;

        return folder;

    }



    private static String getPolygonFolder(Polygon searchRange) {

        String folder;
        List<GpsPosition> positions = new ArrayList<>();

        String linearRing =

                "        <Placemark>\n" +
                        "          <name>Sight</name>\n" +
                        "          <visibility>1</visibility>\n" +
                        "          <styleUrl>#transRedPoly</styleUrl>\n" +
                        "          <Polygon>\n" +
                        "            <extrude>10</extrude>\n" +
                        "            <altitudeMode>relativeToGround</altitudeMode>\n" +
                        "            <outerBoundaryIs>\n" +
                        "              <LinearRing>\n" +
                        "                <coordinates> ";

        int[] x = searchRange.xpoints;
        int[] y = searchRange.ypoints;


        for (int i = 0; i < x.length; i++) {
            GpsPosition position = new GpsPosition(x[i], y[i]);
            positions.add(position);

            linearRing += position.getLongitude() + "," + position.getLatitude() + ",0\n";
        }

        linearRing += " </coordinates>\n" +
                "              </LinearRing>\n" +
                "            </outerBoundaryIs>\n" +
                "          </Polygon>\n" +
                "        </Placemark>\n" +
                "      </Folder>\n";


        folder = folderOpenTag;
        folder += "      <name>Sight</name>\n" +
                "      <visibility>1</visibility>\n" +
                "      <description>The calculated sight out of the car.</description>\n" +
                "      <Folder>\n" +
                "        <name>Areal</name>\n" +
                "        <visibility>1</visibility>\n" +
                "        <description></description>\n";

        folder += getLookAtTag(positions);
        folder += linearRing;
        folder += folderClosingTag;

        return folder;
    }

    private static String getLookAtTag(Collection<GpsPosition> positions) {

        double maxLatitude = 0;
        double minLatitude = 0;
        double maxLongitude = 0;
        double minLongitude = 0;

        for (GpsPosition pos : positions) {

            double lon = pos.getLongitude();
            double lat = pos.getLatitude();

            //set extent
            if (maxLatitude == 0 || maxLatitude < lat)
                maxLatitude = lat;
            if (minLatitude == 0 || minLatitude > lat)
                minLatitude = lat;
            if (maxLongitude == 0 || maxLongitude < lon)
                maxLongitude = lon;
            if (minLongitude == 0 || minLongitude > lon)
                minLongitude = lon;

        }
        return " <name>POIs</name>\n <description>"
                + ""
                + "</description>\n <LookAt>\n <longitude>"
                + (maxLongitude + minLongitude) / 2
                + "</longitude>\n <latitude>"
                + (minLatitude + maxLatitude) / 2
                + "</latitude>\n <altitude>0</altitude>\n <heading>-148.4122922628044</heading>\n <tilt>40.5575073395506</tilt>\n <range>"
                + new GpsPosition(maxLongitude, maxLatitude).distanceTo(new GpsPosition(minLongitude, minLatitude))
                + "</range>\n </LookAt>\n";

    }

    private static String stripNonValidXMLCharacters(String t) {
        if(t!=null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < t.length(); i++) {
                char c = t.charAt(i);
                switch (c) {
                    case '<':
                        sb.append("&lt;");
                        break;
                    case '>':
                        sb.append("&gt;");
                        break;
                    case '\"':
                        sb.append("&quot;");
                        break;
                    case '&':
                        sb.append("&amp;");
                        break;
                    case '\'':
                        sb.append("&apos;");
                        break;
                    default:
                        if (c > 0x7e) {
                            sb.append("&#" + ((int) c) + ";");
                        } else
                            sb.append(c);
                }
            }
            return sb.toString();
        }
        return "";
    }
}