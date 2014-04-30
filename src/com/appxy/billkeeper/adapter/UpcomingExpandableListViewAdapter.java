package com.appxy.billkeeper.adapter;

import java.text.BreakIterator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.appxy.billkeeper.R;
import com.appxy.billkeeper.entity.Common;

import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("ResourceAsColor")
public class UpcomingExpandableListViewAdapter extends BaseExpandableListAdapter{
	
	private LayoutInflater inflater;
    private List<Map<String, Object>> groupList;
	private List<List<Map<String, Object>>> childList;
	private int duecheck = -1;
 
    public UpcomingExpandableListViewAdapter(Context context) {
    	
        inflater = LayoutInflater.from(context);
	}
    
    public void setAdapterData(List<Map<String, Object>> groupList, List<List<Map<String, Object>>> childList){
    	
    	this.groupList = groupList;
        this.childList = childList;
    }

    public List<List<Map<String, Object>>> getAdapterDate() {
		return this.childList;
	}
    
    public void  setDuecheck(int check) {
		this.duecheck = check;
	}

	@Override
	 //gets the title of each parent/group
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	//counts the number of group/parent items so the list knows how many times calls getGroupView() method
	public int getGroupCount() {
		// TODO Auto-generated method stub
		if (groupList == null) {
			return 0;
		}
		return groupList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}
	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		gViewHolder viewholder = null;
		if (convertView == null) {
			 viewholder = new gViewHolder();
			 convertView = inflater.inflate(R.layout.upcoming_group, parent,false);
			 viewholder.dueTextView = (TextView)convertView.findViewById(R.id.dueTextView);
			 viewholder.dueCountTextView = (TextView)convertView.findViewById(R.id.dueCountTextView);
			 convertView.setTag(viewholder);
        }else
         {
        	 viewholder = (gViewHolder)convertView.getTag();
         }
		viewholder.dueTextView.setText(groupList.get(groupPosition).get("dueString")+"");
		viewholder.dueCountTextView.setText(groupList.get(groupPosition).get("dueCount")+"");
		
		convertView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
	    });
		
		return convertView;
	}
	
	@Override
	 //gets the name of each item
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}
	
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		cViewHolder viewholder = null;
		 if (convertView == null) {
			 viewholder = new cViewHolder();
			 convertView = inflater.inflate(R.layout.upcoming_child, parent,false);
			 
			 viewholder.colorLabel = (ImageView) convertView
						.findViewById(R.id.dotimageView);
				
				viewholder.bNametextView = (TextView) convertView
						.findViewById(R.id.billnametextView);
				
				viewholder.mDateTextView = (TextView) convertView
				.findViewById(R.id.datetextView);
				
				viewholder.currencyImageView = (TextView) convertView
				.findViewById(R.id.currencyImageView1);
				
				viewholder.amountTextView = (TextView) convertView
				.findViewById(R.id.amounttextView);
				
				viewholder.autoImageView = (ImageView) convertView
				.findViewById(R.id.autorepeatimageView);
				
				viewholder.mline_label = (View) convertView
						.findViewById(R.id.mline_label);
			 
			 convertView.setTag(viewholder);
	        }else
	         {
	        	 viewholder = (cViewHolder)convertView.getTag();
	        	 
	         }
		 
		    viewholder.colorLabel.setImageResource(Common.CATEGORYICON[(Integer) (childList.get(groupPosition).get(childPosition).get("bk_categoryIconName"))]);
			
			viewholder.bNametextView.setText((String) childList.get(groupPosition).get(childPosition).get("bk_accountName"));
			viewholder.mDateTextView.setText((String) childList.get(groupPosition).get(childPosition).get("mDate"));
			viewholder.currencyImageView.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
			
			if ((Integer)childList.get(groupPosition).get(childPosition).get("nbk_billAmountUnknown") == 1) {
				viewholder.amountTextView.setText("N/A"); 
				viewholder.currencyImageView.setVisibility(View.INVISIBLE);
			} else {
				viewholder.amountTextView.setText( Common.doublepoint2str(Double.parseDouble(childList.get(groupPosition).get(childPosition).get("nbillamount")+""))); 
				viewholder.currencyImageView.setVisibility(View.VISIBLE);
			}
			
			if ((Long)childList.get(groupPosition).get(childPosition).get("nbk_billDuedate") <getNowMillis() ) {
				viewholder.amountTextView.setTextColor(Color.rgb(225, 12, 12));
				viewholder.currencyImageView.setTextColor(Color.rgb(225, 12, 12));
			} else {
				viewholder.amountTextView.setTextColor(R.color.text_gray);
				viewholder.currencyImageView.setTextColor(R.color.text_gray);
			}
			
			if ((Integer)childList.get(groupPosition).get(childPosition).get("nbk_billAutoPay")==1) {
				viewholder.autoImageView.setVisibility(View.VISIBLE);
				viewholder.autoImageView.setImageResource(R.drawable.auto_pay);
			} else{
				viewholder.autoImageView.setVisibility(View.INVISIBLE);
			}
			
			if (childPosition == 0) {
				viewholder.mline_label.setVisibility(View.INVISIBLE);
			} else {
				viewholder.mline_label.setVisibility(View.VISIBLE);
			}
		 
		return convertView;
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

	public class cViewHolder {
		
		public ImageView colorLabel;
		public TextView bNametextView;
		public TextView mDateTextView;
		public TextView currencyImageView;
		public TextView amountTextView;
		public ImageView autoImageView;
		public View mline_label;
	}
	
	@Override
	//counts the number of children items so the list knows how many times calls getChildView() method
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		 
		if (childList == null) {
			return 0;
		}
			 return childList.get(groupPosition).size();
	    
		
	}
	
	class gViewHolder {
		public TextView dueTextView;
		public TextView dueCountTextView;
	}
	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

}
