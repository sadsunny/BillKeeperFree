package com.appxy.billkeeper;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;


public class SubListView extends ListView { 

	public  SubListView  (Context context, AttributeSet attrs) { 

		super(context, attrs); 

	} 

	public  SubListView  (Context context) { 

		super(context); 

	} 

	public  SubListView  (Context context, AttributeSet attrs, int defStyle) { 

		super(context, attrs, defStyle); 

	} 

	@Override 

	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { 

		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST); 

		super.onMeasure(widthMeasureSpec, expandSpec); 

	} 

} 
