package com.uottawa.eecs.project_project_group_24;

public class User implements IUser
{
    FirebaseManager database = FirebaseManager.getInstance();
    String firstName,lastName,email;
    requestStatus status;
    //status temporary till requestStatus done
    public enum requestStatus {AcceptedStudent,RejectedStudent,PendingStudent,
        ApprovedTutor,RejectedTutor, PendingTutor};
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

    public requestStatus getStatus() {
        return status;
    }
    public void logOut(){}

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public void setStatus(requestStatus status) {
        this.status = status;
    }


    public FirebaseManager getDatabase() {
        return database;
    }
}