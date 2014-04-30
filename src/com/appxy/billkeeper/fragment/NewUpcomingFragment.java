package com.appxy.billkeeper.fragment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appxy.billkeeper.R;
import com.appxy.billkeeper.activity.AddNewAccountActivity;
import com.appxy.billkeeper.activity.CategoriesActivity;
import com.appxy.billkeeper.activity.EditAccountActivity;
import com.appxy.billkeeper.activity.EditBillActivity;
import com.appxy.billkeeper.activity.NewBillActivity;
import com.appxy.billkeeper.activity.PaymentActivity;
import com.appxy.billkeeper.activity.SlidingMenuActivity;
import com.appxy.billkeeper.adapter.CategoriesActivityAdapter;
import com.appxy.billkeeper.adapter.DialogDeleteBillAdapter;
import com.appxy.billkeeper.adapter.DialogEditBillAdapter;
import com.appxy.billkeeper.adapter.DialogUpcomingItemAdapter;
import com.appxy.billkeeper.adapter.UpcomingExpandableListViewAdapter;
import com.appxy.billkeeper.adapter.UpcomingFragmentAdapter;
import com.appxy.billkeeper.adapter.UpcomingFragmentExpandableListviewAdapter;
import com.appxy.billkeeper.db.BillKeeperSql;
import com.appxy.billkeeper.entity.Common;
import com.appxy.billkeeper.entity.RecurringEvent;
import com.appxy.billkeeper.service.BillNotificationService;

