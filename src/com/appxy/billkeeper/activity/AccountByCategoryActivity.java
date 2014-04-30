package com.appxy.billkeeper.activity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import com.appxy.billkeeper.R;
import com.appxy.billkeeper.activity.AccountDetailActivity;
import com.appxy.billkeeper.activity.NewBillActivity;
import com.appxy.billkeeper.activity.SlidingMenuActivity;
import com.appxy.billkeeper.adapter.AccountFragmentAdapter;
import com.appxy.billkeeper.adapter.CategoryFragmentAllAdapter;
import com.appxy.billkeeper.adapter.CategoryFragmentExpandableListviewAdapter;
import com.appxy.billkeeper.adapter.ReportFragmentAdapter;
import com.appxy.billkeeper.adapter.ReportNavigationListAdapter;
import com.appxy.billkeeper.db.BillKeeperSql;
import com.appxy.billkeeper.entity.Common;
import com.appxy.billkeeper.entity.RecurringEventById;
import com.appxy.billkeeper.entity.RecurringEventByIdandTime;

import android.R.integer;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.OnNavigationListener;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class AccountByCategoryActivity extends Activity {
	
	  private BillKeeperSql bksql;
	  private CategorySeries mSeries = new CategorySeries("");
	  private DefaultRenderer mRenderer = new DefaultRenderer();
	  private GraphicalView mChartView;
	  private LinearLayout PieChartLayout;
	  private ListView allCategoryListView;
	  private CategoryFragmentAllAdapter allAdapter;
	  private CategoryFragmentExpandableListviewAdapter mExpandableListviewAdapter;
	  public static Calendar calStartDate ;
	  private int mYear;
	  private int mMonth;
	  private int mQuarter;
	  
	  private double total;
	  private TextView currencyTextView ;
	  
	  private ReportFragmentAdapter adapter;
      private ListView listView;
      private List<Map<String, Object>> mDataList;
      private TextView totalTextView;
	  public Handler handler;
	  
	  private List<Map<String, Object>> billDataList;
	  private int BK_CategoryId;
	  private long startDate;
	  private long endDate;
	  private int bk_categoryIconName;
	  private String categoryName;
	  private ImageView categoryImageView;
	  private TextView categoryTextView;
	  
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_by);

//		getActionBar().setDisplayShowHomeEnabled(false);
//		getActionBar().setDisplayShowTitleEnabled(true);
//		int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
//		TextView title = (TextView) findViewById(titleId);
//		title.setTextColor(this.getResources().getColor(R.color.white));
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mDataList = new ArrayList<Map<String, Object>>();
		billDataList = new ArrayList<Map<String, Object>>();
		calStartDate = Calendar.getInstance();
      
		categoryImageView = (ImageView)findViewById(R.id.categorymageView);
		categoryTextView = (TextView)findViewById(R.id.categoryTextView);
		
		PieChartLayout = (LinearLayout)findViewById(R.id.PieChartLayout); //Բ��ͼ�Ĳ���
		listView = (ListView)findViewById(R.id.account_listview); 
		totalTextView = (TextView)findViewById(R.id.totalTextView);
		currencyTextView = (TextView)findViewById(R.id.currencyTextView);
		currencyTextView.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
		
		Intent intent = getIntent();
		BK_CategoryId = intent.getIntExtra("BK_CategoryId", 1);
		bk_categoryIconName = intent.getIntExtra("bk_categoryIconName", 1);
		categoryName = intent.getStringExtra("bk_categoryName");
		startDate = intent.getLongExtra("startDate", 1);
		endDate = intent.getLongExtra("endDate", 1);
		
