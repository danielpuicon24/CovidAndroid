package com.example.covidandroid;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class FragmentEditarDatos extends Fragment implements View.OnClickListener {

    Button btnVerfPerfil, btnGuardarDatos, btnBuscarDni;
    TextView txtDni, txtCodigoVerificado, edtNombres;
    EditText edtDni, edtApellidos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_editar_datos, container, false);

        txtDni = (TextView)view.findViewById(R.id.edtDNI);
        txtCodigoVerificado = (TextView)view.findViewById(R.id.edtCodigo);

        btnVerfPerfil = (Button)view.findViewById(R.id.btnVerPerfil);
        btnGuardarDatos = (Button)view.findViewById(R.id.btnGuardarDatosEditados);
        btnBuscarDni = (Button)view.findViewById(R.id.btnBuscarDNI);

        edtNombres = (TextView) view.findViewById(R.id.txtNombresCompletos);

        btnBuscarDni.setOnClickListener(this);
        btnGuardarDatos.setOnClickListener(this);
        btnVerfPerfil.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnVerPerfil:
                FragmentPerfil fr=new FragmentPerfil();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.contenedor,fr)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.btnBuscarDNI:
                consultarDNI();
                break;
            case R.id.btnGuardarDatosEditados:
                break;
        }
    }




    private void consultarDNI() {
        String dni= txtDni.getText().toString();
        String codigo= txtCodigoVerificado.getText().toString();

        String URL = "https://dniruc.apisperu.com/api/v1/dni/" + dni + "?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InB1aWNvbjU3QGdtYWlsLmNvbSJ9.wqWe-5LTm4y8Tk6F6Ah0sjJ1vXwS3_xaKGhFZvvE5X4";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        edtNombres.setText("Response: " + response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        // Access the RequestQueue through your singleton class.
       // MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.contenedor, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}