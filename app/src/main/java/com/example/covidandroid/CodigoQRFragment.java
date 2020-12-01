package com.example.covidandroid;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.covidandroid.adaptador.AdaptadorCodigoQR;
import com.example.covidandroid.logica.Persona;
import com.example.covidandroid.util.ServiciosWeb;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import org.json.JSONObject;

import java.util.HashMap;


public class CodigoQRFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    String codigo;
    Button btnRegresar;

    public RecyclerView QRRecyclerView;
    public AdaptadorCodigoQR adaptador;
    public SwipeRefreshLayout swipeListaQR;
    String emailEditar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_codigo_q_r, container, false);

        btnRegresar = (Button)view.findViewById(R.id.btnRegresarPerfil);
        btnRegresar.setOnClickListener(this);

        QRRecyclerView=view.findViewById(R.id.codigoqrRecyclerView);
        QRRecyclerView.setHasFixedSize(true);
        QRRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        swipeListaQR = view.findViewById(R.id.srlCodigoQR);
        swipeListaQR.setColorSchemeColors(getResources().getColor(R.color.colorAccent)); //cambiar al color del tema
        swipeListaQR.setOnRefreshListener(this);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.idweb))
                .requestEmail()
                .build();

        GoogleSignInAccount signInAccount= GoogleSignIn.getLastSignedInAccount(getContext());

        emailEditar = signInAccount.getEmail();


        adaptador = new AdaptadorCodigoQR(this.getContext());

        //indicarle al recyclerView el adaptador que mostrará el listado
        QRRecyclerView.setAdapter(adaptador);

        //Llmar al método listar()
        listar();
        Bundle data = this.getArguments();
        if(data != null){
            codigo = data.getString("p_cod");
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentPerfil fragmentPerfil = new FragmentPerfil();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.contenedor, fragmentPerfil);
        transaction.addToBackStack(null);

        // Commit a la transacción
        transaction.commit();
    }

    private void listar(){
        //int idBodega = Sesion.CLIENTE_ID;
        new VerCodigoTask(getContext(),codigo).execute();
    }

    @Override
    public void onRefresh() {
        listar();
        swipeListaQR.setRefreshing(false);
    }

    private class VerCodigoTask extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;
        private Context context;
        private String codigo;

        public VerCodigoTask(Context context, String codigo) {
            this.context = context;
            this.codigo = codigo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setTitle("Obteniendo el Codigo QR");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            //Llamada al servicio web
            String resultadoWS="";
            try {
                String URLServicioWeb = ServiciosWeb.URL_WS + "vercodigoqr.php";
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


                    if (estado==500){
                        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
                    }else{ //Devuelve 200 (ok)

                        //Instanciar a la clase producto
                        Persona.listaDNI.clear();
                        Persona obj = new Persona();

                        //Venta.FOTO = json.getString("datos");
                        obj.setQr(json.getString("datos"));
                        Persona.listaDNI.add(obj);

                        adaptador.cargarListaPersonaAuxiliar(Persona.listaDNI);

                        //Log.e("codigo", json.getString("datos"));

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    dialog.dismiss(); //Cerrar el dialogo (Obteniendo lista de cuotas)
                }
            }


        }
    }
}