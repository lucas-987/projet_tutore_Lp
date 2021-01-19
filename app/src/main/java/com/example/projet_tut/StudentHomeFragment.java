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

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
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

import java.util.concurrent.TimeUnit;

public class StudentHomeFragment extends Fragment {

    private static String SHARED_PREFS_FILENAME;
    private static String SHARED_PREFS_ID_KEY;
    private static int SHARED_PREFS_ID_DEFAULT_VALUE;

    private int id;

    private Button sendIdButton;
    private TextView messageTextView;


    private View.OnClickListener sendIdButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build();

            Nearby.getConnectionsClient(getContext()).startDiscovery(
                    getString(R.string.service_id), endpointDiscoveryCallback, discoveryOptions);
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_student_home, container, false);

        sendIdButton = v.findViewById(R.id.studentHome_sendId_button);
        sendIdButton.setOnClickListener(sendIdButtonClicked);

        messageTextView = v.findViewById(R.id.studentHome_message_textView);

        return v;
    }

    private EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(@NonNull String s, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
            Toast.makeText(getContext(), "Advertiser " + s + "found", Toast.LENGTH_SHORT).show();
            Nearby.getConnectionsClient(getContext())
                    .requestConnection("student", s, discovererConnectionLifecycleCallback);
                    // TODO idée : pour optimiser, on pourrait mettre l'id de l'étudiant ici (s: "id") et l'advertiser accepte seulement
                    //  si l'id est dans la liste des absents/ des élèves
            //Nearby.getConnectionsClient(getContext()).stopDiscovery();
        }

        @Override
        public void onEndpointLost(@NonNull String s) {

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
            //Toast.makeText(getApplicationContext(), new String(payload.asBytes()), Toast.LENGTH_LONG).show();
            //resultTextView.setText(new String(payload.asBytes()));
            String successCode = getString(R.string.nearby_student_registered_message);
            String message = new String(payload.asBytes());
            if(successCode.equals(message)) {
                Nearby.getConnectionsClient(getContext()).stopDiscovery();
                messageTextView.setVisibility(View.VISIBLE);
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
