package com.example.covidandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.covidandroid.logica.Sesion;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;


public class FragmentPerfil extends Fragment implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    Button btnEditar, btnCerarSesion;
    private GoogleApiClient googleApiClient;
    TextView txtUsername, txtEmail;

    public FragmentPerfil() {
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

        View view = inflater.inflate(R.layout.fragment_perfil, container, false);


        btnCerarSesion = (Button)view.findViewById(R.id.btnCerrarSesion);
        btnEditar = (Button)view.findViewById(R.id.btnEditarDatos);

        txtUsername = (TextView)view.findViewById(R.id.txtNombreUsuario);
        txtEmail = (TextView)view.findViewById(R.id.txtEmailUsuario);


        btnEditar.setOnClickListener(this);
        btnCerarSesion.setOnClickListener(this);

        GoogleSignInAccount signInAccount= GoogleSignIn.getLastSignedInAccount(getContext());
        if(signInAccount!=null){
            Toast.makeText(getContext(), "Welcome back " + signInAccount.getDisplayName() , Toast.LENGTH_SHORT).show();
        }

        txtUsername.setText("Nombre de Usuario: " + signInAccount.getDisplayName());
        txtEmail.setText("Email del Usuario: " + signInAccount.getEmail());
        return  view;

    }


    private void handleLogOut(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnCerrarSesion:
                handleLogOut();
                break;
            case R.id.btnEditarDatos:
                FragmentEditarDatos frgEditar = new FragmentEditarDatos();
                openFragment(frgEditar);
                break;
        }
    }

    private void goLoginScreen() {
        Intent intent = new Intent(FragmentPerfil.this.getContext(),
                LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.contenedor, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


}