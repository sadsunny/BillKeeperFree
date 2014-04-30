package com.appxy.billkeeper.adapter;


import java.util.List;
import java.util.Map;

import com.appxy.billkeeper.R;

import android.R.integer;
import android.content.Context;
import android.graphics.RadialGradient;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CategoriesActivityAdapter extends BaseAdapter{
	
    private List<Map<String, Object>> data;
    private Context context;
 
    private LayoutInflater mInflater;
    
    private int[] itemState; //����Item�ĵ��״̬
    
    public  CategoriesActivityAdapter(Context context,List<Map<String, Object>> data) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.data=data;
		
		itemState = new int[data.size()];
		
		for(int i=0;i<data.size();i++){
			itemState[i]=0;
		}
	} 
    
    public int[] getItemState(){ //������ô˷����ı�itemState��ֵ
		return itemState;
	}
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
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		 ViewHolder viewholder = null;

		 if(convertView == null)
         {
			 viewholder = new ViewHolder();
             //����Զ����Item���ּ��ز���
             convertView = mInflater.inflate(R.layout.activity_categories_item, null);
             
             viewholder.mCategory = (TextView)convertView.findViewById(R.id.categoriesTextview);
             viewholder.mCategoryNumber = (TextView)convertView.findViewById(R.id.mNumberTextview);
             viewholder.mLayout = (RelativeLayout)convertView.findViewById(R.id.CateRelativeLayout);
             
             //�����úõĲ��ֱ��浽�����У�������������Tag��Ա���淽��ȡ��Tag
             convertView.setTag(viewholder);
         }else
         {
        	 viewholder = (ViewHolder)convertView.getTag();
         }
		 
		 viewholder.mCategory.setText((String)data.get(position).get("Categories"));
		 viewholder.mCategoryNumber.setText((String)data.get(position).get("mCategoryNumber"));
		 
		 updateBackground(position , viewholder.mLayout); //�ı䱳����ɫ
		 
         return convertView;
	}
	
	public void updateBackground(int position, View view) { 
		int backgroundId;
		
		if (itemState[position] == 1) {
		} else {
			backgroundId = R.drawable.conversation_item_background_read;
		}
	}
	
	static class ViewHolder
    {
        public TextView mCategory;
        public TextView mCategoryNumber;
        public RelativeLayout mLayout;
    }	
}
