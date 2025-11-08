package com.uottawa.eecs.project_project_group_24;

public class Session {
    public enum Status { PENDING, APPROVED, REJECTED, CANCELLED, COMPLETED }

    public String id;          // firebase key
    public String tutorId;
    public String studentId;
    public String studentName;
    public String courseCode;
    public long   startMillis; // 30 min
    public int    durationMin = 30;
    public Status status = Status.PENDING;

    public static int sessionCount = 0;

    public Session() {}

    public Session(String tutorId, String studentId, String studentName, String courseCode, long startMillis, String status){
        this.tutorId = tutorId;
        setStatus(status);
    }

    public void setStatus(String status){


        if(status.equalsIgnoreCase("approved")){
            this.status = Status.APPROVED;
        }

        else if(status.equalsIgnoreCase("rejected")){
            this.status = Status.REJECTED;
        }

        else if(status.equalsIgnoreCase("cancelled")){
            this.status = Status.CANCELLED;
        }

        else if(status.equalsIgnoreCase("completed")){
            this.status = Status.COMPLETED;
        }

        else{
            this.status = Status.PENDING;

        }
    }




}
