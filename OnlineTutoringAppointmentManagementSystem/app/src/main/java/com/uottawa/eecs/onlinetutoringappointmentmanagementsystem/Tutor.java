package com.uottawa.eecs.onlinetutoringappointmentmanagementsystem;

public class Tutor extends User
{
    String degree;
    Course[5] courses
    public Tutor(String email, String pass)
    {
        super()
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }
}