package com.appxy.billkeeper.adapter;

import java.text.BreakIterator;
import java.util.ArrayList;
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
public class CalendarFragmentExpandableListviewAdapter extends BaseExpandableListAdapter{
	
	private LayoutInflater inflater;
    private List<Map<String, Object>> groupList;
	private List<List<Map<String, Object>>> childList;
	private int duecheck = -1;
 
    public CalendarFragmentExpandableListviewAdapter(Context context,List<Map<String, Object>> groupList, List<List<Map<String, Object>>> childList) {
    	
        inflater = LayoutInflater.from(context);
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
			 convertView = inflater.inflate(R.layout.fragment_calendar_expandablelistview_item, parent,false);
			 viewholder.dateTextView = (TextView)convertView.findViewById(R.id.dateTextView);
			 convertView.setTag(viewholder);
        }else
         {
        	 viewholder = (gViewHolder)convertView.getTag();
         }
		viewholder.dateTextView.setText(groupList.get(groupPosition).get("mDuedate")+"");
		
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
			 convertView = inflater.inflate(R.layout.fragment_calendar_expand_group, parent,false);
			 
			viewholder.category_imageView = (ImageView) convertView.findViewById(R.id.category_imageView);
			viewholder.account_name_textView = (TextView) convertView.findViewById(R.id.account_name_textView);
			viewholder.currency_textView = (TextView) convertView.findViewById(R.id.currency_textView);
			viewholder.amount_textView = (TextView) convertView.findViewById(R.id.amount_textView);
			viewholder.payment_state_imageView = (ImageView) convertView.findViewById(R.id.payment_state_imageView);
			viewholder.mline_label = (View) convertView.findViewById(R.id.mline_label);
			 
			 convertView.setTag(viewholder);
	        }else
	         {
	        	 viewholder = (cViewHolder)convertView.getTag();
	        	 
	         }
		    viewholder.account_name_textView.setText(childList.get(groupPosition).get(childPosition).get("bk_accountName")+"");
		    viewholder.category_imageView.setImageResource(Common.CATEGORYICON[(Integer) (childList.get(groupPosition).get(childPosition).get("bk_categoryIconName"))]);
			viewholder.currency_textView.setText( Common.CURRENCY_SIGN[Common.CURRENCY]);
			
			if ((Integer)childList.get(groupPosition).get(childPosition).get("nbk_billAmountUnknown") == 1) {
				viewholder.currency_textView.setVisibility(View.INVISIBLE);
				viewholder.amount_textView.setText("N/A");
			} else {
				viewholder.currency_textView.setVisibility(View.VISIBLE);
				if (duecheck == 1) {
					viewholder.amount_textView.setText( Common.doublepoint2str( Double.parseDouble(childList.get(groupPosition).get(childPosition).get("remain")+"")));
				} else {
					viewholder.amount_textView.setText( Common.doublepoint2str( Double.parseDouble(childList.get(groupPosition).get(childPosition).get("nbillamount")+"")));
				}
			}
			
			int paystate = (Integer) childList.get(groupPosition).get(childPosition).get("payState");
			if (paystate == -1) {
				viewholder.currency_textView.setTextColor(Color.rgb(225, 12, 12));
				viewholder.amount_textView.setTextColor(Color.rgb(225, 12, 12));
				viewholder.payment_state_imageView.setVisibility(View.INVISIBLE);
			} else if (paystate == 0) {
				viewholder.payment_state_imageView.setVisibility(View.INVISIBLE);
				viewholder.currency_textView.setTextColor(R.color.text_gray);
				viewholder.amount_textView.setTextColor(R.color.text_gray);
			}else if (paystate == 1) {
				viewholder.payment_state_imageView.setVisibility(View.VISIBLE);
				viewholder.payment_state_imageView.setImageResource(R.drawable.paid_icon);
				viewholder.currency_textView.setTextColor(R.color.text_gray);
				viewholder.amount_textView.setTextColor(R.color.text_gray);
			}
			
			if (childPosition == 0) {
				viewholder.mline_label.setVisibility(View.INVISIBLE);
			} else {
				viewholder.mline_label.setVisibility(View.VISIBLE);
			}
		return convertView;
	}

	public class cViewHolder {
		public ImageView category_imageView;
		public TextView account_name_textView;
		public TextView currency_textView;
		public TextView amount_textView;
		public ImageView payment_state_imageView;
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
		public TextView dateTextView;
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
