//package com.uottawa.eecs.onlinetutoringappointmentmanagementsystem;

public class Tutor extends User
{
    String degree;
    Course[] courses;
    public Tutor(String email, String pass)
    {
        super();
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }
}