package com.example.covidandroid.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hmera on 27/04/2016.
 * Last update by hmera on 03/11/2019.
 */
public class ServiciosWeb {

    //public static final String URL_WS = "http://hci.pe/api_android/ws/";
    public static final String URL_WS = "http://192.168.1.2/wsTesis/ws/";

    public String openWebServiceBearer(String requestURL, String bearerToken, JSONObject jsonParam)
    {
        StringBuilder sb = new StringBuilder();

        try {
            URL url = new URL(requestURL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(15000);
            urlConnection.setRequestProperty("Authorization", "Bearer " + bearerToken);
            urlConnection.setRequestProperty("Content-Type", "application/json");

            OutputStreamWriter out = new   OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8");
            out.write(jsonParam.toString());
            out.close();

            int HttpResult =urlConnection.getResponseCode();
            if(HttpResult == 200)
            {
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "iso-8859-1"), 1024);
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                //System.out.println("xx" + sb.toString());
            }
            else
            {
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream(), "iso-8859-1"), 1024);
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                //System.out.println("xx" + sb.toString());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();

        }catch (FileNotFoundException e2){
            e2.printStackTrace();
            //Log.e("Error File Not Found", e2.getMessage());

        } catch (Exception e) {
            e.printStackTrace();

        }finally{
            /*if(urlConnection!=null)
                urlConnection.disconnect();*/
        }

        return sb.toString();

    }

    public String openWebServiceFormData(String requestURL, HashMap<String, String> postDataParams) {

        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            if (postDataParams != null){
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));
                writer.flush();
                writer.close();
            }

            os.close();
            //int responseCode=conn.getResponseCode();

            int HttpResult =conn.getResponseCode();
            if(HttpResult == 200){
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream(), "iso-8859-1"), 1024);
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }else{
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getErrorStream(), "iso-8859-1"), 1024);
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }

            //System.out.println("RRRR: " + responseCode);


        }catch (FileNotFoundException e2){
            e2.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();

        }

        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }


    public Bitmap descargarImagenURL(String url) {
        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();

            int statusCode = urlConnection.getResponseCode();
            if (statusCode != 200) {
                return null;
            }

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
            urlConnection.disconnect();
            Log.w("ImageDownloader", "Error downloading image from " + url);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    public Bitmap descargarImagen (String imageHttpAddress){
        URL imageUrl = null;
        Bitmap imagen = null;
        try{
            imageUrl = new URL(imageHttpAddress);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.connect();
            imagen = BitmapFactory.decodeStream(conn.getInputStream());
        }catch(IOException ex){
            ex.printStackTrace();
        }

        return imagen;
    }
/*
    public boolean isOnline(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {

                return checkHttpConnection();
                //return true;
            }
        }
        return false;
    }

    public boolean checkHttpConnection() {
        String link = "http://www.google.com.pe";
        URL url = null;
        try {
            url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();

            int resCode = conn.getResponseCode();

            if (resCode == 200){
                return true;
            }

            //System.out.println("Response code:===========" + resCode);

        } catch (MalformedURLException e) {
            return false;

            //e.printStackTrace();
        }catch (IOException e2){
            //System.out.println(e2.);
            return false;
        }

        return false;

    }*/




    /*
    public static void configurarAlmacenamientoCache(Context app){
        //Configurar la librería para guaradar en el cache
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder( app );
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app
        ImageLoader.getInstance().init(config.build());
        //Configurar la librería para guaradar en el cache
    }
    */

    /*
    public static void habilitarDirectivasInternetX(){
        //Si la versión del SO es mayor a la versión de GINGERBARD, entonces habilita una politica especial para conectarse a internet
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        //Si la versión del SO es mayor a la versión de GINGERBARD, entonces habilita una politica especial para conectarse a internet
    }
    */

    /*
    public static void asignarColorSwipeLayout(SwipeRefreshLayout swipeContenedor){
        swipeContenedor.setColorScheme
                (
                    android.R.color.holo_red_light,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_blue_bright
                );
    }*/




}

