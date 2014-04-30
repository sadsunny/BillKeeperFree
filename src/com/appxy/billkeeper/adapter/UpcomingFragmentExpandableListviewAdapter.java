package com.appxy.billkeeper.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.appxy.billkeeper.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

public class UpcomingFragmentExpandableListviewAdapter extends BaseExpandableListAdapter{
	
	private LayoutInflater inflater;
    private List<Map<String, Object>> groupList;
	private List<List<Map<String, Object>>> childList;
 
    public UpcomingFragmentExpandableListviewAdapter(Context context, List<Map<String, Object>> groupList, List<List<Map<String, Object>>> childList) {
        inflater = LayoutInflater.from(context);
        this.groupList = groupList;
        this.childList = childList;
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
			 convertView = inflater.inflate(R.layout.fragment_category_expandablelistview_child_item, parent,false);
			 viewholder.billNaTextView =  (TextView)convertView.findViewById(R.id.billTextView);
			 viewholder.overduePic = (ImageView)convertView.findViewById(R.id.overDueImageView);
			 
			 convertView.setTag(viewholder);
	        }else
	         {
	        	 viewholder = (cViewHolder)convertView.getTag();
	         }
		 viewholder.billNaTextView.setText(childList.get(groupPosition).get(childPosition).get("zbillName")+"");
		 
		 if ( (Integer)childList.get(groupPosition).get(childPosition).get("checkDot")==0) {
//				viewholder.overduePic.setImageResource(R.drawable.overdue);
			}else if ( (Integer)childList.get(groupPosition).get(childPosition).get("checkDot")==7){
//				viewholder.overduePic.setImageResource(R.drawable.overdue7);
			}else if ( (Integer)childList.get(groupPosition).get(childPosition).get("checkDot")==30){
//				viewholder.overduePic.setImageResource(R.drawable.overdue30);
			}
		 
		return convertView;
	}

	class cViewHolder {
		public TextView billNaTextView;
        public ImageView overduePic;
	}
	
	@Override
	//counts the number of children items so the list knows how many times calls getChildView() method
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return childList.get(groupPosition).size();
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
			 convertView = inflater.inflate(R.layout.fragment_category_expandablelistview_group_item, parent,false);
			 viewholder.categoryTextView = (TextView)convertView.findViewById(R.id.categoryTextView);
			 viewholder.totalTextView = (TextView)convertView.findViewById(R.id.totalTextView);
			 convertView.setTag(viewholder);
        }else
         {
        	 viewholder = (gViewHolder)convertView.getTag();
         }
		viewholder.categoryTextView.setText(groupList.get(groupPosition).get("categoryName")+"");
		viewholder.totalTextView.setText(groupList.get(groupPosition).get("categoryAmount")+"");
		return convertView;
	}
	
	
	class gViewHolder {
		public TextView categoryTextView;
        public TextView totalTextView;
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
