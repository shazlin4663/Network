package com.example.networksample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String DEBUG_TAG = "HttpExample";
	private static final String URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyD_eSCF6heEay7b4-KSbKRHTeRGR-apcjY&location=-33.8670522,151.1957362&radius=500&sensor=false&keyword=";
	private EditText textSearch;
	private Button btnSearch;
	private ListView listView;
	private ProgressDialog dialog;
	private String strURL;
	private List<Place> placeInfo = new ArrayList<Place>();
	private PlaceItems _placeItems;
	
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
						strURL = URL.concat(textSearch.getText().toString());	
						new DownloadWebpageTask().execute(strURL);
			      		dialog = ProgressDialog.show(MainActivity.this, "Please wait", "Loading");
					
					}
					else {
						Toast.makeText(MainActivity.this, "Check network Connection", Toast.LENGTH_LONG).show();		
					}
				}
			}
		});
		_placeItems = new PlaceItems(MainActivity.this, placeInfo);
		listView.setAdapter(_placeItems);
	}
	/*	_placeItems.registerDataSetObserver(new dataSetObserver() {
		});
	}
	private class dataSetObserver extends DataSetObserver {
		@Override
		 public void onChanged() {
		        // Do nothing
		    }
		
	}*/
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
	        	JSONObject jsonObject;
	        	JSONArray jArrayResult;
	        	placeInfo.clear();
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
						
						placeInfo.add(place);
						new LoadImage(place, _placeItems).execute(place.getIconURL());
					}
					
					_placeItems.notifyDataSetChanged();
					
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					 e1.printStackTrace();
					Toast.makeText(MainActivity.this, "exception " + result, Toast.LENGTH_LONG).show();
				}
	 
				if (dialog != null && dialog.isShowing()) {
	        		dialog.hide();
	        	}				
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