import android.R.integer;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NewUpcomingFragment extends Fragment {
	
	private ListView listView;
	private List<Map<String, Object>> mdata;
	private List<Map<String, Object>> overDuedata;
	private List<Map<String, Object>> overDue7data;
	private List<Map<String, Object>> overDue30data;

	private List<Map<String, Object>> overDuedataKey;
	private List<Map<String, Object>> overDue7dataKey;
	private List<Map<String, Object>> overDue30dataKey;

	private BillKeeperSql bksql;

	private Map<String, Object> temMap;
	private List<Map<String, Object>> mKeyPosition;
	private long nowMillis;
	private Menu mMenu;
	private AlertDialog alertDialog;
	private ListView diaListView;
	private DialogUpcomingItemAdapter DiaListViewAdapter;
	private ProgressDialog dialog_loading;
	private Handler handler;
	private int curSize;
	private LayoutInflater flater;
//	private TextView currencyImageView;

	private List<Map<String, Object>> dataList;
	private double totalPay = 0;

	private List<Map<String, Object>> list;
	private int checkListCount = 2;

	private Button duedateButton;
	private EditText amounTextView;
	private EditText confTextView;
	private EditText memoTextView;
	private int mYear;
	private int mMonth;
	private int mDay;
	private long payDateLong = 0;
	
	private int  edit_status = 0;
	private DialogDeleteBillAdapter dialogEditBillAdapter ;
	private AlertDialog editDialog;
	private LinearLayout relativeLayoutBac;
	
	private ExpandableListView mExpandableListView ;
	private UpcomingExpandableListViewAdapter mExpandableListviewAdapter ;
	
	private List<Map<String, Object>> groupDataList; //Group���
	private List<List<Map<String, Object>>> childrenAllDataList; //����children���
	private ImageView mAds;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		DiaListViewAdapter = new DialogUpcomingItemAdapter(getActivity());
		setHasOptionsMenu(true);
		dialogEditBillAdapter =new DialogDeleteBillAdapter(getActivity());
		getActivity().getActionBar().setDisplayShowTitleEnabled(true);
		getActivity().setTitle("Upcoming Bills");
	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		// ��һ�����������Fragment��Ҫ��ʾ�Ľ��沼��,�ڶ������������Fragment������Activity,����������Ǿ�����fragment�Ƿ�����Activity
		
		long s = System.currentTimeMillis();
		
		View view = inflater.inflate(R.layout.new_upcoming_fragment, container,
				false);
		dataList = new ArrayList<Map<String, Object>>();
		list = new ArrayList<Map<String, Object>>();
		flater = LayoutInflater.from(getActivity());
		
		relativeLayoutBac = (LinearLayout)view.findViewById(R.id.up_bag);
		mAds = (ImageView)view.findViewById(R.id.ads);
		mExpandableListView = (ExpandableListView)view.findViewById(R.id.expandableListView1);
		mExpandableListviewAdapter = new UpcomingExpandableListViewAdapter(getActivity());
		mExpandableListView.setAdapter(mExpandableListviewAdapter);
		mExpandableListView.setGroupIndicator(null); //ȥ��groupǰ��ļ�ͷ 
		mExpandableListView.setDividerHeight(0);
		
		groupDataList = new ArrayList<Map<String,Object>>();
		childrenAllDataList = new ArrayList<List<Map<String,Object>>>();
		
		 handler = new Handler();
		 handler.post(allUpdater);
//		mUpdater();
		/*
         * 
         */
		Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		
		mAds.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				final String appName = "com.appxy.billkeeperpro&hl=en";

//				List<ApplicationInfo> ls = getActivity().getPackageManager().getInstalledApplications(
//								0);
//				int size = ls.size();
//				ApplicationInfo info = null;
//				for (int i = 0; i < size; i++) {
//
//					if (ls.get(i).packageName.equals("com.android.vending")) {
//						info = ls.get(i);
//					}
//				}
//				if (info != null) {
//					Intent intent = new Intent(
//							Intent.ACTION_VIEW);
//					intent.setData(Uri
//							.parse("market://details?id="+ appName));
//					intent.setPackage(info.packageName);
//					startActivity(intent);
//				} else {
//					startActivity(new Intent(
//							Intent.ACTION_VIEW,
//							Uri.parse("http://play.google.com/store/apps/details?id="
//									+ appName)));
//				}
				
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
		
		mExpandableListView.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				if (childrenAllDataList != null) {
					
					Map<String, Object> mtMap = new HashMap<String, Object>();
					mtMap=childrenAllDataList.get(groupPosition).get(childPosition);
					Intent intent = new Intent();
					intent.putExtra("dataMap",(Serializable)mtMap);
					intent.setClass(getActivity(), PaymentActivity.class);
					startActivityForResult(intent, 10);
				}
					
				return false;
			}
		});
		
		mExpandableListView.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						
						int groupPosition = ExpandableListView.getPackedPositionGroup(arg3);
			            int childPosition = ExpandableListView.getPackedPositionChild(arg3);
			            
			            Log.v("mmes", "groupPosition "+groupPosition+" childPosition"+childPosition);
			            
						// TODO Auto-generated method stub
						View dialogview = inflater.inflate(R.layout.dialog_upcoming_item_operation, null);
						diaListView = (ListView) dialogview.findViewById(R.id.dia_listview);
						
						final int flag = (Integer) childrenAllDataList.get(groupPosition).get(childPosition).get("indexflag");
						final int BK_Bill_Id = (Integer) childrenAllDataList.get(groupPosition).get(childPosition).get("BK_Bill_Id");
						final int size = (Integer)childrenAllDataList.get(groupPosition).get(childPosition).get("payState");
						final double remain = (Double) childrenAllDataList.get(groupPosition).get(childPosition).get("remain");
						final Map<String, Object> mMap = childrenAllDataList.get(groupPosition).get(childPosition);
						java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
						
						checkListCount = size;

						diaListView.setOnItemClickListener(new OnItemClickListener() {

									@Override
									public void onItemClick(
											AdapterView<?> arg0, View arg1,
											int arg2, long arg3) {
										// TODO Auto-generated method stub

										Date date = new Date();
										long todayMill = date.getTime();

									  if (checkListCount == 4) {

											if (arg2 == 0) {

//												 Intent service=new Intent(getActivity(), BillNotificationService.class);   //�ж�����
//												 getActivity().startService(service);  
												 
												if (flag == 0 || flag ==1) {

													long key = billPay(remain ,0,todayMill,BK_Bill_Id,new String(),"Quick pay");
//													mUpdater();
													 handler = new Handler();
													 handler.post(allUpdater);
													
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

													long Okey = insertObject( 0 , obillamount , obk_billAmountUnknown,obk_billAutoPay ,obk_billDuedate,obk_billDuedateNew,  //���������¼�
															obk_billEndDate, obk_billisReminder, obk_billisRepeat, obk_billisVariaable,obk_billReminderDate,obk_billReminderTime ,obk_billRepeatNumber,
															obk_billRepeatType,obillObjecthasBill,billhasAccount);

													billPayO(remain,0 ,todayMill , Okey,new String(),"Quick pay") ;
//													mUpdater();
													 handler = new Handler();
													 handler.post(allUpdater);
												} else if (flag == 3) {

													long key = billPayO(remain ,0,todayMill,BK_Bill_Id,new String(),"Quick pay");
//													mUpdater();
													 handler = new Handler();
													 handler.post(allUpdater);
												}
												// handler.post(allUpdater);
											
												alertDialog.dismiss();

											} else if (arg2 == 1) {

												
												addPaymentDialog(remain, BK_Bill_Id,flag ,mMap);
//												mUpdater();
												 handler = new Handler();
												 handler.post(allUpdater);
												alertDialog.dismiss();

											} else if (arg2 == 2) {

												Intent intent = new Intent();
												intent.putExtra("dataMap",(Serializable)mMap);
												intent.setClass(getActivity(),EditBillActivity.class);
												startActivityForResult(intent,9);
												alertDialog.dismiss();

											} else if (arg2 == 3) {

												judgementDialog(flag,BK_Bill_Id,mMap);
//												deleteThisBill(flag,BK_Bill_Id,mMap);
//												// handler.post(allUpdater);
//												mUpdater();
												alertDialog.dismiss();

											}

										} else if (checkListCount == 5) {

											if (arg2 == 0) {
												
//												 Intent service=new Intent(getActivity(), BillNotificationService.class);   //�ж�����
//												 getActivity().startService(service);  

												if (flag == 0 || flag ==1) {

													
													long key = billPay(remain ,0,todayMill,BK_Bill_Id,new String(),"Quick pay");
//													mUpdater();
													 handler = new Handler();
													 handler.post(allUpdater);
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

													long Okey = insertObject( 0 , obillamount , obk_billAmountUnknown,obk_billAutoPay ,obk_billDuedate,obk_billDuedateNew,  //���������¼�
															obk_billEndDate, obk_billisReminder, obk_billisRepeat, obk_billisVariaable,obk_billReminderDate,obk_billReminderTime ,obk_billRepeatNumber,
															obk_billRepeatType,obillObjecthasBill,billhasAccount);

													billPayO(remain,0 ,todayMill , Okey,new String(),"Quick pay") ;
//													mUpdater();
													 handler = new Handler();
													 handler.post(allUpdater);
												} else if (flag == 3) {

													long key = billPayO(remain ,0,todayMill,BK_Bill_Id,new String(),"Quick pay");
//													mUpdater();
													 handler = new Handler();
													 handler.post(allUpdater);
												}
												alertDialog.dismiss();

											} else if (arg2 == 1) {

												addPaymentDialog(remain, BK_Bill_Id,flag ,mMap);
//												mUpdater();
												 handler = new Handler();
												 handler.post(allUpdater);

											} else if (arg2 == 2) {
												
//												 Intent service=new Intent(getActivity(), BillNotificationService.class);   //�ж�����
//												 getActivity().startService(service);  

												if (flag == 0 || flag ==1) {

													long key = billPay(0 ,-1,todayMill,BK_Bill_Id,new String(),"Mark pay");
//													mUpdater();
													 handler = new Handler();
													 handler.post(allUpdater);
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

													long Okey = insertObject( 0 , obillamount , obk_billAmountUnknown,obk_billAutoPay ,obk_billDuedate,obk_billDuedateNew,  //���������¼�
															obk_billEndDate, obk_billisReminder, obk_billisRepeat, obk_billisVariaable,obk_billReminderDate,obk_billReminderTime ,obk_billRepeatNumber,
															obk_billRepeatType,obillObjecthasBill,billhasAccount);

													billPayO(0,-1 ,todayMill ,Okey,new String(),"Mark pay") ;
//													mUpdater();
													 handler = new Handler();
													 handler.post(allUpdater);
												} else if (flag == 3) {

													long key = billPayO(0 ,-1,todayMill,BK_Bill_Id,new String(),"Mark pay");
//													mUpdater();
													 handler = new Handler();
													 handler.post(allUpdater);
												}
												alertDialog.dismiss();

											} else if (arg2 == 3) {

												Intent intent = new Intent();
												intent.putExtra("dataMap",(Serializable)mMap);
												intent.setClass(getActivity(),EditBillActivity.class);
												startActivityForResult(intent,9);
												alertDialog.dismiss();

											} else if (arg2 == 4) {

												judgementDialog(flag,BK_Bill_Id,mMap);
//												deleteThisBill(flag,BK_Bill_Id,mMap);
//												// handler.post(allUpdater);
//												mUpdater();
												alertDialog.dismiss();

											}

										} else {

										}

									}
								});

						DiaListViewAdapter.listCount(size);
						diaListView.setAdapter(DiaListViewAdapter);

						AlertDialog.Builder builder = new AlertDialog.Builder(
								getActivity());
						builder.setView(dialogview);
						alertDialog = builder.create();
						alertDialog.show();

						return true;
					}

				});

		
		long e = System.currentTimeMillis();

		Log.v("mcheck", "����ʱ��"+(e-s));

		return view;

	}

	/*
	 * 
	 */
	private void updateDisplay() {
		// TODO Auto-generated method stub
		duedateButton.setText(new StringBuilder()
				// Month is 0 based so add 1
				.append(mMonth + 1).append("-").append(mDay).append("-")
				.append(mYear));
		String untilDateString = (new StringBuilder().append(mMonth + 1)
				.append("-").append(mDay).append("-").append(mYear)).toString();
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(untilDateString));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		payDateLong = c.getTimeInMillis();
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
		}
	};

	public void addPaymentDialog(double remain, final int bill_id ,final int flag ,final Map<String, Object> mMap) {

		// TODO Auto-generated method stub
		View view = flater.inflate(R.layout.dialog_payment, null);

		amounTextView = (EditText)view.findViewById(R.id.payAmount);
		duedateButton =(Button)view.findViewById(R.id.dueDate);
		confTextView = (EditText)view.findViewById(R.id.ConfNumber);
		memoTextView = (EditText)view.findViewById(R.id.memo);

		java.text.DecimalFormat  df  = new java.text.DecimalFormat("0.00");

		amounTextView.setText(df.format(remain));
		amounTextView.addTextChangedListener(new TextWatcher() { // �����Զ�������λС��
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

				if (isChanged) {// ----->����ַ�δ�ı��򷵻�    
					return;    
				}    
				String str = s.toString();    

				isChanged = true;    
				String cuttedStr = str;    
				/* ɾ���ַ��е�dot */    
				for (int i = str.length() - 1; i >= 0; i--) {    
					char c = str.charAt(i);    
					if ('.' == c) {    
						cuttedStr = str.substring(0, i) + str.substring(i + 1);    
						break;    
					}    
				}    
				/* ɾ��ǰ������0 */    
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
				/* ����3λ��0 */    
				if (cuttedStr.length() < 3) {    
					cuttedStr = "0" + cuttedStr;    
				}    
				/* ����dot������ʾС������λ */    
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
				DatePickerDialog DPD = new DatePickerDialog( // �ı�theme
						new ContextThemeWrapper(getActivity(),
								android.R.style.Theme_Holo_Light),
								mDateSetListener, mYear, mMonth, mDay);
				DPD.setTitle("Due Date");
				DPD.show();
			}
		});

		AlertDialog.Builder addPaymentDialog = new AlertDialog.Builder(getActivity());
		addPaymentDialog.setView(view);
		addPaymentDialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				double pay = Double.parseDouble(amounTextView.getText().toString()); //ע��.221452����ֵ�Ƿ�Ϸ�

