package com.appxy.billkeeper.adapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.appxy.billkeeper.R;
import com.appxy.billkeeper.adapter.AccountDetailActivityListViewAdapter.ViewHolder;

import android.R.integer;
import android.R.string;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.ContactsContract.Contacts.Data;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("ResourceAsColor")
public class CalendarAdapter extends BaseAdapter {
	private Context mContext;

	private Calendar month;
	public GregorianCalendar pmonth; // calendar instance for previous month
	/**
	 * calendar instance for previous month for getting complete view
	 */
	public GregorianCalendar pmonthmaxset;
	private GregorianCalendar selectedDate;
	int firstDay;
	int maxWeeknumber;
	int maxP;
	int calMaxP;
	int lastWeekDay;
	int leftDays;
	int mnthlength;
	String itemvalue, curentDateString;
	DateFormat df;

	private ArrayList<String> items;
	public static List<String> dayString;
	private View previousView;
	private View firstdatView;
	private String firstDayofMonthString;
	private String mTodayDate;
	private List<Map<String, Object>> payDataList;

	private String showDay; //���º��¸��µĵ�һ�죬���µĵ���
	public  boolean showDayCheck = true; //ִֻ��һ��
    private long NowMillis;
	private int mSelPosition;
    
	public CalendarAdapter(Context c, GregorianCalendar monthCalendar) {
		
		payDataList = new ArrayList<Map<String, Object>>(); 
		CalendarAdapter.dayString = new ArrayList<String>();
		Locale.setDefault(Locale.ENGLISH);////////
		month = monthCalendar;
		selectedDate = (GregorianCalendar) monthCalendar.clone();
		mContext = c;
		month.set(GregorianCalendar.DAY_OF_MONTH, 1);
		this.items = new ArrayList<String>();
		df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		curentDateString = df.format(selectedDate.getTime());
		refreshDays();
		getFirstDayOfMonth();
		NowMillis = getNowMillis();
	}

	public void getFirstDayOfMonth() {   //��ȡĳ��ĳ�µĵ�һ��ĺ�����
		Calendar cal = Calendar.getInstance();   
		String mCurrDate = new SimpleDateFormat( "yyyy-MM-dd").format(cal.getTime());
		String[] separatedTime = mCurrDate.split("-");
		mTodayDate = separatedTime[2];
		cal.set(Calendar.DAY_OF_MONTH,cal.getMinimum(Calendar.DATE));
		firstDayofMonthString = new SimpleDateFormat( "yyyy-MM-dd").format(cal.getTime());
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
	
	public long getStringtoDate(String day) {//������ת���ɹ̶���ʽ��������
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(day));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c.getTimeInMillis();

	}

	public void setItems(ArrayList<String> items) {
		
		for (int i = 0; i != items.size(); i++) {
			if (items.get(i).length() == 1) {
				items.set(i, "0" + items.get(i));
			}
		}
		this.items = items;

		//		notifyDataSetChanged();
	}
	
	public void setPaidDate(List<Map<String, Object>> paydata) {
//		payDataList.clear();
//		items.clear();
		this.payDataList = paydata;
		this.items.clear();
		for (Map<String, Object> iMap:paydata) {
			String dayItem = (String) iMap.get("dayItem");
			this.items.add(dayItem);
		}
	}
	
	public int getCount() {
		Log.v("mtake", "dayString:"+dayString);
		return dayString.size();
	}

	public List<String> getDayStrings(){
		
		return dayString;
	}
	
	public void setSelPositon(int selPositon) {
		mSelPosition = selPositon;
		Log.v("calx", "gridviewPosition���뵽adapter:= "+mSelPosition);
	}
	
	public Object getItem(int position) {
		return dayString.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	// create a new view for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView dayView;
		ViewHolder viewholder = null;
		
		if (convertView == null) { // if it's not recycled, initialize some
			// attributes
			LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.fragment_calender_item, null);
			viewholder = new ViewHolder();
			
			viewholder.dayView = (TextView) convertView.findViewById(R.id.date);
			viewholder.iw = (ImageView) convertView.findViewById(R.id.date_icon_red);
			viewholder.LinearLayout1 = (LinearLayout)convertView.findViewById(R.id.LinearLayout1);
			convertView.setTag(viewholder);
		}else {
			viewholder = (ViewHolder) convertView.getTag();
		}
		
