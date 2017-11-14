package de.tuberlin.amos.ws17.swit.image_analysis;


import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.*;
import com.google.api.services.vision.v1.model.Image;
import com.google.common.collect.ImmutableList;
import com.jayway.jsonpath.JsonPath;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static de.tuberlin.amos.ws17.swit.image_analysis.ImageUtils.getTestImageFile;

public class LandmarkDetectorImpl implements LandmarkDetector {

    private static final String CLOUD_VISION_API_KEY = "AIzaSyB-zEWlIkLba444D8LePxwoB3C4E766Uvo";

    private static final String APPLICATION_NAME = "Swit-Image-Analysis";

    private static final String LANDMARK_DETECTION_FEATURE = "LANDMARK_DETECTION";

    private static final Color[] HIGHLIGHT_COLORS = {Color.red, Color.green, Color.blue, Color.cyan, Color.yellow};

    private static LandmarkDetector instance;

    private final Vision vision;

    private List<LandmarkResult> landmarkResults = Collections.emptyList();

    private Image image;

    @Nullable
    public static LandmarkDetector getInstance() {
        if (instance == null) {
            try {
                instance = new LandmarkDetectorImpl(getVisionService());
            } catch (IOException | GeneralSecurityException e) {
                e.printStackTrace();
                return null;
            }
        }
        return instance;
    }

    private LandmarkDetectorImpl(Vision vision) {
        this.vision = vision;
    }

