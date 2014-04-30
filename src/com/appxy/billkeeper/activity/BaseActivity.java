package com.appxy.billkeeper.activity;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.appxy.billkeeper.BaseHomeActivity;
import com.appxy.billkeeper.R;
import com.appxy.billkeeper.fragment.AccountFragment;

import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.OnCloseListener;
import com.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.slidingmenu.lib.app.SlidingActivity;

import android.R.integer;
import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.TextView;
import android.widget.Toast;

public class BaseActivity extends SlidingActivity {

	private int mTitleRes;
	SlidingMenu sm; 
	
	private MenuItem serchItem;
	private MenuItem addBillItem;
	private MenuItem settingItem;
	private ActionBar mActionBar;
	
	public static int  STATE = 0;
	public static int STATE0 = 0;
	public static int STATE1 = 0;
	public static int STATE2 = 0;
	public static int STATE3 = 0;
	
	public BaseActivity (int titleRes) {
		mTitleRes = titleRes ;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle(mTitleRes);
		mActionBar = getActionBar();
		
//		getSupportActionBar().setDisplayShowHomeEnabled(false);
		
//		 mActionBar.setBackgroundDrawable(new ColorDrawable(Color.argb(255, 87, 192, 252)));
//		 int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
//		 TextView title = (TextView) findViewById(titleId);
//		 title.setTextColor(this.getResources().getColor(R.color.white));
		 
		// set the Behind View
        setBehindContentView(R.layout.menu_frame);
        android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        LeftFragment lf = new LeftFragment();
        fragmentTransaction.replace(R.id.menu_frame, lf);
        fragmentTransaction.commit();
        
		//�Զ���໬�˵�
        sm = getSlidingMenu();
		sm.setMode(SlidingMenu.LEFT);                      //�������󻬻����һ����������Ҷ����Ի�
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset); //���ò˵�ռ��Ļ�ı���
		sm.setFadeDegree(0.0f);		 //���õ��뵭���ı���
		sm.setBehindScrollScale(0);	 //���û���ʱ��קЧ��,����Ч��
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);//����Ҫʹ�˵�������������Ļ�ķ�Χ
		
		sm.setOnCloseListener(new OnCloseListener() {

			@Override
			public void onClose() {
				// TODO Auto-generated method stub
				if (sm.isMenuShowing()) {
					
				} else {
					getSupportActionBar().setDisplayHomeAsUpEnabled(true);//�Ƿ���ʾ���Ͻǵ���Ƿ��ؼ�
					
//					getSupportActionBar().setDisplayShowHomeEnabled(false);
//					getSupportActionBar().setDisplayUseLogoEnabled(true);
					
					if (serchItem != null && settingItem != null && addBillItem != null) {
					serchItem.setVisible(false);
					settingItem.setVisible(false);
					
					if (STATE==1 || STATE==2) {
						getActionBar().setDisplayShowTitleEnabled(false);
						mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
						if (STATE==2) {
							addBillItem.setVisible(false);
						} else {
							addBillItem.setVisible(true);
						}
					}else if (STATE==3 || STATE==0) {
						getActionBar().setDisplayShowTitleEnabled(true);
						mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
						setTitle("Upcoming Bills");
						addBillItem.setVisible(true);
						
					} else if (STATE==4) {
						getActionBar().setDisplayShowTitleEnabled(true);
						mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
						setTitle("Account");
						addBillItem.setVisible(true);
						
						if (AccountFragment.item0 != null && AccountFragment.sortCheck == 1) {
							
							    addBillItem.setVisible(false);
							    AccountFragment.item0.setVisible(true);
								AccountFragment.item1.setVisible(false);
								
//							AccountFragment.item0.setVisible(false);
//							AccountFragment.c.setSortEnabled(false);
//							AccountFragment.listView.setLongClickable(true);
//							AccountFragment.adapter.isChecked(1);
//							AccountFragment.adapter.notifyDataSetChanged();
						}
					}
					
					
//					if ( STATE==2 ) {
//						addBillItem.setVisible(false);
//					} else {
//						addBillItem.setVisible(true);
//					}
					
					
					}
				}
				
				// return false;
			}
		});
		
		sm.setOnOpenListener(new OnOpenListener() {

			@Override
			public void onOpen() {
				// TODO Auto-generated method stub
				if (sm.isMenuShowing()) {
					getSupportActionBar().setDisplayHomeAsUpEnabled(false);//�Ƿ���ʾ���Ͻǵ���Ƿ��ؼ�
//					getSupportActionBar().setDisplayShowHomeEnabled(true);
//					getSupportActionBar().setDisplayUseLogoEnabled(true);
					
					if (serchItem != null && settingItem != null && addBillItem != null) {
						serchItem.setVisible(true);
						settingItem.setVisible(true);
						addBillItem.setVisible(false);
					}
					
					if (AccountFragment.item0 != null && AccountFragment.sortCheck == 1) {
						
						AccountFragment.item0.setVisible(false);
						AccountFragment.item1.setVisible(false);
//						AccountFragment.c.setSortEnabled(false);
//						AccountFragment.listView.setLongClickable(true);
//						AccountFragment.adapter.isChecked(1);
//						AccountFragment.adapter.notifyDataSetChanged();
					}
					mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
					getActionBar().setDisplayShowTitleEnabled(true);
					setTitle("Bill Keeper");
				} 
			}
		});
	}
	
	
	private long exitTime = 0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(BaseActivity.this, "Press again to exit!!",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {

				// MyApplication.folder_id = 0;
				// MyApplication.mMemoryCache.evictAll();
				Intent intent = new Intent();
				intent.setAction("ExitLogin");
				this.sendBroadcast(intent);
				super.finish();
				System.exit(0);

			}
		}
		return false;
	}

	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:  //���Ͻǰ�ť���ص�id
			getSlidingMenu().showMenu();
//			Intent intent = new Intent(this, HomeActivity.class);  //���ص���activity
//          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
//          startActivity(intent);  
			
			return true;
		case R.id.search:
			Intent intent = new Intent(this, SearchActivity.class);
			startActivity(intent);
			break;
			
		case R.id.setting:
			
			Intent sintent = new Intent();
			sintent.setClass(this, SettingActivity.class);
			startActivity(sintent);
			
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu ) {
		getSupportMenuInflater().inflate(R.menu.main, menu);

		serchItem = menu.findItem(R.id.search);//����actionbar�е�������ť���ɼ�
		settingItem = menu.findItem(R.id.setting);
		addBillItem = menu.findItem(R.id.addbill);
		serchItem.setVisible(false);
		settingItem.setVisible(false);
		addBillItem.setVisible(true);
		
		return super.onCreateOptionsMenu(menu);
	}

}
