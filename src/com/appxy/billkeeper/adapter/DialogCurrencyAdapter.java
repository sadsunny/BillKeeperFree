package com.appxy.billkeeper.adapter;

	import com.appxy.billkeeper.R;
import com.appxy.billkeeper.entity.Common;

	import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

	public class DialogCurrencyAdapter extends BaseAdapter{
		
		private LayoutInflater mInflater;
		private Context context;
		private String[] currency_name;
		private String[] currency_sign;
		    
	    public  DialogCurrencyAdapter(Context context) {
				this.context = context;
				this.mInflater = LayoutInflater.from(context);
				this.currency_name = Common.CURRENCY_NAME;
				this.currency_sign = Common.CURRENCY_SIGN;
			} 
		    
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return currency_name.length;
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
	             convertView = mInflater.inflate(R.layout.dialog_set_currency_item, null);
	             
	             viewholder.textView1 = (TextView)convertView.findViewById(R.id.currency_sign_textView);
	             viewholder.textView2 = (TextView)convertView.findViewById(R.id.currency_name_textView);
	             
	             //�����úõĲ��ֱ��浽�����У�������������Tag��Ա���淽��ȡ��Tag
	             convertView.setTag(viewholder);
	         }else
	         {
	        	 viewholder = (ViewHolder)convertView.getTag();
	         }

			 viewholder.textView1.setText(currency_sign[position]);
			 viewholder.textView2.setText(currency_name[position]);
	         return convertView;
		}
		
		static class ViewHolder
	    {
	        public TextView textView1; 
	        public TextView textView2;
	    }	
	}
