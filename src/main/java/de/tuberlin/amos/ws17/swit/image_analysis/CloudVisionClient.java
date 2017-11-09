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

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class CloudVisionClient implements LandmarkDetector {

    private static final String CLOUD_VISION_API_KEY = "AIzaSyCDYEQsEKR6KXY23qbQC5Daih1RoW1vEho";

    private static final String APPLICATION_NAME = "Swit-Image-Analysis";

    private static final String LANDMARK_DETECTION_FEATURE = "LANDMARK_DETECTION";

    private static final Color[] HIGHLIGHT_COLORS = {Color.red, Color.green, Color.blue, Color.cyan, Color.yellow};

    private final Vision vision;

    public CloudVisionClient(Vision vision) {
        this.vision = vision;
    }

    public static Vision getVisionService() throws IOException, GeneralSecurityException {
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        VisionRequestInitializer requestInitializer = new VisionRequestInitializer(CLOUD_VISION_API_KEY);

        return new Vision.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, null)
                .setVisionRequestInitializer(requestInitializer)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private List<EntityAnnotation> sendRequest(Image image, int maxResults) throws IOException {
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
        return response.getLandmarkAnnotations();
    }

    @Override
    public List<LandmarkResult> identifyLandmark(Path path, int maxResults) throws IOException {
        byte[] data = Files.readAllBytes(path);
        Image image = new Image().encodeContent(data);
        return convertToLandmarkResults(sendRequest(image, maxResults));
    }

    @Override
    public List<LandmarkResult> identifyLandmarkUrl(String gcsUrl, int maxResults) throws IOException {
        ImageSource imageSource = new ImageSource()
                .setImageUri(gcsUrl);

        Image image = new Image()
                .setSource(imageSource);
        return convertToLandmarkResults(sendRequest(image, maxResults));
    }

    private List<LandmarkResult> convertToLandmarkResults(List<EntityAnnotation> annotations) {
        return annotations.stream()
                .map(LandmarkResult::fromEntityAnnotation)
                .collect(Collectors.toList());
    }

    private void highlightLandmarks(String path, List<String> names, List<String> descriptions, List<BoundingPoly> boundingPolies) throws IOException {
        BufferedImage img = ImageIO.read(new File(path));
        // Create a graphics context on the buffered image
        Graphics2D g2d = img.createGraphics();
        JFrame frame = new JFrame();

        int k = 0;
        for (BoundingPoly bd : boundingPolies) {
            // Draw on the buffered image
            g2d.setColor(HIGHLIGHT_COLORS[k]);
            g2d.setStroke(new BasicStroke(3));
            int npoints = bd.getVertices().size();
            int xpoints[] = new int[npoints];
            int ypoints[] = new int[npoints];
            int xmin = Integer.MAX_VALUE;
            int ymax = 0;
            for (int i = 0; i < npoints; i++) {
                xpoints[i] = bd.getVertices().get(i).getX();
                ypoints[i] = bd.getVertices().get(i).getY();
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
            g2d.drawString(names.get(k), xtext, ytext);
            showInfoBox(descriptions.get(k), img, frame, g2d);
            k++;
        }
        g2d.dispose();

        showImage(img, frame);
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

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        if (args.length != 1) {
            System.err.println("Missing imagePath argument.");
            System.err.println("Usage:");
            System.err.printf("\tjava %s imagePath\n", CloudVisionClient.class.getCanonicalName());
            System.exit(1);
        }
        Path imagePath = Paths.get(args[0]);

        CloudVisionClient app = new CloudVisionClient(getVisionService());
        List<LandmarkResult> results = app.identifyLandmark(imagePath, 5);
        printLandmarks(System.out, imagePath, results);

        if (results.size() == 0) {
            System.out.println("Could not detect any landmarks");
            return;
        }
        try {
            List<String> descriptions = app.getLandmarkDescriptions(results.stream().map(LandmarkResult::getId).collect(Collectors.toList()));

            app.highlightLandmarks(imagePath.toString(),
                    results.stream()
                            .map(LandmarkResult::getName)
                            .collect(Collectors.toList()),
                    descriptions,
                    results.stream()
                            .map(LandmarkResult::getBoundlingPoly)
                            .collect(Collectors.toList()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printLandmarks(PrintStream out, Path imagePath, List<LandmarkResult> landmarks) {
        out.printf("Labels for image %s:\n", imagePath);
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
