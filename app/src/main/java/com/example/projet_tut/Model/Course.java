package com.example.projet_tut.Model;

public class Course {
    private String disciplineName;
    private String groupeName;
    private String id;

    public Course(String disciplineName, String groupeName, String id) {
        this.disciplineName = disciplineName;
        this.groupeName = groupeName;
        this.id = id;
    }

    public String getDisciplineName() {
        return disciplineName;
    }

    public String getGroupeName() {
        return groupeName;
    }

    public String getId() {
        return id;
    }
}
