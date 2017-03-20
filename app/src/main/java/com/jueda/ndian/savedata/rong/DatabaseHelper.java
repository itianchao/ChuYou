package com.jueda.ndian.savedata.rong;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
	private static final String DB_NAME = "mydata.db"; //
	private static final int version = 1; //
	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql="CREATE TABLE IF NOT EXISTS friend"+"(_id INTEGER PRIMARY KEY AUTOINCREMENT,userId VARCHAR,userName VARCHAR,portraitUri VARCHAR)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("ALTER TABLE person ADD COLUMN other STRING");
	}

}
