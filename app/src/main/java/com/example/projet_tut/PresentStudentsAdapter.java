package com.example.projet_tut;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet_tut.Model.Student;
import com.example.projet_tut.ViewModel.SessionViewModel;

import java.util.ArrayList;
import java.util.List;

public class PresentStudentsAdapter extends RecyclerView.Adapter<PresentStudentsAdapter.PresentStudentHolder> {

    private List<Student> studentsList = new ArrayList<>();

    private SessionViewModel sessionViewModel;

    public PresentStudentsAdapter(SessionViewModel pSessionViewModel) {
        super();
        sessionViewModel = pSessionViewModel;
    }

    @NonNull
    @Override
    public PresentStudentsAdapter.PresentStudentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.present_students_recycler_item, parent, false);
        return new PresentStudentsAdapter.PresentStudentHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PresentStudentHolder holder, int position) {
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

    class PresentStudentHolder extends RecyclerView.ViewHolder {
        private TextView firstnameTextView;
        private TextView lastnameTextView;
        private Button passAbsentButton;

        private View.OnClickListener buttonClicked = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(Student student : studentsList) {
                    if(student.getFirstname().equals(firstnameTextView.getText().toString())
                            && student.getLastname().equals(lastnameTextView.getText().toString())) {

                        student.setPresent(false);
                        ArrayList<Student> missingStudents = sessionViewModel.getMissingStudents().getValue();
                        missingStudents.add(student);
                        sessionViewModel.setMissingStudents(missingStudents);

                        ArrayList<Student> presentStudents = new ArrayList<Student>(studentsList); // copy of studentsList
                        presentStudents.remove(student);
                        sessionViewModel.setPresentStudents(presentStudents);
                    }
                }
            }
        };

        public PresentStudentHolder(@NonNull View itemView) {
            super(itemView);
            firstnameTextView = itemView.findViewById(R.id.presentStudentItem_firstname_textView);
            lastnameTextView = itemView.findViewById(R.id.presentStudentItem_lastname_textView);

            passAbsentButton = itemView.findViewById(R.id.presentStudentItem_passAbsent_button);
            passAbsentButton.setOnClickListener(buttonClicked);
        }
    }
}
