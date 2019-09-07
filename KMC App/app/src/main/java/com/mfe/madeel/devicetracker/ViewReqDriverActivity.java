package com.mfe.madeel.devicetracker;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ViewReqDriverActivity extends AppCompatActivity implements OnMapReadyCallback {
    static double latitude ;
    static double longitude;
    private GoogleMap mMap;
    TextView usernameHeading,reqUserTextView;
    static SharedPreferences sharedpreferences;
    public static LatLng reqLatLon ;
    public static String reqUsername;
    public static String reqId;
    static boolean isAccepted;
    Button btnAccept;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_req_driver);
        btnAccept = (Button)findViewById(R.id.regbtn2);
        usernameHeading = (TextView)findViewById(R.id.txtHeadingUsername);
        reqUserTextView = (TextView)findViewById(R.id.txtrequsername);
        reqUserTextView.setText(reqUserTextView.getText()+ " "+reqUsername);
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


        MarkerOptions marker = new MarkerOptions().position(reqLatLon).title(reqUsername);
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.usericon));

        MarkerOptions marker1 = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Your Location");

        marker1.icon(BitmapDescriptorFactory.fromResource(R.drawable.truckicon));

        mMap.addMarker(marker);
        mMap.addMarker(marker1);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(reqLatLon));
        mMap.setMinZoomPreference(12);
        mMap.setMaxZoomPreference(20);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

    }
    public  void logout(View view){
        Intent stopServiceIntent = new Intent(getApplicationContext(), MyService.class);
        stopService(stopServiceIntent);
        SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.mypreference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();
        Intent activityChangeIntent = new Intent(ViewReqDriverActivity.this,MainActivity.class);

        // currentContext.startActivity(activityChangeIntent);
        finishAffinity();
        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(1);
        ViewReqDriverActivity.this.startActivity(activityChangeIntent);
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //Do the Logics Here
        Intent activityChangeIntent = new Intent(ViewReqDriverActivity.this, DriverActivity.class);
        // currentContext.startActivity(activityChangeIntent);
        startActivity(activityChangeIntent);
    }

    public void btnBookATruckClick(View view) {
        Uri.Builder builder1 = new Uri.Builder();
        //http://adeel20.000webhostapp.com/acceptreq.php?key=1345&AUId=12&DId=12
        builder1.scheme("https")
                .authority("adeel20.000webhostapp.com")
                .appendPath("acceptreq.php")
                .appendQueryParameter("key","1345")
                .appendQueryParameter("AUId",  reqId)
                .appendQueryParameter("DId", sharedpreferences.getString("AUId",""));
        String myUrl1 = builder1.build().toString();
        AsyncRetrieve2 sa = new AsyncRetrieve2();
        sa.u = myUrl1;
        sa.execute();
    }
    public class AsyncRetrieve2 extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(ViewReqDriverActivity.this);
        HttpURLConnection conn;
        URL url = null;
        String u;
        String res;

        //this method will interact with UI, here display loading message
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

           pdLoading.setMessage("\tAccepting Request...");
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
            pdLoading.dismiss();
            if(result.equals("1")){
                Toast.makeText(getApplicationContext(),"Acception Successful!",Toast.LENGTH_LONG).show();
                isAccepted= true;
                btnAccept.setVisibility(View.GONE);
            }
            else {
                Toast.makeText(getApplicationContext(),"Sorry problem accepting request!",Toast.LENGTH_LONG).show();
            }


        }

    }
    public void hideButton(){

    }

}
