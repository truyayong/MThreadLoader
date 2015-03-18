package com.example.mthreadloader;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DownLoadSqlTool {

	private DownLaodHelper dbHelper;
	
	//context´«µÝ¸ødbhelper
	public DownLoadSqlTool(Context context) {
		// TODO Auto-generated constructor stub
		dbHelper = new DownLaodHelper(context);
	}
	
	public void insertInfos(List<DownLoadInfo>infos){
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		for (DownLoadInfo info : infos) {
			  String sql = "insert into download_info(thread_id,start_pos," +
			  		" end_pos,compelete_size,url)" +
			  		" values (?,?,?,?,?)";
	            Object[] bindArgs = { info.getThreadId(), info.getStartPos(),
	                    info.getEndPos(), info.getCompleteSize(), info.getUrl() };
	            database.execSQL(sql, bindArgs);
		}
	}
	
	public List<DownLoadInfo> getThreadInfos(String url){
		List<DownLoadInfo>list = new ArrayList<DownLoadInfo>();
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql = "select thread_id, start_pos, end_pos,compelete_size,url " +
				"from download_info where url=?";
		Cursor cursur = database.rawQuery(sql, new String[]{url});//
		while (cursur.moveToNext()) {//
           DownLoadInfo info = new DownLoadInfo(cursur.getInt(0),
        		   cursur.getInt(1), cursur.getInt(2), cursur.getInt(3), 
        		   cursur.getString(4));	
           list.add(info);
		}
		return list;
	}
	public void updataInfos(int threadId,int completeSize,String url){
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql = "update download_info set compelete_size=? " +
				"where thread_id=? and url=?";
		Object[] bindArgs = {completeSize,threadId,url};
		database.execSQL(sql, bindArgs);
	}
	
	public void delete(String url){
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		database.delete("download_info", "url=?", new String[]{url});
	}
	
	public void DBclose(){
		dbHelper.close();
	}
}
