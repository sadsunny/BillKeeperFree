package com.appxy.billkeeper.adapter;

import java.util.List;
import java.util.Map;

import com.appxy.billkeeper.R;
import com.appxy.billkeeper.entity.Common;

import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.RadialGradient;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("ResourceAsColor")
public class CalendarFragmentListviewAdapter extends BaseAdapter {

	private List<Map<String, Object>> mData;
	private Context context;
	private LayoutInflater mInflater;
	private int duecheck = -1;

	public CalendarFragmentListviewAdapter(Context context) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
	}

	public void setAdapterList(List<Map<String, Object>> data) {
		this.mData= data;
	}
	
	public List<Map<String, Object>> getAdapterDate() {
		return this.mData;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (mData == null) {
			return 0;
		}
		return mData.size();
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

		if (convertView == null) {
			viewholder = new ViewHolder();
			// ����Զ����Item���ּ��ز���
			convertView = mInflater.inflate(R.layout.fragment_calender_listview_item,null);

			viewholder.category_imageView = (ImageView) convertView.findViewById(R.id.category_imageView);
			viewholder.account_name_textView = (TextView) convertView.findViewById(R.id.account_name_textView);
			viewholder.currency_textView = (TextView) convertView.findViewById(R.id.currency_textView);
			viewholder.amount_textView = (TextView) convertView.findViewById(R.id.amount_textView);
			viewholder.payment_state_imageView = (ImageView) convertView.findViewById(R.id.payment_state_imageView);

			// �����úõĲ��ֱ��浽�����У�������������Tag��Ա���淽��ȡ��Tag
			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}
		viewholder.category_imageView.setImageResource(Common.CATEGORYICON[(Integer) (mData.get(position).get("bk_categoryIconName"))]);
		viewholder.account_name_textView.setText((String) mData.get(position).get("bk_accountName"));
		viewholder.currency_textView.setText( Common.CURRENCY_SIGN[Common.CURRENCY]);
		
		if ((Integer)mData.get(position).get("nbk_billAmountUnknown") == 1) {
			viewholder.currency_textView.setVisibility(View.INVISIBLE);
			viewholder.amount_textView.setText("N/A"); 
		} else {
			viewholder.currency_textView.setVisibility(View.VISIBLE);
			if (duecheck == 1 ) {
				viewholder.amount_textView.setText( Common.doublepoint2str(Double.parseDouble(mData.get(position).get("remain")+""))); 
			} else {
				viewholder.amount_textView.setText( Common.doublepoint2str(Double.parseDouble(mData.get(position).get("nbillamount")+""))); 
			}
		}
		
	
		
		
		int paystate = (Integer) mData.get(position).get("payState");
//		Log.v("mtst", "pay״̬in"+paystate);
		if (paystate == -1) {
			viewholder.currency_textView.setTextColor(Color.rgb(225, 12, 12));
			viewholder.amount_textView.setTextColor(Color.rgb(225, 12, 12));
			viewholder.payment_state_imageView.setVisibility(View.INVISIBLE);
		} else if (paystate == 0) {
//			viewholder.payment_state_imageView.setVisibility(View.INVISIBLE);
			viewholder.currency_textView.setTextColor(R.color.text_gray);
			viewholder.amount_textView.setTextColor(R.color.text_gray);
			
			if ((Integer)mData.get(position).get("nbk_billAutoPay")==1) {
				viewholder.payment_state_imageView.setVisibility(View.VISIBLE);
				viewholder.payment_state_imageView.setImageResource(R.drawable.auto_pay);
			} else{
				viewholder.payment_state_imageView.setVisibility(View.INVISIBLE);
			}
			
		}else if (paystate == 1) {
			viewholder.payment_state_imageView.setVisibility(View.VISIBLE);
			viewholder.payment_state_imageView.setImageResource(R.drawable.paid_icon);
			viewholder.currency_textView.setTextColor(R.color.text_gray);
			viewholder.amount_textView.setTextColor(R.color.text_gray);
		}
		
		
		return convertView;
	}
	
	public void  setDuecheck(int check) {
		this.duecheck = check;
	}


 public class ViewHolder {
		public ImageView category_imageView;
		public TextView account_name_textView;
		public TextView currency_textView;
		public TextView amount_textView;
		public ImageView payment_state_imageView;
	}
}
