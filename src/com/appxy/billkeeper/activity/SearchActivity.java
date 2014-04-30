package com.appxy.billkeeper.activity;

import java.io.Serializable;
import java.lang.reflect.Field;
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

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.appxy.billkeeper.BaseHomeActivity;
import com.appxy.billkeeper.R;
import com.appxy.billkeeper.R.layout;
import com.appxy.billkeeper.R.menu;
import com.appxy.billkeeper.adapter.SearchActivityAdapter;
import com.appxy.billkeeper.db.BillKeeperSql;
import com.appxy.billkeeper.entity.Common;
import com.appxy.billkeeper.entity.RecurringEventById;
import com.appxy.billkeeper.entity.RecurringEventLike;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.SearchView.OnQueryTextListener;

public class SearchActivity extends BaseHomeActivity {
	
	private BillKeeperSql bksql;
	private ListView searchListView;
	private SearchActivityAdapter adapter;
	private SearchView searchView;
	private List<Map<String, Object>> searchDataList; //��ݿ��ѯ���
	private String queryWordString = "" ;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		searchDataList = new ArrayList<Map<String,Object>>();
		searchListView =(ListView)findViewById(R.id.searchlist);
		
		adapter = new SearchActivityAdapter(this);
		searchListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,//searchÿ��ļ���
					
					long arg3) {
				// TODO Auto-generated method stub
				
				if (searchDataList != null) {
					Map<String, Object> mtMap = new HashMap<String, Object>();
					mtMap=searchDataList.get(arg2);
					Intent intent = new Intent();
					intent.putExtra("dataMap",(Serializable)mtMap);
					intent.setClass(SearchActivity.this, PaymentActivity.class);
					startActivityForResult(intent, 10);
				}
			}
		});
		searchListView.setAdapter(adapter);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);//�Ƿ���ʾ���Ͻǵ���Ƿ��ؼ�
		
//		getActionBar().setDisplayShowHomeEnabled(false);
//		getActionBar().setDisplayShowTitleEnabled(true);
		
	}
	
	public List<Map<String, Object>> getData(String keyWords) {
		List<Map<String, Object>> finalDataList = new ArrayList<Map<String, Object>>();
		searchDataList.clear();
		bksql = new BillKeeperSql(this);
		SQLiteDatabase db = bksql.getReadableDatabase();
		Map<String, Object> map;
		String keyWordsString = sqliteEscape(keyWords); // �����ַ�ת��
		String keyWordsConditionString = "( BK_Account.bk_accountMemo like '%"+keyWordsString+"%' or BK_Account.bk_accountName like '%"+keyWordsString+"%' or BK_Category.bk_categoryName like '%"+keyWordsString+"%' )"; 
		String sql = "select BK_Bill.* ,BK_Account.bk_accountName ,BK_Account.accounthasCategory  ,BK_Category.bk_categoryIconName ,BK_Category.bk_categoryName from BK_Bill,BK_Account,BK_Category where BK_Bill.billhasAccount = BK_Account._id and BK_Category._id =BK_Account.accounthasCategory and BK_Bill.bk_billisRepeat != 1 and "+keyWordsConditionString;

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

			searchDataList.add(map);

		}
		cursorEA.close();
		db.close();
		searchDataList.addAll(RecurringEventLike.recurringData(this, keyWords)); // ���ѭ��ʱ���¼�
		Collections.sort(searchDataList, new Common.MapComparator()); // ��list��������
		judgePayment(searchDataList);

		return searchDataList;
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

	public static String sqliteEscape(String keyWord){  //�����ַ����ת���ַ�
	    keyWord = keyWord.replace("/", "//");  
	    keyWord = keyWord.replace("'", "''");  
	    keyWord = keyWord.replace("[", "/[");  
	    keyWord = keyWord.replace("]", "/]");  
	    keyWord = keyWord.replace("%", "/%");  
	    keyWord = keyWord.replace("&", "/&");  
	    keyWord = keyWord.replace("_", "/_");  
	    keyWord = keyWord.replace("(", "/(");  
	    keyWord = keyWord.replace(")", "/)");  
	    return keyWord;  
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

	
//	@Override
//	public boolean onPrepareOptionsMenu(android.view.Menu menu) {
//		// TODO Auto-generated method stub
//		
//		 MenuItem searchViewMenuItem = (MenuItem) menu.findItem(R.id.menu_search);    
//		 SearchView  mSearchView = (SearchView) searchViewMenuItem.getActionView();
//		    int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
//		    ImageView v = (ImageView) mSearchView.findViewById(searchImgId);
//		    v.setImageResource(R.drawable.action_search); 
//		return super.onPrepareOptionsMenu(menu);
//		
//	}
	
private void setSearchIcons(SearchView searchView) {
        try {
        	
            Field searchField = SearchView.class.getDeclaredField("mCloseButton");
            searchField.setAccessible(true);
            ImageView closeBtn = (ImageView) searchField.get(searchView);
            closeBtn.setImageResource(R.drawable.action_pencil);
 
            searchField = SearchView.class.getDeclaredField("mVoiceButton");
            searchField.setAccessible(true);
            ImageView voiceBtn = (ImageView) searchField.get(searchView);
            voiceBtn.setImageResource(R.drawable.action_pencil);
            
            
            searchField = SearchView.class.getDeclaredField("mSearchHintIcon");
            searchField.setAccessible(true);
            ImageView hideImageView = (ImageView) searchField.get(searchView);
            hideImageView.setImageResource(R.drawable.action_pencil);
            
//            ImageView searchButton = (ImageView) searchView.findViewById(R.id.abs__search_mag_icon);            
//            searchButton.setImageResource(R.drawable.action_pencil);
 
        } catch (NoSuchFieldException e) {
            Log.e("SearchView", e.getMessage(), e);
        } catch (IllegalAccessException e) {
            Log.e("SearchView", e.getMessage(), e);
        }
    }

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.search, menu);
	   
	    searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView(); 
	    
	    searchView.setIconified(false);
