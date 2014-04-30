package com.appxy.billkeeper;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.appxy.billkeeper.activity.SlidingMenuActivity;
import com.appxy.billkeeper.service.BillNotificationService;
import com.appxy.billkeeper.service.BillPastDueService;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public class Activity_Start extends Activity {
	SharedPreferences sharedPreferences;
	public static final String PREFS_NAME = "SAVE_INFO";
	private int isPasscode;
	
	private PendingIntent mAlarmSender;
	private PendingIntent pAlarmSender;
	private static final long days31 = 31*24*3600*1000L;
	private static final long days = 24*3600*1000L;
	private static final long nineHours = 9*3600*1000L;
	
	private AlarmManager am;
	private AlarmManager pm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		 getActionBar().setTitle("Bill Keeper");
		 SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);  
         isPasscode = sharedPreferences.getInt("isPasscode", 0); //���ȡ������Ĭ��ֵΪ1  
        
         if (isPasscode == 1) {
 			Intent intent = new Intent(this, Activity_Login.class);
 			startActivity(intent);
 			this.finish();
 		} else {
 			Intent intent = new Intent(this, SlidingMenuActivity.class);
 			startActivity(intent);
 			this.finish();
 		}
         
         if (PendingIntent.getService(Activity_Start.this, 0, new Intent(Activity_Start.this, BillNotificationService.class), PendingIntent.FLAG_NO_CREATE) !=null) {
 			Log.v("mtake", "�����ѱ�����");
 		} else {
 			 mAlarmSender = PendingIntent.getService(Activity_Start.this, 0, new Intent(Activity_Start.this, BillNotificationService.class), PendingIntent.FLAG_UPDATE_CURRENT);
 	         long firstTime = SystemClock.elapsedRealtime();
 	         //   Schedule the alarm!
 	         am = (AlarmManager)getSystemService(ALARM_SERVICE);//�ж�����ִ��һ��
 	         am.set(AlarmManager.RTC_WAKEUP,firstTime, mAlarmSender);
 	         am.setRepeating(AlarmManager.RTC_WAKEUP, getZeroTime(), days, mAlarmSender);
 		}
         
         if (PendingIntent.getService(Activity_Start.this, 1, new Intent(Activity_Start.this, BillPastDueService.class), PendingIntent.FLAG_NO_CREATE) !=null) {
  			Log.v("mtake", "past�ѱ�����");
  		} else {
  			 pAlarmSender = PendingIntent.getService(Activity_Start.this, 1, new Intent(Activity_Start.this, BillPastDueService.class), PendingIntent.FLAG_UPDATE_CURRENT);
  	         long firstTime = SystemClock.elapsedRealtime();
  	         //   Schedule the alarm!
  	         pm = (AlarmManager)getSystemService(ALARM_SERVICE);//�ж�����ִ��һ��
  	         pm.set(AlarmManager.RTC_WAKEUP,firstTime, pAlarmSender);
  	         pm.setRepeating(AlarmManager.RTC_WAKEUP, getNineTime(), days, pAlarmSender);
  		}
      
		
	}
	
     public String getMilltoDate(long milliSeconds) {//������ת���ɹ̶���ʽ��������
			SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(milliSeconds);
			return formatter.format(calendar.getTime());
		}
	  
	  public long  getNineTime() { //ÿ��ŵ㿪ʼ
			 Date date1=new Date(); 
			 SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		     String nowTime = formatter.format(date1);
		     Calendar c = Calendar.getInstance();
		     c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH)+1);
		     try {
					c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(nowTime));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		     long nowMillis = c.getTimeInMillis()+nineHours; //��ȡ���������ն�Ӧ�ĺ������ȥʱ���룬�������
		     return nowMillis;
}
	
	public long  getZeroTime() {
		 Date date1=new Date(); 
		 SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
	     String nowTime = formatter.format(date1);
	     Calendar c = Calendar.getInstance();
	     c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH)+1);
	     try {
				c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(nowTime));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	     long nowMillis = c.getTimeInMillis()+days; //��ȡ���������ն�Ӧ�ĺ������ȥʱ���룬�������
	     return nowMillis;
	}

}
