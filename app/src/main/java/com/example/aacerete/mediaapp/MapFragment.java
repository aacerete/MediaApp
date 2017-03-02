package com.example.aacerete.mediaapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

/**
 * Created by aacerete on 28/02/17.
 */

public class MapFragment extends android.support.v4.app.Fragment {



    private org.osmdroid.views.MapView mvMap;
    private IMapController mapController;
    private MyLocationNewOverlay myLocationOverlay;

    private RadiusMarkerClusterer mediaMarkerClusterer;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mvMap = (org.osmdroid.views.MapView) view.findViewById(R.id.mvMap);

        initializeMap();
      //  setOverlays();
        setZoom();

        //Reactualitza el mapa
        //refresh();

        mvMap.invalidate();

        return view;

    }

    private void putMarkers(){

        setupMarkerOverlay();



    }

    private void setupMarkerOverlay() {

        mediaMarkerClusterer = new RadiusMarkerClusterer(getContext());
        mvMap.getOverlays().add(mediaMarkerClusterer);
        mediaMarkerClusterer.setRadius(100);

    }

    private void initializeMap() {

        mvMap.setTileSource(TileSourceFactory.MAPNIK);
        mvMap.setTilesScaledToDpi(true);
        mvMap.setBuiltInZoomControls(true);
        mvMap.setMultiTouchControls(true);

    }


    private void setZoom() {
        mapController = mvMap.getController();
        mapController.setCenter(new GeoPoint(41.383333, 2.183333));
        mapController.setZoom(15);
    }




}