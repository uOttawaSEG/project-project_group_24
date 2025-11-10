package com.uottawa.eecs.project_project_group_24;

import java.sql.Time;

public class Slot {
    Tutor tutor;
    Time startTime,endTime;
    Session session;
    String manualApproval;
    public Slot(Tutor tutor, Time startTime, String manualApproval)
    {
        this.tutor = tutor;
        this.startTime = startTime;
        this.endTime = new Time(startTime.getTime()+(1000*30));
        this.manualApproval = manualApproval;
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
