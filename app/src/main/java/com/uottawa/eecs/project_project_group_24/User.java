package com.uottawa.eecs.project_project_group_24;

import java.util.Map;

public class User implements IUser
{
    FirebaseManager database = FirebaseManager.getInstance();
    String firstName,lastName,email,password;
    requestStatus status;
    //status temporary till requestStatus done
    public enum requestStatus {AcceptedStudent,RejectedStudent,PendingStudent,
        AcceptedTutor,RejectedTutor, PendingTutor};
    long phoneNumber;
    private boolean requested_register;


    //Calls on AuthenticateUser to login into account
    public User(String email, String pass)
    {
        this.email = email;
        this.password = pass;
        Map<String,Object> tmp = database.getUserData(email);
        firstName = String.valueOf(tmp.get("firstName"));
        lastName = String.valueOf(tmp.get("lastName"));
        phoneNumber = (long) tmp.get("phoneNumber");



    }
    //Compare login info with database then log user in
    public boolean authenticateUser(String email,String pass)
    {return true;}//TODO

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
    public void StudentRegister()
    {
        Administrator.receiveRequest(this,RegistrationRequest.Role.STUDENT);
    }
    public void TutorRegister()
    {
        Administrator.receiveRequest(this,RegistrationRequest.Role.TUTOR);
    }

}