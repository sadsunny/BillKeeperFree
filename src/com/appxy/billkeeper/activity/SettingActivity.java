package com.appxy.billkeeper.activity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appxy.billkeeper.Activity_ChangePass;
import com.appxy.billkeeper.Activity_ClosePass;
import com.appxy.billkeeper.Activity_Login;
import com.appxy.billkeeper.Activity_SetPass;
import com.appxy.billkeeper.BaseHomeActivity;
import com.appxy.billkeeper.R;
import com.appxy.billkeeper.adapter.DialogCurrencyAdapter;
import com.appxy.billkeeper.db.BillKeeperSql;
import com.appxy.billkeeper.entity.Common;
import com.appxy.billkeeper.entity.RecurringEvent;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import android.R.integer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView.FixedViewInfo;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class SettingActivity extends BaseHomeActivity {
	
	public static final String SETTING_ID = "1";
	public static final String PREFS_NAME = "SAVE_INFO";
	private Switch passcode_switch ;
	private RelativeLayout currency_RelativeLayout;
	private TextView currency_TextView;
	private RelativeLayout management_RelativeLayout;
//	private Switch badges_switch;
	private RelativeLayout export_RelativeLayout;
	private RelativeLayout feedback_RelativeLayout;
	
	private RelativeLayout migrate_RelativeLayout;
	private RelativeLayout update_RelativeLayout;
	
	private int isPasscode;
	private String info;
	private LayoutInflater inflater; 
	private ListView currencyListView;
	private AlertDialog currencyDialog;
	private BillKeeperSql bksql; 
	private Map<String, Object> dataMap;
	
	private int currencyPosition = 148;
	private int badgeStatus = 0;
	private int listPositionSelect;
	CreateFiles createFiles ;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
//		getActionBar().setDisplayShowHomeEnabled(false);
//		getActionBar().setDisplayShowTitleEnabled(true);
		
//		int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
//		TextView title = (TextView) findViewById(titleId);
//		title.setTextColor(this.getResources().getColor(R.color.white));
		createFiles = new CreateFiles();
		
		passcode_switch = (Switch)findViewById(R.id.passcode_switch);
		currency_RelativeLayout = (RelativeLayout)findViewById(R.id.currency_RelativeLayout);
		currency_TextView = (TextView)findViewById(R.id.currency_TextView);
		management_RelativeLayout = (RelativeLayout)findViewById(R.id.management_RelativeLayout);
//		badges_switch = (Switch)findViewById(R.id.badges_switch);
		export_RelativeLayout = (RelativeLayout)findViewById(R.id.export_RelativeLayout);
		feedback_RelativeLayout = (RelativeLayout)findViewById(R.id.feedback_RelativeLayout);
		migrate_RelativeLayout = (RelativeLayout)findViewById(R.id.migrate_RelativeLayout);
		update_RelativeLayout = (RelativeLayout)findViewById(R.id.update_RelativeLayout);
		inflater= LayoutInflater.from(this);
		dataMap = new HashMap<String, Object>();
		
		getSettingData(); //��ȡsetting��ݿ�
		if (dataMap != null) {
			currencyPosition = (Integer) dataMap.get("bk_settingCurrency");
			badgeStatus = (Integer) dataMap.get("bk_settingBadge");
		}
		listPositionSelect = currencyPosition;
		currency_TextView.setText(Common.CURRENCY_SIGN[currencyPosition]+" "+Common.CURRENCY_NAME[currencyPosition]);
		
//		if (badgeStatus == 1) {
//			badges_switch.setChecked(true);
//		} else {
//			badges_switch.setChecked(false);
//		}
		
		SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);  
        isPasscode = sharedPreferences.getInt("isPasscode", 0); //���ȡ������Ĭ��ֵΪ1  
        
        if (isPasscode==1) {
        	passcode_switch.setChecked(true);
		} else {
			passcode_switch.setChecked(false);
		}
        
		passcode_switch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				
				SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);  
		        int isPass = sharedPreferences.getInt("isPasscode", 0); //���ȡ������Ĭ��ֵΪ1  
				
				if (isChecked && isPass != 1 ) {
					Intent intent = new Intent(SettingActivity.this, Activity_SetPass.class);
					startActivityForResult(intent, 11);
				} else if( !isChecked && isPass == 1){
					
					 Intent intent = new Intent(SettingActivity.this, Activity_ChangePass.class);
					 startActivityForResult(intent, 22);
				}
			}
		});
		
	    migrate_RelativeLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
				builder.setTitle("Migrate data to full version");
				builder.setMessage("Please make sure that both the latest free and full version are on your device. Warning: This will replace all data in the Full Version!");
				builder.setPositiveButton("No",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {

							}
						});
				builder.setNegativeButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								if (checkBrowser("com.appxy.billkeeperpro")) {
										try {
											createFiles.CreateAccount();
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										
										try {
											createFiles.CreateBill();
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										
										try {
											createFiles.CreateBillO();
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										
										try {
											createFiles.CreateCategory();
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										
										try {
											createFiles.CreatePay();
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										createFiles.printAccount();
										createFiles.printBill();
										createFiles.printBillO();
										createFiles.printCategory();
										createFiles.printPay();
										
										Intent intent = new Intent();
										intent.putExtra("howtoin", 1);
										ComponentName cn = new ComponentName(
												"com.appxy.billkeeperpro",
												"com.appxy.billkeeperpro.FirstStartActivity");
										intent.setComponent(cn);
										startActivity(intent);
										// finish();

								} else {

									AlertDialog.Builder builder1 = new AlertDialog.Builder(
											SettingActivity.this);
									builder1.setTitle("No Full Version was found on this device!");
									builder1.setMessage("Would you like to get the Full Version and then transfer data to it?");
									builder1.setPositiveButton("No, thanks",
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {

												}
											});
									builder1.setNegativeButton("OK",
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(DialogInterface dialog,
														int which) {
													// TODO Auto-generated method
													// stub

													// System.exit(1);
													final String appName = "com.appxy.billkeeperpro&hl=en";

													List<ApplicationInfo> ls = getPackageManager()
															.getInstalledApplications(
																	0);
													int size = ls.size();
													ApplicationInfo info = null;
													for (int i = 0; i < size; i++) {

														if (ls.get(i).packageName
																.equals("com.android.vending")) {
															info = ls.get(i);

														}
													}
													if (info != null) {
														Intent intent = new Intent(
																Intent.ACTION_VIEW);
														intent.setData(Uri
																.parse("market://details?id="
																		+ appName));
														intent.setPackage(info.packageName);
														startActivity(intent);
													} else {
														startActivity(new Intent(
																Intent.ACTION_VIEW,
																Uri.parse("http://play.google.com/store/apps/details?id="
																		+ appName)));
													}

												}
											});
									builder1.create().show();

								}
								// System.exit(1);
							}
						});
				builder.create().show();
				
			}
		});
	     
	   update_RelativeLayout.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			final String appName = "com.appxy.billkeeperpro&hl=en";