//		getActionBar().setTitle(categoryName);
		
		 categoryImageView.setImageResource(Common.CATEGORYICON[bk_categoryIconName]);
		 categoryTextView.setText(categoryName);
		
		handler = new Handler();
		handler.post(mUpdater);
		
		int  iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);
		
        mRenderer.setStartAngle(180);//���ýǶȵ��������
        mRenderer.setDisplayValues(false);
        mRenderer.setPanEnabled(false);
        mRenderer.setShowLabels(false);
        mRenderer.setShowLegend(false);
        mRenderer.setZoomEnabled(false);
        mRenderer.setAntialiasing(true);
        mRenderer.setScale(1.4f);
        
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				List<Map<String, Object>> dataList =adapter.getAdapterDate();
				
				int key =1;
				String accountName ="";
				int accounthasCategory = 1;
				 
				if (dataList != null) {
					key = (Integer) dataList.get(arg2).get("BK_CategoryId");
					accountName = (String) dataList.get(arg2).get("categoryName");
				}
				Intent intent = new Intent();
				intent.putExtra("account_id", key);
				intent.putExtra("accountName",accountName);
				
				intent.setClass(AccountByCategoryActivity.this, ViewReportActivity.class);
				startActivityForResult(intent, 5);
				
			}
		});
        
	    
    } //oncreatView�Ľ���
	
	
	public Runnable mUpdater = new Runnable() {

		@Override
		public void run() {
			
			getAllData(BK_CategoryId, startDate, endDate);
			dataChangedPie(mDataList);//��ͼ
			totalTextView.setText( Common.doublepoint2str( Double.parseDouble(total+"")));
			adapter = new ReportFragmentAdapter(AccountByCategoryActivity.this, mDataList);
		    listView.setAdapter(adapter);
		        
		}
	};
	
	 public void dataChangedPie(List<Map<String, Object>> dataList) { // ���»�ȡ�����ݣ�����piechart����ݣ���ˢ��pieͼ
     
		 mSeries.clear();
		 if (dataList.size()==0 || total == 0 ) { //���Ϊ�յ�ʱ������pie
			 mSeries.add(1);
			 SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
			 int i = (mSeries.getItemCount() - 1) % Common.COLORS.length;//��ȡ��ɫ��λ��
             renderer.setColor(Common.COLORS[i]);
             mRenderer.addSeriesRenderer(renderer);
             
		}else {
         for(Map<String, Object> mMap:dataList){
        	 double amount = (Double) mMap.get("categoryAmount");
			 mSeries.add(amount);
			 SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
			 int i = (mSeries.getItemCount() - 1) % Common.COLORS.length;//��ȡ��ɫ��λ��
             renderer.setColor(Common.COLORS[i]);
             mRenderer.addSeriesRenderer(renderer);
		}
	} 
		 if (mChartView == null) {
			  mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);
		      PieChartLayout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT,
		                LayoutParams.MATCH_PARENT));
		 }else {
			 mChartView.repaint();
		}
       }
	 
	 public int getDaysBetween(long beginDate, long endDate) { //������������ʱ��
			long between_days=(endDate-beginDate)/(1000*3600*24); 
			return Integer.parseInt(String.valueOf(between_days)); 
		}
	 
	 public List<Map<String, Object>> getBillData(int category_id,long firstDayOfMonth, long lastDayOfMonth) {
		 
			List<Map<String, Object>> finalDataList = new ArrayList<Map<String, Object>>();
			billDataList.clear();
			bksql = new BillKeeperSql(this);
			SQLiteDatabase db = bksql.getReadableDatabase();
			Map<String, Object> map;
			Log.v("mdb", "����category_id"+category_id);
			String sql = "select BK_Bill.* ,BK_Account.bk_accountName ,BK_Account.accounthasCategory,BK_Category.bk_categoryIconName ,BK_Category.bk_categoryName ,BK_Category._id from BK_Bill,BK_Account,BK_Category where BK_Bill.bk_billisRepeat != 1 and "+ category_id+" = BK_Category._id and BK_Bill.billhasAccount = BK_Account._id and BK_Category._id = BK_Account.accounthasCategory and BK_Bill.bk_billDuedate >="+firstDayOfMonth+" and BK_Bill.bk_billDuedate <= "+lastDayOfMonth;

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
				int BK_CategoryId = cursorEA.getInt(29);
				
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
			    map.put("BK_CategoryId", BK_CategoryId);
				map.put("indexflag", 0); // �����¼��ı�־��0�������ı�־��1��2���ظ����¼���3���ظ������¼�
				map.put("payState", -2);

				billDataList.add(map);

			}
			Log.v("mdb", "billDataListd�����¼�"+billDataList);
			cursorEA.close();
			db.close();
			billDataList.addAll(RecurringEventByIdandTime.recurringData(this, category_id, firstDayOfMonth, lastDayOfMonth)); // ���ѭ��ʱ���¼�
			Log.v("mdb", "billDataList��Ӻ�"+billDataList);
			
			return billDataList;
		}
	 
	 public List<Map<String, Object>> getAccountData(int category_id ,long firstDayOfMonth, long lastDayOfMonth)//��ȡԴ���,��ѯcategory������ʹ��bill��account�����Ҹ�account��billʹ�ü�¼����Ҫ�����ܶʹ���ظ�����
	    {
		    mDataList.clear();
	        Map<String, Object> map;
	        bksql = new BillKeeperSql(this);
	   	    SQLiteDatabase db = bksql.getReadableDatabase();
	   	    
	   	    String sql = "select b._id ,b.bk_accountName from BK_Bill c,BK_Account b,BK_Category a where c.bk_billDuedate >="+firstDayOfMonth+" and c.bk_billDuedate <= "+lastDayOfMonth+" and c.billhasAccount = b._id and "+category_id+" = b.accounthasCategory GROUP BY b._id union select b._id ,b.bk_accountName from BK_BillObject c,BK_Account b,BK_Category a where c.bk_billODueDate >="+firstDayOfMonth+" and c.bk_billODueDate <= "+lastDayOfMonth+" and c.billObjecthasAccount = b._id and "+category_id+" = b.accounthasCategory GROUP BY b._id";
	   	    
	   	    Cursor cursorEA = db.rawQuery(sql, null);
  	     
	   	    while (cursorEA.moveToNext()) {
	   	    	
	            map = new HashMap<String, Object>();
	            int accountId =  cursorEA.getInt(0);
	            String bk_accountName = cursorEA.getString(1);
	            
	   		    map.put("BK_CategoryId", accountId);
	   		    map.put("categoryName", bk_accountName);
	            map.put("mPercent", "-1"); //�ٷֱ�
	            map.put("categoryAmount", -1);
	            
	            mDataList.add(map);
	        }
	   	 cursorEA.close();
	   	 db.close();
	   	 
	     return mDataList;
	    }

	public List<Map<String, Object>> getAllData(int category_id,long firstDayOfMonth, long lastDayOfMonth)//��ȡԴ���
	    {
//		getAccountData(category_id, firstDayOfMonth, lastDayOfMonth);
		getBillData(category_id, firstDayOfMonth, lastDayOfMonth);
		
		 List<Map<String, Object>> tDataList = new ArrayList<Map<String, Object>>();
		  Map<String, Object> mtMap;
	      for(Map<String, Object> tMap:billDataList){
	    	 mtMap = new HashMap<String, Object>();
	    	 
	    	 int BK_CategoryId = (Integer)tMap.get("billhasAccount");
	    	 String bk_categoryName = (String)tMap.get("bk_accountName");
	    	 
	    	 mtMap.put("BK_CategoryId", BK_CategoryId);
	    	 mtMap.put("categoryName", bk_categoryName);
	    	 mtMap.put("mPercent", "-1"); //�ٷֱ�
	    	 mtMap.put("categoryAmount", -1);
	    	 tDataList.add(mtMap);  //���Ƶ�mDataList
     }
	    
	    Map<String, Map> msp = new TreeMap<String, Map>();//treemap��������
	    for(Map<String, Object> Map:tDataList){  //ɸѡ�ظ���
	    	
			String BK_CategoryId = Map.get("BK_CategoryId").toString(); //*****************************
			Map.remove("BK_CategoryId");
		    msp.put(BK_CategoryId, Map);
		 }
	    Set<String> mspKey = msp.keySet();
	    
	    mDataList.clear();
		 for(String key: mspKey){
			 Map newMap = msp.get(key);
			 newMap.put("BK_CategoryId", Integer.parseInt(key));
			 mDataList.add(newMap);
		 }
		 
		
		double tzero= 0.00f; 
	    BigDecimal totalBigDecimal = new BigDecimal(Double.toString(tzero)); //������㾫��
	
		for(Map<String, Object> cMap:mDataList){
			int BK_CategoryId = (Integer) cMap.get("BK_CategoryId");
			
			double zero= 0.00f; 
		    BigDecimal b1 = new BigDecimal(Double.toString(zero)); //������㾫��
			for(Map<String, Object> bMap:billDataList){
				int hasCategoryId = (Integer)bMap.get("billhasAccount");
				if (hasCategoryId == BK_CategoryId) {
					double billamount = (Double)bMap.get("nbillamount");
					BigDecimal b2 = new BigDecimal(Double.toString(billamount));
			        b1 = b1.add(b2); 
				}
			}
		 	 totalBigDecimal = totalBigDecimal.add(b1);
			 double totalPayAmount = b1.doubleValue();
			 cMap.put("categoryAmount", totalPayAmount);
		}
		total = totalBigDecimal.doubleValue(); //��������ܶ�
		
		NumberFormat percentFormat = NumberFormat.getPercentInstance();// ��ȡ��ʽ����ʵ��  
		percentFormat.setMinimumFractionDigits(2);// ����С��λ  
		for(Map<String, Object> xMap:mDataList){//����ÿһ����ռ�İٷֱ� 
			
			if (total == 0) {
				xMap.put("mPercent", percentFormat.format(0));
			} else {
				double categoryAmount  = (Double) xMap.get("categoryAmount");
				xMap.put("mPercent", percentFormat.format(categoryAmount / total));
			}
		}
		
	     return mDataList;
	    }
	 
	
	// ���������������ʾ������
	
	
	 public static long getLastDayOfMonthMillis(int year, int month) {   //��ȡĳ�µ����һ��ĺ�����
        Calendar cal = Calendar.getInstance();   
        cal.set(Calendar.YEAR, year);   
        cal.set(Calendar.MONTH, month-1);   
        cal.set(Calendar.DAY_OF_MONTH,cal.getActualMaximum(Calendar.DATE)); //xxx.set��Calendar.DAY_OF_MONTH,12����������Ϊ12��..cal.getActualMaximum(Calendar.DATE)ĳ��ĳ�µ����һ��   
        
        
        String dateTime = new SimpleDateFormat( "MM-dd-yyyy").format(cal.getTime());
        Calendar c = Calendar.getInstance();

       try {
		c.setTime(new SimpleDateFormat( "MM-dd-yyyy").parse(dateTime));
	 } catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	 }
     
       return  c.getTimeInMillis();
       
    } 
	 
	 public static long getFirstDayOfMonthMillis(int year, int month) {   //��ȡĳ�µĵ�һ��ĺ�����
        Calendar cal = Calendar.getInstance();   
        cal.set(Calendar.YEAR, year);   
        cal.set(Calendar.MONTH, month-1);
        cal.set(Calendar.DAY_OF_MONTH,cal.getMinimum(Calendar.DATE));
        
        String dateTime = new SimpleDateFormat( "MM-dd-yyyy").format(cal.getTime());
        
        Calendar c = Calendar.getInstance();

        try {
 		c.setTime(new SimpleDateFormat( "MM-dd-yyyy").parse(dateTime));
 	 } catch (ParseException e) {
 		// TODO Auto-generated catch block
 		e.printStackTrace();
 	 }
        return  c.getTimeInMillis();
    } 
	 
	 public void onActivityResult(int requestCode, int resultCode, Intent data) {  //�������غ�����д�ȷ�� ********
			super.onActivityResult(requestCode, resultCode, data);
			switch (resultCode) {
//			case 2:
//				 if (data != null) {
//					 calStartDate = Calendar.getInstance();
//		        		
//		        		long firstDayOfMonth = getFirstDayOfMonthMillis(calStartDate.get(Calendar.YEAR), calStartDate.get(Calendar.MONTH)+1);
//		        		long lastDayOfMonth = getLastDayOfMonthMillis(calStartDate.get(Calendar.YEAR), calStartDate.get(Calendar.MONTH)+1);
//		        		getAllData(firstDayOfMonth, lastDayOfMonth);
//		        		
//		        		totalTextView.setText(total+"");
//		        		dataChangedPie(mDataList);//��ͼ
//		                adapter = new ReportFragmentAdapter(this, mDataList);
//		                listView.setAdapter(adapter);
//				}
//				break;
//
//			case 5:
//				 if (data != null) {
//					 calStartDate = Calendar.getInstance();
//		        		
//		        		long firstDayOfMonth = getFirstDayOfMonthMillis(calStartDate.get(Calendar.YEAR), calStartDate.get(Calendar.MONTH)+1);
//		        		long lastDayOfMonth = getLastDayOfMonthMillis(calStartDate.get(Calendar.YEAR), calStartDate.get(Calendar.MONTH)+1);
//		        		getAllData(firstDayOfMonth, lastDayOfMonth);
//		        		
//		        		totalTextView.setText(total+"");
//		        		dataChangedPie(mDataList);//��ͼ
//		                adapter = new ReportFragmentAdapter(this, mDataList);
//		                listView.setAdapter(adapter);
//				 }
//				break;
			}
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
}
