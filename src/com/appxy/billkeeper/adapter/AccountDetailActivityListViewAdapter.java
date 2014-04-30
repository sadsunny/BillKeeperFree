package com.appxy.billkeeper.adapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

public class AccountDetailActivityListViewAdapter extends BaseAdapter {

	private List<Map<String, Object>> mData;
	private Context context;
	private LayoutInflater mInflater;

	public AccountDetailActivityListViewAdapter(Context context) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
	}

	public void setAdapterList(List<Map<String, Object>> data) {
		this.mData= data;
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

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewholder = null;

		if (convertView == null) {
			viewholder = new ViewHolder();
			// ����Զ����Item���ּ��ز���
			convertView = mInflater.inflate(R.layout.activity_account_detail_item,null);

			viewholder.account_name_textView = (TextView) convertView.findViewById(R.id.date_textView);
			viewholder.currency_textView = (TextView) convertView.findViewById(R.id.currency_textView);
			viewholder.amount_textView = (TextView) convertView.findViewById(R.id.dueamount_textView);
			viewholder.recurring_imageView = (ImageView) convertView.findViewById(R.id.recurring_imageView);

			// �����úõĲ��ֱ��浽�����У�������������Tag��Ա���淽��ȡ��Tag
			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}
		
		
		viewholder.account_name_textView.setText(getMilltoDate((Long)mData.get(position).get("nbk_billDuedate")));
		viewholder.currency_textView.setText( Common.CURRENCY_SIGN[Common.CURRENCY]);
		
		int amountUnknown = (Integer) mData.get(position).get("nbk_billAmountUnknown");
		if (amountUnknown == 1) {
			viewholder.amount_textView.setText("N/A");
			viewholder.currency_textView.setVisibility(View.INVISIBLE);
		} else {
			viewholder.currency_textView.setVisibility(View.VISIBLE);
			viewholder.amount_textView.setText(Common.doublepoint2str( Double.parseDouble(mData.get(position).get("nbillamount")+"")));
		}
		
		int paystate = (Integer) mData.get(position).get("payState");
//		Log.v("mtst", "pay״̬in"+paystate);
		if (paystate == -1) {
			viewholder.currency_textView.setTextColor(Color.rgb(225, 12, 12)); //��ɫrgb
			viewholder.amount_textView.setTextColor(Color.rgb(225, 12, 12));
			viewholder.recurring_imageView.setVisibility(View.INVISIBLE);
		} else if (paystate == 0) {
			viewholder.recurring_imageView.setVisibility(View.INVISIBLE);
			viewholder.currency_textView.setTextColor(R.color.text_gray);
			viewholder.amount_textView.setTextColor(R.color.text_gray);
		}else if (paystate == 1) {
			viewholder.recurring_imageView.setVisibility(View.VISIBLE);
			viewholder.recurring_imageView.setImageResource(R.drawable.paid_icon);
			viewholder.currency_textView.setTextColor(R.color.text_gray);
			viewholder.amount_textView.setTextColor(R.color.text_gray);
		}
		
		return convertView;
	}
	
	public String getMilltoDate(long milliSeconds) {// ������ת���ɹ̶���ʽ��������
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

 public class ViewHolder {
		public TextView account_name_textView;
		public TextView currency_textView;
		public TextView amount_textView;
		public ImageView recurring_imageView;
	}
}
