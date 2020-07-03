package com.example.necoo.reklamyonetimsistemi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.AsyncListUtil;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ApplicationActivity extends AppCompatActivity {

    NodeAPI nodeAPI = null;
    TextView info = null;
    Spinner range = null;
    EditText latitude = null;
    EditText longitude = null;
    List<Campaign> campaigns = null;
    String apiURL;
    FileOps fileOps = new FileOps();
    static String companyName = "";
    private LocationManager locationManager;
    private LocationListener listener;
    private List<Campaign> lastList = new ArrayList<>();

    //HashMap<Integer,List<String>> companyMap = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(getApplicationContext(), "Giriş Başarılı.", Toast.LENGTH_LONG).show();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        info = findViewById(R.id.info);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longtitude);
        range = findViewById(R.id.rangeSpinner);

        apiURL = getResources().getString(R.string.api_url);
        campaigns = getCampaigns("all", "all");
        showCampaigns(campaigns);
        fileOps.write(this, "filter.txt", "Hepsi");

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String message = "Bu arama için bir kampanya bulunamadı.";
                String category = fileOps.read(getApplicationContext(), "filter.txt");
                System.out.println(category + "-" + companyName + "1");
                if (category.equalsIgnoreCase("Hepsi"))
                    category = "all";
                if (companyName.equals(""))
                    companyName = "all";

                campaigns = getCampaigns(category, companyName);
                int size = campaigns.size();
                showCampaigns(campaigns);

                if (size != 0)
                    message = size + " kayıt bulundu.";

                Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                String category = fileOps.read(getApplicationContext(), "filter.txt");

                if (category.equalsIgnoreCase("Hepsi"))
                    category = "all";

                if (companyName == null || companyName.equals("") || companyName.equalsIgnoreCase("Default Value"))
                    companyName = "all";

                campaigns = getCampaigns(category, companyName);


                Location here = new Location("");
                Location other = new Location("");

                if (!latitude.getText().toString().isEmpty() && !longitude.getText().toString().isEmpty()) {
                    here.setLatitude(Double.parseDouble(latitude.getText().toString()));
                    here.setLongitude(Double.parseDouble(longitude.getText().toString()));
                } else {
                    here.setLatitude(location.getLatitude());
                    here.setLongitude(location.getLongitude());
                }


                if (!range.getSelectedItem().toString().equals("All")) {


                    String temp = range.getSelectedItem().toString();
                    temp = temp.substring(0, temp.length() - 7);
                    int maxRange = Integer.parseInt(temp);

                    List<Campaign> filtered = new ArrayList<>();
                    boolean isFound = false;

                    try {
                        for (Campaign campaign : campaigns) {
                            String loc = campaign.getCompanyLocation();
                            String[] locs = loc.split("-");

                            other.setLatitude(Double.parseDouble(locs[0]));
                            other.setLongitude(Double.parseDouble(locs[1]));
                            double distance = here.distanceTo(other);
                            if (distance < maxRange) {
                                filtered.add(campaign);
                                isFound = true;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    NotificationHelper notificationHelper = new NotificationHelper(ApplicationActivity.this);


                    boolean isEqual = compareLists(filtered, lastList);
                    if (isFound && !isEqual) {
                        notificationHelper.notify("Yeni Kampanya var :) ", "Az önce yeni bir kampnaya bulundu.");
                        showCampaigns(filtered);
                    } else
                        showCampaigns(filtered);

                    lastList = new ArrayList<>(filtered);

                } else {
                    showCampaigns(campaigns);
                }


                System.out.println("yenilendi");
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        configure_button();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private List<Campaign> getCampaigns(String category, String companyName) {
        try {
            URL url = new URL(apiURL);
            nodeAPI = new NodeAPI(url);

            String jsonResponse = nodeAPI.requestCampaigns(category, companyName);
            List<Campaign> campaigns = nodeAPI.getCampaignsList(jsonResponse); // List<Campaigns>
            return campaigns;

        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    private void showCampaigns(List<Campaign> campaigns) {
        //TODO: Use HTML formatting instead string builder
        StringBuilder sb = new StringBuilder();
        for (Campaign campaign : campaigns) {
            sb.append("Company Name : " + campaign.getCompanyName() + "\n");
            sb.append("Category : " + campaign.getCampaignCategory() + "\n");
            sb.append("Info : " + campaign.getCampaignInfo() + "\n");
            sb.append("Dead Line : " + campaign.getCampaignDeadLine() + "\n");
            sb.append("Location " + campaign.getCompanyLocation() + "\n\n\n");
        }

        sb.append("");
        info.setText(sb.toString());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }

    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.

        try {
            //noinspection MissingPermission
            locationManager.requestLocationUpdates("gps", 5000, 0, listener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }


    }

    public boolean compareLists(List<Campaign> first, List<Campaign> second) {

        if (first.size() == second.size()) {
            for (int i = 0; i < first.size(); i++) {
                if(!first.get(i).getCampaignDeadLine().equalsIgnoreCase(second.get(i).getCampaignDeadLine()))
                    return false;
                if(!first.get(i).getCompanyLocation().equalsIgnoreCase(second.get(i).getCompanyLocation()))
                    return false;
                if(!first.get(i).getCampaignInfo().equalsIgnoreCase(second.get(i).getCampaignInfo()))
                    return false;
                if(!first.get(i).getCampaignCategory().equalsIgnoreCase(second.get(i).getCampaignCategory()))
                    return false;
                if(first.get(i).getCompanyID() != second.get(i).getCompanyID())
                    return false;
                if(!first.get(i).getCompanyName().equalsIgnoreCase(second.get(i).getCompanyName()))
                    return false;
            }

            return true;
        } else
            return false;
    }

}
