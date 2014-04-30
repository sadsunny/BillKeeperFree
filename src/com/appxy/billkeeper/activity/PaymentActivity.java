package com.appxy.billkeeper.activity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.actionbarsherlock.view.MenuItem;
import com.appxy.billkeeper.BaseHomeActivity;
import com.appxy.billkeeper.R;
import com.appxy.billkeeper.R.layout;
import com.appxy.billkeeper.R.menu;
import com.appxy.billkeeper.adapter.AccountFragmentAdapter;
import com.appxy.billkeeper.adapter.DialogAccountItemAdapter;
import com.appxy.billkeeper.adapter.DialogDeletePayAdapter;
import com.appxy.billkeeper.adapter.PaymentActivityAdapter;
import com.appxy.billkeeper.db.BillKeeperSql;
import com.appxy.billkeeper.entity.Common;
import com.appxy.billkeeper.service.BillNotificationService;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class PaymentActivity extends BaseHomeActivity {

	private int bill_id;
	private LinearLayout mLinearLayout;
	private ImageView mImageView;
	private TextView accountTextView;
	private TextView dateTextView;
	private TextView currencyTextView1;
	private TextView currencyTextView2;
	private TextView currencyTextView3;
	private TextView totalTextView;
	private TextView paidTextView;
	private TextView remainTextView;
	private TextView repeaTextView;
	private ImageButton markButton;
	private ImageButton quickButton;
	private ImageButton addPayButton;
	private ImageView paidImageView;
	private ListView mListView;

	private Button duedateButton ;
	private EditText amounTextView ;
	private EditText confTextView;
	private EditText memoTextView;
	private int mYear;
	private int mMonth;
	private int mDay;
	
	private int pYear;
	private int pMonth;
	private int pDay;

	private BillKeeperSql bksql;
	private List<Map<String, Object>> mData;
	private List<Map<String, Object>> dataList;
	private double totalPay = 0;
	private int checkPayMode = 0;//??��??�?pay?????????�?quickpay???mark,mark =1 quick =2;
	private PaymentActivityAdapter pAadaAdapter;
	private LayoutInflater flater;
	public Handler handler;
	private double remain = 0.00;
	private int curSize ;
	private long payDateLong = 0 ;
	private  int flag;
	private  Map<String, Object> mMap ;//�???��??map???
	
    private ListView diaListView;
    private DialogDeletePayAdapter dialogAccountItemAdapter ;
    private AlertDialog alertDialog;
    private long payChangeDateLong ; 
    
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment);

		dataList = new ArrayList<Map<String, Object>>();
		mMap = new HashMap<String, Object>();
		flater = LayoutInflater.from(PaymentActivity.this);
		
