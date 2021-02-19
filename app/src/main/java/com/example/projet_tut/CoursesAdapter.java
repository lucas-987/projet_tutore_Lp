package com.example.projet_tut;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet_tut.Model.Course;

import java.util.ArrayList;
import java.util.List;

public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.CoursesHolder> {

    private List<Course> courses = new ArrayList<>();

    private CoursesAdapterListener coursesAdapterListener;

    public interface CoursesAdapterListener {

        void onButtonClicked(View v, String id);
    }

    public CoursesAdapter(CoursesAdapterListener coursesAdapterListener) {
        super();
        this.coursesAdapterListener = coursesAdapterListener;
    }

    @NonNull
    @Override
    public CoursesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.course_recycler_item, parent, false);
        return new CoursesAdapter.CoursesHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CoursesHolder holder, int position) {
        Course currentCourse = courses.get(position);

        holder.disciplineTextView.setText(currentCourse.getDisciplineName());
        holder.groupTextView.setText(currentCourse.getGroupeName());
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
        notifyDataSetChanged();
    }

    class CoursesHolder extends RecyclerView.ViewHolder {

        private TextView disciplineTextView;
        private TextView groupTextView;
        private Button sendButton;

        public CoursesHolder(@NonNull View itemView) {
            super(itemView);
            disciplineTextView = itemView.findViewById(R.id.courseItem_discipline_TextView);
            groupTextView = itemView.findViewById(R.id.courseItem_groupe_TextView);

            sendButton = itemView.findViewById(R.id.courseItem_send_button);

            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Course course = courses.get(getAdapterPosition());

                    coursesAdapterListener.onButtonClicked(v, course.getId());
                }
            });
        }
    }
}
