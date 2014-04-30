package com.appxy.billkeeper.fragment;


import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.appxy.billkeeper.R;
import com.appxy.billkeeper.activity.NewBillActivity;
import com.appxy.billkeeper.activity.PaymentActivity;
import com.appxy.billkeeper.activity.SlidingMenuActivity;
import com.appxy.billkeeper.adapter.CalendarAdapter;
import com.appxy.billkeeper.adapter.CalendarFragmentExpandableListviewAdapter;
import com.appxy.billkeeper.adapter.CalendarFragmentListviewAdapter;
import com.appxy.billkeeper.adapter.CalendarNavigationListAdapter;
import com.appxy.billkeeper.adapter.ReportFragmentAdapter;
import com.appxy.billkeeper.db.BillKeeperSql;
import com.appxy.billkeeper.entity.Common;
import com.appxy.billkeeper.entity.RecurringEvent;
import com.appxy.billkeeper.entity.Utility;
import com.appxy.billkeeper.fragment.ReportFragment.DropDownListenser;
import com.appxy.billkeeper.fragment.ReportFragment.Next_MonthOnClickListener;
import com.appxy.billkeeper.fragment.ReportFragment.Pre_MonthOnClickListener;

import android.R.integer;
import android.R.string;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.ActionBar.OnNavigationListener;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.ViewDebug.FlagToString;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

public class CalendarFragmen extends Fragment {

	private SpinnerAdapter spinneradapter;
	private CalendarNavigationListAdapter cNAdapter;
	private BillKeeperSql bksql;
	
	public GregorianCalendar month, itemmonth;// calendar instances.

	public CalendarAdapter adapter;// adapter instance
	public Handler handler;// for grabbing some event values for showing the dot
						   // marker.
	public ArrayList<String> items; // container to store calendar items which
									// need s showing actionbaritembackground_selthe event marker
	ArrayList<String> event;
	ArrayList<String> date;
	
	private TextView title ;
	private List<Map<String, Object>> mDataList ; // ��ݿ��ѯ �������
	private List<Map<String, Object>> mListViewData;
	private CalendarFragmentListviewAdapter mListviewAdapter;
	private ListView mListView; //�·���listview
	private RelativeLayout mRelativeLayout; //RelativeLayout���������gridview��������ɼ�����
	private ExpandableListView mExpandableListView; //�·���ExpandableListView
	private CalendarFragmentExpandableListviewAdapter CFEAdapter;
	
	private List<Map<String, Object>> calendarPayList ; // ���뵽����������ʾ���жϵ���pay״̬
	
	private List<Map<String, Object>> groupDataList; //Group���
	private List<Map<String, Object>> childrenDataList ; //ÿ��child���
	private List<List<Map<String, Object>>> childrenAllDataList; //����children���
	
	private List<Map<String, Object>> mPaidList ;
	private List<Map<String, Object>> mUnpaidList ;
	private String selectedGridDate;
	private static int checkList = 0;
	private static int duecheck = -1;//1��ʾdue����unpaid������ʾremain���֡� -1 ��ʾall��0unpaid
	private static int mStatus = 0;//0��ʾall��1paid��2due
	private static int gridviewPosition ;
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
		
        // ��һ�����������Fragment��Ҫ��ʾ�Ľ��沼��,�ڶ������������Fragment������Activity,����������Ǿ�����fragment�Ƿ�����Activity
        View  view = inflater.inflate(R.layout.fragment_calender, container, false);      
        mDataList = new  ArrayList<Map<String, Object>>();
        mPaidList = new  ArrayList<Map<String, Object>>();
        mUnpaidList = new  ArrayList<Map<String, Object>>();
        calendarPayList = new ArrayList<Map<String, Object>>();
        
        mListViewData = new ArrayList<Map<String, Object>>();
        groupDataList = new ArrayList<Map<String, Object>>();
        childrenAllDataList = new ArrayList<List<Map<String,Object>>>(); 
        
        mRelativeLayout = (RelativeLayout)view.findViewById(R.id.calendar_view);
        mExpandableListView = (ExpandableListView)view.findViewById(R.id.expandableListView1);
        
        cNAdapter =new CalendarNavigationListAdapter(getActivity());
        SpinnerAdapter spinneradapter = ArrayAdapter.createFromResource(getActivity(), R.array.repeat_type, android.R.layout.simple_spinner_item);   //simple_spinner_dropdown_item
        // �õ�ActionBar  
        ActionBar actionBar = getActivity().getActionBar();  
        // ��ActionBar�Ĳ���ģ������ΪNAVIGATION_MODE_LIST  
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);  
        // ΪActionBar���������˵��ͼ�����  
        actionBar.setListNavigationCallbacks(cNAdapter, new DropDownListenser());  
        /*
         * ��������
         */
       Locale.setDefault(Locale.ENGLISH); // ����Ĭ�ϵ�������Ϣ
		
		month = (GregorianCalendar) GregorianCalendar.getInstance();
		itemmonth = (GregorianCalendar) month.clone();
		items = new ArrayList<String>();

		selectedGridDate = getMilltoDate(getNowMillis());
			
		adapter = new CalendarAdapter(getActivity(), month); //��������
		GridView gridview = (GridView)view.findViewById(R.id.gridview);
		gridview.setAdapter(adapter);
		
		mRelativeLayout.setVisibility(View.VISIBLE);
		mExpandableListView.setVisibility(View.GONE);
		
		mListviewAdapter = new CalendarFragmentListviewAdapter(getActivity());//����listview
		mListView = (ListView)view.findViewById(R.id.ListView1);
		mListView.setVisibility(View.VISIBLE);
		mListView.setAdapter(mListviewAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				List<Map<String, Object>> dataList = mListviewAdapter.getAdapterDate();
				
				if (dataList != null) {
					Map<String, Object> mtMap = new HashMap<String, Object>();
					mtMap=dataList.get(arg2);
					Intent intent = new Intent();
					intent.putExtra("dataMap",(Serializable)mtMap);
					intent.setClass(getActivity(), PaymentActivity.class);
					startActivityForResult(intent, 10);
				}
			}
		});
		
		CFEAdapter = new CalendarFragmentExpandableListviewAdapter(getActivity(),groupDataList,childrenAllDataList);
		mExpandableListView.setAdapter(CFEAdapter);
	    mExpandableListView.setGroupIndicator(null); //ȥ��groupǰ��ļ�ͷ 
	    mExpandableListView.setDividerHeight(0);
	    
		handler = new Handler();
		handler.post(calendarUpdater);
		
	    title = (TextView)view.findViewById(R.id.title);
		title.setText(android.text.format.DateFormat.format("MMM, yyyy", month));

		RelativeLayout previous = (RelativeLayout)view.findViewById(R.id.previous);
		previous.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				checkList = 0;
				setPreviousMonth();
				refreshCalendar();
			}
		});

		RelativeLayout next = (RelativeLayout) view.findViewById(R.id.next);
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				checkList = 0;
				setNextMonth();
				refreshCalendar();
			}
		});

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
	
				date = new ArrayList<String>();
				mListViewData.clear();
			   	Map<String, Object> map;
			   	
			  
			   	
			   	((CalendarAdapter) parent.getAdapter()).setSelected(v);
			    selectedGridDate = CalendarAdapter.dayString.get(position);
				String[] separatedTime = selectedGridDate.split("-");
				String gridvalueString = separatedTime[2].replaceFirst("^0*",
						"");// taking last part of date. ie; 2 from 2012-12-02.
				int gridvalue = Integer.parseInt(gridvalueString);
				// navigate to next or previous month on clicking offdays.
				if ((gridvalue > 10) && (position < 8)) {
					setPreviousMonth();
					refreshCalendar();
				} else if ((gridvalue < 7) && (position > 28)) {
					setNextMonth();
					refreshCalendar();
				}
				((CalendarAdapter) parent.getAdapter()).setSelected(v);
			 	gridviewPosition = position;
			 	adapter.setSelPositon(position);
			 	Log.v("calx", "gridviewPosition���ѡ��:= "+gridviewPosition);
