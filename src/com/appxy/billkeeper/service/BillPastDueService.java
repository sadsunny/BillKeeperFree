package com.appxy.billkeeper.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appxy.billkeeper.Activity_Start;
import com.appxy.billkeeper.R;
import com.appxy.billkeeper.db.BillKeeperSql;
import com.appxy.billkeeper.entity.Common;
import com.appxy.billkeeper.entity.RecurringEvent;
import com.appxy.billkeeper.entity.RecurringEventPastDue;
import com.appxy.billkeeper.entity.RecurringEventService;

import android.R.integer;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public class BillPastDueService extends Service {

	NotificationManager mNM;
	int uid = 0;
	private BillKeeperSql bksql;
	private List<Map<String, Object>> mDataList;
	private final static long days30 = 31 * 24 * 3600 * 1000L;
	private final static long day = 24 * 3600 * 1000L;
	private AlarmManager am;
	private PendingIntent pi;
	private ArrayList<PendingIntent> intentArray;
	int uniqueCode = 0;
	public ArrayList<String> accountArray;
	
	@Override
	public void onCreate() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mDataList = new ArrayList<Map<String, Object>>();
		intentArray = new ArrayList<PendingIntent>();
		accountArray = new ArrayList<String>();
	}

	@Override
	public void onStart(Intent intent, int startId) { // ��ʼ��ѯ��ݿ⣬����������alarm
		// TODO Auto-generated method stub
		super.onStart(intent, startId);

		// Start up the thread running the service. Note that we create a
		// separate thread because the service normally runs in the process's
		// main thread, which we don't want to block.
		if (intentArray.size() > 0) {
			Log.v("mdb", "intentArray��С��" + intentArray.size());

			for (int i = 0; i < intentArray.size(); i++) {
				am.cancel(intentArray.get(i));
			}
			intentArray.clear();
		}

		Thread thr = new Thread(null, mTask, "mPastDueService_Service");
		thr.start();
		Log.v("mmes","�Ƿ�ִ�и÷���" );
		
//		 Toast.makeText(this, "pastdueservice��ʼִ��", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDestroy() {
		// Cancel the notification -- we use the same ID that we had used to
		// start it
		// mNM.cancel(1212);
		// Toast.makeText(this, "service�Ѿ���kill", Toast.LENGTH_SHORT).show();
		// Tell the user we stopped.
	}

	/**
	 * The function that runs in our worker thread
	 */
	
	Runnable mTask = new Runnable() {
		public void run() {

			// Log.v("mdb", "intentArray.size()��"+intentArray.size());
			// show the icon in the status bar
			getData(0, getNowexactMillis());

			for (Map<String, Object> iMap : mDataList) {
				
//				long bk_billDuedate = (Long) iMap.get("nbk_billDuedate"); // �൱���¼��Ŀ�ʼ����
//				int bk_billisReminder = (Integer) iMap.get("nbk_billisReminder");
//				long bk_billReminderDate = (Long) iMap.get("nbk_billReminderDate");
//				long bk_billReminderTime = (Long) iMap.get("bk_billReminderTime");
//				int bk_billAmountUnknown = (Integer) iMap.get("nbk_billAmountUnknown");
//				double billamount = (Double) iMap.get("nbillamount");
				
				String bk_accountName = (String) iMap.get("bk_accountName");
				int payState = (Integer) iMap.get("payState");

				if (payState == -1 ) {//ɸѡ����bill
					accountArray.add(bk_accountName);
				}
				
			}
			
			if (accountArray.size() > 0) {

				Intent intent = new Intent(BillPastDueService.this,PastDueReceiver.class);
				intent.putExtra("dataList", (Serializable)accountArray);

				am = (AlarmManager) getSystemService(ALARM_SERVICE);
				pi = PendingIntent.getBroadcast(BillPastDueService.this, uniqueCode, intent,PendingIntent.FLAG_CANCEL_CURRENT);
				intentArray.add(pi);
				am.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), pi);
				Log.v("mmes","�Ƿ�ִ�и÷���" );
			}
			
			BillPastDueService.this.stopSelf();
		}
	};

	public String getMilltoDate(long milliSeconds) {// ������ת���ɹ̶���ʽ��������
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	/**
	 * Show a notification while this service is running.
	 */
	private void showNotification(String mesage) {
		// In this sample, we'll use the same text for the ticker and the
		// expanded notification
		CharSequence text = "bill�����¼�";

		// Set the icon, scrolling text and timestamp
		Notification notification = new Notification(R.drawable.logo,
				text, System.currentTimeMillis());

		// The PendingIntent to launch our activity if the user selects this
		// notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, Activity_Start.class), 0);

		// Set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(this, "bill����", text, contentIntent);

		// Send the notification.
		// We use a layout id because it is a unique number. We use it later to
		// cancel.
		int uniqueRequestCode = (int) System.currentTimeMillis();
		mNM.notify(uniqueRequestCode, notification);
		// Toast.makeText(this, "���ѵ�id��"+a, Toast.LENGTH_SHORT).show();
	}

	/**
	 * This is the object that receives interactions from clients. See
	 * RemoteService for a more complete example.
	 */
	private final IBinder mBinder = new Binder() {
		@Override
		protected boolean onTransact(int code, Parcel data, Parcel reply,
				int flags) throws RemoteException {
			return super.onTransact(code, data, reply, flags);
		}
	};

	public long reminderDate(long dueDate, long before, long remindAt) { // ��������ʱ��
		long reminder = dueDate - before * day + remindAt;
		return reminder;
	}

	public long getNowMillis() { //�õ�����ĺ�����
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
	     long nowMillis = c.getTimeInMillis(); //��ȡ���������ն�Ӧ�ĺ������ȥʱ���룬�������
	     
	     return nowMillis;
	}
	
	public long getNowexactMillis() { // ������ڵľ�ȷʱ��

		Calendar c = Calendar.getInstance();
		long nowMillis = c.getTimeInMillis(); // ��ȡ���������ն�Ӧ�ĺ������ȥʱ���룬�������
		return nowMillis;
	}

	public List<Map<String, Object>> getData(long firstDayOfMonth,
			long lastDayOfMonth) {

		mDataList.clear();
		bksql = new BillKeeperSql(this);
		SQLiteDatabase db = bksql.getReadableDatabase();
		Map<String, Object> map;
		
		String sql = "select BK_Bill.* ,BK_Account.bk_accountName ,BK_Account.accounthasCategory  ,BK_Category.bk_categoryIconName ,BK_Category.bk_categoryName from BK_Bill,BK_Account,BK_Category where BK_Bill.billhasAccount = BK_Account._id and BK_Category._id = BK_Account.accounthasCategory and BK_Bill.bk_billisRepeat != 1 and BK_Bill.bk_billDuedate >="
				+ firstDayOfMonth
				+ " and BK_Bill.bk_billDuedate <= "
				+ lastDayOfMonth;

		Cursor cursorEA = db.rawQuery(sql, null);
		while (cursorEA.moveToNext()) {
			map = new HashMap<String, Object>();

			int BK_Bill_Id = cursorEA.getInt(0);
			double billamount = cursorEA.getDouble(2);
			int bk_billAmountUnknown = cursorEA.getInt(3);
			int bk_billAutoPay = cursorEA.getInt(4);
			long bk_billDuedate = cursorEA.getLong(8);
			long bk_billEndDate = cursorEA.getLong(9);
			int bk_billisReminder = cursorEA.getInt(10);
			int bk_billisRepeat = cursorEA.getInt(11);
			int bk_billisVariaable = cursorEA.getInt(12);
			long bk_billReminderDate = cursorEA.getLong(14);
			long bk_billReminderTime = cursorEA.getLong(15);
			int bk_billRepeatNumber = cursorEA.getInt(16);
			int bk_billRepeatType = cursorEA.getInt(17);
			int billhasAccount = cursorEA.getInt(24);
			String bk_accountName = cursorEA.getString(25);
			int accounthasCategory = cursorEA.getInt(26);
			int bk_categoryIconName = cursorEA.getInt(27);
			String bk_categoryName = cursorEA.getString(28);

			map.put("BK_Bill_Id", BK_Bill_Id);
			map.put("nbillamount", billamount);
			map.put("nbk_billAmountUnknown", bk_billAmountUnknown);
			map.put("nbk_billAutoPay", bk_billAutoPay);
			map.put("nbk_billDuedate", bk_billDuedate);
			map.put("nbk_billEndDate", bk_billEndDate);
			map.put("nbk_billisReminder", bk_billisReminder);
			map.put("nbk_billisRepeat", bk_billisRepeat);
			map.put("bk_billisVariaable", bk_billisVariaable);
			map.put("nbk_billReminderDate", bk_billReminderDate);
			map.put("bk_billReminderTime", bk_billReminderTime);
			map.put("nbk_billRepeatNumber", bk_billRepeatNumber);
			map.put("nbk_billRepeatType", bk_billRepeatType);
			map.put("billhasAccount", billhasAccount);
			map.put("bk_accountName", bk_accountName);
			map.put("accounthasCategory", accounthasCategory);
			map.put("bk_categoryIconName", bk_categoryIconName);
			map.put("bk_categoryName", bk_categoryName);

			map.put("indexflag", 0); // �����¼��ı�־��0�������ı�־��1��2���ظ����¼���3���ظ������¼�
			map.put("payState", -2);

			mDataList.add(map);

		}
		
		Log.v("mcheck", "����mDataList�Ĵ�С��"+mDataList.size());
		cursorEA.close();
		db.close();
		RecurringEventPastDue mRecurringEventPastDue = new RecurringEventPastDue();
		mDataList.addAll(mRecurringEventPastDue.recurringData(this, firstDayOfMonth,lastDayOfMonth)); // ���ѭ��ʱ���¼�
		judgePayment(mDataList);
		
		Log.v("mcheck", "�����ظ�mDataList�Ĵ�С��"+mDataList.size());
		
		return mDataList;
	}

	private void judgePayment(List<Map<String, Object>> dataList) { // ʹ�ø÷����ж�pay��״̬
		
		bksql = new BillKeeperSql(this);
		SQLiteDatabase db = bksql.getReadableDatabase();
		long nowMillis = getNowMillis();

		for (Map<String, Object> bMap : dataList) {

			int flag = (Integer) bMap.get("indexflag");
			int id = (Integer) bMap.get("BK_Bill_Id");
			double billamount = (Double) bMap.get("nbillamount");
			long billduedate = (Long) bMap.get("nbk_billDuedate");
			int billAutoPay = (Integer) bMap.get("nbk_billAutoPay");
			int nbk_billAmountUnknown = (Integer) bMap
					.get("nbk_billAmountUnknown");
			bMap.put("remain", 0); // ʣ�µ����
			if (flag == 0 || flag == 1) {

				String sql = "select BK_Payment._id, BK_Payment.bk_payAmount, BK_Payment.bk_payMode ,BK_Payment.paymenthasBill from BK_Payment where BK_Payment.paymenthasBill = "
						+ id;
				List<Map<String, Object>> pDataList = new ArrayList<Map<String, Object>>();
				double zero = 0.00f;
				BigDecimal b1 = new BigDecimal(Double.toString(zero)); // ������㾫��
				int checkPayMode = 0;// �жϸ�pay�Ƿ��й�quickpay��mark,�й�һ�α�ʾ��bill�Ѿ�pay�꣬mark
										// =1 quick =2;
				int curSize;
				Cursor cursor1 = db.rawQuery(sql, null);
				curSize = cursor1.getCount();
				// Log.v("mtest", "pangmeny��С"+curSize);
				while (cursor1.moveToNext()) {
					
					int paymentZ_PK = cursor1.getInt(0);
					double paymentAmount = cursor1.getDouble(1);
					int paymentMode = cursor1.getInt(2);
					int paymenthasBill = cursor1.getInt(3);
					// Log.v("mtest", "paymentAmount��ʷ"+paymentAmount);

					BigDecimal b2 = new BigDecimal(
							Double.toString(paymentAmount));
					b1 = b1.add(b2);
					if (paymentMode == -1) { // mark
						checkPayMode = 1;
					}
				}
				cursor1.close();

				double totalPayAmount = b1.doubleValue(); // �ܹ�֧���ܶ�
				double remain = 0.0;// ʣ�µ�

				if (nbk_billAmountUnknown == 1) {

					if (curSize > 0) {
						bMap.put("payState", 1);
						bMap.put("remain", 0);
					} else {

						if (billduedate < nowMillis) {
							bMap.put("payState", -1);
						} else {
							bMap.put("payState", 0);
						}
						bMap.put("remain", 0);
					}

				} else if (checkPayMode == 1) {

					bMap.put("payState", 1);
					bMap.put("remain", 0);

				} else {

					BigDecimal mb1 = new BigDecimal(Double.toString(billamount)); // ������㾫��
					BigDecimal mb2 = new BigDecimal(
							Double.toString(totalPayAmount)); // ������㾫��
					remain = (mb1.subtract(mb2)).doubleValue();
					// Log.v("mtest", "billamount"+billamount);
					// Log.v("mtest", "totalPayAmount����"+totalPayAmount);
					// Log.v("mtest", "remain����"+remain);

					if (remain <= 0) {
						bMap.put("payState", 1);
						bMap.put("remain", 0);
					} else {

						if (billduedate < nowMillis) {
							bMap.put("payState", -1);
						} else {
							bMap.put("payState", 0);
						}
						bMap.put("remain", remain);

					}
				}
				int mpayState = (Integer) bMap.get("payState");

				if (mpayState == -2) {

					if (billduedate < nowMillis) {
						bMap.put("payState", -1);
					} else {
						bMap.put("payState", 0);
					}
					bMap.put("remain", billamount);

				}

				if ((billAutoPay == 1 && billduedate < (nowMillis + 86399) && remain > 0)
						|| (billAutoPay == 1
								&& billduedate < (nowMillis + 86399)
								&& nbk_billAmountUnknown == 1 && curSize == 0)) { // auto��bill����duedateС�ڵ����ʱ�򣬻���unkonwn
																					// �Զ�pay
					long key;
					// Log.v("mtest", "remainʣ��"+remain);
					if (nbk_billAmountUnknown == 1) {
						key = billPay(0.00, 2, billduedate, id);
					} else {
						key = billPay(remain, 2, billduedate, id);
					}
					bMap.put("payState", 1);
					bMap.put("remain", 0);
				}

			} else if (flag == 2) {

				if (billduedate < nowMillis) {
					bMap.put("payState", -1);
					bMap.put("remain", billamount);
				} else {
					bMap.put("payState", 0);
					bMap.put("remain", billamount);
				}

				if (billAutoPay == 1 && billduedate < (nowMillis + 86399)) { // �ظ������¼����ڴ���

					bMap.put("payState", 1);

					int obillObjecthasBill = (Integer) bMap.get("BK_Bill_Id");
					double obillamount = (Double) bMap.get("nbillamount");
					int obk_billAmountUnknown = (Integer) bMap
							.get("nbk_billAmountUnknown");
					int obk_billAutoPay = (Integer) bMap.get("nbk_billAutoPay");
					long obk_billDuedate = (Long) bMap.get("nbk_billDuedate");
					long obk_billDuedateNew = 0;
					long obk_billEndDate = (Long) bMap.get("nbk_billEndDate");
					int obk_billisReminder = (Integer) bMap
							.get("nbk_billisReminder");
					int obk_billisRepeat = (Integer) bMap
							.get("nbk_billisRepeat");
					int obk_billisVariaable = (Integer) bMap
							.get("bk_billisVariaable");
					long obk_billReminderDate = (Long) bMap
							.get("nbk_billReminderDate");
					long bk_billReminderTime = (Long) bMap
							.get("bk_billReminderTime");
					int obk_billRepeatNumber = (Integer) bMap
							.get("nbk_billRepeatNumber");
					int obk_billRepeatType = (Integer) bMap
							.get("nbk_billRepeatType");
					int billhasAccount = (Integer) bMap.get("billhasAccount");

					long Okey = insertObject(
							0,
							obillamount,
							obk_billAmountUnknown,
							obk_billAutoPay,
							obk_billDuedate,
							obk_billDuedateNew, // ���������¼�
							obk_billEndDate, obk_billisReminder,
							obk_billisRepeat, obk_billisVariaable,
							obk_billReminderDate, bk_billReminderTime,
							obk_billRepeatNumber, obk_billRepeatType,
							obillObjecthasBill, billhasAccount);
					if (obk_billAmountUnknown == 1) {
						billPayO(0.0, 2, obk_billDuedate, Okey);
					} else {
						billPayO(obillamount, 2, obk_billDuedate, Okey);
					}
					int key = (int) Okey;

					bMap.put("remain", 0);
					bMap.put("indexflag", 3);
					bMap.put("BK_Bill_Id", key);
				}

			} else if (flag == 3) {

				String sql = "select BK_Payment._id, BK_Payment.bk_payAmount, BK_Payment.bk_payMode ,BK_Payment.paymenthasBill from BK_Payment ,BK_BillObject where BK_Payment.paymenthasBillO = "
						+ id;

				List<Map<String, Object>> pDataList = new ArrayList<Map<String, Object>>();
				Map<String, Object> map1;

				double zero = 0.00f;
				BigDecimal b1 = new BigDecimal(Double.toString(zero)); // ������㾫��
				int checkPayMode = 0;// �жϸ�pay�Ƿ��й�quickpay��mark,�й�һ�α�ʾ��bill�Ѿ�pay�꣬mark
										// =1 quick =2;
				int curSize;
				Cursor cursor1 = db.rawQuery(sql, null);
				curSize = cursor1.getCount();
				while (cursor1.moveToNext()) {

					String paymentZ_PK = cursor1.getString(0);
					double paymentAmount = cursor1.getDouble(1);
					int paymentMode = cursor1.getInt(2);
					int paymenthasBill = cursor1.getInt(3);

					BigDecimal b2 = new BigDecimal(
							Double.toString(paymentAmount));
					b1 = b1.add(b2);
					if (paymentMode == -1) { // mark
						checkPayMode = 1;
					}

				}
				cursor1.close();

				double totalPayAmount = b1.doubleValue(); // �ܹ�֧���ܶ�
				double remain = 0.0;// ʣ�µ�

				if (nbk_billAmountUnknown == 1) {

					if (curSize > 0) {
						bMap.put("payState", 1);
						bMap.put("remain", 0);
					} else {
						if (billduedate < nowMillis) {
							bMap.put("payState", -1);
						} else {
							bMap.put("payState", 0);
						}
						bMap.put("remain", 0);
					}

				} else if (checkPayMode == 1) {

					bMap.put("payState", 1);
					bMap.put("remain", 0);

				} else {

					BigDecimal mb1 = new BigDecimal(Double.toString(billamount)); // ������㾫��
					BigDecimal mb2 = new BigDecimal(
							Double.toString(totalPayAmount)); // ������㾫��
					remain = (mb1.subtract(mb2)).doubleValue();

					if (remain <= 0) {
						bMap.put("payState", 1);
						bMap.put("remain", 0);
					} else {

						if (billduedate < nowMillis) {
							bMap.put("payState", -1);
						} else {
							bMap.put("payState", 0);
						}
						bMap.put("remain", remain);

					}
				}
				int mpayState = (Integer) bMap.get("payState");

				if (mpayState == -2) {

					if (billduedate < nowMillis) {
						bMap.put("payState", -1);
					} else {
						bMap.put("payState", 0);
					}
					bMap.put("remain", billamount);

				}

				if ((billAutoPay == 1 && billduedate < (nowMillis + 86399) && remain > 0)
						|| (billAutoPay == 1
								&& billduedate < (nowMillis + 86399)
								&& nbk_billAmountUnknown == 1 && curSize == 0)) { // auto��bill����duedateС�ڵ����ʱ�򣬻���unkonwn
																					// �Զ�pay
					long key;
					if (nbk_billAmountUnknown == 1) {
						key = billPayO(0.00, 2, billduedate, id);
					} else {
						key = billPayO(remain, 2, billduedate, id);
					}
					bMap.put("payState", 1);
					bMap.put("remain", 0);
				}
			}

		}// ����ѭ������
		db.close();
	}

	public long billPay(double payAmount, int payMode, long payDate,
			long paymenthasBill) {

		bksql = new BillKeeperSql(this);
		SQLiteDatabase db = bksql.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("bk_payAmount", payAmount);
		values.put("bk_payMode", payMode);
		values.put("bk_payDate", payDate);
		values.put("paymenthasBill", paymenthasBill);

		db.execSQL("PRAGMA foreign_keys = ON ");
		long pkey = db.insert("BK_Payment", null, values);
		db.close();
		return pkey;

	}

	public long billPayO(double payAmount, int payMode, long payDate,
			long paymenthasBill) {

		bksql = new BillKeeperSql(this);
		SQLiteDatabase db = bksql.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("bk_payAmount", payAmount);
		values.put("bk_payMode", payMode);
		values.put("bk_payDate", payDate);
		values.put("paymenthasBillO", paymenthasBill);

		db.execSQL("PRAGMA foreign_keys = ON ");
		long pkey = db.insert("BK_Payment", null, values);
		db.close();
		return pkey;

	}

	public long insertObject(int bk_billsDelete,
			double billamount,
			int nbk_billAmountUnknown,
			int bk_billAutoPay,
			long bk_billDuedate,
			long bk_billDuedateNew, // ���������¼�
			long bk_billEndDate, int bk_billisReminder, int bk_billisRepeat,
			int bk_billisVariaable, long bk_billReminderDate,
			long bk_billReminderTime, int bk_billRepeatNumber,
			int bk_billRepeatType, long billObjecthasBill, int billhasAccount) {

		bksql = new BillKeeperSql(this);
		SQLiteDatabase db = bksql.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("bk_billsDelete", bk_billsDelete);
		values.put("bk_billOAmount", billamount);
		values.put("bk_billOAmountUnknown", nbk_billAmountUnknown);
		values.put("bk_billOAutoPay", bk_billAutoPay);
		values.put("bk_billODueDate", bk_billDuedate);
		values.put("bk_billODueDateNew", bk_billDuedateNew);
		values.put("bk_billOEndDate", bk_billEndDate);
		values.put("bk_billOisReminder", bk_billisReminder);
		values.put("bk_billOisRepeat", bk_billisRepeat);
		values.put("bk_billOisVariaable", bk_billisVariaable);
		values.put("bk_billOReminderDate", bk_billReminderDate);
		values.put("bk_billOReminderTime", bk_billReminderTime);
		values.put("bk_billORepeatNumber", bk_billRepeatNumber);
		values.put("bk_billORepeatType", bk_billRepeatType);
		values.put("billObjecthasBill", billObjecthasBill);
		values.put("billObjecthasAccount", billhasAccount);

		db.execSQL("PRAGMA foreign_keys = ON ");
		long okey = db.insert("BK_BillObject", null, values);
		db.close();
		return okey;
	}

}
