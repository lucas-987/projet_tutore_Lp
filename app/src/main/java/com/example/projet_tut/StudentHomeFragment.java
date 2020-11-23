package com.example.projet_tut;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class StudentHomeFragment extends Fragment {

    private Button sendIdButton;


    private View.OnClickListener sendIdButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_student_home, container, false);

        sendIdButton = v.findViewById(R.id.studentHome_sendId_button);
        sendIdButton.setOnClickListener(sendIdButtonClicked);

        return v;
    }
}
