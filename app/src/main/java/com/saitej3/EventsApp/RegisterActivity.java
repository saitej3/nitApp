package com.saitej3.EventsApp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saitej3.EventsApp.app.AppController;
import com.saitej3.EventsApp.app.URL;
import com.saitej3.EventsApp.helper.ConnectionDetector;
import com.saitej3.EventsApp.util.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sai Teja on 10/22/2015.
 */
public class RegisterActivity extends Activity {
    EditText editName,editEmail,editRegid;
    private SessionManager session;
    Button btnRegister;
    private ProgressDialog pDialog;
    ConnectionDetector cd;
    SharedPreferences sharedPref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        getActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#00BCD4")));
        cd=new ConnectionDetector(getApplicationContext());
        editName= (EditText) findViewById(R.id.name);
        editEmail= (EditText) findViewById(R.id.name);
        editRegid= (EditText) findViewById(R.id.name);
        btnRegister= (Button) findViewById(R.id.btnRegister);
        sharedPref = RegisterActivity.this.getPreferences(Context.MODE_PRIVATE);
        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=editName.getText().toString().trim();
                String email=editEmail.getText().toString().trim();
                String regid=editRegid.getText().toString().trim();

                if(!name.isEmpty()&&!email.isEmpty()&&!regid.isEmpty())
                {
                    if(cd.isConnectingToInternet())
                    {
                        registerUser(name,email,regid);
                    }
                    else {
                        Toast.makeText(RegisterActivity.this,"Please Check Your Network Connection",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(RegisterActivity.this,"Enter correct Details",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void registerUser(final String name, final String email,
                              final String regid) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL.registerUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Tag_register", "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        //store everthing in shared preferences
                        SharedPreferences.Editor editor;
                        editor = sharedPref.edit();
                        editor.putString("name", name);
                        editor.putString("email", email);
                        editor.putString("regno", regid);
                        editor.commit();
                        Intent intent = new Intent(
                                RegisterActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message

                        Toast.makeText(getApplicationContext(),
                                "There was an Error please try again", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "There was an Error please try again", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "register");
                params.put("name", name);
                params.put("email", email);
                params.put("password", regid);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


}
