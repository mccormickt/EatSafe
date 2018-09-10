package edu.gatech.cc.eatsafe;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserDatabase {

//    private FirebaseDatabase database;
//    private DatabaseReference usersRef;
//
//    public UserDatabase() {
//        database = FirebaseDatabase.getInstance();
//        usersRef = database.getReference("users");
//    }

    public static void main(String[] args) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");
    }
}
