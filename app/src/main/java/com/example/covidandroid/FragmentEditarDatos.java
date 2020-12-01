package com.example.covidandroid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpResponse;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.example.covidandroid.logica.Persona;
import com.example.covidandroid.logica.Sesion;
import com.example.covidandroid.util.Pickers;
import com.example.covidandroid.util.ServiciosWeb;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.widget.Toast;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;


public class FragmentEditarDatos extends Fragment implements View.OnClickListener {

    Button btnVerfPerfil, btnGuardarDatos, btnBuscarDni;
    TextView txtDni, txtCodigoVerificador, txtNombres, txtApellidos, txtCodigoIdentificador,
            txtDNISolicitado, txtResultadoDNI, txtNombreUsuarioEditar, txtEmailUsuarioEditar, txtTelefono;
    ImageView imgUsuarioPerfilEditar, imgFecha, ivCodeContainer;

    EditText txtFecha;
    public ArrayList<Persona> listaDNI;
    public ProgressDialog dialog;
    String emailEditar;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_editar_datos, container, false);

        txtDni = view.findViewById(R.id.txtDNIBuscar);
        txtCodigoVerificador = view.findViewById(R.id.txtCodigoBuscar);

        txtDNISolicitado = view.findViewById(R.id.txtDNISolicitado);
        txtNombres = view.findViewById(R.id.txtNombresCompletosSolicitados);
        txtApellidos = view.findViewById(R.id.txtApellidosCompletosSolicitado);
        txtFecha = view.findViewById(R.id.txtFechaNacimiento);
        imgFecha = view.findViewById(R.id.imgFecha);
        txtCodigoIdentificador = view.findViewById(R.id.txtCodigoEditar);
        txtTelefono = view.findViewById(R.id.txtTelefonoEditar);

        txtResultadoDNI = view.findViewById(R.id.txtResultadoDNI);

        txtNombreUsuarioEditar = view.findViewById(R.id.txtNombreUsuarioEditar);
        txtEmailUsuarioEditar = view.findViewById(R.id.txtEmailUsuarioEditar);

        btnVerfPerfil = (Button) view.findViewById(R.id.btnVerPerfil);
        btnGuardarDatos = (Button) view.findViewById(R.id.btnGuardarDatosEditados);
        btnBuscarDni = (Button) view.findViewById(R.id.btnBuscarDNI);
        imgUsuarioPerfilEditar = (ImageView) view.findViewById(R.id.imgUsuarioPerfilEditar);



        btnBuscarDni.setOnClickListener(this);
        btnGuardarDatos.setOnClickListener(this);
        btnVerfPerfil.setOnClickListener(this);
        imgFecha.setOnClickListener(this);


        //CABECERA DEL PERFIL
        GoogleSignInAccount signInAccount= GoogleSignIn.getLastSignedInAccount(getContext());

        txtNombreUsuarioEditar.setText("Usuario: " + signInAccount.getDisplayName());
        txtEmailUsuarioEditar.setText("Email: " + signInAccount.getEmail());
        Glide.with(this).load(signInAccount.getPhotoUrl()).into(imgUsuarioPerfilEditar);

        emailEditar = signInAccount.getEmail();

        Sesion.UID = txtCodigoIdentificador.getText().toString();
        return view;
    }

    @Override
    public void onClick(View v) {
        final String dniSolicitado =  txtDNISolicitado.getText().toString();
        final String nombresCompletos = txtNombres.getText().toString();
        final String apellidosCompletos = txtApellidos.getText().toString();
        final String fecha = txtFecha.getText().toString();
        final String codigoIdentificador = txtCodigoIdentificador.getText().toString();
        final String telefono = txtTelefono.getText().toString();
        switch (v.getId()) {
            case R.id.btnVerPerfil:
                FragmentPerfil fr = new FragmentPerfil();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.contenedor, fr)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.btnBuscarDNI:
                Log.e("buscar dni", "click");
                //this.listar();
                buscar();
                break;
            case R.id.btnGuardarDatosEditados:
                if (dniSolicitado.isEmpty() || nombresCompletos.isEmpty() || apellidosCompletos.isEmpty()
                || fecha.isEmpty() || codigoIdentificador.isEmpty() || telefono.isEmpty()){
                    Toast.makeText(getContext(), "Debe ingresar todos los campos requeridos", Toast.LENGTH_SHORT).show();
                }else{
                    new GuardarPerfilTask(dniSolicitado, codigoIdentificador, nombresCompletos, apellidosCompletos, fecha, telefono).execute();
                    Toast.makeText(getContext(), "Datos del Perfil Actualizados", Toast.LENGTH_SHORT).show();
                    // Crea el nuevo fragmento y la transacción.
                    FragmentPerfil fragmentPerfil = new FragmentPerfil();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.contenedor, fragmentPerfil);
                    transaction.addToBackStack(null);

                    // Commit a la transacción
                    transaction.commit();
                }
                break;
            case R.id.imgFecha:
                Pickers.obtenerFecha(FragmentEditarDatos.this.getContext(), txtFecha);
                break;
        }
    }

    private void buscar(){
        //ejevutar la clase cargar el dni ,ediante la cual accede al servicio web
        new cargarDNI().execute();
    }

