package com.uottawa.eecs.project_project_group_24;

public class Tutor extends User
{
    String degree;
    Course[] courses;
    public Tutor(String email, String pass, boolean newuser)
    {
        super();
        if (newuser) {
            setStatus(requestStatus.PendingStudent);
            getDatabase().registerTutor(this, pass);
        }
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }


}