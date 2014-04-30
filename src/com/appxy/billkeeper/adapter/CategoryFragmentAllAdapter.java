package com.appxy.billkeeper.adapter;

	import java.util.List;
import java.util.Map;

import com.appxy.billkeeper.R;
import com.appxy.billkeeper.entity.Common;

	import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

	public class CategoryFragmentAllAdapter extends BaseAdapter{
		
	    private List<Map<String, Object>> data;
	    private Context context;
	 
	    private LayoutInflater mInflater;
	    
	    public  CategoryFragmentAllAdapter(Context context,List<Map<String, Object>> data) {
			this.context = context;
			this.mInflater = LayoutInflater.from(context);
			this.data=data;
		} 

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (data==null) {
				return 0;
			}
			return data.size();
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
	             convertView = mInflater.inflate(R.layout.fragment_category_all_item, null);
	             
	             viewholder.imageView1 = (ImageView)convertView.findViewById(R.id.imageView1);
	             viewholder.textView1 = (TextView)convertView.findViewById(R.id.textView1);
	             viewholder.textView2 = (TextView)convertView.findViewById(R.id.textView2);
	             
	             //�����úõĲ��ֱ��浽�����У�������������Tag��Ա���淽��ȡ��Tag
	             convertView.setTag(viewholder);
	         }else
	         {
	        	 viewholder = (ViewHolder)convertView.getTag();
	         }

			 viewholder.imageView1.setBackgroundColor(Common.COLORS[(Integer) data.get(position).get("listClolor")]);
			 
			 viewholder.textView1.setText((String)(data.get(position).get("categoryName")));
			 
			 viewholder.textView2.setText((String)data.get(position).get("mPercent"));
			 
	         return convertView;
		}
		
		
		static class ViewHolder
	    {
	        public ImageView imageView1;
	        public TextView textView1;
	        public TextView textView2; 
	    }	
	}
