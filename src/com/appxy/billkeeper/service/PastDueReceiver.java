package com.appxy.billkeeper.service;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.appxy.billkeeper.Activity_Start;
import com.appxy.billkeeper.R;
import com.appxy.billkeeper.activity.PaymentActivity;
import com.appxy.billkeeper.activity.SlidingMenuActivity;
import com.appxy.billkeeper.entity.Category;
import com.appxy.billkeeper.entity.Common;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class PastDueReceiver extends BroadcastReceiver {
	  NotificationManager mNM;
	  public ArrayList<String> accountArray;
	  
	  private  Map<String, Object> tMap = new HashMap<String, Object>();//�����mapֵ 
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
	    mNM = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
	    accountArray = new ArrayList<String>();
	    accountArray.clear();
	    accountArray = (ArrayList<String>) intent.getSerializableExtra("dataList");
	    
		showNotification(context,accountArray);
	    // ��ʾ��ʾ��Ϣ
	}

	public String getMilltoDate(long milliSeconds) {//������ת���ɹ̶���ʽ��������
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}
	
	 private void showNotification(Context context,ArrayList<String> accountArray) {
	        // In this sample, we'll use the same text for the ticker and the expanded notification
	        CharSequence text = "Past Due Bills";
	        
	        String subTitleString ="";
	        for (String mString: accountArray) {
	        	subTitleString =subTitleString+ mString+" ,";
			}
//	        if (subTitleString.length()>35) {
//	        	 subTitleString = subTitleString.substring(0, 35)+"...";
//			}
	       
	        // Set the icon, scrolling text and timestamp
//	        Notification notification = new Notification(R.drawable.bill_24_24, text,System.currentTimeMillis());
//	        notification.defaults = Notification.DEFAULT_VIBRATE; 
	        
	         NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
	         builder.setContentTitle(text);
	         builder.setContentText(subTitleString);
	         builder.setSmallIcon(R.drawable.bill_small);
	         builder.setDefaults(Notification.DEFAULT_VIBRATE);
	         builder.setNumber(accountArray.size());
	         Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
	         builder.setLargeIcon(bm);  
	         builder.setAutoCancel(true);
	                 
//	            Intent intent = new Intent(context, PaymentActivity.class);
//	            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//	            // ��Ӻ�̨��ջ
//                  stackBuilder.addParentStack(PaymentActivity.class);
//	                intent.putExtra("dataMap",(Serializable)tMap);
////		        intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
////		        intent.setAction(Intent.ACTION_MAIN);
////		        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//		        
//		        // ���Intent��ջ��
//                stackBuilder.addNextIntent(intent);
//                // ���һ��PendingIntent�������̨��ջ containing the entire back stack
//                PendingIntent contentIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
//		        
////	            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
//	            builder.setContentIntent(contentIntent);
	            
//	        nm.notify("direct_tag", 1, builder.build());
	         
	        Intent intent = new Intent(context, Activity_Start.class);
	        intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
	        intent.setAction(Intent.ACTION_MAIN);
	        intent.addCategory(Intent.CATEGORY_LAUNCHER);
	        
	        // The PendingIntent to launch our activity if the user selects this notification
	        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
	        builder.setContentIntent(contentIntent);
	        
	        // Set the info for the views that show in the notification panel.
//	        notification.setLatestEventInfo(context, text+currencyString ,subTitleString, contentIntent);
//	        
//	        notification.flags =Notification.FLAG_AUTO_CANCEL;
	        
	        // Send the notification.
	        // We use a layout id because it is a unique number.  We use it later to cancel.
	        int uniqueRequestCode = (int)System.currentTimeMillis(); 
	        mNM.notify(0, builder.build());
	        
//	        Toast.makeText(this, "���ѵ�id��"+a, Toast.LENGTH_SHORT).show();
	    }
}
