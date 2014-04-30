package com.appxy.billkeeper.activity;

/**
 * ���������ά������Ĵ���
 * ����˵���Ѿ�����ע���ˡ�ǰ��û����ƺ�ģʽ��������Զ��
 * ���ϱ仯������ֻ��������
 * 
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import com.appxy.billkeeper.BaseHomeActivity;
import com.appxy.billkeeper.R;
import com.appxy.billkeeper.R.layout;
import com.appxy.billkeeper.R.menu;
import com.appxy.billkeeper.adapter.DialogEditBillAdapter;
import com.appxy.billkeeper.adapter.DialogUpcomingItemAdapter;
import com.appxy.billkeeper.db.BillKeeperSql;
import com.appxy.billkeeper.service.BillNotificationService;

import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.R.bool;
import android.R.integer;
import android.R.string;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.TimePicker;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;

@SuppressLint("ResourceAsColor")
public class  EditBillActivity extends BaseHomeActivity {

	private Button accountNa;
	private EditText dueAmonut;
	private Button dueDate;
	private Button repeating;
	private Button remindMe;
	private Button unknownButton;
	private EditText untilEditText;
	private EditText remindAtEditText;
	private Switch autoPaySwitch;
	private Switch repeatingSwitch;
	private LinearLayout LinearLayout1;
	private Button addNewAccountButton;

	private TextView onceTextView ;
	private TextView UntilTextView ;

	private ImageButton fixedButton;
	private ImageButton variableButton;

	// date and time
	private int mYear;
	private int mMonth;
	private int mDay;
	private int mHour;
	private int mMinute;

	private static int CHECKED = -1;//�ж��Ƿ��dueAmount������м���
	private String myDate;

	private String remindMeString;

	private long remindatTime;  //���ѵ�ʱ��
	private Spinner repeatFrequencySpinner; //repeat dialog�е��ظ�Ƶ��
	private Spinner repeatUnitSpinner;		//�ظ���Ԫ
	private Spinner isForeverSpinner;       //�ظ�����
	private TextView repeatUnitTextView;	//�ظ���Ԫ
	private Button repeatUntilButton;		//�ظ���ֹ����
	private Spinner isFixedSpinner;			//����Ƿ�̶�
	private TextView everyTextView;
	private ImageButton deleteButton;

	private TextView reminderMeTextView;
	private TextView reminderAtTextView;
	private Spinner reminderMeSpinner;
	private Switch remindMeSwitch;
	private BillKeeperSql bksql;

	private String accountNameString;//AccountName���
	private int zIsunknownAmount = -1;	 //N/A
	private String dueAmountFloat = "0.00"; //Due Amount���  **** 

	private float mdueAmountFloat;

	private String dueDateString;    //Due Date���
	private int repeatOnOff = 2;     //repeat�ظ��Ƿ��
	private String repeatFrequencyString; //�ظ�Ƶ�� ****
	private String repeatUnitString; //�ظ���Ԫ
	private String untilDateString;      //����ʱ��
	private int amountTypeString; //amountType�ظ����� 1��ʾ�̶� 0��ʾ���̶�
	private int isAutoPay=0;    		 //�Ƿ��Զ�֧��  ****
	private int reminderOnOf=0;        //�Ƿ�����
	private String remindBefore;        //���ǰ����
	private String remindTimeString; //��ȡ����ʱ��

	private TextView repeatTextView;
	private TextView repeatTypeTextView;
	private RelativeLayout buttonLayout;

	private long dueDateLong; //����ʱ�䣬���浽��ݿ�Ϊ���� ****
	private long untileDateLong; //�ظ���ֹʱ�䣬���浽��ݿ�Ϊ���� ****
	private long remindTimeLong; //��ʲôʱ������������
	private long remindDateLong; //bill����ʱ�䣨����ʱ��-before����Ȼ����������time������

	private final int UNKNOWNZERO = 0; 

	private int REPCHECK = 0; //�Ƿ��ظ� ****
	private String zAccountinfoZ_PK;//����
	private List<String> list = new ArrayList<String>();
	private LayoutInflater inflater; 
	private int checked = -1; //categoryѡ���λ��

	private String[] accountNameArray; //����account name�����飬���dialog��
	private int[]  accountIdArray;  // ����account��������顣
	private int accountId; //���浽��ݿ��account������Ϊ���ʹ�á� ****
	private int accountPosition = -1;
	private int accountlength = 0; //����ݿ��ѯ��caccountname������
	private int isdue = 0 ; //�ж�dueamount�Ƿ����
	private String [] unitArray = {"day(s)","week(s)","month(s)","year(s)"};
	private int [] mUnitdate = {1,2,3,4};
	private int mRepeatUnit = 1 ; //���浽��ݵ��ظ���Ԫ���ֱ���������,0��Ϊ���ø�ѡ�� ****
	private int isForever = -1; //���浽��ݿ⣬�Ƿ���Զ�ظ���-2��ʾ��duedate��-1��ʾ��Զ�ظ�
	private int isFixed = 0; //�Ƿ�̶� 0��ʾ�̶�ֵ��1��ʾ�ɱ��

	private AlertDialog selectDialog ; 
	private int bill_id;
	private  List<Map<String, Object>> billInfoList;
	private Map<String, Object> bMap; 
	public Handler handler;


	private double nbillamount ;
	private int nbk_billAmountUnknown;
	private int nbk_billAutoPay ;
	private long nbk_billDuedate ;
	private long nbk_billEndDate;
	private int nbk_billisReminder ;
	private int nbk_billisRepeat;
	private long nbk_billReminderDate;
	private int nbk_billRepeatNumber ;
	private int nbk_billRepeatType ;
	private String naccountname ;
	private int naccountid ;
	public Calendar c ;
	private Map<String, Object> mMap ;//�����mapֵ
	private int flag;

	private  double mbillamount ;
	private  int mbk_billAmountUnknown ;
	private  int mbk_billAutoPay ;
	private  long mbk_billDuedate ; //�൱���¼��Ŀ�ʼ����
	private  long mbk_billEndDate ; //�ظ��¼��Ľ�ֹ����
	private  int mbk_billisReminder ;
	private  int mbk_billisRepeat ;
	private  int mbk_billisVariaable;
	private long mbk_billReminderDate ;
	private long mbk_billReminderTime ;
	private int mbk_billRepeatNumber ; 
	private int mbk_billRepeatType ; 
	private int mbillhasAccount ;
	private String mbk_accountName ;
	private int maccounthasCategory ;
	private int mpayState ;
	private int mbk_categoryIconName ;
	private String mbk_categoryName ;

	private int edit_status = 0; //�༭״̬��1��ʾchange this ��2��ʾthis and future

	private DialogEditBillAdapter dialogEditBillAdapter ;
	private ListView diaListView;
	private AlertDialog editDialog;
    private Calendar cTime;
    private Calendar cDate;
    
	private int TeremindSpinnerPosition ; //��ʱ���� �������ϴε���ظ���״̬
	private int TerepeatFrequencyPositon;
	private int TerepeatUnitPositon ;
	private int TeisForeverPosition ;
	private int TeisFixedPosition;
	private int TeREPCHECK;
	private int TereminderOnOf ;
	
	private int remindSpinnerPosition = 0; //������һ��spinner�����λ��
	private int repeatFrequencyPositon = 0;
	private int repeatUnitPositon  = 0;
	private int isForeverPosition  = 0;
	private int isFixedPosition  = 0;
	
	private int uYear;
	private int uMonth;
	private int uDay;
	
	private static String mUntil = "Until a date" ;
	private static String[] repeat_type_Strings = {"Forever",mUntil};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_bill);

		dialogEditBillAdapter =new DialogEditBillAdapter(this);
		inflater = LayoutInflater.from(EditBillActivity.this); //�Զ���actionbar
		billInfoList = new ArrayList<Map<String, Object>>();
		bMap = new  HashMap<String, Object>();
		c = Calendar.getInstance();

		ActionBar mActionBar = getActionBar();
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		View customActionBarView = inflater.inflate(R.layout.activity_new_bill_custom_actionbar,null,false);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setCustomView(customActionBarView,lp);
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		accountNa = (Button) findViewById(R.id.accountNa);
		autoPaySwitch = (Switch)findViewById(R.id.autoPayswitch); //�Զ�֧��
		dueAmonut = (EditText) findViewById(R.id.dueAmount);   // �����ܶ�
		unknownButton = (Button) findViewById(R.id.unknownBut);// unknownbutton
		dueDate = (Button) findViewById(R.id.dueDate);
		repeating = (Button) findViewById(R.id.repeating);
		remindMe = (Button) findViewById(R.id.reminder);
		deleteButton = (ImageButton)findViewById(R.id.delete_bill);

		Intent intent = getIntent();
		mMap = new HashMap<String, Object>();
		mMap.clear();
		mMap = (Map<String, Object>) intent.getSerializableExtra("dataMap");
		flag  = (Integer) mMap.get("indexflag");
		bill_id = (Integer)mMap.get("BK_Bill_Id");

		Log.v("mtake", "���ܵ���mMap:"+mMap);
		mbillamount = (Double)mMap.get("nbillamount");
		mbk_billAmountUnknown = (Integer)mMap.get("nbk_billAmountUnknown");
		mbk_billAutoPay = (Integer)mMap.get("nbk_billAutoPay");
		mbk_billDuedate = (Long)mMap.get("nbk_billDuedate"); //�൱���¼��Ŀ�ʼ����
		mbk_billEndDate =  (Long)mMap.get("nbk_billEndDate"); //�ظ��¼��Ľ�ֹ����
		mbk_billisReminder = (Integer)mMap.get("nbk_billisReminder");
		mbk_billisRepeat = (Integer)mMap.get("nbk_billisRepeat");
		mbk_billisVariaable = (Integer)mMap.get("bk_billisVariaable");
		mbk_billReminderDate = (Long)mMap.get("nbk_billReminderDate");
		mbk_billReminderTime = (Long)mMap.get("bk_billReminderTime");
		mbk_billRepeatNumber = (Integer)mMap.get("nbk_billRepeatNumber"); 
		mbk_billRepeatType = (Integer)mMap.get("nbk_billRepeatType"); 
		mbillhasAccount = (Integer)mMap.get("billhasAccount");
		mbk_accountName = (String)mMap.get("bk_accountName");
		maccounthasCategory = (Integer)mMap.get("accounthasCategory");
		mpayState = (Integer)mMap.get("payState");
		mbk_categoryIconName = (Integer)mMap.get("bk_categoryIconName");
		mbk_categoryName = (String)mMap.get("bk_categoryName");
		
		REPCHECK = mbk_billisRepeat;
		reminderOnOf = mbk_billisReminder;
		
		cTime = Calendar.getInstance();
		
		if (mbk_billisReminder == 1) {

			cTime.set(Calendar.HOUR_OF_DAY, (int) (mbk_billReminderTime/(60*60*1000)));
			cTime.set(Calendar.MINUTE, (int) (mbk_billReminderTime%(60*60*1000))/(60*1000));
			mHour = cTime.get(Calendar.HOUR_OF_DAY);  
			mMinute = cTime.get(Calendar.MINUTE);  
			
			if (mbk_billReminderDate == 1) {
				remindSpinnerPosition = 0;
			} else if (mbk_billReminderDate == 3) {
				remindSpinnerPosition = 1;
			} else if (mbk_billReminderDate == 7) {
				remindSpinnerPosition = 2;
			} else if (mbk_billReminderDate == 30){
				remindSpinnerPosition = 3;
			}
		
		} else {
			cTime.set(Calendar.HOUR_OF_DAY, 8);
			cTime.set(Calendar.MINUTE, 0);
			mHour = cTime.get(Calendar.HOUR_OF_DAY);  
			mMinute = cTime.get(Calendar.MINUTE);  
		}
		
		cDate = Calendar.getInstance();
		if (mbk_billisRepeat == 1 ) {
			
			repeatFrequencyPositon = mbk_billRepeatNumber -1; //���ظ����Ե��ʺϣ����õ�һ�ν����λ��
			repeatUnitPositon = mbk_billRepeatType -1;
			isFixedPosition = mbk_billisVariaable;
			
			if (mbk_billEndDate == -1) {
				isForeverPosition = 0;
				uYear = cDate.get(Calendar.YEAR);
				uMonth = cDate.get(Calendar.MONTH);
				uDay = cDate.get(Calendar.DAY_OF_MONTH);
			} else {
				isForeverPosition = 1;
				cDate.setTimeInMillis(mbk_billEndDate);
				uYear = cDate.get(Calendar.YEAR);
				uMonth = cDate.get(Calendar.MONTH);
				uDay = cDate.get(Calendar.DAY_OF_MONTH);
			}
			
		} else {
			
//			cDate.setTimeInMillis(mbk_billEndDate);
			uYear = cDate.get(Calendar.YEAR);
			uMonth = cDate.get(Calendar.MONTH);
			uDay = cDate.get(Calendar.DAY_OF_MONTH);
			
		}

		handler = new Handler();
		handler.post(billUpdater);

		LinearLayout1 = (LinearLayout)findViewById(R.id.mLinearLayout1); //������ռedittext�Ľ���
		LinearLayout1.setFocusable(true);
		LinearLayout1.setFocusableInTouchMode(true);

		deleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (edit_status == 1) {

					new AlertDialog.Builder(EditBillActivity.this)
					.setTitle("Warning! ")
					.setMessage(
					"Are you sure you want to delete this bill? ")
					.setPositiveButton("Delete",	new DialogInterface.OnClickListener() {

						@Override	
						public void onClick(DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub

							Log.v("mdb", "ɾ���¼���flag��"+flag + " :**bill_id:"+bill_id);
							deleteThisBill(flag,bill_id);
							dialog.dismiss();

							Intent intent = new Intent();
							intent.putExtra("flag", 1);
							setResult(21, intent);  
							finish();
						}
					})
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

						}

					}).show();


				} else if(edit_status == 2){

					new AlertDialog.Builder(EditBillActivity.this)
					.setTitle("Warning! ")
					.setMessage(
					" Are you sure you want to delete this bill and all future bills?")
					.setPositiveButton("Delete",
							new DialogInterface.OnClickListener() {

						@Override	
						public void onClick(DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub
							deleteAllFuture(flag,bill_id);
							dialog.dismiss();
							Intent intent = new Intent();
							intent.putExtra("flag", 1);
							setResult(21, intent);  
							finish();

						}
					})
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

						}

					}).show();
				}

			}
		});

		accountNa.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				View view = inflater.inflate(R.layout.dialog_select_account, null);

				addNewAccountButton = (Button)view.findViewById(R.id.add_new_account);  //����½�һ��new account��dialog
				addNewAccountButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						Intent intent = new Intent();
						intent.setClass(EditBillActivity.this,
								AddNewAccountActivity.class);
						startActivityForResult(intent, 0);
					}
				});

				getData();
				AlertDialog.Builder selectAccountDialog = new AlertDialog.Builder( //��һ��account��dialog
						EditBillActivity.this);
				selectAccountDialog.setTitle("Select Account");
				selectAccountDialog.setView(view);
				selectAccountDialog.setSingleChoiceItems(accountNameArray, accountPosition,new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int item) {
						accountPosition = item ;
						String  mAccountName = accountNameArray[item];
						accountId = accountIdArray[item];
						accountNa.setText(mAccountName);
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


		autoPaySwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					isAutoPay=1;
				} else {
					isAutoPay=0;
				}
			}
		});


		dueAmonut = (EditText) findViewById(R.id.dueAmount);   // �����ܶ�
		dueAmonut.setText("0.00");
		unknownButton = (Button) findViewById(R.id.unknownBut);// unknownbutton

		unknownButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				CHECKED = 1;
				dueAmonut.setText("N/A");
				zIsunknownAmount = 1;

				((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(EditBillActivity.this
						.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS); //��������
				dueAmonut.setSelection(dueAmonut.length());
				unknownButton.setVisibility(View.INVISIBLE);
			}
		});

		dueAmonut.setOnTouchListener(new OnTouchListener() {// accountName��ȡ����
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (MotionEvent.ACTION_DOWN == event.getAction()) {
					unknownButton.setVisibility(View.VISIBLE);
					CHECKED = 0;
					zIsunknownAmount = -1;

					dueAmonut.setText("0.00");
				}
				return false;
			}
		});


		dueAmonut.addTextChangedListener(new TextWatcher() { // ���ñ�����λС��
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
				if (CHECKED == 0) {

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


					dueAmonut.setText(cuttedStr);
					dueAmountFloat= dueAmonut.getText().toString();
					dueAmonut.setSelection(cuttedStr.length());

					isChanged = false;
				}
			}
		});


		dueDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DatePickerDialog DPD = new DatePickerDialog( // �ı�theme
						new ContextThemeWrapper(EditBillActivity.this,
								android.R.style.Theme_Holo_Light),
								mDateSetListener, mYear, mMonth, mDay);
				DPD.setTitle("Due Date");
				DPD.show();
			}
		});


		repeating.setOnClickListener(new OnClickListener() {// repeating��ȡ����
			@Override
			public void onClick(View v)  {
				// TODO Auto-generated method stub
				{
					
					View view = inflater.inflate(R.layout.dialog_repeating, null);
					
					repeatUnitSpinner = (Spinner) view.findViewById(R.id.repeat_type_spinner); //�ظ����� day
					repeatFrequencySpinner = (Spinner) view.findViewById(R.id.repeat_frequency_spinner); // Ƶ��1 2 
					repeatUnitTextView = (TextView)view.findViewById(R.id.typetextview); 
					isForeverSpinner = (Spinner) view.findViewById(R.id.is_forever_spinner); //��Զor����
					repeatUntilButton = (Button)view.findViewById(R.id.repeating_until); //����button
					isFixedSpinner = (Spinner) view.findViewById(R.id.is_fixed_spinner); // fixed
					everyTextView = (TextView) view.findViewById(R.id.everytextView);
					repeatingSwitch = (Switch)view.findViewById(R.id.is_repeat_switch);

					everyTextView.setEnabled(false);
					repeatUnitSpinner.setEnabled(false);
					repeatFrequencySpinner.setEnabled(false);
					repeatUnitTextView.setEnabled(false);
					isForeverSpinner.setEnabled(false);
					repeatUntilButton.setEnabled(false);
					isFixedSpinner.setEnabled(false);
					repeatUntilButton.setTextColor(android.graphics.Color.GRAY);

					if (REPCHECK == 1) {
						repeatingSwitch.setChecked(true);

						everyTextView.setEnabled(true);
						repeatUnitSpinner.setEnabled(true);
						repeatFrequencySpinner.setEnabled(true);
						repeatUnitTextView.setEnabled(true);
						isForeverSpinner.setEnabled(true);
						repeatUntilButton.setEnabled(true);
						isFixedSpinner.setEnabled(true);
						repeatUntilButton.setTextColor(android.graphics.Color.BLACK);
						repeatUnitTextView.setText(turnToRepeatType(mbk_billRepeatType));

					} else {
						repeatingSwitch.setChecked(false);

						everyTextView.setEnabled(false);
						repeatUnitSpinner.setEnabled(false);
						repeatFrequencySpinner.setEnabled(false);
						repeatUnitTextView.setEnabled(false);
						isForeverSpinner.setEnabled(false);
						repeatUntilButton.setEnabled(false);
						isFixedSpinner.setEnabled(false);
						repeatUntilButton.setTextColor(android.graphics.Color.GRAY);
					}

					TeREPCHECK = REPCHECK;
					repeatingSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							// TODO Auto-generated method stub
							if (isChecked) {
//								REPCHECK = 1;
								TeREPCHECK = 1;

								everyTextView.setEnabled(true);
								repeatUnitSpinner.setEnabled(true);
								repeatFrequencySpinner.setEnabled(true);
								repeatUnitTextView.setEnabled(true);
								isForeverSpinner.setEnabled(true);
								repeatUntilButton.setEnabled(true);
								isFixedSpinner.setEnabled(true);
								repeatUntilButton.setTextColor(android.graphics.Color.BLACK);

							}else {
//								REPCHECK = 0;
								TeREPCHECK = 1;

								everyTextView.setEnabled(false);
								repeatUnitSpinner.setEnabled(false);
								repeatFrequencySpinner.setEnabled(false);
								repeatUnitTextView.setEnabled(false);
								isForeverSpinner.setEnabled(false);
								repeatUntilButton.setEnabled(false);
								isFixedSpinner.setEnabled(false);
								repeatUntilButton.setTextColor(android.graphics.Color.GRAY);
							}
						}
					});


					ArrayAdapter<CharSequence> adapter = ArrayAdapter //�ظ�Ƶ��
					.createFromResource(
							EditBillActivity.this,
							R.array.repeat_requency,
							R.layout.spinner_item);
					adapter.setDropDownViewResource(R.layout.spinner_drop_item);
					repeatFrequencySpinner.setAdapter(adapter);

					TerepeatFrequencyPositon = repeatFrequencyPositon;
					repeatFrequencySpinner.setSelection(repeatFrequencyPositon, true);
					
//					if (mbk_billisRepeat == 1) {
//						repeatFrequencySpinner.setSelection(mbk_billRepeatNumber-1, true);
//					}
					repeatFrequencySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							// TODO Auto-generated method stub
							TerepeatFrequencyPositon = arg2;
						}

						@Override
						public void onNothingSelected(
								AdapterView<?> arg0) {
							// TODO Auto-generated method stub

						}
					});

					ArrayAdapter<CharSequence> adapter1 = ArrayAdapter //�ظ���Ԫ
					.createFromResource(
							EditBillActivity.this,
							R.array.repeat_unit,
							R.layout.spinner_item);
					adapter1.setDropDownViewResource(R.layout.spinner_drop_item);
					repeatUnitSpinner.setAdapter(adapter1);
					
					repeatUnitSpinner.setSelection(repeatUnitPositon, true);
					TerepeatUnitPositon = repeatUnitPositon;
					repeatUnitTextView.setText(unitArray[repeatUnitPositon]);
					
//					if (mbk_billisRepeat == 1) {
//						repeatUnitSpinner.setSelection(mbk_billRepeatType-1, true);
//					}
					repeatUnitSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							// TODO Auto-generated method stub
							repeatUnitTextView.setText(unitArray[arg2]);
							mRepeatUnit = mUnitdate[arg2];
							TerepeatUnitPositon = arg2;
						}

						@Override
						public void onNothingSelected(
								AdapterView<?> arg0) {
							// TODO Auto-generated method stub

						}

					});

					 ArrayAdapter<CharSequence> adapter2 = ArrayAdapter //�ظ����ͣ��Ƿ���Զ�ظ�
					.createFromResource(
							EditBillActivity.this,
							R.array.repeat_type,
							R.layout.spinner_item);
					adapter2.setDropDownViewResource(R.layout.spinner_drop_item);
					isForeverSpinner.setAdapter(adapter2);
					
					isForeverSpinner.setSelection(isForeverPosition, true);
					TeisForeverPosition =isForeverPosition;
					
					if (isForeverPosition == 0 ) {
						repeatUntilButton.setVisibility(View.INVISIBLE);
					} if (isForeverPosition == 1) {
						repeatUntilButton.setVisibility(View.VISIBLE);
					} 
					
//					if (mbk_billisRepeat == 1) {
//						if (mbk_billEndDate == -1) {
//							isForeverSpinner.setSelection(0, true);
//						}else{
//							isForeverSpinner.setSelection(1, true);
//							repeatUntilButton.setVisibility(View.VISIBLE);
//						}
//					}
					isForeverSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							// TODO Auto-generated method stub
							if (arg2 == 0 ) {
								isForever = -1;	
								repeatUntilButton.setVisibility(View.INVISIBLE);
								
							} if (arg2 == 1) {
								isForever = -2;	
								repeatUntilButton.setVisibility(View.VISIBLE);
							} 
							TeisForeverPosition = arg2;
						}

						@Override
						public void onNothingSelected(
								AdapterView<?> arg0) {
							// TODO Auto-generated method stub

						}

					});

					ArrayAdapter<CharSequence> adapter3 = ArrayAdapter //�Ƿ�̶���
					.createFromResource(
							EditBillActivity.this,
							R.array.amount_type,
							R.layout.spinner_item);
					adapter3.setDropDownViewResource(R.layout.spinner_drop_item);
					isFixedSpinner.setAdapter(adapter3);
					
					isFixedSpinner.setSelection(isFixedPosition, true);
					TeisFixedPosition = isFixedPosition;
					
//					if (mbk_billisRepeat == 1) {
//						if (mbk_billisVariaable == 1 ) {
//							isFixedSpinner.setSelection(1, true);
//							isFixed = 1;
//						} else {
//							isFixedSpinner.setSelection(0, true);
//							isFixed = 0;
//						}
//					}

					isFixedSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							// TODO Auto-generated method stub
							if ( arg2 == 0) {
								isFixed = 0;
							} if(arg2 == 1){
								isFixed = 1;
							}
							TeisFixedPosition = arg2;
						}

						@Override
						public void onNothingSelected(
								AdapterView<?> arg0) {
							// TODO Auto-generated method stub

						}

					});

					repeatUntilButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							DatePickerDialog DPD1 = new DatePickerDialog(
									// �ı�theme
									new ContextThemeWrapper(
											EditBillActivity.this,
											android.R.style.Theme_Holo_Light),
											mDateSetListenerUntil,
											uYear, uMonth, uDay);
							DPD1.setTitle("Until");
							DPD1.show();
						}
					});


					AlertDialog.Builder repeatingDialog = new AlertDialog.Builder(
							EditBillActivity.this);
					repeatingDialog.setView(view);
					repeatingDialog.setPositiveButton("Done",
							new DialogInterface.OnClickListener() {

						@Override
						public void onClick(
								DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub
//							mbk_billisRepeat = REPCHECK;
							
							if (repeatingSwitch.isChecked()) {
								
								REPCHECK = TeREPCHECK;
								repeatFrequencyPositon = TerepeatFrequencyPositon;
								repeatUnitPositon = TerepeatUnitPositon;
								isForeverPosition = TeisForeverPosition;
								isFixedPosition  = TeisFixedPosition;

								String  mUnitTString = repeatUnitTextView.getText().toString();
								String duedateString = repeatUntilButton.getText().toString();

								repeatFrequencyString = repeatFrequencySpinner.getSelectedItem().toString();
								repeatUnitString = repeatUnitSpinner.getSelectedItem().toString();
								
								if (isForever == -1) {
									repeating.setText("Every "+ repeatFrequencyString+ " " + mUnitTString);
								}if(isForever == -2){
									repeating.setText("Every "+ repeatFrequencyString+ " " + mUnitTString+ " ,Until "+duedateString);
								}
								
							}else {
								repeating.setText("No repeating bill");
							}
						}
					});

					//							repeatingDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
					//								@Override
					//								public void onCancel(DialogInterface arg0) {
					//								finish();
					//								Log.v("mdb", "ִ��kill");
					//								}
					//							});
					repeatingDialog.show();

					updateDisplayUntil();
//					
//					if (mbk_billisRepeat != 1) {
//						final Calendar c = Calendar.getInstance();
//						mYear = c.get(Calendar.YEAR);
//						mMonth = c.get(Calendar.MONTH);
//						mDay = c.get(Calendar.DAY_OF_MONTH);
//						updateDisplayUntil();
//					}

				}
			}
		});

		remindMe.setOnTouchListener(new OnTouchListener() {// remindMe��ȡ����
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (MotionEvent.ACTION_DOWN == event.getAction()) {

					LayoutInflater flater = LayoutInflater.from(EditBillActivity.this);
					View view = flater.inflate(R.layout.dialog_remindme, null);

					reminderMeTextView = (TextView)view.findViewById(R.id.textView1);
					reminderAtTextView = (TextView)view.findViewById(R.id.textView2);
					remindAtEditText = (EditText) view.findViewById(R.id.remindatedittext); 
					reminderMeSpinner = (Spinner) view.findViewById(R.id.spinner1); //remind me Spinner����

					reminderMeTextView.setEnabled(false);
					reminderAtTextView.setEnabled(false);
					remindAtEditText.setEnabled(false);
					reminderMeSpinner.setEnabled(false);

					remindMeSwitch = (Switch)view.findViewById(R.id.switch1);
					
					TereminderOnOf = reminderOnOf;
					
					if (reminderOnOf == 1) {
						remindMeSwitch.setChecked(true);
						reminderMeTextView.setEnabled(true);
						reminderAtTextView.setEnabled(true);
						remindAtEditText.setEnabled(true);
						reminderMeSpinner.setEnabled(true);
						remindDateLong = mbk_billReminderDate;
						remindatTime = mbk_billReminderTime;

					} else {
						remindMeSwitch.setChecked(false);
						reminderMeTextView.setEnabled(false);
						reminderAtTextView.setEnabled(false);
						remindAtEditText.setEnabled(false);
						reminderMeSpinner.setEnabled(false);
					}
					
					remindMeSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							// TODO Auto-generated method stub
							if (isChecked) {
								reminderMeTextView.setEnabled(true);
								reminderAtTextView.setEnabled(true);
								remindAtEditText.setEnabled(true);
								reminderMeSpinner.setEnabled(true);
//								reminderOnOf=1;
								TereminderOnOf = 1;

							} else {
								reminderMeTextView.setEnabled(false);
								reminderAtTextView.setEnabled(false);
								remindAtEditText.setEnabled(false);
								reminderMeSpinner.setEnabled(false);
//								reminderOnOf=0;
								TereminderOnOf = 0; 
							}
						}
					});
					ArrayAdapter<CharSequence> adapter = ArrayAdapter
					.createFromResource(EditBillActivity.this,
							R.array.reminder,
							R.layout.spinner_item);
					adapter.setDropDownViewResource(R.layout.spinner_drop_item);
					reminderMeSpinner.setAdapter(adapter);
					
					
//					if (mbk_billisReminder == 1) {
//						int sp = -1;
//						if (mbk_billReminderDate == 1) {
//							sp = 0;
//						} else if (mbk_billReminderDate == 3) {
//							sp = 1;
//						} else if (mbk_billReminderDate == 7) {
//							sp = 2;
//						} else if (mbk_billReminderDate == 30){
//							sp = 3;
//						}
//						remindBefore = mbk_billReminderDate+"" ;
//						remindMeString = EditBillActivity.this.getResources().getStringArray(R.array.reminder)[sp];
//						reminderMeSpinner.setSelection(sp, true);
//					}

					final String[] frequencyString = {"1","3","7","30"};
					
					reminderMeSpinner.setSelection(remindSpinnerPosition, true);
					TeremindSpinnerPosition =  remindSpinnerPosition ;
					remindBefore = frequencyString[remindSpinnerPosition];
					remindMeString = EditBillActivity.this.getResources().getStringArray(R.array.reminder)[remindSpinnerPosition];
					
					reminderMeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							// TODO Auto-generated method stub
							remindMeString = EditBillActivity.this.getResources().getStringArray(R.array.reminder)[arg2];
							remindBefore = frequencyString[arg2];
							TeremindSpinnerPosition = arg2;
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
							// TODO Auto-generated method stub

						}

					});

					remindAtEditText.setOnTouchListener(new OnTouchListener() {//reminde at����

						@Override
						public boolean onTouch(View v, MotionEvent event) {
							// TODO Auto-generated method stub

							if (MotionEvent.ACTION_DOWN == event.getAction()) {

								TimePickerDialog DPD1 = new TimePickerDialog(
										// �ı�theme
										new ContextThemeWrapper(EditBillActivity.this,android.R.style.Theme_Holo_Light),
										mTimeSetListener, mHour, mMinute, false);
								DPD1.setTitle("Remind At");
								DPD1.show();
							}
							return false;
						}
					});

					AlertDialog.Builder remindMeDialog = new AlertDialog.Builder(
							EditBillActivity.this);
					remindMeDialog.setView(view);

					remindMeDialog.setPositiveButton("Done",
							new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub
							
							remindSpinnerPosition = TeremindSpinnerPosition;
							reminderOnOf = TereminderOnOf;
							
							mbk_billisReminder = reminderOnOf;
							if (remindMeSwitch.isChecked()) {
								remindMe.setText(remindMeString+" , at "+remindTimeString);
							} else {
								remindMe.setText("No reminder");
							}

							if (remindBefore != null && remindBefore.length() != 0) {
								
								

								//										long remindwhichDay =(long)(dueDateLong - (Integer.parseInt(remindBefore))*24*60*60*1000L);
								//										
								//										Date whichDate = new Date(remindwhichDay);
								//										SimpleDateFormat adFormat = new SimpleDateFormat("MM-dd-yyyy");
								//										String whichString = adFormat.format(whichDate);
								//										String whichDayTimeString  = whichString+" "+mHour+":"+mMinute+":"+"00";
								//										
								//										Calendar c = Calendar.getInstance();
								//										try {
								//											c.setTime(new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").parse(whichDayTimeString));
								//										} catch (ParseException e) {
								//											// TODO Auto-generated catch block
								//											e.printStackTrace();
								//										}
								//										remindDateLong = c.getTimeInMillis();

								remindDateLong = Integer.parseInt(remindBefore);
								remindatTime = mHour*60*60*1000 + mMinute*60*1000;
							}
						}
					});
					remindMeDialog.show();
					updateDisplayTime();

				}
				return false;
			}
		});

		View cancelActionView = customActionBarView.findViewById(R.id.action_cancel);
		cancelActionView.setOnClickListener(mActionBarListener);
		View doneActionView = customActionBarView.findViewById(R.id.action_done);
		doneActionView.setOnClickListener(mActionBarListener);

	} 

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onPrepareDialog(int, android.app.Dialog)
	 */

	public Runnable billUpdater = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			if (mMap != null) {

				accountNa.setText(mbk_accountName);
				accountId = mbillhasAccount;

				if (mbk_billAmountUnknown == 1) {
					zIsunknownAmount = 1;
					dueAmonut.setText("N/A");
					unknownButton.setVisibility(View.GONE);
				}else {
					zIsunknownAmount = -1;
					java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
					dueAmonut.setText(df.format(mbillamount));
					unknownButton.setVisibility(View.VISIBLE);
					dueAmountFloat = mbillamount+"";
				}
				c.setTimeInMillis(mbk_billDuedate);
				mYear = c.get(Calendar.YEAR);
				mMonth = c.get(Calendar.MONTH);
				mDay = c.get(Calendar.DAY_OF_MONTH);
				updateDisplay();

				isFixed = mbk_billisVariaable;
				isAutoPay = mbk_billAutoPay;

				if (mbk_billisRepeat == 1) {

					if (mbk_billEndDate == -1) {
						repeating.setText("Every "+ mbk_billRepeatNumber+ " " + turnToRepeatType(mbk_billRepeatType));
					}else{
						repeating.setText("Every "+ mbk_billRepeatNumber+ " " + turnToRepeatType(mbk_billRepeatType)+ " ,Until "+getMilltoDate(mbk_billEndDate));
					}

					REPCHECK = 1;
					mRepeatUnit = mbk_billRepeatType;
					repeatFrequencyString = mbk_billRepeatNumber+"";
					if (mbk_billEndDate == -1) {
						isForever = -1;
					} else {
						isForever = -2;
						untileDateLong = mbk_billEndDate;
					}

					
				} else {

					repeating.setText("No repeating bill");
					REPCHECK = 0;
				}

				if (mbk_billisReminder == 1 ) {

					Log.v("mtake", "����ʱ��:"+mbk_billReminderTime);
					int hours = (int) (mbk_billReminderTime/(60*60*1000));	
					Log.v("mtake", "����ʱ��hours:"+hours);
					int minute = (int) (mbk_billReminderTime%(60*60*1000))/(60*1000);	
					String ap ;
					if (hours>=12) {
						ap="PM";
					}else {
						ap="AM";
					}

					if (13<=hours) {
						hours=hours%12;
					}else {
						hours=hours;
					}

					String hoursString = String.valueOf(hours);
					String minuteString = String.valueOf(minute);

					if (hoursString.length() == 1) {
						hoursString = "0"+hoursString;
					}
					if(minuteString.length() ==1){
						minuteString = "0"+minuteString;
					}
					remindMe.setText( mbk_billReminderDate+" Days before"+" , at "+hoursString+":"+minuteString+" "+ap);
					reminderOnOf =1;
					remindDateLong = mbk_billReminderDate;
					remindatTime = mbk_billReminderTime;

				} else {
					remindMe.setText("No reminder");
					reminderOnOf = 0;
				}

				if (mbk_billAutoPay == 1) {
					autoPaySwitch.setChecked(true);
				}else if (mbk_billAutoPay == 0 ) {
					autoPaySwitch.setChecked(false);
				}

				judgementDialog(flag , bill_id);
			}
		}

	};

	public void judgementDialog(int mFlag , int mId) { //����dialog��֮ǰ���ж�

		if (mFlag == 0) {

			edit_status = 1; //1��ʾֻ�޸ĵ�ǰ��2��ʾ�޸ĵ�ǰ��all future

		}else if (mFlag == 1) {

			int temPaydate = judgeTemPayDate(bill_id); //����pay״̬
			long firstPayDate = judgePayDate(bill_id);//���ص�һ��pay��ʱ�䣬���Ϊ0��û��pay��bill

			if (temPaydate > 0) {
				edit_status = 1;
				accountNa.setEnabled(false);
				repeating.setEnabled(false);
				accountNa.setTextColor(R.color.gray);
				repeating.setTextColor(R.color.gray);
			} else {

				if (firstPayDate == 0) {

					editDialogShow();

				} else {

					edit_status = 1;
					accountNa.setEnabled(false);
					repeating.setEnabled(false);
					accountNa.setTextColor(R.color.gray);
					repeating.setTextColor(R.color.gray);
				}
			}

		}else if(mFlag == 2){
			long firstPayDate = judgePayDate(bill_id);

			if (firstPayDate == 0) {
				editOnlyDialogShow();
			} else {

				if (firstPayDate < mbk_billDuedate) {

					editOnlyDialogShow();

				} else {
					edit_status = 1;
					accountNa.setEnabled(false);
					repeating.setEnabled(false);
					accountNa.setTextColor(R.color.gray);
					repeating.setTextColor(R.color.gray);
				}

			}

		}else if(mFlag == 3){

			if (mMap.containsKey("billObjecthasBill")) {
				int billo_id = (Integer)mMap.get("billObjecthasBill");
				long firstPayDate = judgePayDate(billo_id);

				if (firstPayDate == 0) {
					editOnlyDialogShow();
				} else {

					if (firstPayDate < mbk_billDuedate) {

						editOnlyDialogShow();

					} else {
						edit_status = 1;
						accountNa.setEnabled(false);
						repeating.setEnabled(false);
						accountNa.setTextColor(R.color.gray);
						repeating.setTextColor(R.color.gray);
					}

				}

			}else {

				edit_status = 1;
				accountNa.setEnabled(false);
				repeating.setEnabled(false);
				accountNa.setTextColor(R.color.gray);
				repeating.setTextColor(R.color.gray);
			}

		}


	}

	public void editOnlyDialogShow() { //�Ի���ĵ���,���Ʋ����޸�account

		View  dialogview = inflater.inflate(R.layout.dialog_upcoming_item_operation,null); 
		diaListView = (ListView)dialogview.findViewById(R.id.dia_listview);
		diaListView.setAdapter(dialogEditBillAdapter);
		diaListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (arg2 == 0) {
					edit_status =1;
					accountNa.setEnabled(false);
					repeating.setEnabled(false);
					accountNa.setTextColor(R.color.gray);
					repeating.setTextColor(R.color.gray);
					editDialog.dismiss();

				} else if (arg2 == 1) {
					edit_status = 2;
					editDialog.dismiss();
				}

			}

		});
		AlertDialog.Builder builder = new AlertDialog.Builder(EditBillActivity.this);
		builder.setTitle("Details");
		builder.setView(dialogview);
		builder.setOnCancelListener(new DialogInterface.OnCancelListener(){
			@Override
			public void onCancel(DialogInterface arg0) {
				finish();
			}
		});
		editDialog = builder.create();
		editDialog.show();
	}

	public void editDialogShow() { //�Ի���ĵ�������

		View  dialogview = inflater.inflate(R.layout.dialog_upcoming_item_operation,null); 
		diaListView = (ListView)dialogview.findViewById(R.id.dia_listview);
		diaListView.setAdapter(dialogEditBillAdapter);
		diaListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (arg2 == 0) {
					edit_status =1;
					accountNa.setEnabled(false);
					repeating.setEnabled(false);
					accountNa.setTextColor(R.color.gray);
					repeating.setTextColor(R.color.gray);
					editDialog.dismiss();

				} else if (arg2 == 1) {
					edit_status = 2;
					editDialog.dismiss();
				}

			}

		});
		AlertDialog.Builder builder = new AlertDialog.Builder(EditBillActivity.this);
		builder.setTitle("Details");
		builder.setView(dialogview);
		builder.setOnCancelListener(new DialogInterface.OnCancelListener(){
			@Override
			public void onCancel(DialogInterface arg0) {
				finish();
			}
		});
		editDialog = builder.create();
		editDialog.show();

	}

	public int judgeTemPayDate(int b_id) { //�жϸ����¼���pay״̬�������pay���ܸ�ĵ�ǰ�����ж�bill���е�pay״̬

		Map<String, Object> bMap;
		bksql = new BillKeeperSql(this); 
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
		bksql = new BillKeeperSql(this); 
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

		ArrayList<Long> OPayList = new ArrayList<Long>();
		if (cursorCounnt > 0) {

			long reData = 0;
			for (Map<String, Object> oMap:dataList) {
				int Object_id = (Integer) oMap.get("Object_id");
				long bk_billODueDate = (Long) oMap.get("bk_billODueDate");

				String pSql = "select * from BK_Payment where paymenthasBillO = "+Object_id;
				Cursor cursor = db.rawQuery(pSql, null);
				int pCursorCounnt =cursor.getCount();
				cursor.close();
				if (pCursorCounnt > 0) {
					OPayList.add(bk_billODueDate);
				} 
			}
			db.close();
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

	public String getMilltoDate(long milliSeconds) {//������ת���ɹ̶���ʽ��������
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

	public String turnToRepeatType(int number) {
		String type = "";
		if (number == 1) {
			type ="day";
		} else if (number == 2) {
			type = "week";
		} else if (number == 3) {
			type = "month";
		} else if (number == 4) {
			type= "year";
		} 
		return type;
	}

	public void getBillInfo(int id) {

		bMap.clear();
		bksql = new BillKeeperSql(this); 
		SQLiteDatabase db = bksql.getReadableDatabase();
		String  sql = " select BK_Bill.* ,BK_Account.bk_accountName ,BK_Account._id from BK_Bill,BK_Account where BK_Bill._id = "+id+" and BK_Bill.billhasAccount = BK_Account._id";

		Cursor cursorEA = db.rawQuery(sql, null);
		while (cursorEA.moveToNext()) {

			int mId =  cursorEA.getInt(0); 
			double nbillamount = cursorEA.getDouble(2);
			int nbk_billAmountUnknown = cursorEA.getInt(3);
			int nbk_billAutoPay = cursorEA.getInt(4);
			long nbk_billDuedate = cursorEA.getLong(8);
			long nbk_billEndDate = cursorEA.getLong(9);
			int nbk_billisReminder = cursorEA.getInt(10);
			int nbk_billisRepeat = cursorEA.getInt(11);
			long nbk_billReminderDate = cursorEA.getLong(14);
			int nbk_billRepeatNumber = cursorEA.getInt(16);
			int nbk_billRepeatType = cursorEA.getInt(17);
			String naccountname = cursorEA.getString(25); 
			int naccountid = cursorEA.getInt(26);

			bMap.put("nbillamount", nbillamount);
			bMap.put("nbk_billAmountUnknown", nbk_billAmountUnknown);
			bMap.put("nbk_billAutoPay", nbk_billAutoPay);
			bMap.put("nbk_billDuedate", nbk_billDuedate);
			bMap.put("nbk_billEndDate", nbk_billEndDate);
			bMap.put("nbk_billisReminder", nbk_billisReminder);
			bMap.put("nbk_billisRepeat", nbk_billisRepeat);
			bMap.put("nbk_billReminderDate", nbk_billReminderDate);
			bMap.put("nbk_billRepeatNumber", nbk_billRepeatNumber);
			bMap.put("nbk_billRepeatType", nbk_billRepeatType);
			bMap.put("naccountname", naccountname);
			bMap.put("naccountid", naccountid);

		}
		cursorEA.close();  
		db.close();

	}

	@Override
	@Deprecated
	protected void onPrepareDialog(int id, Dialog dialog) {
		// TODO Auto-generated method stub
		super.onPrepareDialog(id, dialog);
		((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);

	}


	private void getData(){ //��ѯaccount��ݿ�

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

	/*����ʱ����ط�������*/
	private void updateDisplayTime() {  
		String AmOrPm = "";
		int Hour;
		if (mHour>=12) {
			AmOrPm="PM";
		}else {
			AmOrPm="AM";
		}

		if (13<=mHour) {
			Hour=mHour%12;
		}else {
			Hour=mHour;
		}

		remindAtEditText.setText(new StringBuilder().append(pad(Hour)).append(":").append(pad(mMinute)).append(" ").append(AmOrPm).append(" "));  
	    remindTimeString = (new StringBuilder().append(pad(Hour)).append(":").append(pad(mMinute)).append(" ").append(AmOrPm)).toString();

	}  

	private TimePickerDialog.OnTimeSetListener mTimeSetListener =  
		new TimePickerDialog.OnTimeSetListener() {  
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {  
			mHour = hourOfDay;  
			mMinute = minute;  
			updateDisplayTime();  
		}
	};  

	private static String pad(int c) {  //���÷��Ӻ�Сʱ�Ƿ���Ҫ��0
		if (c >= 10)  
			return String.valueOf(c);  
		else  
			return "0" + String.valueOf(c);  
	}  
	/*����ʱ����ط�������*/

	/*����������ط�������*/
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
		}
	};

	private DatePickerDialog.OnDateSetListener mDateSetListenerUntil = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view1, int year, int monthOfYear,
				int dayOfMonth) {
			uYear = year;
			uMonth = monthOfYear;
			uDay = dayOfMonth;
			updateDisplayUntil();
		}

	};

	private void updateDisplayUntil() {
		// TODO Auto-generated method stub
		repeatUntilButton.setText(new StringBuilder().append(uMonth + 1)
				.append("-").append(uDay).append("-").append(uYear));
		untilDateString = (new StringBuilder().append(uMonth + 1).append("-").append(uDay).append("-").append(uYear)).toString();
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(untilDateString));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		untileDateLong = c.getTimeInMillis();
		mbk_billEndDate = untileDateLong;
	}

	private void updateDisplay() {
		// TODO Auto-generated method stub
		dueDate.setText(new StringBuilder()
		// Month is 0 based so add 1
		.append(mMonth + 1).append("-").append(mDay).append("-")
		.append(mYear));
		dueDateString = (new StringBuilder().append(mMonth + 1).append("-").append(mDay).append("-").append(mYear)).toString();

		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(dueDateString));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dueDateLong = c.getTimeInMillis();

	}
	/*����������ط�������*/
	public long billDelete(int rowid){

		bksql = new BillKeeperSql(this);
		SQLiteDatabase db = bksql.getReadableDatabase();
		String id = rowid+"";
		db.execSQL("PRAGMA foreign_keys = ON ");
		long row=db.delete("BK_Bill", "_id = ?", new String[]{id});
		db.close();

		return row;
	}

	public long billObjectDelete(int rowid){

		bksql = new BillKeeperSql(this);
		SQLiteDatabase db = bksql.getReadableDatabase();
		String id = rowid+"";
		String [] ids = {String.valueOf(rowid)}; //��ǰobject bill��id
		ContentValues values = new ContentValues();
		values.put("bk_billsDelete", 1);
		long row=db.update("BK_BillObject", values, "_id=?" ,ids); 
		db.close();

		return row;
	}

	public long billVirtualThisDelete(int rowid){  //ɾ��ǰ�������¼� ��ʵ��Ȼ������ɾ������

		bksql = new BillKeeperSql(this);
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

	public long billVirtualFutuDelete(int rowid){  //ɾ��ǰ��֮�����ݣ���ĸ����������ڣ�Ȼ��ɾ��ǰ��֮��������¼�

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

		bksql = new BillKeeperSql(this);
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

		bksql = new BillKeeperSql(this);
		SQLiteDatabase db = bksql.getReadableDatabase();

		List<Map<String, Object>> tDataList = new ArrayList<Map<String, Object>>();
		Map<String, Object> tMap;
		String sql = "select _id from BK_BillObject where billObjecthasBill = "+rowid+" and bk_billODueDate >= " +thisDate;
		Cursor cursorEA = db.rawQuery(sql, null);
		while (cursorEA.moveToNext()) {

			tMap = new HashMap<String, Object>();
			int tbillo_id = cursorEA.getInt(0);
			tMap.put("tbillo_id", tbillo_id);
			Log.v("mmes", "�����Ҫɾ���Obj�� " +tbillo_id);
			tDataList.add(tMap);
		}
		cursorEA.close();
		db.close();
		if (tDataList.size()>0) {

			for(Map<String, Object> iMap:tDataList){
				int tbillo_id = (Integer) iMap.get("tbillo_id");
				Log.v("mmes", "��ʼִ��ɾ���Obj�� " +tbillo_id);
				billObjTrueDelete(tbillo_id);
				
			}

		} 
	}

	public long billObjTrueDelete(int rowid){ //����ݿ�ɾ��������¼��ļ�¼

		bksql = new BillKeeperSql(this);
		SQLiteDatabase db = bksql.getReadableDatabase();
		String id = rowid+"";
		db.execSQL("PRAGMA foreign_keys = ON ");
		long row=db.delete("BK_BillObject", "_id = ?", new String[]{id});
		db.close();

		return row;
	}

	public long billParentDelete(int rowid){ //��ǰ������ɾ��duedate����Ϊ��һ�η����ʱ�䣬��κ���һ��ʱ����ȣ����ʾ�����������ظ���ֱ��ɾ�� 

		long row = 0;

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

			bksql = new BillKeeperSql(this);
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
	
	public void deletePayment(int hasId){ //ɾ���pay״̬
		
		bksql = new BillKeeperSql(this);
		SQLiteDatabase db = bksql.getReadableDatabase();
		String id = hasId+"";
		long row=db.delete("BK_Payment", "paymenthasBill = ?", new String[]{id});
		db.close();
	}

	public void deleteThisBill(int mFlag ,int theId) { //dialog���õķ�����ɾ����¼�

		if (mFlag == 0) {
			billDelete(theId);
		} else if(mFlag == 1){
			billParentDelete(theId);
		}else if(mFlag == 2){
			billVirtualThisDelete(theId);
		}else if (mFlag == 3) {
			billObjectDelete(theId);
		}

	}

	public void deleteAllFuture(int mFlag ,int theId) { //dialog ɾ��ǰ�������¼�

		if(mFlag == 1){
			billDelete(theId);
		}else if(mFlag == 2){

			billVirtualFutuDelete(theId);

		}else if (mFlag == 3) {
			int billo_id =0 ;

			if (mMap.containsKey("billObjecthasBill")) {
				billo_id = (Integer)mMap.get("billObjecthasBill");
			}else{
				finish();
			}
			billVirtualFutuDelete(billo_id);
		}
	}
	
	public long parentDueDate(int id) {
		bksql = new BillKeeperSql(this);
		SQLiteDatabase db = bksql.getReadableDatabase();
		String sql = "select bk_billDuedate from BK_Bill where _id = "+ id;
		Cursor cursorEA = db.rawQuery(sql, null);
		long dueDate = 0; 
		while (cursorEA.moveToNext()) {

			dueDate = cursorEA.getLong(0);
		}
		cursorEA.close();
		db.close();
		return dueDate;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 0:

			selectDialog.dismiss();
			if (data != null) {
				accountNa.setText(data.getStringExtra("aName"));
				accountId = Integer.parseInt(data.getStringExtra("pKey"));
				accountPosition = accountlength;
			}

			break;
		}
	}


	public View.OnClickListener mActionBarListener = new View.OnClickListener() { //����actionbar����

		public void onClick(View v) {

			switch (v.getId()){
			case R.id.action_cancel:
				finish();
				break;
			case R.id.action_done:

				String accountName = accountNa.getText().toString();

				String mdueAmoutString = dueAmonut.getText().toString();
				float checkAuto ;
				try {
					checkAuto = Float.parseFloat(mdueAmoutString);
				} catch (NumberFormatException e) {
					checkAuto = 1;
				}

				if (accountName.length() == 0) {

						    				new AlertDialog.Builder(EditBillActivity.this)
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

				}else if(checkAuto == 0){

						    				new AlertDialog.Builder(EditBillActivity.this)
											.setTitle("Warning! ")
											.setMessage(
													"Please make sure the Due Amount is not zero! ")
											.setPositiveButton("Retry",
													new DialogInterface.OnClickListener() {
					
														@Override	
														public void onClick(DialogInterface dialog,
																int which) {
															// TODO Auto-generated method stub
															dialog.dismiss();
														}
													}).show();

				}else if(accountName != null) {

					bksql=new BillKeeperSql(EditBillActivity.this);
					SQLiteDatabase db = bksql.getWritableDatabase();

					Log.v("mmes","�༭���״̬flag: "+flag+"REPCHECK:"+REPCHECK);
					
					if(flag == 0){
						ContentValues values = new ContentValues();
						values.put("billhasAccount", accountId);
						if (zIsunknownAmount ==1) {
							values.put("bk_billAmountUnknown", zIsunknownAmount); //���浽���ܶ��unknownֵ��1��ʾδ֪N/A,-1��ʾδ����N/A��
							values.put("bk_billAmount", UNKNOWNZERO);
						} else {
							values.put("bk_billAmountUnknown", -1);
							values.put("bk_billAmount",dueAmountFloat);
						}
						values.put("bk_billDuedate", dueDateLong);

						if( REPCHECK == 1){

							values.put("bk_billisRepeat", REPCHECK);
							values.put("bk_billRepeatType", mRepeatUnit);
							values.put("bk_billRepeatNumber", repeatFrequencyString);
							if(isForever == -1 ){
								values.put("bk_billEndDate", isForever);
							}else if(isForever == -2){
								values.put("bk_billEndDate", untileDateLong);
							}

						}else if( REPCHECK == 0){
							values.put("bk_billisRepeat", REPCHECK);
						}
						values.put("bk_billisVariaable", isFixed);
						values.put("bk_billAutoPay", isAutoPay);

						if(reminderOnOf== 1){

							values.put("bk_billisReminder",reminderOnOf);
							values.put("bk_billReminderDate",remindDateLong); //��������ʱ��ĺ��������ʱ��-(��ǰ����+����ʱ�䣩)
							values.put("bk_billReminderTime",remindatTime);
						}else{
							values.put("bk_billisReminder",0);
						}
						String [] ids = {String.valueOf(bill_id)};
						int mkey = db.update("BK_Bill", values, "_id=?" ,ids);
						String keyCode = Long.toString(mkey);
						db.close();

						Log.v("mdb","����ɹ�keyCode����"+keyCode);
						Log.v("mdb","����ɹ�����"+mkey);
						Log.v("mdb","����ɹ�������"+bill_id);

//						Bundle b = new Bundle();
//						b.putInt("flag", 9999);
						Intent intent = new Intent();
						intent.putExtra("pKey", bill_id);
						intent.putExtra("flag", 0);

//						intent.putExtras(b);
						setResult(20, intent);

					}else if (flag == 1){ //�����¼����޸�

						if(edit_status == 1){

							ContentValues values = new ContentValues();
							values.put("billObjecthasAccount", accountId);
							if (zIsunknownAmount ==1) {
								values.put("bk_billOAmountUnknown", zIsunknownAmount); //���浽���ܶ��unknownֵ��1��ʾδ֪N/A,-1��ʾδ����N/A��
								values.put("bk_billOAmount", UNKNOWNZERO);
							} else {
								values.put("bk_billOAmountUnknown", -1);
								values.put("bk_billOAmount",dueAmountFloat);
							}
							
							if( dueDateLong == mbk_billDuedate){
								values.put("bk_billODueDate", dueDateLong);
								values.put("bk_billsDelete", 0);
							}else{
								values.put("bk_billODueDate", dueDateLong);
								values.put("bk_billODueDateNew", mbk_billDuedate);
								values.put("bk_billsDelete", 2);
							}
							if( REPCHECK == 1){

								values.put("bk_billOisRepeat", REPCHECK);
								values.put("bk_billORepeatType", mRepeatUnit);
								values.put("bk_billORepeatNumber", repeatFrequencyString);
								if(isForever == -1 ){
									values.put("bk_billOEndDate", isForever);
								}else if(isForever == -2){
									values.put("bk_billOEndDate", untileDateLong);
								}

							}else if( REPCHECK == 0){
								values.put("bk_billOisRepeat", REPCHECK);
							}
							values.put("bk_billOisVariaable", isFixed);
							values.put("bk_billOAutoPay", isAutoPay);

							if(reminderOnOf== 1){

								values.put("bk_billOisReminder",reminderOnOf);
								values.put("bk_billOReminderDate",remindDateLong); //��������ʱ��ĺ��������ʱ��-(��ǰ����+����ʱ�䣩)
								values.put("bk_billOReminderTime",remindatTime);
							}else{
								values.put("bk_billOisReminder",0);
							}
							values.put("billObjecthasBill",bill_id);
							long mkey = db.insert("BK_BillObject", null, values);
							
							Log.v("mdb","����ʱ�䳤�޸�obj����ɹ�����"+mkey);
							db.close();

							Intent intent = new Intent();
							intent.putExtra("pKey", Integer.parseInt(mkey+""));
							intent.putExtra("flag", 3);
							setResult(20, intent);

						}else if(edit_status == 2){
							
							ContentValues values = new ContentValues();
							values.put("billhasAccount", accountId);
							if (zIsunknownAmount ==1) {
								values.put("bk_billAmountUnknown", zIsunknownAmount); //���浽���ܶ��unknownֵ��1��ʾδ֪N/A,-1��ʾδ����N/A��
								values.put("bk_billAmount", UNKNOWNZERO);
							} else {
								values.put("bk_billAmountUnknown", -1);
								values.put("bk_billAmount",dueAmountFloat);
							}
							values.put("bk_billDuedate", dueDateLong);

							if( REPCHECK == 1){

								values.put("bk_billisRepeat", REPCHECK);
								values.put("bk_billRepeatType", mRepeatUnit);
								values.put("bk_billRepeatNumber", repeatFrequencyString);
								if(isForever == -1 ){
									values.put("bk_billEndDate", isForever);
								}else if(isForever == -2){
									values.put("bk_billEndDate", untileDateLong);
								}

							}else if( REPCHECK == 0){
								values.put("bk_billisRepeat", REPCHECK);
							}
							values.put("bk_billisVariaable", isFixed);
							values.put("bk_billAutoPay", isAutoPay);

							if(reminderOnOf== 1){

								values.put("bk_billisReminder",reminderOnOf);
								values.put("bk_billReminderDate",remindDateLong); //��������ʱ��ĺ��������ʱ��-(��ǰ����+����ʱ�䣩)
								values.put("bk_billReminderTime",remindatTime);
							}else{
								values.put("bk_billisReminder",0);
							}
							String [] ids = {String.valueOf(bill_id)};
							long mkey = db.update("BK_Bill", values, "_id=?" ,ids);
							String keyCode = Long.toString(mkey);
							db.close();

						    billObjThisandFuDelete( bill_id ,1); //ɾ��ǰ��֮������������¼�*********************�����Ƿ�Ҫ����
						    
							Intent intent = new Intent();
							intent.putExtra("pKey", bill_id);
							intent.putExtra("flag", 1);
							setResult(20, intent);
						}

					}else if (flag == 2){

						if(edit_status == 1){

							ContentValues values = new ContentValues();
							values.put("billObjecthasAccount", accountId);
							if (zIsunknownAmount ==1) {
								values.put("bk_billOAmountUnknown", zIsunknownAmount); //���浽���ܶ��unknownֵ��1��ʾδ֪N/A,-1��ʾδ����N/A��
								values.put("bk_billOAmount", UNKNOWNZERO);
							} else {
								values.put("bk_billOAmountUnknown", -1);
								values.put("bk_billOAmount",dueAmountFloat);
							}

							if( dueDateLong == mbk_billDuedate){
								values.put("bk_billODueDate", dueDateLong);
								values.put("bk_billsDelete", 0);
							}else{
								values.put("bk_billODueDate", dueDateLong);
								values.put("bk_billODueDateNew", mbk_billDuedate);
								values.put("bk_billsDelete", 2);
							}
							if( REPCHECK == 1){

								values.put("bk_billOisRepeat", REPCHECK);
								values.put("bk_billORepeatType", mRepeatUnit);
								values.put("bk_billORepeatNumber", repeatFrequencyString);
								if(isForever == -1 ){
									values.put("bk_billOEndDate", isForever);
								}else if(isForever == -2){
									values.put("bk_billOEndDate", untileDateLong);
								}

							}else if( REPCHECK == 0){
								values.put("bk_billOisRepeat", REPCHECK);
							}
							values.put("bk_billOisVariaable", isFixed);
							values.put("bk_billOAutoPay", isAutoPay);

							if(reminderOnOf== 1){

								values.put("bk_billOisReminder",reminderOnOf);
								values.put("bk_billOReminderDate",remindDateLong); //��������ʱ��ĺ��������ʱ��-(��ǰ����+����ʱ�䣩)
								values.put("bk_billOReminderTime",remindatTime);
							}else{
								values.put("bk_billOisReminder",0);
							}
							values.put("billObjecthasBill",bill_id);
							long mkey = db.insert("BK_BillObject", null, values);
							
							Log.v("mdb","���ⵥ������ɹ�����"+mkey);
							db.close();

							Intent intent = new Intent();
							intent.putExtra("pKey", Integer.parseInt(mkey+""));
							intent.putExtra("flag", 3);
							setResult(20, intent);

						}else if(edit_status == 2){

							ContentValues values = new ContentValues();
							values.put("billhasAccount", accountId);
							if (zIsunknownAmount ==1) {
								values.put("bk_billAmountUnknown", zIsunknownAmount); //���浽���ܶ��unknownֵ��1��ʾδ֪N/A,-1��ʾδ����N/A��
								values.put("bk_billAmount", UNKNOWNZERO);
							} else {
								values.put("bk_billAmountUnknown", -1);
								values.put("bk_billAmount",dueAmountFloat);
							}
							values.put("bk_billDuedate", dueDateLong);

							if( REPCHECK == 1){

								values.put("bk_billisRepeat", REPCHECK);
								values.put("bk_billRepeatType", mRepeatUnit);
								values.put("bk_billRepeatNumber", repeatFrequencyString);
								if(isForever == -1 ){
									values.put("bk_billEndDate", isForever);
								}else if(isForever == -2){
									values.put("bk_billEndDate", untileDateLong);
								}

							}else if( REPCHECK == 0){
								values.put("bk_billisRepeat", REPCHECK);
							}
							values.put("bk_billisVariaable", isFixed);
							values.put("bk_billAutoPay", isAutoPay);

							if(reminderOnOf== 1){

								values.put("bk_billisReminder",reminderOnOf);
								values.put("bk_billReminderDate",remindDateLong); //��������ʱ��ĺ��������ʱ��-(��ǰ����+����ʱ�䣩)
								values.put("bk_billReminderTime",remindatTime);
							}else{
								values.put("bk_billisReminder",0);
							}
							String [] ids = {String.valueOf(bill_id)};
							long mkey = db.insert("BK_Bill", null, values); //���µ�ǰ��future���Ѹ������¼�����Ϊһ�������¼�



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


							ContentValues valuesb = new ContentValues();
							valuesb.put("bk_billEndDate", preDuedate); // �޸ĸ����ظ�����ʱ��Ϊ���¼������ǰһ��ʱ��
							db.update("BK_Bill", valuesb, "_id=?" ,ids); //���ԭ�����¼��ĵ���ʱ�䣬�����¼���ֹ

							billObjThisandFuDelete( bill_id ,mbk_billDuedate); //ɾ��ǰ��֮������������¼�*********************�����Ƿ�Ҫ����

							db.close();
							
							Log.v("mdb","�����¼���ǰ�����в���ɹ�����"+mkey);
							Intent intent = new Intent();
							intent.putExtra("pKey", Integer.parseInt(mkey+""));
							intent.putExtra("flag", 1);
							setResult(20, intent);

						}

					}else if (flag == 3){

						if(edit_status == 1){

							int billo_id ;

							if (mMap.containsKey("billObjecthasBill")) {
								billo_id = (Integer)mMap.get("billObjecthasBill");
							}else{
								finish();
								break;
							}

							ContentValues values = new ContentValues();
							values.put("billObjecthasAccount", accountId);
							if (zIsunknownAmount ==1) {
								values.put("bk_billOAmountUnknown", zIsunknownAmount); //���浽���ܶ��unknownֵ��1��ʾδ֪N/A,-1��ʾδ����N/A��
								values.put("bk_billOAmount", UNKNOWNZERO);
							} else {
								values.put("bk_billOAmountUnknown", -1);
								values.put("bk_billOAmount",dueAmountFloat);
							}

							if( dueDateLong == mbk_billDuedate){
								values.put("bk_billODueDate", dueDateLong);
								values.put("bk_billsDelete", 0);
							}else{
								values.put("bk_billODueDate", dueDateLong);
								values.put("bk_billODueDateNew", mbk_billDuedate);
								values.put("bk_billsDelete", 2);
							}

							if( REPCHECK == 1){

								values.put("bk_billOisRepeat", REPCHECK);
								values.put("bk_billORepeatType", mRepeatUnit);
								values.put("bk_billORepeatNumber", repeatFrequencyString);
								if(isForever == -1 ){
									values.put("bk_billOEndDate", isForever);
								}else if(isForever == -2){
									values.put("bk_billOEndDate", untileDateLong);
								}

							}else if( REPCHECK == 0){
								values.put("bk_billOisRepeat", REPCHECK);
							}
							values.put("bk_billOisVariaable", isFixed);
							values.put("bk_billOAutoPay", isAutoPay);

							if(reminderOnOf== 1){

								values.put("bk_billOisReminder",reminderOnOf);
								values.put("bk_billOReminderDate",remindDateLong); //��������ʱ��ĺ��������ʱ��-(��ǰ����+����ʱ�䣩)
								values.put("bk_billOReminderTime",remindatTime);
							}else{
								values.put("bk_billOisReminder",0);
							}
							values.put("billObjecthasBill",billo_id);

							String [] ids = {String.valueOf(bill_id)};
							long mkey = db.update("BK_BillObject", values, "_id=?" ,ids);

							db.close();

							Intent intent = new Intent();
							intent.putExtra("pKey", bill_id);
							intent.putExtra("flag", 3);
							setResult(20, intent);

						}else if(edit_status == 2){

							int billo_id ;

							if (mMap.containsKey("billObjecthasBill")) {
								billo_id = (Integer)mMap.get("billObjecthasBill");
							}else{
								finish();
								break;
							}

							ContentValues values = new ContentValues();
							values.put("billhasAccount", accountId);
							if (zIsunknownAmount ==1) {
								values.put("bk_billAmountUnknown", zIsunknownAmount); //���浽���ܶ��unknownֵ��1��ʾδ֪N/A,-1��ʾδ����N/A��
								values.put("bk_billAmount", UNKNOWNZERO);
							} else {
								values.put("bk_billAmountUnknown", -1);
								values.put("bk_billAmount",dueAmountFloat);
							}
							values.put("bk_billDuedate", dueDateLong);

							if( REPCHECK == 1){

								values.put("bk_billisRepeat", REPCHECK);
								values.put("bk_billRepeatType", mRepeatUnit);
								values.put("bk_billRepeatNumber", repeatFrequencyString);
								if(isForever == -1 ){
									values.put("bk_billEndDate", isForever);
								}else if(isForever == -2){
									values.put("bk_billEndDate", untileDateLong);
								}

							}else if( REPCHECK == 0){
								values.put("bk_billisRepeat", REPCHECK);
							}
							values.put("bk_billisVariaable", isFixed);
							values.put("bk_billAutoPay", isAutoPay);

							if(reminderOnOf== 1){

								values.put("bk_billisReminder",reminderOnOf);
								values.put("bk_billReminderDate",remindDateLong); //��������ʱ��ĺ��������ʱ��-(��ǰ����+����ʱ�䣩)
								values.put("bk_billReminderTime",remindatTime);
							}else{
								values.put("bk_billisReminder",0);
							}
							String [] ids = {String.valueOf(billo_id)}; //׷�ݵ�����id
							long mkey = db.insert("BK_Bill", null, values); //���µ�ǰ��future���Ѹ������¼�����Ϊһ�������¼�

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

							ContentValues valuesb = new ContentValues();
							valuesb.put("bk_billEndDate", preDuedate);
							db.update("BK_Bill", valuesb, "_id=?" ,ids); //���ԭ�����¼��ĵ���ʱ�䣬�����¼���ֹ
							
							Log.v("mmes", "��ȡ��Ҫɾ���obj billo_id�� " +billo_id);
							billObjThisandFuDelete(billo_id ,mbk_billDuedate); //ɾ��ǰ��֮������������¼�***********************************************************
							
							Log.v("mdb","******mbk_billDuedate��"+mbk_billDuedate);
							Log.v("mdb","******parentDueDate(billo_id)��"+parentDueDate(billo_id));
							
							if(mbk_billDuedate == parentDueDate(billo_id) ){
								 billDelete(billo_id); //����¼������ԭ�ȸ���ɾ��
							     Log.v("mmes", "������ɾ�� " +billo_id);
							}
							
							db.close();
							Log.v("mdb","�����¼���ǰ�����в���ɹ�����"+mkey);
							
							Intent intent = new Intent();
							intent.putExtra("pKey", Integer.parseInt(mkey+""));
							intent.putExtra("flag", 1);
							setResult(20, intent);  //���ڸ�ҳ�淵�����ֵ�˵�������ص�ֵΪ20��ʾ�޸ģ�21��ʾɾ��flag���ָı���ֵ
							
						}
					}
					
					if( reminderOnOf==1){ //����ɹ��������������ѵ�
						 Intent service=new Intent(EditBillActivity.this, BillNotificationService.class);   //�ж�����
						 EditBillActivity.this.startService(service);  
					}

					finish();

				}
				break;

			}
		}
	};


}
