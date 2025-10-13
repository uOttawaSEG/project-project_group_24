package com.uottawa.eecs.project_project_group_24;

public interface IUser
{
    public boolean authenticateUser(String email, String pass);
    public void logOut();
}