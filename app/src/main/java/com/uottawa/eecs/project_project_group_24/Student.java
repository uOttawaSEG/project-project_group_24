package com.uottawa.eecs.project_project_group_24;

public class Student extends User
{
    String program;//enrolled program

    public Student(String email, String pass)
    {
        super(email,pass);
    }

    public void rateTutor(Tutor t){}

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }
}