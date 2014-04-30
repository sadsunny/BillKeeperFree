package com.appxy.billkeeper.adapter;

import com.appxy.billkeeper.R;
import com.appxy.billkeeper.adapter.ExistingAccountAdapter.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class ReportNavigationListAdapter extends BaseAdapter implements SpinnerAdapter {
	
    private LayoutInflater mInflater;
    private String[] content = {"Month                           ","Quarter","Year"};
    
    
    public ReportNavigationListAdapter(Context context){
    	this.mInflater = LayoutInflater.from(context);
    }
    
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		 ViewHolder viewholder = null;
		 
		 if(convertView == null)
         {
			 viewholder = new ViewHolder();
             //����Զ����Item���ּ��ز���
             convertView = mInflater.inflate(R.layout.calender_getview, null);
             viewholder.mTextView = (TextView)convertView.findViewById(R.id.get_text);
             //�����úõĲ��ֱ��浽�����У�������������Tag��Ա���淽��ȡ��Tag
             convertView.setTag(viewholder);
         }else
         {
        	 viewholder = (ViewHolder)convertView.getTag();
         }
		 
		 viewholder.mTextView.setText("Report  ");
		 
		return convertView;
	}

	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		DropViewHolder viewholder = null;
		 
		 if(convertView == null)
         {
			 viewholder = new DropViewHolder();
             //����Զ����Item���ּ��ز���
             convertView = mInflater.inflate(R.layout.report_getdropdownview, null);
             viewholder.mTextView1 = (TextView)convertView.findViewById(R.id.report_drop_text);
             viewholder.lineView = (View)convertView.findViewById(R.id.blacklineview);
             //�����úõĲ��ֱ��浽�����У�������������Tag��Ա���淽��ȡ��Tag
             convertView.setTag(viewholder);
         }else
         {
        	 viewholder = (DropViewHolder)convertView.getTag();
         }
		 viewholder.mTextView1.setText(content[position]);
		 
		 if (position == 2) {
			 viewholder.lineView.setVisibility(View.INVISIBLE);
		} else {
			 viewholder.lineView.setVisibility(View.VISIBLE);
		}
		
		 
		return convertView;
	}
	
	class ViewHolder
    {
        public TextView mTextView;
    }
	
	class DropViewHolder
    {
        public TextView mTextView1;
        public View lineView;
    }
	
}
