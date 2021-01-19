package com.example.projet_tut;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.projet_tut.Model.Discipline;
import com.example.projet_tut.Model.Group;
import com.example.projet_tut.ViewModel.SessionViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TeacherHomeFragment extends Fragment {

    //private static final String API_HOST = "http://10.0.2.2:8080/";
    private static String API_HOST;

    private SessionViewModel sessionViewModel;

    private SharedPreferences sharedPreferences;

    private static String SHARED_PREFS_FILENAME;
    private static String SHARED_PREFS_ID_KEY;
    private static int SHARED_PREFS_ID_DEFAULT_VALUE;

    RequestQueue requestQueue;

    private Spinner disciplineSpinner;
    private Spinner groupSpinner;

    private Button validateButton;

    private View.OnClickListener validateButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Group selectedGroup = groupSpinner.getSelectedItem() instanceof Group ? (Group)groupSpinner.getSelectedItem() : null;
            Discipline selectedDiscipline = disciplineSpinner.getSelectedItem() instanceof Discipline ? (Discipline)disciplineSpinner.getSelectedItem() : null;

            if(selectedGroup == null || selectedDiscipline == null) {
                return;
            }

            sessionViewModel.setDiscipline(selectedDiscipline);
            sessionViewModel.setGroup(selectedGroup);

            Navigation.findNavController(getView()).navigate(R.id.action_teacherHomeFragment_to_teacherCallFragment);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SHARED_PREFS_FILENAME = getString(R.string.sharedPrefs_fileName);
        SHARED_PREFS_ID_KEY = getString(R.string.sharedPrefs_id_Key);
        SHARED_PREFS_ID_DEFAULT_VALUE = getResources().getInteger(R.integer.sharedPrefs_id_defaultValue);

        sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);

        requestQueue = Volley.newRequestQueue(getContext());

        sessionViewModel = new ViewModelProvider(getActivity()).get(SessionViewModel.class);

        API_HOST = getString(R.string.API_host);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_teacher_home, container, false);

        disciplineSpinner = v.findViewById(R.id.teacherHome_discipline_spinner);
        groupSpinner = v.findViewById(R.id.teacherHome_group_spinner);

        validateButton = v.findViewById(R.id.teacherHome_validate_button);
        validateButton.setOnClickListener(validateButtonClicked);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fillSpinners();
    }

    private void fillSpinners() {

        String url = API_HOST + "request/session/" + sharedPreferences.getInt(SHARED_PREFS_ID_KEY, SHARED_PREFS_ID_DEFAULT_VALUE);

        JsonObjectRequest getGroupsAndDisciplinesRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray disciplinesVandaele = response.getJSONArray("disciplines");
                    JSONArray groupsVandaele = response.getJSONArray("groups");

                    ArrayAdapter<Discipline> disciplinesAdapter = new ArrayAdapter<Discipline>(getContext(), android.R.layout.simple_spinner_item);
                    ArrayAdapter<Group> groupsAdapter = new ArrayAdapter<Group>(getContext(), android.R.layout.simple_spinner_item);

                    for(int i=0; i<disciplinesVandaele.length(); i++) {
                        JSONObject disciplineVandaele = (JSONObject)disciplinesVandaele.get(i);
                        String label = disciplineVandaele.getString("label");
                        int id = disciplineVandaele.getInt("id");

                        Discipline discipline = new Discipline(label, id);
                        disciplinesAdapter.add(discipline);
                    }

                    for(int i=0; i<groupsVandaele.length(); i++) {
                        JSONObject groupVandaele = (JSONObject)groupsVandaele.get(i);
                        String label = groupVandaele.getString("label");
                        int id = groupVandaele.getInt("id");

                        Group group = new Group(label, id);
                        groupsAdapter.add(group);
                    }

                    disciplinesAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    disciplineSpinner.setAdapter(disciplinesAdapter);
                    disciplineSpinner.setSelection(0);

                    groupsAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    groupSpinner.setAdapter(groupsAdapter);
                    groupSpinner.setSelection(0);

                }catch (JSONException e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(getGroupsAndDisciplinesRequest);
    }
}
