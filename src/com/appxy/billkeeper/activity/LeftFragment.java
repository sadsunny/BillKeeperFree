package com.appxy.billkeeper.activity;

import com.appxy.billkeeper.R;
import com.appxy.billkeeper.adapter.ExistingAccountAdapter;
import com.appxy.billkeeper.adapter.FragmentSlidingMenuAdapter;
import com.appxy.billkeeper.fragment.AccountFragment;
import com.appxy.billkeeper.fragment.CalendarFragmen;
import com.appxy.billkeeper.fragment.CategoryFragment;
import com.appxy.billkeeper.fragment.ExistingAccountFragment;
import com.appxy.billkeeper.fragment.NewUpcomingFragment;
import com.appxy.billkeeper.fragment.ReportFragment;
import com.appxy.billkeeper.fragment.UpcomingFragment;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.OnCloseListener;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class LeftFragment extends Fragment
{
	 private FragmentSlidingMenuAdapter adapter;
	 private int index = -1;
	 
	 private LinearLayout upcomingLinearLayout;
	 private ImageView upcomingImageView;
	 private TextView upcomingTextView;
	 
	 private LinearLayout calendarLinearLayout;
	 private ImageView calendarImageView;
	 private TextView calendarTextView;
	 
	 private LinearLayout accountLinearLayout;
	 private ImageView accountImageView;
	 private TextView accountTextView;
	 
	 private LinearLayout reportLinearLayout;
	 private ImageView reportImageView;
	 private TextView reportTextView;
	 
	 private FragmentManager fragmentManager; 
	 
	 public void onCreate(Bundle savedInstanceState) {
	        // TODO Auto-generated method stub
	        super.onCreate(savedInstanceState);
	        setRetainInstance(true);
	        
	    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
   	 View view = inflater.inflate(R.layout.fragment_sliding_menu, container, false);
   	 
   	   upcomingLinearLayout = (LinearLayout)view.findViewById(R.id.upcomingLinearLayout);
 	   upcomingImageView = (ImageView)view.findViewById(R.id.upcomingImageView);
 	   upcomingTextView = (TextView)view.findViewById(R.id.upcomingTextView);
 	 
 	   calendarLinearLayout = (LinearLayout)view.findViewById(R.id.calendarLinearLayout); 
 	   calendarImageView = (ImageView)view.findViewById(R.id.calendarImageView);
 	   calendarTextView = (TextView)view.findViewById(R.id.calendarTextView);
 	 
 	   accountLinearLayout = (LinearLayout)view.findViewById(R.id.accountLinearLayout);
       accountImageView =  (ImageView)view.findViewById(R.id.accountImageView);
 	   accountTextView = (TextView)view.findViewById(R.id.accountTextView);
 	 
 	   reportLinearLayout =  (LinearLayout)view.findViewById(R.id.reportLinearLayout);
 	   reportImageView = (ImageView)view.findViewById(R.id.reportImageView);
 	   reportTextView = (TextView)view.findViewById(R.id.reportTextView);
   	 
       fragmentManager=getFragmentManager(); 
        // ������ɫ��ʮ����ֵ��87 192 252 
        upcomingLinearLayout.setBackgroundResource(R.drawable.menu_item_sel);
		upcomingImageView.setImageResource(R.drawable.upcoming_sel);
		upcomingTextView.setTextColor(Color.rgb(87, 192, 252));
		
		calendarLinearLayout.setBackgroundResource(R.drawable.menu_item);
		calendarImageView.setImageResource(R.drawable.calendar);
		calendarTextView.setTextColor(Color.rgb(255, 255, 255));
		
		accountLinearLayout.setBackgroundResource(R.drawable.menu_item);
		accountImageView.setImageResource(R.drawable.account);
		accountTextView.setTextColor(Color.rgb(255, 255, 255));
		
		reportLinearLayout.setBackgroundResource(R.drawable.menu_item);
		reportImageView.setImageResource(R.drawable.report);
		reportTextView.setTextColor(Color.rgb(255, 255, 255));
		
        upcomingLinearLayout.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			upcomingLinearLayout.setBackgroundResource(R.drawable.menu_item_sel);
			upcomingImageView.setImageResource(R.drawable.upcoming_sel);
			upcomingTextView.setTextColor(Color.rgb(87, 192, 252));
			
			calendarLinearLayout.setBackgroundResource(R.drawable.menu_item);
			calendarImageView.setImageResource(R.drawable.calendar);
			calendarTextView.setTextColor(Color.rgb(255, 255, 255));
			
			accountLinearLayout.setBackgroundResource(R.drawable.menu_item);
			accountImageView.setImageResource(R.drawable.account);
			accountTextView.setTextColor(Color.rgb(255, 255, 255));
			
			reportLinearLayout.setBackgroundResource(R.drawable.menu_item);
			reportImageView.setImageResource(R.drawable.report);
			reportTextView.setTextColor(Color.rgb(255, 255, 255));
			
			BaseActivity.STATE = 3;
			if(index == 0) {
                ((SlidingMenuActivity)getActivity()).getSlidingMenu().toggle();
            }else {
            //otherwise , replace the content view via a new Content fragment
            	    index = 0;
					FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
					NewUpcomingFragment upcomingFragment = new  NewUpcomingFragment();
		    		fragmentTransaction.replace(R.id.content_frame,upcomingFragment);  
		    		fragmentTransaction.commit();
		    		((SlidingMenuActivity)getActivity()).getSlidingMenu().showContent();
            }
			
		}
	});
       
       calendarLinearLayout.setOnClickListener(new OnClickListener() {
   		
   		@Override
   		public void onClick(View v) {
   			// TODO Auto-generated method stub
   			
   			upcomingLinearLayout.setBackgroundResource(R.drawable.menu_item);
			upcomingImageView.setImageResource(R.drawable.upcoming);
			upcomingTextView.setTextColor(Color.rgb(255, 255, 255));
			
			calendarLinearLayout.setBackgroundResource(R.drawable.menu_item_sel);
			calendarImageView.setImageResource(R.drawable.calendar_sel);
			calendarTextView.setTextColor(Color.rgb(87, 192, 252));
			
			accountLinearLayout.setBackgroundResource(R.drawable.menu_item);
			accountImageView.setImageResource(R.drawable.account);
			accountTextView.setTextColor(Color.rgb(255, 255, 255));
			
			reportLinearLayout.setBackgroundResource(R.drawable.menu_item);
			reportImageView.setImageResource(R.drawable.report);
			reportTextView.setTextColor(Color.rgb(255, 255, 255));
			
			BaseActivity.STATE = 1;
			//if the content view is that we need to show . show directly
			if(index == 1) {
                ((SlidingMenuActivity)getActivity()).getSlidingMenu().toggle();
                
            }else {
            //otherwise , replace the content view via a new Content fragment
            	index = 1;
	    	    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); 
	    	    CalendarFragmen calenderFragment = new  CalendarFragmen();
	    		fragmentTransaction.replace(R.id.content_frame,calenderFragment);  
	    	    fragmentTransaction.commit(); 
	    	    ((SlidingMenuActivity)getActivity()).getSlidingMenu().showContent();
            }
			
   			
   		}
   	});
       
      accountLinearLayout.setOnClickListener(new OnClickListener() {
   		
   		@Override
   		public void onClick(View v) {
   			// TODO Auto-generated method stub
   			
   			upcomingLinearLayout.setBackgroundResource(R.drawable.menu_item);
			upcomingImageView.setImageResource(R.drawable.upcoming);
			upcomingTextView.setTextColor(Color.rgb(255, 255, 255));
			
			calendarLinearLayout.setBackgroundResource(R.drawable.menu_item);
			calendarImageView.setImageResource(R.drawable.calendar);
			calendarTextView.setTextColor(Color.rgb(255, 255, 255));
			
			accountLinearLayout.setBackgroundResource(R.drawable.menu_item_sel);
			accountImageView.setImageResource(R.drawable.account_sel);
			accountTextView.setTextColor(Color.rgb(87, 192, 252));
			
			reportLinearLayout.setBackgroundResource(R.drawable.menu_item);
			reportImageView.setImageResource(R.drawable.report);
			reportTextView.setTextColor(Color.rgb(255, 255, 255));
			
			BaseActivity.STATE = 4;
			//if the content view is that we need to show . show directly
			if(index == 2) {
                ((SlidingMenuActivity)getActivity()).getSlidingMenu().toggle();
                
            }else {
            //otherwise , replace the content view via a new Content fragment
            	index = 2;
	    	    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); 
	    	    AccountFragment accountFragment = new  AccountFragment();
	    		fragmentTransaction.replace(R.id.content_frame,accountFragment);  
	    	    fragmentTransaction.commit(); 
	    	    ((SlidingMenuActivity)getActivity()).getSlidingMenu().showContent();
            }
   			
   			
   		}
   	});
       
       reportLinearLayout.setOnClickListener(new OnClickListener() {
   		
   		@Override
   		public void onClick(View v) {
   			// TODO Auto-generated method stub
   			
   			upcomingLinearLayout.setBackgroundResource(R.drawable.menu_item);
			upcomingImageView.setImageResource(R.drawable.upcoming);
			upcomingTextView.setTextColor(Color.rgb(255, 255, 255));
			
			calendarLinearLayout.setBackgroundResource(R.drawable.menu_item);
			calendarImageView.setImageResource(R.drawable.calendar);
			calendarTextView.setTextColor(Color.rgb(255, 255, 255));
			
			accountLinearLayout.setBackgroundResource(R.drawable.menu_item);
			accountImageView.setImageResource(R.drawable.account);
			accountTextView.setTextColor(Color.rgb(255, 255, 255));
			
			reportLinearLayout.setBackgroundResource(R.drawable.menu_item_sel);
			reportImageView.setImageResource(R.drawable.report_sel);
			reportTextView.setTextColor(Color.rgb(87, 192, 252));
			
			  BaseActivity.STATE = 2;
				//if the content view is that we need to show . show directly
				if(index == 3) {
	                ((SlidingMenuActivity)getActivity()).getSlidingMenu().toggle();
	                
	            }else {
	            //otherwise , replace the content view via a new Content fragment
	            	index = 3;
		    	    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); 
		    	    ReportFragment reportFragment = new  ReportFragment();
		    		fragmentTransaction.replace(R.id.content_frame,reportFragment);  
		    	    fragmentTransaction.commit(); 
		    	    ((SlidingMenuActivity)getActivity()).getSlidingMenu().showContent();
	            }
   			
   		}
   	});
       
   	 return view;
	}

	 
}