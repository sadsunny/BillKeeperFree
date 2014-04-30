package com.appxy.billkeeper.activity;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.appxy.billkeeper.BaseHomeActivity;
import com.appxy.billkeeper.R;
import com.appxy.billkeeper.R.layout;
import com.appxy.billkeeper.R.menu;
import com.appxy.billkeeper.db.BillKeeperSql;
import com.appxy.billkeeper.entity.Common;
import com.appxy.billkeeper.entity.RecurringEventByAccountIdTime;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.html.simpleparser.StyleSheet;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

public class ExportActivity extends BaseHomeActivity {
	
		private  final String mHtmlHead = "<!DOCTYPE html> <html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><style type=\"text/css\"></style></head>"  ;
		private  final String mHtmlBodyFirst = "<body>"
	   +"<table cellpadding=\"0\" cellspacing=\"0\" style=\"border:solid 1px #b0b0b0;text-align:center;border-collapse:collapse;\">"
		+"<tbody>"
			+"<tr height=\"30\" style=\"background-color:#75b8f0;\">"
				+"<td width=\"\" style=\"border:solid 1px #b0b0b0;padding: 5px;\"><font size=\"2\"> Account Name </font></td>"
				+"<td width=\"\" style=\"border:solid 1px #b0b0b0;padding: 5px;\"><font size=\"2\"> Account Number </font></td>"
				+"<td width=\"\" style=\"border:solid 1px #b0b0b0;padding: 5px;\"><font size=\"2\"> Due Date </font></td>"
				+"<td width=\"\" style=\"border:solid 1px #b0b0b0;padding: 5px;\"><font size=\"2\"> Paid/Unpaid </font></td>"
				+"<td width=\"\" style=\"border:solid 1px #b0b0b0;padding: 5px;\"><font size=\"2\"> Due Amount </font></td>"
				+"<td width=\"\" style=\"border:solid 1px #b0b0b0;padding: 5px;\"><font size=\"2\"> Amount Paid </font></td>"
				+"<td width=\"\" style=\"border:solid 1px #b0b0b0;padding: 5px;\"><font size=\"2\"> Amount Unpaid </font></td>"
			+"</tr>";  
		private  final String mHtmlEnd = "</tbody>"
				+"</table>"
				+"</body>"
				+"</html>";  
		private  final String mHtmlItemGary ="<tr height=\"30\" style=\"background-color:Silver\">";
		private  final String mHtmlItemWhite ="<tr height=\"30\">";
		
		private String mHtmlBody = "";
		private String mHtmlAll = "";
	private int sYear;
	private int sMonth;
	private int sDay;
	
	private int eYear;
	private int eMonth;
	private int eDay;
	private long startDate;
	private long endDate;
	
	private BillKeeperSql bksql;
	private int accountlength = 0; //����ݿ��ѯ��caccountname������
	private String[] accountNameArray; //����account name�����飬���dialog��
	private int[]  accountIdArray;  // ����account��������顣
	private AlertDialog selectDialog ; 
	
	private Button account_Button;
	private Button start_Button;
	private Button end_Button;
	private Button html_Button;
	private ImageButton csv_Button;
	private Button pdf_Button;
	private Dialog dialog_loading;
	
	private int accountId; ////****��δȷ���Ƿ���all
	private int accountPosition = -1;
	private String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
			"Sep", "Oct", "Nov", "Dec" };
	private List<Map<String, Object>> dataList;
	
	private int billLength = 0;
	private String nowCreat; //�ļ�����ʱ��
	private String Accountname ;
	private CreateFiles createFiles;
	private Createhtml createhtml;
	private String currencyString ;
	
	
	private int file_type = 1 ; // 1html.2csv,3pdf	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_export);
		
		currencyString = Common.CURRENCY_SIGN[Common.CURRENCY];
		createhtml =  new Createhtml();
		createFiles = new CreateFiles();
		dataList = new ArrayList<Map<String, Object>>();
		getActionBar().setDisplayHomeAsUpEnabled(true);//����actionbar������ɫ��������ɫ
		
//		getActionBar().setDisplayShowHomeEnabled(false);
//		getActionBar().setDisplayShowTitleEnabled(true);
//		int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
//		TextView title = (TextView) findViewById(titleId);
//		title.setTextColor(this.getResources().getColor(R.color.white));
		
		account_Button = (Button)findViewById(R.id.accountNa);
		start_Button = (Button)findViewById(R.id.start_Button);
		end_Button = (Button)findViewById(R.id.end_Button);
		
		html_Button = (Button)findViewById(R.id.html_Button);
		csv_Button = (ImageButton)findViewById(R.id.csv_Button);
		pdf_Button = (Button)findViewById(R.id.pdf_Button);
		
		Calendar c = Calendar.getInstance();
		sYear = c.get(Calendar.YEAR);
		sMonth = c.get(Calendar.MONTH);
		sDay = c.get(Calendar.DAY_OF_MONTH);
		
		eYear = c.get(Calendar.YEAR);
		eMonth = c.get(Calendar.MONTH);
		eDay = c.get(Calendar.DAY_OF_MONTH);
		
		getAccountData();
		if (accountlength>0) {
			account_Button.setText(accountNameArray[0]);
			accountId = accountIdArray[0];
			Accountname = accountNameArray[0];
		}
		
		Log.v("mtest", "accountId"+accountId);
		account_Button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				AlertDialog.Builder selectAccountDialog = new AlertDialog.Builder( //��һ��account��dialog
						ExportActivity.this);
				selectAccountDialog.setTitle("Select Account");
				selectAccountDialog.setSingleChoiceItems(accountNameArray, accountPosition,new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int item) {
						accountPosition = item;
						String  mAccountName = accountNameArray[item];
						accountId = accountIdArray[item];
						account_Button.setText(mAccountName);
						Accountname = mAccountName;
						dialog.dismiss();
					   
					}
				});
				selectAccountDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
				selectDialog = selectAccountDialog.create();
				selectDialog.show();
				
			}
		});
		
		updateDisplay();
		start_Button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				DatePickerDialog DPD = new DatePickerDialog( // �ı�theme
						new ContextThemeWrapper(ExportActivity.this,
								android.R.style.Theme_Holo_Light),
						mDateSetListener, sYear, sMonth, sDay);
				DPD.setTitle("Start Date");
				DPD.show();
				
			}
		});
		
		updateDisplaye();
		end_Button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				DatePickerDialog DPD = new DatePickerDialog( // �ı�theme
						new ContextThemeWrapper(ExportActivity.this,
								android.R.style.Theme_Holo_Light),
						mDateSetListenere, eYear, eMonth,eDay);
				DPD.setTitle("End Date");
				DPD.show();
				
			}
		});
		
		html_Button.setBackgroundResource(R.drawable.html_sel);
