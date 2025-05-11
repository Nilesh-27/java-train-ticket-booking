package ticket.booking.services;

import ticket.booking.entities.Train;

import java.util.List;

public class TrainService
{

    public List<Train> searchTrains(String source, String destination) {
        return trainList.stream().filter(train -> validTrain(train, source, destination))
    }

    private Object validTrain(Object train, String source, String destination) {
    }
}
