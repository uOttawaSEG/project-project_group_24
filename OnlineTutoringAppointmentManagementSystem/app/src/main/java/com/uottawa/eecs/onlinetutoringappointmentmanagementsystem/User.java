package com.uottawa.eecs.onlinetutoringappointmentmanagementsystem;

public abstract class User implements IUser
{
    String firstName,lastName,email;

    //Prompts User to create an account
    public User()
    {}
    //Calls on AuthenticateUser to login into account
    public User(String email, String pass)
    {}

    //Compare login info with database then log user in
    public boolean AuthenticateUser(String email,String pass)
    {}

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

}