package com.saitej3.EventsApp;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.saitej3.EventsApp.DatePickerFragment.Communicator;
import com.saitej3.EventsApp.helper.ServerUtilities;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.saitej3.EventsApp.adater.CustomEventListAdapter;
import com.saitej3.EventsApp.app.AppController;
import com.saitej3.EventsApp.helper.ConnectionDetector;
import com.saitej3.EventsApp.helper.Util;
import com.saitej3.EventsApp.model.Event;

/**
 * Created by Sai Teja on 10/22/2015.
 */
public class MainActivity extends Activity implements Communicator{

    //for gcm
    ConnectionDetector cd;
    private static final String TAG = MainActivity.class.getSimpleName();
    public static String personname="saitejas";
    public static String email="saitej1852@gmail.com";
    public static final String PROPERTY_REG_ID = "206033082123";
   // private static final String PROPERTY_APP_VERSION = "appVersion";
    String SENDER_ID = "206033082123";
    static final String TAGGCM = "GCMDemo";
    GoogleCloudMessaging gcm;
    Context context;
    String regid;
    SharedPreferences sharedPreferences;

    //for events adapter
    // Movies json url
    private static final String url = "http://wsdc.nitw.ac.in/tz-main/home/events_mob_json";
    private ProgressDialog pDialog;
    private List<Event> eventList = new ArrayList<Event>();
    private ListView listView;
    private CustomEventListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list);
        adapter = new CustomEventListAdapter(this, eventList);
        listView.setAdapter(adapter);
        // changing action bar color
        getActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#00BCD4")));


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent intent = new Intent(MainActivity.this, AboutEventActivity.class);
                Event e = (Event) parent.getItemAtPosition(position);
                Log.d("tag", e.getEventName());
                intent.putExtra("id", e.getId());
                startActivity(intent);

            }

            @SuppressWarnings("unused")
            public void onClick(View v) {
            }


        });

        cd = new ConnectionDetector(getApplicationContext());
        if (!cd.isConnectingToInternet()) {
            Toast.makeText(this,
                    "Internet Connection Error",
                    Toast.LENGTH_SHORT);
            return;
        }
        if (true) {

            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i(TAGGCM, "No valid Google Play Services APK found.");
        }

            new FetchTaskCord().execute("2015-09-21");
    }

    public void getData(final String date) {
        // Tag used to cancel the request

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Getting Events List...");
        pDialog.show();

        JsonArrayRequest movieReq = new JsonArrayRequest(
                url,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());
                hidePDialog();

                // Parsing json
                for (int i = 0; i < response.length(); i++) {
                    try {

                        JSONObject obj = response.getJSONObject(i);
                        Event event = new Event();
                       // event.setId(Integer.valueOf(obj.getInt("id")));
                        event.setEventName(obj.getString("event_name"));
                        event.setEventTime(obj.getString("date"));
                        event.setEventDesc(obj.getString("remarks"));

                        // adding movie to movies array
                        eventList.add(event);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("date", date);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.action_date)
        {
            showDatePickerDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }


    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAGGCM, "Registration not found.");
            return "";
        }
       // int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
//        int currentVersion = getAppVersion(context);
//        if (registeredVersion != currentVersion) {
//            Log.i(TAGGCM, "App version changed.");
//            return "";
//        }
        return registrationId;
    }



    private SharedPreferences getGCMPreferences(Context context) {

        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

//    private static int getAppVersion(Context context) {
//        try {
//            PackageInfo packageInfo = context.getPackageManager()
//                    .getPackageInfo(context.getPackageName(), 0);
//            return packageInfo.versionCode;
//        } catch (PackageManager.NameNotFoundException e) {
//            // should never happen
//            throw new RuntimeException("Could not get package name: " + e);
//        }
//    }


    private void registerInBackground() {
        new Reg().execute(null, null, null);
    }



    private void sendRegistrationIdToBackend() {

        ServerUtilities.register(this, personname, email, regid);
    }


    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
       // int appVersion = getAppVersion(context);
        //Log.i(TAGGCM, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        //editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    @Override
    public void onDialogMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    private class Reg extends AsyncTask<Void,Void,String>
    {

        protected String doInBackground(Void... params) {
            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                regid = gcm.register(SENDER_ID);
                msg = "Device registered, registration ID=" + regid;

                sendRegistrationIdToBackend();
                storeRegistrationId(context, regid);
            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
            }
            return msg;
        }

        protected void onPostExecute(String msg) {
            Toast.makeText(MainActivity.this,"redid created",Toast.LENGTH_SHORT).show();
        }
    }


    public class FetchTaskCord extends AsyncTask< String,Void,String> {

        private ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Fetching Event Locations");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            if(strings[0]==null) return null;
            HashMap<String,String> data=new HashMap();
            data.put("date", strings[0]);
            Log.d("tag me one",strings[0]);
            String jsonstr= Util.getStringFromURL("http://wsdc.nitw.ac.in/tz-main/home/events_mob_json_date", data);

            if (jsonstr!=null) {
                Log.d("GOT FROM HTTP", jsonstr);
                try {
                    JSONArray jsonArr= new JSONArray(jsonstr);
                    Log.d("jsonArray",jsonArr.toString());
                    for(int i=0;i<jsonArr.length();i++) {
                        JSONObject feedObj = (JSONObject) jsonArr.get(i);
                        Event ename=new Event();
                        ename.setId(Integer.valueOf(feedObj.getString("id")));
                        ename.setEventName(feedObj.getString("event_name"));
                        ename.setEventTime(feedObj.getString("time"));
                        ename.setEventDesc(feedObj.getString("remarks"));

                        String path="http://172.20.34/tz-main/assets/images/events_icons/"+ feedObj.getString("event_alias")+".jpg";
                        ename.setImagePath(path);

                        Log.d("icon",path);

                        eventList.add(ename);
                    }

                    return "yeas";

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void  onPostExecute(String yes ) {
            super.onPostExecute(yes);
            if (progressDialog.isShowing()) {
                progressDialog.cancel();
            }


            adapter.notifyDataSetChanged();

        }
    }
}
