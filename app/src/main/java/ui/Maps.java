package ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

//import com.github.amlcurran.showcaseview.ShowcaseView;
//import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import app.Constants;
import app.Place;
import app.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import dudic.accieo.PlaceTypeFilter;
import dudic.accieo.R;
import dudic.accieo.Route;

public class Maps extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    Context mContext;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation, mCurrentLocation;
    LatLng currentLatLng;
    String TAG = Maps.class.getSimpleName();
    LocationRequest mLocationRequest;
    ProgressDialog progressDialog;

    Intent addPlaceIntent;

    MarkerOptions mMarkerOptions[];
    Place mPlace[];

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    User user;

    RelativeLayout.LayoutParams lps;
//    ShowcaseView s2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        initUI();


    }

    private void showShowCase() {


//        new ShowcaseView.Builder(this)
//                .setTarget(new ViewTarget(findViewById(R.id.explore_bt_float)))
//                .setContentTitle("Explore")
//                .setContentText("Search nearby accessible places around you")
//                .setStyle(R.style.CustomShowcaseTheme)
//                .hideOnTouchOutside()
//                .build();
    }

    private void initUI() {
        ButterKnife.bind(this);

        user = new User(this);





        lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        lps.setMargins(margin, margin, margin, margin);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mContext = this;
        if (!user.isConnected()) {
            showToast(getString(R.string.network_problem));
            return;
        }
        addPlaceIntent = new Intent(mContext, AddPlace.class);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching your current location...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));

        initGAPI();

    }


    public void addPlace(View v) {
        if (currentLatLng == null) {
            return;
        }

        if (!user.isConnected()) {
            Toast.makeText(mContext, getString(R.string.network_problem), Toast.LENGTH_LONG).show();
            return;
        }
        Log.d(TAG, "isLoggedIn() = " + user.isLoggedIn());

        if (user.isLoggedIn()) {
            Log.d(TAG, currentLatLng.toString());
            Intent intent = new Intent(this, AddPlace.class);
            intent.putExtra("lat", currentLatLng.latitude + "");
            intent.putExtra("lang", currentLatLng.longitude + "");

            if (addPlaceIntent != null)
                startActivity(addPlaceIntent);
        } else {
            startActivity(new Intent(this, Splash.class));
        }



        progressDialog.dismiss();
    }

    public void explorePlaces(View v) {
        if (currentLatLng == null) {
            return;
        }
        // initND();
        progressDialog.setMessage("Finding accessible places around you...");
        progressDialog.show();
        mMap.clear();
        Log.e(TAG, Constants.GET_PLACES);
        Ion.with(mContext).load(Constants.GET_PLACES).asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                progressDialog.dismiss();
                if (e != null) {
                    Log.e(TAG, e.toString());
                    showToast(getString(R.string.server_problem));
                    return;
                }
                Log.e(TAG, result);

                try {
                    JSONObject json = new JSONObject(Constants.formatResponse(result));
                    showToast(json.getString(Constants.MESSAGE));


                    if (!json.getString(Constants.STATUS).equals("200")) {
                        showToast(getString(R.string.server_problem));
                        return;
                    }
                    JSONArray placesArray = json.getJSONArray("places");


                    renderPlaces(placesArray);
                } catch (JSONException e1) {
                    Log.e(TAG, e1.toString());
                    showToast(getString(R.string.server_problem));
                }

            }
        });
    }

    private void renderPlaces(JSONArray placesArray) {

        mMarkerOptions = new MarkerOptions[placesArray.length()];
        mPlace = new Place[placesArray.length()];
        mMap.clear();
        onLocationChanged(mCurrentLocation);
        try {
            for (int i = 0; i < placesArray.length(); i++) {
                JSONObject place = placesArray.getJSONObject(i);

                LatLng placeLatLng = new LatLng(Double.parseDouble(place.getString(Constants.LATITUDE)),
                        Double.parseDouble(place.getString(Constants.LONGITUDE)));

                if (mMap == null) {
                    Log.e(TAG, "MAP IS NULL");
                }

                String name = place.getString(Constants.PLACE_NAME);
                String address = place.getString(Constants.ADDRESS);
                String type = place.getString(Constants.PLACE_TYPE);
                int type_in = Integer.parseInt(type);
                String rate = place.getString(Constants.ACCESSIBLITY_LEVEL);
                int rate_in = Integer.parseInt(rate);
                if(rate_in > 2)
                    rate_in = 2;
                if(rate_in < 0)
                    rate_in = 0;
                String facilities = place.getString(Constants.FACILITIES);
                Log.e(TAG,name +  " T " + type_in + "| R " + rate);
                Bitmap b = BitmapFactory.decodeResource(getResources(), Place.getPlaceDrawable(type_in,  rate_in));
                Bitmap c = Bitmap.createScaledBitmap(b, 80, 120, true);
                mMarkerOptions[i] = new MarkerOptions().position(placeLatLng).snippet(name).icon(BitmapDescriptorFactory.
                        fromBitmap(c));

                mPlace[i] = new Place(name, address, rate, facilities, type);

                mMap.addMarker(mMarkerOptions[i]);

            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }


    private void initGAPI() {

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setNumUpdates(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (getIntent().getBooleanExtra(Constants.SHOW_TUTORIAL, false))
            showShowCase();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (mPlace == null)
                    return true;
                //  Log.e(TAG, "Marker Clicked " + marker.toString());
                for (int i = 0; i < mPlace.length; i++) {
                    Log.e(TAG, mPlace[i].name);

                    if (marker.getSnippet().equals(mPlace[i].name)) {



                        Intent intent = new Intent(mContext, PlaceInfo.class);
                        intent.putExtra(Constants.PLACE_OBJECT, Parcels.wrap(mPlace[i]));
                        // Log.e("Marker - clicked", "clc");
                        startActivity(intent);
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(Maps.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        changeLocationSettings();
        if (mLastLocation != null) {
            Log.e(TAG, String.valueOf(mLastLocation.getLatitude()));
            Log.e(TAG, String.valueOf(mLastLocation.getLongitude()));
        } else {
            Log.e(TAG, "Last location is null");
            startLocationUpdates();
        }

        if (!mGoogleApiClient.isConnected()) {
            Log.e(TAG, "GAC not connected");
            return;
        }

        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, null);

        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(@NonNull PlaceLikelihoodBuffer likelyPlaces) {
                Log.e(TAG, "onResult \n" + likelyPlaces.toString());
                try {
                    addPlaceIntent.putExtra(Constants.PLACE_NAME, likelyPlaces.get(0).getPlace().getName());
                    addPlaceIntent.putExtra(Constants.ADDRESS, likelyPlaces.get(0).getPlace().getAddress());
                    addPlaceIntent.putExtra(Constants.LATITUDE, likelyPlaces.get(0).getPlace().getLatLng().latitude);
                    addPlaceIntent.putExtra(Constants.LONGITUDE, likelyPlaces.get(0).getPlace().getLatLng().longitude);
                    addPlaceIntent.putExtra(Constants.PLACE_TYPE, likelyPlaces.get(0).getPlace().getPlaceTypes().get(0));
                    likelyPlaces.release();
                }
                catch(Exception e)
                {
                    Log.e("Hi","Exception Handled");
                }
            }
        });

        progressDialog.dismiss();

    }


    void showToast(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    changeLocationSettings();
                }

                break;
        }
    }

    private void changeLocationSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        startLocationUpdates();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(
                                    Maps.this,
                                    1);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                        break;
                }
            }
        });
    }

    private void startLocationUpdates() throws SecurityException {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        Log.e(TAG, mCurrentLocation.toString());
        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.addMarker(new MarkerOptions().position(currentLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker)).snippet("Your Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16));
        progressDialog.dismiss();


    }

    @Override
    protected void onStop() {
        super.onStop();

        mGoogleApiClient.disconnect();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_maps, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_explore:
                explorePlaces(toolbar);
                break;
            case R.id.action_logout:
                user.logOut();
                startActivity(new Intent(mContext, Maps.class));
                break;
            case R.id.action_filter_place:
                filterPlaces();
                break;
            case R.id.action_feedback:
                takeFeedbackFromUser();
                break;
            case R.id.action_get_route:
                Intent intent = new Intent(mContext, Route.class);
                if (currentLatLng != null) {
                    intent.putExtra(Constants.LATITUDE, currentLatLng.latitude);
                    intent.putExtra(Constants.LONGITUDE, currentLatLng.longitude);
                }
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Ion.with(mContext).load(Constants.PLACE_FILTER).setBodyParameter(Constants.PLACE_TYPE,
                    data.getIntExtra(Constants.PLACE_TYPE, 0) + "").setBodyParameter("rate", "2").asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            if (e != null) {
                                showToast(getString(R.string.server_problem));
                                Log.e(TAG, e.toString());
                                return;
                            }

                            Log.e(TAG, result);
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                renderPlaces(jsonObject.getJSONArray("places"));
                            } catch (Exception e1) {
                                Log.e(TAG, e1.toString());
                                showToast(getString(R.string.server_problem));
                            }

                        }
                    });
        }
    }

    private void filterPlaces() {
        Intent intent = new Intent(mContext, PlaceTypeFilter.class);
        startActivityForResult(intent, 0);
    }

    private void takeFeedbackFromUser() {

        Intent sendEmail = new Intent(Intent.ACTION_SEND);
        sendEmail.setType("message/rfc822");
        sendEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{"acceio.cic@gmail.com"});
        sendEmail.putExtra(Intent.EXTRA_SUBJECT, "Acceio App Feedback");

        startActivity(Intent.createChooser(sendEmail, "Send Feedback via..."));

    }

    public void getToCurrentLoc(View view) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 16));
    }
}