//			List<ApplicationInfo> ls = getPackageManager().getInstalledApplications(
//							0);
//			int size = ls.size();
//			ApplicationInfo info = null;
//			for (int i = 0; i < size; i++) {
//
//				if (ls.get(i).packageName.equals("com.android.vending")) {
//					info = ls.get(i);
//				}
//			}
//			if (info != null) {
//				Intent intent = new Intent(
//						Intent.ACTION_VIEW);
//				intent.setData(Uri
//						.parse("market://details?id="+ appName));
//				intent.setPackage(info.packageName);
//				startActivity(intent);
//			} else {
//				startActivity(new Intent(
//						Intent.ACTION_VIEW,
//						Uri.parse("http://play.google.com/store/apps/details?id="
//								+ appName)));
//			}
			
			try{
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("samsungapps://ProductDetail/com.appxy.billkeeperpro"));
				startActivity(intent);
			}catch (android.content.ActivityNotFoundException anfe) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://apps.samsung.com/mars/topApps/topAppsDetail.as?productId=000000788878&listYN=Y"));
				startActivity(intent);
			}
			
			
		}
	   });
	   
		
		currency_RelativeLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				View view = inflater.inflate(R.layout.dialog_set_currency, null);
				currencyListView = (ListView)view.findViewById(R.id.mListView);
				DialogCurrencyAdapter dcAdapter = new DialogCurrencyAdapter(SettingActivity.this);
				currencyListView.setAdapter(dcAdapter);
				currencyListView.setSelection(listPositionSelect);
				currencyListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						currencyDialog.dismiss();
						currency_TextView.setText(Common.CURRENCY_SIGN[arg2]+" "+Common.CURRENCY_NAME[arg2]);
						listPositionSelect = arg2;
						Common.CURRENCY = arg2;
						currencyUpdate(arg2);
					}
				});
				
				AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
				builder.setView(view);
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
				currencyDialog = builder.create();
				currencyDialog.show();
				
			}
		});
		
		management_RelativeLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SettingActivity.this, EditCategoryActivity.class);
				startActivity(intent);
			}
		});
		
