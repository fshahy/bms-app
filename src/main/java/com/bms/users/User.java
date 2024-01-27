package com.bms.users;

import java.util.UUID;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

public final class User {
    
    private final String id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;

    private User(String id, String firstName, String lastName, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    @JsonbCreator
    public static User of(@JsonbProperty("id") String id, @JsonbProperty("firstName") String firstName, 
                            @JsonbProperty("lastName") String lastName, @JsonbProperty("email") String email, 
                            @JsonbProperty("password") String password) {
        if (id == null || id.trim().toLowerCase().equals("")) {
            id = UUID.randomUUID().toString();
        }
        return new User(id, firstName, lastName, email, password);
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
    
}
