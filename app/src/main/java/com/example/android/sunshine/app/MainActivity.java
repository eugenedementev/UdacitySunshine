package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String FORECASTFRAGMENT_TAG = "FFTAG";
    private String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocation = Utility.getPreferredLocation(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment(),FORECASTFRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        String location = Utility.getPreferredLocation(this);
        if (location != null && !location.equals(mLocation)){
            ForecastFragment forecastFragment = (ForecastFragment)getSupportFragmentManager().findFragmentByTag(FORECASTFRAGMENT_TAG);
            if (forecastFragment != null){
                forecastFragment.onLocationChanged();
            }
            mLocation = location;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_settings:
                Intent startSettingsActivityIntent = new Intent(this,SettingsActivity.class);
                startActivity(startSettingsActivityIntent);
                return true;
            case R.id.action_map:
                openPrefferedLocationMap();
                return true;
        }
//        if (id == R.id.action_settings) {
//            Intent startSettingsActivityIntent = new Intent(this,SettingsActivity.class);
//            startActivity(startSettingsActivityIntent);
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    private void openPrefferedLocationMap(){
        String locationFromTheSettings = Utility.getPreferredLocation(this);

                Uri geoLocationUri = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q",locationFromTheSettings)
                .build();
        Intent openMapIntent = new Intent(Intent.ACTION_VIEW);
        openMapIntent.setData(geoLocationUri);

        if (openMapIntent.resolveActivity(getPackageManager()) != null){
            startActivity(openMapIntent);
        }else{
            Log.d(LOG_TAG, "Couldn't call " + locationFromTheSettings + ", no receiving apps installed!");
        }
    }

}
