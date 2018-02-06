package de.tuberlin.amos.ws17.swit.image_analysis;

import com.google.common.io.ByteStreams;
import de.tuberlin.amos.ws17.swit.common.DebugTF;
import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import javafx.application.Platform;
import org.apache.jena.base.Sys;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.Tensors;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class TFLandmarkClassifier implements LandmarkDetector {

    private       Graph        graph   = new Graph();
    private       Session      session = new Session(graph);
    private final List<String> labels  = loadLabels();

    public TFLandmarkClassifier() throws IOException {
        graph.importGraphDef(loadGraphDef());
    }

    @Override
    public List<PointOfInterest> identifyPOIs(Path imagePath) throws IOException {
        byte[] data = Files.readAllBytes(imagePath);
        return identifyPOIs(data);
    }

    @Override
    public List<PointOfInterest> identifyPOIs(BufferedImage image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
            byte[] data = baos.toByteArray();
            List<PointOfInterest> pois = identifyPOIs(data);
            for (PointOfInterest p : pois) {
                p.setImage(image);
            }
            return pois;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }


    private List<PointOfInterest> identifyPOIs(byte[] data) {
        String name = labelImage(data);
        if (name != null) {
            PointOfInterest poi = new PointOfInterest();
            poi.setName(name);
            return Collections.singletonList(poi);
        }
        return Collections.emptyList();
    }

    public void labelImage(String filename) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(filename));
        labelImage(bytes);
    }

    private String labelImage(byte[] bytes) {
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

            int[] sortedIndices = IntStream.range(0, probabilities.length)
                    .boxed().sorted((i, j) -> Float.compare(probabilities[j], probabilities[i]))
                    .mapToInt(ele -> ele).toArray();

            StringBuilder log = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                int index = sortedIndices[i];
                log.append(String.format("%-15s (%.2f%% likely)\n", labels.get(index), probabilities[index] * 100.0));
            }
            Platform.runLater(() -> DebugTF.log(log.toString()));
            float bestScore = probabilities[sortedIndices[0]];

            // threshold
            if (bestScore >= 0.6) {
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
                labels.add(line);
            }
        }
        return labels;
    }
}
