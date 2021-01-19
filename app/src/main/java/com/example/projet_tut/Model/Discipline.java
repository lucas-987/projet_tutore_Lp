package com.example.projet_tut.Model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Discipline {
    private String label;
    private int id;

    public Discipline(String label, int id) {
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
        if(obj instanceof Discipline) {
            Discipline d = (Discipline) obj;
            if(d.getId() == id) {
                return true;
            }
        }

        return false;
    }
}
