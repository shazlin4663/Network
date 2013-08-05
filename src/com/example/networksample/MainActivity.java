package com.example.networksample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String DEBUG_TAG = "HttpExample";
	private static final String KEYWORD_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyD_eSCF6heEay7b4-KSbKRHTeRGR-apcjY&location=-33.8670522,151.1957362&radius=500&sensor=false&keyword=";
	private static final String REFERENCE_URL = "https://maps.googleapis.com/maps/api/place/details/json?sensor=false&key=AIzaSyD_eSCF6heEay7b4-KSbKRHTeRGR-apcjY&reference=";
	private EditText textSearch;
	private Button btnSearch;
	private ListView listView;
	private ProgressDialog dialog;
	private String strURL;
	private List<Place> listPlaceInfo = new ArrayList<Place>();
	private PlaceItems _placeItemAdapter;
	private boolean isReferencePlace = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		textSearch = (EditText) findViewById(R.id.textSearch);
		btnSearch = (Button) findViewById(R.id.btnSearch);
		listView = (ListView) findViewById(R.id.listView);
		
		btnSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String editSearch = textSearch.getText().toString();
				if (editSearch != null && !(editSearch.isEmpty())) {
					if (isNetworkAvailable()) {
						strURL = KEYWORD_URL.concat(textSearch.getText().toString());	
						new DownloadWebpageTask().execute(strURL);
			      		dialog = ProgressDialog.show(MainActivity.this, "Please wait", "Loading");
					
					}
					else {
						Toast.makeText(MainActivity.this, "Check network Connection", Toast.LENGTH_LONG).show();		
					}
				}
			}
		});
		_placeItemAdapter = new PlaceItems(MainActivity.this, listPlaceInfo);
		listView.setAdapter(_placeItemAdapter);
		
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				isReferencePlace = true;
				String refURL = REFERENCE_URL.concat(listPlaceInfo.get(position).getReference());
				new DownloadWebpageTask().execute(refURL);
				
			}
			
		});
	}
	
private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
	
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
	        	listPlaceInfo.clear();
	        	
	        	if (isReferencePlace == false) 
	        		showPlaceDetail(result);
	        	else  {
	        		showReferencePlace(result);
	        		isReferencePlace = false;
	        	}
					
				_placeItemAdapter.notifyDataSetChanged();
					
				if (dialog != null && dialog.isShowing()) {
	        		dialog.hide();
	        	}				
	       }
	    }
private void showReferencePlace(String result) {
	try {
		JSONObject jsonObject = new JSONObject(result);
		JSONObject jsonResult = jsonObject.getJSONObject("result");
		
			Place place = new Place();
			place.setAddress(jsonResult.getString("formatted_address"));
			place.setIconURL(jsonResult.getString("icon"));
			place.setReference(jsonResult.getString("reference"));
			place.setId(jsonResult.getString("id"));
			place.setName(jsonResult.getString("name"));
			place.setRate(jsonResult.getString("rating"));
			
			if (jsonResult.has("events"))
				place.setSummary(jsonResult.getJSONObject("events").getString("summary"));
			
			listPlaceInfo.add(place);
			new LoadImage(place, _placeItemAdapter).execute(place.getIconURL());
		
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		Toast.makeText(MainActivity.this, "showRefernceDetail " + result, Toast.LENGTH_LONG).show();
	}
}

private void showPlaceDetail (String result) {
	JSONObject jsonObject;
	JSONArray jArrayResult;
	
	try {
		jsonObject = new JSONObject(result);
		jArrayResult = jsonObject.getJSONArray("results");	
		for (int x = 0; x < jArrayResult.length(); x++) {
			Place place = new Place();
			JSONObject jGetInfo = jArrayResult.getJSONObject(x);
			
			place.setAddress(jGetInfo.getString("vicinity"));
			place.setIconURL(jGetInfo.getString("icon"));
			place.setReference(jGetInfo.getString("reference"));
			place.setId(jGetInfo.getString("id"));
			place.setName(jGetInfo.getString("name"));
			
			listPlaceInfo.add(place);
			new LoadImage(place, _placeItemAdapter).execute(place.getIconURL());
		}
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		Toast.makeText(MainActivity.this, "showPlaceDetail " + result, Toast.LENGTH_LONG).show();
	}
}

private String downloadUrl(String myurl) throws IOException {
	    InputStream is = null;
	    try {
	        URL url = new URL(myurl);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setReadTimeout(10000);
	        conn.setConnectTimeout(15000);
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
