package com.appxy.billkeeper;

import com.appxy.billkeeper.entity.MyApplication;
import com.slidingmenu.lib.app.SlidingActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

public class BaseHomeActivity extends Activity{
	
	SharedPreferences sharedPreferences;
	public static final String PREFS_NAME = "SAVE_INFO";
	private int isPasscode;
	
	Context context;
	SharedPreferences preferences;
	HomeKeyEventBroadCastReceiver receiver;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);  
		isPasscode = sharedPreferences.getInt("isPasscode", 0); //���ȡ������Ĭ��ֵΪ0
        
		context = this;
		preferences = getSharedPreferences("TinyScanPro", MODE_PRIVATE);
		receiver = new HomeKeyEventBroadCastReceiver();
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if(MyApplication.isHomePress){
			MyApplication.isHomePress = false;
			
			SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);  
			isPasscode = sharedPreferences.getInt("isPasscode", 0); //���ȡ������Ĭ��ֵΪ0
			
			if(isPasscode==1){
				Intent intent = new Intent(this, Activity_HomeBack.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent ,45);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

	class HomeKeyEventBroadCastReceiver extends BroadcastReceiver {

		static final String SYSTEM_REASON = "reason";
		static final String SYSTEM_HOME_KEY = "homekey";//home key
		static final String SYSTEM_RECENT_APPS = "recentapps";//long home key
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra(SYSTEM_REASON);
				if (reason != null) {
					if (reason.equals(SYSTEM_HOME_KEY)) {
						MyApplication.isHomePress = true;
						
					} else if (reason.equals(SYSTEM_RECENT_APPS)) {
						MyApplication.isHomePress = true;
					}
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
	}
	
}