//		getActionBar().setDisplayShowHomeEnabled(false);
//		getActionBar().setDisplayShowTitleEnabled(true);
//		
//		 int titleId = Resources.getSystem().getIdentifier("action_+bar_title", "id", "android");
//		 TextView title = (TextView) findViewById(titleId);
//		 title.setTextColor(this.getResources().getColor(R.color.white));

		mLinearLayout = (LinearLayout)findViewById(R.id.LinearLayout1);
		mImageView = (ImageView)findViewById(R.id.account_category_imageView);
		accountTextView = (TextView)findViewById(R.id.account_textView);
		dateTextView = (TextView)findViewById(R.id.date_textView);
		currencyTextView1 = (TextView)findViewById(R.id.currency_textView1);
		currencyTextView2 = (TextView)findViewById(R.id.currency_textView2);
		currencyTextView3 = (TextView)findViewById(R.id.currency_textView3);
		totalTextView = (TextView)findViewById(R.id.Total_textView);
		paidTextView = (TextView)findViewById(R.id.Paid_textView);
		remainTextView = (TextView)findViewById(R.id.remain_textView);
		repeaTextView = (TextView)findViewById(R.id.rep_textView);
		markButton = (ImageButton)findViewById(R.id.mark_button);
		quickButton = (ImageButton)findViewById(R.id.quick_button);
		addPayButton = (ImageButton)findViewById(R.id.add_pay_button);
		paidImageView = (ImageView)findViewById(R.id.paidImageView);
		mListView = (ListView)findViewById(R.id.mListview);
		pAadaAdapter = new PaymentActivityAdapter(this);
		mListView.setAdapter(pAadaAdapter);
		
		currencyTextView1.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
		currencyTextView2.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);
		currencyTextView3.setText(Common.CURRENCY_SIGN[Common.CURRENCY]);

		markButton.setClickable(false);
		quickButton.setClickable(true);
		addPayButton.setClickable(true);
		Date date=new Date();
		final long todayMill = date.getTime();

		Intent intent = getIntent();
		mMap = new HashMap<String, Object>();
		
		mMap = (HashMap<String, Object>) intent.getSerializableExtra("dataMap");
	
		if(mMap!=null && mMap.size() > 0){
			
			Log.v("mmes", "传入到payment的不是空");
			Log.v("mmes", "在payment得到的map"+mMap);
		
		flag  = (Integer) mMap.get("indexflag");
		bill_id = (Integer)mMap.get("BK_Bill_Id");

//		handler = new Handler();
//		handler.post(listUpdater);
		mHandler.post(listUpdater);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> paramAdapterView,
					View paramView, int paramInt, long paramLong) {
				// TODO Auto-generated method stub
					
					final int key =  (Integer) dataList.get(paramInt).get("pay_id");
					Map<String, Object> pMap = getPayDate(key);
					
					View view = flater.inflate(R.layout.dialog_payment, null);

					amounTextView = (EditText)view.findViewById(R.id.payAmount);
					duedateButton = (Button)view.findViewById(R.id.dueDate);
					confTextView = (EditText)view.findViewById(R.id.ConfNumber);
					memoTextView = (EditText)view.findViewById(R.id.memo);

					java.text.DecimalFormat  df = new java.text.DecimalFormat("0.00");
					Date date1=new Date(); 
					double bk_payAmount = 0 ;
					long bk_payDate = date1.getTime() ;
					String bk_payConfigNumber ="";
					String bk_payMemo = "";

					if (pMap != null) {
						 bk_payAmount = (Double) pMap.get("bk_payAmount");
						 bk_payDate = (Long) pMap.get("bk_payDate");
						 bk_payConfigNumber = (String) pMap.get("bk_payConfigNumber");
						 bk_payMemo = (String) pMap.get("bk_payMemo");
					}
					
					confTextView.setText(bk_payConfigNumber+"");
					memoTextView.setText(bk_payMemo);
					amounTextView.setText(df.format(bk_payAmount));
					amounTextView.addTextChangedListener(new TextWatcher() { // 设置?????��?????两�??�????
						private boolean isChanged = false;

						@Override
						public void onTextChanged(CharSequence s, int start,
								int before, int count) {
							// TODO Auto-generated method stub

						}

						@Override
						public void beforeTextChanged(CharSequence s, int start,
								int count, int after) {
							// TODO Auto-generated method stub
						}

						@Override
						public void afterTextChanged(Editable s) {
							// TODO Auto-generated method stub

							if (isChanged) {// ----->�????�?�??????��?????�????    
								return;    
							}    
							String str = s.toString();    

							isChanged = true;    
							String cuttedStr = str;    
							/* ?????��??�?串中???dot */    
							for (int i = str.length() - 1; i >= 0; i--) {    
								char c = str.charAt(i);    
								if ('.' == c) {    
									cuttedStr = str.substring(0, i) + str.substring(i + 1);    
									break;    
								}    
							}    
							/* ?????��????��??�????0 */    
							int NUM = cuttedStr.length();   
							int zeroIndex = -1;  
							for (int i = 0; i < NUM - 2; i++) {    
								char c = cuttedStr.charAt(i);    
								if (c != '0') {    
									zeroIndex = i;  
									break;  
								}else if(i == NUM - 3){  
									zeroIndex = i;  
									break;  
								}  
							}    
							if(zeroIndex != -1){  
								cuttedStr = cuttedStr.substring(zeroIndex);  
							}  
							/* �?�?3�?�?0 */    
							if (cuttedStr.length() < 3) {    
								cuttedStr = "0" + cuttedStr;    
							}    
							/* ???�?dot�?以�?�示�???��?��??两�?? */    
							cuttedStr = cuttedStr.substring(0, cuttedStr.length() - 2)    
							+ "." + cuttedStr.substring(cuttedStr.length() - 2);    


							amounTextView.setText(cuttedStr);
							//								dueAmountFloat= dueAmonut.getText().toString();
							amounTextView.setSelection(cuttedStr.length());
							isChanged = false;
						}
					});
					
					Calendar c = Calendar.getInstance();
					c.setTimeInMillis(bk_payDate);
					pYear = c.get(Calendar.YEAR);
					pMonth = c.get(Calendar.MONTH);
					pDay = c.get(Calendar.DAY_OF_MONTH);
					updatePayDisplay();
					
					duedateButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							DatePickerDialog DPD = new DatePickerDialog( // ??��??theme
									new ContextThemeWrapper(PaymentActivity.this,
											android.R.style.Theme_Holo_Light),
											mDatePaySetListener, pYear, pMonth, pDay);
							DPD.setTitle("Due Date");
							DPD.show();
						}
					});

					AlertDialog.Builder addPaymentDialog = new AlertDialog.Builder(PaymentActivity.this);
					addPaymentDialog.setView(view);
					addPaymentDialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							double pay = Double.parseDouble(amounTextView.getText().toString()); //注�??.221452?????��?��????????�?
							pAadaAdapter.notifyDataSetChanged();
							if (pay == 0) {
								new AlertDialog.Builder(PaymentActivity.this)
								.setTitle("Warning! ")
								.setMessage(
								"Please make sure the Pay Amount is not zero! ")
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
								String  ConfNumber = confTextView.getText().toString();
								String  memo = memoTextView.getText().toString();
								updateBillpay(key, pay, payChangeDateLong, ConfNumber, memo);
//								handler.post(listUpdater);
								mHandler.post(listUpdater);
								Intent intent = new Intent();
    							intent.putExtra("id", key);
    							setResult(10, intent);
								//								Log.v("mdb", msg);
							}

						}
					});
					addPaymentDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

						}
					});
					addPaymentDialog.show();
					
			}
			
		});
		
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> paramAdapterView,
					View paramView, int paramInt, long paramLong) {
				// TODO Auto-generated method stub
				
                if (dataList != null && dataList.size() != 0) {
                	
                	 View  dialogview = flater.inflate(R.layout.dialog_account_item_operation,null); 
    				 diaListView = (ListView)dialogview.findViewById(R.id.dia_listview);
    				 dialogAccountItemAdapter = new DialogDeletePayAdapter(PaymentActivity.this);
    				  
    				 final int key =  (Integer) dataList.get(paramInt).get("pay_id");
    				 diaListView.setOnItemClickListener(new OnItemClickListener() {

    					@Override
    					public void onItemClick(AdapterView<?> arg0, View arg1,
    							int arg2, long arg3) {
    						// TODO Auto-generated method stub
    						if (arg2 == 0) {
    							
    							deletePay(key);
    							alertDialog.dismiss();
    							
//    							handler.post(listUpdater);
    							mHandler.post(listUpdater);
    							Intent intent = new Intent();
    							intent.putExtra("id", key);
    							setResult(10, intent);

    						} 
    					}
    				 });
    					 diaListView.setAdapter(dialogAccountItemAdapter);
    					 AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
    					 builder.setView(dialogview);
    					 alertDialog = builder.create();
    					 alertDialog.show();
				}
				return true;
			}
		});

		markButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				new AlertDialog.Builder(PaymentActivity.this)
				.setTitle("Warning! ")
				.setMessage(
				"Are you sure to make this bill as paid? ")
				.setPositiveButton("Mark",
						new DialogInterface.OnClickListener() {

					@Override	
					public void onClick(DialogInterface dialog,
							int which) {
						// TODO Auto-generated method stub
						
						dialog.dismiss();
//						 Intent service=new Intent(PaymentActivity.this, BillNotificationService.class);   //�ж�����
//						 PaymentActivity.this.startService(service);  
						 
						if (flag == 0 || flag ==1) {
							
							long key = billPay(0 ,-1,todayMill,bill_id,new String(),"Mark as pay");
//							handler.post(listUpdater);
							mHandler.post(listUpdater);
							Intent intent = new Intent();
							intent.putExtra("id", key);
							setResult(10, intent);

						} else if (flag == 2) {

						} else if (flag == 3) {

							long key = billPayO(0 ,-1,todayMill,bill_id,new String(),"Mark as pay");
//							handler.post(listUpdater);
							mHandler.post(listUpdater);
							Intent intent = new Intent();
							intent.putExtra("id", key);
							setResult(10, intent);
						}
						
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}

				}).show();
				
				
			}
		});
		
		quickButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (flag == 0 || flag ==1) {

					long key = billPay(remain ,0,todayMill,bill_id,new String(),"Quick pay");
//					handler.post(listUpdater);
					mHandler.post(listUpdater);
					Intent intent = new Intent();
					intent.putExtra("id", key);
					setResult(10, intent);

				} else if (flag == 2) {

					int obillObjecthasBill = (Integer)mMap.get("BK_Bill_Id");
					double obillamount = (Double)mMap.get("nbillamount");
					int obk_billAmountUnknown = (Integer)mMap.get("nbk_billAmountUnknown");
					int obk_billAutoPay = (Integer)mMap.get("nbk_billAutoPay");
					long obk_billDuedate = (Long) mMap.get("nbk_billDuedate");
					long obk_billDuedateNew = 0;
					long obk_billEndDate = (Long) mMap.get("nbk_billEndDate");
					int obk_billisReminder = (Integer) mMap.get("nbk_billisReminder");
					int obk_billisRepeat = (Integer) mMap.get("nbk_billisRepeat");
					int obk_billisVariaable = (Integer) mMap.get("bk_billisVariaable");
					long obk_billReminderDate = (Long) mMap.get("nbk_billReminderDate");
					long obk_billReminderTime = (Long)mMap.get("bk_billReminderTime");
					int obk_billRepeatNumber = (Integer) mMap.get("nbk_billRepeatNumber");
					int obk_billRepeatType = (Integer) mMap.get("nbk_billRepeatType");
					int billhasAccount = (Integer)mMap.get("billhasAccount");

					long Okey = insertObject( 0 , obillamount , obk_billAmountUnknown,obk_billAutoPay ,obk_billDuedate,obk_billDuedateNew,  //?????��?��??�?�?
							obk_billEndDate, obk_billisReminder, obk_billisRepeat, obk_billisVariaable,obk_billReminderDate,obk_billReminderTime ,obk_billRepeatNumber,
							obk_billRepeatType,obillObjecthasBill,billhasAccount);

					billPayO(remain,0 ,todayMill , Okey,new String(),"Quick pay") ;
					
					flag = 3 ;
					mMap.put("BK_Bill_Id", (int) Okey);
					mMap.put("indexflag", 3);
					mMap.put("bk_billsDelete", 0);
					mMap.put("billObjecthasBill", bill_id);
					
					bill_id = (int) Okey;
					Intent intent = new Intent();
					intent.putExtra("id", Okey);
					setResult(10, intent);
//					handler.post(listUpdater);
					mHandler.post(listUpdater);

				} else if (flag == 3) {

					long key = billPayO(remain ,0,todayMill,bill_id,new String(),"Quick pay");
//					handler.post(listUpdater);
					mHandler.post(listUpdater);
					Intent intent = new Intent();
					intent.putExtra("id", key);
					setResult(10, intent);
				}
				
