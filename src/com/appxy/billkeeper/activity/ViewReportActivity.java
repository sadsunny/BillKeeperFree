package com.appxy.billkeeper.activity;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;

import com.appxy.billkeeper.BaseHomeActivity;
import com.appxy.billkeeper.ChartDrawing;
import com.appxy.billkeeper.R;
import com.appxy.billkeeper.R.layout;
import com.appxy.billkeeper.R.menu;
import com.appxy.billkeeper.adapter.ViewReportActivityAdapter;
import com.appxy.billkeeper.db.BillKeeperSql;
import com.appxy.billkeeper.entity.Common;
import com.appxy.billkeeper.entity.RecurringEvent;
import com.appxy.billkeeper.entity.RecurringEventByAccountIdTime;

import android.os.Bundle;
import android.os.Handler;
import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ViewReportActivity extends BaseHomeActivity {

	private GraphicalView lChart;
	private ChartDrawing lineChart;
	private LinearLayout lineChartContainer;
	public Handler handler;
	private List<Map<String, Object>> mDataList;
	private double totalAll;
	
	private double totalZero; //0����
	private double totalOne;
	private double totalTwo;
	private double totalThree;
	private double totalFour;
	private double totalFive;
	
	private double totalSix;
	private double totalSeven;
	private double totalEghit;
	private double totalNine;
	private double totalTen;
	private double totalEleven;
	
	private ListView mListView;
	private BillKeeperSql bksql;
	private int account_id;
	private String accountName;
	private int categoryInt ;
	private ViewReportActivityAdapter VRAdapter;
	private String account_name="" ;
	
	private List<Map<String, Object>> monthDataList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_report);
		
