package com.appxy.billkeeper.service;

import java.io.Serializable;
import java.text.SimpleDateFormat;
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

public class AlamrReceiver extends BroadcastReceiver {
	  NotificationManager mNM;
	  private  Map<String, Object> mMap ;//�???��??map???
	  
	  private  Map<String, Object> tMap = new HashMap<String, Object>();//�???��??map??? 
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
	    mNM = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
	    mMap = new HashMap<String, Object>();
	    mMap.clear();
	    mMap = (Map<String, Object>) intent.getSerializableExtra("dataMap");
	    
	    Log.v("mmes", "广�?????map"+mMap);
		String bk_accountName = (String) mMap.get("bk_accountName");
		long bk_billDuedate = (Long) mMap.get("nbk_billDuedate");
		double billamount = (Double) mMap.get("nbillamount");
		int bk_billAmountUnknown =  (Integer) mMap.get("nbk_billAmountUnknown");
		int bk_categoryIconName =  (Integer) mMap.get("bk_categoryIconName");
		 
		showNotification(context,bk_accountName,bk_billDuedate,billamount,bk_billAmountUnknown,bk_categoryIconName,mMap);
	        // ??�示???示信???
	}

	public String getMilltoDate(long milliSeconds) {//�?�?�?�?????????��????��?????年�?????
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}
	
	 private void showNotification(Context context,String aName,long dueDate,double amount, int unKonwn,int cIcon,Map<String, Object> tMap) {
	        // In this sample, we'll use the same text for the ticker and the expanded notification
	        CharSequence text = aName;
	        String subTitleString ="Due on "+getMilltoDate(dueDate);
	        String currencyString = "";
	        if (unKonwn  == 1) {
	        	currencyString = " N/A";
			} else {
				currencyString ="   "+Common.CURRENCY_SIGN[Common.CURRENCY]+Common.doublepoint2str(amount);
			}
	        // Set the icon, scrolling text and timestamp
//	        Notification notification = new Notification(R.drawable.bill_24_24, text,System.currentTimeMillis());
//	        notification.defaults = Notification.DEFAULT_VIBRATE; 
	        
	         NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
	         builder.setContentTitle(text);
	         builder.setContentText(subTitleString+currencyString);
	         builder.setSmallIcon(R.drawable.bill_small);
	         builder.setDefaults(Notification.DEFAULT_VIBRATE);
	         Bitmap bm = BitmapFactory.decodeResource(context.getResources(), Common.CATEGORYICON[cIcon]);
//	         Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.m3);
	         builder.setLargeIcon(bm);  
	         builder.setAutoCancel(true);
	         
//	         RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.remote_view); 
//	         views.setImageViewResource(R.id.notificationIcon, R.drawable.logo_72_72); 
//	         builder.setContent(views);
	            
	            Intent intent = new Intent(context, PaymentActivity.class);
	            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
	            // 添�???????��?????
                stackBuilder.addParentStack(PaymentActivity.class);
                Log.v("mmes", "??????�???��??"+tMap);
	            intent.putExtra("dataMap",(Serializable)tMap);
//		        intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
//		        intent.setAction(Intent.ACTION_MAIN);
//		        intent.addCategory(Intent.CATEGORY_LAUNCHER);
		        
		        // 添�??Intent??��??�?
                stackBuilder.addNextIntent(intent);
                // ??��??�?�?PendingIntent????????�个?????��????? containing the entire back stack
                PendingIntent contentIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
		        
//	            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
	            builder.setContentIntent(contentIntent);
	            
	            
//	        nm.notify("direct_tag", 1, builder.build());
	         
//	        Intent intent = new Intent(context, Activity_Start.class);
//	        intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
//	        intent.setAction(Intent.ACTION_MAIN);
//	        intent.addCategory(Intent.CATEGORY_LAUNCHER);
	        
	        // The PendingIntent to launch our activity if the user selects this notification
//	        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
	        
	        // Set the info for the views that show in the notification panel.
//	        notification.setLatestEventInfo(context, text+currencyString ,subTitleString, contentIntent);
//	        
//	        notification.flags =Notification.FLAG_AUTO_CANCEL;
	        
	        // Send the notification.
	        // We use a layout id because it is a unique number.  We use it later to cancel.
	        int uniqueRequestCode = (int)System.currentTimeMillis(); 
	        mNM.notify(uniqueRequestCode, builder.build());
	        
//	        Toast.makeText(this, "?????????id�?"+a, Toast.LENGTH_SHORT).show();
	    }
}
