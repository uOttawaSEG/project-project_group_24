package com.uottawa.eecs.project_project_group_24;

public class Administrator extends User{
    static FirebaseManager database = FirebaseManager.getInstance();
    static Queue<RegistrationRequest> PendingRequests; //should get list of all pending registration requests
    static Queue<RegistrationRequest> RejectedRequests; //should get list of all rejected registration requests

    public Administrator(String email, String pass) //Need to add login/sign up logic
    {
        super(email,pass);
    }

    public static void receiveRequest(User u, RegistrationRequest.Role role)
    {
        PendingRequests.Enqueue(new RegistrationRequest(u.getFirstName(),u.getLastName(),u.getEmail(), role));
    }

    public void registerRequest(Node<RegistrationRequest> n)
    {
        n.getElement().AcceptRequest();
        n.removeNode();
    }
    public void rejectRequest(Node<RegistrationRequest> n)
    {
        n.getElement().RejectRequest();
        RejectedRequests.Enqueue(n.removeNode().getElement());
    }

    public Queue<RegistrationRequest> getPendingRequests() {
        return PendingRequests;
    }

    public Queue<RegistrationRequest> getRejectedRequests() {
        return RejectedRequests;
    }

}
