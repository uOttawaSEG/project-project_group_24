package com.uottawa.eecs.project_project_group_24;

public abstract class User implements IUser
{
    String firstName,lastName,email;
    long phoneNumber;

    //Prompts User to create an account
    public User()
    {}
    //Calls on AuthenticateUser to login into account
    public User(String email, String pass)
    {

    }
    //Compare login info with database then log user in
    public boolean authenticateUser(String email,String pass)
    {return true; }//TODO

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }
    public void logOut(){}

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}