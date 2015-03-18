package com.example.mthreadloader;

import com.example.mthreadloader.DownloadUtil.OnDownloadListener;

import android.os.Bundle;
import android.os.Environment;
import android.R.integer;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private static final String TAG = MainActivity.class.getSimpleName();//MainActivity这个类的名称
	
	private int max;

	private ProgressBar progressBar;
	private TextView proView;
	private EditText url;
	private EditText path;
	private Button start;
	private Button restart;
	private Button pause;
	private Button delete;
	
	private DownloadUtil mDownloadUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		progressBar = (ProgressBar)findViewById(R.id.progressBar1);
		proView = (TextView)findViewById(R.id.textView3);
		url = (EditText)findViewById(R.id.url);
		path = (EditText)findViewById(R.id.path);
		start = (Button)findViewById(R.id.start);
		restart = (Button)findViewById(R.id.restart);
		pause = (Button)findViewById(R.id.pause);
		delete = (Button)findViewById(R.id.delete);
		
		String localpath = Environment.getExternalStorageDirectory()
				.getAbsolutePath()+"/download";//获得sd卡路径
		mDownloadUtil = new DownloadUtil(4, localpath, path.getText().toString()
				, url.getText().toString(), this);
		
		mDownloadUtil.setOnDownloadListener(new OnDownloadListener() {
			
			@Override
			public void downloadStart(int fileSize) {
				// TODO Auto-generated method stub
				Log.w(TAG, "fileSize::" + fileSize);
				max = fileSize;
				progressBar.setMax(fileSize);
			}
			
			@Override
			public void downloadProgress(int downloadedSize) {
				// TODO Auto-generated method stub
				Log.w(TAG, "Compelete::" + downloadedSize);
				progressBar.setProgress(downloadedSize);
				proView.setText((int)downloadedSize*100/max+"%");
				if (downloadedSize==max) {
				Toast toast = 	Toast.makeText(getApplicationContext(), "finish", 
							Toast.LENGTH_SHORT);
					toast.show();
					proView.setText("");
					progressBar.setProgress(0);
				}
			}
			
			@Override
			public void downloadEnd() {
				// TODO Auto-generated method stub
				Log.w(TAG, "ENd");

			}
		});
		
		start.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mDownloadUtil.start();
			}
		});
		
		pause.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mDownloadUtil.pause();
			}
		});
		
		delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mDownloadUtil.delete();
				Toast toast = 	Toast.makeText(getApplicationContext(), "delete", 
						Toast.LENGTH_SHORT);
				toast.show();
				proView.setText("");
				progressBar.setProgress(0);
			}
		});
		
		restart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mDownloadUtil.reset();
			}
		});
	}

	

}