		// separates daystring into parts.
		String[] separatedTime = dayString.get(position).split("-");
		// taking last part of date. ie; 2 from 2012-12-02
		String gridvalue = separatedTime[2].replaceFirst("^0*", "");
		String mCurString = separatedTime[2];
		// checking whether the day is in current month or not.
		if ((Integer.parseInt(gridvalue) > 1) && (position < firstDay)) {
			// setting offdays to white color.
			viewholder.dayView.setTextColor(Color.rgb(204, 199, 192));
			viewholder.dayView.setClickable(false);
			viewholder.dayView.setFocusable(false);
		} else if ((Integer.parseInt(gridvalue) < 7) && (position > 28)) {
			viewholder.dayView.setTextColor(Color.rgb(204, 199, 192)); //��ɫ
			viewholder.dayView.setClickable(false);
			viewholder.dayView.setFocusable(false);
		} else {
			// setting curent month's days in blue color.
			if ( curentDateString.equals(dayString.get(position))) {
				viewholder.dayView.setTextColor(Color.rgb(87, 192, 252)); //�������ɫֵ
			} else {
				viewholder.dayView.setTextColor(Color.rgb(100, 100, 100)); //��ɫ
			}
		}
		
//		if (dayString.get(position).equals(showDay)) {
//
//				Log.v("mdb", "showDay�жϵĵط�"+showDay+"showDayCheck"+showDayCheck);
//				setSelected(v);
//				convertView.setBackgroundResource(R.drawable.calendar_cel_selectl);
//			    viewholder.LinearLayout1.setBackgroundResource(R.drawable.calendar_cel_selectl);
//				previousView = convertView;
//				showDayCheck = false;
//				showDay = "";
//
//		}else {
//			convertView.setBackgroundResource(R.drawable.list_item_background);
//		}

//				if (dayString.get(position).equals(curentDateString)) {
//					
//						setSelected(v);
//						previousView = v;
//					
//				} else if ((firstDay-1) ==position && !dayString.get(position).equals(firstDayofMonthString)) {
//						setSelected(v);
////						v.setBackgroundResource(R.drawable.calendar_cel_selectl);
//						previousView = v;
//					
//				} else {
//					v.setBackgroundResource(R.drawable.list_item_background);
//				}
				
//		if (dayString.get(position).equals(curentDateString) ) {
//			
//				convertView.setBackgroundResource(R.drawable.calendar_cel_selectl);
//				previousView = convertView;
//			
//		} else if ((firstDay-1) ==position && !dayString.get(position).equals(firstDayofMonthString)) {
//				convertView.setBackgroundResource(R.drawable.calendar_cel_selectl);
//				firstdatView =convertView;
//		} else {
//			convertView.setBackgroundResource(R.drawable.list_item_background);
//		}
		
		Log.v("calx", "gridviewPositionʹ��ʱ:= "+mSelPosition);
		if (mSelPosition == position) {
			convertView.setBackgroundResource(R.drawable.calendar_cel_selectl);
			previousView = convertView;
		} else {
			convertView.setBackgroundResource(R.drawable.list_item_background);
		}
		
		
		viewholder.dayView.setText(gridvalue);
		// create date string for comparison
		String date = dayString.get(position);

		if (date.length() == 1) {
			date = "0" + date;
		}
		String monthStr = "" + (month.get(GregorianCalendar.MONTH) + 1);
		if (monthStr.length() == 1) {
			monthStr = "0" + monthStr;
		}

		// show icon if date is not empty and it exists in the items array
//		Log.v("mdb", "items:"+items);
//		Log.v("mdb", "date:"+date);
		
