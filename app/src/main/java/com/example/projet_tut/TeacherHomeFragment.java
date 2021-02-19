package com.example.projet_tut;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class TeacherHomeFragment extends Fragment {

    private Button newCallButton;
    private Button updateCallButton;

    private View.OnClickListener newCallButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Navigation.findNavController(getView()).navigate(R.id.action_teacherHomeFragment_to_teacherCreateCall);
        }
    };

    private View.OnClickListener updateCallButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle args = new Bundle();
            args.putBoolean("updateLastCall", true);
            Navigation.findNavController(getView()).navigate(R.id.action_teacherHomeFragment_to_teacherCallFragment, args);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_teacher_home, container, false);

        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    getActivity().finishAffinity();
                    return true;
                }
                return false;
            }
        });

        newCallButton = v.findViewById(R.id.teacherHome_newCall_button);
        updateCallButton = v.findViewById(R.id.teacherHome_updateLastCall_button);

        newCallButton.setOnClickListener(newCallButtonClicked);
        updateCallButton.setOnClickListener(updateCallButtonClicked);

        return v;
    }


}
