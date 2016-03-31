package com.example.suhussai.gameshare;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by sangsoo on 26/03/16.
 */
public class ViewMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private Location lastLocation;
    private LatLng latLng;
    private Marker marker;
    private User user;
    private Item item;
    private String bidString;
    private Bid bid;

    @Override
    // Reference: http://stackoverflow.com/questions/32117468/android-studio-google-maps-how-to-redirect-camera-to-my-current-location-when-i
    // Reference: http://stackoverflow.com/questions/18546234/mark-current-location-on-google-map
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.ViewMap_Map);
        mapFragment.getMapAsync(this);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lastLocation = locationManager.getLastKnownLocation(provider);

        user = UserController.getCurrentUser();
        item = ItemController.getCurrentItem();
        bidString = getIntent().getStringExtra("bidString");


        Button CancelButton = (Button) findViewById(R.id.ViewMap_Cancel);

        CancelButton.setOnClickListener(new View.OnClickListener() {
            /**
             * The method to be run when the cancel button is pressed.
             * @param v the view
             */
            public void onClick(View v) {
                setResult(RESULT_OK);
                Toast.makeText(getApplicationContext(), "Accepting Bid Cancelled.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


        Button AcceptButton = (Button) findViewById(R.id.ViewMap_Accept);

        AcceptButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Method called when pressing Accept button, any changes will be updated
             */
            public void onClick(View v) {
                setResult(RESULT_OK);
                item.setLocation(latLng);

                ArrayList<Bid> bids = item.getBids();
                for (Bid b : bids) {
                    if (b.toString().equals(bidString)) {
                        bid = b;
                        break;
                    }
                }
                user.acceptBid(bid, item);
                Toast.makeText(getApplicationContext(), "Bid accepted", Toast.LENGTH_SHORT).show();
                finish();

            }
        });
    }

    /**
     * Sets the position on the map to Location set by lender.
     */
    //TODO: Screen rotation crashes this thing. :(
    //TODO: Lender needs to see the map too!
    //TODO: make bid error checking, lended item screen, separating login screen.
    // Reference: https://developers.google.com/maps/documentation/android-api/map
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (lastLocation != null) {

            latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            CameraPosition position = map.getCameraPosition();

            CameraPosition.Builder builder = new CameraPosition.Builder();
            builder.zoom(15);
            builder.target(latLng);

            map.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));

            // Reference: http://stackoverflow.com/questions/7167604/how-accurately-should-i-store-latitude-and-longitude
            marker = map.addMarker(new MarkerOptions().position(latLng).title("You are here!")
                    .snippet(String.format("lat: %.6f", latLng.latitude) + String.format(", lng: %.6f", latLng.longitude)));
        }

        else {
            latLng = new LatLng(0, 0);
            marker = map.addMarker(new MarkerOptions().position(latLng).title("GPS is disabled")
                    .snippet(String.format("lat: %.6f", latLng.latitude) + String.format(", lng: %.6f", latLng.longitude)));
        }

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                marker.remove();
                latLng = point;
                marker = map.addMarker(new MarkerOptions().position(latLng).title("Place to Meet")
                        .snippet(String.format("lat: %.6f", latLng.latitude) + String.format(", lng: %.6f", latLng.longitude)));
            }
        });
    }
}

