package com.uottawa.eecs.onlinetutoringappointmentmanagementsystem;

public class Student extends User
{
    String program;//enrolled program

    public Student(String email, String pass, boolean newuser)
    {
        super(email,pass);
        if (newuser) getDatabase().registerStudent
    }

    public void rateTutor(Tutor t){}

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }
}