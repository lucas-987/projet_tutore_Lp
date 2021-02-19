package com.example.projet_tut;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class RegisterFragment extends Fragment {

    private static final Pattern INE_PATTERN = Pattern.compile("^\\d{9}[A-Z\\d][A-Z]$");
    private static final Pattern ARPEGE_PATTERN = Pattern.compile("^\\d{9}[A-Z]{4}");
    //private static final String API_ROOT_URL = "http://10.0.2.2:8080/";
    private static String API_ROOT_URL;

    private SharedPreferences sharedPreferences;

    private static String SHARED_PREFS_FILENAME;
    private static String SHARED_PREFS_ID_KEY;
    private static String SHARED_PREFS_USER_TYPE_KEY;
    private static String SHARED_PREFS_USER_TYPE_TEACHER_VALUE;
    private static String SHARED_PREFS_USER_TYPE_STUDENT_VALUE;

    private EditText ineArpegeEditText;
    private EditText birthDateEditText;

    private Button registerButton;

    private class PostRequest extends StringRequest {

        private String body;

        public PostRequest(int requestMethod, String url, Response.Listener<String> responseListener, Response.ErrorListener errorListener, String body) {
            super(requestMethod, url, responseListener, errorListener);
            this.body = body;
        }

        @Override
        public String getBodyContentType() {
            return "application/json; charset=utf-8";
        }

        @Override
        public byte[] getBody() throws AuthFailureError {
            try {
                return body == null ? null : body.getBytes("utf-8");
            }catch(UnsupportedEncodingException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                return null;
            }
        }
    }

    private View.OnClickListener registerButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String ineArpege = ineArpegeEditText.getText().toString().trim();
            String birthDate = birthDateEditText.getText().toString().trim();

            boolean birthDateOk = true;

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            dateFormat.setLenient(false);
            try {
                dateFormat.parse(birthDate);
            }catch(ParseException e) {
                birthDateOk = false;
                Toast.makeText(getContext(), "date false", Toast.LENGTH_LONG).show();
            }

            boolean test = INE_PATTERN.matcher(ineArpege).matches();

            if(INE_PATTERN.matcher(ineArpege).matches() && birthDateOk) {
                //vandaele = synonime de JSON (prononcer djèillezone avec o comme porte)
                //final String vandaeleBody = "{\"ineArpege\":\"" + ineArpege + "\",\"birthDate\":\"" + birthDate + "\"}";

                final String url = API_ROOT_URL + "request/inscription/student/" + ineArpege + "/" + birthDate;


                RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                StringRequest request = new StringRequest(Request.Method.GET, url,  new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
                        try {
                            JSONObject vandaeleResponse = new JSONObject(response);

                            //no need to check the json because a JSONexception will be thrown if something wrong
                            int id = vandaeleResponse.getInt("id");

                            if(id < 0) {throw new JSONException("Wrong id.");}

                            sharedPreferences.edit()
                                    .putInt(SHARED_PREFS_ID_KEY, id)
                                    .putString(SHARED_PREFS_USER_TYPE_KEY, SHARED_PREFS_USER_TYPE_STUDENT_VALUE)
                                    .apply();
                            Navigation.findNavController(getView()).navigate(R.id.action_registerFragment_to_studentHomeFragment);

                        }catch (JSONException e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

                requestQueue.add(request);
            }
            else if(ARPEGE_PATTERN.matcher(ineArpege).matches() && birthDateOk){
                final String url = API_ROOT_URL + "request/inscription/teacher/" + ineArpege + "/" + birthDate;


                RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                StringRequest request = new StringRequest(Request.Method.GET, url,  new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
                        try {
                            JSONObject vandaeleResponse = new JSONObject(response);

                            //no need to check the json because a JSONexception will be thrown if something wrong
                            int id = vandaeleResponse.getInt("id");

                            if(id < 0) {throw new JSONException("Wrong id.");}

                            sharedPreferences.edit()
                                    .putInt(SHARED_PREFS_ID_KEY, id)
                                    .putString(SHARED_PREFS_USER_TYPE_KEY, SHARED_PREFS_USER_TYPE_TEACHER_VALUE)
                                    .apply();
                            Navigation.findNavController(getView()).navigate(R.id.action_registerFragment_to_teacherHomeFragment);

                        }catch (JSONException e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

                requestQueue.add(request);
            }

            //Only to test student while no connection on old device TODO remove from final version
            /*sharedPreferences.edit()
                    .putInt(SHARED_PREFS_ID_KEY, 1)
                    .putString(SHARED_PREFS_USER_TYPE_KEY, SHARED_PREFS_USER_TYPE_STUDENT_VALUE)
                    .apply();
            Navigation.findNavController(getView()).navigate(R.id.action_registerFragment_to_studentHomeFragment);*/
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SHARED_PREFS_FILENAME = getString(R.string.sharedPrefs_fileName);
        SHARED_PREFS_ID_KEY = getString(R.string.sharedPrefs_id_Key);
        SHARED_PREFS_USER_TYPE_KEY = getString(R.string.sharedPrefs_userType_key);
        SHARED_PREFS_USER_TYPE_STUDENT_VALUE = getString(R.string.sharedPrefs_userType_studentValue);
        SHARED_PREFS_USER_TYPE_TEACHER_VALUE = getString(R.string.sharedPrefs_userType_teacherValue);

        sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);

        API_ROOT_URL = getString(R.string.API_host);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_register, container, false);

        ineArpegeEditText =  v.findViewById(R.id.register_ineArpege_editText);
        birthDateEditText = v.findViewById(R.id.register_birthDate_editText);

        registerButton = v.findViewById(R.id.register_validate_button);
        registerButton.setOnClickListener(registerButtonClicked);

        return v;
    }

    //TODO remove all toast from project
}
