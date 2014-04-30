package com.appxy.billkeeper.fragment;


import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import android.R.integer;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Fragment;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class CategoryFragment extends Fragment {
	
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
	  
	  private TextView Top_Date;
	  private Button btn_pre_month;
	  private Button btn_next_month;
	  private double total;
	  private TextView currencyTextView ;
	  
	  private ReportFragmentAdapter adapter;
      private ListView listView;
      private List<Map<String, Object>> mDataList;
      private TextView totalTextView;
	  private ReportNavigationListAdapter RNAdapter;
	  public Handler handler;
	  
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		getActivity().getActionBar().setDisplayShowTitleEnabled(false);
	}

	@Override	
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
		
		calStartDate = Calendar.getInstance();
        // ��һ�����������Fragment��Ҫ��ʾ�Ľ��沼��,�ڶ������������Fragment������Activity,����������Ǿ�����fragment�Ƿ�����Activity
//		ActionBar mActionBar = getActivity().getActionBar();
//      View  customActionBarView = inflater.inflate(R.layout.actionba_top_month, container, false);  //�Զ���actionbar����
//		mActionBar.setDisplayShowCustomEnabled(true); 
//		mActionBar.setCustomView(customActionBarView);
		/*
		 * actionbar ����ѡ��
		 */
        SpinnerAdapter spinneradapter = ArrayAdapter.createFromResource(getActivity(), R.array.report_unit, android.R.layout.simple_list_item_1);   //simple_spinner_dropdown_item
        // �õ�ActionBar  
      
        RNAdapter = new ReportNavigationListAdapter(getActivity());
        
        ActionBar actionBar = getActivity().getActionBar();  
        // ��ActionBar�Ĳ���ģ������ΪNAVIGATION_MODE_LIST  
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);  
        
        // ΪActionBar���������˵��ͼ�����  
        actionBar.setListNavigationCallbacks(RNAdapter, new DropDownListenser());  
		
		View  view = inflater.inflate(R.layout.fragment_report, container, false); //����fragment��������
		btn_pre_month = (Button)view.findViewById(R.id.btn_pre_month);
		btn_next_month = (Button)view.findViewById(R.id.btn_next_month);
		Top_Date =  (TextView)view.findViewById(R.id.Top_Date);
		PieChartLayout = (LinearLayout)view.findViewById(R.id.PieChartLayout); //Բ��ͼ�Ĳ���
		listView = (ListView)view.findViewById(R.id.account_listview); 
		totalTextView = (TextView)view.findViewById(R.id.totalTextView);
		currencyTextView = (TextView)view.findViewById(R.id.currencyTextView);
		
		currencyTextView.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
		
        btn_pre_month.setOnClickListener(new Pre_MonthOnClickListener());
		btn_next_month.setOnClickListener(new Next_MonthOnClickListener());
		
		UpdateCurrentMonthDisplay();
		
		handler = new Handler();
		handler.post(mUpdater);
		
		int  iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);
		GetQuarter(iMonthViewCurrentMonth+1);
		
        mRenderer.setStartAngle(180);//���ýǶȵ��������
        mRenderer.setDisplayValues(false);
        mRenderer.setPanEnabled(false);
        mRenderer.setShowLabels(false);
        mRenderer.setShowLegend(false);
        mRenderer.setZoomEnabled(false);
        mRenderer.setAntialiasing(true);
        
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
//				if (condition) {
//					
//				} else if (condition) {
//					
//				} else if (condition) {
//					
//				} else {
//
//				}
				List<Map<String, Object>> dataList =adapter.getAdapterDate();
				int key =1;
				if (dataList != null) {
					key = (Integer) dataList.get(arg2).get("accountId");
				}
				Intent intent = new Intent();
				intent.putExtra("account_id", key);
				intent.setClass(getActivity(), AccountDetailActivity.class);
				startActivityForResult(intent, 5);
				
			}
		});
        
	    return view;
	    
    } //oncreatView�Ľ���
	
	
	public Runnable mUpdater = new Runnable() {

		@Override
		public void run() {
			
			long firstDayOfMonth = getFirstDayOfMonthMillis(calStartDate.get(Calendar.YEAR), calStartDate.get(Calendar.MONTH)+1);
			long lastDayOfMonth = getLastDayOfMonthMillis(calStartDate.get(Calendar.YEAR), calStartDate.get(Calendar.MONTH)+1);
			getAllData(firstDayOfMonth, lastDayOfMonth);
			dataChangedPie(mDataList);//��ͼ
			totalTextView.setText(total+"");
			adapter = new ReportFragmentAdapter(getActivity(), mDataList);
		    listView.setAdapter(adapter);
		        
		}
	};
	
	
	class DropDownListenser implements OnNavigationListener  //actionbar�����˵�����
    {  
  
        public boolean onNavigationItemSelected(int itemPosition, long itemId)  
        {
        	if (itemPosition == 0 ) {
        		
        		btn_pre_month.setOnClickListener(new Pre_MonthOnClickListener());
        		btn_next_month.setOnClickListener(new Next_MonthOnClickListener());
        		UpdateCurrentMonthDisplay();
        		
        		long firstDayOfMonth = getFirstDayOfMonthMillis(calStartDate.get(Calendar.YEAR), calStartDate.get(Calendar.MONTH)+1);
        		long lastDayOfMonth = getLastDayOfMonthMillis(calStartDate.get(Calendar.YEAR), calStartDate.get(Calendar.MONTH)+1);
        		getAllData(firstDayOfMonth, lastDayOfMonth);
        		
        		totalTextView.setText(total+"");
        		dataChangedPie(mDataList);//��ͼ
                adapter = new ReportFragmentAdapter(getActivity(), mDataList);
                listView.setAdapter(adapter);
        		
			}else if (itemPosition == 1) {
				
				btn_pre_month.setOnClickListener(new Pre_QuarterOnClickListener());
        		btn_next_month.setOnClickListener(new Next_QuarterOnClickListener());
        		UpdateQuertarYearDisplay();
    			
    			long firstDayOfQuarter=0;
    			long lastDayOfQuarter=0;
    			if (mQuarter == 1) {
    				firstDayOfQuarter = getFirstDayOfMonthMillis(calStartDate.get(Calendar.YEAR), 0+1);
    				lastDayOfQuarter = getLastDayOfMonthMillis(calStartDate.get(Calendar.YEAR), 2+1);
    			} else if (mQuarter == 2) {
    				firstDayOfQuarter = getFirstDayOfMonthMillis(calStartDate.get(Calendar.YEAR), 3+1);
    				lastDayOfQuarter = getLastDayOfMonthMillis(calStartDate.get(Calendar.YEAR), 5+1);
    			} else if (mQuarter == 3) {
    				firstDayOfQuarter = getFirstDayOfMonthMillis(calStartDate.get(Calendar.YEAR), 6+1);
    				lastDayOfQuarter = getLastDayOfMonthMillis(calStartDate.get(Calendar.YEAR), 8+1);
    			} else if (mQuarter == 4) {
    				firstDayOfQuarter = getFirstDayOfMonthMillis(calStartDate.get(Calendar.YEAR), 9+1);
    				lastDayOfQuarter = getLastDayOfMonthMillis(calStartDate.get(Calendar.YEAR), 11+1);
    			} 
    	   	    
    			getAllData(firstDayOfQuarter, lastDayOfQuarter);
    			totalTextView.setText(total+"");
    			dataChangedPie(mDataList);
    			adapter = new ReportFragmentAdapter(getActivity(), mDataList);
    		    listView.setAdapter(adapter);
        		
			}else if (itemPosition == 2) {
				
				btn_pre_month.setOnClickListener(new Pre_YearOnClickListener());
        		btn_next_month.setOnClickListener(new Next_YearOnClickListener());
        		UpdateCurrentYearDisplay();
        		
        		long firstDayOfYear = getFirstDayOfMonthMillis(calStartDate.get(Calendar.YEAR), 0+1);
    			long lastDayOfYear = getLastDayOfMonthMillis(calStartDate.get(Calendar.YEAR), 11+1);
    			
    			getAllData(firstDayOfYear, lastDayOfYear);
    			totalTextView.setText(total+"");
    			dataChangedPie(mDataList);
    			adapter = new ReportFragmentAdapter(getActivity(), mDataList);
    		    listView.setAdapter(adapter);
			}
			return true;  
        }  
    }  
  
	
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
			  mChartView = ChartFactory.getPieChartView(getActivity(), mSeries, mRenderer);
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
	 

	public List<Map<String, Object>> getAllData(long firstDayOfMonth, long lastDayOfMonth)//��ȡԴ���
	    {
		    mDataList = new ArrayList<Map<String, Object>>();
	        
	        Map<String, Object> map;
	        
	        bksql = new BillKeeperSql(getActivity());
	   	    SQLiteDatabase db = bksql.getReadableDatabase();
	   	    
	   	    Date date=new Date();
	        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
	        String nowTime = formatter.format(date);
	        Calendar c = Calendar.getInstance();
	        try {
				c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(nowTime));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        long nowMillis = c.getTimeInMillis(); //��ȡ���������ն�Ӧ�ĺ������ȥ���룬�������
	   	    
	   	    
	   	    String  Sql = "select BK_Account._id ,BK_Account.bk_accountName , sum(BK_Bill.bk_billAmount) , BK_Category.bk_categoryIconName from BK_Account," +
	   	    		"BK_Bill,BK_Category where BK_Bill.bk_billDuedate >="+firstDayOfMonth+" and BK_Bill.bk_billDuedate <= "+lastDayOfMonth+" and BK_Bill.billhasAccount = BK_Account._id and  BK_Category._id = BK_Account.accounthasCategory GROUP BY BK_Account._id "; 
	   	   
	   	    Cursor cursorEA = db.rawQuery(Sql, null);
	   	    double all= 0;
	   	    
	    	 double  i= 0.00f;
     	     BigDecimal b1 = new BigDecimal(Double.toString(i)); //������㾫��
     	     
	   	    while (cursorEA.moveToNext()) {
	   	    	
	            map = new HashMap<String, Object>();
	            int accountId =  cursorEA.getInt(0);
	            String categoryName = cursorEA.getString(1);
	            double categoryAmount1= cursorEA.getDouble(2); 
	            int categoryPosition  =  cursorEA.getInt(3);
	            
	            BigDecimal bg = new BigDecimal(categoryAmount1);
	            
	            double categoryAmount = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();//ת����ݿ�sum�����2λ������������
	            
	            BigDecimal b2 = new BigDecimal(Double.toString(categoryAmount));
	            b1 = b1.add(b2); 
	            
	   		    all=all+categoryAmount;
	   		    
	   		    map.put("accountId", accountId);
	   		    map.put("categoryName", categoryName);
	            map.put("categoryAmount", categoryAmount);
	            map.put("categoryKey", categoryPosition);
	            map.put("mPercent", -1); //�ٷֱ�
	            
	            mDataList.add(map);
	        }
	   	 cursorEA.close();
	   	 db.close();
	   	 
	   	 NumberFormat format = NumberFormat.getPercentInstance();// ��ȡ��ʽ����ʵ��  
	     format.setMinimumFractionDigits(2);// ����С��λ  
	     total = b1.doubleValue();
	   	 for (Map<String, Object> iMap:mDataList) { //��ÿ��amount��ռ�İٷֱȣ�map.putʵ�����޸ĵ���ͬһ������mDataList
	   		 if (all==0) {
	   			iMap.put("mPercent", format.format(0));
			}else {
				double mAmount = (Double) iMap.get("categoryAmount");
				iMap.put("mPercent", format.format(mAmount / all));
			}
		}
	   	 
	     return mDataList;
	    }
	 
	
	// ���������������ʾ������
	private void UpdateCurrentMonthDisplay() {
		String date = (calStartDate.get(Calendar.MONTH)+1) + "-"
				+ (calStartDate.get(Calendar.YEAR));
		Top_Date.setText(date);
	}
	
	private void UpdateCurrentYearDisplay() {
		String date = (calStartDate.get(Calendar.YEAR))+"";
		Top_Date.setText(date);
	}
	
	private void UpdateQuertarYearDisplay() {
		String date = (calStartDate.get(Calendar.YEAR))+"";
		Top_Date.setText("Q "+mQuarter+"-"+date);
	}
	
 class Pre_YearOnClickListener implements OnClickListener{ //��һ�갴ť����

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int  iMonthViewCurrentYear = calStartDate.get(Calendar.YEAR);
			iMonthViewCurrentYear--;
			
			calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
			UpdateCurrentYearDisplay();
	   	    
			long firstDayOfYear = getFirstDayOfMonthMillis(calStartDate.get(Calendar.YEAR), 0+1);
			long lastDayOfYear = getLastDayOfMonthMillis(calStartDate.get(Calendar.YEAR), 11+1);
			
			getAllData(firstDayOfYear, lastDayOfYear);
			totalTextView.setText(total+"");
			dataChangedPie(mDataList);
			adapter = new ReportFragmentAdapter(getActivity(), mDataList);
		    listView.setAdapter(adapter);
		    
		}
		
	}
	
	class Next_YearOnClickListener implements OnClickListener { //��һ�갴ť����

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int  iMonthViewCurrentYear = calStartDate.get(Calendar.YEAR);
			
		   iMonthViewCurrentYear++;
			calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
			
			UpdateCurrentYearDisplay();
			
			long firstDayOfYear = getFirstDayOfMonthMillis(calStartDate.get(Calendar.YEAR), 0+1);
			long lastDayOfYear = getLastDayOfMonthMillis(calStartDate.get(Calendar.YEAR), 11+1);
			
			getAllData(firstDayOfYear, lastDayOfYear);
			totalTextView.setText(total+"");
			dataChangedPie(mDataList);
			adapter = new ReportFragmentAdapter(getActivity(), mDataList);
		    listView.setAdapter(adapter);
		        
		}
		
	}
	
	private int GetQuarter(int month){
	
		if (1<=month && month <=3) {
			mQuarter = 1;
		}else if (4<=month && month <=6) {
			mQuarter = 2;
		}else if (7<=month && month <=9) {
			mQuarter = 3;
		}else if (10<=month && month <=12) {
			mQuarter = 4;
		}
		return mQuarter;
	}
	
	class Pre_QuarterOnClickListener implements OnClickListener{ //��һ�����Ȱ�ť����

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int  iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);
			int  iMonthViewCurrentYear = calStartDate.get(Calendar.YEAR);
			mQuarter --;
			if (mQuarter == 0) {
				mQuarter = 4;
				iMonthViewCurrentYear--;
			}
			calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
			UpdateQuertarYearDisplay();
			long firstDayOfQuarter=0;
			long lastDayOfQuarter=0;
			if (mQuarter == 1) {
				firstDayOfQuarter = getFirstDayOfMonthMillis(calStartDate.get(Calendar.YEAR), 0+1);
				lastDayOfQuarter = getLastDayOfMonthMillis(calStartDate.get(Calendar.YEAR), 2+1);
			} else if (mQuarter == 2) {
				firstDayOfQuarter = getFirstDayOfMonthMillis(calStartDate.get(Calendar.YEAR), 3+1);
				lastDayOfQuarter = getLastDayOfMonthMillis(calStartDate.get(Calendar.YEAR), 5+1);
			} else if (mQuarter == 3) {
				firstDayOfQuarter = getFirstDayOfMonthMillis(calStartDate.get(Calendar.YEAR), 6+1);
				lastDayOfQuarter = getLastDayOfMonthMillis(calStartDate.get(Calendar.YEAR), 8+1);
			} else if (mQuarter == 4) {
				firstDayOfQuarter = getFirstDayOfMonthMillis(calStartDate.get(Calendar.YEAR), 9+1);
				lastDayOfQuarter = getLastDayOfMonthMillis(calStartDate.get(Calendar.YEAR), 11+1);
			} 
	   	    
			getAllData(firstDayOfQuarter, lastDayOfQuarter);
			totalTextView.setText(total+"");
			dataChangedPie(mDataList);
			adapter = new ReportFragmentAdapter(getActivity(), mDataList);
		    listView.setAdapter(adapter);
		    
		}
		
	}
	
	class Next_QuarterOnClickListener implements OnClickListener{ //��һ�����Ȱ�ť����

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int  iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);
			int  iMonthViewCurrentYear = calStartDate.get(Calendar.YEAR);
			mQuarter ++;
			if (mQuarter == 5) {
				mQuarter = 1;
				iMonthViewCurrentYear++;
			}
			calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
			UpdateQuertarYearDisplay();
			
			long firstDayOfQuarter=0;
			long lastDayOfQuarter=0;
			if (mQuarter == 1) {
				firstDayOfQuarter = getFirstDayOfMonthMillis(calStartDate.get(Calendar.YEAR), 0+1);
				lastDayOfQuarter = getLastDayOfMonthMillis(calStartDate.get(Calendar.YEAR), 2+1);
			} else if (mQuarter == 2) {
				firstDayOfQuarter = getFirstDayOfMonthMillis(calStartDate.get(Calendar.YEAR), 3+1);
				lastDayOfQuarter = getLastDayOfMonthMillis(calStartDate.get(Calendar.YEAR), 5+1);
			} else if (mQuarter == 3) {
				firstDayOfQuarter = getFirstDayOfMonthMillis(calStartDate.get(Calendar.YEAR), 6+1);
				lastDayOfQuarter = getLastDayOfMonthMillis(calStartDate.get(Calendar.YEAR), 8+1);
			} else if (mQuarter == 4) {
				firstDayOfQuarter = getFirstDayOfMonthMillis(calStartDate.get(Calendar.YEAR), 9+1);
				lastDayOfQuarter = getLastDayOfMonthMillis(calStartDate.get(Calendar.YEAR), 11+1);
			} 
	   	    
			getAllData(firstDayOfQuarter, lastDayOfQuarter);
			totalTextView.setText(total+"");
			dataChangedPie(mDataList);
			adapter = new ReportFragmentAdapter(getActivity(), mDataList);
		    listView.setAdapter(adapter);
		    
		}
		
	}
	
	class Pre_MonthOnClickListener implements OnClickListener{ //��һ���°�ť����

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int  iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);
			int  iMonthViewCurrentYear = calStartDate.get(Calendar.YEAR);
			
			iMonthViewCurrentMonth--;
			if (iMonthViewCurrentMonth == -1) {
				iMonthViewCurrentMonth = 11;
				iMonthViewCurrentYear--;
			}
			calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
			calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
			UpdateCurrentMonthDisplay();
			
			long firstDayOfMonth = getFirstDayOfMonthMillis(calStartDate.get(Calendar.YEAR), calStartDate.get(Calendar.MONTH)+1);
			long lastDayOfMonth = getLastDayOfMonthMillis(calStartDate.get(Calendar.YEAR), calStartDate.get(Calendar.MONTH)+1);
			
			getAllData(firstDayOfMonth, lastDayOfMonth);
			totalTextView.setText(total+"");
			dataChangedPie(mDataList);
			adapter = new ReportFragmentAdapter(getActivity(), mDataList);
		    listView.setAdapter(adapter);
		    
		}
		
	}
	
	class Next_MonthOnClickListener implements OnClickListener { //��һ����ť����

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int  iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);
			int  iMonthViewCurrentYear = calStartDate.get(Calendar.YEAR);
			iMonthViewCurrentMonth++;
			
			if (iMonthViewCurrentMonth == 12) {
				iMonthViewCurrentMonth = 0;
				iMonthViewCurrentYear++;
			}
			calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
			calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
			UpdateCurrentMonthDisplay();
			
	   	    
			long firstDayOfMonth = getFirstDayOfMonthMillis(calStartDate.get(Calendar.YEAR), calStartDate.get(Calendar.MONTH)+1);
			long lastDayOfMonth = getLastDayOfMonthMillis(calStartDate.get(Calendar.YEAR), calStartDate.get(Calendar.MONTH)+1);
			
			getAllData(firstDayOfMonth, lastDayOfMonth);
			totalTextView.setText(total+"");
			dataChangedPie(mDataList);
			adapter = new ReportFragmentAdapter(getActivity(), mDataList);
		    listView.setAdapter(adapter);
		        
		}
		
	}
	
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
			case 2:
				 if (data != null) {
					 calStartDate = Calendar.getInstance();
					    btn_pre_month.setOnClickListener(new Pre_MonthOnClickListener());
		        		btn_next_month.setOnClickListener(new Next_MonthOnClickListener());
		        		UpdateCurrentMonthDisplay();
		        		
		        		long firstDayOfMonth = getFirstDayOfMonthMillis(calStartDate.get(Calendar.YEAR), calStartDate.get(Calendar.MONTH)+1);
		        		long lastDayOfMonth = getLastDayOfMonthMillis(calStartDate.get(Calendar.YEAR), calStartDate.get(Calendar.MONTH)+1);
		        		getAllData(firstDayOfMonth, lastDayOfMonth);
		        		
		        		totalTextView.setText(total+"");
		        		dataChangedPie(mDataList);//��ͼ
		                adapter = new ReportFragmentAdapter(getActivity(), mDataList);
		                listView.setAdapter(adapter);
				}
				break;

			case 5:
				 if (data != null) {
					 calStartDate = Calendar.getInstance();
					    btn_pre_month.setOnClickListener(new Pre_MonthOnClickListener());
		        		btn_next_month.setOnClickListener(new Next_MonthOnClickListener());
		        		UpdateCurrentMonthDisplay();
		        		
		        		long firstDayOfMonth = getFirstDayOfMonthMillis(calStartDate.get(Calendar.YEAR), calStartDate.get(Calendar.MONTH)+1);
		        		long lastDayOfMonth = getLastDayOfMonthMillis(calStartDate.get(Calendar.YEAR), calStartDate.get(Calendar.MONTH)+1);
		        		getAllData(firstDayOfMonth, lastDayOfMonth);
		        		
		        		totalTextView.setText(total+"");
		        		dataChangedPie(mDataList);//��ͼ
		                adapter = new ReportFragmentAdapter(getActivity(), mDataList);
		                listView.setAdapter(adapter);
				 }
				break;
			}
		}
	 
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		
		case R.id.addbill:
			Intent intent = new Intent();
			intent.setClass(getActivity(), NewBillActivity.class);
			startActivityForResult(intent, 2);
			return true;
			
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		MenuItem item = menu.findItem(R.id.search);//����actionbar�е�������ť���ɼ�
		item.setVisible(false);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
}
