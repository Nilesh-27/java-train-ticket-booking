package ticket.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.Ticket;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.util.UserServiceUtil;

import java.io.IOException;
import java.util.*;
import java.io.File;

public class UserBookingService {
    private User user;
    private List<User> userList;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String USERS_PATH = "app/src/main/java/ticket/booking/localDb/users.json";

    // Constructor for logged-in users
    public UserBookingService(User user1) throws IOException {
        this.user = user1;
        this.userList = loadUsers();
    }

    // Default constructor
    public UserBookingService() throws IOException {
        this.userList = loadUsers();
    }

    // Login check
    public Boolean loginUser() {
        Optional<User> foundUser = userList.stream().filter(user1 ->
                user1.getName().equalsIgnoreCase(user.getName()) &&
                        UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword())
        ).findFirst();
        return foundUser.isPresent();
    }

    // Sign up a new user
    public Boolean signUp(User user1) {
        try {
            userList.add(user1);
            saveUserListToFile();
            return Boolean.TRUE;
        } catch (IOException ex) {
            ex.printStackTrace();
            return Boolean.FALSE;
        }
    }

    // Save user list to file
    private void saveUserListToFile() throws IOException {
        File usersFile = new File(USERS_PATH);
        objectMapper.writeValue(usersFile, userList);
    }

    // Load users from JSON file
    public List<User> loadUsers() throws IOException {
        File usersFile = new File(USERS_PATH);
        if (!usersFile.exists()) {
            usersFile.getParentFile().mkdirs(); // Ensure directory exists
            usersFile.createNewFile();
            objectMapper.writeValue(usersFile, new ArrayList<User>());
        }
        return objectMapper.readValue(usersFile, new TypeReference<List<User>>() {});
    }

    // Fetch bookings for the current user
    public void fetchBooking() {
        Optional<User> userFetched = userList.stream().filter(user1 ->
                user1.getName().equals(user.getName()) &&
                        UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword())
        ).findFirst();

        if (userFetched.isPresent()) {
            userFetched.get().printTickets();
        } else {
            System.out.println("No bookings found or user not authenticated.");
        }
    }

    // Cancel a booking based on ticket ID
    public Boolean cancelBooking(String ticketId) {
        if (ticketId == null || ticketId.isEmpty()) {
            System.out.println("Ticket ID cannot be null or empty");
            return Boolean.FALSE;
        }

        boolean removed = user.getTicketsBooked().removeIf(ticket ->
                ticket.getTicketId().equals(ticketId)
        );

        if (removed) {
            System.out.println("Ticket with ID " + ticketId + " has been cancelled.");
            return Boolean.TRUE;
        } else {
            System.out.println("No ticket found with ID " + ticketId);
            return Boolean.FALSE;
        }
    }

    // Search trains between source and destination
    public List<Train> getTrains(String source, String destination) throws IOException {
        TrainService trainService = new TrainService();
        return trainService.searchTrains(source, destination);
    }

    // Fetch seat layout of a train
    public List<List<Integer>> fetchSeats(Train train) {
        return train.getSeats();
    }

    // Book a seat
    public Boolean bookTrainSeat(Train trainSelectedForBooking, int row, int seat) throws IOException {
        TrainService trainService = new TrainService();
        List<List<Integer>> seats = trainSelectedForBooking.getSeats();

        if (row >= 0 && row < seats.size() && seat >= 0 && seat < seats.get(row).size()) {
            if (seats.get(row).get(seat) == 0) {
                seats.get(row).set(seat, 1);
                trainSelectedForBooking.setSeats(seats);
                trainService.addTrain(trainSelectedForBooking);
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } else {
            return Boolean.FALSE;
        }
    }
}