//				Log.v("mtest", "ѡ���ʱ��"+selectedGridDate);
//				Log.v("mtest", "checkList"+checkList);
				
				if (checkList == 0) {
					refreshListview(selectedGridDate,mDataList);
				} else if (checkList == 1) {
					refreshListview(selectedGridDate,mPaidList);
				} else if (checkList == 2) {
					refreshListview(selectedGridDate,mUnpaidList);
				}
			}
		});
		
	    return view;
    }
	/*
	 * ************************************************************
	 */
	
	public void refreshExpandableListview( List<Map<String, Object>> mDataList) { //��ȡexpandableListview���
		 groupDataList.clear();
	     childrenAllDataList.clear();
	     
	     
	     List<Map<String, Object>> tDataList = new ArrayList<Map<String, Object>>();
//	     tDataList.addAll(mDataList); //ʹ��addAll��clone���
//	     tDataList = (List<Map<String, Object>>) ((ArrayList)mDataList).clone();//��list clone�ķ���
	   
		    Map<String, Object> mtMap;
		    for(Map<String, Object> tMap:mDataList){
	    	 mtMap = new HashMap<String, Object>();
	    	 long billduedate = (Long)tMap.get("nbk_billDuedate");
	    	 mtMap.put("nbk_billDuedate", billduedate);
	    	 tDataList.add(mtMap);
	     }
		 
		 Map<String, Object> map;
		 Map<String, Object> mapClone;
		 
		 Map<String, Map> msp = new TreeMap<String, Map>();
		 
		 for(Map<String, Object> Map:tDataList){  //ɸѡ�ظ���billduedate
			 
			String billduedateb = Map.get("nbk_billDuedate").toString();
			Map.remove("nbk_billDuedate");
		    msp.put(billduedateb, Map);
		 }
		 Log.v("mdb", "msp��ֵ��"+msp);
		 Set<String> mspKey = msp.keySet();
		 Log.v("mdb", "mspKey��ֵ��"+mspKey);
		 
		 for(String key: mspKey){
			 Map newMap = msp.get(key);
			 newMap.put("nbk_billDuedate", key);
			 newMap.put("mDuedate", turnToDate(key));
			 groupDataList.add(newMap);
		 }
		 
	 	 for(Map<String, Object> gMap:groupDataList){
	 		    map = new HashMap<String, Object>();
	 		    childrenDataList = new ArrayList<Map<String, Object>>();
	 		    long gbillduedate = Long.parseLong( (String) gMap.get("nbk_billDuedate") );
	 		    
	 			for(Map<String, Object> bMap:mDataList){ 
	 				
	 				 long bbillduedate = (Long)bMap.get("nbk_billDuedate");
	 				 
	 				 if (gbillduedate == bbillduedate) {
	 					 
	 			        childrenDataList.add(bMap);
	 				}
	 			}
	 			childrenAllDataList.add(childrenDataList);
	 	 }
	 	 
	 	 
	 	CFEAdapter = new CalendarFragmentExpandableListviewAdapter(getActivity(),groupDataList,childrenAllDataList);
	 	CFEAdapter.setDuecheck(duecheck);
		mExpandableListView.setAdapter(CFEAdapter);
		
		int groupCount = mExpandableListView.getCount(); //����ExpandableListviewĬ��ȫ��չ��
		Log.v("mcheck", "groupCountr����Ĵ�С"+groupCount);
		
        for (int i=0; i<groupCount; i++) {
        	
        	mExpandableListView.expandGroup(i);
            };
        mExpandableListView.setOnGroupClickListener(new OnGroupClickListener() { //����group�¼��������������return true
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				// TODO Auto-generated method stub
				return true;
			}
		});
        mExpandableListView.setCacheColorHint(0); 
        
		mExpandableListView.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				
				List<List<Map<String, Object>>> dataList = CFEAdapter.getAdapterDate();
				if (dataList != null) {
					
					Map<String, Object> mtMap = new HashMap<String, Object>();
					mtMap=dataList.get(groupPosition).get(childPosition);
					Intent intent = new Intent();
					intent.putExtra("dataMap",(Serializable)mtMap);
					intent.setClass(getActivity(), PaymentActivity.class);
					startActivityForResult(intent, 10);
				}
				
				return false;
			}
		});
	}
	
	public String turnToDate(String mills) { //��������ת��Ϊ���ں�����
		
		long millsLong = Long.parseLong(mills);
		Date date2 = new Date(millsLong);
		SimpleDateFormat sdf = new SimpleDateFormat("EE, MMMM dd");
		String theDate = sdf.format(date2); 
		return theDate;
	}
	
	public void refreshListview(String selectedGridDate,List<Map<String, Object>> dataList) {
		Map<String, Object> map;
		mListViewData.clear();
		for(Map<String, Object> bMap:dataList){ 
	   		long mbillduedate = (Long) bMap.get("nbk_billDuedate");
	   		String dateString = getMilltoDate(mbillduedate);
	   		
	   		if (dateString.equals(selectedGridDate)) {
		        
		        mListViewData.add(bMap); //listview���
			}
	   	 }
		mListviewAdapter.setAdapterList(mListViewData);
		mListviewAdapter.setDuecheck(duecheck);
		mListviewAdapter.notifyDataSetChanged();
//		mListView.setAdapter(mListviewAdapter);
	}

	protected void setNextMonth() {
		if (month.get(GregorianCalendar.MONTH) == month
				.getActualMaximum(GregorianCalendar.MONTH)) {
			month.set((month.get(GregorianCalendar.YEAR) + 1),month.getActualMinimum(GregorianCalendar.MONTH), 1);
		} else {
			month.set(GregorianCalendar.MONTH,
					month.get(GregorianCalendar.MONTH) + 1);
		}
		selectedGridDate = getFirstDayOfMonth(month.get(GregorianCalendar.YEAR),month.get(GregorianCalendar.MONTH)+1);
        Log.v("mdb", "��ʼ�¸��µĵ�һ��"+selectedGridDate);
	}

	protected void setPreviousMonth() {
		if (month.get(GregorianCalendar.MONTH) == month
				.getActualMinimum(GregorianCalendar.MONTH)) {
			month.set((month.get(GregorianCalendar.YEAR) - 1),
					month.getActualMaximum(GregorianCalendar.MONTH), 1);
		} else {
			month.set(GregorianCalendar.MONTH,
					month.get(GregorianCalendar.MONTH) - 1);
		}
		selectedGridDate = getFirstDayOfMonth(month.get(GregorianCalendar.YEAR),month.get(GregorianCalendar.MONTH)+1);
	}

	protected void showToast(String string) {
		Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();

	}

	public void refreshCalendar() {
		
		adapter.refreshDays();
//		adapter.notifyDataSetChanged();
		
		handler.post(calendarUpdater); // generate some calendar items
		title.setText(android.text.format.DateFormat.format("MMM yyyy", month));
	}

	public Runnable calendarUpdater = new Runnable() { //�̲߳�ѯ��ݿ����UI

		@Override
		public void run() {
			
			items.clear();
			// Print dates of the current week
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String itemvalue;
			
			long firstDay = getFirstDayOfMonthMillis(month.get(GregorianCalendar.YEAR),month.get(GregorianCalendar.MONTH)+1);
			long lastDay = getLastDayOfMonthMillis(month.get(GregorianCalendar.YEAR),month.get(GregorianCalendar.MONTH)+1);
			getData(firstDay, lastDay);
			
			calendarPayList.clear();
			ArrayList<Long> mlist = new ArrayList(); //����������������
			 
			if (checkList == 0 ) {
				
				for(Map<String, Object> bMap:mDataList){ 
			   		 
			   		long billduedate = (Long) bMap.get("nbk_billDuedate");
			   		mlist.add(billduedate);
			   		
//			   		String dateString = getMilltoDate(billduedate);
//			   		itemvalue = df.format(itemmonth.getTime());
//					itemmonth.add(GregorianCalendar.DATE, 1);
//			   		items.add(dateString);
			   	 }
				
				  Iterator it1 = mlist.iterator();  //ʹ�õ������ȥ��items���ظ�����,ͬһ���¼��϶��¿����������ʾЧ��
				  Hashtable ht = new Hashtable();
				  while(it1.hasNext()){
				   Object obj = it1.next();
				   ht.put(obj, obj);
				  }
				  Iterator it2 = ht.keySet().iterator();
				  ArrayList<Long> list = new ArrayList();
				  while(it2.hasNext()){
				   list.add((Long) it2.next());
				  }
//				  Log.v("mdb", "ɸѡ�������"+list);
				  Map<String, Object> cMap;
				  for (int i = 0; i < list.size(); i++) { //�жϵ����Ƿ�����ȫ��֧����
					  
					  cMap = new HashMap<String, Object>();
					  long date = list.get(i);
					  int paidMent = -5;
					  int unpaidMent = -5;
					  int finalPaid = -5;
					  
					  for(Map<String, Object> bMap:mDataList){ 
						  long billduedate = (Long) bMap.get("nbk_billDuedate");
						  int payState = (Integer)bMap.get("payState");
						  
					   		 if (date == billduedate) {
								if (payState == 1) {
									paidMent =1;
								}
								if (payState == -2 || payState == -1 || payState == 0) {
									unpaidMent = 1;
								}
							}
					  }

					  if (unpaidMent==1) {
						  finalPaid = 0;
					} else if (paidMent == 1 && unpaidMent == -5) {
						  finalPaid = 1;
					}
					   	cMap.put("dayItem", getMilltoDate(date));
						cMap.put("payStatement", finalPaid);
					   	calendarPayList.add(cMap);	 
				}
				  
//				adapter.setShowDay(selectedGridDate, true); //�÷�������ÿ�µĳ�ʼЧ��,���ڲ�����߼���bug
//				adapter.setItems(items);
				  
				  List<String> dayStrings = new ArrayList<String>();
				  dayStrings.clear();
				  dayStrings = adapter.getDayStrings();//��ȡ����ĵ���ʱ��
				  
				  long nowMillis = getNowMillis(); //��ȡ���������ն�Ӧ�ĺ������ȥ���룬�������
				    
				     if (isEqualMonth(nowMillis,lastDay)) { //�ж��Ƿ�Ϊ����
				    	 refreshListview(getMilltoDate(nowMillis),mDataList);
				    	 gridviewPosition = getListPosition(dayStrings, getMilltoDate(nowMillis));
				    	 Log.v("calx", "gridviewPosition��һ�θ�ֵ:= "+gridviewPosition);
					}else {
						 refreshListview(getMilltoDate(firstDay),mDataList);
						 gridviewPosition = getListPosition(dayStrings, getMilltoDate(firstDay));
						 Log.v("calx", "gridviewPosition�Ǳ��¸�ֵ:= "+gridviewPosition);
					}
				     
			    adapter.setPaidDate(calendarPayList);
			
//				Log.v("mmes", "daustring:"+dayStrings);
				
				if (gridviewPosition >=0 ) {
					adapter.setSelPositon(gridviewPosition);
				}
				adapter.notifyDataSetChanged();
				month.get(GregorianCalendar.MONTH);
				
				refreshExpandableListview(mDataList);
				
				
			} else if (checkList == 1) {
				
				
				getPaidData();
				
//				for(Map<String, Object> bMap:mPaidList){ 
//			   		long billduedate = (Long) bMap.get("nbk_billDuedate");
//			   		String dateString = getMilltoDate(billduedate);
//			   		items.add(dateString);
//			   	 }
//				
//				adapter.setItems(items);
//				adapter.setShowDay(selectedGridDate, true);
//				adapter.notifyDataSetChanged();
				
					for(Map<String, Object> bMap:mPaidList){ 
				   		 
				   		long billduedate = (Long) bMap.get("nbk_billDuedate");
				   		mlist.add(billduedate);
				   		
//				   		String dateString = getMilltoDate(billduedate);
////				   		itemvalue = df.format(itemmonth.getTime());
////						itemmonth.add(GregorianCalendar.DATE, 1);
//				   		items.add(dateString);
				   	 }
					
					  Iterator it1 = mlist.iterator();  //ʹ�õ������ȥ��items���ظ�����,ͬһ���¼��϶��¿����������ʾЧ��
					  Hashtable ht = new Hashtable();
					  while(it1.hasNext()){
					   Object obj = it1.next();
					   ht.put(obj, obj);
					  }
					  
					  Iterator it2 = ht.keySet().iterator();
					  ArrayList<Long> list = new ArrayList();
					  while(it2.hasNext()){
					   list.add((Long) it2.next());
					  }
//					  Log.v("mdb", "ɸѡ�������"+list);
					  Map<String, Object> cMap;
					  for (int i = 0; i < list.size(); i++) { //�жϵ����Ƿ�����ȫ��֧����
						  
						  cMap = new HashMap<String, Object>();
						  long date = list.get(i);
						  int paidMent = -5;
						  int unpaidMent = -5;
						  int finalPaid = -5;
						  
						  for(Map<String, Object> bMap:mPaidList){ 
							  long billduedate = (Long) bMap.get("nbk_billDuedate");
							  int payState = (Integer)bMap.get("payState");
							  
						   		 if (date == billduedate) {
									if (payState == 1) {
										paidMent =1;
									}
									if (payState == -2 || payState == -1 || payState == 0) {
										unpaidMent = 1;
									}
								}
						  }

						  if (unpaidMent==1) {
							  finalPaid = 0;
						} else if (paidMent == 1 && unpaidMent == -5) {
							  finalPaid = 1;
						}
						   	cMap.put("dayItem", getMilltoDate(date));
							cMap.put("payStatement", finalPaid);
						   	calendarPayList.add(cMap);	 
					}
					  
					  List<String> dayStrings = new ArrayList<String>();
					  dayStrings.clear();
					  dayStrings = adapter.getDayStrings();//��ȡ����ĵ���ʱ��
					  
					  long nowMillis = getNowMillis(); //��ȡ���������ն�Ӧ�ĺ������ȥ���룬�������
					    
					     if (isEqualMonth(nowMillis,lastDay)) { //�ж��Ƿ�Ϊ����
					    	 gridviewPosition = getListPosition(dayStrings, getMilltoDate(nowMillis));
					    	 Log.v("calx", "gridviewPosition��һ�θ�ֵ:= "+gridviewPosition);
						}else {
							 gridviewPosition = getListPosition(dayStrings, getMilltoDate(firstDay));
							 Log.v("calx", "gridviewPosition�Ǳ��¸�ֵ:= "+gridviewPosition);
						}
					     
//					Log.v("mmes", "daustring:"+dayStrings);
					
					if (gridviewPosition >=0 ) {
						adapter.setSelPositon(gridviewPosition);
					}
					  
//					adapter.setShowDay(selectedGridDate, true); //�÷�������ÿ�µĳ�ʼЧ��,���ڲ�����߼���bug
//					adapter.setItems(items);
					adapter.setPaidDate(calendarPayList);
					adapter.notifyDataSetChanged();

			    duecheck = 0 ;
				refreshExpandableListview(mPaidList);
				refreshListview(selectedGridDate, mPaidList);
				
				
				
			} else if (checkList == 2) {
				
				getUnpaidData();
				
//				for(Map<String, Object> bMap:mUnpaidList){ 
//			   		long billduedate = (Long) bMap.get("nbk_billDuedate");
//			   		String dateString = getMilltoDate(billduedate);
//			   		items.add(dateString);
//			   	 }
//				
////				adapter.setItems(items);
//				adapter.setShowDay(selectedGridDate, true);
//				adapter.notifyDataSetChanged();
				
				
					for(Map<String, Object> bMap:mUnpaidList){ 
				   		 
				   		long billduedate = (Long) bMap.get("nbk_billDuedate");
				   		mlist.add(billduedate);
				   		
//				   		String dateString = getMilltoDate(billduedate);
////				   		itemvalue = df.format(itemmonth.getTime());
////						itemmonth.add(GregorianCalendar.DATE, 1);
//				   		items.add(dateString);
				   	 }
					
					  Iterator it1 = mlist.iterator();  //ʹ�õ������ȥ��items���ظ�����,ͬһ���¼��϶��¿����������ʾЧ��
					  Hashtable ht = new Hashtable();
					  while(it1.hasNext()){
					   Object obj = it1.next();
					   ht.put(obj, obj);
					  }
					  
					  Iterator it2 = ht.keySet().iterator();
					  ArrayList<Long> list = new ArrayList();
					  while(it2.hasNext()){
					   list.add((Long) it2.next());
					  }
//					  Log.v("mdb", "ɸѡ�������"+list);
					  Map<String, Object> cMap;
					  for (int i = 0; i < list.size(); i++) { //�жϵ����Ƿ�����ȫ��֧����
						  
						  cMap = new HashMap<String, Object>();
						  long date = list.get(i);
						  int paidMent = -5;
						  int unpaidMent = -5;
						  int finalPaid = -5;
						  
						  for(Map<String, Object> bMap:mUnpaidList){ 
							  long billduedate = (Long) bMap.get("nbk_billDuedate");
							  int payState = (Integer)bMap.get("payState");
							  
						   		 if (date == billduedate) {
									if (payState == 1) {
										paidMent =1;
									}
									if (payState == -2 || payState == -1 || payState == 0) {
										unpaidMent = 1;
									}
								}
						  }

						  if (unpaidMent==1) {
							  finalPaid = 0;
						} else if (paidMent == 1 && unpaidMent == -5) {
							  finalPaid = 1;
						}
						   	cMap.put("dayItem", getMilltoDate(date));
							cMap.put("payStatement", finalPaid);
						   	calendarPayList.add(cMap);	 
					}
					  
					  List<String> dayStrings = new ArrayList<String>();
					  dayStrings.clear();
					  dayStrings = adapter.getDayStrings();//��ȡ����ĵ���ʱ��
					  
					  long nowMillis = getNowMillis(); //��ȡ���������ն�Ӧ�ĺ������ȥ���룬�������
					    
					     if (isEqualMonth(nowMillis,lastDay)) { //�ж��Ƿ�Ϊ����
					    	 refreshListview(getMilltoDate(nowMillis),mDataList);
					    	 gridviewPosition = getListPosition(dayStrings, getMilltoDate(nowMillis));
					    	 Log.v("calx", "gridviewPosition��һ�θ�ֵ:= "+gridviewPosition);
						}else {
							 refreshListview(getMilltoDate(firstDay),mDataList);
							 gridviewPosition = getListPosition(dayStrings, getMilltoDate(firstDay));
							 Log.v("calx", "gridviewPosition�Ǳ��¸�ֵ:= "+gridviewPosition);
						}
					     
				    adapter.setPaidDate(calendarPayList);
				
//					Log.v("mmes", "daustring:"+dayStrings);
					
					if (gridviewPosition >=0 ) {
						adapter.setSelPositon(gridviewPosition);
					}
					  
//					adapter.setShowDay(selectedGridDate, true); //�÷�������ÿ�µĳ�ʼЧ��,���ڲ�����߼���bug
//					adapter.setItems(items);
					adapter.setPaidDate(calendarPayList);
					adapter.notifyDataSetChanged();
				
//				Log.v("mdb", "unpaidlist��"+mUnpaidList);

			    duecheck = 1 ;
				refreshExpandableListview(mUnpaidList);
				refreshListview(selectedGridDate, mUnpaidList);
				
				
			}
			
			     
		}
	};
	
	public boolean isEqualMonth(long month1,long  month2) { //�Ƚ�����������������Ƿ����
		
		Date date1 = new Date(month1);
		Date date2 = new Date(month2);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		String thisDate1 = sdf.format(date1); 
		String thisDate2 = sdf.format(date2); 
		
		if (thisDate1.equals(thisDate2)) {
			return true;
		} else {
			return false;
		}
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
	
	public static String getMilltoDate(long milliSeconds) {//������ת���ɹ̶���ʽ��������
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}
	
	/*
	 * ��ȡlistview��expendlistview�����
	 */
	
	public List<Map<String, Object>> getData(long firstDayOfMonth, long lastDayOfMonth)
    {
	 
	  List<Map<String, Object>> finalDataList = new ArrayList<Map<String, Object>>();
	  mDataList.clear();
	  bksql = new BillKeeperSql(getActivity());
   	  SQLiteDatabase db = bksql.getReadableDatabase();
   	  Map<String, Object> map;
	  String sql = "select BK_Bill.* ,BK_Account.bk_accountName ,BK_Account.accounthasCategory  ,BK_Category.bk_categoryIconName ,BK_Category.bk_categoryName from BK_Bill,BK_Account,BK_Category where BK_Bill.billhasAccount = BK_Account._id and BK_Category._id = BK_Account.accounthasCategory and BK_Bill.bk_billisRepeat != 1 and BK_Bill.bk_billDuedate >="+firstDayOfMonth+" and BK_Bill.bk_billDuedate <= "+lastDayOfMonth;
   	  
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
   	 mDataList.addAll(RecurringEvent.recurringData(getActivity(), firstDayOfMonth, lastDayOfMonth) ); //���ѭ��ʱ���¼�
   	 Collections.sort(mDataList, new Common.MapComparator()); //��list��������
     judgePayment(mDataList);
   	
   return mDataList;
   
}
	
 public void judgePayment(List<Map<String, Object>> dataList ){ //ʹ�ø÷����ж�pay��״̬
	  bksql = new BillKeeperSql(getActivity());
  	  SQLiteDatabase db = bksql.getReadableDatabase();
  	  long nowMillis = getNowMillis();
  	  
		for (Map<String, Object> bMap:dataList) {
			
			int flag  = (Integer) bMap.get("indexflag");
			int id = (Integer) bMap.get("BK_Bill_Id");
      		double billamount = (Double) bMap.get("nbillamount");
      		long billduedate = (Long) bMap.get("nbk_billDuedate");
      		int  billAutoPay = (Integer) bMap.get("nbk_billAutoPay");
      		int  nbk_billAmountUnknown = (Integer)bMap.get("nbk_billAmountUnknown");
      		bMap.put("remain", 0); //ʣ�µ����
			if (flag == 0 || flag ==1) {
				
				String  sql = "select BK_Payment._id, BK_Payment.bk_payAmount, BK_Payment.bk_payMode ,BK_Payment.paymenthasBill from BK_Payment where BK_Payment.paymenthasBill = "+id;
				
				List<Map<String, Object>> pDataList = new ArrayList<Map<String, Object>>();
				double zero= 0.00f; 
			    BigDecimal b1 = new BigDecimal(Double.toString(zero)); //������㾫��
			    int checkPayMode = 0;//�жϸ�pay�Ƿ��й�quickpay��mark,�й�һ�α�ʾ��bill�Ѿ�pay�꣬mark =1 quick =2;
			    int  curSize;
				Cursor cursor1 = db.rawQuery(sql, null);
				curSize = cursor1.getCount();
//				Log.v("mtest", "pangmeny��С"+curSize);
			        while (cursor1.moveToNext()) {
			            
			            int paymentZ_PK = cursor1.getInt(0);	
			            double paymentAmount = cursor1.getDouble(1); 
			   		    int paymentMode = cursor1.getInt(2);	
			   		    int paymenthasBill = cursor1.getInt(3);	
//			   		    Log.v("mtest", "paymentAmount��ʷ"+paymentAmount);
			   		 
			   		  BigDecimal b2 = new BigDecimal(Double.toString(paymentAmount));
			          b1 = b1.add(b2); 
			          if (paymentMode == -1 ) {      //mark
				   	    	checkPayMode = 1;
				   	   }
			          
				      }
			        cursor1.close();
			        
			        double totalPayAmount = b1.doubleValue(); //�ܹ�֧���ܶ�
			        double remain = 0.0;//ʣ�µ�
			        
			        if (nbk_billAmountUnknown == 1) {
			        	
			        	if (curSize >0) {
			        		bMap.put("payState", 1);
			        		bMap.put("remain", 0);
						}else {
							
							if (billduedate < nowMillis) {
								bMap.put("payState", -1);
							} else {
								bMap.put("payState", 0);
							}
			        		bMap.put("remain", 0);
						}
						
					} else if ( checkPayMode ==1 ) {
						
						bMap.put("payState", 1);
						bMap.put("remain", 0);
						
					} else {
						
						 BigDecimal mb1 = new BigDecimal(Double.toString(billamount)); //������㾫��
						 BigDecimal mb2 = new BigDecimal(Double.toString(totalPayAmount)); //������㾫��
						 remain = (mb1.subtract(mb2)).doubleValue();
//						 Log.v("mtest", "billamount"+billamount);
//						 Log.v("mtest", "totalPayAmount����"+totalPayAmount);
//						 Log.v("mtest", "remain����"+remain);
						 
						 if (remain <= 0) {
							 bMap.put("payState", 1);
							 bMap.put("remain", 0);
						}else {
							
							if (billduedate < nowMillis) {
								bMap.put("payState", -1);
							} else {
								bMap.put("payState", 0);
							}
							bMap.put("remain", remain);
							
						}
					}
			        int mpayState = (Integer)bMap.get("payState");
			        
			        if (mpayState == -2) {
			        	
			        	if (billduedate < nowMillis) {
							bMap.put("payState", -1);
						} else {
							bMap.put("payState", 0);
						}
			        	bMap.put("remain", billamount);
						
					} 
			        
			        
				 if ( (billAutoPay == 1  && billduedate < (nowMillis+86399) && remain > 0 ) || (billAutoPay == 1  && billduedate < (nowMillis+86399) && nbk_billAmountUnknown == 1 && curSize == 0)) { //auto��bill����duedateС�ڵ����ʱ�򣬻���unkonwn �Զ�pay
					 long key;
//					 Log.v("mtest", "remainʣ��"+remain);
					 if (nbk_billAmountUnknown == 1) {
						 key = billPay( 0.00 ,2,billduedate,id);
					}else {
						 key = billPay(remain ,2,billduedate,id);
					}
					 bMap.put("payState", 1);
					 bMap.put("remain", 0);
				}
			        
			} else if (flag ==2) {
				
				
				if (billduedate < nowMillis) {
					bMap.put("payState", -1);
					bMap.put("remain", billamount);
				} else {
					bMap.put("payState", 0);
					bMap.put("remain", billamount);
				}
				
				if (billAutoPay == 1 && billduedate < (nowMillis+86399)) { //�ظ������¼����ڴ���
					
					bMap.put("payState", 1);
					
					
			            int obillObjecthasBill = (Integer)bMap.get("BK_Bill_Id");
			            double obillamount = (Double)bMap.get("nbillamount");
			            int obk_billAmountUnknown = (Integer)bMap.get("nbk_billAmountUnknown");
			            int obk_billAutoPay = (Integer)bMap.get("nbk_billAutoPay");
			            long obk_billDuedate = (Long) bMap.get("nbk_billDuedate");
			            long obk_billDuedateNew = 0;
			            long obk_billEndDate = (Long) bMap.get("nbk_billEndDate");
			            int obk_billisReminder = (Integer) bMap.get("nbk_billisReminder");
			            int obk_billisRepeat = (Integer) bMap.get("nbk_billisRepeat");
			            int obk_billisVariaable = (Integer) bMap.get("bk_billisVariaable");
			            long obk_billReminderDate = (Long) bMap.get("nbk_billReminderDate");
			            long bk_billReminderTime = (Long) bMap.get("bk_billReminderTime");
			            int obk_billRepeatNumber = (Integer) bMap.get("nbk_billRepeatNumber");
			            int obk_billRepeatType = (Integer) bMap.get("nbk_billRepeatType");
			            int billhasAccount = (Integer)bMap.get("billhasAccount");
			            
				 long Okey = insertObject( 0 , obillamount , obk_billAmountUnknown,obk_billAutoPay ,obk_billDuedate,obk_billDuedateNew,  //���������¼�
							obk_billEndDate, obk_billisReminder, obk_billisRepeat, obk_billisVariaable,obk_billReminderDate,bk_billReminderTime,obk_billRepeatNumber,
							obk_billRepeatType,obillObjecthasBill,billhasAccount);
				 if (obk_billAmountUnknown==1) {
					billPayO( 0.0, 2 ,obk_billDuedate ,Okey);
				} else {
					billPayO(obillamount,2 ,obk_billDuedate , Okey) ;
				}
				 int key = (int) Okey;
				 
				 bMap.put("remain", 0);
				 bMap.put("indexflag", 3);
				 bMap.put("BK_Bill_Id", key);
				}
				
			} else if (flag == 3) {
				
				String  sql = "select BK_Payment._id, BK_Payment.bk_payAmount, BK_Payment.bk_payMode ,BK_Payment.paymenthasBill from BK_Payment ,BK_BillObject where BK_Payment.paymenthasBillO = "+id;
				
				List<Map<String, Object>> pDataList = new ArrayList<Map<String, Object>>();
				Map<String, Object> map1;
				
				double zero= 0.00f; 
			    BigDecimal b1 = new BigDecimal(Double.toString(zero)); //������㾫��
			    int checkPayMode = 0;//�жϸ�pay�Ƿ��й�quickpay��mark,�й�һ�α�ʾ��bill�Ѿ�pay�꣬mark =1 quick =2;
			    int curSize;
				Cursor cursor1 = db.rawQuery(sql, null);
				curSize = cursor1.getCount();
			        while (cursor1.moveToNext()) {
			            
			            String paymentZ_PK = cursor1.getString(0);	
			            double paymentAmount = cursor1.getDouble(1); 
			   		    int paymentMode = cursor1.getInt(2);	
			   		    int paymenthasBill = cursor1.getInt(3);	
			   		    
			   		  BigDecimal b2 = new BigDecimal(Double.toString(paymentAmount));
			          b1 = b1.add(b2); 
			          if (paymentMode == -1) {      //mark
				   	    	checkPayMode = 1;
				   	   }
			          
				      }
			        cursor1.close();
			        
			        double totalPayAmount = b1.doubleValue(); //�ܹ�֧���ܶ�
			        double remain = 0.0;//ʣ�µ�
			        
			        if (nbk_billAmountUnknown == 1) {
			        	
			        	if (curSize >0) {
			        		bMap.put("payState", 1);
			        		bMap.put("remain", 0);
						}else {
							if (billduedate < nowMillis) {
								bMap.put("payState", -1);
							} else {
								bMap.put("payState", 0);
							}
			        		bMap.put("remain", 0);
						}
						
					} else if ( checkPayMode ==1 ) {
						
						bMap.put("payState", 1);
						bMap.put("remain", 0);
						
					} else {
						
						 BigDecimal mb1 = new BigDecimal(Double.toString(billamount)); //������㾫��
						 BigDecimal mb2 = new BigDecimal(Double.toString(totalPayAmount)); //������㾫��
						 remain = (mb1.subtract(mb2)).doubleValue();
						 
						 if (remain <= 0) {
							 bMap.put("payState", 1);
							 bMap.put("remain", 0);
						}else {
							
							if (billduedate < nowMillis) {
								bMap.put("payState", -1);
							} else {
								bMap.put("payState", 0);
							}
							 bMap.put("remain", remain);
							
						}
					}
			        int mpayState = (Integer)bMap.get("payState");
			        
			        if (mpayState == -2) {
			        	
			        	if (billduedate < nowMillis) {
							bMap.put("payState", -1);
						} else {
							bMap.put("payState", 0);
						}
			        	 bMap.put("remain", billamount);
						
					} 
			        
				 if ( (billAutoPay == 1  && billduedate < (nowMillis+86399) && remain > 0 ) || (billAutoPay == 1  && billduedate < (nowMillis+86399) && nbk_billAmountUnknown == 1 && curSize == 0)) { //auto��bill����duedateС�ڵ����ʱ�򣬻���unkonwn �Զ�pay
					 long key;
					 if (nbk_billAmountUnknown == 1) {
						 key = billPayO( 0.00 ,2,billduedate,id);
					}else {
						 key = billPayO(remain ,2,billduedate,id);
					}
					 bMap.put("payState", 1);
					 bMap.put("remain", 0);
				}
			}
			
		}//����ѭ������
		 db.close();
	}
 
 public long insertObject(int bk_billsDelete, double billamount ,int nbk_billAmountUnknown ,int bk_billAutoPay ,long bk_billDuedate,long  bk_billDuedateNew,  //���������¼�
		   					long bk_billEndDate, int bk_billisReminder, int bk_billisRepeat, int bk_billisVariaable,long bk_billReminderDate,long bk_billReminderTime,int bk_billRepeatNumber,
		   					int bk_billRepeatType,long billObjecthasBill,int billhasAccount) {
	 
    bksql = new BillKeeperSql(getActivity());
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
 
 public int getListPosition(List<String> tList,String tDate) { //�ж��ַ���������е�λ��
	
	 int position = -1 ;
	 for (int i = 0; i < tList.size(); i++) {
		 
		if (tList.get(i).equals(tDate)) {
			position = i;
			 break;
		}else{
			 position = -1 ;
		}
	}
	
	 return position;
}
 
 
 public long billPay(double payAmount,int payMode ,long payDate ,long paymenthasBill ) {
	 
     bksql = new BillKeeperSql(getActivity());
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
 
 public long billPayO(double payAmount,int payMode ,long payDate ,long paymenthasBill ) {
	 
     bksql = new BillKeeperSql(getActivity());
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
	public void getPaidData() { 
		//ɸѡbill��paid�����
		 mPaidList.clear();
		 Map<String, Object> map;
		 for (Map<String, Object> bMap:mDataList) {
			 int payState = (Integer) bMap.get("payState");
			 
			 if (payState ==1) {
			        
			        mPaidList.add(bMap); //listview���
			 }
			 
		 }
	}
	
	public void getUnpaidData() { // ɸѡbill��Unpaid����ݣ�Ч�ʻ�Ƚϵ�
		 mUnpaidList.clear();
		 
		 Map<String, Object> map;
		 for (Map<String, Object> bMap:mDataList) {
			 int payState = (Integer) bMap.get("payState");
			 if (payState == 0 || payState == -1 || payState==-2) {
				 
//				 map =  new HashMap<String, Object>();
//				 int BK_Bill_Id = (Integer)bMap.get("BK_Bill_Id");
//		         double billamount = (Double)bMap.get("nbillamount");
//		         int bk_billAmountUnknown = (Integer)bMap.get("nbk_billAmountUnknown");
//		         int bk_billAutoPay = (Integer)bMap.get("nbk_billAutoPay");
//		         long bk_billDuedate = (Long)bMap.get("nbk_billDuedate"); //�൱���¼��Ŀ�ʼ����
//		         long bk_billEndDate =  (Long)bMap.get("nbk_billEndDate"); //�ظ��¼��Ľ�ֹ����
//		         int bk_billisReminder = (Integer)bMap.get("nbk_billisReminder");
//		         int bk_billisRepeat = (Integer)bMap.get("nbk_billisRepeat");
//		         int bk_billisVariaable = (Integer)bMap.get("bk_billisVariaable");
//		         long bk_billReminderDate = (Long)bMap.get("nbk_billReminderDate");
//		         int bk_billRepeatNumber = (Integer)bMap.get("nbk_billRepeatNumber"); 
//		         int bk_billRepeatType = (Integer)bMap.get("nbk_billRepeatType"); 
//		         int billhasAccount = (Integer)bMap.get("billhasAccount");
//		         String bk_accountName = (String)bMap.get("bk_accountName");
//		         int accounthasCategory = (Integer)bMap.get("accounthasCategory");
//		         int indexflag = (Integer)bMap.get("indexflag"); 
//				 
//		         map.put("BK_Bill_Id", BK_Bill_Id);  
//		         map.put("nbk_billAmountUnknown", bk_billAmountUnknown);
//		         map.put("nbk_billAutoPay", bk_billAutoPay);
//		         map.put("nbk_billDuedate", bk_billDuedate);
//		         map.put("nbk_billEndDate", bk_billEndDate);
//		         map.put("nbk_billisReminder", bk_billisReminder);
//		         map.put("nbk_billisRepeat", bk_billisRepeat);
//		         map.put("bk_billisVariaable", bk_billisVariaable);
//		         map.put("nbk_billReminderDate", bk_billReminderDate);
//		         map.put("nbk_billRepeatNumber", bk_billRepeatNumber);
//		         map.put("nbk_billRepeatType", bk_billRepeatType);
//		         map.put("accounthasCategory", accounthasCategory);
//		         map.put("billhasAccount", billhasAccount);
//		         map.put("bk_accountName", bk_accountName);
//		         map.put("payState", payState);
//		         map.put("indexflag", indexflag); 
//	 	            //1��ʾ�����¼�
//				 double remain = (Double)bMap.get("remain");
//				 map.put("nbillamount", remain);
			     mUnpaidList.add(bMap);
			     
			 }
		 }
	}
	
	public String getFirstDayOfMonth(int year, int month) {   //��ȡĳ��ĳ�µĵ�һ��ĺ�����
        Calendar cal = Calendar.getInstance();   
        cal.set(Calendar.YEAR, year);   
        cal.set(Calendar.MONTH, month-1);
        
        cal.set(Calendar.DAY_OF_MONTH,cal.getMinimum(Calendar.DATE));
        
      String fdateString = new SimpleDateFormat( "yyyy-MM-dd").format(cal.getTime());
        return  fdateString;
    } 
	
	public static long getFirstDayOfMonthMillis(int year, int month) {   //��ȡĳ��ĳ�µĵ�һ��ĺ�����
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
	
	 public static long getLastDayOfMonthMillis(int year, int month) {   //��ȡĳ��ĳ�µ����һ��ĺ�����
	        Calendar cal = Calendar.getInstance();   
	        cal.set(Calendar.YEAR, year);   
	        cal.set(Calendar.MONTH, month-1); 
//	        cal.set(Calendar.HOUR, 23);
//	        cal.set(Calendar.MINUTE, 59);
//	        cal.set(Calendar.SECOND, 59);
	        
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
	
	
	class DropDownListenser implements OnNavigationListener  
    {  
  
        public boolean onNavigationItemSelected(int itemPosition, long itemId)  
        
        {
        	
        	if (itemPosition == 1 ) {
        		
        		mRelativeLayout.setVisibility(View.VISIBLE);
        		mListView.setVisibility(View.VISIBLE);
        		mExpandableListView.setVisibility(View.GONE);
				
			} else if (itemPosition == 2 ) {
				
				mRelativeLayout.setVisibility(View.GONE);
        		mListView.setVisibility(View.GONE);
        		mExpandableListView.setVisibility(View.VISIBLE);
        		
			}else if (itemPosition == 4) { //all
				
				checkList =0;
//				items.clear();
//				for(Map<String, Object> bMap:mDataList){ 
//			   		long billduedate = (Long) bMap.get("nbk_billDuedate");
//			   		String dateString = getMilltoDate(billduedate);
//			   		items.add(dateString);
//			   	 }
//				
////				adapter.setItems(items);
//				adapter.setShowDay(selectedGridDate, true);
//				adapter.notifyDataSetChanged();
				
				calendarPayList.clear();
				ArrayList<Long> mlist = new ArrayList(); //����������������
				
					for(Map<String, Object> bMap:mDataList){ 
				   		 
				   		long billduedate = (Long) bMap.get("nbk_billDuedate");
				   		mlist.add(billduedate);
				   		
//				   		String dateString = getMilltoDate(billduedate);
////				   		itemvalue = df.format(itemmonth.getTime());
////						itemmonth.add(GregorianCalendar.DATE, 1);
//				   		items.add(dateString);
				   	 }
					
					  Iterator it1 = mlist.iterator();  //ʹ�õ������ȥ��items���ظ�����,ͬһ���¼��϶��¿����������ʾЧ��
					  Hashtable ht = new Hashtable();
					  while(it1.hasNext()){
					   Object obj = it1.next();
					   ht.put(obj, obj);
					  }
					  
					  Iterator it2 = ht.keySet().iterator();
					  ArrayList<Long> list = new ArrayList();
					  while(it2.hasNext()){
					   list.add((Long) it2.next());
					  }
//					  Log.v("mdb", "ɸѡ�������"+list);
					  Map<String, Object> cMap;
					  for (int i = 0; i < list.size(); i++) { //�жϵ����Ƿ�����ȫ��֧����
						  
						  cMap = new HashMap<String, Object>();
						  long date = list.get(i);
						  int paidMent = -5;
						  int unpaidMent = -5;
						  int finalPaid = -5;
						  
						  for(Map<String, Object> bMap:mDataList){ 
							  long billduedate = (Long) bMap.get("nbk_billDuedate");
							  int payState = (Integer)bMap.get("payState");
							  
						   		 if (date == billduedate) {
									if (payState == 1) {
										paidMent =1;
									}
									if (payState == -2 || payState == -1 || payState == 0) {
										unpaidMent = 1;
									}
								}
						  }

						  if (unpaidMent==1) {
							  finalPaid = 0;
						} else if (paidMent == 1 && unpaidMent == -5) {
							  finalPaid = 1;
						}
						   	cMap.put("dayItem", getMilltoDate(date));
							cMap.put("payStatement", finalPaid);
						   	calendarPayList.add(cMap);	 
					}
					  
//					adapter.setShowDay(selectedGridDate, true); //�÷�������ÿ�µĳ�ʼЧ��,���ڲ�����߼���bug
//					adapter.setItems(items);
					adapter.setSelPositon(gridviewPosition);
				    adapter.setPaidDate(calendarPayList);
					
					adapter.notifyDataSetChanged();
					

			    duecheck = -1;
				refreshExpandableListview(mDataList);
//				mListviewAdapter.setAdapterList(mListViewData);// listview
//				mListviewAdapter.notifyDataSetChanged();
				refreshListview(selectedGridDate, mDataList);
//				Log.v("mdb", "ѡ���ʱ��"+selectedGridDate);
//				Log.v("mdb", "���µ����"+mDataList);
				
			}else if (itemPosition == 5 ) { //paid
				checkList =1;
				items.clear();
				getPaidData();
				
//				for(Map<String, Object> bMap:mPaidList){ 
//			   		long billduedate = (Long) bMap.get("nbk_billDuedate");
//			   		String dateString = getMilltoDate(billduedate);
//			   		items.add(dateString);
//			   	 }
//				
////				adapter.setItems(items);
//				adapter.setShowDay(selectedGridDate, true);
//				adapter.notifyDataSetChanged();
				
				calendarPayList.clear();
				ArrayList<Long> mlist = new ArrayList(); //����������������
				
					for(Map<String, Object> bMap:mPaidList){ 
				   		 
				   		long billduedate = (Long) bMap.get("nbk_billDuedate");
				   		mlist.add(billduedate);
				   		
//				   		String dateString = getMilltoDate(billduedate);
////				   		itemvalue = df.format(itemmonth.getTime());
////						itemmonth.add(GregorianCalendar.DATE, 1);
//				   		items.add(dateString);
				   	 }
					
					  Iterator it1 = mlist.iterator();  //ʹ�õ������ȥ��items���ظ�����,ͬһ���¼��϶��¿����������ʾЧ��
					  Hashtable ht = new Hashtable();
					  while(it1.hasNext()){
					   Object obj = it1.next();
					   ht.put(obj, obj);
					  }
					  
					  Iterator it2 = ht.keySet().iterator();
					  ArrayList<Long> list = new ArrayList();
					  while(it2.hasNext()){
					   list.add((Long) it2.next());
					  }
//					  Log.v("mdb", "ɸѡ�������"+list);
					  Map<String, Object> cMap;
					  for (int i = 0; i < list.size(); i++) { //�жϵ����Ƿ�����ȫ��֧����
						  
						  cMap = new HashMap<String, Object>();
						  long date = list.get(i);
						  int paidMent = -5;
						  int unpaidMent = -5;
						  int finalPaid = -5;
						  
						  for(Map<String, Object> bMap:mPaidList){ 
							  long billduedate = (Long) bMap.get("nbk_billDuedate");
							  int payState = (Integer)bMap.get("payState");
							  
						   		 if (date == billduedate) {
									if (payState == 1) {
										paidMent =1;
									}
									if (payState == -2 || payState == -1 || payState == 0) {
										unpaidMent = 1;
									}
								}
						  }

						  if (unpaidMent==1) {
							  finalPaid = 0;
						} else if (paidMent == 1 && unpaidMent == -5) {
							  finalPaid = 1;
						}
						   	cMap.put("dayItem", getMilltoDate(date));
							cMap.put("payStatement", finalPaid);
						   	calendarPayList.add(cMap);	 
					}
					  
//					adapter.setShowDay(selectedGridDate, true); //�÷�������ÿ�µĳ�ʼЧ��,���ڲ�����߼���bug
//					adapter.setItems(items);
					 adapter.setSelPositon(gridviewPosition);
					adapter.setPaidDate(calendarPayList);
					adapter.notifyDataSetChanged();

			    duecheck = 0 ;
				refreshExpandableListview(mPaidList);
			
				refreshListview(selectedGridDate, mPaidList);
			}else if (itemPosition == 6 ) {//unpaid
				
				checkList = 2;
				items.clear();
				getUnpaidData();
				
//				for(Map<String, Object> bMap:mUnpaidList){ 
//			   		long billduedate = (Long) bMap.get("nbk_billDuedate");
//			   		String dateString = getMilltoDate(billduedate);
//			   		items.add(dateString);
//			   	 }
//				
////				adapter.setItems(items);
//				adapter.setShowDay(selectedGridDate, true);
//				adapter.notifyDataSetChanged();
				
				calendarPayList.clear();
				ArrayList<Long> mlist = new ArrayList(); //����������������
				
					for(Map<String, Object> bMap:mUnpaidList){ 
				   		 
				   		long billduedate = (Long) bMap.get("nbk_billDuedate");
				   		mlist.add(billduedate);
				   		
//				   		String dateString = getMilltoDate(billduedate);
////				   		itemvalue = df.format(itemmonth.getTime());
////						itemmonth.add(GregorianCalendar.DATE, 1);
//				   		items.add(dateString);
				   	 }
					
					  Iterator it1 = mlist.iterator();  //ʹ�õ������ȥ��items���ظ�����,ͬһ���¼��϶��¿����������ʾЧ��
					  Hashtable ht = new Hashtable();
					  while(it1.hasNext()){
					   Object obj = it1.next();
					   ht.put(obj, obj);
					  }
					  
					  Iterator it2 = ht.keySet().iterator();
					  ArrayList<Long> list = new ArrayList();
					  while(it2.hasNext()){
					   list.add((Long) it2.next());
					  }
//					  Log.v("mdb", "ɸѡ�������"+list);
					  Map<String, Object> cMap;
					  for (int i = 0; i < list.size(); i++) { //�жϵ����Ƿ�����ȫ��֧����
						  
						  cMap = new HashMap<String, Object>();
						  long date = list.get(i);
						  int paidMent = -5;
						  int unpaidMent = -5;
						  int finalPaid = -5;
						  
						  for(Map<String, Object> bMap:mUnpaidList){ 
							  long billduedate = (Long) bMap.get("nbk_billDuedate");
							  int payState = (Integer)bMap.get("payState");
							  
						   		 if (date == billduedate) {
									if (payState == 1) {
										paidMent =1;
									}
									if (payState == -2 || payState == -1 || payState == 0) {
										unpaidMent = 1;
									}
								}
						  }

						  if (unpaidMent==1) {
							  finalPaid = 0;
						} else if (paidMent == 1 && unpaidMent == -5) {
							  finalPaid = 1;
						}
						   	cMap.put("dayItem", getMilltoDate(date));
							cMap.put("payStatement", finalPaid);
						   	calendarPayList.add(cMap);	 
					}
					  
//					adapter.setShowDay(selectedGridDate, true); //�÷�������ÿ�µĳ�ʼЧ��,���ڲ�����߼���bug
//					adapter.setItems(items);
					adapter.setSelPositon(gridviewPosition);
					adapter.setPaidDate(calendarPayList);
					adapter.notifyDataSetChanged();
				
//				Log.v("mdb", "unpaidlist��"+mUnpaidList);

			    duecheck = 1 ;
				refreshExpandableListview(mUnpaidList);
				refreshListview(selectedGridDate, mUnpaidList);
			}
			return true;  
        }  
    }  
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {  //�������غ�����д�ȷ�� ********
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 2:
			 if (data != null) {
				 handler.post(calendarUpdater);
			}
			break;
		case 10:
		 if (data != null) {
			 handler.post(calendarUpdater);
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
		super.onCreateOptionsMenu(menu, inflater);
	}
}
