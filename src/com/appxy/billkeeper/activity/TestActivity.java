package com.appxy.billkeeper.activity;

import com.appxy.billkeeper.R;
import com.appxy.billkeeper.R.layout;
import com.appxy.billkeeper.R.menu;
import com.appxy.billkeeper.adapter.DialogCategoryIconAdapter;
import com.appxy.billkeeper.adapter.DialogChooseCategoryAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;

import android.widget.AdapterView.OnItemClickListener;

public class TestActivity extends Activity {

	private GridView gridView;
	private Button textButton;
	private RadioButton mRadioButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
//		
		Log.v("test", "test");
		
		textButton= (Button)findViewById(R.id.testbut);
		textButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
//				LayoutInflater flater = LayoutInflater.from(TestActivity.this);//��categoryicon������
//			    View view = flater.inflate(R.layout.dialog_category_icon, null);
//			    
//				DialogCategoryIconAdapter Dadapter = new DialogCategoryIconAdapter(TestActivity.this);
//				GridView gridView = (GridView)view.findViewById(R.id.dialogGridView);
//				gridView.setAdapter(Dadapter);
//			    
//				AlertDialog.Builder builder = new AlertDialog.Builder(TestActivity.this);
//				builder.setView(view);
//				AlertDialog alert = builder.create(); 
//				alert.show();
				
				
				LayoutInflater flater = LayoutInflater.from(TestActivity.this);//��categoryicon������
			    View view = flater.inflate(R.layout.dialog_choose_category, null);
				
			    DialogChooseCategoryAdapter cCaAdapter = new DialogChooseCategoryAdapter(TestActivity.this);
			    ListView listView =(ListView)view.findViewById(R.id.listView);
				
			    listView.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						Log.v("test", "testitem");
						
						
					}
			    	
			    });
			    listView.setAdapter(cCaAdapter);
			    
				AlertDialog.Builder builder = new AlertDialog.Builder(TestActivity.this);
				builder.setView(view);
				AlertDialog alert = builder.create(); 
				alert.show();
				
				
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test, menu);
		return true;
	}

}
