package com.uottawa.eecs.project_project_group_24;

public class AvailabilitySlot {
    public String id;            // Firebase key
    public String tutorId;       // tutorID（after can replace by loginID）
    public long startMillis;     // start time(UTC ms)
    public int durationMin = 30; // fixed 30 mins
    public String courseCode;    // e.g: "CSI2110"

    public String manualApproval;

    public AvailabilitySlot() {} // Firebase need no parameterless constructor
}