//				 Intent service=new Intent(PaymentActivity.this, BillNotificationService.class);   //�ж�����
//				 PaymentActivity.this.startService(service);  

			}
		});

		Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		addPayButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				View view = flater.inflate(R.layout.dialog_payment, null);

				amounTextView = (EditText)view.findViewById(R.id.payAmount);
				duedateButton =(Button)view.findViewById(R.id.dueDate);
				confTextView = (EditText)view.findViewById(R.id.ConfNumber);
				memoTextView = (EditText)view.findViewById(R.id.memo);

				java.text.DecimalFormat  df  = new java.text.DecimalFormat("0.00");
				
//				 Intent service=new Intent(PaymentActivity.this, BillNotificationService.class);   //�ж�����
//				 PaymentActivity.this.startService(service);  
				 
				amounTextView.setText(df.format(remain));
				amounTextView.addTextChangedListener(new TextWatcher() { // 设置?????��?????两�??�????
					private boolean isChanged = false;

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						// TODO Auto-generated method stub

					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
						// TODO Auto-generated method stub
					}

					@Override
					public void afterTextChanged(Editable s) {
						// TODO Auto-generated method stub

						if (isChanged) {// ----->�????�?�??????��?????�????    
							return;    
						}    
						String str = s.toString();    

						isChanged = true;    
						String cuttedStr = str;    
						/* ?????��??�?串中???dot */    
						for (int i = str.length() - 1; i >= 0; i--) {    
							char c = str.charAt(i);    
							if ('.' == c) {    
								cuttedStr = str.substring(0, i) + str.substring(i + 1);    
								break;    
							}    
						}    
						/* ?????��????��??�????0 */    
						int NUM = cuttedStr.length();   
						int zeroIndex = -1;  
						for (int i = 0; i < NUM - 2; i++) {    
							char c = cuttedStr.charAt(i);    
							if (c != '0') {    
								zeroIndex = i;  
								break;  
							}else if(i == NUM - 3){  
								zeroIndex = i;  
								break;  
							}  
						}    
						if(zeroIndex != -1){  
							cuttedStr = cuttedStr.substring(zeroIndex);  
						}  
						/* �?�?3�?�?0 */    
						if (cuttedStr.length() < 3) {    
							cuttedStr = "0" + cuttedStr;    
						}    
						/* ???�?dot�?以�?�示�???��?��??两�?? */    
						cuttedStr = cuttedStr.substring(0, cuttedStr.length() - 2)    
						+ "." + cuttedStr.substring(cuttedStr.length() - 2);    


						amounTextView.setText(cuttedStr);
						//								dueAmountFloat= dueAmonut.getText().toString();
						amounTextView.setSelection(cuttedStr.length());
						isChanged = false;
					}
				});

				updateDisplay();
				duedateButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						DatePickerDialog DPD = new DatePickerDialog( // ??��??theme
								new ContextThemeWrapper(PaymentActivity.this,
										android.R.style.Theme_Holo_Light),
										mDateSetListener, mYear, mMonth, mDay);
						DPD.setTitle("Due Date");
						DPD.show();
					}
				});

				AlertDialog.Builder addPaymentDialog = new AlertDialog.Builder(PaymentActivity.this);
				addPaymentDialog.setView(view);
				addPaymentDialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						double pay = Double.parseDouble(amounTextView.getText().toString()); //注�??.221452?????��?��????????�?

						if (pay == 0) {
							new AlertDialog.Builder(PaymentActivity.this)
							.setTitle("Warning! ")
							.setMessage(
							"Please make sure the Pay Amount is not zero! ")
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
							String  ConfNumber = confTextView.getText().toString();
							String  memo = memoTextView.getText().toString();

							//								Log.v("mdb", msg);

							if (flag == 0 || flag ==1) {

								long key = billPay(pay ,1,payDateLong,bill_id,ConfNumber,memo);
//								handler.post(listUpdater);
								mHandler.post(listUpdater);
								Intent intent = new Intent();
								intent.putExtra("id", key);
								setResult(10, intent);

							} else if (flag == 2) {

								int obillObjecthasBill = (Integer)mMap.get("BK_Bill_Id");
								double obillamount = (Double)mMap.get("nbillamount");
								int obk_billAmountUnknown = (Integer)mMap.get("nbk_billAmountUnknown");
								int obk_billAutoPay = (Integer)mMap.get("nbk_billAutoPay");
								long obk_billDuedate = (Long) mMap.get("nbk_billDuedate");
								long obk_billDuedateNew = 0;
								long obk_billEndDate = (Long) mMap.get("nbk_billEndDate");
								int obk_billisReminder = (Integer) mMap.get("nbk_billisReminder");
								int obk_billisRepeat = (Integer) mMap.get("nbk_billisRepeat");
								int obk_billisVariaable = (Integer) mMap.get("bk_billisVariaable");
								long obk_billReminderDate = (Long) mMap.get("nbk_billReminderDate");
								long obk_billReminderTime = (Long) mMap.get("bk_billReminderTime"); 
								int obk_billRepeatNumber = (Integer) mMap.get("nbk_billRepeatNumber");
								int obk_billRepeatType = (Integer) mMap.get("nbk_billRepeatType");
								int billhasAccount = (Integer)mMap.get("billhasAccount");

								long Okey = insertObject( 0 , obillamount , obk_billAmountUnknown,obk_billAutoPay ,obk_billDuedate,obk_billDuedateNew,  //?????��?��??�?�?
										obk_billEndDate, obk_billisReminder, obk_billisRepeat, obk_billisVariaable,obk_billReminderDate,obk_billReminderTime,obk_billRepeatNumber,
										obk_billRepeatType,obillObjecthasBill,billhasAccount);

								billPayO(pay,1 ,payDateLong ,Okey,ConfNumber,memo) ;
								
								flag = 3 ;
								mMap.put("BK_Bill_Id", (int) Okey);
								mMap.put("indexflag", 3);
								mMap.put("bk_billsDelete", 0);
								mMap.put("billObjecthasBill", bill_id);
								
								bill_id =  Integer.parseInt(Okey+"") ;
//								Log.v("mtake", "mMap:"+mMap);
//								Log.v("mtake", "flag:"+flag);
//								Log.v("mtake", "bill_id"+bill_id);
								
								Intent intent = new Intent();
								intent.putExtra("id", Okey);
								setResult(10, intent);
//								handler.post(listUpdater);
								mHandler.post(listUpdater);

							} else if (flag == 3) {
								
//								Log.v("mtake", "mMap:"+mMap);
//								Log.v("mtake", "3flag:"+flag);
//								Log.v("bill_id", "3bill_id"+bill_id);
								
								long key = billPayO(pay ,1,payDateLong,bill_id,ConfNumber,memo);
//								handler.post(listUpdater);
								mHandler.post(listUpdater);
								Intent intent = new Intent();
								intent.putExtra("id", key);
								setResult(10, intent);
							}

						}

					}
				});
				addPaymentDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				});
				addPaymentDialog.show();

			}
		});

		getActionBar().setDisplayHomeAsUpEnabled(true);//????????�示�?�?�????�?�?�???????
		
		}else {
			Log.v("mmes", " *********kill");
			finish();
		}

	}
	
	private DatePickerDialog.OnDateSetListener mDatePaySetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			pYear = year;
			pMonth = monthOfYear;
			pDay = dayOfMonth;
			updatePayDisplay();
		}
	};
	

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
		}
	};

	public long insertObject(int bk_billsDelete, double billamount ,int nbk_billAmountUnknown ,int bk_billAutoPay ,long bk_billDuedate,long  bk_billDuedateNew,  //?????��?��??�?�?
			long bk_billEndDate, int bk_billisReminder, int bk_billisRepeat, int bk_billisVariaable,long bk_billReminderDate,long bk_billReminderTime ,int bk_billRepeatNumber,
			int bk_billRepeatType,long billObjecthasBill,int billhasAccount) {

		bksql = new BillKeeperSql(PaymentActivity.this);
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
	
	private void updatePayDisplay() {
		// TODO Auto-generated method stub
		duedateButton.setText(new StringBuilder()
		// Month is 0 based so add 1
		.append(pMonth + 1).append("-").append(pDay).append("-")
		.append(pYear));
		String untilDateString = (new StringBuilder().append(pMonth + 1).append("-").append(pDay).append("-").append(pYear)).toString();
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(untilDateString));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		payChangeDateLong = c.getTimeInMillis();
	}

	private void updateDisplay() {
		// TODO Auto-generated method stub
		duedateButton.setText(new StringBuilder()
		// Month is 0 based so add 1
		.append(mMonth + 1).append("-").append(mDay).append("-")
		.append(mYear));
		String untilDateString = (new StringBuilder().append(mMonth + 1).append("-").append(mDay).append("-").append(mYear)).toString();
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(untilDateString));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		payDateLong = c.getTimeInMillis();
	}
	
	
	public Runnable listUpdater = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String repString = "";
			String autoString = "";
			String divideString = "";
			double dueAmount = 0;
			double totalPayAmount = 0;
			String dueAmountString = "";
			java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");

			getData(); 

			int BK_Bill_Id = (Integer)mMap.get("BK_Bill_Id");
			double billamount = (Double)mMap.get("nbillamount");
			int bk_billAmountUnknown = (Integer)mMap.get("nbk_billAmountUnknown");
			int bk_billAutoPay = (Integer)mMap.get("nbk_billAutoPay");
			long bk_billDuedate = (Long)mMap.get("nbk_billDuedate"); //??��??�?�?件�??�?�???��??
			int bk_billisReminder = (Integer)mMap.get("nbk_billisReminder");
			int bk_billisRepeat = (Integer)mMap.get("nbk_billisRepeat");
			int bk_billisVariaable = (Integer)mMap.get("bk_billisVariaable");
			long bk_billReminderDate = (Long)mMap.get("nbk_billReminderDate");
			long bk_billReminderTime = (Long)mMap.get("bk_billReminderTime");
			int bk_billRepeatNumber = (Integer)mMap.get("nbk_billRepeatNumber"); 
			int bk_billRepeatType = (Integer)mMap.get("nbk_billRepeatType"); 
			int billhasAccount = (Integer)mMap.get("billhasAccount");

			String bk_accountName = (String)mMap.get("bk_accountName");
			int accounthasCategory = (Integer)mMap.get("accounthasCategory");
			int payState = (Integer)mMap.get("payState");
			int bk_categoryIconName = (Integer)mMap.get("bk_categoryIconName");
			String bk_categoryName = (String)mMap.get("bk_categoryName");

			mImageView.setImageResource(Common.CATEGORYICON[bk_categoryIconName]);
			accountTextView.setText(bk_accountName);
			dateTextView.setText(getMilltoDate(bk_billDuedate));

			if (bk_billAmountUnknown == 1) {
				currencyTextView1.setVisibility(View.INVISIBLE);
				dueAmount = 0.00;
				dueAmountString = "N/A";
				totalPayAmount = totalPay;
				markButton.setClickable(false);
				markButton.setBackgroundResource(R.drawable.make_as_paid_gray);
				remain = 0.00;
				if (curSize > 0 || totalPayAmount > 0) {
					markButton.setClickable(false);
					markButton.setBackgroundResource(R.drawable.make_as_paid_gray);
					quickButton.setClickable(false);
					quickButton.setBackgroundResource(R.drawable.quick_pay_gray);
					addPayButton.setClickable(false);
					addPayButton.setBackgroundResource(R.drawable.add_payment_gray);
					paidImageView.setVisibility(View.VISIBLE);
				}else{
					paidImageView.setVisibility(View.INVISIBLE);
				}
			} else {
				dueAmountString = Common.doublepoint2str(billamount);
				totalPayAmount = totalPay;

				BigDecimal b1 = new BigDecimal(billamount); //???�?�?�?精度
				BigDecimal b2 = new BigDecimal(Double.toString(totalPay)); //???�?�?�?精度
				remain = (b1.subtract(b2)).doubleValue();
				remain = Double.parseDouble(df.format(remain));
				
				if (remain < 0) {
					remain=0.00;
				}
				if (totalPay >= billamount || checkPayMode ==1 ) {
					markButton.setClickable(false);
					markButton.setBackgroundResource(R.drawable.make_as_paid_gray);
					quickButton.setClickable(false);
					quickButton.setBackgroundResource(R.drawable.quick_pay_gray);
					addPayButton.setClickable(false);
					addPayButton.setBackgroundResource(R.drawable.add_payment_gray);
					paidImageView.setVisibility(View.VISIBLE);
					checkPayMode = 0;
					if (checkPayMode != 1) {
						remain=0.00;
					} 
					
				}else {
					paidImageView.setVisibility(View.INVISIBLE);
					if (curSize > 0) {
						markButton.setClickable(true);
						markButton.setBackgroundResource(R.drawable.make_as_paid_selector);
					}else {
						markButton.setClickable(false);
						markButton.setBackgroundResource(R.drawable.make_as_paid_gray);
					}
					quickButton.setClickable(true);
					quickButton.setBackgroundResource(R.drawable.quick_pay_selector);
					addPayButton.setClickable(true);
					addPayButton.setBackgroundResource(R.drawable.add_payment_selector);
				}
			}

			Date date=new Date();
			Map<String, Object> pMap;
			totalTextView.setText(dueAmountString+"");
			paidTextView.setText(Common.doublepoint2str(Double.parseDouble(totalPayAmount+"")));
			remainTextView.setText(Common.doublepoint2str( Double.parseDouble(remain+"")));
			
			if (bk_billisRepeat == 1 ) {
				int repeatType = bk_billRepeatType;
				int repeatNumber = bk_billRepeatNumber;
				repString = "Once every "+ repeatNumber +" "+turnToRepeatType(repeatType);
			} if (bk_billAutoPay == 1) {
				autoString = "auto pay";
			}
			if(bk_billisRepeat == 1 && bk_billAutoPay == 1){
				divideString = ",";
			}
			repeaTextView.setText(repString+divideString+autoString);
			
			mHandler.sendEmptyMessage(0);
