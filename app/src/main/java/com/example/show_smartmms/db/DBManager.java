package com.example.show_smartmms.db;

import com.example.show_smartmms.common.CommonFields;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DBManager implements CommonFields {

	Context context;
	private static DBManager dbManager;
	private DBopenhelper dBopenhelper;
	private SQLiteDatabase db;
	
	private DBManager() {
		
	}

	public static synchronized DBManager instance() {
		
		if (dbManager == null) {
			dbManager = new DBManager();
		}
		return dbManager;
	}

	public void init(Context context) {
		dBopenhelper = new DBopenhelper(context, DB_NAME, null, 10);
		db = dBopenhelper.getWritableDatabase();
	}

}
