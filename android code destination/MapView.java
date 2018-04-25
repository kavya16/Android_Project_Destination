package com.vnr.smartcitytraveller;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapView extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<Place> places;
    int position =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent i = getIntent();
        String string = i.getStringExtra(Util.string);
        places = Util.rparse(string);
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position++;
                if(position==places.size())
                {
                    position=0;
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLng(places.get(position).getLocation()));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        PolylineOptions polylineOptions = new PolylineOptions();
        // Add a marker in Sydney and move the camera
        for(int i=0;i<places.size();i++)
        {
            polylineOptions.add(places.get(i).getLocation());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(places.get(i).getLocation());
            markerOptions.title(places.get(i).getName());
            mMap.addMarker(markerOptions);
        }
        polylineOptions.add(places.get(0).getLocation());

        mMap.addPolyline(polylineOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(places.get(0).getLocation()));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }
}
