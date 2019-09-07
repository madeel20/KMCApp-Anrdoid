package com.mfe.madeel.devicetracker;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

public class ViewReqCitizenActivity extends AppCompatActivity  implements OnMapReadyCallback {
    static double latitude ;
    static double longitude;
    private GoogleMap mMap;
    static String DriverName;
    public static LatLng reqLatLon ;
    TextView usernameHeading,reqUserTextView;
    static SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(getApplicationContext(),reqLatLon.latitude + " ",Toast.LENGTH_LONG).show();
        setContentView(R.layout.activity_view_req_citizen);
        usernameHeading = (TextView)findViewById(R.id.txtHeadingUsername);
        reqUserTextView = (TextView)findViewById(R.id.txtrequsername);
        reqUserTextView.setText(reqUserTextView.getText()+ " "+ DriverName);
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
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        //seattle coordinates


        MarkerOptions marker = new MarkerOptions().position(reqLatLon).title(DriverName);
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
        Intent activityChangeIntent = new Intent(ViewReqCitizenActivity.this,MainActivity.class);

        // currentContext.startActivity(activityChangeIntent);
        finishAffinity();
        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(1);
        ViewReqCitizenActivity.this.startActivity(activityChangeIntent);
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
}
