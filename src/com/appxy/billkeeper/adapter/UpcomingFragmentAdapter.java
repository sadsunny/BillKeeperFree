package com.appxy.billkeeper.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

public class UpcomingFragmentAdapter extends BaseAdapter {

	private List<Map<String, Object>> data;
	private Context context;

	private LayoutInflater mInflater;


	public UpcomingFragmentAdapter(Context context) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
//		this.data = data;

	}
	
	public void setAdapterData(List<Map<String, Object>> data) {
		this.data = data;
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (data == null) {
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

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewholder = null;

		if (convertView == null) {
			viewholder = new ViewHolder();
			// ����Զ����Item���ּ��ز���
			convertView = mInflater.inflate(R.layout.fragment_upcoming_item,
					null);

			viewholder.colorLabel = (ImageView) convertView
					.findViewById(R.id.dotimageView);
			
			viewholder.bNametextView = (TextView) convertView
					.findViewById(R.id.billnametextView);
			
			viewholder.mDateTextView = (TextView) convertView
			.findViewById(R.id.datetextView);
			
			viewholder.currencyImageView = (TextView) convertView
			.findViewById(R.id.currencyImageView1);
			
			viewholder.amountTextView = (TextView) convertView
			.findViewById(R.id.amounttextView);
			
			viewholder.autoImageView = (ImageView) convertView
			.findViewById(R.id.autorepeatimageView);
			
			// �����úõĲ��ֱ��浽�����У�������������Tag��Ա���淽��ȡ��Tag
			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}
		
		viewholder.colorLabel.setImageResource(Common.CATEGORYICON[(Integer) (data.get(position).get("bk_categoryIconName"))]);
		
		viewholder.bNametextView.setText((String) data.get(position).get("bk_accountName"));
		viewholder.mDateTextView.setText((String) data.get(position).get("mDate"));
		viewholder.currencyImageView.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
		
		if ((Integer)data.get(position).get("nbk_billAmountUnknown") == 1) {
			viewholder.amountTextView.setText("N/A"); 
			viewholder.currencyImageView.setVisibility(View.INVISIBLE);
		} else {
			viewholder.amountTextView.setText(data.get(position).get("nbillamount")+""); 
			viewholder.currencyImageView.setVisibility(View.VISIBLE);
		}
		
		if ((Long)data.get(position).get("nbk_billDuedate") <getNowMillis() ) {
			viewholder.amountTextView.setTextColor(Color.rgb(225, 12, 12));
			viewholder.currencyImageView.setTextColor(Color.rgb(225, 12, 12));
		} else {
			viewholder.amountTextView.setTextColor(R.color.text_gray);
			viewholder.currencyImageView.setTextColor(R.color.text_gray);
		}
		
		if ((Integer)data.get(position).get("nbk_billAutoPay")==1) {
			viewholder.autoImageView.setVisibility(View.VISIBLE);
			viewholder.autoImageView.setImageResource(R.drawable.auto_pay);
		} else{
			viewholder.autoImageView.setVisibility(View.INVISIBLE);
		}
		
		return convertView;
	}

	public long getNowMillis() { // �õ�����ĺ�����
		Date date1 = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		String nowTime = formatter.format(date1);
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(nowTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long nowMillis = c.getTimeInMillis(); // ��ȡ���������ն�Ӧ�ĺ������ȥʱ���룬�������

		return nowMillis;
	}

	public class ViewHolder {
		public ImageView colorLabel;
		public TextView bNametextView;
		public TextView mDateTextView;
		public TextView currencyImageView;
		public TextView amountTextView;
		public ImageView autoImageView;
	}
}
