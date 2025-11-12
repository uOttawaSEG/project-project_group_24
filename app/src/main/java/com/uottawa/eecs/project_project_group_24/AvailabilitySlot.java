package com.uottawa.eecs.project_project_group_24;

public class AvailabilitySlot {
    public String id;            // Firebase key
    public String tutorId;       // tutorID（after can replace by loginID）
    public long startMillis;     // start time(UTC ms)
    public int durationMin = 30; // fixed 30 mins
    public String courseCode;    // e.g: "CSI2110"

    public String manualApproval;//yes or no
    public AvailabilitySlot() {} // Firebase need no parameterless constructor

    public void setId(String id) {
        this.id = id;
    }

    public void setDurationMin(int durationMin) {
        this.durationMin = durationMin;
    }

    public void setStartMillis(long startMillis) {
        this.startMillis = startMillis;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public void setManualApproval(String manualApproval) {
        this.manualApproval = manualApproval;
    }

    public int getDurationMin() {
        return durationMin;
    }

    public String getId() {
        return id;
    }

    public long getStartMillis() {
        return startMillis;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getManualApproval() {
        return manualApproval;
    }

    public String getTutorId() {
        return tutorId;
    }
}

