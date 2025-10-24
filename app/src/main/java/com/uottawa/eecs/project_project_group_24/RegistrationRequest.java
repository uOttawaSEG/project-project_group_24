package com.uottawa.eecs.project_project_group_24;

import java.util.List;

public class RegistrationRequest {
    public enum Role { STUDENT, TUTOR }
    public enum Status { PENDING, REJECTED, APPROVED }

    public String id;
    public Role role;
    public String firstName, lastName, email, phone;
    // Student:
    public String programOfStudy;
    // Tutor:
    public String highestDegree;
    public List<String> coursesOffered;

    public Status status = Status.PENDING;

    public String fullName() { return firstName + " " + lastName; }
}

