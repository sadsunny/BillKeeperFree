package com.appxy.billkeeper.activity;

import com.appxy.billkeeper.BaseHomeActivity;
import com.appxy.billkeeper.R;
import com.appxy.billkeeper.R.layout;
import com.appxy.billkeeper.R.menu;
import com.appxy.billkeeper.adapter.DialogCategoryIconAdapter;
import com.appxy.billkeeper.db.BillKeeperSql;

import android.R.anim;
import android.R.integer;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ActionBar.LayoutParams;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class AddNewCategoryActivity extends BaseHomeActivity {

	private LayoutInflater inflater; 
	private BillKeeperSql bksql; 
	private int iconPosition = 0; //ѡ��icon��λ�ã�-1���ʾû��ѡ��
	private EditText mCategoryName ;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new_category);
		
		mCategoryName = (EditText)findViewById(R.id.mcategory_na);
		
		inflater = LayoutInflater.from(this);
		ActionBar mActionBar = getActionBar();
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		View customActionBarView = inflater.inflate(R.layout.activity_new_bill_custom_actionbar,null,false);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setCustomView(customActionBarView,lp);
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		
		final DialogCategoryIconAdapter Dadapter = new DialogCategoryIconAdapter(this);
		GridView gridView = (GridView)findViewById(R.id.mGridView);
		gridView.setAdapter(Dadapter);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT)); 
		
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0,
					View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				iconPosition = arg2;
				Dadapter.changeStatus(iconPosition);
				Dadapter.notifyDataSetChanged();
			}
		});
		
		View cancelActionView = customActionBarView.findViewById(R.id.action_cancel);
        cancelActionView.setOnClickListener(mActionBarListener);
        View doneActionView = customActionBarView.findViewById(R.id.action_done);
        doneActionView.setOnClickListener(mActionBarListener);
		
	} //create����
	
	private long addCategory(String name,int position){
		
		bksql = new BillKeeperSql(this); 
		SQLiteDatabase db = bksql.getReadableDatabase();
		ContentValues cv=new ContentValues();
		cv.put("bk_categoryName", name);	
		cv.put("bk_categoryIconName", position);	
		cv.put("bk_categoryUptime", System.currentTimeMillis());	
		long row= db.insert("BK_Category", null, cv);
		db.close();
		return row;
		
	}
	
	 private long categoryUpdate(long rowid, long sort){
		  bksql = new BillKeeperSql(this); 
		  SQLiteDatabase db = bksql.getReadableDatabase();
		  String id = rowid+"";
		  
		  ContentValues cv=new ContentValues();
		  cv.put("bk_categorySequence", sort);
		  
		  long row=db.update("BK_Category", cv, "_id = ?", new String[] {id});
		  db.close();
		  return row;
		  
	  }
	 
	
	private final View.OnClickListener mActionBarListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        	
        	switch (v.getId()){
        	case R.id.action_cancel:
        		finish();
    			break;
    			
        	case R.id.action_done:
        		String cName = mCategoryName.getText().toString();
        		if ( cName == null || cName.trim().length()==0 || cName.trim().equals("") ) {
        			
        			new AlertDialog.Builder(AddNewCategoryActivity.this)
    				.setTitle("Warning! ")
    				.setMessage(
    						"Please make sure the category name is not empty! ")
    				.setPositiveButton("Retry",
    						new DialogInterface.OnClickListener() {

    							@Override
    							public void onClick(DialogInterface dialog,
    									int which) {
    								// TODO Auto-generated method stub
    								dialog.dismiss();
    								
    							}
    						}).show();
        			
				}else if(iconPosition == -1){
					
					new AlertDialog.Builder(AddNewCategoryActivity.this)
    				.setTitle("Warning! ")
    				.setMessage(
    						"Please select an icon! ")
    				.setPositiveButton("Retry",
    						new DialogInterface.OnClickListener() {

    							@Override
    							public void onClick(DialogInterface dialog,
    									int which) {
    								// TODO Auto-generated method stub
    								dialog.dismiss();
    								
    							}
    						}).show();
					
				}else {
					
					long key = addCategory(cName,iconPosition);//������
					
					categoryUpdate(key,key); //���������ֶ�
					String cKey = Long.toString(key);
					
					Intent intent = new Intent();
					intent.putExtra("cName", cName);
					intent.putExtra("cateKey", cKey);
					setResult(1, intent);
					finish();
				}
        	}
        }
    };

}
