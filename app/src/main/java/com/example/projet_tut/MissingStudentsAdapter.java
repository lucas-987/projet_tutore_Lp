package com.example.projet_tut;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet_tut.Model.Student;
import com.example.projet_tut.ViewModel.SessionViewModel;

import java.util.ArrayList;
import java.util.List;

public class MissingStudentsAdapter extends RecyclerView.Adapter<MissingStudentsAdapter.MissingStudentHolder> {

    private List<Student> studentsList = new ArrayList<>();

    private SessionViewModel sessionViewModel;

    public MissingStudentsAdapter(SessionViewModel pSessionViewModel) {
        super();
        sessionViewModel = pSessionViewModel;
    }

    @NonNull
    @Override
    public MissingStudentsAdapter.MissingStudentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.missing_student_recycler_item, parent, false);
        return new MissingStudentsAdapter.MissingStudentHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MissingStudentHolder holder, int position) {
        Student currentStudent = studentsList.get(position);

        holder.firstnameTextView.setText(currentStudent.getFirstname());
        holder.lastnameTextView.setText(currentStudent.getLastname());
    }

    @Override
    public int getItemCount() {
        return studentsList.size();
    }

    public void setStudentsList(List<Student> students) {
        studentsList = students;
        notifyDataSetChanged();
    }

    class MissingStudentHolder extends RecyclerView.ViewHolder {
        private TextView firstnameTextView;
        private TextView lastnameTextView;
        private Button passPresentButton;

        private View.OnClickListener buttonClicked = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(Student student : studentsList) {
                    if(student.getFirstname().equals(firstnameTextView.getText().toString())
                       && student.getLastname().equals(lastnameTextView.getText().toString())) {

                        student.setPresent(true);
                        ArrayList<Student> presentStudents = sessionViewModel.getPresentStudents().getValue();
                        presentStudents.add(student);
                        sessionViewModel.setPresentStudents(presentStudents);

                        ArrayList<Student> missingStudents = new ArrayList<Student>(studentsList); // copy of studentsList
                        missingStudents.remove(student);
                        sessionViewModel.setMissingStudents(missingStudents);
                    }
                }
            }
        };

        public MissingStudentHolder(@NonNull View itemView) {
            super(itemView);
            firstnameTextView = itemView.findViewById(R.id.missingStudentItem_firstname_textView);
            lastnameTextView = itemView.findViewById(R.id.missingStudentItem_lastname_textView);

            passPresentButton = itemView.findViewById(R.id.missingStudentItem_passAbsent_button);
            passPresentButton.setOnClickListener(buttonClicked);
        }
    }
}
