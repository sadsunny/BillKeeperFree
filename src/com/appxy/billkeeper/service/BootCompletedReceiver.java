package com.appxy.billkeeper.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.appxy.billkeeper.Activity_Start;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class BootCompletedReceiver extends BroadcastReceiver {
	
	private PendingIntent mAlarmSender;
	private static long days31 = 31*24*3600*1000L;
	private static long days = 24*3600*1000L;
	private PendingIntent pAlarmSender;
	private static final long nineHours = 9*3600*1000L;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
         
         mAlarmSender = PendingIntent.getService(context, 0, new Intent(context, BillNotificationService.class), PendingIntent.FLAG_UPDATE_CURRENT);
         AlarmManager am = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
         long firstTime = SystemClock.elapsedRealtime();
         
         am.set(AlarmManager.RTC_WAKEUP,firstTime, mAlarmSender);
         am.setRepeating(AlarmManager.RTC_WAKEUP, getZeroTime(), days, mAlarmSender);
         
             AlarmManager pm = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
             pAlarmSender = PendingIntent.getService(context, 0, new Intent(context, BillPastDueService.class), PendingIntent.FLAG_UPDATE_CURRENT);
	         //   Schedule the alarm!
	         pm = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);//�ж�����ִ��һ��
	         pm.set(AlarmManager.RTC_WAKEUP,firstTime, pAlarmSender);
	         pm.setRepeating(AlarmManager.RTC_WAKEUP, getNineTime(), days, pAlarmSender);
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
