package com.example.covidandroid;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.covidandroid.logica.Places;
import com.example.covidandroid.util.ServiciosWeb;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class FragmentMap extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMarkerClickListener {

    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    Marker mGreenMarker;
    MarkerOptions markerGreen;

    double lat;
    double longi;

    public ArrayList<Places> listaLugares;

    //public ListadoVeterinarioAdaptador adaptador;
    public ProgressDialog dialog;

    Marker[] allMarkers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        this.listar();

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Posicion Actual");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));

        for (int i = 0; i < listaLugares.size(); i++)
        {
            if(listaLugares.get(i).getCategoria().equalsIgnoreCase("verde")){
            LatLng latLng1 = new LatLng(listaLugares.get(i).getLatitud(), listaLugares.get(i)
                    .getLongitud());
            MarkerOptions markerOptionsGreen = new MarkerOptions();
            markerOptionsGreen.position(latLng1);
            markerOptionsGreen.title(listaLugares.get(i).getNombre());
            markerOptionsGreen.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            allMarkers[i] = mGoogleMap.addMarker(markerOptionsGreen);
            }else if(listaLugares.get(i).getCategoria().equalsIgnoreCase("amarillo")){
                LatLng latLng2 = new LatLng(listaLugares.get(i).getLatitud(), listaLugares.get(i)
                        .getLongitud());
                MarkerOptions markerOptionsYellow = new MarkerOptions();
                markerOptionsYellow.position(latLng2);
                markerOptionsYellow.title(listaLugares.get(i).getNombre());
                markerOptionsYellow.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                allMarkers[i] = mGoogleMap.addMarker(markerOptionsYellow);
            }else{
                LatLng latLng3 = new LatLng(listaLugares.get(i).getLatitud(), listaLugares.get(i)
                        .getLongitud());
                MarkerOptions markerOptionsRed = new MarkerOptions();
                markerOptionsRed.position(latLng3);
                markerOptionsRed.title(listaLugares.get(i).getNombre());
                markerOptionsRed.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                allMarkers[i] = mGoogleMap.addMarker(markerOptionsRed);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap=googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getContext())
                        .setTitle("Localizacion de permisos denegado")
                        .setMessage("CovidApp necesita los permisos de tu ubicacion, por favor acepta para acceder a tu tu ubicacion actual")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }


    //LISTADO DE MARKERS
    private void listar()
    {
        //Mostrar un dialog
        dialog = new ProgressDialog(this.getContext());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Descargando Lista de Zonas Peligrosas Covid");
        dialog.setCancelable(false);
        dialog.show();
        dialog.show();

        //Instanciar la lista listaProducto (Inicializar la lista)
        listaLugares = new ArrayList<Places>();

        //Instanciar el adaptador
        //Se envia this.getContext() para relacionar el adaptador con el fragment
       // adaptador = new ListadoVeterinarioAdaptador(this.getContext());

       // veterinarioRecyclerView.setAdapter(adaptador);

        new listarPlacesTask().execute();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    private class listarPlacesTask extends AsyncTask<Void,Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean respuesta=false;
            try {
                String URLServicioWeb = ServiciosWeb.URL_WS + "places.listar.php";
                HashMap parametros = new HashMap<String, String>();
                //parametros.put("token", Sesion.TOKEN);

                String veterinarioServicioWeb = new ServiciosWeb().openWebServiceFormData(URLServicioWeb, parametros);

                //Pasar los datos a formato JSON
                JSONObject jsonObject = new JSONObject(veterinarioServicioWeb);

                if (jsonObject.getInt("estado") == 200) //Pregunto si el servicio de la web devuelve OK (200)
                {
                    //Acceder a los datos de los productos. Los productos están contenidos dentro de un array
                    JSONArray jsonArray = jsonObject.getJSONArray("datos");

                    //Limpiar la lista
                    listaLugares.clear();

                    //Recorrer el array y extraer los datos de los productos
                    for (int i=0; i<jsonArray.length(); i++)
                    {
                        //Obtener los datos de cada producto que está en cada elemento del Array
                        JSONObject datosProducto = jsonArray.getJSONObject(i);

                        //Instanciar a la clase producto
                        Places obj = new Places();
                        obj.setId(datosProducto.getInt("id"));
                        obj.setNombre(datosProducto.getString("nombre"));
                        obj.setDescripcion(datosProducto.getString("descripcion"));
                        obj.setCategoria(datosProducto.getString("categoria"));
                        obj.setLatitud(datosProducto.getDouble("latitud"));
                        obj.setLongitud(datosProducto.getDouble("longitud"));

                        Log.e("PLACES", obj.getNombre() + obj.getDescripcion() + String.valueOf(obj.getLatitud()) + String.valueOf(obj.getLongitud()));


                        listaLugares.add(obj);
                        allMarkers =  new Marker[listaLugares.size()];
                    }

                    respuesta = true; //Significa que ha cargado los datos en el arraylist


                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return respuesta;
        }

        @Override
        protected void onPostExecute(Boolean respuesta) {
            super.onPostExecute(respuesta);

            if (respuesta)
            {
                //adaptador.cargarDatosArrayList(listaveterinario);
                dialog.dismiss();
            }
        }
    }





}