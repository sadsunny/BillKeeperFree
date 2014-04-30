package com.appxy.billkeeper.adapter;

import com.appxy.billkeeper.R;
import com.appxy.billkeeper.entity.Common;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class DialogCategoryIconAdapter extends BaseAdapter {  
	 private Context context;  
	 private int mSelect = 0 ;
	 private int mBack = -1 ;
	
	 public DialogCategoryIconAdapter(Context context) {  
	  this.context=context;  
	 }  
	 
	 public void changeStatus(int select)  
	 {  
		this.mSelect = select;
	 }  
	 
	 public void changeBack(int back)  
	 {  
		this.mBack = back;
	 }  
	   
	 //get the number  
	 @Override  
	 public int getCount() {  
	  return Common.CATEGORYICON.length;  
	 }  
	 
	 @Override  
	 public Object getItem(int position) {  
	  return position;  
	 }  
	 
	 //get the current selector's id number  
	 @Override  
	 public long getItemId(int position) {  
	  return position;  
	 }  
	 
	 //create view method  
	 @Override  
	 public View getView(int position, View view, ViewGroup viewgroup) {  
	  ImgTextWrapper wrapper;  
	  if(view==null) {  
	   wrapper = new ImgTextWrapper();  
	   LayoutInflater inflater = LayoutInflater.from(context);  
	   view = inflater.inflate(R.layout.dialog_category_icon_item, null); 
	   wrapper.imageView = (ImageView)view.findViewById(R.id.dialogImageItem);
	   wrapper.imageViewSel = (ImageView)view.findViewById(R.id.dialogImageSel);
	   view.setTag(wrapper);  
//	   view.setPadding(-5,-5, -5,-5);  //ÿ��ļ��  
	  } else {  
	   wrapper = (ImgTextWrapper)view.getTag();  
	  }  
	  
	  wrapper.imageView.setBackgroundResource(Common.CATEGORYICON[position]); 	
	  
	  if (position == mSelect) {
//		  view.setBackgroundColor(Color.argb(255, 135, 206, 255));
		  wrapper.imageViewSel.setVisibility(View.VISIBLE);
	}else {
		  wrapper.imageViewSel.setVisibility(View.INVISIBLE);
	 }
	  return view;  
	 }

	class ImgTextWrapper {  
	 ImageView imageView; 
	 ImageView imageViewSel; 
	}
	
}
	   