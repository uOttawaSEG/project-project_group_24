package com.uottawa.eecs.project_project_group_24;

public class Tutor extends User
{
    String degree;
    Course[] courses;
    Queue<Session> upcomingSessions,pastSessions,pendingSessions;
    Queue<Slot> availableSlots;
    int averageRating;
    int numOfRatings;
    public Tutor(String email, String pass, boolean newuser)
    {
        super(email, pass);
        if (newuser) {
            setStatus(requestStatus.PendingStudent);
            getDatabase().registerTutor(this, pass);
        }
        availableSlots = new Queue<>();
        FirebaseManager.getInstance().getSessions(email);
        //Should access database to initialize Session Queues
    }

    public void setAverageRating(int newRating){
        this.averageRating*=numOfRatings;
        this.averageRating+= newRating;
        this.numOfRatings++;
        this.averageRating/=numOfRatings;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public void receiveRequest(Session session)
    {

    }

    public Queue<Session> getPastSessions() {
        return pastSessions;
    }

    public Queue<Session> getPendingSessions() {
        return pendingSessions;
    }

    public Queue<Session> getUpcomingSessions() {
        return upcomingSessions;
    }

    public Queue<Slot> getAvailableSlots() {
        return availableSlots;
    }
}