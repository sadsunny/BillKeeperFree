package com.appxy.billkeeper.adapter;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.appxy.billkeeper.R;
import com.appxy.billkeeper.entity.Common;
import com.mobeta.android.dslv.DragSortListView;

import android.R.integer;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AccountFragmentAdapter extends BaseAdapter {
	
    private List<Map<String, Object>> data;
    private Context context;
    public  static int ISCHECKED = -1; //���Ǿ�̬����ʱ������***
    
    private LayoutInflater mInflater;
    
    public  AccountFragmentAdapter(Context context,List<Map<String, Object>> data) {
    	super();
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.data=data;
	} 
    
//	@Override
//	public void drop(int from, int to) { //��ҷ
//		// TODO Auto-generated method stub
//		
//		Map<String, Object> mdata = data.remove(from);
//		data.add(to, mdata);
//        notifyDataSetChanged();
//	}	
	
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
	
	public void isChecked(int checked){
		this.ISCHECKED=checked;
//	    notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		 ViewHolder viewholder = null;

		 if(convertView == null)
         {
			 viewholder = new ViewHolder();
             //����Զ����Item���ּ��ز���
             convertView = mInflater.inflate(R.layout.fragment_account_item, null);
             
             viewholder.category_imageView = (ImageView)convertView.findViewById(R.id.category_imageView);
             viewholder.account_name_textView = (TextView)convertView.findViewById(R.id.account_name_textView);
             viewholder.category_name_textView = (TextView)convertView.findViewById(R.id.category_name_textView);
             viewholder.Sort_ImageView = (ImageView)convertView.findViewById(R.id.Sort_ImageView);
             
             //�����úõĲ��ֱ��浽�����У�������������Tag��Ա���淽��ȡ��Tag
             convertView.setTag(viewholder);
         }else
         {
        	 viewholder = (ViewHolder)convertView.getTag();
         }
		 
//		 convertView.setBackgroundResource(R.drawable.bg_handle_section2_selector);
		 viewholder.category_imageView.setImageResource(Common.CATEGORYICON[Integer.parseInt((data.get(position).get("iconPosition")).toString())]);	
		 
		 viewholder.account_name_textView.setText((data.get(position).get("accountName"))+"");
		 viewholder.category_name_textView.setText((String)data.get(position).get("categoryName"));
		 
		 if (ISCHECKED == 1) {
			 viewholder.Sort_ImageView.setVisibility(View.VISIBLE);
		} else {
			 viewholder.Sort_ImageView.setVisibility(View.INVISIBLE);
		}
         return convertView;
	}
	
	static class ViewHolder
    {
        public ImageView category_imageView;
        public TextView account_name_textView;
        public TextView category_name_textView; 
        public ImageView Sort_ImageView;
    }


}
