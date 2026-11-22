/*  Starter project for Mobile Platform Development - 1st diet 26/27
    You should use this project as the starting point for your assignment.
    This project simply reads the data from the required source and displays the
    raw data in a TextField
*/

//
// Name                 _________________
// Student ID           _________________
// Programme of Study   _________________
//

// UPDATE THE PACKAGE NAME to include your Student Identifier
package com.example.mcardle_daniel_s2375827;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.app.Dialog;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigationevent.ViewTreeNavigationEventDispatcherOwner;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Base64;

// temp imports for debugging
import java.io.File;
import java.io.FileWriter;
import java.util.Comparator;
import java.util.Date;
import java.util.function.ToDoubleFunction;

import kotlin.CompareToKt;

import android.os.Bundle;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.activity.EdgeToEdge;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.mcardle_daniel_s2375827.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, OnClickListener, OnMapReadyCallback {
    private Button clearButton;
    private ArrayList<Situation> allSituations;
    ArrayList<Situation> searchSituations;
    ArrayList<Situation> sortedSituations;
    ArrayList<Situation> visibleSituations;
    private ListView listView;
    private ListView toLocationListView;
    private ListView fromLocationListView;
    private TextView impactView;
    private TextView startTimeView;
    private ImageButton searchButton;
    private EditText searchTerm;
    private String roadEntered;
    private Boolean isSearching;
    private Button sortButton;
    private Boolean isSorting;
    private GoogleMap mMap;
    private ViewSwitcher avw;
    private ViewSwitcher barView;
    private Button mapButton;
    private Button listButton;
    private SituationViewModel viewModel;
    private Handler mHandler;
    private ProgressBar mProgressBar;



    //DOWNLOADING FROM TRAFFIC SCOTLAND
    private String clientId = "";
    private String clientKey = "";
    final String unplannedEventsURL = "https://datex2.trafficscotland.org/rest/2.3/publications/UnplannedEvents/Content.xml";
    final String roadworksURL = "https://datex2.trafficscotland.org/rest/2.3/publications/CurrentRoadworks/Content.xml";
    final double latitude = 55.8612;
    final double longitude = -4.2503;
    final int currentProgressCount = 25;
    final int refreshMs = 60 * 60 * 1000;

    private final Runnable refreshTask = new Runnable() {
        public void run() {
            startProgress();
            mHandler.postDelayed(this, refreshMs);
        }
    };

    private final Runnable retryConnection = new Runnable() {
        public void run() {
            startProgress();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String mapsKey="";

        Bundle metaData = null;
        try {
            metaData = getPackageManager().
                    getApplicationInfo(getPackageName(),PackageManager.GET_META_DATA).metaData;
            clientKey = metaData.getString("MCARDLE_DANIEL_s2375827.TS_KEY");
            clientId = metaData.getString("MCARDLE_DANIEL_s2375827.TS_ID");
            mapsKey = metaData.getString("com.google.android.geo.MAPS_KEY");
        } catch (PackageManager.NameNotFoundException e) { throw new RuntimeException(e); }
        Log.d("My key: ", clientKey + clientId + mapsKey);

        clearButton = (Button)findViewById(R.id.clearButton);
        clearButton.setOnClickListener(this);

        listView = (ListView) findViewById(R.id.list);
        listView.setOnItemClickListener(this);

        searchButton = (ImageButton)findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);
        searchTerm = (EditText)findViewById(R.id.searchBar);
        isSearching = false;

        sortButton = (Button)findViewById(R.id.sortButton);
        sortButton.setOnClickListener(this);
        isSorting = false;

        avw = (ViewSwitcher) findViewById(R.id.vwSwitch);
        if (avw == null)
        {
            Toast.makeText(getApplicationContext(), "Null ViewSwicther",
                    Toast.LENGTH_LONG);
            Log.e(getPackageName(), "null pointer");
        }

        barView = (ViewSwitcher) findViewById(R.id.vwSwitchBar);
        if (barView == null)
        {
            Toast.makeText(getApplicationContext(), "Null ViewSwicther",
                    Toast.LENGTH_LONG);
            Log.e(getPackageName(), "null pointer");
        }

        mapButton = (Button) findViewById(R.id.mapButton);
        mapButton.setOnClickListener(this);

        listButton = (Button) findViewById(R.id.listButton);
        listButton.setOnClickListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        viewModel = new ViewModelProvider(this).get(SituationViewModel.class);
        allSituations = viewModel.getAllSituations();
        visibleSituations = viewModel.getVisibleSituations();
        mHandler=new Handler();
        mProgressBar= findViewById(R.id.bar1);
        avw.setDisplayedChild(viewModel.getCurrentScreen());
        barView.setDisplayedChild(viewModel.getLoadingStatusScreen());


        if (visibleSituations != null && !visibleSituations.isEmpty()) {
            SituationAdapter adapter = new SituationAdapter(MainActivity.this, visibleSituations);
            listView.setAdapter(adapter);
        } else {
            startProgress();
        }

        mHandler.postDelayed(refreshTask, refreshMs);


    }

    protected void onPause() {
        super.onPause();
        mHandler.postDelayed(refreshTask, 10000);
    }

    public void onClick(View aview)
    {
        if (aview == clearButton) {
            SituationAdapter adapter = new SituationAdapter(MainActivity.this, allSituations);
            listView.setAdapter(adapter);
            viewModel.setAllSituations(allSituations);
            visibleSituations = allSituations;
            viewModel.setVisibleSituations(visibleSituations);
        }  else if (aview == searchButton) {
            isSearching = true;
            roadEntered = searchTerm.getText().toString();
            Log.e("ROAD SEARCH", roadEntered);
            searchSituations(roadEntered);
            viewModel.setAllSituations(allSituations);
            visibleSituations = searchSituations;
            viewModel.setVisibleSituations(visibleSituations);
            updateMap();
        } else if (aview == sortButton) {
            isSorting = true;
            sortSituations(latitude, longitude);
            viewModel.setAllSituations(allSituations);
            visibleSituations = sortedSituations;
            viewModel.setVisibleSituations(visibleSituations);
            updateMap();
        } else if (aview == mapButton) {
            avw.setDisplayedChild(1);
            viewModel.setCurrentScreen(avw.getDisplayedChild());
        } else if (aview == listButton) {
            avw.setDisplayedChild(0);
            viewModel.setCurrentScreen(avw.getDisplayedChild());
        }
    }


    public void startProgress()
    {
        barView.setDisplayedChild(0);
        allSituations = new ArrayList<>();
        mProgressBar.setProgress(0);
        // Run network access on a separate thread;
        new Thread(new Task(new String[]{unplannedEventsURL, roadworksURL})).start();
    } //

    private class Task implements Runnable
    {
        private String result="";
        private String[] myurls;
        BufferedReader in = null;
        String inputLine = "";
        public Task(String[] urls){
            myurls = urls;
        }
        @Override
        public void run() {
            Looper.prepare();
            Log.d("MyTask", "in run");
            for (String myurl : myurls) {

                result = "";
                try {
                    URL url = new URL(myurl);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    //ADD AUTHORISATION (string with clientIID:clientKey)
                    urlConnection.setRequestProperty("Authorization", Base64.getEncoder().encodeToString((clientId + ":" + clientKey).getBytes()));
                    try {
                        in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        while ((inputLine = in.readLine()) != null) {
                            result = result + inputLine;
                        }
                        in.close();
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "No Internet Connection. Retrying in 10 seconds...",
                                Toast.LENGTH_LONG).show();
                        mHandler.postDelayed(retryConnection, 10000);
                        Log.e("urlConnection", "ioexception");
                        return;
                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (IOException e) {
                    Log.e("URL", "ioexception");
                }

                mHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mProgressBar.incrementProgressBy(currentProgressCount);
                    }
                });


                //Clean up any leading garbage characters
                int i = result.indexOf("<?"); //initial tag
                result = result.substring(i);

                File file = new File(MainActivity.this.getExternalFilesDir(null), "feed.xml");
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(result);
                    Log.d("MyTag", "Saved XML to " + file.getAbsolutePath());
                } catch (IOException e) {
                    Log.e("MyTag", "Could not save XML", e);
                }

                parseData(result, myurl);
                mHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mProgressBar.incrementProgressBy(currentProgressCount);
                    }
                });
            }