		if (date.length() > 0 && items != null && items.contains(date)) { //**********************
			int paySta = -1;
			
			for (Map<String, Object> iMap:payDataList) {
				String dayItem = (String) iMap.get("dayItem");
				if (dayItem.equals(date) ) {
					int payStatement = (Integer) iMap.get("payStatement");
					paySta = payStatement;
				}
			}
			
//			Log.v("mdb", "pay״ֵ̬"+paySta);
			if (paySta == 1 ) {
				viewholder.iw .setVisibility(View.VISIBLE);
				viewholder.iw.setImageResource(R.drawable.green_circle);
				viewholder.dayView.setTextColor(Color.rgb(52, 209, 66));
			} else {
				viewholder.iw .setVisibility(View.VISIBLE);
				if (getStringtoDate(date)<NowMillis) {
					viewholder.iw.setImageResource(R.drawable.red_circle);
					viewholder.dayView.setTextColor(Color.rgb(225, 12, 12));
				} else {
					viewholder.iw.setImageResource(R.drawable.gray_circle);
				} 
			}
			
		} else {
			viewholder.iw .setVisibility(View.INVISIBLE);
		}

		return convertView;
	}
	
	 public class ViewHolder {
		    public TextView dayView;
			public ImageView iw;
			public LinearLayout LinearLayout1;
		}
	 

	public View setSelected(View view) {
		if (previousView != null) {
			previousView.setBackgroundResource(R.drawable.list_item_background);
		}
//		if (firstdatView !=null) {
//		    firstdatView.setBackgroundResource(R.drawable.list_item_background);
//					}
		previousView = view;
		view.setBackgroundResource(R.drawable.calendar_cel_selectl);
		return view;
	}

	public void setShowDay(String show,Boolean showCheck) {
		this.showDay = show;
		this.showDayCheck = showCheck;
		Log.v("mdb", "���ܵ������ڣ�"+showDay+"��ֵ��check��"+showDayCheck);
	}

	//	public View setSelected(View view ) {
	//		
	//		if (firstdatView !=null) {
	//			firstdatView.setBackgroundResource(R.drawable.list_item_background);
	//		}
	//		
	//		if (previousView != null) {
	//			
	//				previousView.setBackgroundResource(R.drawable.list_item_background);
	//		}
	////		if (!today.equals(curentDateString)) {
	////			previousView = view;
	////		}
	////		
	////			if (today.equals(curentDateString)) {
	////				view.setBackgroundResource(R.drawable.calendar_cel_selectl2);
	////			}else {
	//				view.setBackgroundResource(R.drawable.calendar_cel_selectl);
	////			}
	//		return view;
	//	}

	public void refreshDays() {
		// clear items
		items.clear();
		dayString.clear();
		Locale.setDefault(Locale.ENGLISH);
		pmonth = (GregorianCalendar) month.clone();
		// month start day. ie; sun, mon, etc
		firstDay = month.get(GregorianCalendar.DAY_OF_WEEK);
		// finding number of weeks in current month.
		maxWeeknumber = month.getActualMaximum(GregorianCalendar.WEEK_OF_MONTH);
		// allocating maximum row number for the gridview.
		mnthlength = maxWeeknumber * 7;
		maxP = getMaxP(); // previous month maximum day 31,30....
		calMaxP = maxP - (firstDay - 1);// calendar offday starting 24,25 ...
		/**
		 * Calendar instance for getting a complete gridview including the three
		 * month's (previous,current,next) dates.
		 */
		pmonthmaxset = (GregorianCalendar) pmonth.clone();
		/**
		 * setting the start date as previous month's required date.
		 */
		pmonthmaxset.set(GregorianCalendar.DAY_OF_MONTH, calMaxP + 1);

		/**
		 * filling calendar gridview.
		 */
		Log.v("mmes", "mnthlength:"+mnthlength);
		for (int n = 0; n < mnthlength; n++) {

			itemvalue = df.format(pmonthmaxset.getTime());
			pmonthmaxset.add(GregorianCalendar.DATE, 1);
			dayString.add(itemvalue);

		}
	}

	private int getMaxP() {
		int maxP;
		if (month.get(GregorianCalendar.MONTH) == month
				.getActualMinimum(GregorianCalendar.MONTH)) {
			pmonth.set((month.get(GregorianCalendar.YEAR) - 1),
					month.getActualMaximum(GregorianCalendar.MONTH), 1);
		} else {
			pmonth.set(GregorianCalendar.MONTH,
					month.get(GregorianCalendar.MONTH) - 1);
		}
		maxP = pmonth.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);

		return maxP;
	}

}