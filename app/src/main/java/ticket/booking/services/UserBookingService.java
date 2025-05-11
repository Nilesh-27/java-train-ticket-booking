package ticket.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.util.UserServiceUtil;

import java.io.IOException;
import java.util.*;
import java.io.File;

public class UserBookingService {
    private User user;
    private List<User> userList;
    // Object Mapper is used map train class data with user data and others.
    private ObjectMapper objectMapper = new ObjectMapper();





    private static final String USERS_PATH = "app/src/main/java/ticket/booking/localDb/users.json";
    public UserBookingService(User user1) throws IOException {
        this.user=user1;
        loadUsers();
    }

    public Boolean loginUser(){
        Optional<User> foundUser = userList.stream().filter(user1 -> {
            return user1.getName().equalsIgnoreCase(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(),user1.getHashedPassword());

        }).findFirst();
            return foundUser.isPresent();
    }
    public Boolean signUp(User user1){
        try{
            userList.add(user1);
            saveUserListToFile();
            return Boolean.TRUE;
        }
        catch(IOException ex){
            return Boolean.FALSE;
        }
    }

    private void saveUserListToFile() throws IOException {
        File usersFile = new File(USERS_PATH);
        objectMapper.writeValue(usersFile, userList);
    }
    // json --> Object (User) --> Deserialize
    // Object (User) --> json --> Serialize

    public void fetchBooking(){
        //user.printTickets();
        Optional<User> userFetched = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();
        if(userFetched.isPresent()){
            userFetched.get().printTickets();
        }
    }
    public UserBookingService() throws IOException{
        loadUsers();
    }
    public List<User> loadUsers() throws IOException{
        File users = new File(USERS_PATH);

        return objectMapper.readValue(users, new TypeReference<List<User>>() {
        });
    }
    public Boolean cancelBooking(String ticketId){
        return Boolean.FALSE;
    }
    public List<Train> getTrains(String source, String destination){
        try{
            TrainService trainService= new TrainService();
            return trainService.searchTrains(source, destination);
        }
    }

}
