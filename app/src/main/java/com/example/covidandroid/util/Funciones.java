package com.example.covidandroid.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Spinner;

import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//import pe.edu.usat.hmera.laboratorio.cardviewsqlite.R;
//import com.example.iniciosesiones.R;


import com.example.covidandroid.R;



public class Funciones {

    static String currentPhotoPath;

    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = ((Activity) context).getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    public static String imageToBase64(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String imageDecode = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return imageDecode;
    }


    public static Bitmap base64ToImage(String imageString){
        //encode image to base64 string
        byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        return decodedImage;
    }

    public static void selectedItemSpinner(Spinner spinner, String itemSelection){
        int position = 0;
        for (int i=0; i<spinner.getCount(); i++){
            if (spinner.getItemAtPosition(i).equals(itemSelection)){
                position = i;
                break;
            }
        }
        spinner.setSelection(position);
    }


    //Gestionar diálogos
    public static void dialog(Context context, String title, String bt_ok, String bt_cancel, final Runnable if_ok){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(title)
                .setCancelable(false)
                .setPositiveButton(bt_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if_ok.run();
                    }
                }).setNegativeButton(bt_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel(); //close dialog
            }
        });

        //show dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    //Método que permite tomar fotos
    public static void tomarFoto(Activity activity, int request_code) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(takePictureIntent, request_code);
        }
    }

    public static String obtenerDireccionMapa(Context context, double latitud, double longitud) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitud, longitud, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
            } else {
                Log.w("Dirección", "No se ha retornado la dirección!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd.substring(0,strAdd.length()-1);
    }



}
