package com.example.mthreadloader;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import android.R.integer;
import android.app.DownloadManager;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class DownLoadHttpTool {

	private static final String TAG =DownLoadHttpTool.class.getSimpleName();
	
	private int threadCount;
	private String urlString;
	private Context mcontext;
	private Handler mhandler;
	private List<DownLoadInfo>downLoadInfos;
	
	private String localPath;
	private String fileName;
	private int fileSize;
	private DownLoadSqlTool sqlTool;//数据库工具类
	
	private enum Download_State{
		Downloading, Pause, Ready;//下载状态
	}
	
	private Download_State state = Download_State.Ready;
	private int globalcompelete = 0;
	
	public DownLoadHttpTool(int threadCount, String urlString,
			String localPath, String fileName, Context context, Handler handler){
		
		this.threadCount = threadCount;
		this.urlString = urlString;
		this.localPath = localPath;
		this.fileName = fileName;
		this.mcontext = context;
		this.mhandler = handler;
		sqlTool = new DownLoadSqlTool(mcontext);
	}
	
	public void ready(){
		Log.w(TAG, "ready");
		globalcompelete = 0;//所有线程完成进度
		downLoadInfos = sqlTool.getThreadInfos(urlString);//从数据库取出线程信息
		
		if (downLoadInfos.size()==0) {
			initFirst();
		}else {
			File file = new File(localPath+"/"+fileName);
			if (!file.exists()) {
				sqlTool.delete(urlString);
				initFirst();
			} else {
               fileSize = downLoadInfos.get(threadCount-1).getEndPos();
               for (DownLoadInfo info : downLoadInfos) {
				globalcompelete+=info.getCompleteSize();
			}
               Log.w(TAG, "globalcompelete::"+globalcompelete);
			}
		}
	}
	
	public void start(){
		Log.w(TAG, "start");
		if (downLoadInfos!=null) {
			if (state==Download_State.Downloading) {
				return;
			}
			state=Download_State.Downloading;
			for (DownLoadInfo info : downLoadInfos) {//开启全部下载线程
				Log.v(TAG, "startThread");
				new DownloadThread(info.getThreadId(), info.getStartPos(),
						info.getEndPos(), info.getCompleteSize(),
						info.getUrl()).start();
			}
		}
	}
	
	public void pause(){
		state=Download_State.Pause;
		sqlTool.DBclose();
	}
	
	public void delete(){
		state=Download_State.Pause;
		compelete();
		File file = new File(localPath + "/" + fileName);
		file.delete();
	}
	
	public void compelete() {
		sqlTool.delete(urlString);
		sqlTool.DBclose();
	}

	public int getFileSize() {
		return fileSize;
	}

	public int getCompeleteSize() {
		return globalcompelete;
	}
	
	public void initFirst(){
		Log.w(TAG, "inifirst");
		try {
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setConnectTimeout(5000);
			connection.setRequestMethod("GET");
			fileSize = connection.getContentLength();
			Log.w(TAG, "fileSize::"+fileSize);
			
			File fileParent = new File(localPath);
			if (!fileParent.exists()) {
				fileParent.mkdir();
			}
			File file = new File(fileParent, fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			//设置文件大小
			RandomAccessFile accessFile = new RandomAccessFile(file, "rw");//指向文件的流，起始位置可变
			accessFile.setLength(fileSize);//限制流指向的范围
			accessFile.close();
			connection.disconnect();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		int range = fileSize/threadCount;
		downLoadInfos = new ArrayList<DownLoadInfo>();
		for (int i = 0; i < threadCount-1; i++) {
			DownLoadInfo info = new DownLoadInfo(i, i*range, (i+1)*range-1,
					0, urlString);
			downLoadInfos.add(info);
		}
		DownLoadInfo info = new DownLoadInfo(threadCount-1, (threadCount-1)*range, fileSize,
				0, urlString);
		downLoadInfos.add(info);
		sqlTool.insertInfos(downLoadInfos);
	}
	
	private class DownloadThread extends Thread{
		private int threadId;
		private int startPos;
		private int endPos;
		private int compeleteSize;
		private String urlstr;
		private int totalThreadSize;

		public DownloadThread(int threadId, int startPos, int endPos,
				int compeleteSize, String urlstr) {
			this.threadId = threadId;
			this.startPos = startPos;
			this.endPos = endPos;
			totalThreadSize = endPos - startPos + 1;
			this.urlstr = urlstr;
			this.compeleteSize = compeleteSize;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			HttpURLConnection connection = null;
			RandomAccessFile randomAccessFile =null;
			InputStream is = null;
			try {
				randomAccessFile = new RandomAccessFile(localPath+"/"+fileName
						, "rwd");
				randomAccessFile.seek(startPos+compeleteSize);
				URL url = new URL(urlstr);
				connection = (HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(5000);
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Range", "bytes="
						+ (startPos + compeleteSize) + "-" + endPos);
				is = connection.getInputStream();
				byte[] buffer = new byte[1024];
				int length = -1;
				while ((length=is.read(buffer))!=-1) {
					randomAccessFile.write(buffer, 0, length);
					compeleteSize+=length;
					Message message = Message.obtain();
					message.what=threadId;
					message.obj=urlstr;
					message.arg1=length;
					mhandler.sendMessage(message);//下载进度有while频率发送
					sqlTool.updataInfos(threadId, compeleteSize, urlstr);
					Log.w(TAG, "Threadid::" + threadId + "    compelete::"
							+ compeleteSize + "    total::" + totalThreadSize);
					if (compeleteSize>=totalThreadSize) {
						break;
					}
					if (state!=Download_State.Downloading) {
						break;
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally{
				try {
					if (is!=null) {
						is.close();
					}
					randomAccessFile.close();
					connection.disconnect();
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}
			}
		}
	}
	
}
