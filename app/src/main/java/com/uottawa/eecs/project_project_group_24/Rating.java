package com.uottawa.eecs.project_project_group_24;

public class Rating {
    String id;
    String tutorId;
    String studentId;
    String studentName;

    String sessId;
    int rating;

    public Rating(){

    }

    public Rating(String id){
        this.id = id;

    }

    public Rating(String id, String tutorId, String studentId, String studentName, String sessId){
        this.id = id;
        this.tutorId = tutorId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.sessId = sessId;

    }

    public Rating(String id, String tutorId, String studentId, String studentName, String sessId, int rating){
        this.id = id;
        this.tutorId = tutorId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.sessId = sessId;
        setRating(rating);
    }

    public void setRating(int rating){
        if(rating < 0 || rating > 100){
            this.rating = 100;
        }

        else{
            this.rating = rating;
        }
    }
}