//			if (flag ==2) {
//				pAadaAdapter.notifyDataSetChanged();
//			}else {
//				pAadaAdapter.setAdapterList(dataList);
//				pAadaAdapter.notifyDataSetChanged();
//			}
			

		}
	};
	
private Handler mHandler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message paramMessage) {
			// TODO Auto-generated method stub
			
			if (flag ==2) {
				pAadaAdapter.notifyDataSetChanged();
			}else {
				pAadaAdapter.setAdapterList(dataList);
				pAadaAdapter.notifyDataSetChanged();
			}
				
			return true;
		}
	});
	
	public long deletePay(int key) {
		bksql = new BillKeeperSql(this);
		SQLiteDatabase db = bksql.getWritableDatabase();
		String id = key+"";
		long row=db.delete("BK_Payment", "_id = ?", new String[]{id});
		db.close();
		return 1;
	}
	
	public long  updateBillpay(int key ,double payAmount ,long payDate, String conf,String memo) {
		
		bksql = new BillKeeperSql(this);
		SQLiteDatabase db = bksql.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("bk_payAmount", payAmount);
		values.put("bk_payDate", payDate);
		values.put("bk_payConfigNumber", conf);
		values.put("bk_payMemo", memo);

		long pkey = db.update("BK_Payment", values, "_id = ?", new String[]{key+""});
		db.close();
		return pkey;
	}

	public long billPay(double payAmount,int payMode ,long payDate ,long paymenthasBill, String conf,String memo) {
		bksql = new BillKeeperSql(this);
		SQLiteDatabase db = bksql.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("bk_payAmount", payAmount);
		values.put("bk_payMode", payMode);
		values.put("bk_payDate", payDate);
		values.put("paymenthasBill", paymenthasBill);
		values.put("bk_payConfigNumber", conf);
		values.put("bk_payMemo", memo);

		db.execSQL("PRAGMA foreign_keys = ON ");
		long pkey = db.insert("BK_Payment", null, values);
		db.close();
		return pkey;
	}

	public long billPayO(double payAmount,int payMode ,long payDate ,long paymenthasBill ,String conf,String memo ) {

		bksql = new BillKeeperSql(this);
		SQLiteDatabase db = bksql.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("bk_payAmount", payAmount);
		values.put("bk_payMode", payMode);
		values.put("bk_payDate", payDate);
		values.put("paymenthasBillO", paymenthasBill);
		values.put("bk_payConfigNumber", conf);
		values.put("bk_payMemo", memo);

		db.execSQL("PRAGMA foreign_keys = ON ");
		long pkey = db.insert("BK_Payment", null, values);
		db.close();
		return pkey;

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

	public String getMilltoDate(long milliSeconds) {//�?�?�?�?????????��????��?????年�?????
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}
	
	public Map<String, Object> getPayDate(int key) {
		
		bksql = new BillKeeperSql(this);
		SQLiteDatabase db = bksql.getReadableDatabase();
		String sql1 = "select bk_payAmount ,bk_payDate ,bk_payConfigNumber ,bk_payMemo from BK_Payment where _id = "+key; 
		Cursor cursor = db.rawQuery(sql1, null);
		
		Map<String, Object> map = null;
		while (cursor.moveToNext()) {
			map = new HashMap<String, Object>();

			double bk_payAmount = cursor.getDouble(0);
			long bk_payDate = cursor.getLong(1);
			String bk_payConfigNumber = cursor.getString(2);
			String bk_payMemo = cursor.getString(3);
			
				map.put("bk_payAmount", bk_payAmount);
				map.put("bk_payDate", bk_payDate);
				map.put("bk_payConfigNumber", bk_payConfigNumber);
				map.put("bk_payMemo", bk_payMemo);
		}
		
		return map;
	}

	private void getData() {

		dataList.clear();
//		pAadaAdapter.notifyDataSetChanged();
		bksql = new BillKeeperSql(this);
		SQLiteDatabase db = bksql.getReadableDatabase();

		Map<String, Object> map;
		double zero= 0.00f; 
		BigDecimal b1 = new BigDecimal(Double.toString(zero)); //???�?�?�?精度

		if (flag==1 || flag==0 ){

			String sql1 = "select _id ,bk_payAmount ,bk_payDate ,bk_payMode from BK_Payment where BK_Payment.paymenthasBill = "+bill_id+" order by bk_payDate DESC "; 
			Cursor cursor = db.rawQuery(sql1, null);
			curSize = cursor.getCount();
			while (cursor.moveToNext()) {
				map = new HashMap<String, Object>();

				int pay_id = cursor.getInt(0); 
				double bk_payAmount = cursor.getDouble(1);
				long bk_payDate = cursor.getLong(2);
				int bk_payMode =  cursor.getInt(3);

				BigDecimal b2 = new BigDecimal(Double.toString(bk_payAmount));
				b1 = b1.add(b2);
				
				if (bk_payMode == -1) {      //1mark as pay表示�?�?bill???�???�为pay??��??
					checkPayMode = 1;
				}
				java.text.DecimalFormat  df = new java.text.DecimalFormat("0.00");

//				if (bk_payMode != -1) {
					map.put("pay_id", pay_id);
					map.put("bk_payAmount", Double.parseDouble(df.format(bk_payAmount)));
					map.put("bk_payDate", bk_payDate);
					map.put("bk_payMode", bk_payMode);
					dataList.add(map);
//				}
				
			}

			totalPay = b1.doubleValue(); //??��?��??�?�?
			cursor.close();

		} else if (flag == 2) {

//			map = new HashMap<String, Object>();
//			dataList.add(map);//??��??为空

		}else if ( flag == 3 ) {

			String sql1 = "select _id ,bk_payAmount ,bk_payDate ,bk_payMode from BK_Payment where BK_Payment.paymenthasBillO = "+bill_id+" order by bk_payDate DESC ";
			Cursor cursor = db.rawQuery(sql1, null);
			curSize = cursor.getCount();
			while (cursor.moveToNext()) {
				map = new HashMap<String, Object>();

				int pay_id = cursor.getInt(0); 
				double bk_payAmount = cursor.getDouble(1);
				long bk_payDate = cursor.getLong(2);
				int bk_payMode =  cursor.getInt(3);

				BigDecimal b2 = new BigDecimal(Double.toString(bk_payAmount));
				b1 = b1.add(b2);
				
//				if (bk_payMode != -1) {
				map.put("pay_id", pay_id);
				map.put("bk_payAmount", bk_payAmount);
				map.put("bk_payDate", bk_payDate);
				map.put("bk_payMode", bk_payMode);
				dataList.add(map);
//				}

				if (bk_payMode == -1) {      //mark
					checkPayMode = 1;
					Log.v("mmes", "????????��??checkpaymode"+checkPayMode);
				}
			}
			totalPay = b1.doubleValue(); //??��?��??�?�?
			cursor.close();
		} 

		db.close();

	}

	public  Map<String, Object> getTemplateDate(int tId){ //??��?????�?�?件�??模�??Bill表中

		Map<String, Object> bMap = new HashMap<String, Object>();
		bksql = new BillKeeperSql(this); 
		SQLiteDatabase db = bksql.getReadableDatabase();
		String  sql = " select BK_Bill.* ,BK_Account.bk_accountName ,BK_Account.accounthasCategory,BK_Category.bk_categoryIconName ,BK_Category.bk_categoryName from BK_Bill,BK_Account,BK_Category where BK_Bill._id = "+tId+" and BK_Category._id =BK_Account.accounthasCategory and BK_Bill.billhasAccount = BK_Account._id";

		Cursor cursorEA = db.rawQuery(sql, null);
		while (cursorEA.moveToNext()) {

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
			bMap.put("bk_billReminderTime", bk_billReminderTime);
			bMap.put("nbk_billRepeatNumber", bk_billRepeatNumber);
			bMap.put("nbk_billRepeatType", bk_billRepeatType);
			bMap.put("billhasAccount", billhasAccount);
			bMap.put("bk_accountName", bk_accountName);
			bMap.put("accounthasCategory", accounthasCategory);
			bMap.put("bk_categoryIconName", bk_categoryIconName);
			bMap.put("bk_categoryName", bk_categoryName);
			bMap.put("payState", -2); //添�?????pay??��????��??
			bMap.put("indexflag", 1); 

		}
		cursorEA.close();  
		db.close();

		return bMap;
	}
	
	public  Map<String, Object> getIndepDate(int tId){ //??��?????�?�?件�??模�??Bill表中

		Map<String, Object> bMap = new HashMap<String, Object>();
		bksql = new BillKeeperSql(this); 
		SQLiteDatabase db = bksql.getReadableDatabase();
		String  sql = " select BK_Bill.* ,BK_Account.bk_accountName ,BK_Account.accounthasCategory,BK_Category.bk_categoryIconName ,BK_Category.bk_categoryName from BK_Bill,BK_Account,BK_Category where BK_Bill._id = "+tId+" and BK_Category._id =BK_Account.accounthasCategory and BK_Bill.billhasAccount = BK_Account._id";

		Cursor cursorEA = db.rawQuery(sql, null);
		while (cursorEA.moveToNext()) {

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
			bMap.put("bk_billReminderTime", bk_billReminderTime);
			bMap.put("nbk_billRepeatNumber", bk_billRepeatNumber);
			bMap.put("nbk_billRepeatType", bk_billRepeatType);
			bMap.put("billhasAccount", billhasAccount);
			bMap.put("bk_accountName", bk_accountName);
			bMap.put("accounthasCategory", accounthasCategory);
			bMap.put("bk_categoryIconName", bk_categoryIconName);
			bMap.put("bk_categoryName", bk_categoryName);
			bMap.put("payState", -2); //添�?????pay??��????��??
			bMap.put("indexflag", 0); 

		}
		cursorEA.close();  
		db.close();

		return bMap;
	}

	

	public Map<String, Object> getObjectDate(int oId){ //??��?????�?�?件�????��??BillObject表中

		Map<String, Object> bMap = new HashMap<String, Object>();
		bksql = new BillKeeperSql(this); 
		SQLiteDatabase db = bksql.getReadableDatabase();
		String  sql = "select BK_BillObject.* ,BK_Account.bk_accountName ,BK_Account.accounthasCategory ,BK_Category.bk_categoryIconName ,BK_Category.bk_categoryName from BK_BillObject ,BK_Account,BK_Category where BK_BillObject._id = "+oId+" and BK_BillObject.billObjecthasAccount = BK_Account._id and BK_Category._id =BK_Account.accounthasCategory";

		Cursor cursorEA = db.rawQuery(sql, null);
		while (cursorEA.moveToNext()) {

			int BK_Bill_Id =  cursorEA.getInt(0); 
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
			String bk_accountName = cursorEA.getString(25);
			int accounthasCategory = cursorEA.getInt(26);
			int bk_categoryIconName = cursorEA.getInt(27);
			String bk_categoryName  = cursorEA.getString(28);

			bMap.put("BK_Bill_Id", BK_Bill_Id); 
			bMap.put("bk_billsDelete", bk_billsDelete);  //?????��?��??�?件�?????�??????��??indexflag�???��????��?��??该�??: 正常�?�?delete�?0�??????�为1�?2??��??�?duedate???�?件�??设置该�??�??????��??为空�????表示�?delete�?
			bMap.put("nbillamount", billamount);
			bMap.put("nbk_billAmountUnknown", bk_billAmountUnknown);
			bMap.put("nbk_billAutoPay", bk_billAutoPay);
			bMap.put("nbk_billDuedate", bk_billDuedate);
			bMap.put("bk_billDuedateNew", bk_billDuedateNew); //duedate�?�???��?????�?�?�???��??�?�?件中??????�?�?�?�???��????��??
			bMap.put("nbk_billEndDate", bk_billEndDate);
			bMap.put("nbk_billisReminder", bk_billisReminder);
			bMap.put("nbk_billisRepeat", bk_billisRepeat);
			bMap.put("bk_billisVariaable", bk_billisVariaable);
			bMap.put("nbk_billReminderDate", bk_billReminderDate);
			bMap.put("bk_billReminderTime", bk_billReminderTime);
			bMap.put("nbk_billRepeatNumber", bk_billRepeatNumber);
			bMap.put("nbk_billRepeatType", bk_billRepeatType);
			bMap.put("bk_accountName", bk_accountName);
			bMap.put("billObjecthasBill", billObjecthasBill); //??��??�?件�??bill�????
			bMap.put("accounthasCategory", accounthasCategory);
			bMap.put("billhasAccount", billhasAccount);
			bMap.put("bk_categoryIconName", bk_categoryIconName);
			bMap.put("bk_categoryName", bk_categoryName);
			bMap.put("payState", -2); //pay??��????��??
			bMap.put("indexflag", 3); 

		}
		cursorEA.close();  
		db.close();

		return bMap;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Log.v("mdb", "resultCode"+resultCode);
		
//		Bundle bundle = data.getExtras();
//		
//		int dd = bundle.getInt("flag");
//		
//		Log.v("mdb", "dd"+dd);
		//		Bundle bunde = data.getExtras();  
		//		int sex = bunde.getInt("flag");  


//		Intent data1 = data.getIntent();
//		int flag1  = data.getExtras();  //�????�?????????��??�????�?�????mMap�??????��????��?��????��??
//		int bill_id1 = (int)data.getLongExtra("pKey", 0);
		
//		int flag1  = data.getIntExtra("flag",-888);  //�????�?????????��??�????�?�????mMap�??????��????��?��????��??
//		int bill_id1 = data.getIntExtra("pKey", -999);
//		Log.v("mdb", "flag1"+flag1);
//		Log.v("mdb", "bill_id1"+bill_id1);
		switch (resultCode) {

		case 20:

			if (data != null) {
									    
				int flag1  = data.getIntExtra("flag", -1);  //�????�?????????��??�????�?�????mMap�??????��????��?��????��??
				int bill_id1 = data.getIntExtra("pKey", 0);
				Log.v("mtake", "flag �????"+flag1);
				Log.v("mtake", "bill_id�????"+bill_id1);

				if (flag1 != -1 && bill_id1!=0) {
					
					
					mMap.clear();
					if (flag1 == 1 ) {
						mMap = getTemplateDate(bill_id1);
					} else if (flag1==0) {
						mMap = getIndepDate(bill_id1);
					}else if(flag1 == 3){
						mMap = getObjectDate(bill_id1);
					}
					
				
					flag = flag1;
					bill_id = bill_id1;
					
					Log.v("mdb", "??��?????map"+mMap);
//					handler.post(listUpdater);
					mHandler.post(listUpdater);
					
					Intent intent = new Intent();
					intent.putExtra("id", -1);
					setResult(10, intent);
//
				} else {
					Intent intent = new Intent();
					intent.putExtra("id", -1);
					setResult(10, intent);
					finish();

				}

			}

			break;

		case 21:

			if (data != null) {
				Intent intent = new Intent();
				intent.putExtra("id", -1);
				setResult(10, intent);
				finish();
			}

			break;

		}

	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:  //�?�?�???????�???????id
			finish();
			return true;

		case R.id.imenu_edit:

			Intent intent = new Intent();
			intent.putExtra("dataMap",(Serializable)mMap);
			Log.v("mtake", "�???��??mMap�?"+mMap);
			intent.setClass(this, EditBillActivity.class);
			startActivityForResult(intent, 20);

			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.payment, menu);
		return true;
	}

}