//		badges_switch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				// TODO Auto-generated method stub
//				
//				if (isChecked) {
//					badgesUpdate(1);
//				} else {
//					badgesUpdate(0);
//				}
//			}
//		});
		
		export_RelativeLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(SettingActivity.this, ExportActivity.class);
				startActivity(intent);
			}
		});
		
		feedback_RelativeLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				fillinfo();
				
				Intent email = new Intent(android.content.Intent.ACTION_SEND);
				email.setType("plain/text");
				String[] emailReciver = new String[] { "billkeeper.a@appxy.com" };
				String emailSubject = "Billkeeper+ Feedback";

				List<Intent> targetedShareIntents = new ArrayList<Intent>();
				List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(email, 0);
				if (!resInfo.isEmpty()){
					for (ResolveInfo minfo : resInfo) {
						Intent targetedShare=new Intent(Intent.ACTION_SEND);
						
						targetedShare.putExtra(android.content.Intent.EXTRA_EMAIL, emailReciver);
						targetedShare.putExtra(android.content.Intent.EXTRA_SUBJECT, emailSubject);
						targetedShare.putExtra(android.content.Intent.EXTRA_TEXT, info);
						targetedShare.setType("plain/text");
						
						if (minfo.activityInfo.packageName.toLowerCase().contains("mail") || 
								minfo.activityInfo.name.toLowerCase().contains("mail")) {

							targetedShare.setPackage(minfo.activityInfo.packageName);
							targetedShareIntents.add(targetedShare);
						}
					}
					if(targetedShareIntents.size()>0){
						Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "Export");
						chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
						startActivityForResult(chooserIntent,3);
					}else{
						Toast.makeText(SettingActivity.this, "Can't find mail application", Toast.LENGTH_SHORT).show();
					}
				}
				