//		csv_Button.setBackgroundResource(R.drawable.csv);
		pdf_Button.setBackgroundResource(R.drawable.pdf);
		
		html_Button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				file_type = 1;
				html_Button.setBackgroundResource(R.drawable.html_sel);
//				csv_Button.setBackgroundResource(R.drawable.csv);
				pdf_Button.setBackgroundResource(R.drawable.pdf);
			}
		});
		
		/*
		 * List<ApplicationInfo> ls = getPackageManager().getInstalledApplications(
												0);
								int size = ls.size();
								ApplicationInfo info = null;
								for (int i = 0; i < size; i++) {

									if (ls.get(i).packageName.equals("com.sec.android.app.samsungapps")) { //com.sec.android.app.samsungapps  com.android.vending
										info = ls.get(i);
									}
								}
								if (info != null) {
									Intent intent = new Intent(
											Intent.ACTION_VIEW);
									intent.setData(Uri
											.parse("samsungapps://ProductDetail/"+ appName)); //  samsungapps://ProductDetail/   market://details?id=
									intent.setPackage(info.packageName);
									startActivity(intent);
								} else {
									startActivity(new Intent(
											Intent.ACTION_VIEW,
											Uri.parse("samsungapps://ProductDetail/"
													+ appName)));
								}
		 */
		
		csv_Button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub 
				
				new AlertDialog.Builder(ExportActivity.this)
				.setTitle("Upgrade")
				.setMessage("Only the Full version can enjoy this feature. Do you want to Upgrade! ")
				.setPositiveButton("Upgrade",
						new DialogInterface.OnClickListener() {

							@Override	
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
								
								final String appName = "com.appxy.billkeeperpro"; 

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
				
				file_type = 1;
				html_Button.setBackgroundResource(R.drawable.html_sel);
//				csv_Button.setBackgroundResource(R.drawable.csv);
				pdf_Button.setBackgroundResource(R.drawable.pdf);
				
				
			}
		});
		
		
		pdf_Button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				file_type = 3;
				html_Button.setBackgroundResource(R.drawable.html);
