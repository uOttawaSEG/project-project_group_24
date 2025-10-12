package com.uottawa.eecs.onlinetutoringappointmentmanagementsystem;

public interface IUser
{
    public boolean authenticateUser(String email, String pass);
    public void logOut();
}