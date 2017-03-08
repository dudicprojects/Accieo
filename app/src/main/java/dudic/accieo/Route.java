package dudic.accieo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import app.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Route extends FragmentActivity implements OnMapReadyCallback, DirectionCallback {

    private GoogleMap mMap;
    Intent intent;
    LatLng currentLatLng, source, destination;
    Context mContext;

    String TAG = Route.class.getSimpleName();
    PolylineOptions routeLine;
    Polyline polyline;

    @BindView(R.id.sourceField)
    EditText sourceField;

    @BindView(R.id.destinationField)
    EditText destinationField;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        initUI();


    }

    private void initUI() {
        intent = getIntent();
        mContext = this;

        ButterKnife.bind(this);
        currentLatLng = new LatLng(intent.getDoubleExtra(Constants.LATITUDE, 0), intent.getDoubleExtra(Constants.LONGITUDE, 0));
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        sourceField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    return;
                pickPlace(Constants.SOURCE_PICKER_REQUEST);

            }
        });

        destinationField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    return;
                pickPlace(Constants.DESTINATION_PICKER_REQUEST);

            }
        });
    }

    void pickPlace(int option) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(Route.this), option);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e(TAG, e.toString());
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, e.toString());
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions().position(currentLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            Log.e(TAG, "Result code: " + resultCode);
            return;
        }
        Place place;

        switch (requestCode) {
            case Constants.SOURCE_PICKER_REQUEST:
                place = PlacePicker.getPlace(mContext, data);
                sourceField.setText(place.getName());
                source = place.getLatLng();
                break;

            case Constants.DESTINATION_PICKER_REQUEST:
                place = PlacePicker.getPlace(mContext, data);
                destinationField.setText(place.getName());
                destination = place.getLatLng();
                break;
        }
    }

    public void showRoute(View v) {
        Log.e(TAG, "Finding Route");

        if (source == null && destination == null) {

            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching the route...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        GoogleDirection.withServerKey(Constants.SERVER_API_KEY)
                .from(source)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(this);

        //    Log.e(TAG, source.toString() + "\n" + destination.toString() + "\n" + Constants.SERVER_API_KEY);

    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {

        // Log.e(TAG, "" + direction.getStatus() + " success " + rawBody + direction.toString());
        mMap.clear();

        if (!direction.isOK()) {
            Log.e(TAG, "" + direction.getStatus() + " success " + rawBody + direction.toString());
            return;
        }

        mMap.addMarker(new MarkerOptions().position(source).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker)));
        mMap.addMarker(new MarkerOptions().position(destination).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker)));


        ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
        routeLine = DirectionConverter.createPolyline(this, directionPositionList, 5, Color.RED);

        if (polyline != null)
            polyline.remove();

        polyline = mMap.addPolyline(routeLine);
        progressDialog.dismiss();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(source, 16));
        // Log.e(TAG, directionPositionList.toString());

    }

    @Override
    public void onDirectionFailure(Throwable t) {
        Log.e(TAG, "Failure: " + t.toString());
        progressDialog.dismiss();

    }
}
