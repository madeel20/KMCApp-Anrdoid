package com.mfe.madeel.devicetracker;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.net.Uri;
import android.os.AsyncTask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class register extends AppCompatActivity {
EditText name,username,password,rpassword;
    Button uploadbtn;
    String userType;
    private RadioGroup radioTypebutton;
    private RadioButton radioType;
    public static final int CONNECTION_TIMEOUT = 50000;
    public static final int READ_TIMEOUT = 15000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        radioTypebutton = (RadioGroup) findViewById(R.id.radioType);
        name = (EditText) findViewById(R.id.txtName);
        password = (EditText) findViewById(R.id.txtPass);
        rpassword = (EditText) findViewById(R.id.txtConfirmPass);
        username = (EditText) findViewById(R.id.txtUsername);
    }



    public void regtbn(View v){
        int selectedId = radioTypebutton.getCheckedRadioButtonId();

        // find the radiobutton by returned id
        radioType = (RadioButton) findViewById(selectedId);
        userType  = radioType.getText().toString();
        if(name.getText().toString().isEmpty()){
            Toast .makeText(register.this,"Name is required!",Toast.LENGTH_LONG).show();
        }
        else if(username.getText().toString().isEmpty()){
            Toast .makeText(register.this,"Username is required!",Toast.LENGTH_LONG).show();
        }
        else if(password.getText().toString().isEmpty() ||  rpassword.getText().toString().isEmpty())
       {
           Toast.makeText(register.this, "password is required!", Toast.LENGTH_LONG).show();
       }
       else if(password.getText().toString().length()<4 || rpassword.getText().toString().length()<4){
           Toast.makeText(register.this, "Password should contain atleast 4 characters!", Toast.LENGTH_LONG).show();
       }
        else {

//register.php?key=1234&username=ayesha&password=1234&cdate=2018-9-3

            if(password.getText().toString().equals(rpassword.getText().toString())){
                Date c = Calendar.getInstance().getTime();
                //System.out.println("Current time => " + c);

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = df.format(c);
                //Toast.makeText(register.this, formattedDate, Toast.LENGTH_LONG).show();
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority("adeel20.000webhostapp.com")
                        .appendPath("register.php")
                        .appendQueryParameter("key","1234")
                        .appendQueryParameter("username", username.getText().toString())
                        .appendQueryParameter("password", password.getText().toString())
                        .appendQueryParameter("name", name.getText().toString())
                        .appendQueryParameter("userType", userType)
                        .appendQueryParameter("cdate",formattedDate);
                String myUrl = builder.build().toString();
                AsyncRetrieve as = new AsyncRetrieve();
                as.execute();
                as.u = myUrl;





            }
            else {
                Toast.makeText(register.this, "Passwords does not matched!", Toast.LENGTH_LONG).show();
            }

        }

    }


    private class AsyncRetrieve extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(register.this);
        HttpURLConnection conn;
        URL url = null;
        String u;
        String res;

        //this method will interact with UI, here display loading message
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoading.setMessage("\tCreating Account...");
            pdLoading.setCancelable(true);
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
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("GET");

                // setDoOutput to true as we recieve data from json file
                conn.setDoOutput(true);

            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                Toast.makeText(register.this, e1.toString(), Toast.LENGTH_LONG).show();
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

            } catch (Exception e) {

               // Toast.makeText(register.this, e.toString(), Toast.LENGTH_LONG).show();
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

         SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.mypreference, Context.MODE_PRIVATE);

         if (result.equalsIgnoreCase("1")) {
             // you to understand error returned from doInBackground method
             sharedpreferences = getSharedPreferences(MainActivity.mypreference,
                     Context.MODE_PRIVATE);
             //   Toast.makeText(register.this, "Registration Succesfull!", Toast.LENGTH_LONG).show();
             SharedPreferences.Editor editor = sharedpreferences.edit();
             editor.putString("username", username.getText().toString());
             editor.putString("password", password.getText().toString());
             editor.putString("type", userType);
             editor.commit();
             AsyncRetrieve1 as = new AsyncRetrieve1();
             Uri.Builder builder = new Uri.Builder();
             builder.scheme("https")
                     .authority("adeel20.000webhostapp.com")
                     .appendPath("getauid.php")
                     .appendQueryParameter("key", "1345")
                     .appendQueryParameter("username", username.getText().toString());


             String myUrl = builder.build().toString();

             as.execute();


             as.u = myUrl;

         } else {
             Toast.makeText(register.this, result.toString(), Toast.LENGTH_LONG).show();

         }




         }

            /*t2.setText(result.toString());
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("id" , result.toString());
            sendgps();
           /* res = result.toString();
            result.trim();
            if(res.equalsIgnoreCase("Succeed") ){

                Toast.makeText(login.this, "Log In Succesfull!", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(login.this, "Username or password is incorrect!", Toast.LENGTH_LONG).show();
            }*/

        }




    private class AsyncRetrieve1 extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(register.this);
        HttpURLConnection conn;
        URL url = null;
        String u;
        String res;

        public AsyncRetrieve1() {

        }

        //this method will interact with UI, here display loading message
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoading.setMessage("\tRegistering..");
            pdLoading.setCancelable(true);
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
            SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.mypreference, Context.MODE_PRIVATE);

            // you to understand error returned from doInBackground method
            result.trim();

            res = result.toString();
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("AUId", res);
           // editor.putString("Approval","0");

            editor.commit();
            Toast.makeText(register.this, "Account Created!!", Toast.LENGTH_LONG).show();
            finishAffinity();
            finish();
            if(userType.equalsIgnoreCase("Citizen")){
                Intent activityChangeIntent = new Intent(register.this, CitizenActivity.class);
                // currentContext.startActivity(activityChangeIntent);
                finishAffinity();

                startActivity(activityChangeIntent);
            }
            else{
                Intent activityChangeIntent = new Intent(register.this, DriverActivity.class);
                // currentContext.startActivity(activityChangeIntent);
                finishAffinity();
                startActivity(activityChangeIntent);
            }

        }

    }
}



