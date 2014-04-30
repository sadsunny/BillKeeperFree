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

	public class DialogDeletePayAdapter extends BaseAdapter{
		
		private String[] data = {"Delete"};
		private LayoutInflater mInflater;
		private Context context;
		    
	    public  DialogDeletePayAdapter(Context context) {
				this.context = context;
				this.mInflater = LayoutInflater.from(context);
			} 
		    
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.length;
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
		
		
	public class ViewHolder
	    {
	        public TextView textView1; 
	    }	
	}
