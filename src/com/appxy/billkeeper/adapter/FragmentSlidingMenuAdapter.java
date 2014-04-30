package com.appxy.billkeeper.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appxy.billkeeper.R;

public class FragmentSlidingMenuAdapter extends BaseAdapter {
	
	 private Context context;  
	 private LayoutInflater mInflater =null;
	 public FragmentSlidingMenuAdapter(Context context) {  
	  this.context=context; 
	  mInflater = LayoutInflater.from(context);
	 }  
	 
	 private Integer[] menuIcon={
			 R.drawable.upcoming,
			 R.drawable.calendar,
			 R.drawable.account,
			 R.drawable.report,
	 };
	 
	 private String[] mStrings={"Upcoming","Calendar","Account","Report"};
	 
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return menuIcon.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		 ViewHolder viewholder = null;

		 if(convertView == null)
         {
			 viewholder = new ViewHolder();
             //����Զ����Item���ּ��ز���
//             convertView = mInflater.inflate(R.layout.fragment_sliding_menu_item, null);
//             viewholder.mImageView = (ImageView)convertView.findViewById(R.id.menuiconimageView1);
//             viewholder.mTextView = (TextView)convertView.findViewById(R.id.menutextView1);
             
             //�����úõĲ��ֱ��浽�����У�������������Tag��Ա���淽��ȡ��Tag
             convertView.setTag(viewholder);
         }else
         {
        	 viewholder = (ViewHolder)convertView.getTag();
         }
		 viewholder.mImageView.setImageResource(menuIcon[position]);
		 viewholder.mTextView.setText(mStrings[position]);	 
         return convertView;
	}
	
	class ViewHolder {
		ImageView mImageView;
		TextView mTextView;
	}

}
