package com.example.covidandroid.adaptador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidandroid.FragmentPerfil;
import com.example.covidandroid.R;
import com.example.covidandroid.logica.Persona;
import com.example.covidandroid.util.Funciones;
import java.util.Calendar;

import java.util.ArrayList;
import java.util.Date;


public class AdaptadorCodigoQR extends RecyclerView.Adapter<AdaptadorCodigoQR.ViewHolder>{
    public static ArrayList<Persona> listaPersonaAuxiliar;
    private Context context;
    private int posicionItemSeleccionado;

    public AdaptadorCodigoQR(Context context) {
        this.context = context;
        this.listaPersonaAuxiliar = new ArrayList<Persona>();
    }

    public void cargarListaPersonaAuxiliar(ArrayList<Persona> lista){
        listaPersonaAuxiliar = lista;
        notifyDataSetChanged(); //refrescar la lista
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Permite enlazar el archivo que contiene el cardview
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.codigoqr_cardview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Persona obj = listaPersonaAuxiliar.get(position);
        Date currentTime = Calendar.getInstance().getTime();

        holder.imgEvidencia_qr.setImageBitmap(Funciones.base64ToImage(obj.getQr()));
        holder.txtFecha.setText("Fecha: " + currentTime);
    }

    @Override
    public int getItemCount() {
        return listaPersonaAuxiliar.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        ImageView imgEvidencia_qr;
        TextView txtFecha;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //Relacionar o enlazar los controles declarados en Java con los controles que estan en el cardview
            imgEvidencia_qr = itemView.findViewById(R.id.imgCodigoQR);

            txtFecha = itemView.findViewById(R.id.txtFechaCodigoQR);

        }


    }
}
