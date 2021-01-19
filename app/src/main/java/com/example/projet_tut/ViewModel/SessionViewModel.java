package com.example.projet_tut.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.projet_tut.Model.Discipline;
import com.example.projet_tut.Model.Group;
import com.example.projet_tut.Model.Student;

import java.util.ArrayList;

public class SessionViewModel extends AndroidViewModel {

    private Group group;
    private Discipline discipline;

    private MutableLiveData<ArrayList<Student>> missingStudents;
    private MutableLiveData<ArrayList<Student>> presentStudents;

    public SessionViewModel(@NonNull Application application) {
        super(application);
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Discipline getDiscipline() {
        return discipline;
    }

    public void setDiscipline(Discipline discipline) {
        this.discipline = discipline;
    }

    public MutableLiveData<ArrayList<Student>> getMissingStudents() {
        if(missingStudents == null) {
            missingStudents = new MutableLiveData<>();
        }
        return missingStudents;
    }

    public void setMissingStudents(ArrayList<Student> pMissingStudents) {
        if(missingStudents == null) {
            missingStudents = new MutableLiveData<>();
        }
        missingStudents.setValue(pMissingStudents);
    }

    public MutableLiveData<ArrayList<Student>> getPresentStudents() {
        if(presentStudents == null) {
            presentStudents = new MutableLiveData<>();
        }
        return presentStudents;
    }

    public void setPresentStudents(ArrayList<Student> pPresentStudents) {
        if(presentStudents == null) {
            presentStudents = new MutableLiveData<>();
        }
        presentStudents.setValue(pPresentStudents);
    }
}
