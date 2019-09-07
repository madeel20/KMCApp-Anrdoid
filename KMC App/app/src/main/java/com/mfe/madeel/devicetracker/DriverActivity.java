package com.mfe.madeel.devicetracker;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class DriverActivity extends AppCompatActivity {
    TextView usernameHeading;
    static SharedPreferences sharedpreferences;
    static JSONArray array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        usernameHeading = (TextView) findViewById(R.id.txtHeadingUsername);
        sharedpreferences = getSharedPreferences(MainActivity.mypreference, Context.MODE_PRIVATE);
        String txt = "Username: " + sharedpreferences.getString("username", "");
        usernameHeading.setText(txt);
        if (!isMyServiceRunning(MyService.class)) {
            Intent myServiceIntent = new Intent(this, MyService.class);
            startService(myServiceIntent);

        }
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            Uri.Builder builder;
                            builder  = new Uri.Builder();
                            AsyncRetrieve as = new AsyncRetrieve();
                            //http://adeel20.000webhostapp.com/getrequest.php?key=1345&AUId=12
                            builder.scheme("https")
                                    .authority("adeel20.000webhostapp.com")
                                    .appendPath("requests.php");
                            String myUrl = builder.build().toString();

                            as.execute();
                            as.u = myUrl;
                        } catch (Exception e) {
                            // Toast.makeText(login.this, e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 10000);



    }
    public  void logout(View view){
        Intent stopServiceIntent = new Intent(getApplicationContext(), MyService.class);
        stopService(stopServiceIntent);
        SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.mypreference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();
        Intent activityChangeIntent = new Intent(this,MainActivity.class);

        // currentContext.startActivity(activityChangeIntent);
        finishAffinity();
        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(1);
        this.startActivity(activityChangeIntent);
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
    public class AsyncRetrieve extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(DriverActivity.this);
        HttpURLConnection conn;
        URL url = null;
        String u;
        String res;

        //this method will interact with UI, here display loading message
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

          pdLoading.setMessage("\tLoading Requests...");
            pdLoading.setCancelable(false);
           // pdLoading.show();

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
           // pdLoading.dismiss();
            ArrayList<String> list = new ArrayList<String>();


            try {
                array = new JSONArray(result);
                for (int i=0;i<array.length();i++){
                    JSONObject jsonobject = array.getJSONObject(i);
                    list.add(jsonobject.getString("Username"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //generate list


            //instantiate custom adapter
            MyCustomAdapter adapter = new MyCustomAdapter(list, getApplicationContext());
            //handle listview and assign adapter
            ListView lView = (ListView)findViewById(R.id.my_listview);
            lView.setAdapter(adapter);
           // Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
        }
    }

    public class MyCustomAdapter extends BaseAdapter implements ListAdapter {
        private ArrayList<String> list = new ArrayList<String>();
        private Context context;



        public MyCustomAdapter(ArrayList<String> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int pos) {
            return list.get(pos);
        }

        @Override
        public long getItemId(int pos) {
          //  return list.get(pos).getId();
            //just return 0 if your list items do not have an Id variable.
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.singleitem, null);
            }

            //Handle TextView and display string from your list
            TextView listItemText = view.findViewById(R.id.list_item_string);
            listItemText.setText(list.get(position));

            //Handle buttons and add onClickListeners
            //Button deleteBtn = (Button)view.findViewById(R.id.delete_btn);
            Button addBtn = (Button)view.findViewById(R.id.add_btn);

           /* deleteBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //do something
                    list.remove(position); //or some other task
                    notifyDataSetChanged();
                }
            });*/
            addBtn.setOnClickListener(new View.OnClickListener(){
                JSONObject jso;
                @Override
                public void onClick(View v) {
                    try {
                        jso = array.getJSONObject(position);
                        double longitude = Double.parseDouble(jso.get("Longtitude").toString());
                        double Latitude = Double.parseDouble(jso.get("Latitude").toString());
                        ViewReqDriverActivity.reqUsername = jso.get("Username").toString();
                        ViewReqDriverActivity.reqLatLon = new LatLng(Latitude, longitude);
                        ViewReqDriverActivity.reqId = jso.get("uid").toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //do something
                    Intent activityChangeIntent = new Intent(DriverActivity.this, ViewReqDriverActivity.class);
                    // currentContext.startActivity(activityChangeIntent);
                    startActivity(activityChangeIntent);
                    notifyDataSetChanged();
                }
            });
            return view;
        }
    }
}

