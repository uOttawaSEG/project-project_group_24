package com.uottawa.eecs.project_project_group_24;

import java.sql.Time;

public class Slot {
    Tutor tutor;
    Time startTime,endTime;
    public Slot(Tutor tutor, Time startTime)
    {
        this.tutor = tutor;
        this.startTime = startTime;
        this.endTime = new Time(startTime.getTime()+(1000*30));
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
}
