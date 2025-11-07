package com.uottawa.eecs.project_project_group_24;

public class Session {
    public enum Status { PENDING, APPROVED, REJECTED, CANCELED, COMPLETED }

    public String id;          // firebase key
    public String tutorId;
    public String studentId;
    public String studentName;
    public String courseCode;
    public long   startMillis; // 30 min
    public int    durationMin = 30;
    public Status status = Status.PENDING;

    public Session() {}
}
