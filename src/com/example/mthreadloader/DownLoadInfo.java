package com.example.mthreadloader;

import android.R.integer;

public class DownLoadInfo {//表示一个线程的信息

	private int threadId;
	private int startPos;
	private int endPos;
	private int completeSize;//线程完成量
	private String url;
	
	public DownLoadInfo(int threadId,int startPos,int endPos,
			int completeSize,String url) {
		// TODO Auto-generated constructor stub
		this.threadId = threadId;
		this.startPos = startPos;
		this.endPos = endPos;
		this.completeSize = completeSize;
		this.url = url;
	}
	public DownLoadInfo() {
		// TODO Auto-generated constructor stub
	}

	public int getThreadId() {
		return threadId;
	}

	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}

	public int getStartPos() {
		return startPos;
	}

	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}

	public int getEndPos() {
		return endPos;
	}

	public void setEndPos(int endPos) {
		this.endPos = endPos;
	}

	public int getCompleteSize() {
		return completeSize;
	}

	public void setCompleteSize(int completeSize) {
		this.completeSize = completeSize;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}