//				csv_Button.setBackgroundResource(R.drawable.csv);
				pdf_Button.setBackgroundResource(R.drawable.pdf_sel);
			}
		});
		
	}
	/*
	 * ************************
	 */
	/*����������ط�������*/
	private DatePickerDialog.OnDateSetListener mDateSetListenere = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			eYear = year;
			eMonth = monthOfYear;
			eDay = dayOfMonth;
			updateDisplaye();
		}
	};
	private void updateDisplaye() {
		// TODO Auto-generated method stub
		end_Button.setText(new StringBuilder()
				// Month is 0 based so add 1
				.append(months[eMonth]).append(" ").append(eDay).append(",")
				.append(eYear));
		String dueDateString = (new StringBuilder().append(eMonth + 1).append("-").append(eDay).append("-").append(eYear)).toString();
		
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(dueDateString));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		endDate = c.getTimeInMillis();
	}
	
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			sYear = year;
			sMonth = monthOfYear;
			sDay = dayOfMonth;
			updateDisplay();
		}
	};
	private void updateDisplay() {
		// TODO Auto-generated method stub
		start_Button.setText(new StringBuilder()
				// Month is 0 based so add 1
				.append(months[sMonth]).append(" ").append(sDay).append(",")
				.append(sYear));
		String dueDateString = (new StringBuilder().append(sMonth + 1).append("-").append(sDay).append("-").append(sYear)).toString();
		
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(dueDateString));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		startDate = c.getTimeInMillis();
	}
	
	public long getNowMillis() { //�õ�����ĺ�����
		 Date date1=new Date(); 
	     SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
	     String nowTime = formatter.format(date1);
	     Calendar c = Calendar.getInstance();
	     try {
				c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(nowTime));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	     long nowMillis = c.getTimeInMillis(); //��ȡ���������ն�Ӧ�ĺ������ȥʱ���룬�������
	     
	     return nowMillis;
	}
	
	 public long billPay(double payAmount,int payMode ,long payDate ,long paymenthasBill ) {
		 
	     bksql = new BillKeeperSql(this);
		SQLiteDatabase db = bksql.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("bk_payAmount", payAmount);
		values.put("bk_payMode", payMode);
		values.put("bk_payDate", payDate);
		values.put("paymenthasBill", paymenthasBill);
		
		db.execSQL("PRAGMA foreign_keys = ON ");
		long pkey = db.insert("BK_Payment", null, values);
		db.close();
		return pkey;
		
	}
	 
	 public long billPayO(double payAmount,int payMode ,long payDate ,long paymenthasBill ) {
		 
	     bksql = new BillKeeperSql(this);
		SQLiteDatabase db = bksql.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("bk_payAmount", payAmount);
		values.put("bk_payMode", payMode);
		values.put("bk_payDate", payDate);
		values.put("paymenthasBillO", paymenthasBill);
		
		db.execSQL("PRAGMA foreign_keys = ON ");
		long pkey = db.insert("BK_Payment", null, values);
		db.close();
		return pkey;
		
	}
	 
	 public long insertObject(int bk_billsDelete, double billamount ,int nbk_billAmountUnknown ,int bk_billAutoPay ,long bk_billDuedate,long  bk_billDuedateNew,  //���������¼�
				long bk_billEndDate, int bk_billisReminder, int bk_billisRepeat, int bk_billisVariaable,long bk_billReminderDate,long bk_billReminderTime,int bk_billRepeatNumber,
				int bk_billRepeatType,long billObjecthasBill,int billhasAccount) {

		 bksql = new BillKeeperSql(this
		);
		 SQLiteDatabase db = bksql.getWritableDatabase();

		 ContentValues values = new ContentValues();
		 values.put("bk_billsDelete", bk_billsDelete);
		 	values.put("bk_billOAmount", billamount);
		 	values.put("bk_billOAmountUnknown", nbk_billAmountUnknown);
		 	values.put("bk_billOAutoPay", bk_billAutoPay);
		 	values.put("bk_billODueDate", bk_billDuedate);
		 	values.put("bk_billODueDateNew", bk_billDuedateNew);
		 	values.put("bk_billOEndDate", bk_billEndDate);
		 	values.put("bk_billOisReminder", bk_billisReminder);
		 	values.put("bk_billOisRepeat", bk_billisRepeat);
		 	values.put("bk_billOisVariaable", bk_billisVariaable);
		 	values.put("bk_billOReminderDate", bk_billReminderDate);
		 	values.put("bk_billOReminderTime", bk_billReminderTime);
		 	values.put("bk_billORepeatNumber", bk_billRepeatNumber);
		 	values.put("bk_billORepeatType", bk_billRepeatType);
		 	values.put("billObjecthasBill", billObjecthasBill);
		 	values.put("billObjecthasAccount", billhasAccount);

		 	db.execSQL("PRAGMA foreign_keys = ON ");
		 	long okey = db.insert("BK_BillObject", null, values);
		 	db.close();
		 	return okey;

}
	 
	
	public void judgePayment(List<Map<String, Object>> dataList ){ //ʹ�ø÷����ж�pay��״̬
		  bksql = new BillKeeperSql(this);
	  	  SQLiteDatabase db = bksql.getReadableDatabase();
	  	  long nowMillis = getNowMillis();
	  	  Double mZero = 0.00;
	  	  
			for (Map<String, Object> bMap:dataList) {
				
				int flag  = (Integer) bMap.get("indexflag");
				int id = (Integer) bMap.get("BK_Bill_Id");
	      		double billamount = (Double) bMap.get("nbillamount");
	      		long billduedate = (Long) bMap.get("nbk_billDuedate");
	      		int  billAutoPay = (Integer) bMap.get("nbk_billAutoPay");
	      		int  nbk_billAmountUnknown = (Integer)bMap.get("nbk_billAmountUnknown");
	      		bMap.put("remain", mZero); //ʣ�µ����
				if (flag == 0 || flag ==1) {
					
					String  sql = "select BK_Payment._id, BK_Payment.bk_payAmount, BK_Payment.bk_payMode ,BK_Payment.paymenthasBill from BK_Payment where BK_Payment.paymenthasBill = "+id;
					
					List<Map<String, Object>> pDataList = new ArrayList<Map<String, Object>>();
					double zero= 0.00f; 
				    BigDecimal b1 = new BigDecimal(Double.toString(zero)); //������㾫��
				    int checkPayMode = 0;//�жϸ�pay�Ƿ��й�quickpay��mark,�й�һ�α�ʾ��bill�Ѿ�pay�꣬mark =1 quick =2;
				    int  curSize;
					Cursor cursor1 = db.rawQuery(sql, null);
					curSize = cursor1.getCount();
//					Log.v("mtest", "pangmeny��С"+curSize);
				        while (cursor1.moveToNext()) {
				            
				            int paymentZ_PK = cursor1.getInt(0);	
				            double paymentAmount = cursor1.getDouble(1); 
				   		    int paymentMode = cursor1.getInt(2);	
				   		    int paymenthasBill = cursor1.getInt(3);	
//				   		    Log.v("mtest", "paymentAmount��ʷ"+paymentAmount);
				   		 
				   		  BigDecimal b2 = new BigDecimal(Double.toString(paymentAmount));
				          b1 = b1.add(b2); 
				          if (paymentMode == -1 || paymentMode == 0 || paymentMode == 2) {      //mark
					   	    	checkPayMode = 1;
					   	   }
				          
					      }
				        cursor1.close();
				        
				        double totalPayAmount = b1.doubleValue(); //�ܹ�֧���ܶ�
				        double remain = 0.0;//ʣ�µ�
				        
				        if (nbk_billAmountUnknown == 1) {
				        	
				        	if (curSize >0) {
				        		bMap.put("payState", 1);
				        		bMap.put("remain", mZero);
							}else {
								
								if (billduedate < nowMillis) {
									bMap.put("payState", -1);
								} else {
									bMap.put("payState", 0);
								}
				        		bMap.put("remain", mZero);
							}
							
						} else if ( checkPayMode ==1 ) {
							
							bMap.put("payState", 1);
							bMap.put("remain", mZero);
							
						} else {
							
							 BigDecimal mb1 = new BigDecimal(Double.toString(billamount)); //������㾫��
							 BigDecimal mb2 = new BigDecimal(Double.toString(totalPayAmount)); //������㾫��
							 remain = (mb1.subtract(mb2)).doubleValue();
//							 Log.v("mtest", "billamount"+billamount);
//							 Log.v("mtest", "totalPayAmount����"+totalPayAmount);
//							 Log.v("mtest", "remain����"+remain);
							 
							 if (remain <= 0) {
								 bMap.put("payState", 1);
								 bMap.put("remain", mZero);
							}else {
								
								if (billduedate < nowMillis) {
									bMap.put("payState", -1);
								} else {
									bMap.put("payState", 0);
								}
								bMap.put("remain", remain);
								
							}
						}
				        int mpayState = (Integer)bMap.get("payState");
				        
				        if (mpayState == -2) {
				        	
				        	if (billduedate < nowMillis) {
								bMap.put("payState", -1);
							} else {
								bMap.put("payState", 0);
							}
				        	bMap.put("remain", billamount);
							
						} 
				        
				        
					 if ( (billAutoPay == 1  && billduedate < (nowMillis+86399) && remain > 0 ) || (billAutoPay == 1  && billduedate < (nowMillis+86399) && nbk_billAmountUnknown == 1 && curSize == 0)) { //auto��bill����duedateС�ڵ����ʱ�򣬻���unkonwn �Զ�pay
						 long key;
//						 Log.v("mtest", "remainʣ��"+remain);
						 if (nbk_billAmountUnknown == 1) {
							 key = billPay( 0.00 ,2,billduedate,id);
						}else {
							 key = billPay(remain ,2,billduedate,id);
						}
						 bMap.put("payState", 1);
						 bMap.put("remain", mZero);
					}
				        
				} else if (flag ==2) {
					
					
					if (billduedate < nowMillis) {
						bMap.put("payState", -1);
						bMap.put("remain", billamount);
					} else {
						bMap.put("payState", 0);
						bMap.put("remain", billamount);
					}
					
					if (billAutoPay == 1 && billduedate < (nowMillis+86399)) { //�ظ������¼����ڴ���
						
						bMap.put("payState", 1);
						
						
				            int obillObjecthasBill = (Integer)bMap.get("BK_Bill_Id");
				            double obillamount = (Double)bMap.get("nbillamount");
				            int obk_billAmountUnknown = (Integer)bMap.get("nbk_billAmountUnknown");
				            int obk_billAutoPay = (Integer)bMap.get("nbk_billAutoPay");
				            long obk_billDuedate = (Long) bMap.get("nbk_billDuedate");
				            long obk_billDuedateNew = 0;
				            long obk_billEndDate = (Long) bMap.get("nbk_billEndDate");
				            int obk_billisReminder = (Integer) bMap.get("nbk_billisReminder");
				            int obk_billisRepeat = (Integer) bMap.get("nbk_billisRepeat");
				            int obk_billisVariaable = (Integer) bMap.get("bk_billisVariaable");
				            long obk_billReminderDate = (Long) bMap.get("nbk_billReminderDate");
				            long bk_billReminderTime = (Long) bMap.get("bk_billReminderTime");
				            int obk_billRepeatNumber = (Integer) bMap.get("nbk_billRepeatNumber");
				            int obk_billRepeatType = (Integer) bMap.get("nbk_billRepeatType");
				            int billhasAccount = (Integer)bMap.get("billhasAccount");
				            
					 long Okey = insertObject( 0 , obillamount , obk_billAmountUnknown,obk_billAutoPay ,obk_billDuedate,obk_billDuedateNew,  //���������¼�
								obk_billEndDate, obk_billisReminder, obk_billisRepeat, obk_billisVariaable,obk_billReminderDate,bk_billReminderTime,obk_billRepeatNumber,
								obk_billRepeatType,obillObjecthasBill,billhasAccount);
					 if (obk_billAmountUnknown==1) {
						billPayO( 0.0, 2 ,obk_billDuedate ,Okey);
					} else {
						billPayO(obillamount,2 ,obk_billDuedate , Okey) ;
					}
					 int key = (int) Okey;
					 
					 bMap.put("remain", mZero);
					 bMap.put("indexflag", 3);
					 bMap.put("BK_Bill_Id", key);
					}
					
				} else if (flag == 3) {
					
					String  sql = "select BK_Payment._id, BK_Payment.bk_payAmount, BK_Payment.bk_payMode ,BK_Payment.paymenthasBill from BK_Payment ,BK_BillObject where BK_Payment.paymenthasBillO = "+id;
					
					List<Map<String, Object>> pDataList = new ArrayList<Map<String, Object>>();
					Map<String, Object> map1;
					
					double zero= 0.00f; 
				    BigDecimal b1 = new BigDecimal(Double.toString(zero)); //������㾫��
				    int checkPayMode = 0;//�жϸ�pay�Ƿ��й�quickpay��mark,�й�һ�α�ʾ��bill�Ѿ�pay�꣬mark =1 quick =2;
				    int curSize;
					Cursor cursor1 = db.rawQuery(sql, null);
					curSize = cursor1.getCount();
				        while (cursor1.moveToNext()) {
				            
				            String paymentZ_PK = cursor1.getString(0);	
				            double paymentAmount = cursor1.getDouble(1); 
				   		    int paymentMode = cursor1.getInt(2);	
				   		    int paymenthasBill = cursor1.getInt(3);	
				   		    
				   		  BigDecimal b2 = new BigDecimal(Double.toString(paymentAmount));
				          b1 = b1.add(b2); 
				          if (paymentMode == -1 || paymentMode == 0 || paymentMode == 2) {      //mark
					   	    	checkPayMode = 1;
					   	   }
				          
					      }
				        cursor1.close();
				        
				        double totalPayAmount = b1.doubleValue(); //�ܹ�֧���ܶ�
				        double remain = 0.0;//ʣ�µ�
				        
				        if (nbk_billAmountUnknown == 1) {
				        	
				        	if (curSize >0) {
				        		bMap.put("payState", 1);
				        		bMap.put("remain", mZero);
							}else {
								if (billduedate < nowMillis) {
									bMap.put("payState", -1);
								} else {
									bMap.put("payState", 0);
								}
				        		bMap.put("remain", mZero);
							}
							
						} else if ( checkPayMode ==1 ) {
							
							bMap.put("payState", 1);
							bMap.put("remain", mZero);
							
						} else {
							
							 BigDecimal mb1 = new BigDecimal(Double.toString(billamount)); //������㾫��
							 BigDecimal mb2 = new BigDecimal(Double.toString(totalPayAmount)); //������㾫��
							 remain = (mb1.subtract(mb2)).doubleValue();
							 
							 if (remain <= 0) {
								 bMap.put("payState", 1);
								 bMap.put("remain", mZero);
							}else {
								
								if (billduedate < nowMillis) {
									bMap.put("payState", -1);
								} else {
									bMap.put("payState", 0);
								}
								 bMap.put("remain", remain);
								
							}
						}
				        int mpayState = (Integer)bMap.get("payState");
				        
				        if (mpayState == -2) {
				        	
				        	if (billduedate < nowMillis) {
								bMap.put("payState", -1);
							} else {
								bMap.put("payState", 0);
							}
				        	 bMap.put("remain", billamount);
							
						} 
				        
					 if ( (billAutoPay == 1  && billduedate < (nowMillis+86399) && remain > 0 ) || (billAutoPay == 1  && billduedate < (nowMillis+86399) && nbk_billAmountUnknown == 1 && curSize == 0)) { //auto��bill����duedateС�ڵ����ʱ�򣬻���unkonwn �Զ�pay
						 long key;
						 if (nbk_billAmountUnknown == 1) {
							 key = billPayO( 0.00 ,2,billduedate,id);
						}else {
							 key = billPayO(remain ,2,billduedate,id);
						}
						 bMap.put("payState", 1);
						 bMap.put("remain", mZero);
					}
				}
				
			}//����ѭ������
			 db.close();
		}
	
	
	
	
 private void getAccountData(){ //��ѯaccount��ݿ�
		
		bksql = new BillKeeperSql(this); 
		SQLiteDatabase db = bksql.getReadableDatabase();
		Cursor cursorCC = db.rawQuery("select bk_accountName, _id from BK_Account", null);
		accountlength=cursorCC.getCount();
		int i = 0 ;
		accountNameArray = new  String[accountlength]; 
   	    accountIdArray = new int[accountlength];
   	    
		while (cursorCC.moveToNext()) {
			
			accountNameArray[i] = cursorCC.getString(0);
			accountIdArray[i] =  cursorCC.getInt(1);
		    i++;
		}
		cursorCC.close();
		db.close();
	}
 
 public List<Map<String, Object>> getData(int accountId,long firstDayOfMonth, long lastDayOfMonth)
 {
	 
	  List<Map<String, Object>> finalDataList = new ArrayList<Map<String, Object>>();
	  dataList.clear();
	  bksql = new BillKeeperSql(this);
	  SQLiteDatabase db = bksql.getReadableDatabase();
	  Map<String, Object> map;
	  String sql = "select BK_Bill.* ,BK_Account.bk_accountName ,BK_Account.accounthasCategory  ,BK_Category.bk_categoryIconName ,BK_Category.bk_categoryName, BK_Account.bk_accountNo from BK_Bill,BK_Account,BK_Category where BK_Account._id = "+accountId+" and BK_Bill.billhasAccount = BK_Account._id and BK_Category._id =BK_Account.accounthasCategory and BK_Bill.bk_billisRepeat != 1 and BK_Bill.bk_billDuedate >="+firstDayOfMonth+" and BK_Bill.bk_billDuedate <= "+lastDayOfMonth;
	  
	  Cursor cursorEA = db.rawQuery(sql, null);
	    while (cursorEA.moveToNext()) {
         map = new HashMap<String, Object>();
         
         int BK_Bill_Id =  cursorEA.getInt(0); 
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
         String bk_accountName = cursorEA.getString(25);
         int accounthasCategory = cursorEA.getInt(26);
         int bk_categoryIconName = cursorEA.getInt(27);
         String bk_categoryName  = cursorEA.getString(28);
         String bk_accountNo = cursorEA.getString(29);
        
         map.put("BK_Bill_Id", BK_Bill_Id);  
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
         map.put("bk_accountName", bk_accountName);
         map.put("accounthasCategory", accounthasCategory);
         map.put("bk_categoryIconName", bk_categoryIconName);
         map.put("bk_categoryName", bk_categoryName);
         map.put("bk_accountNo", bk_accountNo);
         map.put("indexflag", 0); //�����¼��ı�־��0�������ı�־��1��2���ظ����¼���3���ظ������¼�
         map.put("payState", -2); 
         
         dataList.add(map);
	        
     }
	 cursorEA.close();
	 db.close();
	 dataList.addAll(RecurringEventByAccountIdTime.recurringData(this, accountId ,firstDayOfMonth, lastDayOfMonth) ); //���ѭ��ʱ���¼�
     Collections.sort(dataList, new Common.MapComparator());
     judgePayment(dataList);
     
return dataList;

}
 
 public void getBillInfo(int id, long start, long end) {
	 
	    dataList.clear();
	    Map<String, Object> bMap;
	    bksql = new BillKeeperSql(this); 
	  	SQLiteDatabase db = bksql.getReadableDatabase();
	  	String  sql = " select BK_Bill.* from BK_Bill where billhasAccount = "+id+" and bk_billDuedate >= "+start+" and bk_billDuedate <= "+end;
	  	
	    Cursor cursorEA = db.rawQuery(sql, null);
	    billLength = cursorEA.getCount();
	    while (cursorEA.moveToNext()) {
	    	
	     bMap = new HashMap<String, Object>();
	     int BK_Bill_Id =  cursorEA.getInt(0); 
	     double billamount = cursorEA.getDouble(2);
	     int bk_billAmountUnknown = cursorEA.getInt(3);
         int bk_billAutoPay = cursorEA.getInt(4);
         long bk_billDuedate = cursorEA.getLong(8);
         long bk_billEndDate = cursorEA.getLong(9);
         int bk_billisReminder = cursorEA.getInt(10);
         int bk_billisRepeat = cursorEA.getInt(11);
         int bk_billisVariaable = cursorEA.getInt(12);
         long bk_billReminderDate = cursorEA.getLong(14);
         int bk_billRepeatNumber = cursorEA.getInt(16);
         int bk_billRepeatType = cursorEA.getInt(17);
         
         bMap.put("BK_Bill_Id", BK_Bill_Id);  
         bMap.put("nbillamount", billamount);
         bMap.put("nbk_billAmountUnknown", bk_billAmountUnknown);
         bMap.put("nbk_billAutoPay", bk_billAutoPay);
         bMap.put("nbk_billDuedate", bk_billDuedate);
         bMap.put("nbk_billEndDate", bk_billEndDate);
         bMap.put("nbk_billisReminder", bk_billisReminder);
         bMap.put("nbk_billisRepeat", bk_billisRepeat);
         bMap.put("bk_billisVariaable", bk_billisVariaable);
         bMap.put("nbk_billReminderDate", bk_billReminderDate);
         bMap.put("nbk_billRepeatNumber", bk_billRepeatNumber);
         bMap.put("nbk_billRepeatType", bk_billRepeatType);
         
         dataList.add(bMap);
	    }
	   cursorEA.close();  
	   db.close();
}
 
 
 
 Handler mHanlder = new Handler() {

		public void handleMessage(Message message) {
			
			switch (message.what) {
			case 1:
				dialog_loading.dismiss();
				
				String mstartString = start_Button.getText().toString();
				String mendString = end_Button.getText().toString();
				
				final Intent emailIntent = new Intent(
						android.content.Intent.ACTION_SEND);
				emailIntent.setType("text/html;charset=utf-8");
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Bills Data:"+ mstartString+"-"+ mendString);
				
				emailIntent.putExtra(Intent.EXTRA_TEXT, "The Bills data was generated by Bill Keeper on Android Device");
				// String a=TextUtils.htmlEncode(all);

//				emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,Html.fromHtml(mHtmlAll));
				emailIntent.putExtra(
						Intent.EXTRA_STREAM,
						Uri.parse("file:///sdcard/BillKeeperData/"+ "BillsData" + ".html"));
				// Html.fromHtml(all, null, null);
				System.out.println(mHtmlAll);
				startActivity(Intent.createChooser(emailIntent, "Export"));

				break;
			case 2:
				dialog_loading.dismiss();
				
				String mstartString1 = start_Button.getText().toString();
				String mendString1 = end_Button.getText().toString();
				
				try {
					
			      Document document = new Document(PageSize.A4);
			      PdfWriter.getInstance(document, new FileOutputStream("/sdcard/BillKeeperData/"+"bills data"+ ".pdf"));
			      document.open();
			      document.addAuthor("Real Gagnon");
			      document.addCreator("Real's HowTo");
			      document.addSubject("Thanks for your support");
			      document.addCreationDate();
			      document.addTitle("Please read this");
			      
			      HTMLWorker htmlWorker = new HTMLWorker(document);
			      
			      htmlWorker.parse(new StringReader(mHtmlAll));
			      document.close();
			      System.out.println("Done");
			      }
			    catch (Exception e) {
			      e.printStackTrace();
			    }
				
				final Intent pdfemailIntent = new Intent(
						android.content.Intent.ACTION_SEND);
				pdfemailIntent.setType("text/html;charset=utf-8");
				pdfemailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						"subject");
				pdfemailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"bills data:"+ mstartString1+"-"+ mendString1);
				// String a=TextUtils.htmlEncode(all);
				pdfemailIntent.putExtra(Intent.EXTRA_TEXT, "These Bills was generated by Bill Keeper.");
//				emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,Html.fromHtml(mHtmlAll));
				pdfemailIntent.putExtra(
						Intent.EXTRA_STREAM,
						Uri.parse("file:///sdcard/BillKeeperData/"+ "bills data" + ".pdf"));
				// Html.fromHtml(all, null, null);
				System.out.println(mHtmlAll);
				startActivity(Intent
						.createChooser(pdfemailIntent, "Export"));
				

				break;
			default:
				break;
			}
		}
	};
	
	public String getMilltoDate(long milliSeconds) {//������ת���ɹ̶���ʽ��������
		SimpleDateFormat formatter = new SimpleDateFormat("MMM-dd-yyyy");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}
	
	public String getMilltoTime(long milliSeconds) {//������ת���ɹ̶���ʽ��������
		SimpleDateFormat formatter = new SimpleDateFormat("MMM-dd-yyyy HH,mm,ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}
	
	public String turnToRepeatType(int number) {
		String type = "";
		if (number == 1) {
			type ="day(s)";
		} else if (number == 2) {
			type = "week(s)";
		} else if (number == 3) {
			type = "month(s)";
		} else if (number == 4) {
			type= "year(s)";
		} 
		return type;
	}
	
	/*
	 * ****���html�ļ�****
	 */
	
   class Createhtml {

		// �����ļ��м��ļ�
		public void CreateText(int types) throws IOException {
			File file = new File("/sdcard/BillKeeperData/");
			if (!file.exists()) {
				try {
					// ����ָ����·�������ļ���
					file.mkdirs();
					System.out.println("OK");
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println(e + "Exception");
				}
			}
			File path;
			path = new File("/sdcard/BillKeeperData/");
			File dir = null;
			
			Calendar c = Calendar.getInstance(); 
		    nowCreat = getMilltoTime(c.getTimeInMillis()); 
			dir = new File(path, "BillsData" + ".html");

			if (!dir.exists()) {
				try {
					// ��ָ�����ļ����д����ļ�
					dir.createNewFile();
					System.out.println("OKdsdsa");
					System.out.println("createe");
				} catch (Exception e) {
					System.out.println(e + "ddd");
				}
			} else {
				dir.delete();
				System.out.println("delete");
			}

		}

		// ���Ѵ������ļ���д�����
		public void print(int types) {

			FileWriter fw = null;
			BufferedWriter bw = null;

			String datetime = "";

			try {

				fw = new FileWriter("/sdcard/BillKeeperData/"
						+ "BillsData" + ".html", true);//
				// fw=new FileOutputStream("/sdcard/CheckBookData/" +
				// checkbookdataString + ".csv",
				// ,"GBK")
				// ����FileWriter��������д���ַ���
				// bw = new BufferedWriter(fw);
				bw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream("/sdcard/BillKeeperData/"
								+ "BillsData" + ".html"), "UTF-8"));// ����
				// bw.write("\uFEFF");

				// all="<html><table  border='1'><tr><th>aads</th><th>dd</th></tr></table></html>";
				
				bw.write(mHtmlAll);
				System.out.println(mHtmlAll);
				Log.v("mtake", "������htmle�ı���"+mHtmlAll);
				// bw.append("sd"+","+"\'2013_dd\'"+","+"ss");
				bw.newLine();

				bw.flush(); // ˢ�¸����Ļ���
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
	}
	
	
	/*
	 * *****���csv�ļ�*****
	 */
	
  class CreateFiles {  

		// String filenameTemp = "/sdcard/" + checkbookdataString + ".txt";

		// �����ļ��м��ļ�
		public void CreateText(int types) throws IOException {
			File file = new File("/sdcard/BillKeeperData/");
			if (!file.exists()) {
				try {
					// ����ָ����·�������ļ���
					file.mkdirs();
					Log.v("mtest", "�ļ������ɹ�");
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println(e + "asdasd");
				}
			}
			File path;
			path = new File("/sdcard/BillKeeperData/");
			File dir = null;
			
			Calendar c = Calendar.getInstance(); 
		    nowCreat = getMilltoTime(c.getTimeInMillis()); 
			dir = new File(path, "BillsData"+".csv");

			if (!dir.exists()) {
				try {
					// ��ָ�����ļ����д����ļ�
					dir.createNewFile();
					System.out.println("OKdsdsa");
					System.out.println("createe");
				} catch (Exception e) {
					System.out.println(e + "ddd");
				}
			} else {
				dir.delete();
				System.out.println("delete");
			}
		}
		
		// ���Ѵ������ļ���д�����
		public void print(int types) {

			FileWriter fw = null;
			BufferedWriter bw = null;

			try {
				
				fw = new FileWriter("/sdcard/BillKeeperData/"
						+ "BillsData" + ".csv", true);//
				// fw=new FileOutputStream("/sdcard/CheckBookData/" +
				// checkbookdataString + ".csv",
				// ,"GBK")
				// ����FileWriter��������д���ַ���
				// bw = new BufferedWriter(fw);
				bw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream("/sdcard/BillKeeperData/"
								+ "BillsData" + ".csv"), "GBK"));// ����
				
				bw.write("Account Name" + "," +"Account Number" + "," + "Due Date" + "," + "Paid/Unpaid" + ","+ "Due Amount" + "," + "Amount Paid" + "," + "Amount Unpaid");
				bw.newLine();
				for (Map<String, Object> iMap:dataList) {
					
					 int BK_Bill_Id = (Integer)iMap.get("BK_Bill_Id");
			         double billamount = (Double)iMap.get("nbillamount");
			         int bk_billAmountUnknown = (Integer)iMap.get("nbk_billAmountUnknown");
			         long bk_billDuedate = (Long)iMap.get("nbk_billDuedate"); //�൱���¼��Ŀ�ʼ����
			         String bk_accountName = (String)iMap.get("bk_accountName");
			         int payState = (Integer)iMap.get("payState");
			         
			         String bk_accountNo = (String)iMap.get("bk_accountNo");
			         Double remain =  (Double)iMap.get("remain");
			         
			         int ID = BK_Bill_Id;
			         String Duedate = "";
			         
			      Duedate = getMilltoDate(bk_billDuedate);
			      String payString = "";
			      if (payState == 1) {
			    	  payString ="paid";
				} else {
					  payString = "unPaid";
				}
			      String dueAmountString ="";
			      if (bk_billAmountUnknown ==1) {
			    	  dueAmountString = "N/A";
				} else {
					  dueAmountString = currencyString+billamount;
				}
			      
			      BigDecimal b1 = new BigDecimal(Double.toString(billamount));
			      BigDecimal b2 = new BigDecimal(Double.toString(remain));
			      
			      double paidDouble = (b1.subtract(b2)).doubleValue(); 
			      
			         bw.write(bk_accountName+"");
			         bw.write(",");
			         bw.write(bk_accountNo+"");
			         bw.write(",");
			         bw.write(Duedate);
			         bw.write(",");
			         bw.write(payString);
			         bw.write(",");
			         bw.write(dueAmountString);
			         bw.write(",");
			         bw.write(paidDouble+"");
			         bw.write(",");
			         bw.write(remain+"");
			         bw.newLine();
				}
				bw.flush(); // ˢ�¸����Ļ���
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
	}
		
		
//  public static void parseHTML2PDFFile(String pdfFile,
//		              InputStream htmlFileStream) throws Exception {
//		   
//		          BaseFont bfCN = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H",
//		                  false);
//		          // �������嶨��
//		          Font chFont = new Font(bfCN, 12, Font.NORMAL, BaseColor.BLUE);
//		          Font secFont = new Font(bfCN, 12, Font.NORMAL, new BaseColor(0, 204,
//		                  255));
//		   
//		          Document document = new Document();
//		          PdfWriter pdfwriter = PdfWriter.getInstance(document,
//		                  new FileOutputStream(pdfFile));
//		          pdfwriter.setViewerPreferences(PdfWriter.HideToolbar);
//		          document.open();
//		   
//		          int chNum = 1;
//		          Chapter chapter = new Chapter(new Paragraph("HTML", chFont),chNum++);
//		          Section section = chapter.addSection(new Paragraph("/dev/null 2>&1 ���",
//		                  secFont));
//		          // section.setNumberDepth(2);
//		          // section.setBookmarkTitle("����Ϣ");
//		          section.setIndentation(10);
//		          section.setIndentationLeft(10);
//		          section.setBookmarkOpen(false);
//		          section.setNumberStyle(Section.NUMBERSTYLE_DOTTED_WITHOUT_FINAL_DOT);
//		          section.add(Chunk.NEWLINE);
//		          document.add(chapter);
//		   
//		          // html�ļ�
//		          InputStreamReader isr = new InputStreamReader(htmlFileStream, "UTF-8");
//		   
//		          XMLWorkerHelper.getInstance().parseXHtml(pdfwriter, document, isr);
//		   
//		          document.close();
//		 
//		      }
  
//  public void htmlCodeComeFromFile(String filePath, String pdfPath) {   
//      Document document = new Document();   
//      try {   
//          StyleSheet st = new StyleSheet(); 
//          st.loadTagStyle("body", "leading", "16,0"); 
//          PdfWriter.getInstance(document, new FileOutputStream(pdfPath));   
//          document.open();
//          BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UTF-8", BaseFont.NOT_EMBEDDED);   
//          Font FontChinese = new Font(bfChinese, 12, Font.NORMAL);   
//          Paragraph t = new Paragraph(filePath, FontChinese); 
//          ArrayList p = (ArrayList) HTMLWorker.parseToList(new FileReader(filePath), st); 
//          for(int k = 0; k < p.size(); ++k) {   
//              document.add((Element)p.get(k));   
//          }   
//          document.close();   
//          System.out.println("�ĵ������ɹ�");   
//      }catch(Exception e) {   
//          e.printStackTrace();   
//      }   
//  } 
  
//  public void htmlCodeComeFromFile(String filePath, String pdfPath) {   
//      Document document = new Document();   
//      try {   
//          StyleSheet st = new StyleSheet(); 
//          st.loadTagStyle("body", "leading", "16,0"); 
//          PdfWriter.getInstance(document, new FileOutputStream(pdfPath));   
//          document.open();
//          BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);   
//          Font FontChinese = new Font(bfChinese, 12, Font.NORMAL);   
//          Paragraph t = new Paragraph(filePath, FontChinese); 
//          ArrayList p = HTMLWorker.parseToList(new FileReader(filePath), st); 
//          for(int k = 0; k < p.size(); ++k) {   
//              document.add((Element)p.get(k));   
//          }   
//          document.close();   
//          System.out.println("�ĵ������ɹ�");   
//      }catch(Exception e) {   
//          e.printStackTrace();   
//      }   
//  } 
  
//  public void htmlCodeComeString(String htmlCode, String pdfPath) {   
//      Document doc = new Document(PageSize.A4);   
//      try {   
//          PdfWriter.getInstance(doc, new FileOutputStream(pdfPath));   
//          doc.open();   
//          // �����������   
//          BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);   
//          Font FontChinese = new Font(bfChinese, 12, Font.NORMAL);   
//          Paragraph t = new Paragraph(htmlCode, FontChinese);   
//          doc.add(t);   
//          doc.close();   
//          System.out.println("�ĵ������ɹ�");   
//      }catch(Exception e) {   
//          e.printStackTrace();   
//      } 
//  } 
// 

	
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:  //���Ͻǰ�ť���ص�id
			finish();
			return true;
		case R.id.export:
			
//			getBillInfo(accountId,startDate,endDate);
			getData(accountId, startDate, endDate);
			billLength = dataList.size();
			Log.v("mtake", "������ݴ�С��"+billLength);
			if (accountlength == 0) {
				
				new AlertDialog.Builder(ExportActivity.this)
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
				
			}else if (billLength ==0) {
				
				new AlertDialog.Builder(ExportActivity.this)
				.setTitle("Warning! ")
				.setMessage(
						"There is no bill fit to your conditions! ")
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
				
				if (file_type ==2) {
					
					

					
				} else if (file_type == 1) {
					
					dialog_loading = ProgressDialog.show(ExportActivity.this, "waiting...",
							"Hold on,Data is being exported ...", true);
					Log.v("mtest", "�߳̿�ʼ");
					Log.v("mtest", "file_type"+file_type);
					mHtmlBody = "";
					new Thread(new Runnable() {

						@Override
						public void run() {

								Log.v("mtest", "html��ʼ");
								int i = 0 ;
								for (Map<String, Object> iMap:dataList) {
									
									 int BK_Bill_Id = (Integer)iMap.get("BK_Bill_Id");
							         double billamount = (Double)iMap.get("nbillamount");
							         int bk_billAmountUnknown = (Integer)iMap.get("nbk_billAmountUnknown");
							         long bk_billDuedate = (Long)iMap.get("nbk_billDuedate"); //�൱���¼��Ŀ�ʼ����
							         String bk_accountName = (String)iMap.get("bk_accountName");
							         int payState = (Integer)iMap.get("payState");
							         
							         String bk_accountNo = (String)iMap.get("bk_accountNo");
							         Double remain =  (Double)iMap.get("remain");
							         
							         int ID = BK_Bill_Id;
							         String Duedate = "";
							         
							         
							      Duedate = getMilltoDate(bk_billDuedate);
							      String payString = "";
							      if (payState == 1) {
							    	  payString ="paid";
								} else {
									  payString = "unPaid";
								}
							      String dueAmountString ="";
							      if (bk_billAmountUnknown ==1) {
							    	  dueAmountString = "N/A";
								} else {
									  dueAmountString = currencyString+billamount;
								}
							      
							      BigDecimal b1 = new BigDecimal(Double.toString(billamount));
							      BigDecimal b2 = new BigDecimal(Double.toString(remain));
							      
							      double paidDouble = (b1.subtract(b2)).doubleValue(); 
							      
							         
							      String mStyleString = "";
							    	  if (i % 2 ==0 ) {
							    		  mStyleString = mHtmlItemWhite;
									} else {
										mStyleString = mHtmlItemGary;
									}
							    	i = i+1;
							    	Log.v("mtake", "ѭ��ֵ��"+i);
							mHtmlBody = mHtmlBody+mStyleString 
							+"<td width=\"\" style=\"border:solid 1px #b0b0b0;padding: 5px;\"><font size=\"2\">"+bk_accountName+"</font></td>"
							+"<td width=\"\" style=\"border:solid 1px #b0b0b0;padding: 5px;\"><font size=\"2\">"+bk_accountNo+" </font></td>"
							+"<td width=\"\" style=\"border:solid 1px #b0b0b0;padding: 5px;\"><font size=\"2\">"+Duedate+"</font></td>"
							+"<td width=\"\" style=\"border:solid 1px #b0b0b0;padding: 5px;\"><font size=\"2\">"+payString+"</font></td>"
							+"<td width=\"\" style=\"border:solid 1px #b0b0b0;padding: 5px;\"><font size=\"2\">"+dueAmountString+"</font></td>"
							+"<td width=\"\" style=\"border:solid 1px #b0b0b0;padding: 5px;\"><font size=\"2\">"+paidDouble+"</font></td>"
							+"<td width=\"\" style=\"border:solid 1px #b0b0b0;padding: 5px;\"><font size=\"2\">"+remain+"</font></td>"
						 +"</tr>";
							         
							}
								
								String mstartString = start_Button.getText().toString();
								String mendString = end_Button.getText().toString();
								Calendar c = Calendar.getInstance(); 
								String nowString  = getMilltoTime(c.getTimeInMillis()); 
								
								mHtmlAll = mHtmlHead+mHtmlBodyFirst+mHtmlBody+mHtmlEnd;
								Log.v("mdb", "mHtmlAll:"+mHtmlAll);
								try {
									createhtml.CreateText(1);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								createhtml.print(1);
							  
								Message message = new Message();
								message.what = 1;
								mHanlder.sendMessage(message);
						}
					}).start();
					
				} else if ( file_type == 3) {
					
					dialog_loading = ProgressDialog.show(ExportActivity.this, "waiting...",
							"Hold on,Data is being exported ...", true);
					Log.v("mtest", "file_type"+file_type);
					mHtmlBody = "";
					new Thread(new Runnable() {

						@Override
						public void run() {

								Log.v("mtest", "html��ʼ");
								int i = 0 ;
								for (Map<String, Object> iMap:dataList) {
									
									 int BK_Bill_Id = (Integer)iMap.get("BK_Bill_Id");
							         double billamount = (Double)iMap.get("nbillamount");
							         int bk_billAmountUnknown = (Integer)iMap.get("nbk_billAmountUnknown");
							         long bk_billDuedate = (Long)iMap.get("nbk_billDuedate"); //�൱���¼��Ŀ�ʼ����
							         String bk_accountName = (String)iMap.get("bk_accountName");
							         int payState = (Integer)iMap.get("payState");
							         
							         String bk_accountNo = (String)iMap.get("bk_accountNo");
							         Double remain =  (Double)iMap.get("remain");
							         
							         int ID = BK_Bill_Id;
							         String Duedate = "";
							         
							         
							      Duedate = getMilltoDate(bk_billDuedate);
							      String payString = "";
							      if (payState == 1) {
							    	  payString ="paid";
								} else {
									  payString = "unPaid";
								}
							      String dueAmountString ="";
							      if (bk_billAmountUnknown ==1) {
							    	  dueAmountString = "N/A";
								} else {
									  dueAmountString = currencyString+billamount;
								}
							      
							      BigDecimal b1 = new BigDecimal(Double.toString(billamount));
							      BigDecimal b2 = new BigDecimal(Double.toString(remain));
							      
							      double paidDouble = (b1.subtract(b2)).doubleValue(); 
							
						    	mHtmlBody=mHtmlBody +"<tr style=\"text-align:center;\"><td>"+Duedate+"</td><td>"+bk_accountName+"</td><td>"+payString+"</td><td>"+dueAmountString+"</td><td>"+paidDouble+"</td><td>"+bk_accountNo+"</td></tr>";
									
								}
								
								String mstartString = start_Button.getText().toString();
								String mendString = end_Button.getText().toString();
								Calendar c = Calendar.getInstance(); 
								String nowString  = getMilltoDate(c.getTimeInMillis()); 
								
								mHtmlAll =  "<!DOCTYPE html>"
										 +"<html>"
										   +"<head>"
										     +"<title></title>"
										     + "<meta http-equiv=content-type content=text/html; charset=UTF-8>"
										   +"</head>"
										   +"<body>"
										 	+"<h1 style=\"text-align:center;\">Bill Report</h1>"
										     +"<table border=\"1\">"
										 	 +"<tr><td>Report Date:</td><td>"+nowString+"</td><td colspan=\"4\" style=\"text-align:right;\">Generated by Bill Keeper</td></tr>"
										     +"<tr style=\"text-align:center;background-color: ;\"><td colspan=\"6\">Bill Data:"+mstartString+"- "+mendString+"</td></tr>"
										     +"<tr style=\"background-color: #7ac5cd;text-align:center;\"><td>Due Date</td><td>Account</td>"
										     +"<td>Paid/Unpaid</td><td>Due Amount</td><td>Amount Paid</td><td>Account No.</td></tr>"
								             +mHtmlBody
								             +"</table>"
											 +"</body>"
											 +"</html>";
								
								Log.v("mdb", "mHtmlAllPDF:"+mHtmlAll);
								nowCreat = getMilltoTime(c.getTimeInMillis()); 
								
								Message message = new Message();
								message.what = 2;
								mHanlder.sendMessage(message);
						}
					}).start();
					
					
				}
			}
			
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.export, menu);
		return true;
	}

}
