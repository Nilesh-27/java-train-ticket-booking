package ticket.booking;

import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.services.UserBookingService;
import ticket.booking.util.UserServiceUtil;

import java.io.IOException;
import java.sql.Time;
import java.util.*;

public class App {
    public static void main(String[] args) throws IOException {
        System.out.println("Running Train Booking System");
        Scanner scanner = new Scanner(System.in);
        int option = 0;
        UserBookingService userBookingService = null;
        Train trainSelectedForBooking = null;

        while (option != 7) {
            System.out.println("\nChoose option:");
            System.out.println("1. Sign up");
            System.out.println("2. Login");
            System.out.println("3. Fetch Bookings");
            System.out.println("4. Search Trains");
            System.out.println("5. Book a Seat");
            System.out.println("6. Cancel my Booking (Not Implemented)");
            System.out.println("7. Exit the App");

            try {
                option = scanner.nextInt();
                scanner.nextLine(); // consume newline
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number.");
                scanner.next(); // clear invalid input
                continue;
            }

            switch (option) {
                case 1:
                    System.out.print("Enter username: ");
                    String signupName = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String signupPass = scanner.nextLine();
                    User newUser = new User(signupName, signupPass,
                            UserServiceUtil.hashPassword(signupPass),
                            new ArrayList<>(), UUID.randomUUID().toString());
                    try {
                        new UserBookingService().signUp(newUser);
                        System.out.println("Signup successful!");
                    } catch (IOException e) {
                        System.out.println("Signup failed: " + e.getMessage());
                    }
                    break;

                case 2:
                    System.out.print("Enter username: ");
                    String loginName = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String loginPass = scanner.nextLine();
                    User loginUser = new User(loginName, loginPass,
                            UserServiceUtil.hashPassword(loginPass),
                            new ArrayList<>(), UUID.randomUUID().toString());
                    try {
                        userBookingService = new UserBookingService(loginUser);
                        System.out.println("Login successful!");
                    } catch (IOException e) {
                        System.out.println("Login failed: " + e.getMessage());
                    }
                    break;

                case 3:
                    if (userBookingService == null) {
                        System.out.println("Please login first.");
                        break;
                    }
                    userBookingService.fetchBooking();
                    break;

                case 4:
                    if (userBookingService == null) {
                        System.out.println("Please login first.");
                        break;
                    }
                    System.out.print("Enter source station: ");
                    String source = scanner.nextLine();
                    System.out.print("Enter destination station: ");
                    String destination = scanner.nextLine();
                    List<Train> trains = userBookingService.getTrains(source, destination);
                    if (trains.isEmpty()) {
                        System.out.println("No trains found.");
                        break;
                    }
                    for (int i = 0; i < trains.size(); i++) {
                        Train t = trains.get(i);
                        System.out.println("\n" + (i + 1) + ". Train ID: " + t.getTrainId());
                        for (Map.Entry<String, Time> entry : t.getStationTimes().entrySet()) {
                            System.out.println("Station: " + entry.getKey() + ", Time: " + entry.getValue());
                        }
                    }
                    System.out.print("Select a train by number: ");
                    int selection = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    if (selection >= 1 && selection <= trains.size()) {
                        trainSelectedForBooking = trains.get(selection - 1);
                        System.out.println("Train selected.");
                    } else {
                        System.out.println("Invalid selection.");
                    }
                    break;

                case 5:
                    if (userBookingService == null) {
                        System.out.println("Please login first.");
                        break;
                    }
                    if (trainSelectedForBooking == null) {
                        System.out.println("Please select a train first using option 4.");
                        break;
                    }
                    try {
                        List<List<Integer>> seats = userBookingService.fetchSeats(trainSelectedForBooking);
                        System.out.println("Available seats (0 = free, 1 = booked):");
                        for (int i = 0; i < seats.size(); i++) {
                            System.out.print("Row " + i + ": ");
                            for (int val : seats.get(i)) {
                                System.out.print(val + " ");
                            }
                            System.out.println();
                        }
                        System.out.print("Enter seat row: ");
                        int row = scanner.nextInt();
                        System.out.print("Enter seat column: ");
                        int col = scanner.nextInt();
                        scanner.nextLine(); // consume newline

                        boolean booked = userBookingService.bookTrainSeat(trainSelectedForBooking, row, col);
                        if (booked) {
                            System.out.println("Seat booked successfully!");
                        } else {
                            System.out.println("Seat could not be booked.");
                        }
                    } catch (IOException e) {
                        System.out.println("Error during booking: " + e.getMessage());
                    }
                    break;

                case 6:
                    System.out.println("Cancel booking feature not implemented yet.");
                    break;

                case 7:
                    System.out.println("Thank you for using the Train Booking System!");
                    break;

                default:
                    System.out.println("Please select a valid option between 1 and 7.");
            }
        }

        scanner.close();
    }
}