    private static Vision getVisionService() throws IOException, GeneralSecurityException {
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        VisionRequestInitializer requestInitializer = new VisionRequestInitializer(CLOUD_VISION_API_KEY);

        return new Vision.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, null)
                .setVisionRequestInitializer(requestInitializer)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    @Override
    public List<LandmarkResult> identifyLandmarks(BufferedImage bufferedImage, int maxResults) throws IOException {
        try {
            Image image = ImageUtils.convertToImage(bufferedImage);
            return identifyLandmarks(image, maxResults);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public List<LandmarkResult> identifyLandmarks(Path path, int maxResults) throws IOException {
        byte[] data = Files.readAllBytes(path);
        Image image = new Image().encodeContent(data);
        return identifyLandmarks(image, maxResults);
    }

    @Override
    public List<LandmarkResult> identifyLandmarkUrl(String gcsUrl, int maxResults) throws IOException {
        ImageSource imageSource = new ImageSource()
                .setImageUri(gcsUrl);

        Image image = new Image()
                .setSource(imageSource);
        return identifyLandmarks(image, maxResults);
    }

    private List<LandmarkResult> identifyLandmarks(Image image, int maxResults) throws IOException {
        this.image = image;
        AnnotateImageRequest request = new AnnotateImageRequest()
                .setImage(image)
                .setFeatures(ImmutableList.of(
                        new Feature()
                                .setType(LANDMARK_DETECTION_FEATURE)
                                .setMaxResults(maxResults)));

        Vision.Images.Annotate annotate =
                vision.images()
                        .annotate(new BatchAnnotateImagesRequest().setRequests(ImmutableList.of(request)));
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotate.setDisableGZipContent(true);

        BatchAnnotateImagesResponse batchResponse = annotate.execute();
        assert batchResponse.getResponses().size() == 1;
        AnnotateImageResponse response = batchResponse.getResponses().get(0);
        if (response.getLandmarkAnnotations() == null) {
            throw new IOException(
                    response.getError() != null
                            ? response.getError().getMessage()
                            : "Unknown error getting image annotations");
        }
        landmarkResults = convertToLandmarkResults(response.getLandmarkAnnotations());
        return landmarkResults;
    }

    private List<LandmarkResult> convertToLandmarkResults(List<EntityAnnotation> annotations) {
        return annotations.stream()
                .map(LandmarkResult::fromEntityAnnotation)
                .collect(Collectors.toList());
    }

    @Override
    public void showHighlightedLandmarks() {
        BufferedImage img = ImageUtils.convertToBufferedImage(this.image);
        // Create a graphics context on the buffered image
        Graphics2D g2d = img.createGraphics();
        JFrame frame = new JFrame();

        int k = 0;
        for (LandmarkResult result: landmarkResults) {
            BoundingPoly bp = result.getBoundlingPoly();
            // Draw on the buffered image
            g2d.setColor(HIGHLIGHT_COLORS[k]);
            g2d.setStroke(new BasicStroke(3));
            int npoints = bp.getVertices().size();
            int xpoints[] = new int[npoints];
            int ypoints[] = new int[npoints];
            int xmin = Integer.MAX_VALUE;
            int ymax = 0;
            for (int i = 0; i < npoints; i++) {
                xpoints[i] = bp.getVertices().get(i).getX();
                ypoints[i] = bp.getVertices().get(i).getY();
                if (ypoints[i] > ymax)
                    ymax = ypoints[i];
                if (xpoints[i] < xmin) {
                    xmin = xpoints[i];
                }
            }

            g2d.drawPolygon(xpoints, ypoints, npoints);
            int xtext = xmin + 10;
            int ytext = ymax - 10;
            g2d.setFont(new Font("Serif", Font.BOLD, 16));
            g2d.drawString(result.getName(), xtext, ytext);
            k++;
        }
        g2d.dispose();

        ImageUtils.showImage(img, frame);
    }

    private void showInfoBox(String description, BufferedImage img, JFrame frame, Graphics2D g2d) {
        String desc = "<html><body style='width: 200px; padding: 5px;'>" + description;
        JLabel textLabel = new JLabel(desc);
        textLabel.setSize(textLabel.getPreferredSize());
        Dimension d = textLabel.getPreferredSize();
        BufferedImage bi = new BufferedImage(
                d.width,
                d.height,
                BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.createGraphics();
        g.setColor(new Color(255, 255, 255, 128));
        g.fillRoundRect(
                0,
                0,
                d.width,
                d.height,
                15,
                10);
        g.setColor(Color.black);
        textLabel.paint(g);
        g2d.drawImage(bi, img.getWidth() / 2, 20, frame);
    }

<<<<<<< HEAD:src/main/java/de/tuberlin/amos/ws17/swit/image_analysis/CloudVisionClient.java
    private void showImage(BufferedImage img, JFrame frame) {
        ImageIcon icon = new ImageIcon(img);
        frame.setLayout(new FlowLayout());
        frame.setSize(img.getWidth(), img.getHeight());
        JLabel lbl = new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static String getLandmark(Path imagePath) throws IOException {
        CloudVisionClient app = null;
        try {
            app = new CloudVisionClient(getVisionService());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        List<LandmarkResult> results = app.identifyLandmark(imagePath, 5);
        return results.get(0).getName();
    }

=======
>>>>>>> Add option to analyze images captured from camera:src/main/java/de/tuberlin/amos/ws17/swit/image_analysis/LandmarkDetectorImpl.java
    public static void main(String[] args) throws IOException, GeneralSecurityException {
        LandmarkDetector landmarkDetector = getInstance();
        if (landmarkDetector != null) {
            landmarkDetector.identifyLandmarks(getTestImageFile("brandenburger-tor.jpg"), 3);
            landmarkDetector.showHighlightedLandmarks();
        }
    }

    private static void printLandmarks(PrintStream out, List<LandmarkResult> landmarks) {
        for (LandmarkResult result : landmarks) {
            out.printf(
                    "\t%s (score: %.3f)\n",
                    result.getName(),
                    result.getScore());
        }
        if (landmarks.isEmpty()) {
            out.println("\tNo landmarks found.");
        }
    }

    // get detailed descriptions using google knowledge graph api
    private List<String> getLandmarkDescriptions(List<String> ids) {
        Collections.reverse(ids);
        try {
            HttpTransport httpTransport = new NetHttpTransport();
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
            JSONParser parser = new JSONParser();
            GenericUrl url = new GenericUrl("https://kgsearch.googleapis.com/v1/entities:search");
            for (String id : ids) {
                url.put("ids", id);
            }
            url.put("limit", "10");
            url.put("indent", "true");
            url.put("key", CLOUD_VISION_API_KEY);
            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse httpResponse = request.execute();
            JSONObject response = (JSONObject) parser.parse(httpResponse.parseAsString());
            JSONArray elements = (JSONArray) response.get("itemListElement");
            List<String> descriptions = new ArrayList<>();
            for (Object element : elements) {
                String detailedDescription = JsonPath.read(element, "$.result.detailedDescription.articleBody").toString();
                descriptions.add(detailedDescription);
                System.out.println(detailedDescription);
            }
            return descriptions;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Collections.emptyList();
    }
}
