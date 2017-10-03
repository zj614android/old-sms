package com.example.show_smartmms.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBopenhelper extends SQLiteOpenHelper {

	public DBopenhelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {	// 创建groups表
//		String sql = "create table groups(_id integer primary key, group_name varchar(20));";
//		db.execSQL(sql);
//		// 创建thread_group表
//		sql = "create table thread_group(_id integer primary key, thread_id integer, group_id integer);";
//		db.execSQL(sql);
		db.execSQL("CREATE TABLE IF NOT EXISTS groupname_table (group_id integer primary key,group_name varchar(20))");
		db.execSQL("CREATE TABLE IF NOT EXISTS thread_and_group_table (_id integer primary key,group_id integer,thread_id integer)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
