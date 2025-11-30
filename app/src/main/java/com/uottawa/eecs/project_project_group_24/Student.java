package com.uottawa.eecs.project_project_group_24;

public class Student extends User
{
    String program;//enrolled program
    public Student(String email, String pass)
    {
        super(email,pass);

//        if (newuser){
//            setStatus(requestStatus.PendingTutor);
//            getDatabase().registerStudent(this,pass);}
        getDatabase().loginStudent(email,pass);
    }

    public void rateTutor(Tutor t, int rating, FirebaseManager.OpCallback callback){
        t.setAverageRating(rating, callback);
    }

    public String getProgram() {
        return program;
    }
    public void setProgram(String program) {
        this.program = program;
    }
}