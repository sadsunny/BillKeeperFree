package com.appxy.billkeeper.adapter;

	import java.util.List;
import java.util.Map;

import com.appxy.billkeeper.R;

	import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

	public class DialogUpcomingItemAdapter extends BaseAdapter{
		
		private String[] data5 = {"Quick pay","Add payment","Mark as paid","Edit","Delete"};
		private String[] data4 = {"Quick pay","Add payment","Edit","Delete"};
		private String[] data2 = {"Edit","Delete"};
		private String[] data;
		
		private LayoutInflater mInflater;
		private Context context;
		private int conut = 2;
		    
	    public  DialogUpcomingItemAdapter(Context context) {
				this.context = context;
				this.mInflater = LayoutInflater.from(context);
			} 
	    
	    public void listCount(int count) {
			this.conut = count;
			if (count == 2) {
				data = data2;
			} else if (count == 4) {
				data = data4;
			} else if (count == 5) {
				data = data5;
			} else {
				data = data2;
				this.conut =2;
			}
			
		}
	    
		    
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return conut;
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
	             convertView = mInflater.inflate(R.layout.dialog_upcoming_item_operation_item, null);
	             
	             viewholder.textView1 = (TextView)convertView.findViewById(R.id.diaTextView1);
	             
	             //�����úõĲ��ֱ��浽�����У�������������Tag��Ա���淽��ȡ��Tag
	             convertView.setTag(viewholder);
	         }else
	         {
	        	 viewholder = (ViewHolder)convertView.getTag();
	         }

			 viewholder.textView1.setText(data[position]);
	         return convertView;
		}
		
		
		static class ViewHolder
	    {
	        public TextView textView1; 
	    }	
	}
