package com.appxy.billkeeper;


import com.actionbarsherlock.view.MenuItem;
import com.appxy.billkeeper.entity.MyApplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_ChangePass extends BaseHomeActivity {

	TextView turn, change;
	TextView changepass_tv;
	RelativeLayout turn_layout, change_layout;
	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	Context context;
	ColorStateList passon, passoff;
	ImageView back;
	int isset = 0;
	SharedPreferences sharedPreferences;
	public static final String PREFS_NAME = "SAVE_INFO";
	private int isPasscode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);  
        isPasscode = sharedPreferences.getInt("isPasscode", 0); //���ȡ������Ĭ��ֵΪ1  
        
		if (MyApplication.isPad) {
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		setContentView(R.layout.activity_change_pass);
		
//		 getActionBar().setDisplayShowHomeEnabled(false);
//		 getActionBar().setDisplayShowTitleEnabled(true);
		
//		 int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
//		 TextView title = (TextView) findViewById(titleId);
//		 title.setTextColor(this.getResources().getColor(R.color.white));
		 
		
		getActionBar().setDisplayHomeAsUpEnabled(true);//�Ƿ���ʾ���Ͻǵ���Ƿ��ؼ�
		
		context = this;
		Resources resource = (Resources) getBaseContext().getResources();
		
		passon = (ColorStateList) resource.getColorStateList(R.color.black);
		passoff = (ColorStateList) resource.getColorStateList(R.color.gray);
		
		turn = (TextView) findViewById(R.id.changepass_turn);
		change = (TextView) findViewById(R.id.changepass_change);
		turn_layout = (RelativeLayout) findViewById(R.id.changepass_layout2);
		change_layout = (RelativeLayout) findViewById(R.id.changepass_layout3);
		
		preferences = getSharedPreferences("TinyScanPro", MODE_PRIVATE);
		
		editor = preferences.edit();
		if (isPasscode == 1) {
			turn.setText("Turn passcode off");
			change.setTextColor(passon);
		} else {
			turn.setText("Turn passcode on");
			change.setTextColor(Color.rgb(163, 163, 163));
		}
		turn_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (isPasscode == 1) {
					Intent intent = new Intent(context,Activity_ClosePass.class);
					startActivityForResult(intent, 11);
				} else {
					Intent intent = new Intent(context, Activity_SetPass.class);
					startActivityForResult(intent, 22);
				}
			}

		});
		change_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (isPasscode == 1) {
					Intent intent = new Intent(context,Activity_ResetPass.class);
					startActivityForResult(intent, 33);
				} else {

				}
			}

		});

	}

//	OnTouchListener mlistener2 = new OnTouchListener() {
//
//		@Override
//		public boolean onTouch(View arg0, MotionEvent arg1) {
//			// TODO Auto-generated method stub
//			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
//				arg0.setBackgroundColor(Color.rgb(223, 223, 223));
//			} else if (arg1.getAction() == MotionEvent.ACTION_UP) {
//				arg0.setBackgroundColor(Color.TRANSPARENT);
//			} else if (arg1.getAction() == MotionEvent.ACTION_MOVE) {
//				arg0.setBackgroundColor(Color.rgb(223, 223, 223));
//			}
//			return false;
//		}
//
//	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		System.out.println(isPasscode + "sssisset");
		if (requestCode == 11) {
			if (resultCode == 11) {
				// editor.putBoolean("isSetPass", false);
				// editor.commit();
				
				SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);  
		        // ����ݽ��б༭,���ص���һ��Editor����  
		        SharedPreferences.Editor editor = sharedPreferences.edit();  
				editor.putInt("isPasscode", 0);  
		        editor.commit(); //����ݳ־û����洢�����У��п������ֻ����ô洢,�п�����SD����
		        isPasscode = 0;
		        
				turn.setText("Turn passcode on");
				change.setTextColor(Color.rgb(163, 163, 163));
				Toast toast = Toast.makeText(context,
						"Passcode cancel success.", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			} else {
				// editor.putBoolean("isSetPass", true);
				// editor.commit();
				SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);  
		        // ����ݽ��б༭,���ص���һ��Editor����  
		        SharedPreferences.Editor editor = sharedPreferences.edit();  
				editor.putInt("isPasscode", 1);  
		        editor.commit(); //����ݳ־û����洢�����У��п������ֻ����ô洢,�п�����SD����
		        isPasscode =1;
		        
				turn.setText("Turn passcode off");
				change.setTextColor(passon);
			}
		} else if (requestCode == 22) {
			if (resultCode == 11) {
				// editor.putBoolean("isSetPass", true);
				// editor.commit();
				SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);  
		        // ����ݽ��б༭,���ص���һ��Editor����  
		        SharedPreferences.Editor editor = sharedPreferences.edit();  
				editor.putInt("isPasscode", 1);  
		        editor.commit(); //����ݳ־û����洢�����У��п������ֻ����ô洢,�п�����SD����
		        isPasscode =1;
		        
				turn.setText("Turn passcode off");
				change.setTextColor(passon);

				Toast toast = Toast.makeText(context, "Passcode set success.",
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			} else {
				// editor.putBoolean("isSetPass", false);
				
				SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);  
		        // ����ݽ��б༭,���ص���һ��Editor����  
		        SharedPreferences.Editor editor = sharedPreferences.edit();  
				editor.putInt("isPasscode", 0);  
		        editor.commit(); //����ݳ־û����洢�����У��п������ֻ����ô洢,�п�����SD����
		        isPasscode =0;
		        
				// editor.commit();
				turn.setText("Turn passcode on");
				change.setTextColor(Color.rgb(163, 163, 163));
			}
		} else if (requestCode == 33) {
			if (resultCode == 11) {
				Toast toast = Toast.makeText(context,
						"Passcode change success.", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				
			} else {
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

	}

	protected void OnResume() {
		super.onResume();
		
		SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);  
        isPasscode = sharedPreferences.getInt("isPasscode", 0); //���ȡ������Ĭ��ֵΪ1  
        
		if (isPasscode == 1) {
			turn.setText("Turn passcode off");
			change.setTextColor(passon);
		} else {
			turn.setText("Turn passcode on");
			change.setTextColor(Color.rgb(163, 163, 163));
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:  //���Ͻǰ�ť���ص�id
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	

}
