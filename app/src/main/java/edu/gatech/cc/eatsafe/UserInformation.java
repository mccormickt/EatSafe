package edu.gatech.cc.eatsafe;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserInformation {
    private String firstName;
    private String lastName;
    private String birthdate;
    private String email;
    private Map<String, Boolean> allergens;
    private ArrayList<String> friends; // list of friends and their allergens

    // Default constructor for firebase
    public UserInformation(){}

    public UserInformation(String firstName, String lastName, String birthdate, String email,
                           Map<String, Boolean> allergens, ArrayList<String> friends) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.email = email;
        this.allergens = allergens;
        this.friends = friends;
    }

    public void setFirstName(String newFirstName) {
        this.firstName = newFirstName;
    }

    public void setLastName(String newLastName) {
        this.lastName = newLastName;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public void setEmail(String newEmail) {
        this.email = newEmail;
    }

    public void setAllergens(Map<String, Boolean> newAllergens) {
        this.allergens = newAllergens;
    }

    public void setFriends(ArrayList<String> friends) { this.friends = friends;}

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getEmail() {
        return email;
    }

    public Map<String, Boolean> getAllergens() {
        return allergens;
    }

    public ArrayList<String> getFriends() { return friends; }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("firstName", firstName);
        result.put("lastName", lastName);
        result.put("email", email);
        result.put("birthdate", birthdate);
        result.put("allergens", allergens);
        result.put("friends", friends);

        return result;
    }
}
