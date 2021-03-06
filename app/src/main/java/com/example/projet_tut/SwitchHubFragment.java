package com.example.projet_tut;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class SwitchHubFragment extends Fragment {

    private static final int ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE = 1;

    private static String SHARED_PREFS_FILENAME;
    private static String SHARED_PREFS_ID_KEY;
    private static int SHARED_PREFS_ID_DEFAULT_VALUE;

    private static String SHARED_PREFS_USER_TYPE_KEY;
    private static String SHARED_PREFS_USER_TYPE_DEFAULT_VALUE;
    private static String SHARED_PREFS_USER_TYPE_TEACHER_VALUE;
    private static String SHARED_PREFS_USER_TYPE_STUDENT_VALUE;

    private SharedPreferences sharedPreferences;

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
        //Ask for acces fine location permission
        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        }
        /*else if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

        }*/
        else {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE);
        }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == SwitchHubFragment.ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
