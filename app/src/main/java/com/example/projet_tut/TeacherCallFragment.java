package com.example.projet_tut;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.projet_tut.Model.Discipline;
import com.example.projet_tut.Model.Group;
import com.example.projet_tut.Model.Student;
import com.example.projet_tut.ViewModel.SessionViewModel;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class TeacherCallFragment extends Fragment {

    private static String API_HOST;

    private SessionViewModel sessionViewModel;

    private SharedPreferences sharedPreferences;

    private static String SHARED_PREFS_FILENAME;
    private static String SHARED_PREFS_ID_KEY;
    private static int SHARED_PREFS_ID_DEFAULT_VALUE;

    private RequestQueue requestQueue;

    private RecyclerView recyclerView;

    private TabLayout tabLayout;

    private PresentStudentsAdapter presentStudentsAdapter;
    private MissingStudentsAdapter missingStudentsAdapter;

    private Button terminateButton;

    private View.OnClickListener terminateTheCallClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Nearby.getConnectionsClient(getContext()).stopAdvertising();
            sendCallToServer();
        }
    };

    private ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String s, @NonNull ConnectionInfo connectionInfo) {
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Toast.makeText(getContext(), "Connection innitiated s (endpoint id) : " + s, Toast.LENGTH_SHORT).show();
            Nearby.getConnectionsClient(getContext()).acceptConnection(s, payloadCallback);
        }

        @Override
        public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution connectionResolution) {
            switch(connectionResolution.getStatus().getStatusCode()) {
                case ConnectionsStatusCodes.STATUS_OK:
                    Toast.makeText(getContext(), "Connection accepted", Toast.LENGTH_SHORT).show();
                    break;

                case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                    Toast.makeText(getContext(), "Connection rejected", Toast.LENGTH_SHORT).show();
                    break;

                case ConnectionsStatusCodes.STATUS_ERROR:
                    Toast.makeText(getContext(), "Connection error", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(getContext(), "Trouble trouble trouble", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onDisconnected(@NonNull String s) {

        }
    };

    private PayloadCallback payloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {
            String message = new String(payload.asBytes());
            try {
                int id = Integer.parseInt(message);
                Payload responsePayload;
                if(checkId(id)) {
                    responsePayload = Payload.fromBytes(getString(R.string.nearby_student_registered_message).getBytes());
                }
                else {
                    responsePayload = Payload.fromBytes(getString(R.string.nearby_student_registration_failure).getBytes());
                }

                Nearby.getConnectionsClient(getContext()).sendPayload(s, responsePayload);

            } catch (NumberFormatException e) {
                // we don't treat it, it's invalid nothing happens
                // we will not send information for the moment maybe later ? TODO <--
            }

            Nearby.getConnectionsClient(getContext()).disconnectFromEndpoint(s);
        }

        @Override
        public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {
            switch (payloadTransferUpdate.getStatus()) {
                case PayloadTransferUpdate.Status.SUCCESS:
                    Toast.makeText(getContext(), "payload update success", Toast.LENGTH_LONG).show();
                    break;

                case PayloadTransferUpdate.Status.FAILURE:
                    Toast.makeText(getContext(), "payload update failure", Toast.LENGTH_LONG).show();
                    break;

                case PayloadTransferUpdate.Status.CANCELED:
                    Toast.makeText(getContext(), "payload update cancelled", Toast.LENGTH_LONG).show();
                    break;

                case PayloadTransferUpdate.Status.IN_PROGRESS:
                    Toast.makeText(getContext(), "payload update in progress", Toast.LENGTH_LONG).show();
                    break;

                default:
                    Toast.makeText(getContext(), "payload update shit", Toast.LENGTH_LONG).show();
            }
        }
    };

    private TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            switch (tab.getPosition()) {
                case 0:
                    recyclerView.setAdapter(presentStudentsAdapter);
                    break;

                case 1:
                    recyclerView.setAdapter(missingStudentsAdapter);
                    break;
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SHARED_PREFS_FILENAME = getString(R.string.sharedPrefs_fileName);
        SHARED_PREFS_ID_KEY = getString(R.string.sharedPrefs_id_Key);
        SHARED_PREFS_ID_DEFAULT_VALUE = getResources().getInteger(R.integer.sharedPrefs_id_defaultValue);

        sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);

        sessionViewModel = new ViewModelProvider(getActivity()).get(SessionViewModel.class);

        presentStudentsAdapter = new PresentStudentsAdapter(sessionViewModel);
        missingStudentsAdapter = new MissingStudentsAdapter(sessionViewModel);

        sessionViewModel.getPresentStudents().observe(getActivity(), new Observer<ArrayList<Student>>() {
            @Override
            public void onChanged(ArrayList<Student> students) {
                presentStudentsAdapter.setStudentsList(students);
            }
        });

        sessionViewModel.getMissingStudents().observe(getActivity(), new Observer<ArrayList<Student>>() {
            @Override
            public void onChanged(ArrayList<Student> students) {
                missingStudentsAdapter.setStudentsList(students);
            }
        });

        requestQueue = Volley.newRequestQueue(getActivity());

        API_HOST = getString(R.string.API_host);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_teacher_call, container, false);

        recyclerView = v.findViewById(R.id.teacherCallFragment_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(presentStudentsAdapter);

        tabLayout = v.findViewById(R.id.teacherCallFragment_tabLayout);
        tabLayout.addOnTabSelectedListener(tabSelectedListener);

        terminateButton = v.findViewById(R.id.teacherCallFragment_terminate_button);
        terminateButton.setOnClickListener(terminateTheCallClicked);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadStudents();
    }

    private void loadStudents() {

        if(getArguments().getBoolean("updateLastCall")) {
            String url = API_HOST + "request/call/" + sharedPreferences.getInt(SHARED_PREFS_ID_KEY, SHARED_PREFS_ID_DEFAULT_VALUE);

            JsonObjectRequest getLastCallRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        int callId = response.getInt("idSeance");

                        //TODO set groups and discipline in the sessionViewModel

                        JSONArray studentsVandaele = response.getJSONArray("students");

                        ArrayList<Student> presentStudents = new ArrayList<>();
                        ArrayList<Student> missingStudents = new ArrayList<>();

                        for(int i=0; i<studentsVandaele.length(); i++) {
                            JSONObject studentVandaele = (JSONObject)studentsVandaele.get(i);

                            int id = studentVandaele.getInt("id");
                            String lastname = studentVandaele.getString("nomEtudiant");
                            String firstname = studentVandaele.getString("prenomEtudiant");
                            Boolean present = studentVandaele.getBoolean("presence");

                            if(present) {
                                presentStudents.add(new Student(id, firstname, lastname));
                            }
                            else {
                                missingStudents.add(new Student(id, firstname, lastname));
                            }
                        }

                        JSONArray groupsArray = response.getJSONArray("groups");
                        JSONObject groupJson = (JSONObject) groupsArray.get(0);

                        Group group = new Group(groupJson.getString("label"), groupJson.getInt("id"));
                        Discipline discipline = new Discipline(response.getString("labelMatiere"), response.getInt("idMatiere"));

                        sessionViewModel.setGroup(group);
                        sessionViewModel.setDiscipline(discipline);
                        sessionViewModel.setCallId(callId);
                        sessionViewModel.setMissingStudents(missingStudents);
                        sessionViewModel.setPresentStudents(presentStudents);
                        startAdvertising();

                    } catch (JSONException e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            requestQueue.add(getLastCallRequest);
        }
        else {
            String url = API_HOST + "request/group/" + sessionViewModel.getGroup().getId();

            JsonObjectRequest getStudentsRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray studentsVandaele = response.getJSONArray("students");

                        ArrayList<Student> students = new ArrayList<>();

                        for(int i=0; i<studentsVandaele.length(); i++) {
                            JSONObject studentVandaele = (JSONObject)studentsVandaele.get(i);

                            int id = studentVandaele.getInt("id");
                            String lastname = studentVandaele.getString("nomEtudiant");
                            String firstname = studentVandaele.getString("prenomEtudiant");

                            students.add(new Student(id, firstname, lastname));
                        }

                        sessionViewModel.setCallId(-1);
                        sessionViewModel.setMissingStudents(students);
                        sessionViewModel.setPresentStudents(new ArrayList<Student>());
                        startAdvertising();

                    } catch (JSONException e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            requestQueue.add(getStudentsRequest);
        }
    }

    private void startAdvertising() {
        AdvertisingOptions advertisingOptions = new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_STAR).build();

        //TODO look if it is ok and if it is possible to do it in a cleaner way
        String courseInfos = sessionViewModel.getDiscipline().toString() + "!" +sessionViewModel.getGroup().toString();

        Nearby.getConnectionsClient(getContext()).startAdvertising(courseInfos, getString(R.string.service_id), connectionLifecycleCallback, advertisingOptions);
    }

    // TODO find a better name for this function
    private boolean checkId(int id) {
        ArrayList<Student> missingStudents = sessionViewModel.getMissingStudents().getValue();

        for(Student student: missingStudents) {
            if(student.getId() == id) {
                student.setPresent(true);
                ArrayList<Student> presentStudents = sessionViewModel.getPresentStudents().getValue();
                presentStudents.add(student);
                sessionViewModel.setPresentStudents(presentStudents);

                ArrayList<Student> updatedMissingStudents = new ArrayList<Student>(missingStudents); // copy of missingStudents
                updatedMissingStudents.remove(student);
                sessionViewModel.setMissingStudents(updatedMissingStudents);

                return true;
            }
        }

        return false;
    }

    private void sendCallToServer() {
        String url = API_HOST + "request/call/validate";

        JSONObject body;
        try {
            body = new JSONObject();
            body.put("idSeance", sessionViewModel.getCallId());
            body.put("disciplineId", sessionViewModel.getDiscipline().getId());
            body.put("groupsId", new JSONArray().put(sessionViewModel.getGroup().getId())); // TODO handle this shit of several groups hint use an array of group in the session view model
            body.put("teacherId", sharedPreferences.getInt(SHARED_PREFS_ID_KEY, SHARED_PREFS_ID_DEFAULT_VALUE)); // TODO test id valid

            JSONArray attendancesArray = new JSONArray();

            for(Student student : sessionViewModel.getPresentStudents().getValue()) {
                JSONObject attendance = new JSONObject();
                attendance.put("id", student.getId());
                attendance.put("presence", true);
                attendancesArray.put(attendance);
            }

            for(Student student : sessionViewModel.getMissingStudents().getValue()) {
                JSONObject attendance = new JSONObject();
                attendance.put("id", student.getId());
                attendance.put("presence", false);
                attendancesArray.put(attendance);
            }

            body.put("attendances", attendancesArray);

        }catch (JSONException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            //TODO erreur 500 ici
            return;
        }

        JsonObjectRequest validateCallRequest = new JsonObjectRequest(Request.Method.POST, url, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // TODO implement that
                try {
                    if(response.has("ok")) {
                        if(response.getBoolean("ok")) {
                            Toast.makeText(getContext(), "Success !!", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getContext(), "An unknow error occured.", Toast.LENGTH_LONG);
                            return;
                        }
                    }
                    else {
                        if(response.has("requestError")) {
                            Toast.makeText(getContext(), response.getString("requestError"), Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getContext(), "An unknow error occured.", Toast.LENGTH_LONG);
                        }
                        return;
                    }

                }catch (JSONException e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }

                Navigation.findNavController(getView()).navigate(R.id.action_teacherCallFragment_to_teacherHomeFragment);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(validateCallRequest);
    }
}
