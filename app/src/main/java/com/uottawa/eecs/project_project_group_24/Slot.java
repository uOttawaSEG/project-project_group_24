package com.uottawa.eecs.project_project_group_24;

import java.sql.Time;
import java.sql.Timestamp;

public class Slot {
    Tutor tutor;
    Time startTime,endTime;
    static int count = 0;
    String id;
    Timestamp time;
    Session session;
    public Slot(Tutor tutor, Timestamp time)
    {
        this.id = String.valueOf(count);
        count ++;
        this.tutor = tutor;
        this.time = time;

    }

    public Slot(Tutor tutor, Timestamp time, String id)
    {
        this.id = id;
        this.tutor = tutor;
        this.time = time;
    }

    public Time getStartTime() {
        return startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public void setTutor(Tutor tutor) {
        this.tutor = tutor;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }
}
