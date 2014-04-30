package com.appxy.billkeeper.activity;

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

import com.appxy.billkeeper.BaseHomeActivity;
import com.appxy.billkeeper.R;
import com.appxy.billkeeper.R.layout;
import com.appxy.billkeeper.R.menu;
import com.appxy.billkeeper.adapter.AccountDetailActivityListViewAdapter;
import com.appxy.billkeeper.db.BillKeeperSql;
import com.appxy.billkeeper.entity.Common;
import com.appxy.billkeeper.entity.RecurringEvent;
import com.appxy.billkeeper.entity.RecurringEventByAccountIdTime;
import com.appxy.billkeeper.entity.RecurringEventById;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AccountDetailActivity extends BaseHomeActivity {

	private int account_id;
	private BillKeeperSql bksql;
	private Map<String, Object> aMap;
	public Handler handler;
	private List<Map<String, Object>> billDataList;

	private ImageView mImageView;
	private TextView accountTextView;
	private TextView categoryTextView;
	private ImageButton callsupportButton;
	private ImageButton viewreportButton;
	private ImageButton viewwebButton;
	private ListView mListView;
	private AccountDetailActivityListViewAdapter ADAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_detail);
		
//		 getActionBar().setDisplayShowHomeEnabled(false);
//		 getActionBar().setDisplayShowTitleEnabled(true);
//		
//		 int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
//		 TextView title = (TextView) findViewById(titleId);
//		 title.setTextColor(this.getResources().getColor(R.color.white));

		mImageView = (ImageView) findViewById(R.id.account_category_imageView);
		accountTextView = (TextView) findViewById(R.id.account_textView);
		categoryTextView = (TextView) findViewById(R.id.category_textView);
		callsupportButton = (ImageButton) findViewById(R.id.callSupporttButton);
		viewwebButton = (ImageButton) findViewById(R.id.viewWebButton);
		viewreportButton = (ImageButton) findViewById(R.id.viewReportButton);
		mListView = (ListView) findViewById(R.id.mListview);

		Intent intent = getIntent();
		account_id = intent.getIntExtra("account_id", 1);

		callsupportButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if ((String) aMap.get("cb_accountPhoneNo") != null) {

					Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
							+ (String) aMap.get("cb_accountPhoneNo")));
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(i);
				}
			}
		});

		viewwebButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse("http://"
						+ aMap.get("bk_accountWebsite"));
				intent.setData(content_url);
				startActivity(intent);
			}
		});

		viewreportButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent();
				intent.putExtra("account_id", account_id);
				intent.putExtra("accountName",(String) aMap.get("bk_accountName"));
				intent.putExtra("categoryInt",(Integer) aMap.get("accounthasCategory"));
				intent.setClass(AccountDetailActivity.this,
						ViewReportActivity.class);
				startActivity(intent);

			}
		});
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
				if (billDataList != null) {
					Map<String, Object> mtMap = new HashMap<String, Object>();
					mtMap=billDataList.get(arg2);
					Intent intent = new Intent();
					intent.putExtra("dataMap",(Serializable)mtMap);
					intent.setClass(AccountDetailActivity.this, PaymentActivity.class);
					startActivityForResult(intent, 10);
					
				}
			}
		});

		ADAdapter = new AccountDetailActivityListViewAdapter(this);
		mListView.setAdapter(ADAdapter);

		aMap = new HashMap<String, Object>();
		billDataList = new ArrayList<Map<String, Object>>();

		handler = new Handler();
		handler.post(mUpdater);

		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	/*
	 * end
	 */
	public Runnable mUpdater = new Runnable() {

		@Override
		public void run() {
			getAccountData(account_id);
			getData(account_id);

			if (aMap != null) {

				String bk_accountName = (String) aMap.get("bk_accountName");
				String bk_categoryName = (String) aMap.get("bk_categoryName");
				int accounthasCategory = (Integer) aMap.get("accounthasCategory");
				String bk_accountWebsite = (String) aMap.get("bk_accountWebsite");
				String cb_accountPhoneNo = (String) aMap.get("cb_accountPhoneNo");

				mImageView.setImageResource(Common.CATEGORYICON[accounthasCategory]);
				accountTextView.setText(bk_accountName);
				categoryTextView.setText(bk_categoryName);

				if (cb_accountPhoneNo != null && cb_accountPhoneNo.length() != 0) {
					callsupportButton.setEnabled(true);
					callsupportButton.setBackgroundResource(R.drawable.call_support_selector);
				} else {
					callsupportButton.setEnabled(false);
					callsupportButton.setBackgroundResource(R.drawable.shape_gray);
					
				}

				if (bk_accountWebsite != null
						&& bk_accountWebsite.length() != 0) {
					viewwebButton.setEnabled(true);
					viewwebButton.setBackgroundResource(R.drawable.view_web_selector);
				} else {
					viewwebButton.setEnabled(false);
					viewwebButton.setBackgroundResource(R.drawable.internet_gray);
				}
			}
			if (billDataList != null) {
				ADAdapter.setAdapterList(billDataList);
				ADAdapter.notifyDataSetChanged();
			}

		}
	};

	public void getAccountData(int id) {
		aMap.clear();
		bksql = new BillKeeperSql(this);
		SQLiteDatabase db = bksql.getReadableDatabase();
		String sql = "select BK_Account.bk_accountName , BK_Category.bk_categoryName , BK_Category.bk_categoryIconName ,BK_Account.bk_accountNo ,BK_Account.bk_accountMemo ,BK_Account.bk_accountWebsite ,BK_Account.cb_accountPhoneNo from BK_Account, BK_Category where BK_Account._id = "
				+ id + " and BK_Account.accounthasCategory = BK_Category._id";
		Cursor cursorEA = db.rawQuery(sql, null);
		while (cursorEA.moveToNext()) {

			String bk_accountName = cursorEA.getString(0);
			String bk_categoryName = cursorEA.getString(1);
			int accounthasCategory = cursorEA.getInt(2);
			String bk_accountNo = cursorEA.getString(3);
			String bk_accountMemo = cursorEA.getString(4);
			String bk_accountWebsite = cursorEA.getString(5);
			String cb_accountPhoneNo = cursorEA.getString(6);

			aMap.put("bk_accountName", bk_accountName);
			aMap.put("bk_categoryName", bk_categoryName);
			aMap.put("accounthasCategory", accounthasCategory);
			aMap.put("bk_accountNo", bk_accountNo);
			aMap.put("bk_accountMemo", bk_accountMemo);
			aMap.put("bk_accountWebsite", bk_accountWebsite);
			aMap.put("cb_accountPhoneNo", cb_accountPhoneNo);
		}
		db.close();
	}

	public String getMilltoDate(long milliSeconds) {// ������ת���ɹ̶���ʽ��������
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

	public long todayDate() { // ��ȡ���������ն�Ӧ�ĺ������ȥ����

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		String nowTime = formatter.format(date);
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(nowTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long nowMillis = c.getTimeInMillis();
		return nowMillis;
	}

	public void getBillData(int id) {
		billDataList.clear();
		Map<String, Object> map;
		bksql = new BillKeeperSql(this);
		SQLiteDatabase db = bksql.getReadableDatabase();
		String sql = " select BK_Bill.*  from BK_Bill where BK_Bill.billhasAccount = "
				+ id + " order by BK_Bill.bk_billDuedate DESC "; // ����Ҫ�����ظ��¼�������һ�������

		Cursor cursorEA = db.rawQuery(sql, null);
		while (cursorEA.moveToNext()) {

			map = new HashMap<String, Object>();

			int nbk_id = cursorEA.getInt(0);
			double nbillamount = cursorEA.getDouble(2);
			int nbk_billAmountUnknown = cursorEA.getInt(3);
			int nbk_billAutoPay = cursorEA.getInt(4);
			long nbk_billDuedate = cursorEA.getLong(8);
			long nbk_billEndDate = cursorEA.getLong(9);
			int nbk_billisReminder = cursorEA.getInt(10);
			int nbk_billisRepeat = cursorEA.getInt(11);
			long nbk_billReminderDate = cursorEA.getLong(14);
			int nbk_billRepeatNumber = cursorEA.getInt(16);
			int nbk_billRepeatType = cursorEA.getInt(17);

			map.put("nbk_id", nbk_id);
			map.put("nbillamount", nbillamount);
			map.put("nbk_billAmountUnknown", nbk_billAmountUnknown);
			map.put("nbk_billAutoPay", nbk_billAutoPay);
			map.put("nbk_billDuedate", nbk_billDuedate);
			map.put("nbk_billEndDate", nbk_billEndDate);
			map.put("nbk_billisReminder", nbk_billisReminder);
			map.put("nbk_billisRepeat", nbk_billisRepeat);
			map.put("nbk_billReminderDate", nbk_billReminderDate);
			map.put("nbk_billRepeatNumber", nbk_billRepeatNumber);
			map.put("nbk_billRepeatType", nbk_billRepeatType);

			map.put("formateDuedate", getMilltoDate(nbk_billDuedate));

			int AUTORE = -1;
			if (nbk_billAutoPay == 1 && nbk_billisRepeat == 0) { // 0����Զ�֧����1����ظ���2���both,-1��?����
				AUTORE = 0;
			} else if (nbk_billAutoPay == 0 && nbk_billisRepeat == 1) {
				AUTORE = 1;
			} else if (nbk_billAutoPay == 1 && nbk_billisRepeat == 1) {
				AUTORE = 2;
			} else {
				AUTORE = -1;
			}
			map.put("autore", AUTORE);

			String sql1 = "select _id ,bk_payAmount ,bk_payDate ,bk_payMode from BK_Payment where BK_Payment.paymenthasBill = "
					+ nbk_id; // �ж�pay״̬

			double totalPay = 0;
			double zero = 0.00f;
			int checkPayMode = -1; // �ж�֧��״̬��1��ʾ����quickpay��mark��autopay
			int curSize = 0;
			BigDecimal b1 = new BigDecimal(Double.toString(zero)); // ������㾫��
			Cursor cursor = db.rawQuery(sql1, null);
			curSize = cursor.getCount();
			while (cursor.moveToNext()) {

				int pay_id = cursor.getInt(0);
				double bk_payAmount = cursor.getDouble(1);
				long bk_payDate = cursor.getLong(2);
				int bk_payMode = cursor.getInt(3);

				BigDecimal b2 = new BigDecimal(Double.toString(bk_payAmount));
				b1 = b1.add(b2);

				if (bk_payMode == -1 || bk_payMode == 0 || bk_payMode == 2) { // mark
					checkPayMode = 1;
				}
			}
			totalPay = b1.doubleValue(); // �ܹ�֧����
			cursor.close();

			/*
			 * �����ж�pay��״̬
			 */
			double remain = 0;
			if (nbk_billAmountUnknown == 1) {
				if (curSize > 0 || totalPay > 0) {
					checkPayMode = 1;
				}
			} else {

				BigDecimal mb1 = new BigDecimal(
						Double.toString((Double) nbillamount)); // ������㾫��
				BigDecimal mb2 = new BigDecimal(Double.toString(totalPay)); // ������㾫��
				remain = (mb1.subtract(mb2)).doubleValue();

				if (totalPay >= nbillamount) {
					checkPayMode = 1;
				}
			}

			Date date = new Date(); // ����Ӧ���Զ�֧���ģ�������ݿ�������,��ʹ��billΪ֧��״̬
			if ((nbk_billAutoPay == 1 && nbk_billDuedate < todayDate() && totalPay < nbillamount)
					|| (nbk_billAutoPay == 1 && nbk_billDuedate < todayDate()
							&& nbk_billAmountUnknown == 1 && curSize == 0)) { // auto��bill����duedateС�ڵ����ʱ�򣬻���unkonwn
																				// �Զ�pay
				long key;
				if (nbk_billAmountUnknown == 1) {
					key = billPay(0.00, 2, nbk_billDuedate, nbk_id);
					checkPayMode = 1;
				} else {
					key = billPay(remain, 2, nbk_billDuedate, nbk_id);
					checkPayMode = 1;
				}
			}

			int payStaus = -1;// 1��ʾpaid״̬��0��ʾoverdue��-1��ʾunpaid
			if (checkPayMode == 1) {
				payStaus = 1;
			} else if (checkPayMode != 1 && nbk_billDuedate < todayDate()) {
				payStaus = 0;
			} else {
				payStaus = -1;
			}

			/*
			 * Ƕ�ײ�ѯ��������
			 */
			map.put("payStaus", payStaus);
			billDataList.add(map);
		}
		cursorEA.close();

	}

	public long billPay(double payAmount, int payMode, long payDate,
			int paymenthasBill) {
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

	public List<Map<String, Object>> getData(int account_id) {
		List<Map<String, Object>> finalDataList = new ArrayList<Map<String, Object>>();
		billDataList.clear();
		bksql = new BillKeeperSql(this);
		SQLiteDatabase db = bksql.getReadableDatabase();
		Map<String, Object> map;
		String sql = "select BK_Bill.* ,BK_Account.bk_accountName ,BK_Account.accounthasCategory  ,BK_Category.bk_categoryIconName ,BK_Category.bk_categoryName from BK_Bill,BK_Account,BK_Category where BK_Bill.billhasAccount = BK_Account._id and BK_Category._id =BK_Account.accounthasCategory and BK_Bill.bk_billisRepeat != 1 and BK_Bill.billhasAccount = "
				+ account_id;

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

			billDataList.add(map);

		}
		cursorEA.close();
		db.close();
		long thirtyDay = 30*24*3600*1000L;
		billDataList.addAll(RecurringEventByAccountIdTime.recurringData(this, account_id ,1, getNowMillis()+thirtyDay)); // ���ѭ��ʱ���¼�
		Collections.sort(billDataList, new Common.MapComparator()); // ��list��������
		judgePayment(billDataList);

		return billDataList;
	}

	public void judgePayment(List<Map<String, Object>> dataList) { // ʹ�ø÷����ж�pay��״̬
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
					if (paymentMode == -1 || paymentMode == 0
							|| paymentMode == 2) { // mark
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
					if (paymentMode == -1 || paymentMode == 0
							|| paymentMode == 2) { // mark
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

	public long getNowMillis() { // �õ�����ĺ�����
		Date date1 = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		String nowTime = formatter.format(date1);
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(nowTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long nowMillis = c.getTimeInMillis(); // ��ȡ���������ն�Ӧ�ĺ������ȥʱ���룬�������

		return nowMillis;
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.account_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home: // ���Ͻǰ�ť���ص�id

			finish();

			return true;

		case R.id.account_detail_edit:

			Intent intent = new Intent();
			intent.putExtra("account_id", account_id);
			intent.setClass(this, EditAccountActivity.class);
			startActivityForResult(intent, 4);

			break;

		}
		return super.onOptionsItemSelected(item);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 4:
			if (data != null) {
				handler.post(mUpdater);

				Intent intent = new Intent();
				intent.putExtra("msg", "succeed");
				setResult(5, intent);
			}
			break;
		case 10:
			if (data != null) {
				handler.post(mUpdater);

				Intent intent = new Intent();
				intent.putExtra("msg", "succeed");
				setResult(5, intent);
			}
			break;

			

		}
	}

}
