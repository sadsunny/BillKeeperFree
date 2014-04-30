package com.appxy.billkeeper.adapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.appxy.billkeeper.R;
import com.appxy.billkeeper.entity.Common;

import android.R.integer;
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
import android.widget.TextView;

public class ViewReportActivityAdapter extends BaseAdapter {

	private List<Map<String, Object>> mData;
	private Context context;
	private LayoutInflater mInflater;

	public ViewReportActivityAdapter(Context context) {
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewholder = null;

		if (convertView == null) {
			viewholder = new ViewHolder();
			// ����Զ����Item���ּ��ز���
			convertView = mInflater.inflate(R.layout.activity_view_report_item,null);

			viewholder.date_textView = (TextView) convertView.findViewById(R.id.date_textview);
			viewholder.currency_textView = (TextView) convertView.findViewById(R.id.currency_textview);
			viewholder.amount_textView = (TextView) convertView.findViewById(R.id.paid_textview);

			// �����úõĲ��ֱ��浽�����У�������������Tag��Ա���淽��ȡ��Tag
			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}
		
		viewholder.date_textView.setText((String)mData.get(position).get("monthString"));
		viewholder.currency_textView.setText( Common.CURRENCY_SIGN[Common.CURRENCY]);
	    viewholder.amount_textView.setText(Common.doublepoint2str( Double.parseDouble( mData.get(position).get("monthTotal")+"" )));
		
		return convertView;
	}

	public String getMilltoDate(long milliSeconds) {// ������ת���ɹ̶���ʽ��������
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}
 public class ViewHolder {
		public TextView date_textView;
		public TextView currency_textView;
		public TextView amount_textView;
	}
}
