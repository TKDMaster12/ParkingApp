package com.parking.parkingapp;

public class User {
    String name, username, password, email;

    public User (String name, String email, String username, String password)
    {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public User (String username, String password)
    {
        this.username = username;
        this.password = password;
        this.email = "";
        this.name = "";
    }

    public User (String username, String password, String email)
    {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = "";
    }

    public User (String email)
    {
        this.name = "";
        this.email = email;
        this.username = "";
        this.password = "";
    }
}
