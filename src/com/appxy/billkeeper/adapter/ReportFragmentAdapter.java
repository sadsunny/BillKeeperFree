package com.appxy.billkeeper.adapter;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ReportFragmentAdapter extends BaseAdapter {

	private List<Map<String, Object>> mData;
	private Context context;
	private LayoutInflater mInflater;

	public ReportFragmentAdapter(Context context,
			List<Map<String, Object>> data) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.mData = data;
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
			convertView = mInflater.inflate(R.layout.fragment_report_item,
					null);

			viewholder.categoryImageView = (ImageView) convertView.findViewById(R.id.account_category_imageView);
			
			viewholder.accounTextView = (TextView) convertView.findViewById(R.id.account_name_textView);
			
			viewholder.percentTextView = (TextView) convertView.findViewById(R.id.percent_textView);
			
			viewholder.currencyTextView = (TextView) convertView.findViewById(R.id.currency_textView);
			
			viewholder.amountTextView = (TextView) convertView.findViewById(R.id.amount_textView);

			// �����úõĲ��ֱ��浽�����У�������������Tag��Ա���淽��ȡ��Tag
			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}
		
		viewholder.categoryImageView.setBackgroundColor(Common.COLORS[position % Common.COLORS.length]);
		
		viewholder.accounTextView.setText((String) mData.get(position).get("categoryName"));
		viewholder.percentTextView.setText((String) mData.get(position).get("mPercent"));
		viewholder.currencyTextView.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
		viewholder.amountTextView.setText(Common.doublepoint2str( Double.parseDouble( mData.get(position).get("categoryAmount")+"")));
		
		

		return convertView;
	}


	static class ViewHolder {
		public ImageView categoryImageView;
		public TextView accounTextView;
		public TextView percentTextView;
		public TextView currencyTextView;
		public TextView amountTextView;
	}
}
