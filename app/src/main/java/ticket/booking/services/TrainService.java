package ticket.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.Train;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrainService {
    private List<Train> trainList;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String TRAIN_DB_PATH = "app/src/main/java/ticket/booking/localDb/trains.json";

    // Constructor
    public TrainService() throws IOException {
        File trainFile = new File(TRAIN_DB_PATH);
        if (!trainFile.exists()) {
            trainFile.getParentFile().mkdirs(); // Ensure the directory exists
            objectMapper.writeValue(trainFile, List.of()); // Initialize with empty list
        }
        trainList = objectMapper.readValue(trainFile, new TypeReference<List<Train>>() {});
    }

    // Search trains by source and destination
    public List<Train> searchTrains(String source, String destination) {
        return trainList.stream()
                .filter(train -> validTrain(train, source, destination))
                .collect(Collectors.toList());
    }

    // Validate source-destination route
    private boolean validTrain(Train train, String source, String destination) {
        List<String> stations = train.getStations().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        int sourceIndex = stations.indexOf(source.toLowerCase());
        int destIndex = stations.indexOf(destination.toLowerCase());
        return sourceIndex != -1 && destIndex != -1 && sourceIndex < destIndex;
    }

    // Add or update train
    public void addTrain(Train newTrain) {
        Optional<Train> existingTrain = trainList.stream()
                .filter(train -> train.getTrainId().equalsIgnoreCase(newTrain.getTrainId()))
                .findFirst();

        if (existingTrain.isPresent()) {
            updateTrain(newTrain); // Overwrite if exists
        } else {
            trainList.add(newTrain);
            saveTrainListToFile();
        }
    }

    // Update train info
    public void updateTrain(Train updatedTrain) {
        OptionalInt indexOpt = IntStream.range(0, trainList.size())
                .filter(i -> trainList.get(i).getTrainId().equalsIgnoreCase(updatedTrain.getTrainId()))
                .findFirst();

        if (indexOpt.isPresent()) {
            trainList.set(indexOpt.getAsInt(), updatedTrain);
            saveTrainListToFile();
        } else {
            addTrain(updatedTrain);
        }
    }

    // Save train list to JSON file
    private void saveTrainListToFile() {
        try {
            objectMapper.writeValue(new File(TRAIN_DB_PATH), trainList);
        } catch (IOException e) {
            System.err.println("Failed to save train list to file: " + e.getMessage());
        }
    }
}