//				 Intent service=new Intent(getActivity(), BillNotificationService.class);   //�ж�����
//				 getActivity().startService(service);  
				 
				if (pay == 0) {
					new AlertDialog.Builder(getActivity())
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
//						mUpdater();
						 handler = new Handler();
						 handler.post(allUpdater);
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

						long Okey = insertObject( 0 , obillamount , obk_billAmountUnknown,obk_billAutoPay ,obk_billDuedate,obk_billDuedateNew,  //���������¼�
								obk_billEndDate, obk_billisReminder, obk_billisRepeat, obk_billisVariaable,obk_billReminderDate,obk_billReminderTime,obk_billRepeatNumber,
								obk_billRepeatType,obillObjecthasBill,billhasAccount);

						billPayO(pay,1 ,payDateLong ,Okey,ConfNumber,memo) ;
//						mUpdater();
						 handler = new Handler();
						 handler.post(allUpdater);
					} else if (flag == 3) {

						long key = billPay(pay ,1,payDateLong,bill_id,ConfNumber,memo);
//						mUpdater();
						 handler = new Handler();
						 handler.post(allUpdater);
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

	public Runnable allUpdater = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			long s = System.currentTimeMillis();
			
			setData();
			long e1 = System.currentTimeMillis();
			Log.v("mcheck", "�����������ʱ��"+(e1-s));
			
			long s1 = System.currentTimeMillis();
			setAdapter();
			
			long e = System.currentTimeMillis();

			Log.v("mcheck", "����view����ʱ��"+(e-s1));
			Log.v("mcheck", "�߳�����ʱ��"+(e-s));
		}
	};

	public void mUpdater() {
		setData();
		setAdapter();
	}

	public void setAdapter() {

		long s1 = System.currentTimeMillis();
		
		groupDataList.clear();
		childrenAllDataList.clear();
		
		Map<String, Object> map;
		if (overDuedata.size() > 0) { // ��ĳһ���dataΪ��ʱ����ʹ��Ӧ��view��ʧ
			 map = new HashMap<String, Object>();
			 map.put("dueString", "PAST DUE");
			 map.put("dueCount", overDuedata.size());
			 groupDataList.add(map);
			 childrenAllDataList.add(overDuedata);
		}

		if (overDue7data.size() > 0) {
			
			 map = new HashMap<String, Object>();
			 map.put("dueString", "DUE WITHIN 7 DAYS");
			 map.put("dueCount", overDue7data.size());
			 groupDataList.add(map);
			 childrenAllDataList.add(overDue7data);
		
		} 

		if (overDue30data.size() > 0) {
			
			 map = new HashMap<String, Object>();
			 map.put("dueString", "DUE WITHIN 30 DAYS");
			 map.put("dueCount", overDue30data.size());
			 groupDataList.add(map);
			 childrenAllDataList.add(overDue30data);
			
		}
		
		mExpandableListviewAdapter.setAdapterData(groupDataList, childrenAllDataList);
		mExpandableListviewAdapter.notifyDataSetChanged();
		
		int groupCount = groupDataList.size(); //����ExpandableListviewĬ��ȫ��չ��
		Log.v("mmes", "groupCount չ����С"+groupCount);
		
        for (int i=0; i<groupCount; i++) {
        	mExpandableListView.expandGroup(i);
            };
        mExpandableListView.setOnGroupClickListener(new OnGroupClickListener() { //����group�¼��������������return true
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				// TODO Auto-generated method stub
				return true;
			}
		});
        mExpandableListView.setCacheColorHint(0); 
        
		
		if (list.size() == 0) { // ����data���Ϊ�յ�ʱ��ʹ�ײ�view��ʧ
			relativeLayoutBac.setVisibility(View.VISIBLE);
		} else {
			relativeLayoutBac.setVisibility(View.INVISIBLE);
		}


	}

	public void setData() { // ɸѡ���listview�����

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		String nowTime = formatter.format(date);
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(nowTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nowMillis = c.getTimeInMillis(); // ��ȡ���������ն�Ӧ�ĺ�����
		long thirtyDay = 30*24*3600*1000L;
//		getAllData();
		getData(0,(getNowMillis()+thirtyDay));
		mdata = list;

		overDuedata = new ArrayList<Map<String, Object>>();
		overDue7data = new ArrayList<Map<String, Object>>();
		overDue30data = new ArrayList<Map<String, Object>>();

		for (Map<String, Object> mMap : mdata) { // �������ݿ��ȡ��Դ��ݣ�����ɸѡ3��listview��Դ���

//			String zbillZ_PK = (String) mMap.get("zbillZ_PK");
//			String accountname = (String) mMap.get("accountname");
//			String isautopay = (String) mMap.get("isautopay");
//			String isrepeat = (String) mMap.get("isrepeat");
//			String isknownamount = (String) mMap.get("isknownamount");
//			double billamount = (Double) mMap.get("billamount");
//			String billduedate = (String) mMap.get("billduedate");
//			int listSize = (Integer) mMap.get("listSize");
//			double remain = (Double) mMap.get("remain");
			
			long bk_billDuedate = (Long)mMap.get("nbk_billDuedate"); 
			int daysBetween = getDaysBetween(nowMillis,bk_billDuedate);
			String dueDateString = changToDate(bk_billDuedate);
			mMap.put("mDate", dueDateString);
			
			if (daysBetween < 0) { // ��ȡ����֮ǰ���ڵ�bill�����overDuedata
//				temMap = new HashMap<String, Object>();
//
//				temMap.put("zbillZ_PK", zbillZ_PK);
//				temMap.put("checkDot", 0);
//				temMap.put("bName", accountname);
//				temMap.put("mDate", dueDateString);
//
//				if (Integer.parseInt(isknownamount) == 1) {
//					temMap.put("amount", "N/A");
//				} else if (Integer.parseInt(isknownamount) == -1
//						&& billamount >= 0) {
//					temMap.put("amount", billamount);
//				}
//				temMap.put("checkAR", 3);
//				if (Integer.parseInt(isautopay) == 1
//						&& Integer.parseInt(isrepeat) != 1) {
//					temMap.put("checkAR", 0);
//				}
//				if (Integer.parseInt(isrepeat) == 1
//						&& Integer.parseInt(isautopay) != 1) {
//					temMap.put("ChabliseckAR", 1);
//				}
//				if (Integer.parseInt(isautopay) == 1
//						&& Integer.parseInt(isrepeat) == 1) {
//					temMap.put("checkAR", 2);
//				}
//				temMap.put("mcheck", isknownamount);
//				temMap.put("mChecked", 0); // ��item�Ƿ�ѡ��
//				temMap.put("mPosition", -1);// ��ѡ��item��positon
//				temMap.put("listSize", listSize);
//				temMap.put("remain", remain);

				overDuedata.add(mMap);
				Collections.sort(overDuedata, new Common.MapComparator());

			} else if (0 <= daysBetween && daysBetween < 7) { // ��ȡ1��7����ڵ�bill������overDue7data
//				temMap = new HashMap<String, Object>();
//
//				temMap.put("zbillZ_PK", zbillZ_PK);
//				temMap.put("checkDot", 7);
//				temMap.put("bName", accountname);
//				temMap.put("mDate", dueDateString);
//
//				if (Integer.parseInt(isknownamount) == 1) {
//					temMap.put("amount", "N/A");
//				} else if (Integer.parseInt(isknownamount) == -1
//						&& billamount >= 0) {
//					temMap.put("amount", billamount);
//				}
//				temMap.put("checkAR", 3);
//				if (Integer.parseInt(isautopay) == 1
//						&& Integer.parseInt(isrepeat) != 1) {
//					temMap.put("checkAR", 0);
//				}
//				if (Integer.parseInt(isrepeat) == 1
//						&& Integer.parseInt(isautopay) != 1) {
//					temMap.put("checkAR", 1);
//				}
//				if (Integer.parseInt(isautopay) == 1
//						&& Integer.parseInt(isrepeat) == 1) {
//					temMap.put("checkAR", 2);
//				}
//				temMap.put("mcheck", isknownamount);
//
//				temMap.put("mChecked", 0); // ��item�Ƿ�ѡ��
//				temMap.put("mPosition", -1);// ��ѡ��item��positon
//				temMap.put("listSize", listSize);
//				temMap.put("remain", remain);

				overDue7data.add(mMap);
				Collections.sort(overDue7data, new Common.MapComparator());
			} else if (7 <= daysBetween && daysBetween < 30) { // ��ȡ7��30����ڵ�bill������overDue30data
//				temMap = new HashMap<String, Object>();
//
//				temMap.put("zbillZ_PK", zbillZ_PK);
//				temMap.put("checkDot", 30);
//				temMap.put("bName", accountname);
//				temMap.put("mDate", dueDateString);
//
//				if (Integer.parseInt(isknownamount) == 1) {
//					temMap.put("amount", "N/A");
//				} else if (Integer.parseInt(isknownamount) == -1
//						&& billamount >= 0) {
//					temMap.put("amount", billamount);
//				}
//				temMap.put("checkAR", 3);
//				if (Integer.parseInt(isautopay) == 1
//						&& Integer.parseInt(isrepeat) != 1) {
//					temMap.put("checkAR", 0);
//				}
//				if (Integer.parseInt(isrepeat) == 1
//						&& Integer.parseInt(isautopay) != 1) {
//					temMap.put("checkAR", 1);
//				}
//				if (Integer.parseInt(isautopay) == 1
//						&& Integer.parseInt(isrepeat) == 1) {
//					temMap.put("checkAR", 2);
//				}
//				temMap.put("mcheck", isknownamount);
//
//				temMap.put("mChecked", 0); // ��item�Ƿ�ѡ��
//				temMap.put("mPosition", -1);// ��ѡ��item��positon
//				temMap.put("listSize", listSize);
//				temMap.put("remain", remain);

				overDue30data.add(mMap);
				Collections.sort(overDue30data, new Common.MapComparator());
			}
		}
	}
	
	public List<Map<String, Object>> keyDate(List<Map<String, Object>> bData) { // ��listmap��ɸѡ������ѡ�б�����Щ��ѡ�е���Լ���Ӧ��ֵ
		List<Map<String, Object>> mlist = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;

		for (Map<String, Object> imap : bData) {

			mMap = new HashMap<String, Object>();
			mMap.put("bZPKey", imap.get("zbillZ_PK"));
			mMap.put("mChecked", 0); // ��item�Ƿ�ѡ��
			mMap.put("mPosition", -1);// ��ѡ��item��positon
			mlist.add(mMap);
		}
		return mlist;
	}

	
	
	public void setListViewHeightBasedOnChildren(ListView listView) { // ����listview��scrollview�еĸ߶�
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = listView.getPaddingTop()
				+ listView.getPaddingBottom();
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			if (listItem instanceof ViewGroup)
				listItem.setLayoutParams(new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	public void deleteCategory(String key) { // ɾ���zbill��Ӧ�ļ�¼

		bksql = new BillKeeperSql(getActivity());
		SQLiteDatabase db = bksql.getReadableDatabase();
		String sql = "delete from zbill where Z_PK=" + key;
		db.execSQL("PRAGMA foreign_keys = ON ");
		db.execSQL(sql);
		db.close();
	}

	public String changToDate(long millis) { // ��������ת���������
		Date date = new Date(millis);
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
		return sdf.format(date);
	}

	public int getDaysBetween(long beginDate, long endDate) { // ������������ʱ��
		long between_days = (endDate - beginDate) / (1000 * 3600 * 24);
		return Integer.parseInt(String.valueOf(between_days));
	}

	public long todayDate() { // ��ȡ���������ն�Ӧ�ĺ������ȥ����

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		String nowTime = formatter.format(date);
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(nowTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long nowMillis = c.getTimeInMillis();
		return nowMillis;
	}

	public List<Map<String, Object>> getData(long firstDayOfMonth,
			long lastDayOfMonth) {

		list.clear();
		bksql = new BillKeeperSql(getActivity());
		SQLiteDatabase db = bksql.getReadableDatabase();
		Map<String, Object> map;
		String sql = "select BK_Bill.* ,BK_Account.bk_accountName ,BK_Account.accounthasCategory  ,BK_Category.bk_categoryIconName ,BK_Category.bk_categoryName from BK_Bill,BK_Account,BK_Category where BK_Bill.billhasAccount = BK_Account._id and BK_Category._id =BK_Account.accounthasCategory and BK_Bill.bk_billisRepeat != 1 and BK_Bill.bk_billDuedate >="
				+ firstDayOfMonth
				+ " and BK_Bill.bk_billDuedate <= "
				+ lastDayOfMonth;

		Cursor cursorEA = db.rawQuery(sql, null);
		while (cursorEA.moveToNext()) {
			map = new HashMap<String, Object>();

			int BK_Bill_Id = cursorEA.getInt(0);
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
			String bk_categoryName = cursorEA.getString(28);

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
			map.put("indexflag", 0); // �����¼��ı�־��0�������ı�־��1��2���ظ����¼���3���ظ������¼�
			map.put("payState", -2);

			list.add(map);

		}
		Log.v("mdb", "list�����¼���С"+list.size());
		cursorEA.close();
		db.close();
		list.addAll(RecurringEvent.recurringData(getActivity(),firstDayOfMonth, lastDayOfMonth)); // ���ѭ��ʱ���¼�
		judgePayment(list);
		list.removeAll(judegePaidMap(list));
		Collections.sort(list, new Common.MapComparator()); // ��list��������
		
		Log.v("mdb", "�����list�����¼���С"+list.size());
		
		return list;

	}

	public List<Map<String, Object>> getAllData()// ��ȡ��䵽listView��Դ���
	{
		list.clear();
		bksql = new BillKeeperSql(getActivity());
		SQLiteDatabase db = bksql.getReadableDatabase();
		Map<String, Object> map;

		Cursor cursorEA = db
				.rawQuery(
						"select BK_Bill._id, BK_Account.bk_accountName, BK_Bill.bk_billAutoPay, BK_Bill.bk_billisRepeat, BK_Bill.bk_billAmountUnknown, BK_Bill.bk_billAmount, BK_Bill.bk_billDuedate,BK_Bill.bk_billRepeatType ,BK_Bill.bk_billRepeatNumber from BK_Bill,BK_Account where BK_Bill.billhasAccount = BK_Account._id order by BK_Bill.bk_billDuedate ASC ",
						null);
		while (cursorEA.moveToNext()) {

			String zbillZ_PK = cursorEA.getString(0);
			String accountname = cursorEA.getString(1);
			String isautopay = cursorEA.getString(2);
			String isrepeat = cursorEA.getString(3);
			String isknownamount = cursorEA.getString(4);
			double billamount = cursorEA.getDouble(5);
			String billduedate = cursorEA.getString(6);

			int bk_billRepeatType = cursorEA.getInt(7);
			int bk_billRepeatNumber = cursorEA.getInt(8);

			int bill_id = Integer.parseInt(zbillZ_PK);
			long billDuedate = (Long.parseLong(billduedate));
			double billAmount = billamount;
			int isRepeat = (Integer.parseInt(isrepeat));
			int isAuto = (Integer.parseInt(isautopay));
			int unknown = (Integer.parseInt(isknownamount));

			/*
		         * 
		         */
			int checkPayMode = 0;// �жϸ�pay�Ƿ��й�quickpay��mark,mark =1 quick =2;
			totalPay = 0;
			double remain = 0.0;

			Map<String, Object> pMap; // Ƕ�ײ�ѯpayment
			double zero = 0.00f;
			BigDecimal b1 = new BigDecimal(Double.toString(zero)); // ������㾫��

			String sql1 = "select _id ,bk_payAmount ,bk_payDate ,bk_payMode from BK_Payment where BK_Payment.paymenthasBill = "
					+ zbillZ_PK + " order by bk_payDate DESC ";
			Cursor cursor = db.rawQuery(sql1, null);
			curSize = cursor.getCount();

			while (cursor.moveToNext()) {

				pMap = new HashMap<String, Object>();
				int pay_id = cursor.getInt(0);
				double bk_payAmount = cursor.getDouble(1);
				long bk_payDate = cursor.getLong(2);
				int bk_payMode = cursor.getInt(3);

				BigDecimal b2 = new BigDecimal(Double.toString(bk_payAmount));
				b1 = b1.add(b2);

				pMap.put("pay_id", pay_id);
				pMap.put("bk_payAmount", bk_payAmount);
				pMap.put("bk_payDate", getMilltoDate(bk_payDate));
				pMap.put("bk_payMode", bk_payMode);

				if (bk_payMode == -1) { // mark
					checkPayMode = 1;
				} 
				totalPay = b1.doubleValue(); // �ܹ�֧����
				dataList.add(pMap);
			}
			cursor.close();

			/*
		   	 * 
		   	 */

			double totalPayAmount = 0;

			int listSize = 4; // �����¼���Ӧ�ó��ּ�����2��ʾ2����4��5

			if (unknown == 1) {
				totalPayAmount = totalPay;

				remain = 0.00;
				if (curSize > 0 || totalPayAmount > 0) {
					listSize = 2;
				}
			} else {
				totalPayAmount = totalPay;

				BigDecimal mb1 = new BigDecimal(
						Double.toString((Double) billAmount)); // ������㾫��
				BigDecimal mb2 = new BigDecimal(Double.toString(totalPay)); // ������㾫��
				remain = (mb1.subtract(mb2)).doubleValue();
				if (remain < 0) {
					remain = 0.00;
				}
				if (totalPay >= billAmount || checkPayMode == 1
						|| checkPayMode == 2) {
					listSize = 2;
				} else {
					if (curSize > 0) {
						listSize = 5;
					} else {
						listSize = 4;
					}
				}
			}

			Date date = new Date();
			Map<String, Object> xMap;
			if ((isAuto == 1 && billDuedate < todayDate() && totalPay < billAmount)
					|| (isAuto == 1 && billDuedate < todayDate()
							&& unknown == 1 && curSize == 0)) { // auto��bill����duedateС�ڵ����ʱ�򣬻���unkonwn
																// �Զ�pay
				long key;
				if (unknown == 1) {
					key = billPay(0.00, 2, billDuedate, bill_id ,new String(),"Auto pay");
					totalPayAmount = 0.00;
				} else {
					key = billPay(remain, 2, billDuedate, bill_id ,new String(),"Auto pay");
					totalPayAmount = billAmount;
				}

				xMap = new HashMap<String, Object>();
				xMap.put("pay_id", key);
				xMap.put("bk_payAmount", remain);
				xMap.put("bk_payDate", getMilltoDate(billDuedate));
				xMap.put("bk_payMode", 2);

				dataList.add(xMap);
				listSize = 2;
				remain = 0.00;

			}

			/*
			   * 
			   */
			if (listSize == 4 || listSize == 5) {
				map = new HashMap<String, Object>();
				map.put("zbillZ_PK", zbillZ_PK);
				map.put("accountname", accountname);
				map.put("isautopay", isautopay);
				map.put("isrepeat", isrepeat);
				map.put("isknownamount", isknownamount);
				map.put("billamount", billamount);
				map.put("billduedate", billduedate);
				map.put("remain", remain);
				map.put("listSize", listSize);
				list.add(map);
			}

		}
		cursorEA.close();
		db.close();
		return list;

	}

	public long billDelete(int rowid) {

		bksql = new BillKeeperSql(getActivity());
		SQLiteDatabase db = bksql.getReadableDatabase();
		String id = rowid + "";
		db.execSQL("PRAGMA foreign_keys = ON ");
		long row = db.delete("BK_Bill", "_id = ?", new String[] { id });
		db.close();

		return row;
	}

	public long billPay(double payAmount, int payMode, long payDate,
			int paymenthasBill) {

		bksql = new BillKeeperSql(getActivity());
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

	public String getMilltoDate(long milliSeconds) {// ������ת���ɹ̶���ʽ��������
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}
	

	private void judgePayment(List<Map<String, Object>> dataList) { // ʹ�ø÷����ж�pay��״̬,�����޳�paid״̬��bill
		bksql = new BillKeeperSql(getActivity());
		SQLiteDatabase db = bksql.getReadableDatabase();
		long nowMillis = getNowMillis();
		Double mZero = 0.00;
		
		for (Map<String, Object> bMap : dataList) {

			int flag = (Integer) bMap.get("indexflag");
			int id = (Integer) bMap.get("BK_Bill_Id");
			double billamount = (Double) bMap.get("nbillamount");
			long billduedate = (Long) bMap.get("nbk_billDuedate");
			int billAutoPay = (Integer) bMap.get("nbk_billAutoPay");
			int nbk_billAmountUnknown = (Integer) bMap.get("nbk_billAmountUnknown");
//			Double mZero = 0.00;

			bMap.put("remain", mZero); // ʣ�µ����
			if (flag == 0 || flag == 1) {

				String sql = "select BK_Payment._id, BK_Payment.bk_payAmount, BK_Payment.bk_payMode ,BK_Payment.paymenthasBill from BK_Payment where BK_Payment.paymenthasBill = "
						+ id;

				List<Map<String, Object>> pDataList = new ArrayList<Map<String, Object>>();
				double zero = 0.00f;
				BigDecimal b1 = new BigDecimal(Double.toString(zero)); // ������㾫��
				int checkPayMode = 0;// �жϸ�pay�Ƿ��й�quickpay��mark,�й�һ�α�ʾ��bill�Ѿ�pay�꣬mark =1 quick =2;
				int curSize;
				Cursor cursor1 = db.rawQuery(sql, null);
				curSize = cursor1.getCount();
				while (cursor1.moveToNext()) {

					int paymentZ_PK = cursor1.getInt(0);
					double paymentAmount = cursor1.getDouble(1);
					int paymentMode = cursor1.getInt(2);
					int paymenthasBill = cursor1.getInt(3);
					Log.v("mtest", "paymentAmount��ʷ" + paymentAmount);

					BigDecimal b2 = new BigDecimal(
							Double.toString(paymentAmount));
					b1 = b1.add(b2);
					if (paymentMode == -1) { // mark
						checkPayMode = 1;
					}

				}
				cursor1.close();

				double totalPayAmount = b1.doubleValue(); // �ܹ�֧���ܶ�
				double remain = 0.0;// ʣ�µ�

				if (nbk_billAmountUnknown == 1) {

					if (curSize > 0) {
						bMap.put("payState", 1);
						bMap.put("remain", mZero);
					} else {

						if (billduedate < nowMillis) {
							bMap.put("payState", -1);
						} else {
							bMap.put("payState", 0);
						}
						bMap.put("remain", mZero);
					}

				} else if (checkPayMode == 1) {

					bMap.put("payState", 1);
					bMap.put("remain", mZero);

				} else {

					BigDecimal mb1 = new BigDecimal(Double.toString(billamount)); // ������㾫��
					BigDecimal mb2 = new BigDecimal(
							Double.toString(totalPayAmount)); // ������㾫��
					remain = (mb1.subtract(mb2)).doubleValue();
//					Log.v("mtest", "billamount" + billamount);
//					Log.v("mtest", "totalPayAmount����" + totalPayAmount);
//					Log.v("mtest", "remain����" + remain);

					if (remain <= 0) {
						bMap.put("payState", 1);
						bMap.put("remain", mZero);
					} else {

						if (billduedate < nowMillis) {
							bMap.put("payState", -1);
						} else {
							bMap.put("payState", 0);
						}
						bMap.put("remain", remain);

					}
				}
				int mpayState = (Integer) bMap.get("payState");

				if (mpayState == -2) {

					if (billduedate < nowMillis) {
						bMap.put("payState", -1);
					} else {
						bMap.put("payState", 0);
					}
					bMap.put("remain", billamount);

				}

				if ((billAutoPay == 1 && billduedate < (nowMillis + 86399) && remain > 0)
						|| (billAutoPay == 1
								&& billduedate < (nowMillis + 86399)
								&& nbk_billAmountUnknown == 1 && curSize == 0)) { // auto��bill����duedateС�ڵ����ʱ�򣬻���unkonwn
																					// �Զ�pay
					long key;
//					Log.v("mtest", "remainʣ��" + remain);
					if (nbk_billAmountUnknown == 1) {
						key = billPay(0.00, 2, billduedate, id,new String(),"Auto pay");
					} else {
						key = billPay(remain, 2, billduedate, id,new String(),"Auto pay");
					}
					bMap.put("payState", 1);
					bMap.put("remain", mZero);
				}
				 //���ͳһ����pay��״̬����Ϊ3�֣�û��paid�������Ѿ�ֻ��һ��Ϊ5��һ�ζ�û�д�������Ϊ4��paidΪ1
                int checkpaySize = (Integer) bMap.get("payState");
                
                if (checkpaySize == -1 || checkpaySize == 0) {
                	
                	if (curSize > 0) {
                		bMap.put("payState", 5);
					} else {
						bMap.put("payState", 4);
					}
				}else if (checkpaySize == -2) {
                	bMap.put("payState", 4);
				}
                
			} else if (flag == 2) {

				if (billduedate < nowMillis) {
					bMap.put("payState", -1);
					bMap.put("remain", billamount);
				} else {
					bMap.put("payState", 0);
					bMap.put("remain", billamount);
				}

				if (billAutoPay == 1 && billduedate < (nowMillis + 86399)) { // �ظ������¼����ڴ���

					bMap.put("payState", 1);

					int obillObjecthasBill = (Integer) bMap.get("BK_Bill_Id");
					double obillamount = (Double) bMap.get("nbillamount");
					int obk_billAmountUnknown = (Integer) bMap
							.get("nbk_billAmountUnknown");
					int obk_billAutoPay = (Integer) bMap.get("nbk_billAutoPay");
					long obk_billDuedate = (Long) bMap.get("nbk_billDuedate");
					long obk_billDuedateNew = 0;
					long obk_billEndDate = (Long) bMap.get("nbk_billEndDate");
					int obk_billisReminder = (Integer) bMap
							.get("nbk_billisReminder");
					int obk_billisRepeat = (Integer) bMap
							.get("nbk_billisRepeat");
					int obk_billisVariaable = (Integer) bMap
							.get("bk_billisVariaable");
					long obk_billReminderDate = (Long) bMap
							.get("nbk_billReminderDate");
					long bk_billReminderTime = (Long) bMap
							.get("bk_billReminderTime");
					int obk_billRepeatNumber = (Integer) bMap
							.get("nbk_billRepeatNumber");
					int obk_billRepeatType = (Integer) bMap
							.get("nbk_billRepeatType");
					int billhasAccount = (Integer) bMap.get("billhasAccount");

					long Okey = insertObject(
							0,
							obillamount,
							obk_billAmountUnknown,
							obk_billAutoPay,
							obk_billDuedate,
							obk_billDuedateNew, // ���������¼�
							obk_billEndDate, obk_billisReminder,
							obk_billisRepeat, obk_billisVariaable,
							obk_billReminderDate, bk_billReminderTime,
							obk_billRepeatNumber, obk_billRepeatType,
							obillObjecthasBill, billhasAccount);
					if (obk_billAmountUnknown == 1) {
						billPayO(0.0, 2, obk_billDuedate, Okey,new String(),"Auto pay");
					} else {
						billPayO(obillamount, 2, obk_billDuedate, Okey,new String(),"Auto pay");
					}
					int key = (int) Okey;

					bMap.put("remain", mZero);
					bMap.put("indexflag", 3);
					bMap.put("BK_Bill_Id", key);
				}
				
				   int checkpaySize = (Integer) bMap.get("payState");
	                if (checkpaySize == -1 || checkpaySize == 0) {
	                	
	                	if (curSize > 0) {
	                		bMap.put("payState", 5);
						} else {
							bMap.put("payState", 4);
						}
					}else if (checkpaySize == -2) {
	                	bMap.put("payState", 4);
					}
				   

			} else if (flag == 3) {

				String sql = "select BK_Payment._id, BK_Payment.bk_payAmount, BK_Payment.bk_payMode ,BK_Payment.paymenthasBill from BK_Payment ,BK_BillObject where BK_Payment.paymenthasBillO = "
						+ id;

				List<Map<String, Object>> pDataList = new ArrayList<Map<String, Object>>();
				Map<String, Object> map1;

				double zero = 0.00f;
				BigDecimal b1 = new BigDecimal(Double.toString(zero)); // ������㾫��
				int checkPayMode = 0;// �жϸ�pay�Ƿ��й�quickpay��mark,�й�һ�α�ʾ��bill�Ѿ�pay�꣬mark
										// =1 quick =2;
				int curSize;
				Cursor cursor1 = db.rawQuery(sql, null);
				curSize = cursor1.getCount();
				while (cursor1.moveToNext()) {

					String paymentZ_PK = cursor1.getString(0);
					double paymentAmount = cursor1.getDouble(1);
					int paymentMode = cursor1.getInt(2);
					int paymenthasBill = cursor1.getInt(3);

					BigDecimal b2 = new BigDecimal(
							Double.toString(paymentAmount));
					b1 = b1.add(b2);
					if (paymentMode == -1) { // mark
						checkPayMode = 1;
					}

				}
				cursor1.close();

				double totalPayAmount = b1.doubleValue(); // �ܹ�֧���ܶ�
				double remain = 0.0;// ʣ�µ�

				if (nbk_billAmountUnknown == 1) {

					if (curSize > 0) {
						bMap.put("payState", 1);
						bMap.put("remain", mZero);
					} else {
						if (billduedate < nowMillis) {
							bMap.put("payState", -1);
						} else {
							bMap.put("payState", 0);
						}
						bMap.put("remain", mZero);
					}

				} else if (checkPayMode == 1) {

					bMap.put("payState", 1);
					bMap.put("remain", mZero);

				} else {

					BigDecimal mb1 = new BigDecimal(Double.toString(billamount)); // ������㾫��
					BigDecimal mb2 = new BigDecimal(
							Double.toString(totalPayAmount)); // ������㾫��
					remain = (mb1.subtract(mb2)).doubleValue();

					if (remain <= 0) {
						bMap.put("payState", 1);
						bMap.put("remain", mZero);
					} else {

						if (billduedate < nowMillis) {
							bMap.put("payState", -1);
						} else {
							bMap.put("payState", 0);
						}
						bMap.put("remain", remain);

					}
				}
				int mpayState = (Integer) bMap.get("payState");

				if (mpayState == -2) {

					if (billduedate < nowMillis) {
						bMap.put("payState", -1);
					} else {
						bMap.put("payState", 0);
					}
					bMap.put("remain", billamount);

				}

				if ((billAutoPay == 1 && billduedate < (nowMillis + 86399) && remain > 0)
						|| (billAutoPay == 1
								&& billduedate < (nowMillis + 86399)
								&& nbk_billAmountUnknown == 1 && curSize == 0)) { // auto��bill����duedateС�ڵ����ʱ�򣬻���unkonwn
																					// �Զ�pay
					long key;
					if (nbk_billAmountUnknown == 1) {
						key = billPayO(0.00, 2, billduedate, id,new String(),"Auto pay");
					} else {
						key = billPayO(remain, 2, billduedate, id,new String(),"Auto pay");
					}
					bMap.put("payState", 1);
					bMap.put("remain", mZero);
				}
				
				   int checkpaySize = (Integer) bMap.get("payState");
	               if (checkpaySize == -1 || checkpaySize == 0) {
	                	
	                	if (curSize > 0) {
	                		bMap.put("payState", 5);
						} else {
							bMap.put("payState", 4);
						}
					}else if (checkpaySize == -2) {
	                	bMap.put("payState", 4);
					}
			}

		}// ����ѭ������
		db.close();
	}
	
	public List<Map<String, Object>> judegePaidMap(List<Map<String, Object>> allList) { //�޳��Ѿ�paid���˵�
		List<Map<String, Object>> mPaidDataList = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> iMap:allList){
			int payState = (Integer)iMap.get("payState");
			if (payState == 1) {
				mPaidDataList.add(iMap);
			}
		}
		return mPaidDataList;
	}

	public long insertObject(int bk_billsDelete,
			double billamount,
			int nbk_billAmountUnknown,
			int bk_billAutoPay,
			long bk_billDuedate,
			long bk_billDuedateNew, // ���������¼�
			long bk_billEndDate, int bk_billisReminder, int bk_billisRepeat,
			int bk_billisVariaable, long bk_billReminderDate,
			long bk_billReminderTime, int bk_billRepeatNumber,
			int bk_billRepeatType, long billObjecthasBill, int billhasAccount) {

		bksql = new BillKeeperSql(getActivity());
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
	
	public long billPay(double payAmount,int payMode ,long payDate ,long paymenthasBill, String conf,String memo) {
		bksql = new BillKeeperSql(getActivity());
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
	
	public long billPay(double payAmount,int payMode ,long payDate ,long paymenthasBill,String memo) {
		bksql = new BillKeeperSql(getActivity());
		SQLiteDatabase db = bksql.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("bk_payAmount", payAmount);
		values.put("bk_payMode", payMode);
		values.put("bk_payDate", payDate);
		values.put("paymenthasBill", paymenthasBill);
		values.put("bk_payMemo", memo);

		db.execSQL("PRAGMA foreign_keys = ON ");
		long pkey = db.insert("BK_Payment", null, values);
		db.close();
		return pkey;
	}


	public long billPayO(double payAmount,int payMode ,long payDate ,long paymenthasBill ,String conf,String memo ) {

		bksql = new BillKeeperSql(getActivity());
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
	
	public long billPayO(double payAmount, int payMode, long payDate,
			long paymenthasBill) {

		bksql = new BillKeeperSql(getActivity());
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

	public long getNowMillis() { // �õ�����ĺ�����
		Date date1 = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		String nowTime = formatter.format(date1);
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(nowTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long nowMillis = c.getTimeInMillis(); // ��ȡ���������ն�Ӧ�ĺ������ȥʱ���룬�������

		return nowMillis;
	}
	
  public void deletePayment(int hasId){ //ɾ���pay״̬
		
		bksql = new BillKeeperSql(getActivity());
		SQLiteDatabase db = bksql.getReadableDatabase();
		String id = hasId+"";
		long row=db.delete("BK_Payment", "paymenthasBill = ?", new String[]{id});
		db.close();
	}
	
	public void deleteThisBill(int mFlag ,int theId, Map<String, Object> mMap) { //dialog���õķ�����ɾ����¼�

		if (mFlag == 0) {
			billDelete(theId);
		} else if(mFlag == 1){
			billParentDelete(theId,mMap);
		}else if(mFlag == 2){
			billVirtualThisDelete(theId,mMap);
		}else if (mFlag == 3) {
			billObjectDelete(theId);
		}

	}
	
	public long billObjectDelete(int rowid){

		bksql = new BillKeeperSql(getActivity());
		SQLiteDatabase db = bksql.getReadableDatabase();
		String id = rowid+"";
		String [] ids = {String.valueOf(rowid)}; //��ǰobject bill��id
		ContentValues values = new ContentValues();
		values.put("bk_billsDelete", 1);
		long row=db.update("BK_BillObject", values, "_id=?" ,ids); 
		db.close();

		return row;
	}
	
	public long billVirtualThisDelete(int rowid, Map<String, Object> mMap){  //ɾ��ǰ�������¼� ��ʵ��Ȼ������ɾ������
		long mbk_billDuedate = (Long)mMap.get("nbk_billDuedate");
		int mbillhasAccount = (Integer)mMap.get("billhasAccount");
		 
		bksql = new BillKeeperSql(getActivity());
		SQLiteDatabase db = bksql.getReadableDatabase();
		String id = rowid+"";
		String [] ids = {String.valueOf(rowid)}; //��ǰobject bill��id
		ContentValues values = new ContentValues();
		values.put("bk_billsDelete", 1);
		values.put("bk_billODueDate", mbk_billDuedate);
		values.put("billObjecthasBill",rowid);
		values.put("billObjecthasAccount",mbillhasAccount);
		long row=db.insert("BK_BillObject", null, values);
		db.close();

		return row;
	}
	
	public long billParentDelete(int rowid, Map<String, Object> mMap){  //��ǰ������ɾ��duedate����Ϊ��һ�η����ʱ�䣬��κ���һ��ʱ����ȣ����ʾ�����������ظ���ֱ��ɾ�� 

		long row = 0;
		long mbk_billDuedate = (Long)mMap.get("nbk_billDuedate");
		int mbk_billRepeatNumber = (Integer)mMap.get("nbk_billRepeatNumber"); 
		int mbk_billRepeatType = (Integer)mMap.get("nbk_billRepeatType"); 
		long mbk_billEndDate =  (Long)mMap.get("nbk_billEndDate");
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(mbk_billDuedate); //�����ظ��Ŀ�ʼ����

		if ( mbk_billRepeatType == 1 ) {

			calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)+mbk_billRepeatNumber);

		} else if (mbk_billRepeatType == 2) {

			calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)+mbk_billRepeatNumber*7);

		} else if (mbk_billRepeatType == 3) {

			calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)+mbk_billRepeatNumber);

		} else if (mbk_billRepeatType == 4) {

			calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR)+mbk_billRepeatNumber);

		} 

		long nextDuedate = calendar.getTimeInMillis();

		if (nextDuedate > mbk_billEndDate) {  ///�ж���Χ��ȷ��,��ʾ���ظ��¼�ֻ�и���һ��

			row =billDelete(rowid);

		} else {

			bksql = new BillKeeperSql(getActivity());
			SQLiteDatabase db = bksql.getReadableDatabase();
			String id = rowid+"";
			String [] ids = {String.valueOf(rowid)}; //��ǰobject bill��id
			ContentValues values = new ContentValues();
			values.put("bk_billDuedate", nextDuedate);
			row=db.update("BK_Bill", values, "_id=?" ,ids); 
			db.close();

			//				Intent intent = new Intent();
			//				intent.putExtra("pKey", row);
			//				intent.putExtra("flag", 1);
			//				setResult(21, intent);
			deletePayment(rowid); //ɾ��ǰpay��״̬
		}
		return row;
	}
	
	public int judgeTemPayDate(int b_id) { //�жϸ����¼���pay״̬�������pay���ܸ�ĵ�ǰ�����ж�bill���е�pay״̬

		Map<String, Object> bMap;
		bksql = new BillKeeperSql(getActivity()); 
		SQLiteDatabase db = bksql.getReadableDatabase();

		String pSql = "select * from BK_Payment where paymenthasBill = "+b_id; 
		Cursor cursor = db.rawQuery(pSql, null);
		int pCursorCounnt =cursor.getCount();
		cursor.close();
		db.close();
		if (pCursorCounnt > 0) {
			return 1;
		}else {
			return 0;
		}

	}
	
	public long judgePayDate(int b_id) { //�жϸ��ظ��¼�����һ��pay��bill�����ظ�bill����Сduedate,����0�����ʾ��bill���е��ظ��¼���û��pay״̬

		Map<String, Object> bMap;
		bksql = new BillKeeperSql(getActivity()); 
		SQLiteDatabase db = bksql.getReadableDatabase();
		String  sql = "select BK_BillObject._id ,BK_BillObject.bk_billODueDate from BK_BillObject where BK_BillObject.billObjecthasBill ="+b_id; //�Ȳ�������ظ��¼����������

		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Cursor cursorEA = db.rawQuery(sql, null);
		int cursorCounnt =cursorEA.getCount();
		while (cursorEA.moveToNext()) {
			bMap = new HashMap<String, Object>();
			int Object_id = cursorEA.getInt(0);
			long bk_billODueDate = cursorEA.getLong(1);

			bMap.put("Object_id", Object_id);
			bMap.put("bk_billODueDate", bk_billODueDate);
			dataList.add(bMap);
		}
		cursorEA.close();
		db.close();
		
		SQLiteDatabase dbp = bksql.getReadableDatabase();
		
		ArrayList<Long> OPayList = new ArrayList<Long>();
		if (cursorCounnt > 0) {

			long reData = 0;
			for (Map<String, Object> oMap:dataList) {
				int Object_id = (Integer) oMap.get("Object_id");
				long bk_billODueDate = (Long) oMap.get("bk_billODueDate");

				String pSql = "select * from BK_Payment where paymenthasBillO = "+Object_id;
				Cursor cursor = dbp.rawQuery(pSql, null);
				int pCursorCounnt =cursor.getCount();
				cursor.close();
				if (pCursorCounnt > 0) {
					OPayList.add(bk_billODueDate);
				} 
			}
			dbp.close();
			
			if (OPayList.size()>0) { //�ҳ���С������

				long max = OPayList.get(0);
				for (int i = 0; i < OPayList.size(); i++) {

					if (OPayList.get(i)>max) {
						max=OPayList.get(i);
					} 

				}
				reData = max;
			} else {
				reData = 0;
			}

			return reData;

		}else {
			return 0;
		}

	}
	
	public void judgementDialog(int mFlag , int mId , Map<String, Object> mMap) { //����dialog��֮ǰ���ж�

		if (mFlag == 0) {

			edit_status = 1;//1��ʾֻ�޸ĵ�ǰ��2��ʾ�޸ĵ�ǰ��all future
			deleteThisBill(mFlag,mId,mMap);
			// handler.post(allUpdater);
//			mUpdater();
			 handler = new Handler();
			 handler.post(allUpdater);
			
		}else if (mFlag == 1) {

			int temPaydate = judgeTemPayDate(mId);		
			long firstPayDate = judgePayDate(mId);

			if (temPaydate > 0) {
				edit_status = 1;
				deleteThisBill(mFlag,mId,mMap);
				// handler.post(allUpdater);
//				mUpdater();
				 handler = new Handler();
				 handler.post(allUpdater);
				
			} else {

				if (firstPayDate == 0) {

					editDialogShow(mFlag,mId,mMap);

				} else {

					edit_status = 1;
					deleteThisBill(mFlag,mId,mMap);
					// handler.post(allUpdater);
//					mUpdater();
					 handler = new Handler();
					 handler.post(allUpdater);
				}

			}

		}else if(mFlag == 2){
			long firstPayDate = judgePayDate(mId);
			long mbk_billDuedate = (Long)mMap.get("nbk_billDuedate");
			if (firstPayDate == 0) {
				editDialogShow(mFlag,mId,mMap);
			} else {

				if (firstPayDate < mbk_billDuedate) {

					editDialogShow(mFlag,mId,mMap);

				} else {
					edit_status = 1;
					deleteThisBill(mFlag,mId,mMap);
					// handler.post(allUpdater);
//					mUpdater();
					 handler = new Handler();
					 handler.post(allUpdater);
				}

			}

		}else if(mFlag == 3){

			if (mMap.containsKey("billObjecthasBill")) {
				int billo_id = (Integer)mMap.get("billObjecthasBill");
				long firstPayDate = judgePayDate(billo_id);
				long mbk_billDuedate = (Long)mMap.get("nbk_billDuedate");
				
				if (firstPayDate == 0) {
					editDialogShow(mFlag,mId,mMap);
				} else {

					if (firstPayDate < mbk_billDuedate) {

						editDialogShow(mFlag,mId,mMap);
						
					} else {
						edit_status = 1;
						deleteThisBill(mFlag,mId,mMap);
						// handler.post(allUpdater);
//						mUpdater();
						 handler = new Handler();
						 handler.post(allUpdater);
					}
				}
			}else {

				edit_status = 1;
				deleteThisBill(mFlag,mId,mMap);
				// handler.post(allUpdater);
//				mUpdater();
				 handler = new Handler();
				 handler.post(allUpdater);
		}

		}
	}
	
	
	public void editDialogShow(final int mFlag , final int mId , final Map<String, Object> mMap) { //�Ի���ĵ�������

		View  dialogview = flater.inflate(R.layout.dialog_upcoming_item_operation,null); 
		diaListView = (ListView)dialogview.findViewById(R.id.dia_listview);
		diaListView.setAdapter(dialogEditBillAdapter);
		diaListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (arg2 == 0) {
					edit_status =1;
					
					deleteThisBill(mFlag,mId,mMap);
					// handler.post(allUpdater);
//					mUpdater();
					 handler = new Handler();
					 handler.post(allUpdater);
					editDialog.dismiss();

				} else if (arg2 == 1) {
					edit_status = 2;
					deleteAllFuture(mFlag,mId,mMap);
					// handler.post(allUpdater);
//					mUpdater();
					 handler = new Handler();
					 handler.post(allUpdater);
					editDialog.dismiss();
				}
			}

		});
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Details");
		builder.setView(dialogview);
		editDialog = builder.create();
		editDialog.show();

	}
	
	public void deleteAllFuture(int mFlag ,int theId ,Map<String, Object> mMap) { //dialog ɾ��ǰ�������¼�

		if(mFlag == 1){
			billDelete(theId);
		}else if(mFlag == 2){

			billVirtualFutuDelete(theId,mMap);

		}else if (mFlag == 3) {
			int billo_id =0 ;

			if (mMap.containsKey("billObjecthasBill")) {
				billo_id = (Integer)mMap.get("billObjecthasBill");
			}else{
				
			}
			billVirtualFutuDelete(billo_id,mMap);
		}
	}
	
	public long billVirtualFutuDelete(int rowid ,Map<String, Object> mMap){  //ɾ��ǰ��֮�����ݣ���ĸ����������ڣ�Ȼ��ɾ��ǰ��֮��������¼�

		long mbk_billDuedate =  (Long) mMap.get("nbk_billDuedate");
		int mbk_billRepeatType = (Integer)mMap.get("nbk_billRepeatType");
		int mbk_billRepeatNumber = (Integer)mMap.get("nbk_billRepeatNumber");
		
		Log.v("mdb", "ִ�������¼���ǰ��֮��Ĳ���");
		long row = 0;
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(mbk_billDuedate); //�����ظ��Ŀ�ʼ����

		if ( mbk_billRepeatType == 1 ) {

			calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-mbk_billRepeatNumber);

		} else if (mbk_billRepeatType == 2) {

			calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-mbk_billRepeatNumber*7);

		} else if (mbk_billRepeatType == 3) {

			calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)-mbk_billRepeatNumber);

		} else if (mbk_billRepeatType == 4) {

			calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR)-mbk_billRepeatNumber);

		} 

		long preDuedate = calendar.getTimeInMillis(); //��һ���ظ��¼������ʱ��

		//�ж���Χ��ȷ��,��ʾ���ظ��¼�֮ǰ��һ���ظ��¼�ʱ�� ��������

		bksql = new BillKeeperSql(getActivity());
		SQLiteDatabase db = bksql.getReadableDatabase();
		String id = rowid+"";
		String [] ids = {String.valueOf(rowid)}; //��ǰobject bill��id
		ContentValues values = new ContentValues();
		values.put("bk_billEndDate", preDuedate);
		row=db.update("BK_Bill", values, "_id=?" ,ids); 
		db.close();

		billObjThisandFuDelete(rowid,mbk_billDuedate);

		return row;
	}

	public void billObjThisandFuDelete(int rowid ,long thisDate){  //ɾ��ǰ��֮������������¼�

		bksql = new BillKeeperSql(getActivity());
		SQLiteDatabase db = bksql.getReadableDatabase();

		List<Map<String, Object>> tDataList = new ArrayList<Map<String, Object>>();
		Map<String, Object> tMap;
		String sql = "select _id from BK_BillObject where billObjecthasBill = "+rowid+" and bk_billODueDate >= " +thisDate;
		Cursor cursorEA = db.rawQuery(sql, null);
		while (cursorEA.moveToNext()) {

			tMap = new HashMap<String, Object>();
			int tbillo_id = cursorEA.getInt(0);
			tMap.put("tbillo_id", tbillo_id);
			tDataList.add(tMap);
		}
		cursorEA.close();
		db.close();
		if (tDataList.size()>0) {

			for(Map<String, Object> iMap:tDataList){
				int tbillo_id = (Integer) iMap.get("tbillo_id");
				billObjTrueDelete(tbillo_id);
			}

		} 
	}
	
	public long billObjTrueDelete(int rowid){ //����ݿ�ɾ��������¼��ļ�¼

		bksql = new BillKeeperSql(getActivity());
		SQLiteDatabase db = bksql.getReadableDatabase();
		String id = rowid+"";
		db.execSQL("PRAGMA foreign_keys = ON ");
		long row=db.delete("BK_BillObject", "_id = ?", new String[]{id});
		db.close();

		return row;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {

		case R.id.addbill:
			Intent intent = new Intent();
			intent.setClass(getActivity(), NewBillActivity.class);
			startActivityForResult(intent, 2);
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 20:
			if (data != null) {

//				mUpdater();
				 handler = new Handler();
				 handler.post(allUpdater);
			}
			break;

		case 10:
			if (data != null) {

//				mUpdater();
				 handler = new Handler();
				 handler.post(allUpdater);
			}
			break;
		case 2:
			if (data != null) {

//				mUpdater();
				 handler = new Handler();
				 handler.post(allUpdater);
			}
			break;
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
	}

}
