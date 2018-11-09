package edu.gatech.cc.eatsafe.Classes;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

public class UserInformation {
    public String firstName;
    public String lastName;
    public Date birthdate;
    public String email;
    public String password;
    public ArrayList<String> allergens;
    // private Firebase ref;

    public UserInformation(String firstName, String lastName, Date birthdate, String email,
                           String password, ArrayList<String> allergens) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.email = email;
        this.password = password;
        this.allergens = allergens;
        // Add to FireBase when that is up and running !
        // ref = new Firebase(Config.FIREBASE_URL); // either use global reference or this with our unique URL
        // ref.child("Name").setValue(firstName + " " + lastName);
        // OR
        // ref.child("First Name").setValue(firstName);
        // ref.child("Last Name").setValue(lastName);
        // ref.child("Birthdate").setValue(birthdate);
        // ref.child("Email").setValue(email);
        // ref.child("Password").setValue(password);
        // ref.child("Allergens").setValue(allergens); // can do it as a list or can parse it
    }

    public void setFirstName(String newFirstName) {
        firstName = newFirstName;
        // ref.child("First Name").setValue(firstName);
    }

    public void setLastName(String newLastName) {
        lastName = newLastName;
        // ref.child("Last Name").setValue(lastName);
    }

    public void setBirthdate(Date newBirthdate) {
        birthdate = newBirthdate;
        // ref.child("Birthdate").setValue(birthdate);
    }

    public void setEmail(String newEmail) {
        email = newEmail;
        // ref.child("Email").setValue(email);
    }

    public void setPassword(String newPassword) {
        password = newPassword;
        // ref.child("Password").setValue(password);
    }

    public void setAllergens(ArrayList<String> newAllergens) {
        allergens = newAllergens;
        // ref.child("Allergens").setValue(allergens);
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

    public String getPassword() {
        return password;
    }

    public ArrayList<String> getAllergens() {
        return allergens;
    }
}
