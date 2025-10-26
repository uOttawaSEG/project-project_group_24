package com.uottawa.eecs.project_project_group_24;

public class Administrator extends User{
    static FirebaseManager database = FirebaseManager.getInstance();
    static Queue<RegistrationRequest> PendingRequests;
    static Queue<RegistrationRequest> RejectedRequests;

    public Administrator() //Need to add login/sign up logic
    {
        PendingRequests = new Queue<>();
        RejectedRequests = new Queue<>();
    }

    public void registerRequest(RegistrationRequest req)
    {

    }

    public Queue<RegistrationRequest> getPendingRequests() {
        return PendingRequests;
    }

    public Queue<RegistrationRequest> getRejectedRequests() {
        return RejectedRequests;
    }

}
