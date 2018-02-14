package de.tuberlin.amos.ws17.swit.image_analysis;

import com.google.common.io.ByteStreams;
import de.tuberlin.amos.ws17.swit.common.DebugTF;
import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import org.apache.commons.lang3.text.WordUtils;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.Tensors;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class TFLandmarkClassifier implements LandmarkDetector {

    public static        boolean           debug               = true;
    private static final float             predictionThreshold = 0.5f;
    private              Graph             graph               = new Graph();
    private              Session           session             = new Session(graph);
    private final        List<String>      labels              = loadLabels();
    private final        List<GpsPosition> labelLocations      = loadLabelLocations();

    public TFLandmarkClassifier() throws IOException {
        graph.importGraphDef(loadGraphDef());
    }

    @Override
    public List<PointOfInterest> identifyPOIs(Path imagePath) throws IOException {
        byte[] data = Files.readAllBytes(imagePath);
        return identifyPOIs(data, null);
    }

    public List<PointOfInterest> identifyPOIs(BufferedImage image, GpsPosition gpsPosition) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
            byte[] data = baos.toByteArray();
            List<PointOfInterest> pois = identifyPOIs(data, gpsPosition);
            for (PointOfInterest p : pois) {
                p.setImage(image);
            }
            return pois;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public List<PointOfInterest> identifyPOIs(BufferedImage image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
            byte[] data = baos.toByteArray();
            List<PointOfInterest> pois = identifyPOIs(data, null);
            for (PointOfInterest p : pois) {
                p.setImage(image);
            }
            return pois;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private List<PointOfInterest> identifyPOIs(byte[] data, GpsPosition gpsPosition) {
        String name = labelImage(data, gpsPosition);
        if (name != null) {
            PointOfInterest poi = new PointOfInterest();
            poi.setName(name);
            return Collections.singletonList(poi);
        }
        return Collections.emptyList();
    }

    private float[] appendLocationWeight(float[] probabilities, GpsPosition currentPos) {
        // calculate distances
        float[] distances = new float[probabilities.length];
        for (int i = 0; i < labelLocations.size(); i++) {
            float distance = labelLocations.get(i).distanceTo(currentPos);
            distances[i] = distance;
        }

        int[] sortedIndices = IntStream.range(0, probabilities.length)
                .boxed().sorted((i, j) -> Float.compare(distances[i], distances[j]))
                .mapToInt(ele -> ele).toArray();

        // if highest probability and closest to current position
        // increase its probability
        if (sortedIndices[0] == probabilities[0]) {
            probabilities[0] *= 2;
        }

        // normalize
        float sum = sum(probabilities);
        for (int i = 0; i < probabilities.length; i++) {
            probabilities[i] = probabilities[i] / sum;
        }
        return probabilities;
    }

    private static float sum(float... values) {
        float result = 0;
        for (float value : values)
            result += value;
        return result;
    }

    private String labelImage(byte[] bytes, @Nullable GpsPosition gpsPosition) {
        float[][] probabilitiesArray;

        try (Tensor<String> input = Tensors.create(bytes);
             Tensor<Float> output =
                     session.runner()
                             .feed("DecodeJpeg/contents:0", input)
                             .fetch("final_result:0")
                             .run()
                             .get(0)
                             .expect(Float.class)) {
            probabilitiesArray = new float[(int) output.shape()[0]][(int) output.shape()[1]];
            output.copyTo(probabilitiesArray);
            float[] probabilities = probabilitiesArray[0];

            if (gpsPosition != null) {
                // add location weight
                probabilities = appendLocationWeight(probabilities, gpsPosition);

            }

            float[] finalProbabilities = probabilities;
            int[] sortedIndices = IntStream.range(0, probabilities.length)
                    .boxed().sorted((i, j) -> Float.compare(finalProbabilities[j], finalProbabilities[i]))
                    .mapToInt(ele -> ele).toArray();

            StringBuilder log = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                int index = sortedIndices[i];
                log.append(String.format("%.2f%%\t %s\n", probabilities[index] * 100.0, labels.get(index)));
            }

            if (debug) {
                DebugTF.log(log.toString());
            }
            float bestScore = probabilities[sortedIndices[0]];

            // threshold
            if (bestScore >= predictionThreshold) {
                return labels.get(sortedIndices[0]);
            }

            return null;
        }
    }

    private static byte[] loadGraphDef() throws IOException {
        try (InputStream is = TFLandmarkClassifier.class.getClassLoader().getResourceAsStream("graph.pb")) {
            return ByteStreams.toByteArray(is);
        }
    }

    private static ArrayList<String> loadLabels() throws IOException {
        ArrayList<String> labels = new ArrayList<>();
        String line;
        final InputStream is = TFLandmarkClassifier.class.getClassLoader().getResourceAsStream("labels.txt");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            while ((line = reader.readLine()) != null) {
                labels.add(WordUtils.capitalize(line));
            }
        }
        return labels;
    }

    private static ArrayList<GpsPosition> loadLabelLocations() throws IOException {
        ArrayList<GpsPosition> locations = new ArrayList<>();
        String line;
        final InputStream is = TFLandmarkClassifier.class.getClassLoader().getResourceAsStream("label_locations.txt");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            while ((line = reader.readLine()) != null) {
                String[] latLng = line.split(",");
                double lat = Double.valueOf(latLng[0]);
                double lng = Double.valueOf(latLng[1]);
                locations.add(new GpsPosition(lng, lat));
            }
        }

        return locations;
    }
}
