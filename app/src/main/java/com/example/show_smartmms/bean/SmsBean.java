package com.example.show_smartmms.bean;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import android.graphics.Bitmap;

import com.example.show_smartmms.utils.BitmapUtils;
import com.example.show_smartmms.utils.SmsUtils;

public class SmsBean implements Serializable,Comparable<SmsBean> {

	String address;
	String body;
	String type;
	long date;
	String strDate;
	int count;
	String name;
	int index;
	Bitmap bitmapAvantar;
	
	public Bitmap getBitmapAvantar() {
		return bitmapAvantar;
	}

	public void setBitmapAvantar(Bitmap bitmapAvantar) {
		this.bitmapAvantar = bitmapAvantar;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	private int viewtype;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	String thread_id;
	private String formatDate;

	public String getThread_id() {
		return thread_id;
	}

	public void setThread_id(String thread_id) {
		this.thread_id = thread_id;
	}

	public SmsBean(String address, String body, String type, long date,
			int count) {
		super();
		this.address = address;
		this.body = body;
		this.type = type;
		this.date = date;
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public SmsBean() {
	}

	@Override
	public String toString() {
		return "SmsBean [address=" + address + ", body=" + body + ", type="
				+ type + ", date=" + getDate() + ", viewType =" + viewtype +", index =" + index + "]";
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDate() {
		if (strDate != null)
			return strDate;

		formatDate = BitmapUtils.instance().formatDate(new Date(date));
		return formatDate;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public void setDate(String date) {
		this.strDate = date;
	}

//	@Override
//	public int hashCode() {
//		formatDate = BitmapUtils.instance().formatDate(new Date(date));
//		String[] split = formatDate.split(" ");
//		System.out.println(split[0] + "<---split[0]");
//		return split[0].hashCode();
//	}
	
	@Override
	public int hashCode() {
		return getDate().split(" ")[0].hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		 if(o instanceof SmsBean) {		
			 return true;
		 }
			
		return false;
	}

	public int getViewtype() {
		return viewtype;
	}

	public void setViewtype(int viewtype) {
		this.viewtype = viewtype;
	}

	@Override
	public int compareTo(SmsBean another) {
		return this.getIndex().compareTo(another.getIndex());
	}

}
