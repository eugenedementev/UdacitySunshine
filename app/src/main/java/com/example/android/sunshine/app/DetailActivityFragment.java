package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.android.sunshine.app.data.WeatherContract.*;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private ShareActionProvider mShareActionProvider;

    private static final int LOADER_ID = 0;

    private static final String[] FORECAST_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_WEATHER_ID,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_DEGREES,
            WeatherEntry.COLUMN_PRESSURE
            };
            // these constants correspond to the projection defined above, and must change if the
            // projection changes
    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_CONDITION_ID = 2;
    private static final int COL_WEATHER_DESC = 3;
    private static final int COL_WEATHER_MAX_TEMP = 4;
    private static final int COL_WEATHER_MIN_TEMP = 5;
    private static final int COL_WEATHER_HUMIDITY = 6;
    private static final int COL_WEATHER_WIND_SPEED = 7;
    private static final int COL_WEATHER_DEGREES = 8;
    private static final int COL_WEATHER_PRESSURE = 9;


    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private String mForecastString;

    private ImageView mIconWeather;
    private TextView mWeatherDay;
    private TextView mWeatherDate;
    private TextView mWeatherDescription;
    private TextView mMaxTemperature;
    private TextView mMinTemperature;
    private TextView mWeatherHumidity;
    private TextView mWeatherWind;
    private TextView mWeatherPressure;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mIconWeather = (ImageView) rootView.findViewById(R.id.fragment_detail_weather_icon);
        mWeatherDay = (TextView) rootView.findViewById(R.id.fragment_detail_day_textview);
        mWeatherDate = (TextView)rootView.findViewById(R.id.fragment_detail_date_textview);
        mWeatherDescription = (TextView) rootView.findViewById(R.id.fragment_detail_description_textview);
        mMaxTemperature = (TextView) rootView.findViewById(R.id.fragment_detail_high_textview);
        mMinTemperature = (TextView) rootView.findViewById(R.id.fragment_detail_min_textview);
        mWeatherHumidity = (TextView) rootView.findViewById(R.id.fragment_detail_humidity_textview);
        mWeatherWind = (TextView) rootView.findViewById(R.id.fragment_detail_wind_textview);
        mWeatherPressure = (TextView) rootView.findViewById(R.id.fragment_detail_pressure);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if (mForecastString != null){
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        if (intent == null){
            return null;
        }
        return new CursorLoader(
                getActivity(),
                intent.getData(),
                FORECAST_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()){
            return;
        }

        boolean isMetric = Utility.isMetric(getActivity());

        String dayName = Utility.getDayName(getActivity(),data.getLong(COL_WEATHER_DATE));
        String dateString = Utility.formatDate(data.getLong(COL_WEATHER_DATE));
        int iconId = Utility.getArtResourceForWeatherCondition(data.getInt(COL_WEATHER_CONDITION_ID));
        String weatherDescription = data.getString(COL_WEATHER_DESC);
        String high = Utility.formatTemperature(
                getActivity(),
                data.getDouble(COL_WEATHER_MAX_TEMP),
                isMetric
        );
        String low = Utility.formatTemperature(
                getActivity(),
                data.getDouble(COL_WEATHER_MIN_TEMP),
                isMetric
        );
        String humidity = Utility.getFormattedHumidity(getActivity(),data.getDouble(COL_WEATHER_HUMIDITY));
        String wind = Utility.getFormattedWind(getActivity(),data.getFloat(COL_WEATHER_WIND_SPEED),data.getFloat(COL_WEATHER_DEGREES));
        String pressure = Utility.getFormattedPressure(getActivity(),data.getFloat(COL_WEATHER_PRESSURE));

        mForecastString = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);

        mWeatherDay.setText(dayName);
        mWeatherDate.setText(dateString);
        mWeatherDescription.setText(weatherDescription);
        mMaxTemperature.setText(high);
        mMinTemperature.setText(low);
        mWeatherHumidity.setText(humidity);
        mWeatherWind.setText(wind);
        mWeatherPressure.setText(pressure);
        mIconWeather.setImageResource(iconId);

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastString + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }




}
