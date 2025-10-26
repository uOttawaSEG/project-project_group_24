package com.uottawa.eecs.project_project_group_24;

import java.util.List;

/**
 * Firestore-friendly data model for registration requests.
 * collection: "registrationRequests"
 */
public class RegistrationRequest {

    public enum Role { STUDENT, TUTOR }
    public enum Status { PENDING, REJECTED, APPROVED }

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    private Role role;             // "STUDENT" / "TUTOR"
    private String programOfStudy;   // Student
    private String highestDegree;    // Tutor
    private List<String> coursesOffered;

    private Status status;           // "PENDING", "REJECTED", "APPROVED"


    public RegistrationRequest(String firstName, String lastName, String email, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.status = Status.PENDING;
    }

    public void AcceptRequest()
    {
        status = Status.APPROVED;
    }

    //in case administrator wants to see previously approved or rejected requests
    public RegistrationRequest(String firstName, String lastName, String email, String role, String status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    public void RejectRequest()
    {
        status = Status.REJECTED;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role.name(); }
    public void setRole(String role) { this.role = Role.valueOf(role); }

    public String getProgramOfStudy() { return programOfStudy; }
    public void setProgramOfStudy(String programOfStudy) { this.programOfStudy = programOfStudy; }

    public String getHighestDegree() { return highestDegree; }
    public void setHighestDegree(String highestDegree) { this.highestDegree = highestDegree; }

    public List<String> getCoursesOffered() { return coursesOffered; }
    public void setCoursesOffered(List<String> coursesOffered) { this.coursesOffered = coursesOffered; }

    public String getStatus() { return status.name(); }
    public void setStatus(String status) { this.status = Status.valueOf(status); }

    // ========= Helper =========
    public String getFullName() {
        return (firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName);
    }

    @Override
    public String toString() {
        return getFullName() + " (" + role + ") - " + status;
    }
}
