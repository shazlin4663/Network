package com.example.networksample;

import java.net.URL;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PlaceItems extends BaseAdapter {
	private  List<Place> _placeInfo;
	private Context _context;
	
	public PlaceItems (Context context, List<Place> placeInfo) {
		_placeInfo = placeInfo;
		_context = context;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return _placeInfo.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return _placeInfo.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Place currentPlace = _placeInfo.get(position);
		String name = currentPlace.getName();
		String address = currentPlace.getAddress();
		
		LayoutInflater layoutInflater = LayoutInflater.from(_context);
		View placeView = layoutInflater.inflate(R.layout.activity_place_items, null);
		TextView viewName = (TextView) placeView.findViewById(R.id.viewName);
		TextView viewAddress = (TextView) placeView.findViewById(R.id.viewAddress);
		ImageView viewImage = (ImageView) placeView.findViewById(R.id.viewImage);
			
		
		viewName.setText(name);
		viewAddress.setText(address);
		if (currentPlace.getBitmap() != null) {
			viewImage.setImageBitmap(currentPlace.getBitmap());
		}
		
		return placeView;
	}

}
