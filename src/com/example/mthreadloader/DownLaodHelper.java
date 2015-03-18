package com.example.mthreadloader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DownLaodHelper extends SQLiteOpenHelper{
	
	private static final String SQL_NAME = "download.db";
	private static final int DOWNLOAD_VERSION = 1;

	public DownLaodHelper(Context context) {
		super(context, SQL_NAME, null, DOWNLOAD_VERSION);
		// TODO Auto-generated constructor stub
	}

	//建立表格
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table download_info(_id integer PRIMARY KEY AUTOINCREMENT, thread_id integer, "
                + "start_pos integer, end_pos integer, compelete_size integer,url char)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {
		// TODO Auto-generated method stub
		
	}

}
