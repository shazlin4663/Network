package com.example.networksample;

import android.graphics.Bitmap;

public class Place {
	private String _address, _iconURL, _id, _name, _reference;
	private Bitmap _image;


	public Bitmap getBitmap() {
		return _image;
	}
	public void setBitmap(Bitmap image) {
		_image = image;
	}	
	public String getAddress() {
		return _address;
	}

	public void setAddress(String address) {
		this._address = address;
	}

	public String getIconURL() {
		return _iconURL;
	}

	public void setIconURL(String iconURL) {
		this._iconURL = iconURL;
	}

	public String getId() {
		return _id;
	}

	public void setId(String id) {
		this._id = id;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		this._name = name;
	}

	public String getReference() {
		return _reference;
	}

	public void setReference(String reference) {
		this._reference = reference;
	}
	
}
