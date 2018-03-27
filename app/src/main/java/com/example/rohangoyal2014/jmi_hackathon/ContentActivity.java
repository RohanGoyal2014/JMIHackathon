package com.example.rohangoyal2014.jmi_hackathon;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.List;

public class ContentActivity extends AppCompatActivity{

    private static String TAG="ContentActivity";
    private EditText searchView;
    private ImageView searchButton;
    private ListView listView;
    private ProgressBar progressBar;
    private DataAdapter dataAdapter;
    private List<Data> dataArrayList;
    String loc;


    LocationManager locationManager;
    LocationListener locationListener;
    TextView textView;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1){

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

            }

        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        listView= findViewById(R.id.list);
        searchButton=findViewById(R.id.search_image);
        searchView=findViewById(R.id.search_view);
        progressBar=findViewById(R.id.progress_bar);
        dataArrayList=new ArrayList<>();
        dataAdapter=new DataAdapter(this,dataArrayList);
        listView.setAdapter(dataAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(
                        new Intent(ContentActivity.this,BookActivity.class
                ).putExtra("name",dataArrayList.get(i).name).putExtra("vicinity",dataArrayList.get(i).vicinity)
                );
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if(searchView.getText().toString().trim().isEmpty()){
                    Toast.makeText(ContentActivity.this,"Enter some keyword",Toast.LENGTH_SHORT).show();
                } else{
                    String keyword=searchView.getText().toString().trim();
                    String[] s=new String[2];
                    //s[0]="28.646556,77.344179";
                    s[0]=loc;
                    s[1]=keyword;
                    try {
                        doWork(s[0],s[1]);
                        //new JSONTask().execute(s);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Location");
        progressDialog.show();
        //Setting up User Location
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Log.i("location",location.toString());
                loc=String.valueOf(location.getLatitude())+","+String.valueOf(location.getLongitude());
                progressDialog.dismiss();

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(getApplicationContext(), "Enable GPS", Toast.LENGTH_SHORT).show();
            }
        };

        if (Build.VERSION.SDK_INT < 23) {

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},1);
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }
            else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
        }

        //Checking for permission

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            //asking for permission
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},1);

        } else {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,10,20,locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        }

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


    public void doWork(String location,String keyword){
        progressBar.setVisibility(View.VISIBLE);
        final int DEFAULT_RADIUS = 5000;
        final String TYPE_VALUE = "doctor";
        final String KEY_VALUE = "AIzaSyAhLNwq7VVeLZjvOVnWygl3hYMgn96G24c";
        String api_end_point = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
        String QUERY_KEY = "key";
        String QUERY_TYPE = "type";
        String QUERY_KEYWORD = "keyword";
        String QUERY_RADIUS = "radius";
        String QUERY_LOCATION = "location";
        ArrayList<Data> arrayList = new ArrayList<>();
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
                            Log.e(TAG, "Response received");

                            convertToJsonObject(response);
                            for(int i=0;i<dataArrayList.size();++i)
                            {
                                Log.e(TAG,dataArrayList.get(i).icon+" "+dataArrayList.get(i).id+" "+dataArrayList.get(i).name);
                            }
                            dataAdapter.notifyDataSetChanged();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Response not received");
                    Toast.makeText(ContentActivity.this, "Response not received", Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(stringRequest);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        progressBar.setVisibility(View.GONE);
    }




    public void convertToJsonObject(String response)
    {
        dataArrayList.clear();
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
                Log.e(TAG,name);
                String rating;
                try {
                     rating = jsonObject1.getString("rating");
                } catch (JSONException e)
                {
                    rating="No";
                }
                dataArrayList.add(new Data(place_id,name,vicinity,icon,rating));
                Log.e(TAG,"Size of arraylist is:"+String.valueOf(dataArrayList.size()));
            }

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        finally {

            dataAdapter.notifyDataSetChanged();
        }
    }
}
