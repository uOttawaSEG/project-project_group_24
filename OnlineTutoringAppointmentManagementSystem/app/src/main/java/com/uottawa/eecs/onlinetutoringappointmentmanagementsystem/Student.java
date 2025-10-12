package com.uottawa.eecs.onlinetutoringappointmentmanagementsystem;

public class Student extends User
{
    String program;//enrolled program

    public Student(String email, String pass)
    {
        super(email,pass);
    }

    public void rateTutor(Tutor t){}
}