//				startActivity(email);
			}
		});
		
	    getActionBar().setDisplayHomeAsUpEnabled(true);//�Ƿ���ʾ���Ͻǵ���Ƿ��ؼ�
	}
	/*
	 * *********************
	 */
	
   private void getSettingData(){
	   
	    dataMap.clear();
		bksql = new BillKeeperSql(this); 
		SQLiteDatabase db = bksql.getReadableDatabase();
		String sql = "select bk_settingBadge , bk_settingCurrency ,bk_settingPasscode from BK_Seeting where _id = "+SETTING_ID;
		Cursor cursorEA = db.rawQuery(sql,null);
		while (cursorEA.moveToNext()) {
			
			  int bk_settingBadge = cursorEA.getInt(0);
			  int bk_settingCurrency = cursorEA.getInt(1);
			  String bk_settingPasscode = cursorEA.getString(2);
			  
			  dataMap.put("bk_settingBadge", bk_settingBadge);
			  dataMap.put("bk_settingCurrency", bk_settingCurrency);
			  dataMap.put("bk_settingPasscode", bk_settingPasscode);
		}
		cursorEA.close();
		db.close();
		
	}
   
   public class CreateFiles {

		public void CreateAccount() throws IOException {
			
			String dirStorage= Environment.getExternalStorageDirectory().getPath();
			Log.v("mmes", "dirStorage"+dirStorage);
			File file = new File("/sdcard/BillKeeperData/");
			if (!file.exists()) {
				try {
					file.mkdirs();
					Log.v("mmes", "创建文件夹");
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println("Exception");
				}
			}
			File path;
			path = new File("/sdcard/BillKeeperData/");
			File dir = null;

			dir = new File(path, "billkeeperaccount.txt");

			if (!dir.exists()) {
				try {
					dir.createNewFile();
					Log.v("mmes", "创建文件txt");
				} catch (Exception e) {
					System.out.println("Exception");
				}
			} else {
				dir.delete();
			}

		}

		public void printAccount() {

			FileWriter fw = null;
			BufferedWriter bw = null;
			String datetime = "";
			
			Log.v("mmes", "开始写文件");
			try {
				fw = new FileWriter("/sdcard/BillKeeperData/"
						+ "billkeeperaccount.txt", true);//
				bw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream("/sdcard/BillKeeperData/"
								+ "billkeeperaccount.txt"), "UTF-8"));
				
				Log.v("mmes", "读取fw"+fw);
				
				
				List<Map<String, Object>> dataList = getAccountData();
				if (dataList != null && dataList.size() >0) {
					
					for (Map<String, Object> iMap:dataList) {
						 	
						    String id = (String)iMap.get("id");
						    String bk_accountMemo = (String) iMap.get("bk_accountMemo");
						    String bk_accountName = (String) iMap.get("bk_accountName");
						    String bk_accountNo = (String) iMap.get("bk_accountNo");
						    String bk_accountWebsite = (String) iMap.get("bk_accountWebsite");
						    String cb_accountPhoneNo = (String) iMap.get("cb_accountPhoneNo");
						    int accounthasCategory = (Integer) iMap.get("accounthasCategory");
						    int bk_accountSequence = (Integer) iMap.get("bk_accountSequence");
						    
						    bw.write(id+"");
							bw.write(",@！￥");
						    bw.write(bk_accountMemo+"");
							bw.write(",@！￥");
							bw.write(bk_accountName+"");
							bw.write(",@！￥");
							bw.write(bk_accountNo+"");
							bw.write(",@！￥");
							bw.write(bk_accountWebsite+"");
							bw.write(",@！￥");
							bw.write(cb_accountPhoneNo+"");
							bw.write(",@！￥");
							bw.write(accounthasCategory+"");
							bw.write(",@！￥");
							bw.write(bk_accountSequence+"");
							bw.write(",！￥@");
					}
				}
				bw.flush(); 
				bw.close();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					Log.v("mmes", "异常抛出");
					bw.close();
					fw.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
				}
			}
		}
		/*
		 * 以上account
		 */
		
		public void CreateBill() throws IOException {
			File file = new File("/sdcard/BillKeeperData/");
			if (!file.exists()) {
				try {
					file.mkdirs();
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println("Exception");
				}
			}
			File path;
			path = new File("/sdcard/BillKeeperData/");
			File dir = null;

			dir = new File(path, "billkeeperbill.txt");

			if (!dir.exists()) {
				try {
					dir.createNewFile();
				} catch (Exception e) {
					System.out.println("Exception");
				}
			} else {
				dir.delete();
			}

		}

		public void printBill() {

			FileWriter fw = null;
			BufferedWriter bw = null;
			String datetime = "";

			try {
				fw = new FileWriter("/sdcard/BillKeeperData/"
						+ "billkeeperbill.txt", true);//
				bw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream("/sdcard/BillKeeperData/"
								+ "billkeeperbill.txt"), "UTF-8"));
				
				List<Map<String, Object>> dataList = getBillData();
				if (dataList != null && dataList.size() >0) {
					
					for (Map<String, Object> iMap:dataList) {
						
						 String id = (String)iMap.get("id");
				         double billamount = (Double)iMap.get("nbillamount");
				         int bk_billAmountUnknown = (Integer)iMap.get("nbk_billAmountUnknown");
				         int bk_billAutoPay = (Integer)iMap.get("nbk_billAutoPay");
				         long bk_billDuedate = (Long)iMap.get("nbk_billDuedate"); //�൱���¼��Ŀ�ʼ����
				         long bk_billEndDate =  (Long)iMap.get("nbk_billEndDate"); //�ظ��¼��Ľ�ֹ����
				         int bk_billisReminder = (Integer)iMap.get("nbk_billisReminder");
				         int bk_billisRepeat = (Integer)iMap.get("nbk_billisRepeat");
				         int bk_billisVariaable = (Integer)iMap.get("bk_billisVariaable");
				         long bk_billReminderDate = (Long)iMap.get("nbk_billReminderDate");
				         long bk_billReminderTime = (Long)iMap.get("bk_billReminderTime");
				         int bk_billRepeatNumber = (Integer)iMap.get("nbk_billRepeatNumber"); 
				         int bk_billRepeatType = (Integer)iMap.get("nbk_billRepeatType"); 
				         int billhasAccount = (Integer)iMap.get("billhasAccount");
						    
				            bw.write(id);
							bw.write(",@！￥"+"");
						    bw.write(billamount+"");
							bw.write(",@！￥");
							bw.write(bk_billAmountUnknown+"");
							bw.write(",@！￥");
							bw.write(bk_billAutoPay+"");
							bw.write(",@！￥");
							bw.write(bk_billDuedate+"");
							bw.write(",@！￥");
							bw.write(bk_billEndDate+"");
							bw.write(",@！￥");
							bw.write(bk_billisReminder+"");
							bw.write(",@！￥");
							bw.write(bk_billisRepeat+"");
							bw.write(",@！￥");
								bw.write(bk_billisVariaable+"");
								bw.write(",@！￥");
								bw.write(bk_billReminderDate+"");
								bw.write(",@！￥");
								bw.write(bk_billReminderTime+"");
								bw.write(",@！￥");
								bw.write(bk_billRepeatNumber+"");
								bw.write(",@！￥");
								bw.write(bk_billRepeatType+"");
								bw.write(",@！￥");
								bw.write(billhasAccount+"");
							    bw.write(",！￥@");
					}
				}
				bw.flush(); 
				bw.close();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					bw.close();
					fw.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
				}
			}
		}
		/*
		 * 以上是BK_Bill
		 */
		public void CreateBillO() throws IOException {
			File file = new File("/sdcard/BillKeeperData/");
			if (!file.exists()) {
				try {
					file.mkdirs();
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println("Exception");
				}
			}
			File path;
			path = new File("/sdcard/BillKeeperData/");
			File dir = null;

			dir = new File(path, "billkeeperbillo.txt");

			if (!dir.exists()) {
				try {
					dir.createNewFile();
				} catch (Exception e) {
					System.out.println("Exception");
				}
			} else {
				dir.delete();
			}

		}

		public void printBillO() {

			FileWriter fw = null;
			BufferedWriter bw = null;
			String datetime = "";

			try {
				fw = new FileWriter("/sdcard/BillKeeperData/"
						+ "billkeeperbillo.txt", true);//
				bw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream("/sdcard/BillKeeperData/"
								+ "billkeeperbillo.txt"), "UTF-8"));
				
				List<Map<String, Object>> dataList = getBillDataO();
				if (dataList != null && dataList.size() >0) {
					
					for (Map<String, Object> iMap:dataList) {
						
						 String id = (String)iMap.get("id");
						 int bk_billsDelete = (Integer)iMap.get("bk_billsDelete");
				         double billamount = (Double)iMap.get("nbillamount");
				         int bk_billAmountUnknown = (Integer)iMap.get("nbk_billAmountUnknown");
				         int bk_billAutoPay = (Integer)iMap.get("nbk_billAutoPay");
				         long bk_billDuedate = (Long)iMap.get("nbk_billDuedate");
				         long bk_billDuedateNew = (Long)iMap.get("bk_billDuedateNew");
				         long bk_billEndDate =  (Long)iMap.get("nbk_billEndDate"); 
				         int bk_billisReminder = (Integer)iMap.get("nbk_billisReminder");
				         int bk_billisRepeat = (Integer)iMap.get("nbk_billisRepeat");
				         int bk_billisVariaable = (Integer)iMap.get("bk_billisVariaable");
				         long bk_billReminderDate = (Long)iMap.get("nbk_billReminderDate");
				         long bk_billReminderTime = (Long)iMap.get("bk_billReminderTime");
				         int bk_billRepeatNumber = (Integer)iMap.get("nbk_billRepeatNumber"); 
				         int bk_billRepeatType = (Integer)iMap.get("nbk_billRepeatType"); 
				         int billObjecthasBill = (Integer)iMap.get("billObjecthasBill");
				         int billhasAccount = (Integer)iMap.get("billhasAccount");
				        
				            bw.write(id+"");
							bw.write(",@！￥");
				            bw.write(bk_billsDelete+"");
							bw.write(",@！￥");
						    bw.write(billamount+"");
							bw.write(",@！￥");
							bw.write(bk_billAmountUnknown+"");
							bw.write(",@！￥");
							bw.write(bk_billAutoPay+"");
							bw.write(",@！￥");
							bw.write(bk_billDuedate+"");
							bw.write(",@！￥");
							bw.write(bk_billDuedateNew+"");
							bw.write(",@！￥");
							bw.write(bk_billEndDate+"");
							bw.write(",@！￥");
							bw.write(bk_billisReminder+"");
							bw.write(",@！￥");
							bw.write(bk_billisRepeat+"");
							bw.write(",@！￥");
								bw.write(bk_billisVariaable+"");
								bw.write(",@！￥");
								bw.write(bk_billReminderDate+"");
								bw.write(",@！￥");
								bw.write(bk_billReminderTime+"");
								bw.write(",@！￥");
								bw.write(bk_billRepeatNumber+"");
								bw.write(",@！￥");
								bw.write(bk_billRepeatType+"");
								bw.write(",@！￥");
								bw.write(billObjecthasBill+"");
								bw.write(",@！￥");
								bw.write(billhasAccount+"");
							    bw.write(",！￥@");
					}
				}
				bw.flush(); 
				bw.close();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					bw.close();
					fw.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
				}
			}
		}
		/*
		 * 以上是BK_BillO
		 */
		
		public void CreateCategory() throws IOException {
			File file = new File("/sdcard/BillKeeperData/");
			if (!file.exists()) {
				try {
					file.mkdirs();
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println("Exception");
				}
			}
			File path;
			path = new File("/sdcard/BillKeeperData/");
			File dir = null;

			dir = new File(path, "billkeepercategory.txt");

			if (!dir.exists()) {
				try {
					dir.createNewFile();
				} catch (Exception e) {
					System.out.println("Exception");
				}
			} else {
				dir.delete();
			}

		}

		public void printCategory() {

			FileWriter fw = null;
			BufferedWriter bw = null;
			String datetime = "";

			try {
				fw = new FileWriter("/sdcard/BillKeeperData/"
						+ "billkeepercategory.txt", true);//
				bw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream("/sdcard/BillKeeperData/"
								+ "billkeepercategory.txt"), "UTF-8"));
				
				List<Map<String, Object>> dataList = getCategoryData();
				if (dataList != null && dataList.size() >0) {
					
					for (Map<String, Object> iMap:dataList) {
						
						    String id = (String)iMap.get("id");
						    String bk_categoryIconName = (String) iMap.get("bk_categoryIconName");
						    String bk_categoryName = (String)iMap.get("bk_categoryName");
						    String bk_categorySequence = (String) iMap.get("bk_categorySequence");
				        
						    bw.write(id+"");
							bw.write(",@！￥");
				            bw.write(bk_categoryIconName+"");
							bw.write(",@！￥");
						    bw.write(bk_categoryName+"");
							bw.write(",@！￥");
							bw.write(bk_categorySequence+"");
						    bw.write(",！￥@");
					}
				}
				bw.flush(); 
				bw.close();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					bw.close();
					fw.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
				}
			}
		}
		/*
		 * 以上是Category
		 */
		
		public void CreatePay() throws IOException {
			File file = new File("/sdcard/BillKeeperData/");
			if (!file.exists()) {
				try {
					file.mkdirs();
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println("Exception");
				}
			}
			File path;
			path = new File("/sdcard/BillKeeperData/");
			File dir = null;

			dir = new File(path, "billkeeperpay.txt");

			if (!dir.exists()) {
				try {
					dir.createNewFile();
				} catch (Exception e) {
					System.out.println("Exception");
				}
			} else {
				dir.delete();
			}

		}

		public void printPay() {

			FileWriter fw = null;
			BufferedWriter bw = null;
			String datetime = "";

			try {
				fw = new FileWriter("/sdcard/BillKeeperData/"
						+ "billkeeperpay.txt", true);//
				bw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream("/sdcard/BillKeeperData/"
								+ "billkeeperpay.txt"), "UTF-8"));
				
				List<Map<String, Object>> dataList = getPayData();
				if (dataList != null && dataList.size() >0) {
					
					for (Map<String, Object> iMap:dataList) {
						
						String id = (String)iMap.get("id");
						double bk_payAmount = (Double) iMap.get("bk_payAmount");
						long bk_payDate = (Long) iMap.get("bk_payDate");
						String bk_payMemo = (String) iMap.get("bk_payMemo");
						int bk_payMode =  (Integer) iMap.get("bk_payMode");
						int paymenthasBillO =  (Integer) iMap.get("paymenthasBillO");
						int paymenthasBill =  (Integer) iMap.get("paymenthasBill");
						
						    bw.write(id+"");
							bw.write(",@！￥");
				            bw.write(bk_payAmount+"");
							bw.write(",@！￥");
						    bw.write(bk_payDate+"");
							bw.write(",@！￥");
							bw.write(bk_payMemo+"");
							bw.write(",@！￥");
							bw.write(bk_payMode+"");
							bw.write(",@！￥");
						    bw.write(paymenthasBillO+"");
							bw.write(",@！￥");
							bw.write(paymenthasBill+"");
						    bw.write(",！￥@");
					}
				}
				bw.flush(); 
				bw.close();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					bw.close();
					fw.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
				}
			}
		}
		/*
		 * 以上是Category
		 */
   }
   
   public  List<Map<String, Object>> getPayData(){
	   List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
       Map<String, Object> map;
       bksql = new BillKeeperSql(this);
  	   SQLiteDatabase db = bksql.getReadableDatabase();
  	   
  	    String sql1 = "select * from BK_Payment"; 
		Cursor cursor = db.rawQuery(sql1, null);
		while (cursor.moveToNext()) {
			map = new HashMap<String, Object>();

		    String id = cursor.getString(0);
			double bk_payAmount = cursor.getDouble(1);
			long bk_payDate = cursor.getLong(6);
			String bk_payMemo = cursor.getString(7);
			int bk_payMode =  cursor.getInt(8);
			int paymenthasBillO =  cursor.getInt(12);
			int paymenthasBill =  cursor.getInt(13);
			
			    map.put("id", id);
				map.put("bk_payAmount", bk_payAmount);
				map.put("bk_payDate", bk_payDate);
				map.put("bk_payMemo", bk_payMemo);
				map.put("bk_payMode", bk_payMode);
				map.put("paymenthasBillO", paymenthasBillO);
				map.put("paymenthasBill", paymenthasBill);
				
				dataList.add(map);
		}
		return dataList;
   }
   
   public  List<Map<String, Object>> getCategoryData()
   {
	   List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
       Map<String, Object> map;
       bksql = new BillKeeperSql(this);
  	   SQLiteDatabase db = bksql.getReadableDatabase();
  	    
  	    Cursor cursorEA = db.rawQuery("select _id, bk_categoryIconName, bk_categoryName ,bk_categorySequence from BK_Category" , null);
  	    while (cursorEA.moveToNext()) {
      
           map = new HashMap<String, Object>();
           
           String id = cursorEA.getString(0); 
           String bk_categoryIconName = cursorEA.getString(1);
           String bk_categoryName = cursorEA.getString(2); 
  		   String bk_categorySequence = cursorEA.getString(3);
  		 
  		   map.put("id", id);
  		    map.put("bk_categoryIconName", bk_categoryIconName);
            map.put("bk_categoryName", bk_categoryName);
            map.put("bk_categorySequence", bk_categorySequence);
			dataList.add(map);
       }
  	     cursorEA.close();
  	     db.close();
  	     return dataList;
   }
   
   public List<Map<String, Object>> getBillDataO(){ 
	    List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
	    Map<String, Object> bMap;
	    bksql = new BillKeeperSql(this); 
	  	SQLiteDatabase db = bksql.getReadableDatabase();
	  	String  sql = "select BK_BillObject.* from BK_BillObject";
	  	
	    Cursor cursorEA = db.rawQuery(sql, null);
	    while (cursorEA.moveToNext()) {
	    	
	     bMap = new HashMap<String, Object>();
	     
	     String id = cursorEA.getString(0);  
	    int bk_billsDelete = cursorEA.getInt(1); 
	    double billamount = cursorEA.getDouble(2);
	    int bk_billAmountUnknown = cursorEA.getInt(3);
        int bk_billAutoPay = cursorEA.getInt(4);
        long bk_billDuedate = cursorEA.getLong(7);
        long bk_billDuedateNew = cursorEA.getLong(8);
        long bk_billEndDate = cursorEA.getLong(9);
        int bk_billisReminder = cursorEA.getInt(10);
        int bk_billisRepeat = cursorEA.getInt(11);
        int bk_billisVariaable = cursorEA.getInt(12);
        long bk_billReminderDate = cursorEA.getLong(14);
        long bk_billReminderTime = cursorEA.getLong(15);
        int bk_billRepeatNumber = cursorEA.getInt(16);
        int bk_billRepeatType = cursorEA.getInt(17);
        int billObjecthasBill = cursorEA.getInt(23);
        int billhasAccount = cursorEA.getInt(24);
        
        bMap.put("id", id);
        bMap.put("bk_billsDelete", bk_billsDelete); 
        bMap.put("nbillamount", billamount);
        bMap.put("nbk_billAmountUnknown", bk_billAmountUnknown);
        bMap.put("nbk_billAutoPay", bk_billAutoPay);
        bMap.put("nbk_billDuedate", bk_billDuedate);
        bMap.put("bk_billDuedateNew", bk_billDuedateNew); 
        bMap.put("nbk_billEndDate", bk_billEndDate);
        bMap.put("nbk_billisReminder", bk_billisReminder);
        bMap.put("nbk_billisRepeat", bk_billisRepeat);
        bMap.put("bk_billisVariaable", bk_billisVariaable);
        bMap.put("nbk_billReminderDate", bk_billReminderDate);
        bMap.put("bk_billReminderTime", bk_billReminderTime);
        bMap.put("nbk_billRepeatNumber", bk_billRepeatNumber);
        bMap.put("nbk_billRepeatType", bk_billRepeatType);
        bMap.put("billObjecthasBill", billObjecthasBill); 
        bMap.put("billhasAccount", billhasAccount);
        
        dataList.add(bMap);
	    }
	    cursorEA.close();  
	    db.close();
	   
	return dataList;
}
   
   
   public List<Map<String, Object>> getBillData()
   {
	  List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
	  bksql = new BillKeeperSql(this);
  	  SQLiteDatabase db = bksql.getReadableDatabase();
  	  Map<String, Object> map;
	  String sql = "select BK_Bill.* from BK_Bill";
  	  
	  Cursor cursorEA = db.rawQuery(sql, null);
  	    while (cursorEA.moveToNext()) {
           map = new HashMap<String, Object>();
           
           String id = cursorEA.getString(0);
  	       double billamount = cursorEA.getDouble(2);
  	       int bk_billAmountUnknown = cursorEA.getInt(3);
           int bk_billAutoPay = cursorEA.getInt(4);
           long bk_billDuedate = cursorEA.getLong(8);
           long bk_billEndDate = cursorEA.getLong(9);
           int bk_billisReminder = cursorEA.getInt(10);
           int bk_billisRepeat = cursorEA.getInt(11);
           int bk_billisVariaable = cursorEA.getInt(12);
           long bk_billReminderDate = cursorEA.getLong(14);
           long bk_billReminderTime = cursorEA.getLong(15);
           int bk_billRepeatNumber = cursorEA.getInt(16);
           int bk_billRepeatType = cursorEA.getInt(17);
           int billhasAccount = cursorEA.getInt(24);
          
           map.put("id", id);
           map.put("nbillamount", billamount);
           map.put("nbk_billAmountUnknown", bk_billAmountUnknown);
           map.put("nbk_billAutoPay", bk_billAutoPay);
           map.put("nbk_billDuedate", bk_billDuedate);
           map.put("nbk_billEndDate", bk_billEndDate);
           map.put("nbk_billisReminder", bk_billisReminder);
           map.put("nbk_billisRepeat", bk_billisRepeat);
           map.put("bk_billisVariaable", bk_billisVariaable);
           map.put("nbk_billReminderDate", bk_billReminderDate);
           map.put("bk_billReminderTime", bk_billReminderTime);
           map.put("nbk_billRepeatNumber", bk_billRepeatNumber);
           map.put("nbk_billRepeatType", bk_billRepeatType);
           map.put("billhasAccount", billhasAccount);
           
	        dataList.add(map);
	        
       }
  	 cursorEA.close();
  	 db.close();
     return dataList;
}
   
   
   public List<Map<String, Object>> getAccountData() {
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		bksql = new BillKeeperSql(this);
		SQLiteDatabase db = bksql.getReadableDatabase();
		Map<String, Object> map;
		String sql = "select * from BK_Account";
		Cursor cursorEA = db.rawQuery(sql, null);
		while (cursorEA.moveToNext()) {
			map = new HashMap<String, Object>();
				
				String id = cursorEA.getString(0);
			    String bk_accountMemo = cursorEA.getString(3); 
	            String bk_accountName = cursorEA.getString(4); 
	            String bk_accountNo = cursorEA.getString(5); 
	            String bk_accountWebsite = cursorEA.getString(8); 
	            String cb_accountPhoneNo = cursorEA.getString(11); 
	            int accounthasCategory = cursorEA.getInt(13);
	   		    int bk_accountSequence = cursorEA.getInt(16);
	   		    
	   		    map.put("id", id);
	            map.put("bk_accountMemo", bk_accountMemo);
	            map.put("bk_accountName", bk_accountName);
	            map.put("bk_accountNo", bk_accountNo);
				map.put("bk_accountWebsite", bk_accountWebsite);
				map.put("cb_accountPhoneNo", cb_accountPhoneNo);
		        map.put("accounthasCategory", accounthasCategory);
			    map.put("bk_accountSequence", bk_accountSequence);
				dataList.add(map);
		}
		cursorEA.close();
		db.close();
		return dataList;
	}

   
   public boolean checkBrowser(String packageName) {
		if (packageName == null || "".equals("com.appxy.billkeeperpro"))
			return false;
		try {
			ApplicationInfo info = getPackageManager().getApplicationInfo(
					packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}
   
   private long currencyUpdate(int position){
		  bksql = new BillKeeperSql(this); 
		  SQLiteDatabase db = bksql.getReadableDatabase();
		  
		  ContentValues cv=new ContentValues();
		  cv.put("bk_settingCurrency", position);
		  long row=db.update("BK_Seeting", cv, "_id = ?", new String[] {SETTING_ID});
		  
		  db.close();
		  return row;
		  
	  }
   
	public void fillinfo() {
		info = "";

		info += "Model : " + android.os.Build.MODEL + "\n";
		info += "Release : " + android.os.Build.VERSION.RELEASE + "\n";

		info += "App : " + getVersion() + "\n";
	}
	
	public String getVersion() {

		PackageManager manager = this.getPackageManager();
		PackageInfo info = null;
		try {
			info = manager.getPackageInfo(this.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String version = info.versionName;
		return version;

	}
	
	
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:  //���Ͻǰ�ť���ص�id
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	 @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);  
        int  isPass = sharedPreferences.getInt("isPasscode", 0); //���ȡ������Ĭ��ֵΪ1  
        
        if (isPass==1) {
        	passcode_switch.setChecked(true);
		} else {
			passcode_switch.setChecked(false);
		}
        
	}
	 
	
}
