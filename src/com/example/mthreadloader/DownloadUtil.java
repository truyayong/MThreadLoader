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
			synchronized (this) {//����
				downloadedSize+=length;
			}
			if(onDownloadListener!=null){
				onDownloadListener.downloadProgress(downloadedSize);//���½�����
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
		
		new AsyncTask<Void, Void, Void>() {//����һ���첽�߳�,��Ϊ��׿4.0�Ժ����̲߳��ܷ�����������

			@Override
			protected Void doInBackground(Void... arg0) {
				// TODO Auto-generated method stub
				mDownLoadHttpTool.ready();//��ʱ���������첽�߳���ִ��
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {//�첽�̴߳�����ɷ��ؽ�������̴߳�����
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				fileSize = mDownLoadHttpTool.getFileSize();
				downloadedSize = mDownLoadHttpTool.getCompeleteSize();
				Log.w("Tag", "downloadedSize::" + downloadedSize);
				if (onDownloadListener!=null) {
					onDownloadListener.downloadStart(fileSize);//���ý��������ܳ�
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
	public interface OnDownloadListener {//�ص��ӿ�
		public void downloadStart(int fileSize);

		public void downloadProgress(int downloadedSize);

		public void downloadEnd();
	}
}
