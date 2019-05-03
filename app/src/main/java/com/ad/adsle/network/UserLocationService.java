package com.ad.adsle.network;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.ad.adsle.Db.AppData;
import com.ad.adsle.Information.LocationDetails;
import com.ad.adsle.Information.User;
import com.ad.adsle.MyApplication;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class UserLocationService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, com.ad.adsle.Callbacks.LocationCallback {

    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10; //METERS
    Context context = MyApplication.getAppContext();
    boolean isGPSEnabled = false;
    AppData data;
    User user;
    LocationDetails locationDetails;
    double latitude = 0;
    double longitude = 0;
    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationManager mLocationManager;

    public UserLocationService() {
        data = new AppData(context);
        user = data.getUser();
        locationDetails = data.getLocationDetails();
    }

    public void GetCurrentLocation() {
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (checkPlayServices()) {
            buildGoogleApiClient();
            createLocationRequest();
        }
        if (isGPSEnabled) {
            displayLocation();
        }
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            locationDetails.setLatlng(latitude + "," + longitude);
            data.StoreLocationDetails(locationDetails);
            GetLocationFromServer getLocationFromServer = new GetLocationFromServer(context, latitude, longitude, this);
            getLocationFromServer.Locate();
            //Log.e("Location Update", "LatLng = " + latitude + " / " + longitude);
        }
    }


    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //Toast.makeText(LocationUserActivity.this, "This device is not supported", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return true;
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();
    }

    @Override
    public void setLocation(String city, String area, String inside_area, String formatted_address, String country) {
        //Log.e("After LatLng", "Inside Area = " + inside_area);
        locationDetails.setFormatted_address(formatted_address);
        locationDetails.setLatlng(latitude + "," + longitude);
        locationDetails.setInside_area(inside_area);
        locationDetails.setArea(area);
        locationDetails.setCity(city);
        locationDetails.setCountry(country);
        data.StoreLocationDetails(locationDetails);
//        if (!TextUtils.isEmpty(user.getEmail())) {
////            String email = user.getEmail().replace(".", ",");
////            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + email + "/Signup");
////            Map<String, Object> params = new HashMap<>();
////            params.put("loc_latlng", latitude + "/" + longitude);
////            params.put("loc_address", formatted_address);
////            params.put("loc_name", inside_area);
////            ref.updateChildren(params);
//        }
        //UserLocation userLocation = new UserLocation(city, area, inside_area, formatted_address, String.valueOf(latitude), String.valueOf(longitude));
        //ref.setValue(userLocation);
    }
}
