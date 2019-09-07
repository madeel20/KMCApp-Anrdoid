package com.mfe.madeel.devicetracker;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.CircularIntArray;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class CitizenActivity extends AppCompatActivity implements OnMapReadyCallback {
    static double latitude ;
    static double longitude;
    private GoogleMap mMap;
    TextView usernameHeading;
    static SharedPreferences sharedpreferences;
    static int ncheck=0;
    AsyncRetrieve1 as1;
    ProgressDialog pdLoading1;
    Timer timer1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citizen);
        usernameHeading = (TextView)findViewById(R.id.txtHeadingUsername);
        sharedpreferences = getSharedPreferences(MainActivity.mypreference, Context.MODE_PRIVATE);
        String txt = "Username: "+ sharedpreferences.getString("username","");
        usernameHeading.setText(txt);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.g_map);
        mapFragment.getMapAsync(this);
        GPSTracker gps = new GPSTracker(this);
        latitude = gps.getLatitude();
        longitude = gps.getLongitude();
        if (!isMyServiceRunning(MyService.class)) {
            Intent myServiceIntent = new Intent(this, MyService.class);
            startService(myServiceIntent);
        }

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
        LatLng seattle = new LatLng(latitude, longitude);
        final Handler handler = new Handler();
        timer1 = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            mMap.clear();
                            GPSTracker gps = new GPSTracker(getApplicationContext());
                            latitude = gps.getLatitude();
                            longitude = gps.getLongitude();
                            MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Your Location");
                            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.usericon));
                            mMap.addMarker(marker);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                        } catch (Exception e) {
                            // Toast.makeText(login.this, e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };
        timer1.schedule(doAsynchronousTask, 0, 6000);




        mMap.setMinZoomPreference(14);
        mMap.setMaxZoomPreference(20);
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }
    public  void logout(View view){
        Intent stopServiceIntent = new Intent(getApplicationContext(), MyService.class);
        stopService(stopServiceIntent);
        SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.mypreference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();
        Intent activityChangeIntent = new Intent(CitizenActivity.this,MainActivity.class);

        // currentContext.startActivity(activityChangeIntent);
        finishAffinity();
        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(1);
        CitizenActivity.this.startActivity(activityChangeIntent);
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    Uri.Builder builder;
    public void btnBookATruckClick(View view) {
       builder  = new Uri.Builder();
        AsyncRetrieve as = new AsyncRetrieve();
        //http://adeel20.000webhostapp.com/getrequest.php?key=1345&AUId=12
        builder.scheme("https")
                .authority("adeel20.000webhostapp.com")
                .appendPath("getrequest.php")
                .appendQueryParameter("key", "1345")
                .appendQueryParameter("AUId", sharedpreferences.getString("AUId",""));
        String myUrl = builder.build().toString();

        as.execute();
        as.u = myUrl;
    }
    public class AsyncRetrieve extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(CitizenActivity.this);
        HttpURLConnection conn;
        URL url = null;
        String u;
        String res;

        //this method will interact with UI, here display loading message
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoading.setMessage("\tSending Request...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        // This method does not interact with UI, You need to pass result to onPostExecute to display
        @Override
        protected String doInBackground(String... params) {
            try {
                // Enter URL address where your php file resides
                url = new URL(u);

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return e.toString();
            }
            try {

                // Setup HttpURLConnection class to send and receive data from php
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                // setDoOutput to true as we recieve data from json file
                conn.setDoOutput(true);

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return e1.toString();
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {

                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }


        }

        // this method will interact with UI, display result sent from doInBackground method
        @Override
        protected void onPostExecute(String result) {

            Toast.makeText(getApplicationContext(),"reqeust sent!",Toast.LENGTH_LONG).show();

            final Handler handler = new Handler();
           Timer  timer2 = new Timer();
            TimerTask doAsynchronousTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                builder = new Uri.Builder();
                                as1 = new AsyncRetrieve1();
                                //http://adeel20.000webhostapp.com/getrequest.php?key=1345&AUId=12
                                builder.scheme("https")
                                        .authority("adeel20.000webhostapp.com")
                                        .appendPath("checkreq.php")
                                        .appendQueryParameter("key", "1345")
                                        .appendQueryParameter("AUId", sharedpreferences.getString("AUId",""));
                                String myUrl = builder.build().toString();
                               // Toast.makeText(getApplicationContext(),myUrl,Toast.LENGTH_LONG).show();
                                pdLoading.dismiss();
                                as1.u = myUrl;
                                as1.execute();
                            } catch (Exception e) {
                                // Toast.makeText(login.this, e.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            };
            timer2.schedule(doAsynchronousTask, 0, 6000);


            pdLoading1 = new ProgressDialog(CitizenActivity.this);
            pdLoading1.setMessage("\tWaiting for a driver to accept the request...");
            pdLoading1.setCancelable(false);
            pdLoading1.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    as1.cancel(true);
                }
            });
            pdLoading1.show();
        }

    }
    public class AsyncRetrieve1 extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;
        String u;
        String res;
        //this method will interact with UI, here display loading message
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // This method does not interact with UI, You need to pass result to onPostExecute to display
        @Override
        protected String doInBackground(String... params) {
            try {
                // Enter URL address where your php file resides
                url = new URL(u);

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return e.toString();
            }
            try {

                // Setup HttpURLConnection class to send and receive data from php
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                // setDoOutput to true as we recieve data from json file
                conn.setDoOutput(true);

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return e1.toString();
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {

                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }


        }

        // this method will interact with UI, display result sent from doInBackground method
        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
            if(result.equals("1")){
               Toast.makeText(getApplicationContext(),"reqeust accepted!",Toast.LENGTH_LONG).show();
                pdLoading1.dismiss();
                timer1.cancel();
                //http://adeel20.000webhostapp.com/getdriverinfo.php?key=1345&AUId=19
                AsyncRetrieve2 as = new AsyncRetrieve2();
                builder  = new Uri.Builder();
                builder.scheme("https")
                        .authority("adeel20.000webhostapp.com")
                        .appendPath("getdriverinfo.php")
                        .appendQueryParameter("key", "1345")
                        .appendQueryParameter("AUId", sharedpreferences.getString("AUId",""));
                String myUrl = builder.build().toString();

                as.execute();
                as.u = myUrl;

            }

           /* else {
               as1.cancel(true);
                as1 = new AsyncRetrieve1();
                // Toast.makeText(getApplicationContext(), "not accepted!", Toast.LENGTH_LONG).show();
                builder.scheme("https")
                        .authority("adeel20.000webhostapp.com")
                        .appendPath("checkreq.php")
                        .appendQueryParameter("key", "1345")
                        .appendQueryParameter("AUId", sharedpreferences.getString("AUId", ""));
                String myUrl = builder.build().toString();
                as1.execute();
                as1.u = myUrl;
            }
            */
        }

    }
    public class AsyncRetrieve2 extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(CitizenActivity.this);
        HttpURLConnection conn;
        URL url = null;
        String u;
        String res;

        //this method will interact with UI, here display loading message
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoading.setMessage("\tLoading Driver info...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        // This method does not interact with UI, You need to pass result to onPostExecute to display
        @Override
        protected String doInBackground(String... params) {
            try {
                // Enter URL address where your php file resides
                url = new URL(u);

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return e.toString();
            }
            try {

                // Setup HttpURLConnection class to send and receive data from php
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                // setDoOutput to true as we recieve data from json file
                conn.setDoOutput(true);

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return e1.toString();
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {

                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }


        }

        // this method will interact with UI, display result sent from doInBackground method
        @Override
        protected void onPostExecute(String result) {
           // Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
            pdLoading.dismiss();
            try {
                //{"name":"taha","lon":"67.0762091","lan":"24.9878367"}
                JSONObject obj = new JSONObject(result);
               ViewReqCitizenActivity.DriverName = obj.getString("name");
                double longitude = Double.parseDouble(obj.get("lon").toString());
                double Latitude = Double.parseDouble(obj.get("lan").toString());

                ViewReqCitizenActivity.reqLatLon = new LatLng(Latitude, longitude);
            }
            catch (JSONException e){

            }
            Intent activityChangeIntent = new Intent(CitizenActivity.this,  ViewReqCitizenActivity.class);
            // currentContext.startActivity(activityChangeIntent);
            // finishAffinity();
           startActivity(activityChangeIntent);
        }

    }

}
