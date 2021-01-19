package com.example.projet_tut.Model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

// TODO create a parent abstract class for this and Discipline

public class Group {
    private String label;
    private int id;

    public Group(String label, int id) {
        this.label = label;
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public int getId() {
        return id;
    }

    @NonNull
    @Override
    public String toString() {
        return label;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof Group) {
            Group g = (Group)obj;
            if(g.getId() == id) {
                return true;
            }
        }

        return false;
    }
}
