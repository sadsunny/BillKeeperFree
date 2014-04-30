package com.appxy.billkeeper.activity;

import java.util.List;

import com.appxy.billkeeper.BaseHomeActivity;
import com.appxy.billkeeper.R;
import com.appxy.billkeeper.R.layout;
import com.appxy.billkeeper.R.menu;
import com.appxy.billkeeper.db.BillKeeperSql;

import android.net.Uri;
import android.os.Bundle;
import android.R.integer;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ActionBar.LayoutParams;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddNewAccountActivity extends BaseHomeActivity {
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
	
	public int checked = -1; //categoryѡ���λ��
	
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
    	  
    	  getData();
    	  if (isNull != 0) {
    		  mCategoryName = categoryString[0];
    		  categoryPKey = categoryPK[0];
		 }
    	  mCategoryNa.setText(mCategoryName);
    	  
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
						
						new AlertDialog.Builder(AddNewAccountActivity.this)
						.setTitle("Upgrade")
						.setMessage("Only the Full version can enjoy this feature. Do you want to Upgrade! ")
						.setPositiveButton("Upgrade",
								new DialogInterface.OnClickListener() {

									@Override	
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										dialog.dismiss();
										
										final String appName = "com.appxy.billkeeperpro&hl=en";

//										List<ApplicationInfo> ls = getPackageManager().getInstalledApplications(
//														0);
//										int size = ls.size();
//										ApplicationInfo info = null;
//										for (int i = 0; i < size; i++) {
//
//											if (ls.get(i).packageName.equals("com.android.vending")) {
//												info = ls.get(i);
//											}
//										}
//										if (info != null) {
//											Intent intent = new Intent(
//													Intent.ACTION_VIEW);
//											intent.setData(Uri
//													.parse("market://details?id="+ appName));
//											intent.setPackage(info.packageName);
//											startActivity(intent);
//										} else {
//											startActivity(new Intent(
//													Intent.ACTION_VIEW,
//													Uri.parse("http://play.google.com/store/apps/details?id="
//															+ appName)));
//										}
										
										try{
											Intent intent = new Intent(Intent.ACTION_VIEW);
											intent.setData(Uri.parse("samsungapps://ProductDetail/com.appxy.billkeeperpro"));
											startActivity(intent);
										}catch (android.content.ActivityNotFoundException anfe) {
											Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://apps.samsung.com/mars/topApps/topAppsDetail.as?productId=000000788878&listYN=Y"));
											startActivity(intent);
										}
										
									}
								})
								.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										
									}
								})
								.show();
						
					}
				});
				
				getData();
				AlertDialog.Builder builder = new AlertDialog.Builder(AddNewAccountActivity.this);

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
	
	private void getData(){ //��ѯcategory��ݿ�
		
		bksql = new BillKeeperSql(this); 
		SQLiteDatabase db = bksql.getReadableDatabase();
		Cursor cursorCC = db.rawQuery("select bk_categoryName,_id from BK_Category order by BK_Category.bk_categorySequence ASC", null);
		
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
	
	 private long accountUpdateSort(long rowid, long sort){
		  bksql = new BillKeeperSql(this); 
		  SQLiteDatabase db = bksql.getReadableDatabase();
		  String id = rowid+"";
		  
		  ContentValues cv=new ContentValues();
		  cv.put("bk_accountSequence", sort);
		  
		  long row=db.update("BK_Account", cv, "_id = ?", new String[] {id});
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
	        		String accountName = mAccountName.getText().toString();
	        		if (accountName == null || accountName.trim().length()==0 || accountName.trim().equals("")) {
	        			
	        			new AlertDialog.Builder(AddNewAccountActivity.this)
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
					}else if( isNull ==0 ){  //isNull ==0 ���� categoryPKey == -1
						
						new AlertDialog.Builder(AddNewAccountActivity.this)
	    				.setTitle("Warning! ")
	    				.setMessage(
	    						"Please make sure the category is not empty !")
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
						long key = addAccount(accountName,categoryPKey,aNo,web,mPhoneNo,memo);
						
						accountUpdateSort(key,key);
						String zpk = Long.toString(key);
						
						Intent intent = new Intent();
						intent.putExtra("aName", accountName);
						intent.putExtra("pKey", zpk);
						setResult(0, intent);
						finish();
						
					}
	        		break;
	        	}
	        }
	    };
	    
	    private long addAccount(String name,int categoryKey, String accountNo , String website, String phoneNo, String memo){
			
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
			
			long row= db.insert("BK_Account", null, cv);
			ContentValues cv1=new ContentValues();
			cv1.put("bk_accountSequence", row);	
			String whereClause = "_id = "+row;
			db.update("BK_Account", cv1, "_id = ?", new String[] {row+""});
			
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
					 Log.v("mdb", categoryPKey+"category����");
				}
				break;

			}
	    }

}
