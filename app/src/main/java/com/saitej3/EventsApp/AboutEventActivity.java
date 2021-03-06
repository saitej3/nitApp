package com.saitej3.EventsApp;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.saitej3.EventsApp.app.AppController;
import com.saitej3.EventsApp.app.URL;
import com.saitej3.EventsApp.helper.ConnectionDetector;
import com.saitej3.EventsApp.helper.Util;
import com.saitej3.EventsApp.model.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sai Teja on 10/22/2015.
 */
public class AboutEventActivity extends Activity {

    String id;
    ConnectionDetector cd;
    TextView eventName,eventTime,eventDesc,eventVenue,eventpreq,eventcname1,eventcname2,eventcno1,eventcno2;
    NetworkImageView image;
    private ProgressDialog pDialog;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutevent);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id=String.valueOf(extras.getInt("id"));

        }
        eventName= (TextView) findViewById(R.id.aeventName);
        eventTime= (TextView) findViewById(R.id.aeventTime);
        eventDesc= (TextView) findViewById(R.id.aeventDesc);
        eventVenue= (TextView) findViewById(R.id.aeventDesc);
        eventpreq= (TextView) findViewById(R.id.aeventpreq);
        eventcname1= (TextView) findViewById(R.id.aeventConatct1);
        eventcname2= (TextView) findViewById(R.id.aeventConatct2);
        eventcno1= (TextView) findViewById(R.id.aeventContactno1);
        eventcno2= (TextView) findViewById(R.id.aeventContactno2);

        image= (NetworkImageView) findViewById(R.id.eventimage);

        button= (Button) findViewById(R.id.aeventGoing);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(v);
                button.setText("Registerd");
                button.setBackgroundColor(Color.parseColor("#bbdefb"));
                button.setEnabled(false);
            }
        });

        getActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#4DD0E1")));

        cd=new ConnectionDetector(getApplicationContext());
        if(!cd.isConnectingToInternet())
        {
            Toast.makeText(this,"Please checkyour Network",Toast.LENGTH_SHORT).show();
            return;
        }

        getData(id);

    }


    private void getData(final String event) {
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        String tag_string_req = "req_event";
        pDialog.setMessage("Getting Event Details ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL.registerEventUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Tag_event", "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONArray jObj = new JSONArray(response);
                    if(jObj==null)
                    {
                        Toast.makeText(AboutEventActivity.this,"Please check the network",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    JSONObject json=jObj.getJSONObject(0);
                    eventName.setText(json.getString("event_name"));
                    eventTime.setText(json.getString("time"));
                    eventDesc.setText(json.getString("content"));
                    eventVenue.setText(json.getString("event_place"));
                    eventpreq.setText(json.getString("remarks"));
                    eventcname1.setText(json.getString("contact1name"));
                    eventcname2.setText(json.getString("contact2name"));
                    eventcno1.setText(json.getString("contact1num"));
                    eventcno2.setText(json.getString("contact2num"));
                    String base_url="http://172.20.0.34/tz-main/assets/images/headers/";
                    String path=json.getString("event_alias");
                    path=base_url+path+"_header.jpg";
                    Log.d("Image",path);
                    image.setImageUrl(path,imageLoader);

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
                params.put("id", event);
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


    public void showDialog(View v)
    {
        FragmentManager fm=getFragmentManager();
        FeedBackDialog myDialog=new FeedBackDialog();
        myDialog.show(fm,"pop");

    }






}