//		getActionBar().setDisplayShowHomeEnabled(false);
//		getActionBar().setDisplayShowTitleEnabled(true);
//		 int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
//		 TextView title = (TextView) findViewById(titleId);
//		 title.setTextColor(this.getResources().getColor(R.color.white));
		 
		lineChartContainer = (LinearLayout) findViewById(R.id.LineChartLayout);
	    mListView = (ListView)findViewById(R.id.mListView);

		mDataList = new ArrayList<Map<String, Object>>();
		monthDataList = new ArrayList<Map<String, Object>>();
		VRAdapter = new ViewReportActivityAdapter(this);
		mListView.setAdapter(VRAdapter);
		
	    Intent intent = getIntent();
	    account_name = intent.getStringExtra("accountName");
	    account_id = intent.getIntExtra("account_id", 1);	
	    
		handler = new Handler();
		handler.post(mUpdater);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	public Runnable mUpdater = new Runnable() {

		@Override
		public void run() {
			getData(account_id, getFirstDayOfMonthMillis(-5),getLastDayOfMonthMillis(6));
			filterData(mDataList);
			
			String[] mMonth = new String[] { turnToMonth(getFirstDayOfMonthMillis(-5)),turnToMonth(getFirstDayOfMonthMillis(-4)),turnToMonth(getFirstDayOfMonthMillis(-3)),turnToMonth(getFirstDayOfMonthMillis(-2)),turnToMonth(getFirstDayOfMonthMillis(-1)), turnToMonth(getFirstDayOfMonthMillis(0)),turnToMonth(getFirstDayOfMonthMillis(1)), turnToMonth(getFirstDayOfMonthMillis(2)), turnToMonth(getFirstDayOfMonthMillis(3)), turnToMonth(getFirstDayOfMonthMillis(4)), turnToMonth(getFirstDayOfMonthMillis(5)),turnToMonth(getFirstDayOfMonthMillis(6)) };
			
			double[] income = {totalEleven ,totalTen ,totalNine ,totalEghit ,totalSeven ,totalSix, totalFive , totalFour, totalThree,totalTwo, totalOne, totalZero};
			double max = income[0];
			
			 monthDataList.clear();
			 Map<String, Object> map;
			for (int i = 0; i < income.length; i++) {
				
				if (max<income[i]) {
					max=income[i];
				} 
				
				if (income[i]>0) {
					 map = new HashMap<String, Object>();
					 map.put("monthTotal", income[i]);
					 map.put("monthString", getMonthString(i-5));
					 monthDataList.add(map);
				}
			}
			
			lineChart = new ChartDrawing("Amount in Dollars", "Year 2013",
					"Test Chart", mMonth);
			lineChart.set_XYSeries(income, "income");
			lineChart.set_XYMultipleSeriesRenderer_Style(lineChart.set_XYSeriesRender_Style(ViewReportActivity.this) ,max+max/12 ,ViewReportActivity.this);
			// Creating a Line Chart
			lChart = (GraphicalView) ChartFactory.getLineChartView(getBaseContext(), lineChart.getDataset(),lineChart.getMultiRenderer());
			// Adding the Line Chart to the LinearLayout
			lineChartContainer.addView(lChart);
			
			
			 
			
			
			VRAdapter.setAdapterList(monthDataList);
			VRAdapter.notifyDataSetChanged();
			
		}
		
	};
	
	public void filterData(List<Map<String, Object>> DataList) { //ɸѡ��� �ֱ����ÿ����֧�����ܶ�
		
		  double zero= 0.00f; 
		  BigDecimal b0 = new BigDecimal(Double.toString(zero)); //������㾫��
		  BigDecimal b1 = new BigDecimal(Double.toString(zero));
		  BigDecimal b2 = new BigDecimal(Double.toString(zero));
		  BigDecimal b3 = new BigDecimal(Double.toString(zero));
		  BigDecimal b4 = new BigDecimal(Double.toString(zero));
		  BigDecimal b5 = new BigDecimal(Double.toString(zero));
		  BigDecimal b6 = new BigDecimal(Double.toString(zero));
		  BigDecimal b7 = new BigDecimal(Double.toString(zero));
		  BigDecimal b8 = new BigDecimal(Double.toString(zero));
		  BigDecimal b9 = new BigDecimal(Double.toString(zero));
		  BigDecimal b10 = new BigDecimal(Double.toString(zero));
		  BigDecimal b11 = new BigDecimal(Double.toString(zero));
		  
		for(Map<String, Object> mMap:DataList){
			 double billamount = (Double) mMap.get("nbillamount");
	   		 long billduedate = (Long) mMap.get("nbk_billDuedate");
	   		 
	   		 if (getFirstDayOfMonthMillis(6) <= billduedate && getLastDayOfMonthMillis(6) >= billduedate) {
	   			  BigDecimal a0 = new BigDecimal(Double.toString(billamount));
		          b0 = b0.add(a0);
			} else if (getFirstDayOfMonthMillis(5) <= billduedate && getLastDayOfMonthMillis(5) >= billduedate) {
				  BigDecimal a1 = new BigDecimal(Double.toString(billamount));
		          b1 = b1.add(a1);
			} else if (getFirstDayOfMonthMillis(4) <= billduedate && getLastDayOfMonthMillis(4) >= billduedate) {
				  BigDecimal a2 = new BigDecimal(Double.toString(billamount));
		          b2 = b2.add(a2);
			} else if (getFirstDayOfMonthMillis(3) <= billduedate && getLastDayOfMonthMillis(3) >= billduedate) {
				  BigDecimal a3 = new BigDecimal(Double.toString(billamount));
		          b3 = b3.add(a3);
			} else if (getFirstDayOfMonthMillis(2) <= billduedate && getLastDayOfMonthMillis(2) >= billduedate) {
				  BigDecimal a4 = new BigDecimal(Double.toString(billamount));
		          b4 = b4.add(a4);
			} else if (getFirstDayOfMonthMillis(1) <= billduedate && getLastDayOfMonthMillis(1) >= billduedate) {
				  BigDecimal a5 = new BigDecimal(Double.toString(billamount));
		          b5 = b5.add(a5);
			} else if (getFirstDayOfMonthMillis(0) <= billduedate && getLastDayOfMonthMillis(0) >= billduedate) {
				  BigDecimal a6 = new BigDecimal(Double.toString(billamount));
		          b6 = b6.add(a6);
			} else if (getFirstDayOfMonthMillis(-1) <= billduedate && getLastDayOfMonthMillis(-1) >= billduedate) {
				  BigDecimal a7 = new BigDecimal(Double.toString(billamount));
		          b7 = b7.add(a7);
			} else if (getFirstDayOfMonthMillis(-2) <= billduedate && getLastDayOfMonthMillis(-2) >= billduedate) {
				  BigDecimal a8 = new BigDecimal(Double.toString(billamount));
		          b8 = b8.add(a8);
			} else if (getFirstDayOfMonthMillis(-3) <= billduedate && getLastDayOfMonthMillis(-3) >= billduedate) {
				  BigDecimal a9 = new BigDecimal(Double.toString(billamount));
		          b9 = b9.add(a9);
			} else if (getFirstDayOfMonthMillis(-4) <= billduedate && getLastDayOfMonthMillis(-4) >= billduedate) {
				  BigDecimal a10 = new BigDecimal(Double.toString(billamount));
		          b10 = b10.add(a10);
			} else if (getFirstDayOfMonthMillis(-5) <= billduedate && getLastDayOfMonthMillis(-5) >= billduedate) {
				  BigDecimal a11 = new BigDecimal(Double.toString(billamount));
		          b11 = b11.add(a11);
			} 
	   		 
	   	 totalZero = b0.doubleValue();
	   	 totalOne = b1.doubleValue();
	   	 totalTwo = b2.doubleValue();
	   	 totalThree = b3.doubleValue();
	   	 totalFour = b4.doubleValue();
	   	 totalFive = b5.doubleValue();
	   	 
	   	 totalSix = b6.doubleValue();
		 totalSeven = b7.doubleValue();
		 totalEghit = b8.doubleValue(); 
		 totalNine = b9.doubleValue();
		 totalTen  = b10.doubleValue();
		 totalEleven = b11.doubleValue();
		}
		
		 
	}
	
	public List<Map<String, Object>> getData(int accountId,long firstDayOfMonth, long lastDayOfMonth)
    {
	 
	  List<Map<String, Object>> finalDataList = new ArrayList<Map<String, Object>>();
	  mDataList.clear();
	  bksql = new BillKeeperSql(this);
   	  SQLiteDatabase db = bksql.getReadableDatabase();
   	  Map<String, Object> map;
	  String sql = "select BK_Bill.* ,BK_Account.bk_accountName ,BK_Account.accounthasCategory  ,BK_Category.bk_categoryIconName ,BK_Category.bk_categoryName from BK_Bill,BK_Account,BK_Category where BK_Account._id = "+accountId+" and BK_Bill.billhasAccount = BK_Account._id and BK_Category._id =BK_Account.accounthasCategory and BK_Bill.bk_billisRepeat != 1 and BK_Bill.bk_billDuedate >="+firstDayOfMonth+" and BK_Bill.bk_billDuedate <= "+lastDayOfMonth;
   	  
	  Cursor cursorEA = db.rawQuery(sql, null);
   	    while (cursorEA.moveToNext()) {
            map = new HashMap<String, Object>();
            
            int BK_Bill_Id =  cursorEA.getInt(0); 
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
            String bk_categoryName  = cursorEA.getString(28);
            
           
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
            
            map.put("indexflag", 0); //�����¼��ı�־��0�������ı�־��1��2���ظ����¼���3���ظ������¼�
            map.put("payState", -2); 
            
	        mDataList.add(map);
	        
        }
   	 cursorEA.close();
   	 db.close();
   	 mDataList.addAll(RecurringEventByAccountIdTime.recurringData(this, accountId ,firstDayOfMonth, lastDayOfMonth) ); //���ѭ��ʱ���¼�
     Collections.sort(mDataList, new Common.MapComparator());
   	
   return mDataList;
   
}
	

