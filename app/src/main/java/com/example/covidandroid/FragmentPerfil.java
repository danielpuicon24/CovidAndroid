package com.example.covidandroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.covidandroid.logica.Persona;
import com.example.covidandroid.logica.Sesion;
import com.example.covidandroid.util.ServiciosWeb;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class FragmentPerfil extends Fragment implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    Button btnEditar, btnCerarSesion, btnVerCodigoQR;
    TextView txtUsername, txtEmail, txtDNIPerfil, txtNombresUsuarioPerfil, txtApellidosUsuarioPerfil, txtFechaNacUsuarioPerfil,
            txtTelefonoUsuarioPerfil, txtCodigoUsuarioPerfil;
    ImageView imgUsuarioPerfil;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleApiClient mgoogleApiClient;
    public ProgressDialog dialog;
    public ArrayList<Persona> listaPersona;
    String emailEditar;
    Boolean existeUsuario = false;
    String dniPerfil, nombrePerfil, apellidoPerfil, fechaNacPerfil, telefonoPerfil, codigoPerfil;
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
        btnVerCodigoQR = (Button)view.findViewById(R.id.btnVerCodigoQR);

        txtUsername = (TextView)view.findViewById(R.id.txtNombreUsuario);
        txtEmail = (TextView)view.findViewById(R.id.txtEmailUsuario);
        imgUsuarioPerfil = (ImageView) view.findViewById(R.id.imgUsuarioPerfil);

        txtDNIPerfil = (TextView)view.findViewById(R.id.txtDNIPerfil);
        txtNombresUsuarioPerfil = (TextView)view.findViewById(R.id.txtNombresUsuarioPerfil);
        txtApellidosUsuarioPerfil = (TextView)view.findViewById(R.id.txtApellidosUsuarioPerfil);
        txtFechaNacUsuarioPerfil = (TextView)view.findViewById(R.id.txtFechaNacUsuarioPerfil);
        txtTelefonoUsuarioPerfil = (TextView)view.findViewById(R.id.txtTelefonoUsuarioPerfil);
        txtCodigoUsuarioPerfil = (TextView)view.findViewById(R.id.txtCodigoUsuarioPerfil);


        btnEditar.setOnClickListener(this);
        btnCerarSesion.setOnClickListener(this);
        btnVerCodigoQR.setOnClickListener(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.idweb))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);


        GoogleSignInAccount signInAccount= GoogleSignIn.getLastSignedInAccount(getContext());

        txtUsername.setText("Usuario: " + signInAccount.getDisplayName());
        txtEmail.setText("Email: " + signInAccount.getEmail());
        Glide.with(this).load(signInAccount.getPhotoUrl()).into(imgUsuarioPerfil);
        emailEditar = signInAccount.getEmail();

        this.listar();

        if(existeUsuario = false){
            txtDNIPerfil.setText("Falta Completar Datos");
            txtNombresUsuarioPerfil.setText("Falta Completar Datos");
            txtApellidosUsuarioPerfil.setText("Falta Completar Datos"); ;
            txtFechaNacUsuarioPerfil.setText("Falta Completar Datos");
            txtTelefonoUsuarioPerfil.setText("Falta Completar Datos");
            txtCodigoUsuarioPerfil.setText("Falta Completar Datos");
        }


        return  view;

    }


    private void handleLogOut(){
        FirebaseAuth.getInstance().signOut();
        Auth.GoogleSignInApi.signOut(mGoogleSignInClient.asGoogleApiClient());
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
            case R.id.btnVerCodigoQR:
                CodigoQRFragment frgCodigoQR = new CodigoQRFragment();
                openFragment(frgCodigoQR);

                break;
        }
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


    private void listar(){
        //Llamar a la clase ListarClienteTask para ejecutar la descarga de datos desde el servicio web
        new ListadoPerfilTask().execute();
    }

    private class ListadoPerfilTask extends AsyncTask<Void, Void, String>{
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            //Pre-configuración de la tarea
            super.onPreExecute();
            dialog = new ProgressDialog(getContext());
            dialog.setTitle("Obteniendo Datos del Perfil del Usuario");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            //Llaada al servicio web
            String resultadoWS="";
            try {
                String URLServicioWeb = ServiciosWeb.URL_WS + "persona.listar.php";
                HashMap parametros = new HashMap<String,String>();
                parametros.put("email", emailEditar);
                resultadoWS = new ServiciosWeb().openWebServiceFormData(URLServicioWeb, parametros);

            }catch (Exception e){
                e.printStackTrace();
            }
            return resultadoWS;
        }

        @Override
        protected void onPostExecute(String resultadoWS) {
            super.onPostExecute(resultadoWS);

            if (! resultadoWS.isEmpty()){
                try {
                    JSONObject json = new JSONObject(resultadoWS);
                    int estado = json.getInt("estado");
                    String mensaje = json.getString("mensaje");
                    String datos = json.getString("datos");
                    if(estado==500){
                        Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
                    }else { //Devuelve 200 (ok)
                        JSONArray jsonArray = new JSONArray(datos);

                        //listaPersona.clear(); //Limpiar los elementos de la lista

                        for (int i = 0; i < jsonArray.length(); i++){
                            JSONObject datosProducto = jsonArray.getJSONObject(i);
                            //Instanciar a la clase producto
                            //Instanciar a la clase producto
                            Persona obj = new Persona();
                            obj.setDni(datosProducto.getString("dni"));
                            obj.setCodigo(datosProducto.getString("codigo"));
                            obj.setNombre(datosProducto.getString("nombre"));
                            obj.setApellidoPaterno(datosProducto.getString("apellido"));
                            obj.setFechanac(datosProducto.getString("fechanac"));
                            obj.setTelefono(datosProducto.getString("telefono"));
                            txtDNIPerfil.setText(obj.getDni());
                            txtNombresUsuarioPerfil.setText(obj.getNombre());
                            txtApellidosUsuarioPerfil.setText(obj.getApellidoPaterno()); ;
                            txtFechaNacUsuarioPerfil.setText(obj.getFechanac());
                            txtTelefonoUsuarioPerfil.setText(obj.getTelefono());
                            txtCodigoUsuarioPerfil.setText(obj.getCodigo());
                            Persona.listaDNI.add(obj);
                            existeUsuario = true;
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    dialog.dismiss(); //Cerrar el dialog (Obteniendo lista de cuotas)
                }
            }
        }
    }


}