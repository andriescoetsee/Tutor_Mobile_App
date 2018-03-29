/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.android.sunshine.utilities.NetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView mSarahTodaySessions;
    private TextView mSarahTomorrowSessions;
    private TextView mChristiaanTodaySessions;
    private TextView mChristiaanTomorrowSessions;
    private TextView mTodayDate;
    private TextView mTomorrowDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        String accessToken = BuildConfig.ACCESS_TOKEN;

//        Log.i("ID ----------->",accessToken);


        /*
         * Using findViewById, we get a reference to our TextView from xml. This allows us to
         * do things like set the text of the TextView.
         */
        mSarahTodaySessions = (TextView) findViewById(R.id.sarah_today_sessions);
        mSarahTomorrowSessions = (TextView) findViewById(R.id.sarah_tomorrow_sessions);
        mChristiaanTodaySessions = (TextView) findViewById(R.id.christiaan_today_sessions);
        mChristiaanTomorrowSessions = (TextView) findViewById(R.id.christiaan_tomorrow_sessions);
        mTodayDate = (TextView) findViewById(R.id.today_date);
        mTomorrowDate = (TextView) findViewById(R.id.tomorrow_date);

        /* Once all of our views are setup, we can load the weather data. */
        mSarahTodaySessions.setText("...loading...");
        mSarahTomorrowSessions.setText("...loading...");
        mChristiaanTodaySessions.setText("...loading...");
        mChristiaanTomorrowSessions.setText("...loading...");

        loadTutorData();
    }

    /**
     * This method will get the user's preferred location for weather, and then tell some
     * background method to get the weather data in the background.
     */
    private void loadTutorData() {

        new FetchTutorTask().execute();
    }

    public class FetchTutorTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            URL tutorRequestUrl = NetworkUtils.buildUrl();

            try {
                String jsonTutorResponse = NetworkUtils
                        .getResponseFromHttpUrl(tutorRequestUrl);

                return jsonTutorResponse;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String eventsData) {
            if (eventsData != null) {

                final String EVENT_INSTRUCTOR = "instructor";
                final String EVENT_STUDENT = "student";
                final String EVENT_DT_TYPE = "dt_type";
                final String EVENT_FROM_TO_TIME = "from_to_time";
                final String EVENT_DT = "dt";

                JSONArray eventsArray = null;

                mSarahTodaySessions.setText("");
                mSarahTomorrowSessions.setText("");
                mChristiaanTodaySessions.setText("");
                mChristiaanTomorrowSessions.setText("");

                try {
                    eventsArray = new JSONArray(eventsData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < eventsArray.length(); i++) {

                    String instructor = "", student = "", date_type = "", from_to_time = "", dt = "";

                    JSONObject event = null;
                    try {
                        event = eventsArray.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        student = event.getString(EVENT_STUDENT);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        instructor = event.getString(EVENT_INSTRUCTOR);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    try {
                        date_type = event.getString(EVENT_DT_TYPE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        from_to_time = event.getString(EVENT_FROM_TO_TIME);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        dt = event.getString(EVENT_DT);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // take care of the Today and Tomorrow Date
                    if (date_type.equals("TODAY") ){
                        mTodayDate.setText( dt );
                    }
                    else if (date_type.equals("TOMORROW") ){
                        mTomorrowDate.setText( dt );
                    }
                    else {
                        mTomorrowDate.setText( "No sessions" );
                        mTodayDate.setText( "No sessions" );
                    }

                    // now take care of the events and putting it in the correct window
                    if ((instructor.equals("Sarah")) &&  (date_type.equals("TODAY"))) {
                        mSarahTodaySessions.append((from_to_time) + "\n");
                        mSarahTodaySessions.append("(" + (student) + ")\n\n");

                    }
                    else if  ((instructor.equals("Sarah")) &&  (date_type.equals("TOMORROW"))) {
                        mSarahTomorrowSessions.append((from_to_time)  + "\n");
                    }
                    else if  ((instructor.equals("Christiaan")) &&  (date_type.equals("TODAY"))) {
                        mChristiaanTodaySessions.append((from_to_time)  + "\n");
                        mChristiaanTodaySessions.append("(" + (student) + ")\n\n");


                    }
                    else if  ((instructor.equals("Christiaan")) &&  (date_type.equals("TOMORROW"))) {
                        mChristiaanTomorrowSessions.append((from_to_time)  + "\n");
                    }
                }
            }
        }
    }


    // COMPLETED (5) Override onCreateOptionsMenu to inflate the menu for this Activity
    // COMPLETED (6) Return true to display the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.forecast, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    // COMPLETED (7) Override onOptionsItemSelected to handle clicks on the refresh button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            mSarahTodaySessions.setText("...loading...");
            mSarahTomorrowSessions.setText("...loading...");
            mChristiaanTodaySessions.setText("...loading...");
            mChristiaanTomorrowSessions.setText("...loading...");

            loadTutorData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}