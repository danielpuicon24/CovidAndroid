package com.example.covidandroid;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.covidandroid.util.Funciones;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class PersonaMapaActivity extends AppCompatActivity  implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapClickListener, View.OnClickListener{
    double latitud, longitud;
    GoogleMap mapa;
    Marker marcador;
    String nombre;
    String ubicacion;
    FloatingActionButton fabMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persona_mapa);

        //Remover la barra de notificaciones
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Remover la barra de titulo
        getSupportActionBar().hide();

        //Vincular el fragment del mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_asig);
        mapFragment.getMapAsync(this);

        //Vincular el botón flotante
        fabMap = (FloatingActionButton) findViewById(R.id.fabmap);
        fabMap.setOnClickListener(this);


        //Leer el código del lugar turístico
        Bundle parametros = this.getIntent().getExtras();
        int codigo_persona = parametros.getInt("p_cod_persona");

        if (codigo_persona == 0){ //Es para cuando estamos agregando
            this.nombre = "Debe ubicar en el mapa";

            /*Centro de chiclayo*/
            this.latitud = -6.7719709;
            this.longitud = -79.8374808;
            /*Centro de chiclayo*/

        }else{ //Para cuando estamos editando
            //Leer la latitud, longitud y nombre del lugar turístico
           /* Persona obj = new Persona().leerDatos(codigo_persona);
            if (obj.getLatitud()==0){
                this.latitud = -6.7719709;
                this.longitud = -79.8374808;
                this.nombre = "Debe ubicar en el mapa";
            }else{
                this.latitud = obj.getLatitud();
                this.longitud = obj.getLongitud();
                this.nombre = obj.getNombre();
            }*/
        }
    }

    @Override
    public void onClick(View view) {
        //Para el botón flotante
        switch (view.getId()){
            case R.id.fabmap:
                Bundle parametros = new Bundle();
                parametros.putDouble("latitud", this.latitud);
                parametros.putDouble("longitud", this.longitud);
                parametros.putString("direccion", this.ubicacion);

                Intent i = new Intent();
                i.putExtras(parametros);

                this.setResult(RESULT_OK, i);
                this.finish();

                break;
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        //Cuando el usuario hace clic en el mapa
        marcador.setPosition(latLng);
        this.latitud = latLng.latitude;
        this.longitud = latLng.longitude;
        this.ubicacion = Funciones.obtenerDireccionMapa(this, this.latitud, this.longitud);

        //Centrar la camara, de tal manera que el marcador siempre quede al centro del mapa
        mapa.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        //Mostrar la información sobre el marcador
        marcador.showInfoWindow();

        Toast.makeText(this, "Ubicación, Lat: " + String.valueOf(this.latitud) + "  Long: " + String.valueOf(this.longitud) + "\n" + ubicacion, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        //Se ejecuta cuando el marcador inicia el arrastre
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        //Se ejecuta cuando el marcador se esta arrastrando
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        //Se ejecuta cuando el marcador termina el arrastre
        this.latitud = marker.getPosition().latitude;
        this.longitud = marker.getPosition().longitude;
        this.ubicacion = Funciones.obtenerDireccionMapa(this, this.latitud, this.longitud);
        mapa.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

        Toast.makeText(this, "Ubicación, Lat: " + String.valueOf(this.latitud) + "  Long: " + String.valueOf(this.longitud), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Lee las coordenadas y lo muestra en el mapa
        mapa = googleMap;
        googleMap.setOnMarkerDragListener(this);
        googleMap.setOnMapClickListener(this);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.draggable(true);
        markerOptions.position(new LatLng(this.latitud, this.longitud));
        markerOptions.title(this.nombre);
        markerOptions.snippet("Puede arrastrar y ubicar una dirección");

        marcador = googleMap.addMarker(markerOptions);
        marcador.showInfoWindow();

        CameraPosition camPos = new CameraPosition.Builder()
                .target(getCenterCoordinate())
                .zoom(10)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(camPos);
        mapa.animateCamera(cameraUpdate);
    }

    public LatLng getCenterCoordinate(){
        //Centrar la camara del mapa según el marcador (Marker)
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(this.latitud, this.longitud));
        LatLngBounds bounds = builder.build();
        return bounds.getCenter();
    }
}
