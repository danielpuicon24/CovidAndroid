package com.example.covidandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BtmNavigationView extends AppCompatActivity {

    BottomNavigationView btnNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_navigation_view);

        btnNavigationView = (BottomNavigationView)findViewById(R.id.btmNavigationView);

        setDefaultFragment();
        btnNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_mapa:
                        Toast.makeText(BtmNavigationView.this, "Mapa", Toast.LENGTH_SHORT).show();

                        setDefaultFragment();
                        return true;
                    case R.id.navigation_perfil:
                        Toast.makeText(BtmNavigationView.this, "Perfil", Toast.LENGTH_SHORT).show();
                        FragmentPerfil frgmPerfil = new FragmentPerfil();
                        openFragment(frgmPerfil);
                        return true;
                }
                return false;
            }
        });
    }



    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contenedor, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setDefaultFragment() {
        FragmentMap mapFragment = new FragmentMap();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.contenedor, mapFragment).commit();
    }

    private void setPerfilFragment() {
        FragmentPerfil perfilFragment = new FragmentPerfil();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.contenedor, perfilFragment).commit();
    }





}