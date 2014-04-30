package com.appxy.billkeeper.activity;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.appxy.billkeeper.Activity_HomeBack;
import com.appxy.billkeeper.R;
import com.appxy.billkeeper.db.BillKeeperSql;
import com.appxy.billkeeper.entity.Common;
import com.appxy.billkeeper.entity.MyApplication;
import com.appxy.billkeeper.fragment.AccountFragment;
import com.appxy.billkeeper.fragment.NewUpcomingFragment;
import com.appxy.billkeeper.fragment.UpcomingFragment;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;


public class SlidingMenuActivity extends BaseActivity {
	
	private BillKeeperSql bkSql;
	Context context;
	private Map<String, Object> dataMap;
	
	SharedPreferences sharedPreferences;
	mHomeKeyEventBroadCastReceiver receiver;
	public static final String PREFS_NAME = "SAVE_INFO";
	private int isPasscode;
	public static final String SETTING_ID = "1";
	private FragmentManager fragmentManager;
	
	private SharedPreferences.Editor editor;
	private SharedPreferences preferences;
	private LayoutInflater inflater;
	
	public SlidingMenuActivity() {
		super(R.string.m_app_name);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	    fragmentManager=getFragmentManager(); 
		context = this;
		sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);  
		isPasscode = sharedPreferences.getInt("isPasscode", 0); //���ȡ������Ĭ��ֵΪ0
		receiver = new mHomeKeyEventBroadCastReceiver();
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
		
		setContentView(R.layout.content_frame);
		
		inflater = LayoutInflater.from(this);
		dataMap = new HashMap<String, Object>();
		bkSql = new BillKeeperSql(this); 
		SQLiteDatabase db = bkSql.getReadableDatabase();
		String sql = "select bk_settingBadge , bk_settingCurrency ,bk_settingPasscode from BK_Seeting where _id = "+SETTING_ID;
		Cursor cursorEA = db.rawQuery(sql,null);
		while (cursorEA.moveToNext()) {
			
			  int bk_settingBadge = cursorEA.getInt(0);
			  int bk_settingCurrency = cursorEA.getInt(1);
			  String bk_settingPasscode = cursorEA.getString(2);
			  
			  dataMap.put("bk_settingBadge", bk_settingBadge);
			  dataMap.put("bk_settingCurrency", bk_settingCurrency);
			  dataMap.put("bk_settingPasscode", bk_settingPasscode);
		}
		
		cursorEA.close();
		db.close();
		if (dataMap != null) {
			Common.CURRENCY = (Integer)dataMap.get("bk_settingCurrency");
		}
		
	    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); 
		NewUpcomingFragment upcomingFragment = new NewUpcomingFragment();
//		upcomingFragment.setHasOptionsMenu(false);
		fragmentTransaction.replace(R.id.content_frame,upcomingFragment);  
	    fragmentTransaction.commit(); 
	      
	    getActionBar().setDisplayHomeAsUpEnabled(true);//�Ƿ���ʾ���Ͻǵ���Ƿ��ؼ�
		setSlidingActionBarEnabled(false);//����ActionBar�Ƿ����Ų˵��ƶ� 
		
//		preferences = getSharedPreferences("BillKeeperPro", MODE_PRIVATE);	
// 		editor = preferences.edit();
// 		int times = preferences.getInt("times", 0);
// 		
// 		if(times == 0){
//
//			editor.putLong("time", System.currentTimeMillis());
//			editor.commit();
//		}
// 		
//		editor.putInt("times", times+1);
//		editor.commit();
//		rate();
		
	}
	
public void rate(){
		
		if(preferences.getBoolean("canrate", true)){
			int times = preferences.getInt("times", 0);
			long time = System.currentTimeMillis() - preferences.getLong("time", 0);
			if(times >=10 && time>= 604800000L ){

				View view = inflater.inflate(R.layout.rate, null);

				final AlertDialog mDialog = new AlertDialog.Builder(this).setTitle("Rate Bill Keeper").setView(view).create();
				mDialog.show();
				ImageView rate = (ImageView)view.findViewById(R.id.rate_rateimage);
				ImageView remind = (ImageView)view.findViewById(R.id.rate_remindimage);
				ImageView cancel = (ImageView)view.findViewById(R.id.rate_cancelimage);
				rate.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						final String appName = "com.appxy.billkeeper&hl=en";

						List<ApplicationInfo> ls= getPackageManager().getInstalledApplications(0);
						int size = ls.size();
						ApplicationInfo info = null;
						for(int i = 0; i<size; i++){

							if(ls.get(i).packageName.equals("com.android.vending")){
								info = ls.get(i);

							}
						}
						if(info!=null){
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setData(Uri.parse("market://details?id="+appName));
							intent.setPackage(info.packageName);
							startActivity(intent);
						}else{
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+appName)));
						}

						/*try{
					        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("amzn://apps/android?asin=B00GD57KUY"));
				            startActivity(intent);
					    }catch (android.content.ActivityNotFoundException anfe) {
						   Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.amazon.com/gp/mas/dl/android?asin=B00GD57KUY"));
					       startActivity(intent);
					    } */

						editor.putBoolean("canrate", false);
						editor.commit();
						if(mDialog!=null &&mDialog.isShowing()){
							mDialog.dismiss();
						}
					}

				});
				remind.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						editor.putInt("times", 0);
						editor.putLong("time", System.currentTimeMillis());
						editor.commit();
						if(mDialog!=null &&mDialog.isShowing()){
							mDialog.dismiss();
						}
					}

				});
				cancel.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						editor.putBoolean("canrate", false);
						editor.commit();
						if(mDialog!=null &&mDialog.isShowing()){
							mDialog.dismiss();
						}
					}

				});

				mDialog.setOnCancelListener(new OnCancelListener(){

					@Override
					public void onCancel(DialogInterface arg0) {
						// TODO Auto-generated method stub

						editor.putInt("times", 0);
						editor.putLong("time", System.currentTimeMillis());
						editor.commit();
					}

				});
			}
		}
	}


	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && AccountFragment.sortCheck ==1 ) {
			AccountFragment.sortCheck = 0;
			
			if (AccountFragment.item1 != null) {
				
			AccountFragment.item1.setVisible(true);
			AccountFragment.item0.setVisible(false);
			AccountFragment.c.setSortEnabled(false);
			AccountFragment.listView.setLongClickable(true);
			AccountFragment.adapter.isChecked(-1);
			AccountFragment.adapter.notifyDataSetChanged();
			
			return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if(MyApplication.isHomePress){
			MyApplication.isHomePress = false;
			
			sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);  
			isPasscode = sharedPreferences.getInt("isPasscode", 0); //���ȡ������Ĭ��ֵΪ0
			
			if(isPasscode==1){
				Intent intent = new Intent(this, Activity_HomeBack.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent ,45);
			}
		}
	}
	
	class mHomeKeyEventBroadCastReceiver extends BroadcastReceiver {

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
