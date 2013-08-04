package com.example.networksample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.Toast;


public class LoadImage extends AsyncTask<String, Void, Bitmap> {
	Place _placeImage;
	PlaceItems _adapter;
	
	public LoadImage (Place placeImage, PlaceItems adapter) {
		_placeImage = placeImage;
		_adapter = adapter;
		
	}
			@Override
	        protected Bitmap doInBackground(String... urls) {
	              
	            // params comes from the execute() call: params[0] is the url.
	            try {
	                return downloadUrl(urls[0]);
	            } catch (IOException e) {
	            }
				return null;
	        }

			// onPostExecute displays the results of the AsyncTask.
	        @Override
	        protected void onPostExecute(Bitmap bitmap) {
	        	if (bitmap == null)
	        		return;
	        	_placeImage.setBitmap(bitmap);
	        	_adapter.notifyDataSetChanged();
	        	
	
	    }

private Bitmap downloadUrl(String myurl) throws IOException {
	    InputStream is = null;
	    try {
	        URL url = new URL(myurl);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setReadTimeout(10000 /* milliseconds */);
	        conn.setConnectTimeout(15000 /* milliseconds */);
	        conn.setRequestMethod("GET");
	        conn.setDoInput(true);
	        // Starts the query
	        conn.connect();
	        int response = conn.getResponseCode();
	       // Log.d(DEBUG_TAG, "The response is: " + response);
	        is = conn.getInputStream();

	        Bitmap bitmap = BitmapFactory.decodeStream(is);
	        // Convert the InputStream into a string
	        // contentAsString = readIt(is);
	      return bitmap;
	        
	    // Makes sure that the InputStream is closed after the app is
	    // finished using it.
	    } finally {
	        if (is != null) {
	            is.close();
	        } 
	    }
	}
}
