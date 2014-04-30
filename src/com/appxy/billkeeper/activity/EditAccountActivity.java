package com.appxy.billkeeper.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appxy.billkeeper.BaseHomeActivity;
import com.appxy.billkeeper.R;
import com.appxy.billkeeper.R.layout;
import com.appxy.billkeeper.R.menu;
import com.appxy.billkeeper.db.BillKeeperSql;

import android.os.Bundle;
import android.R.integer;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ActionBar.LayoutParams;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EditAccountActivity extends BaseHomeActivity {
	private LayoutInflater inflater; 
	private BillKeeperSql bksql;
	private int isNull;//�ж�category��ݿ��ǲ���Ϊ�գ�Ϊ�ձ����½�category���ܴ���account.

	private int categoryPKey = -1;  	//���浽��ݿ��category����-1��ʾcategoryΪ��
	private String mCategoryName = "No Category";//category�ĳ�ʼֵ
	
	private String[] categoryString; 	// ����category���������
	private int[] categoryPK; 			// ������category����������
	
	
	private EditText mAccountName;
	private Button mCategoryNa;
	private EditText mAccountNo;
	private EditText mWebsitEdit;
	private EditText mPhoneNoEdit;
	private EditText mMemo;
	
	private AlertDialog alert ;
	
	private Button addNewCategoryButton;
	
	private String maccountName; 
	private int mcategoryId ; 
	private String maccountNo;
	private String maccountWeb ;
	private String maccountPhone ;
	private String maccountMemo;
	private String mCName;
	
	public int checked = -1; //categoryѡ���λ��
	int account_id ; 
	private  List<Map<String, Object>> dataList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new_account);
		
		
		inflater = LayoutInflater.from(this);
		ActionBar mActionBar = getActionBar();
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		View customActionBarView = inflater.inflate(R.layout.activity_new_bill_custom_actionbar,null,false);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setCustomView(customActionBarView,lp);
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		
		 
    	 
    	  mAccountName = (EditText)findViewById(R.id.account_na);
    	  mCategoryNa = (Button)findViewById(R.id.category_na);
    	  mAccountNo = (EditText)findViewById(R.id.accountno);
    	  mWebsitEdit = (EditText)findViewById(R.id.website);
    	  mPhoneNoEdit = (EditText)findViewById(R.id.phoneno);
    	  mMemo = (EditText)findViewById(R.id.memo);
    	  
    	  Intent mIntent = getIntent();
    	  account_id = mIntent.getIntExtra("account_id", -1);
    	  getAllData(account_id);
    	  getCategoryData(mcategoryId);
    	  
    	  mAccountName.setText(maccountName);
    	  mCategoryNa.setText(mCName);
    	  mAccountNo.setText(maccountNo);
    	  mWebsitEdit.setText(maccountWeb);
    	  mPhoneNoEdit.setText(maccountPhone);
    	  mMemo.setText(maccountMemo);
    	  
    	  categoryPKey = mcategoryId;
    	  
    	  mCategoryNa.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				View view = inflater.inflate(R.layout.dialog_select_category, null);
				addNewCategoryButton = (Button)view.findViewById(R.id.add_new_category);
				addNewCategoryButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent();
						intent.setClass(EditAccountActivity.this,
								AddNewCategoryActivity.class);
						startActivityForResult(intent, 1);
						
					}
				});
				
				getData();
				AlertDialog.Builder builder = new AlertDialog.Builder(EditAccountActivity.this);

				builder.setTitle("Select category").setCancelable(true);
				builder.setView(view);
				builder.setSingleChoiceItems(categoryString, checked,new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int item) {
					checked = item ;
					mCategoryName = categoryString[item];
					categoryPKey = categoryPK[item];
					mCategoryNa.setText(mCategoryName);
					dialog.dismiss();
				   
				}
				});
				
				builder.setPositiveButton("Cancel",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
				
				alert = builder.create();
				alert.show();
			}
		});
		
		View cancelActionView = customActionBarView.findViewById(R.id.action_cancel);
        cancelActionView.setOnClickListener(mActionBarListener);
        View doneActionView = customActionBarView.findViewById(R.id.action_done);
        doneActionView.setOnClickListener(mActionBarListener);
		
	} ///////////onCreate����
	
	private void getCategoryData(int cId) {
		
		bksql = new BillKeeperSql(this); 
		SQLiteDatabase db = bksql.getReadableDatabase();
		String sql = "Select bk_categoryName from BK_Category where _id = "+cId;
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			mCName = cursor.getString(0);
		}
		cursor.close();
		db.close();
	}
	
	private void getAllData(int id) {
	        
		bksql = new BillKeeperSql(this); 
		SQLiteDatabase db = bksql.getReadableDatabase();
		String sql = "select bk_accountName ,accounthasCategory ,bk_accountNo ,bk_accountWebsite ,cb_accountPhoneNo,bk_accountMemo from BK_Account where _id = "+ id;
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
            
            maccountName = cursor.getString(0); 
            mcategoryId = cursor.getInt(1); 
            maccountNo = cursor.getString(2);
            maccountWeb = cursor.getString(3);
            maccountPhone = cursor.getString(4);
            maccountMemo = cursor.getString(5);
		}
		 cursor.close();
	   	 db.close();
	}
	
	private void getData(){ //��ѯcategory��ݿ�
		
		bksql = new BillKeeperSql(this); 
		SQLiteDatabase db = bksql.getReadableDatabase();
		Cursor cursorCC = db.rawQuery("select bk_categoryName,_id from BK_Category", null);
		
		isNull=cursorCC.getCount();
		int i = 0 ;
		categoryString = new  String[isNull]; 
   	    categoryPK = new int[isNull];
		while (cursorCC.moveToNext()) {
			
		   categoryString[i] = cursorCC.getString(0);
		   categoryPK[i] =  cursorCC.getInt(1);
		   i++;
		}
		cursorCC.close();
		db.close();
	}
	
	 private final View.OnClickListener mActionBarListener = new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	
	        	switch (v.getId()){
	        	case R.id.action_cancel:
	        		
	        		Intent intent = new Intent();
					intent.putExtra("aName", "success");
					setResult(4, intent);
	        		finish();
	        		
	    			break;
	    			
	        	case R.id.action_done:
	        		String accountName = mAccountName.getText().toString();
	        		if (accountName == null || accountName.trim().length()==0 || accountName.trim().equals("")) {
	        			
	        			new AlertDialog.Builder(EditAccountActivity.this)
	    				.setTitle("Warning! ")
	    				.setMessage(
	    						"Please make sure the account name is not empty! ")
	    				.setPositiveButton("Retry",
	    						new DialogInterface.OnClickListener() {

	    							@Override
	    							public void onClick(DialogInterface dialog,
	    									int which) {
	    								// TODO Auto-generated method stub
	    								dialog.dismiss();
	    								
	    							}
	    						}).show();
					}else if( categoryPKey == -1 ){  //isNull ==0 ���� categoryPKey == -1
						
						new AlertDialog.Builder(EditAccountActivity.this)
	    				.setTitle("Warning! ")
	    				.setMessage(
	    						"Please make sure the category is not empty! ")
	    				.setPositiveButton("Retry",
	    						new DialogInterface.OnClickListener() {

	    							@Override
	    							public void onClick(DialogInterface dialog,
	    									int which) {
	    								// TODO Auto-generated method stub
	    								dialog.dismiss();
	    								
	    							}
	    						}).show();
					}else{
						
						String aNo = mAccountNo.getText().toString();
						String web =  mWebsitEdit.getText().toString();
						String mPhoneNo =  mPhoneNoEdit.getText().toString();
						String memo =  mMemo.getText().toString();
						long key = upDateAccount(accountName,categoryPKey,aNo,web,mPhoneNo,memo);
						String zpk = Long.toString(key);
						
						Intent intent1 = new Intent();
						intent1.putExtra("aName", "success");
						setResult(4, intent1);
						finish();
						
					}
	        		break;
	        	}
	        }
	    };
	    
	    private long upDateAccount(String name,int categoryKey, String accountNo , String website, String phoneNo, String memo){
			
			bksql = new BillKeeperSql(this); 
			SQLiteDatabase db = bksql.getReadableDatabase();
			ContentValues cv=new ContentValues();
			cv.put("bk_accountName", name);	
			cv.put("accounthasCategory", categoryKey);	
			cv.put("bk_accountNo", accountNo);	
			
			cv.put("bk_accountWebsite", website);	
			cv.put("cb_accountPhoneNo", phoneNo);	
			cv.put("bk_accountMemo", memo);	
			cv.put("bk_accountDate", System.currentTimeMillis());	
			
			long row=db.update("BK_Account", cv, "_id = ?", new String[] {account_id+""});
			
			db.close();
			return row;
		}
	    
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
			super.onActivityResult(requestCode, resultCode, data);
			switch (resultCode) {
			case 1:
				
				 alert.dismiss();
				 if (data != null) {
					 mCategoryNa.setText(data.getStringExtra("cName"));
					 checked = isNull;//����һ����¼��dialogѡ�е�λ�ã����һ����
					 categoryPKey = Integer.parseInt(data.getStringExtra("cateKey"));
				}
				break;

			}
	    }

}
