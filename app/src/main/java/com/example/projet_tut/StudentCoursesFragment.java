package com.example.projet_tut;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet_tut.Model.Course;
import com.example.projet_tut.UtilitariesClass.PresenceResultDialog;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StudentCoursesFragment extends Fragment {

    private static String SHARED_PREFS_FILENAME;
    private static String SHARED_PREFS_ID_KEY;
    private static int SHARED_PREFS_ID_DEFAULT_VALUE;

    private int id;

    private List<Course> courses = new ArrayList<>();

    private RecyclerView recyclerView;
    private CoursesAdapter coursesAdapter;

    private Button stopDiscoveryButton;

    private CoursesAdapter.CoursesAdapterListener coursesAdapterListener  = new CoursesAdapter.CoursesAdapterListener() {
        @Override
        public void onButtonClicked(View v, String id) {
            Nearby.getConnectionsClient(getContext())
                    .requestConnection("student", id, discovererConnectionLifecycleCallback);
            // TODO idée : pour optimiser, on pourrait mettre l'id de l'étudiant ici (s: "id") et l'advertiser accepte seulement
            //  si l'id est dans la liste des absents/ des élèves
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SHARED_PREFS_FILENAME = getString(R.string.sharedPrefs_fileName);

        SHARED_PREFS_ID_KEY = getString(R.string.sharedPrefs_id_Key);
        SHARED_PREFS_ID_DEFAULT_VALUE = getResources().getInteger(R.integer.sharedPrefs_id_defaultValue);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);

        id = sharedPreferences.getInt(SHARED_PREFS_ID_KEY, SHARED_PREFS_ID_DEFAULT_VALUE);

        coursesAdapter = new CoursesAdapter(coursesAdapterListener);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_student_courses, container, false);

        recyclerView = v.findViewById(R.id.studentCourses_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(coursesAdapter);

        stopDiscoveryButton = v.findViewById(R.id.studentCourses_stopDiscovery_Button);
        stopDiscoveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Nearby.getConnectionsClient(getContext()).stopDiscovery();
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build();

        Nearby.getConnectionsClient(getContext()).startDiscovery(
                getString(R.string.service_id), endpointDiscoveryCallback, discoveryOptions);
    }

    private EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(@NonNull String s, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
            Toast.makeText(getContext(), "Advertiser " + s + "found", Toast.LENGTH_SHORT).show();

            // TODO regex to test if enpointName is valid (contains 1 and only 1 ! which is not in begining nor end)
            String endPointName = discoveredEndpointInfo.getEndpointName();
            String[] endpointDisciplineAndGroupe = endPointName.split("!");

            Course course = new Course(endpointDisciplineAndGroupe[0], endpointDisciplineAndGroupe[1], s);
            courses.add(course);
            coursesAdapter.setCourses(courses);
        }

        @Override
        public void onEndpointLost(@NonNull String s) {
            //TODO on endpoint lost never called
            for(Course course : courses) {
                if(course.getId().equals(s)) {
                    courses.remove(course);
                    coursesAdapter.setCourses(courses);
                    return;
                }
            }
        }
    };

    private ConnectionLifecycleCallback discovererConnectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String s, @NonNull ConnectionInfo connectionInfo) {
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Toast.makeText(getContext(), "Connection innitiated s (endpoint id) : " + s, Toast.LENGTH_SHORT).show();
            Nearby.getConnectionsClient(getContext()).acceptConnection(s, discovererPayloadCallback);
        }

        @Override
        public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution connectionResolution) {
            switch(connectionResolution.getStatus().getStatusCode()) {
                case ConnectionsStatusCodes.STATUS_OK:
                    Toast.makeText(getContext(), "Connection accepted", Toast.LENGTH_SHORT).show();

                    Payload payload = Payload.fromBytes(Integer.toString(id).getBytes());
                    Nearby.getConnectionsClient(getContext()).sendPayload(s, payload);

                    Toast.makeText(getContext(), "Message sent to " + s, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "Disconnected from" + s, Toast.LENGTH_SHORT).show();
        }
    };

    private PayloadCallback discovererPayloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {
            String successCode = getString(R.string.nearby_student_registered_message);
            String message = new String(payload.asBytes());
            // TODO test modals
            // TODO indicate the course corresponding to the result
            if(successCode.equals(message)) {
                Nearby.getConnectionsClient(getContext()).stopDiscovery();

                new PresenceResultDialog(getString(R.string.student_registered_message))
                                        .show(getParentFragmentManager(), "success");
            }
            else {
                new PresenceResultDialog(getString(R.string.nearby_student_registration_failure))
                        .show(getParentFragmentManager(), "failure");
            }
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
}
