package com.rdev.bstrack.modals;

public class User {
    private String email;
    private String password;
    private String name;
    private String contact;
    private String gender;

    // Constructor
    public User(String email, String password, String name, String contact, String gender) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.contact = contact;
        this.gender = gender;
    }

    // Getters and Setters (Optional if using Gson)
}

