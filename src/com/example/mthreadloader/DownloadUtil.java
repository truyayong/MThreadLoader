package com.example.mthreadloader;

import android.R.integer;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class DownloadUtil {

	private DownLoadHttpTool mDownLoadHttpTool;
	private OnDownloadListener onDownloadListener;
	
	private int fileSize;
	private int downloadedSize=0;
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int length = msg.arg1;
			synchronized (this) {//互斥
				downloadedSize+=length;
			}
			if(onDownloadListener!=null){
				onDownloadListener.downloadProgress(downloadedSize);//更新进度条
			}
			if (downloadedSize>=fileSize) {
				mDownLoadHttpTool.compelete();
				if (onDownloadListener!=null) {
					onDownloadListener.downloadEnd();
				}
			}
		}
	};
	
	public DownloadUtil(int threadCount, String filePath, String filename,
			String urlString, Context context) {
		// TODO Auto-generated constructor stub
		mDownLoadHttpTool = new DownLoadHttpTool(threadCount, urlString, 
				filePath, filename, context, mHandler);
	}
	
	public void start(){
		
		new AsyncTask<Void, Void, Void>() {//产生一个异步线程,因为安卓4.0以后主线程不能访问网络数据

			@Override
			protected Void doInBackground(Void... arg0) {
				// TODO Auto-generated method stub
				mDownLoadHttpTool.ready();//耗时方法，在异步线程中执行
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {//异步线程处理完成返回结果，主线程处理结果
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				fileSize = mDownLoadHttpTool.getFileSize();
				downloadedSize = mDownLoadHttpTool.getCompeleteSize();
				Log.w("Tag", "downloadedSize::" + downloadedSize);
				if (onDownloadListener!=null) {
					onDownloadListener.downloadStart(fileSize);//设置进度条的总长
				}
				mDownLoadHttpTool.start();
			}
		}.execute();
	}
	
	public void pause() {
		mDownLoadHttpTool.pause();
	}
	
	public void delete(){
		mDownLoadHttpTool.delete();
	}

	public void reset(){
		mDownLoadHttpTool.delete();
		start();
	}
	
	public void setOnDownloadListener(OnDownloadListener onDownloadListener){
		this.onDownloadListener=onDownloadListener;
	}
	public interface OnDownloadListener {//回调接口
		public void downloadStart(int fileSize);

		public void downloadProgress(int downloadedSize);

		public void downloadEnd();
	}
}
