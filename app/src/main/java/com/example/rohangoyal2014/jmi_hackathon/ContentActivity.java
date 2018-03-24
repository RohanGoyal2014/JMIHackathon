package com.example.rohangoyal2014.jmi_hackathon;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

public class ContentActivity extends AppCompatActivity implements DataAdapter.ListItemClickListener{

    private static String TAG="ContentActivity";
    private EditText searchView;
    private ImageView searchButton;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private DataAdapter dataAdapter;
    private ArrayList<Data> dataArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        recyclerView= findViewById(R.id.recycler_view);
        searchButton=findViewById(R.id.search_image);
        searchView=findViewById(R.id.search_view);
        progressBar=findViewById(R.id.progress_bar);
        dataArrayList=new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(false);
        dataAdapter=new DataAdapter(dataArrayList,this,this);
        recyclerView.setAdapter(dataAdapter);
        searchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if(searchView.getText().toString().trim().isEmpty()){
                    Toast.makeText(ContentActivity.this,"Enter some keyword",Toast.LENGTH_SHORT).show();
                } else{
                    String keyword=searchView.getText().toString().trim();
                    String[] s=new String[2];
                    s[0]="28.646556,77.344179";
                    s[1]=keyword;
                    try {
                        new JSONTask().execute(s).get();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.signout, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.signout){
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                    });
        }
        return true;
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Toast.makeText(this,"Clicked",Toast.LENGTH_SHORT).show();
    }

    public class JSONTask extends AsyncTask<String,Void,ArrayList<Data>>{
        final int DEFAULT_RADIUS = 5000;
        final String TYPE_VALUE = "doctor";
        final String KEY_VALUE = "AIzaSyAhLNwq7VVeLZjvOVnWygl3hYMgn96G24c";
        String api_end_point = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
        String QUERY_KEY = "key";
        String QUERY_TYPE = "type";
        String QUERY_KEYWORD = "keyword";
        String QUERY_RADIUS = "radius";
        String QUERY_LOCATION = "location";
        ArrayList<Data> arrayList=new ArrayList<>();
        @Override
        protected ArrayList<Data> doInBackground(String... strings) {
            String location=strings[0];
            String keyword=strings[1];
            String url = api_end_point + QUERY_LOCATION + "=" + location + "&" +
                    QUERY_RADIUS + "=" + String.valueOf(DEFAULT_RADIUS) + "&" +
                    QUERY_KEYWORD + "=" + keyword + "&" +
                    QUERY_TYPE + "=" + TYPE_VALUE + "&" +
                    QUERY_KEY + "=" + KEY_VALUE;
            Log.e(TAG, url);
            try {
                URL entire_URL = URI.create(url).toURL();
                RequestQueue queue = Volley.newRequestQueue(ContentActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, entire_URL.toString(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.e(TAG,"Response received");
                                //Log.e(TAG,response);
                                arrayList=convertToJsonObject(response);
                                dataArrayList=arrayList;
                                Log.e(TAG,String.valueOf(dataArrayList.size()));
                                dataAdapter.notifyDataSetChanged();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG,"Response not received");
                        Toast.makeText(ContentActivity.this,"Response not received",Toast.LENGTH_SHORT).show();
                    }
                });
                queue.add(stringRequest);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Data> arrayList) {
            super.onPostExecute(arrayList);
            Log.e(TAG,"Aa agaya");
            //updateUI(arrayList);
        }
    }
    void updateUI(ArrayList<Data> arrayList){


        // COMPLETED (13) Pass in this as the ListItemClickListener to the GreenAdapter constructor
        /*
         * The GreenAdapter is responsible for displaying each item in the list.
         */
        dataAdapter = new DataAdapter(arrayList, this,this);
        Log.e(TAG,"Setting adapter");
    }

    public ArrayList<Data> convertToJsonObject(String response)
    {
        ArrayList<Data> arrayList=new ArrayList<>();
        try{
            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("results");
            for(int i=0;i<jsonArray.length();++i)
            {
                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                String name=jsonObject1.getString("name");
                String place_id=jsonObject1.getString("place_id");
                String vicinity=jsonObject1.getString("vicinity");
                String icon=jsonObject1.getString("icon");
                String rating;
                try {
                     rating = jsonObject1.getString("rating");
                } catch (JSONException e)
                {
                    rating="No";
                }
                arrayList.add(new Data(place_id,name,vicinity,icon,rating));
                Log.e(TAG,"Size of arraylist is:"+String.valueOf(arrayList.size()));
            }

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        finally {
            return arrayList;
        }
    }
}
