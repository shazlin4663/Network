package com.example.networksample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	  private static final String DEBUG_TAG = "HttpExample";
	  static final String URL = "http://api.wunderground.com/api/9a6e91c790252381/conditions/q/CA/San_Francisco.json";
	EditText textSearch;
	TextView textURL;
	Button btnSearch;
	ListView listView;
	ProgressDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		

		textURL = (TextView) findViewById(R.id.textURL);
		textSearch = (EditText) findViewById(R.id.textSearch);
		btnSearch = (Button) findViewById(R.id.btnSearch);
	//	listView = (ListView) findViewById(R.id.listView);
		
		btnSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String editSearch = textSearch.getText().toString();
				if (editSearch != null && !(editSearch.isEmpty())) {
					if (isNetworkAvailable()) {
						new DownloadWebpageTask().execute(URL, "");
			      		dialog = ProgressDialog.show(MainActivity.this, "hi", "his");
						
					}
					else {
						Toast.makeText(MainActivity.this, "Check network Connection", Toast.LENGTH_LONG).show();		
					}
				}
			}
		});

	}
private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
	
	
	        @Override
	protected void onProgressUpdate(Void... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}
			@Override
	        protected String doInBackground(String... urls) {
	              
	            // params comes from the execute() call: params[0] is the url.
	            try {
	                return downloadUrl(urls[0]);
	            } catch (IOException e) {
	                return "Unable to retrieve web page. URL may be invalid.";
	            }
	        }
	        // onPostExecute displays the results of the AsyncTask.
	        @Override
	        protected void onPostExecute(String result) {
	        	JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(result);
					JSONObject current = jsonObject.getJSONObject("current_observation"); 
					JSONObject displayLocation = current.getJSONObject("display_location"); 
					textURL.setText(displayLocation.getString("city"));
					
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					 e1.printStackTrace();
					Toast.makeText(MainActivity.this, "JSON exception", Toast.LENGTH_LONG).show();
				}
	 
				if (dialog != null && dialog.isShowing()) {
	        		dialog.hide();
	        	}				
	       }
	    }

private String downloadUrl(String myurl) throws IOException {
	    InputStream is = null;
	    // Only display the first 500 characters of the retrieved
	    // web page content.
	//    int len = 1000;
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
	        Log.d(DEBUG_TAG, "The response is: " + response);
	        is = conn.getInputStream();

	        // Convert the InputStream into a string
	        String contentAsString = readIt(is);
	      return contentAsString;
	        
	    // Makes sure that the InputStream is closed after the app is
	    // finished using it.
	    } finally {
	        if (is != null) {
	            is.close();
	        } 
	    }
	}

public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
	BufferedReader r = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
	StringBuilder total = new StringBuilder();
	String line;
	while ((line = r.readLine()) != null) {
		total.append(line);
	}
	/*
    Reader reader = null;
    reader = new InputStreamReader(stream, "UTF-8");        
    char[] buffer = new char[30000];
    reader.read(buffer);
    return new String(buffer);*/
	return total.toString();
}

public boolean isNetworkAvailable() {
	ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	if (networkInfo != null && networkInfo.isConnected()) {
		return true;
	} else {
		// display error
		return false;
	}

}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
