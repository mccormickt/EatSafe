package edu.gatech.cc.eatsafe;

import java.util.ArrayList;
import java.util.Date;

public class UserInformation {
    private String firstName;
    private String lastName;
    private Date birthdate;
    private String email;
    private ArrayList<String> allergens;

    public UserInformation(String firstName, String lastName, Date birthdate, String email,
                           ArrayList<String> allergens) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.email = email;
        this.allergens = allergens;
    }

    public void setFirstName(String newFirstName) {
        this.firstName = newFirstName;
    }

    public void setLastName(String newLastName) {
        this.lastName = newLastName;
    }

    public void setBirthdate(Date newBirthdate) {
        this.birthdate = newBirthdate;
    }

    public void setEmail(String newEmail) {
        this.email = newEmail;
    }

    public void setAllergens(ArrayList<String> newAllergens) {
        this.allergens = newAllergens;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public String getEmail() {
        return email;
    }

    public ArrayList<String> getAllergens() {
        return allergens;
    }
}
