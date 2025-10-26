package com.uottawa.eecs.project_project_group_24;

import java.util.Calendar;

public class Session {
    public enum SessStatus{PENDING,SCHEDULED,ONGOING,COMPLETE,CANCELLED}
    static long count;
    public final long id = count;
    public final Calendar time = Calendar.getInstance();
    Tutor tutor;
    Student student;
    SessStatus status;

    public Session(Tutor t, Student s)
    {
        tutor = t;
        student = s;
        status = SessStatus.PENDING;
    }
}
