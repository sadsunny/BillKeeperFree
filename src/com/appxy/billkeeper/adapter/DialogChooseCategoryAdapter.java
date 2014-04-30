package com.appxy.billkeeper.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import com.actionbarsherlock.R.color;
import com.appxy.billkeeper.R;
import com.appxy.billkeeper.entity.Category;


public class DialogChooseCategoryAdapter extends BaseAdapter {

	private List<Category> categoryNa; // ��ݿ����
//	private LayoutInflater inflator;
	private int resource; // ���ص�item�Ĳ���

	private Context context;

	public DialogChooseCategoryAdapter(Context context,
			List<Category> categoryNa, int resource) {
		this.context = context;
		this.categoryNa = categoryNa;
		this.resource = resource;
	}
	
	public DialogChooseCategoryAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
//		return categoryNa.size();
		return 16;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return categoryNa.get(position); // //
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final int index = position;
		final ViewHolder viewholder;
		 LayoutInflater inflater = LayoutInflater.from(context);  
		if (convertView != null) {
			viewholder = (ViewHolder) convertView.getTag();
		} else {
			viewholder = new ViewHolder();
			convertView = inflater.inflate(
					R.layout.dialog_choose_category_item, null);
			convertView.setTag(viewholder);
			viewholder.mCategoryNa = (TextView) convertView
					.findViewById(R.id.categoryNa);
			viewholder.mRadioButton = (RadioButton) convertView
					.findViewById(R.id.radioButton);
			
//			convertView.setClickable ( true );  
//			convertView.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					Log.v("test", "testcli");
//					viewholder.mRadioButton.setChecked(true);
//				}
//			});  
			
		}

		return convertView;
	}

	public class ViewHolder {
		TextView mCategoryNa;
		RadioButton mRadioButton;

	}

}