//	public void getData() { // ��ѯ��ȥ����µ����
//
//		  mDataList.clear();
//		  bksql = new BillKeeperSql(this);
//	   	  SQLiteDatabase db = bksql.getReadableDatabase();
//	   	  Map<String, Object> map;
//		  String sql = "select BK_Bill._id, BK_Account.bk_accountName, BK_Bill.bk_billAmountUnknown, BK_Bill.bk_billAmount, BK_Bill.bk_billDuedate, BK_Account.accounthasCategory ,BK_Bill.bk_billAutoPay from BK_Bill,BK_Account where BK_Bill.billhasAccount = BK_Account._id and BK_Bill.bk_billDuedate >="+getFirstDayOfMonthMillis(-5)+" and BK_Bill.bk_billDuedate <= "+getLastDayOfMonthMillis(0)+" and BK_Bill.billhasAccount = "+account_id+" order by BK_Bill.bk_billDuedate DESC";
//	   	  
//		  double zero= 0.00f; 
//		  BigDecimal b1 = new BigDecimal(Double.toString(zero)); //������㾫��
//		  
//		  Cursor cursorEA = db.rawQuery(sql, null);
//	   	    while (cursorEA.moveToNext()) {
//	   	     map = new HashMap<String, Object>();
//	            
//	            int zbillZ_PK = cursorEA.getInt(0);	
//	            String accountname = cursorEA.getString(1); 
//	   		    int isknownamount = cursorEA.getInt(2);	
//	   		    double billamount = cursorEA.getDouble(3);
//	   		    long billduedate = cursorEA.getLong(4);
//	   		    int billcategory = cursorEA.getInt(5);
//	   		    int billAutoPay =  cursorEA.getInt(6);
//	   		    
//	   		  BigDecimal b2 = new BigDecimal(Double.toString(billamount));
//	          b1 = b1.add(b2);
//	   		    
//	   		    map.put("zbillZ_PK", zbillZ_PK);
//	            map.put("accountname", accountname);
//		        map.put("isknownamount", isknownamount);
//		        map.put("billamount", billamount);
//		        map.put("billduedate", billduedate);
//		        map.put("billcategory", billcategory);
//		        map.put("billAutoPay", billAutoPay);
//		        map.put("mDueDate", turnToDate(billduedate));
//		        
//		        mDataList.add(map);
//	   	    }
//	   	    
//	   	    totalAll = b1.doubleValue();
//	   	 cursorEA.close();   
//	   	 db.close();
//	}

   public String turnToDate(long mills) { //��������ת��Ϊ���ں�����
		
		Date date2 = new Date(mills);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");
		String theDate = sdf.format(date2); 
		return theDate;
	}


	public String turnToMonth(long mills) { // ��������ת��Ϊ��

		Date date2 = new Date(mills);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM");
		String theDate = sdf.format(date2);
		return theDate;
	}
	
	public String getMonthString(int froMonth) { // ��ȡ�������X�µģ�ĳ��ĳ�µ�����
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, froMonth);

		String dateTime = new SimpleDateFormat("MMM, yyyy").format(cal
				.getTime());

		return dateTime;
	}
	

	public long getFirstDayOfMonthMillis(int froMonth) { // ��ȡ�������X�µģ�ĳ��ĳ�µĵ�һ��ĺ�����
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, froMonth);
		cal.set(Calendar.DAY_OF_MONTH, cal.getMinimum(Calendar.DATE));

		String dateTime = new SimpleDateFormat("MM-dd-yyyy").format(cal
				.getTime());

		Calendar c = Calendar.getInstance();

		try {
			c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(dateTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c.getTimeInMillis();
	}

	public long getLastDayOfMonthMillis(int froMonth) { // ��ȡ�������X�µ�,ĳ��ĳ�µ����һ��ĺ�����
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, froMonth);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE)); // xxx.set��Calendar.DAY_OF_MONTH,12����������Ϊ12��..cal.getActualMaximum(Calendar.DATE)ĳ��ĳ�µ����һ��

		String dateTime = new SimpleDateFormat("MM-dd-yyyy").format(cal
				.getTime());
		Calendar c = Calendar.getInstance();

		try {
			c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(dateTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return c.getTimeInMillis();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home: // ���Ͻǰ�ť���ص�id

			finish();
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

}