//CARGAR DNI HACE HERENCIA A LA CLASE  ASYNTASK
    /*ASYNTASK SE EJECUTARÁ EN SEGUNDO PLANO, LA WEB SERVICES SE DEMORA,
// MIENTRAS ESE TIEMPO SE DEMORA,
// LAS OTRAS TAREAS DE HILO PRINCIPAL SE EJECUTEN*/

    String apellido_paterno;
    String apellido_materno;
    String nombres;
    String DNISolicitado;
    String codVerifica;

    private class cargarDNI extends AsyncTask<Void, Void, Integer>
    {

        protected void onPreExecute(){
            super.onPreExecute();
            //ANTES DE EJECUTAR LA TAREA EN SEGUNDO PLANO

        }


        @Override
        protected Integer doInBackground(Void... voids) {

            //PERMITE EJECUTAR LA TAREA DE SEGUNDO PLANO, NO ALTERA EL HILO PRINCIPAL DE LA TAREA

            int resultado=0;

            try {
                String leerdni= txtDni.getText().toString();

                String enlace="https://dniruc.apisperu.com/api/v1/dni/"+leerdni+"?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InB1aWNvbjU3QGdtYWlsLmNvbSJ9.wqWe-5LTm4y8Tk6F6Ah0sjJ1vXwS3_xaKGhFZvvE5X4";

                try {

                    URL url = new URL(enlace);
                    URLConnection request= url.openConnection();
                    request.connect();

                    JsonParser jp = new JsonParser();
                    JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));

                    if(root.isJsonObject()){

                        JsonObject rootobj = root.getAsJsonObject();
                        apellido_paterno= rootobj.get("apellidoPaterno").getAsString();
                        apellido_materno= rootobj.get("apellidoMaterno").getAsString();
                        nombres= rootobj.get("nombres").getAsString();
                        DNISolicitado= rootobj.get("dni").getAsString();
                        codVerifica= rootobj.get("codVerifica").getAsString();

                    }



                }catch (Exception e){
                    //Toast.makeText(this,""+e,Toast.LENGTH_SHORT).show();
                    //System.out.println(e.getMessage());
                }
                return 1;
            }catch (Exception e){
                e.printStackTrace();
            }


            return resultado;

        }

        @Override
        protected void onPostExecute(Integer resultado) {
            super.onPostExecute(resultado);
            String codSolicitado = txtCodigoVerificador.getText().toString();
            String leerdni= txtDni.getText().toString();
            //DESPUES, COMO REACCIONA DESPUES DE EJECUTAR , RECIBE EL RESULTADO, MOSTRAR EL NOMBRE DEL DNI
            if(resultado==1){

                if(leerdni.isEmpty() ||  codSolicitado.isEmpty()){
                    txtResultadoDNI.setText("Ingrese los Campos a Buscar");
                }else {
                    if(codSolicitado.equalsIgnoreCase(codVerifica)){
                        txtNombres.setText("");
                        txtApellidos.setText("");
                        txtDNISolicitado.setText("");

                        txtNombres.append(nombres);
                        txtDNISolicitado.append(DNISolicitado);
                        txtApellidos.append(apellido_paterno+" "+apellido_materno);

                        txtResultadoDNI.setText("¡ DNI VALIDO !");
                    }else {
                        txtResultadoDNI.setText("¡ ERROR DE CODIGO VERIFICADOR !");

                        txtNombres.setText("");
                        txtApellidos.setText("");
                        txtDNISolicitado.setText("");
                    }
                }



            }else {
                Toast.makeText(getContext(), "Ha ocurrido un error al cargar los datos de la web service", Toast.LENGTH_SHORT).show();

            }


        }


    }


    public class GuardarPerfilTask extends AsyncTask<Void, Void, String>{
        private String dni;
        private String codigo;
        private String nombres;
        private String apellidos;
        private String fechanac;
        private String telefono;

        public GuardarPerfilTask(String dni, String codigo, String nombres, String apellidos, String fechanac, String telefono) {
            this.dni = dni;
            this.codigo = codigo;
            this.nombres = nombres;
            this.apellidos = apellidos;
            this.fechanac = fechanac;
            this.telefono = telefono;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String resultado="";
            try {
                String ServicioWeb = ServiciosWeb.URL_WS + "persona.registrar.php";
                HashMap<String, String> parametros = new HashMap<>();
                parametros.put("dni", this.dni);
                parametros.put("codigo", this.codigo);
                parametros.put("nombre", this.nombres);
                parametros.put("apellido", this.apellidos);
                parametros.put("fechanac", this.fechanac);
                parametros.put("telefono", this.telefono);
                parametros.put("email", emailEditar);

                resultado = new ServiciosWeb().openWebServiceFormData(ServicioWeb, parametros);
            }catch (Exception e){
                e.printStackTrace();
            }
            return  resultado;
        }

        @Override
        protected void onPostExecute(String resultado) {
            super.onPostExecute(resultado);

            try{
                JSONObject jsonObject = new JSONObject(resultado);
                int estado = jsonObject.getInt("estado");
                String mensaje = jsonObject.getString("mensaje");

                if(estado==500)
                {
                    Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
                }else{
                    //Leer el Codigo del Usuario que se ha grabado
                    JSONObject jsonObjectDatos = jsonObject.getJSONObject("datos");
                    String idCodigo = jsonObjectDatos.getString("codigo");

                    //Mostrar el Codigo del Usuario Registrado
                    Toast.makeText(getContext(), mensaje + "\n" + "Datos del Perfil Editados con el Codigo:" + idCodigo, Toast.LENGTH_SHORT).show();

                    //Enviar la respuesta al fragment (Se le indica que todo terminó correctamente)
                    getActivity().setResult(Activity.RESULT_OK);

                    //Finalizar el activity
                    getActivity().finish();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }



}
