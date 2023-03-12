package com.example.runapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class showActivities extends AppCompatActivity {
    Button back3;
    RecyclerView recyclerView;
    List<MyModel> myModelList;
    CustomAdapter customAdapter;
    private Float time;
    private Float distance;
    private String username = MainActivity.getLoggedInUser();
    private String[] points;
    private int radius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_activities);
        back3 = findViewById(R.id.back3);
        //inputs from set new activity
        SharedPreferences prefs = getSharedPreferences("MY_DATA", MODE_PRIVATE);

        //set values
        time = prefs.getFloat("time", 0);
        distance = prefs.getFloat("distance", 0);

        displayItems();

        back3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(showActivities.this, FirstActivity.class);
                startActivity(i);
            }
        });
    }

    private void displayItems() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        myModelList = new ArrayList<>();
        String username = MainActivity.getLoggedInUser();
        //TODO(noa): fetch longitude latitude and radius.
        //fetchActivities();
        customAdapter = new CustomAdapter(this, myModelList);
        recyclerView.setAdapter(customAdapter);
    }

    private void fetchActivities(double latitude, double longitude, int radius) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String url = String.format("http://10.0.2.2:3000/activities?latitude=%f&longitude=%f&radius=%d", latitude, longitude, radius);
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONArray jsonActivities = new JSONArray(responseBody);

                        // Iterate through the JSON array and extract the activity data
                        List<List> activities = new ArrayList<>();
                        for (int i = 0; i < jsonActivities.length(); i++) {
                            JSONObject jsonActivity = jsonActivities.getJSONObject(i);

                            // Extract the activity data from the JSON object
                            String username = jsonActivity.getString("username");
                            int distance = jsonActivity.getInt("distance");
                            int time = jsonActivity.getInt("time");

                            // Fetch the user data from the server
                            String userUrl = String.format("10.0.2.2:3000/users?username=%s", username);
                            Request userRequest = new Request.Builder().url(userUrl).build();

                            Response userResponse = client.newCall(userRequest).execute();
                            if (!userResponse.isSuccessful())
                                throw new IOException("Unexpected code " + userResponse);

                            String userResponseBody = userResponse.body().string();
                            JSONArray jsonUsers = new JSONArray(userResponseBody);

                            // Extract the user data from the JSON object
                            JSONObject jsonUser = jsonUsers.getJSONObject(0);
                            String firstName = jsonUser.getString("firstName");
                            String lastName = jsonUser.getString("lastName");

                            // Create an activity object and add it to the list
                            myModelList.add(new MyModel(firstName + " " + lastName, time, distance));
                        }
                    } catch (JSONException e) {
                        Log.e("parsingError", "error parsing json", e);
                    }
                }
            }
        });
    }

    //TODO(noa): complete fetchUserAndLocationRadius
    private void fetchUserLocationAndRadius(String username) {
        OkHttpClient client = new OkHttpClient();

        String url = String.format("http://10.0.2.2:3000/users?username=%s", username);
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle the error
                Log.e("fetchError", "Error fetching user location and radius", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONArray jsonUsers = new JSONArray(responseBody);

                        // Extract the user data from the JSON object
                        JSONObject jsonUser = jsonUsers.getJSONObject(0);
                        double latitude = jsonUser.getDouble("latitude");
                        double longitude = jsonUser.getDouble("longitude");
                        int radius = jsonUser.getInt("radius");

                        // Update the UI with the user data
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Do something with the user data
                            }
                        });
                    } catch (JSONException e) {
                        // Handle the error
                        Log.e("parseError", "Error parsing JSON response", e);
                    }
                }
            }
        });
    }

}
