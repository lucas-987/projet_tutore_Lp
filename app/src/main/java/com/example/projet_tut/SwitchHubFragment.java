package com.example.projet_tut;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class SwitchHubFragment extends Fragment {

    private static String SHARED_PREFS_FILENAME;
    private static String SHARED_PREFS_ID_KEY;
    private static int SHARED_PREFS_ID_DEFAULT_VALUE;

    private static String SHARED_PREFS_USER_TYPE_KEY;
    private static String SHARED_PREFS_USER_TYPE_DEFAULT_VALUE;
    private static String SHARED_PREFS_USER_TYPE_TEACHER_VALUE;
    private static String SHARED_PREFS_USER_TYPE_STUDENT_VALUE;

    SharedPreferences sharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SHARED_PREFS_FILENAME = getString(R.string.sharedPrefs_fileName);

        SHARED_PREFS_ID_KEY = getString(R.string.sharedPrefs_id_Key);
        SHARED_PREFS_ID_DEFAULT_VALUE = getResources().getInteger(R.integer.sharedPrefs_id_defaultValue);

        SHARED_PREFS_USER_TYPE_KEY = getString(R.string.sharedPrefs_userType_key);
        SHARED_PREFS_USER_TYPE_DEFAULT_VALUE = getString(R.string.sharedPrefs_userType_defaultValue);
        SHARED_PREFS_USER_TYPE_STUDENT_VALUE = getString(R.string.sharedPrefs_userType_studentValue);
        SHARED_PREFS_USER_TYPE_TEACHER_VALUE = getString(R.string.sharedPrefs_userType_teacherValue);

        sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_switchhub, container, false);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        handleUserType();
    }

    private void handleUserType() {
        int id = sharedPreferences.getInt(SHARED_PREFS_ID_KEY, SHARED_PREFS_ID_DEFAULT_VALUE);

        if(id == SHARED_PREFS_ID_DEFAULT_VALUE) {
            Navigation.findNavController(getView()).navigate(R.id.action_switchHubFragment_to_registerFragment);
        }
        else {
            String userType = sharedPreferences.getString(SHARED_PREFS_USER_TYPE_KEY, SHARED_PREFS_USER_TYPE_DEFAULT_VALUE);

            if(userType.equals(SHARED_PREFS_USER_TYPE_DEFAULT_VALUE)) {
                Navigation.findNavController(getView()).navigate(R.id.action_switchHubFragment_to_registerFragment);
            }
            else if (userType.equals(SHARED_PREFS_USER_TYPE_TEACHER_VALUE)) {
                Navigation.findNavController(getView()).navigate(R.id.action_switchHubFragment_to_teacherHomeFragment);
            }
            else if (userType.equals(SHARED_PREFS_USER_TYPE_STUDENT_VALUE)) {
                Navigation.findNavController(getView()).navigate(R.id.action_switchHubFragment_to_studentHomeFragment);
            }
            else {
                Navigation.findNavController(getView()).navigate(R.id.action_switchHubFragment_to_registerFragment);
            }
        }
    }

}
