package com.appxy.billkeeper.adapter;

import com.appxy.billkeeper.R;
import com.appxy.billkeeper.adapter.ExistingAccountAdapter.ViewHolder;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class CalendarNavigationListAdapter extends BaseAdapter implements SpinnerAdapter {
	
    private LayoutInflater mInflater;
    private Context context;
    
    private String[]  Arrays ={"VIEW AS                   ","Calendar View","List View","DISPLAY","All","Paid","Unpaid"};
    
    public CalendarNavigationListAdapter(Context context){
    	this.mInflater = LayoutInflater.from(context);
    	this.context = context;
    }
    
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 7;
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
		 
		 viewholder.mTextView.setText("Calendar");
		 
		return convertView;
	}
	
	
	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		if (position==0 || position == 3) {
			return false;
		}else {
			return true;
		}
	}
	
	public int Dp2Px(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}


	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		DropViewHolder viewholder = null;
		 
		 if(convertView == null)
         {
			 viewholder = new DropViewHolder();
             //����Զ����Item���ּ��ز���
             convertView = mInflater.inflate(R.layout.calendar_getdropdownview, null);
             viewholder.mTextView1 = (TextView)convertView.findViewById(R.id.drop_text);
             viewholder.mView1 = (View)convertView.findViewById(R.id.blacklineview);
             viewholder.mView2 = (View)convertView.findViewById(R.id.whitelineview);
             //�����úõĲ��ֱ��浽�����У�������������Tag��Ա���淽��ȡ��Tag
             convertView.setTag(viewholder);
         }else
         {
        	 viewholder = (DropViewHolder)convertView.getTag();
         }
		 
		 viewholder.mTextView1.setText(Arrays[position]);
		 if (position == 0 || position ==3) {
			 viewholder.mTextView1.setTextColor(Color.rgb(199, 210, 216));
			 viewholder.mTextView1.setTextSize(16);
			 viewholder.mView2.setVisibility(View.VISIBLE);
			 viewholder.mView1.setVisibility(View.INVISIBLE);
			 viewholder.mTextView1.setHeight(Dp2Px(context,32));
		} else if(position == 2 || position == 6){
			 viewholder.mTextView1.setTextColor(Color.rgb(255, 255, 255));
			 viewholder.mTextView1.setTextSize(19);
			 viewholder.mView2.setVisibility(View.INVISIBLE);
			 viewholder.mView1.setVisibility(View.INVISIBLE);
			 viewholder.mTextView1.setHeight(Dp2Px(context,48));
		}else {
			 viewholder.mTextView1.setTextColor(Color.rgb(255, 255, 255));
			 viewholder.mTextView1.setTextSize(19);
			 viewholder.mView2.setVisibility(View.INVISIBLE);
			 viewholder.mView1.setVisibility(View.VISIBLE);
			 viewholder.mTextView1.setHeight(Dp2Px(context,48));
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
        public View mView1;
        public View mView2;
    }
	
}
