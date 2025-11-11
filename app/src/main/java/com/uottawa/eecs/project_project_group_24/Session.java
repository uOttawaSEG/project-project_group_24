package com.uottawa.eecs.project_project_group_24;

import android.util.Log;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

public class Session {
    public enum Status { PENDING, APPROVED, REJECTED, CANCELLED, COMPLETED }

    public String id;          // firebase key
    public String tutorId;
    public String studentId;
    public String studentName;
    public String courseCode;

//    public Timestamp time;
    public long   startMillis; // 30 min
    public int    durationMin = 30;
    public Status status = Status.PENDING;
    public static int sessionCount = 0;

    public Session() {
        this.id = String.valueOf(sessionCount);
        sessionCount++;
    }

    public Session(String id) {
        this.id = id;
    }

    public Session(String tutorId, String studentId, String studentName, String courseCode, Timestamp time, String status){
        this.id = String.valueOf(sessionCount);
        sessionCount++;
        this.tutorId = tutorId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.courseCode = courseCode;
//        this.time = time;
        Log.d("OTA_SESSION",String.valueOf(time.getTime()));
//        Log.d("OTA_SESSION",String.valueOf(time.getTime()));
        this.startMillis = time.getTime();

        setStatus(status);
    }

    public Session(String id, String tutorId, String studentId, String studentName, String courseCode, Timestamp time, String status){
        this.id = id;
        this.tutorId = tutorId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.courseCode = courseCode;
//        this.time = time;
        this.startMillis = time.getTime();

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

    public void setId(String id) {
        this.id = id;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public void setStartMillis(long startMillis) {
        this.startMillis = startMillis;
    }

    public void setDurationMin(int durationMin) {
        this.durationMin = durationMin;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    public static void setSessionCount(int sessionCount) {
        Session.sessionCount = sessionCount;
    }

//    public void setStatus(Status status) {
//        this.status = status;
//    }

    public static int getSessionCount() {
        return sessionCount;
    }

    public int getDurationMin() {
        return durationMin;
    }

//    public String getId() {
//        return id;
//    }
//
//    public String getTutorId() {
//        return tutorId;
//    }

    public String getStudentId() {
        return studentId;
    }

    public long getStartMillis() {
        return startMillis;
    }

    public Status getStatus() {
        return status;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getStudentName() {
        return studentName;
    }

}
