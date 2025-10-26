package com.uottawa.eecs.project_project_group_24;

public class Student extends User
{
    String program;//enrolled program
    boolean newuser;
    public Student(String email, String pass, boolean newuser)
    {
        super(email,pass);
        this.newuser = newuser;

        if (newuser) getDatabase().registerStudent(this,pass);
        else getDatabase().loginStudent(email,pass);
    }

    public void rateTutor(Tutor t){}

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }
}