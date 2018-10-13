package com.vit.gokul.froggybuddy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.vit.gokul.froggybuddy.R;
import com.vit.gokul.froggybuddy.changeCity;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HeaderElementIterator;


public class WeatherController extends AppCompatActivity {

    // Constants:
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    final int REQUEST_CODE=1234;
    // App ID to use OpenWeather data
    final String APP_ID = "f3d6619f496e4adbdc9cde89e1a00f5c";
    // Time between location updates (5000 milliseconds or 5 seconds)
    final long MIN_TIME = 5000;
    // Distance between location updates (1000m or 1km)
    final float MIN_DISTANCE = 1000;

    // TODO: Set LOCATION_PROVIDER here:
    String Location_provider = LocationManager.GPS_PROVIDER;


    // Member Variables:
    TextView mCityLabel;
    ImageView mWeatherImage;
    TextView mTemperatureLabel;

    // TODO: Declare a LocationManager and a LocationListener here:
    LocationManager mLocationManager;
    LocationListener mLocationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller_layout);

        // Linking the elements in the layout to Java code
        mCityLabel = (TextView) findViewById(R.id.locationTV);
        mWeatherImage = (ImageView) findViewById(R.id.weatherSymbolIV);
        mTemperatureLabel = (TextView) findViewById(R.id.tempTV);
        ImageButton changeCityButton = (ImageButton) findViewById(R.id.changeCityButton);

        changeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WeatherController.this,changeCity.class);
                startActivity(intent);
            }
        });


        // TODO: Add an OnClickListener to the changeCityButton here:

    }


    // TODO: Add onResume() here:
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Clima", "On resume called");
        Intent myIntent = getIntent();
        String city=myIntent.getStringExtra("City");
        if(city!=null){
            getWeatherforNewCity(city);
        }else {
            Log.d("Clima", "weather for clima");
            getweatherforcurrentlocation();
        }


    }

    private void getWeatherforNewCity(String city){
        RequestParams params=new RequestParams();
        params.put("q",city);
        params.put("appid",APP_ID);
        letsDoNetworking(params);
    }


    // TODO: Add getWeatherForNewCity(String city) here:


    // TODO: Add getWeatherForCurrentLocation() here:
    private void getweatherforcurrentlocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("Clima", "OnLocChanged call back received");
                String longitude = String.valueOf(location.getLongitude());
                String latitude=String.valueOf(location.getLatitude());

                Log.d("Clima","The latitude is"+latitude);
                Log.d("Clima","The longitude  is"+longitude);

                RequestParams params= new RequestParams();
                params.put("lat",latitude);
                params.put("lon",longitude);
                params.put("appid",APP_ID);
                letsDoNetworking(params);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("Clima", "Location is diabled");

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_CODE);

            return;
        }
        mLocationManager.requestLocationUpdates(Location_provider, MIN_TIME, MIN_DISTANCE, mLocationListener);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode== REQUEST_CODE){
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Log.d("Clima","OnRequest Pemission Result(): GRANTED!");
                getweatherforcurrentlocation();
            }else{
                Log.d("Clima","Rejeceted!");

            }
        }
    }
    // TODO: Add letsDoSomeNetworking(RequestParams params) here:
    private void letsDoNetworking(RequestParams params)
    {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(WEATHER_URL,params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response)
            {
                Log.d("Clima","Success JSON"+response.toString());

                com.vit.gokul.froggybuddy.WeatherDataModel weatherDataModel= com.vit.gokul.froggybuddy.WeatherDataModel.fromJson(response);
                updateUI(weatherDataModel);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e,JSONObject response){
                Log.e("Clima","Fail" +e.toString());
                Log.d("Clima","Status Code"+statusCode);
                Toast.makeText(WeatherController.this,"Request Failed",Toast.LENGTH_SHORT).show();
            }
        });

    }



    // TODO: Add updateUI() here:
    private void updateUI(com.vit.gokul.froggybuddy.WeatherDataModel weatherDataModel){
        mCityLabel.setText(weatherDataModel.getCity());
        mTemperatureLabel.setText(weatherDataModel.getTemperature());
        int imageRes=getResources().getIdentifier(weatherDataModel.getIconName(),"drawable",getPackageName());
        mWeatherImage.setImageResource(imageRes);
    }



    // TODO: Add onPause() here:
    @Override
    protected void onPause(){
        super.onPause();
        if(mLocationManager !=null) mLocationManager.removeUpdates(mLocationListener);
    }


}