//	    searchView.setIconifiedByDefault(false);
	    
//	    int searchImgId = searchView.getContext().getResources().getIdentifier("android:id/search_mag_icon", null, null);
//	    ImageView mv = (ImageView) searchView.findViewById(searchImgId);
//	    mv.setImageResource(R.drawable.action_pencil); 
//	    mv.setVisibility(View.GONE);
//	    mv.setBackgroundResource(Color.TRANSPARENT);
	    
//	    setSearchIcons(searchView);
	    
	    int mid = getResources().getIdentifier("android:id/search_src_text", null, null);
	    TextView textView = (TextView) searchView.findViewById(mid);
	    textView.setHintTextColor(Color.WHITE); //rgb(130, 136, 139)
	    textView.setTextSize(16);
	    
//	    EditText searchPlate = (EditText) searchView.findViewById(mid);        
//	    searchPlate.setTextColor(getResources().getColor(R.color.white));  
//	    searchPlate.setBackgroundResource(R.drawable.action_pencil);  
//	    searchPlate.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
	    
//	    setSearchIcons(searchView);
	    
	    
//	    final Point p = new Point();
//
//        getWindowManager().getDefaultDisplay().getSize(p);
//
//        // Create LayoutParams with width set to screen's width
//        LayoutParams params = new LayoutParams(p.x, LayoutParams.MATCH_PARENT);
//
//        searchView.setLayoutParams(params);
	    
//	    int searchImgId = getResources().getIdentifier("android:id/search_mag_icon", null, null);
//	    ImageView v = (ImageView) searchView.findViewById(searchImgId);
////	    v.setImageResource(R.drawable.action_search); 
//	   
//	    v.setAdjustViewBounds(true);
//	    v.setMaxWidth(0);
//	    v.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//	    v.setImageDrawable(null);
//	    v.setVisibility(View.GONE);
	    
//	    ImageView searchHintIcon = (ImageView)searchView.findViewById( android.support.v7.appcompat.R.id.search_mag_icon);
//	    searchHintIcon.setImageResource(R.drawable.action_search);
	        
	   
	    
	    searchView.setQueryHint("Memo, Account, Category");
	    searchView.onActionViewExpanded();
	    
		searchView.setOnQueryTextListener(new OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				
 				return false;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) { 
				// TODO Auto-generated method stub
				queryWordString = newText;
				
				if (newText!=null && newText.length()!=0) {
				getData(newText);
				adapter.setAdapterData(searchDataList);
				adapter.notifyDataSetChanged();
				}
				return false;
			}
		});
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {

		case 10:
			if (data != null) {
				
				if (queryWordString!=null && queryWordString.length()!=0) {
				getData(queryWordString);
				adapter.setAdapterData(searchDataList);
				adapter.notifyDataSetChanged();
				}
			}

			break;

		}

	}
  
	
	}