//            MainActivity.this.runOnUiThread(new Runnable()
            mHandler.post(new Runnable() {
                public void run() {

                    SituationAdapter adapter = new SituationAdapter(MainActivity.this, allSituations);
                    viewModel.setAllSituations(allSituations);
                    visibleSituations = allSituations;
                    viewModel.setVisibleSituations(visibleSituations);
                    listView.setAdapter(adapter);
                    barView.setDisplayedChild(1);
                    viewModel.setLoadingStatusScreen(barView.getDisplayedChild());
                    updateMap();
                }
            });
        }

            private void parseData(String dataToParse, String url)
            {

                Situation aSituation = null;
                // Now that you have the xml data into result, you can parse it
                try {
                    boolean insideASituation = false;
                    boolean insideAPublicComment = false;
                    boolean insideATo = false;
                    boolean insideAFrom = false;
                    XmlPullParserFactory factory = null;
                    factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser xpp = null;
                    xpp = factory.newPullParser();
                    xpp.setInput(new StringReader(result));

                    int eventType = xpp.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_TAG) // Found a start tag
                        {
                            if (xpp.getName().equalsIgnoreCase("situation")) {
                                insideASituation = true;
                                //Start a new Situation object
                                aSituation = new Situation();
                                Log.d("MyTag", "New Situation found!");
                                Log.d("MyTag", url);
                                aSituation.setSourceURL(url);
                            } else if (xpp.getName().equalsIgnoreCase("generalPublicComment")) {
                                insideAPublicComment = true;
                            } else if (xpp.getName().equalsIgnoreCase("value") && insideASituation && insideAPublicComment) {
                                String temp = xpp.nextText();
                                aSituation.setComment(temp);
                                Log.d("MyTag", "Comment: " + temp);
                            } else if (xpp.getName().equalsIgnoreCase("situationRecord") && insideASituation) {
                                String temp = xpp.getAttributeValue(0);
                                aSituation.setType(temp);
                                Log.d("MyTag", "Type: " + temp);
                            } else if (xpp.getName().equalsIgnoreCase("tpegDirection") && insideASituation) {
                                String temp = xpp.nextText();
                                aSituation.setDirection(temp);
                                Log.d("MyTag", "Direction: " + temp);
                            } else if (xpp.getName().equalsIgnoreCase("to") && insideASituation) {
                                insideATo = true;
                            } else if (xpp.getName().equalsIgnoreCase("from") && insideASituation) {
                                insideAFrom = true;
                            } else if (xpp.getName().equalsIgnoreCase("latitude") && insideATo) {
                                String temp = xpp.nextText();
                                aSituation.setToLatitude(temp);
                                Log.d("MyTag", "To latitude: " + temp);
                            } else if (xpp.getName().equalsIgnoreCase("longitude") && insideATo) {
                                String temp = xpp.nextText();
                                aSituation.setToLongitude(temp);
                                Log.d("MyTag", "To longitude: " + temp);
                            } else if (xpp.getName().equalsIgnoreCase("value") && insideATo) {
                                String temp = xpp.nextText();
                                aSituation.setToLocations(temp);
                                Log.d("MyTag", "To locations: " + temp);
                            } else if (xpp.getName().equalsIgnoreCase("value") && insideAFrom) {
                                String temp = xpp.nextText();
                                aSituation.setFromLocations(temp);
                                Log.d("MyTag", "From locations: " + temp);
                            } else if (xpp.getName().equalsIgnoreCase("latitude") && insideAFrom) {
                                String temp = xpp.nextText();
                                aSituation.setFromLatitude(temp);
                                Log.d("MyTag", "From latitude: " + temp);
                            } else if (xpp.getName().equalsIgnoreCase("longitude") && insideAFrom) {
                                String temp = xpp.nextText();
                                aSituation.setFromLongitude(temp);
                                Log.d("MyTag", "From longitude: " + temp);
                            } else if (xpp.getName().equalsIgnoreCase("delaysType")) {
                                Log.d("MyTag", "Inside delays");
                                String temp = xpp.nextText();
                                String[] response = {"delays", temp};
                                aSituation.setImpact(response);
                                Log.d("MyTag", "Delay: " + temp);
                            } else if (xpp.getName().equalsIgnoreCase("overallStartTime")) {
                                Log.d("MyTag", "Inside overallStartTime");
                                String temp = xpp.nextText();
                                aSituation.setStartTime(temp);
                                Log.d("MyTag", "overallStartTime: " + temp);
                            } else if (xpp.getName().equalsIgnoreCase("numberOfOperationalLanes")) {
                                Log.d("MyTag", "Inside numberOfOperationalLanes");
                                String temp = xpp.nextText();
                                String[] response = {"numberOfOperationalLanes", temp};
                                aSituation.setImpact(response);
                                Log.d("MyTag", "numberOfOperationalLanes: " + temp);
                            } else if (xpp.getName().equalsIgnoreCase("numberOfLanesRestricted")) {
                                Log.d("MyTag", "Inside numberOfLanesRestricted");
                                String temp = xpp.nextText();
                                String[] response = {"numberOfLanesRestricted", temp};
                                aSituation.setImpact(response);
                                Log.d("MyTag", "numberOfLanesRestricted: " + temp);
                            }
                        } else if (eventType == XmlPullParser.END_TAG) // Found an end tag
                        {
                            if (xpp.getName().equalsIgnoreCase("situation")) {
                                allSituations.add(aSituation);
                                insideASituation = false;
                                Log.d("MyTag", "Situation parsing completed!");
                                Log.d("toString", aSituation.toString());
                            } else if (xpp.getName().equalsIgnoreCase("generalPublicComment")) {
                                insideAPublicComment = false;
                                Log.d("MyTag", "general public comment parsing completed!");
                            } else if (xpp.getName().equalsIgnoreCase("to")) {
                                insideATo = false;
                            } else if (xpp.getName().equalsIgnoreCase("from")) {
                                insideAFrom = false;
                            }
                        }

                        eventType = xpp.next(); // Get the next event  before looping again
                    } // End of while
                } catch (XmlPullParserException ex) {
                    Log.e("Parsing", "EXCEPTION" + ex.toString());
                    //throw new RuntimeException(e);
                } catch (IOException ae1) {
                    Log.d("MyTag", "IO error during parsing");
                }

                Log.d("MyTag", "End of document reached");

        }



    }

    public void searchSituations(String road){

        searchSituations = new ArrayList<>();
        for (int i = 0; i < allSituations.size(); i++) {

            ArrayList<String> toLocations = allSituations.get(i).getToLocations();
            ArrayList<String> fromLocations = allSituations.get(i).getFromLocations();


            if (toLocations.contains(road) || fromLocations.contains(road)) {
                searchSituations.add(allSituations.get(i));
        } else {
                Log.e("Filter Else", "Road not found");
            }
        }

        SituationAdapter adapter = new SituationAdapter(MainActivity.this, searchSituations);
        listView.setAdapter(adapter);
    }

    public double haversine(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }

    public double calculateDistance(double startLat, double startLong, double endLat, double endLong) {
        int EARTH_RADIUS = 6371;

        double dLat = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat = Math.toRadians(endLat);

        double a = haversine(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversine(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    public void sortSituations(double latitude, double longitude){
        sortedSituations = new ArrayList<>();

        if (visibleSituations != null && !visibleSituations.isEmpty()) {
            for (int i = 0; i < visibleSituations.size(); i++) {

                double fromLatitude = Double.parseDouble(visibleSituations.get(i).getFromLatitude());
                double fromLongitude = Double.parseDouble(visibleSituations.get(i).getFromLongitude());

                double distance = calculateDistance(latitude, longitude, fromLatitude, fromLongitude);

                sortedSituations.add(visibleSituations.get(i));
                sortedSituations.get(i).setDistance(distance);
            }
            sortedSituations.sort(Comparator.comparingDouble(Situation::getDistance).reversed());

            SituationAdapter adapter = new SituationAdapter(MainActivity.this, sortedSituations);
            listView.setAdapter(adapter);

        }
    }

    public void onItemClick(AdapterView<?> parent, View view,int position,long id)
    {
        ArrayList<String> toLocation = visibleSituations.get(position).getToLocations();
        ArrayList<String> fromLocation = visibleSituations.get(position).getFromLocations();
        String[] impact = visibleSituations.get(position).getImpact();
        String time = visibleSituations.get(position).getStartTime();
        String type = visibleSituations.get(position).getType();

        showcustomDialog(toLocation, fromLocation, impact, time, type);
    }
    private void showcustomDialog(ArrayList<String> toLocation, ArrayList<String> fromLocation, String[] impact, String time, String type)
    {
        // Custom dialog setup
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_info);
        dialog.setTitle("Incident Information");


        String[] startTimeResponseArray = time.split("T");
        String startTimeResponse = startTimeResponseArray[0] + " " + startTimeResponseArray[1];

        fromLocationListView = (ListView) dialog.findViewById(R.id.fromLocationView);
        toLocationListView = (ListView) dialog.findViewById(R.id.toLocationView);
        impactView = (TextView) dialog.findViewById(R.id.impactView);
        startTimeView = (TextView) dialog.findViewById(R.id.timeView);


        startTimeView.setText(startTimeResponse);


        ArrayAdapter fromLocationAdapter = new ArrayAdapter<String>(MainActivity.this,
                R.layout.activity_listview, fromLocation);

        if (fromLocationAdapter == null)
        {
            Log.e("MyTag","Adapter error");
        }

        ArrayAdapter toLocationAdapter = new ArrayAdapter<String>(MainActivity.this,
                R.layout.activity_listview, toLocation);

        if (toLocationAdapter == null)
        {
            Log.e("MyTag","Adapter error");
        }

        fromLocationListView.setAdapter(fromLocationAdapter);
        toLocationListView.setAdapter(toLocationAdapter);

        String impactResponse;

        if (impact != null && impact.length >= 2)
        {
            impactResponse = impact[0] + " = " + impact[1];
        }
        else
        {
            impactResponse = type;
        }

        Log.e("DEBUG", impactResponse);

        impactView.setText(impactResponse);

        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        dialogButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                dialog.dismiss();

            }
        });

        dialog.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng glasgow = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(glasgow, 10));
        if (mMap != null && allSituations != null && !allSituations.isEmpty()) {
            updateMap();
        }
    }

    public void updateMap() {
        mMap.clear();

        LatLng glasgow = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(glasgow, 10));


        for (int i=0; i < visibleSituations.size(); i++){
            Log.e("map debug", "in loop");
            double fromLatitude = Double.parseDouble(visibleSituations.get(i).getFromLatitude());
            double fromLongitude = Double.parseDouble(visibleSituations.get(i).getFromLongitude());
            LatLng situationCoords = new LatLng(fromLatitude, fromLongitude);
            mMap.addMarker(new MarkerOptions().position(situationCoords).title(visibleSituations.get(i).getComment()));
        }
    }

}