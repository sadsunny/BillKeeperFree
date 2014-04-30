package com.appxy.billkeeper.activity;

/**
 * 
 * 
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
import com.appxy.billkeeper.db.BillKeeperSql;
import com.appxy.billkeeper.service.BillNotificationService;
import com.appxy.billkeeper.service.BillPastDueService;
import com.appxy.billkeeper.service.PastDueReceiver;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.R.bool;
import android.R.integer;
import android.R.string;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import android.content.res.Resources;

public class NewBillActivity extends BaseHomeActivity {

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
	
	private int uYear;
	private int uMonth;
	private int uDay;

	private static int CHECKED = -1;//�ж��Ƿ��dueAmount������м���
	private String myDate;

	private String remindMeString;
	
	private Spinner repeatFrequencySpinner; //repeat dialog�е��ظ�Ƶ��
	private Spinner repeatUnitSpinner;		//�ظ���Ԫ
	private Spinner isForeverSpinner;       //�ظ�����
	private TextView repeatUnitTextView;	//�ظ���Ԫ
	private Button repeatUntilButton;		//�ظ���ֹ����
	private Spinner isFixedSpinner;			//����Ƿ�̶�
	private TextView everyTextView;
	
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
	private int remindDateLong = 1; //bill����ʱ�䣨����ʱ��-before����Ȼ����������time������
	private long remindatTime;  //���ѵ�ʱ��
	
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
	
	private int remindSpinnerPosition = 0; //������һ��spinner�����λ��
	private int repeatFrequencyPositon = 0;
	private int repeatUnitPositon  = 0;
	private int isForeverPosition  = 0;
	private int isFixedPosition  = 0;
	
	private int TeremindSpinnerPosition ; //��ʱ���� �������ϴε���ظ���״̬
	private int TerepeatFrequencyPositon;
	private int TerepeatUnitPositon ;
	private int TeisForeverPosition ;
	private int TeisFixedPosition;
	private int TeREPCHECK;
	private int TereminderOnOf ;
	
	private AlertDialog selectDialog ; 
	private  Calendar timec;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_bill);
		
		remindMeString = this.getResources().getStringArray(R.array.reminder)[0];
		
		timec = Calendar.getInstance();
		timec.set(Calendar.HOUR_OF_DAY, 8);
		timec.set(Calendar.MINUTE, 0);
		mHour = timec.get(Calendar.HOUR_OF_DAY);  
	    mMinute = timec.get(Calendar.MINUTE); 
	    
		inflater = LayoutInflater.from(NewBillActivity.this); //�Զ���actionbar
		ActionBar mActionBar = getActionBar();
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		View customActionBarView = inflater.inflate(R.layout.activity_new_bill_custom_actionbar,null,false);
		
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setCustomView(customActionBarView,lp);
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		
//		 mActionBar.setBackgroundDrawable(new ColorDrawable(Color.argb(255, 114, 189, 251)));
//		 int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
//		 TextView title = (TextView) findViewById(titleId);
//		 title.setTextColor(this.getResources().getColor(R.color.white));
		 
		LinearLayout1 = (LinearLayout)findViewById(R.id.mLinearLayout1); //������ռedittext�Ľ���
		LinearLayout1.setFocusable(true);
		LinearLayout1.setFocusableInTouchMode(true);
		
		accountNa = (Button) findViewById(R.id.accountNa);
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
						intent.setClass(NewBillActivity.this,
								AddNewAccountActivity.class);
						startActivityForResult(intent, 0);
					}
				});
				
				getData();
				AlertDialog.Builder selectAccountDialog = new AlertDialog.Builder( //��һ��account��dialog
						NewBillActivity.this);
				selectAccountDialog.setTitle("Select Account");
				selectAccountDialog.setView(view);
				selectAccountDialog.setSingleChoiceItems(accountNameArray, accountPosition,new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int item) {
						accountPosition = item ;
						Log.v("mtest", "�����"+item);
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
		
		autoPaySwitch = (Switch)findViewById(R.id.autoPayswitch); //�Զ�֧��
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
				dueAmonut = (EditText) findViewById(R.id.dueAmount);
				dueAmonut.setText("N/A");
				zIsunknownAmount = 1;
				isdue = 1; //����Ϊ���Թ���
				
				((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(NewBillActivity.this
								.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS); //��������
				dueAmonut.setSelection(dueAmonut.length());
				unknownButton.setVisibility(View.GONE);
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
							
							if (isdue == 1) {
								dueAmonut.setText("0.00");
							}
							isdue = 0;
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

		dueDate = (Button) findViewById(R.id.dueDate);
		dueDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DatePickerDialog DPD = new DatePickerDialog( // �ı�theme
						new ContextThemeWrapper(NewBillActivity.this,
								android.R.style.Theme_Holo_Light),
						mDateSetListener, mYear, mMonth, mDay);
				DPD.setTitle("Due Date");
				DPD.show();
			}
		});

		repeating = (Button) findViewById(R.id.repeating);
		int repeatTe = 0; //������ʱ������ֻ�е��û����done��ʱ�򣬸�ҳ�����õ���ݲŻ���Ч���´ν����ʱ����ǵ�ǰ״̬
		repeating.setOnClickListener(new OnClickListener() {// repeating��ȡ����
					@Override
					public void onClick(View v)  {
						// TODO Auto-generated method stub
						 {

							View view = inflater.inflate(R.layout.dialog_repeating, null);
						
							repeatUnitSpinner = (Spinner) view.findViewById(R.id.repeat_type_spinner);
							repeatFrequencySpinner = (Spinner) view.findViewById(R.id.repeat_frequency_spinner);
							repeatUnitTextView = (TextView)view.findViewById(R.id.typetextview);
							isForeverSpinner = (Spinner) view.findViewById(R.id.is_forever_spinner);
							repeatUntilButton = (Button)view.findViewById(R.id.repeating_until);
							isFixedSpinner = (Spinner) view.findViewById(R.id.is_fixed_spinner);
							everyTextView = (TextView) view.findViewById(R.id.everytextView);
							
							everyTextView.setEnabled(false);
							repeatUnitSpinner.setEnabled(false);
							repeatFrequencySpinner.setEnabled(false);
							repeatUnitTextView.setEnabled(false);
							isForeverSpinner.setEnabled(false);
							repeatUntilButton.setEnabled(false);
							isFixedSpinner.setEnabled(false);
							repeatUntilButton.setTextColor(android.graphics.Color.GRAY);
							
							repeatingSwitch = (Switch)view.findViewById(R.id.is_repeat_switch);
							
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
//										REPCHECK = 1;
										
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
//										REPCHECK = 0;
										
										TeREPCHECK = 0;
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
											NewBillActivity.this,
											R.array.repeat_requency,
											R.layout.spinner_item);
							adapter.setDropDownViewResource(R.layout.spinner_drop_item);
							repeatFrequencySpinner.setAdapter(adapter);
							
							TerepeatFrequencyPositon = repeatFrequencyPositon;
							repeatFrequencySpinner.setSelection(repeatFrequencyPositon, true);
							
							repeatFrequencySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

								@Override
								public void onItemSelected(AdapterView<?> arg0,
										View arg1, int arg2, long arg3) {
									// TODO Auto-generated method stub
//									repeatFrequencyPositon = arg2;
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
											NewBillActivity.this,
											R.array.repeat_unit,
											R.layout.spinner_item);
							adapter1.setDropDownViewResource(R.layout.spinner_drop_item);
							repeatUnitSpinner.setAdapter(adapter1);
							
							repeatUnitSpinner.setSelection(repeatUnitPositon, true);
							TerepeatUnitPositon = repeatUnitPositon;
							
								repeatUnitTextView.setText(unitArray[repeatUnitPositon]);
						
							repeatUnitSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

								@Override
								public void onItemSelected(AdapterView<?> arg0,
										View arg1, int arg2, long arg3) {
									// TODO Auto-generated method stub
									
										repeatUnitTextView.setText(unitArray[arg2]);
									
									mRepeatUnit = mUnitdate[arg2]; //1 2 3 4
//									repeatUnitPositon = arg2;
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
									NewBillActivity.this,
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
//						isForeverPosition = arg2;
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
							NewBillActivity.this,
							R.array.amount_type,
							R.layout.spinner_item);
			adapter3.setDropDownViewResource(R.layout.spinner_drop_item);
			isFixedSpinner.setAdapter(adapter3);
			
			isFixedSpinner.setSelection(isFixedPosition, true);
			TeisFixedPosition = isFixedPosition;
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
//					isFixedPosition = arg2;
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
											NewBillActivity.this,
											android.R.style.Theme_Holo_Light),
									mDateSetListenerUntil,
									uYear, uMonth, uDay);
							DPD1.setTitle("Until");
							DPD1.show();
						}
					});

					
							AlertDialog.Builder repeatingDialog = new AlertDialog.Builder(
									NewBillActivity.this);
							repeatingDialog.setView(view);
							repeatingDialog.setPositiveButton("Done",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											if (repeatingSwitch.isChecked()) {
												
												REPCHECK = TeREPCHECK;
												repeatFrequencyPositon = TerepeatFrequencyPositon;
												repeatUnitPositon = TerepeatUnitPositon;
												isForeverPosition = TeisForeverPosition;
												isFixedPosition  =TeisFixedPosition;
												
										String  mUnitTString = repeatUnitTextView.getText().toString();
										String duedateString = repeatUntilButton.getText().toString();
										
										repeatFrequencyString =  repeatFrequencySpinner.getSelectedItem().toString();
							         	repeatUnitString = repeatUnitSpinner.getSelectedItem().toString();
							         	if (isForever == -1) {
							         		repeating.setText("Every "+ repeatFrequencyString+ " " + mUnitTString);
										}if(isForever == -2){
											repeating.setText("Every "+ repeatFrequencyString+ " " + mUnitTString+ " ,Until "+duedateString);
										}
										
											}else {
												REPCHECK = TeREPCHECK;
												repeating.setText("No repeating bill");
											}
										}
									});
							repeatingDialog.show();
							
//							Calendar c = Calendar.getInstance();
//							mYear = c.get(Calendar.YEAR);
//							mMonth = c.get(Calendar.MONTH);
//							mDay = c.get(Calendar.DAY_OF_MONTH);
							updateDisplayUntil();
						}
					}
				});

		remindMe = (Button) findViewById(R.id.reminder);
		remindMe.setOnTouchListener(new OnTouchListener() {// remindMe��ȡ����
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (MotionEvent.ACTION_DOWN == event.getAction()) {
					
					LayoutInflater flater = LayoutInflater
							.from(NewBillActivity.this);
					View view = flater.inflate(R.layout.dialog_remindme, null);
					
					reminderMeTextView = (TextView)view.findViewById(R.id.textView1);
					reminderAtTextView = (TextView)view.findViewById(R.id.textView2);
					remindAtEditText = (EditText) view.findViewById(R.id.remindatedittext); 
					reminderMeSpinner = (Spinner) view.findViewById(R.id.spinner1); //remind me Spinner����
					remindMeSwitch = (Switch)view.findViewById(R.id.switch1);
					
					reminderMeTextView.setEnabled(false);
					reminderAtTextView.setEnabled(false);
					remindAtEditText.setEnabled(false);
					reminderMeSpinner.setEnabled(false);
					
					TereminderOnOf = reminderOnOf;
					if (reminderOnOf == 1) { //����֮ǰ��״̬
						
						remindMeSwitch.setChecked(true);
						reminderMeTextView.setEnabled(true);
						reminderAtTextView.setEnabled(true);
						remindAtEditText.setEnabled(true);
						reminderMeSpinner.setEnabled(true);
						Log.v("mdb","���ѱ�����ֶ�"+remindTimeString+"���λ�ã�"+remindSpinnerPosition);
						
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
							.createFromResource(NewBillActivity.this,
									R.array.reminder,
									R.layout.spinner_item);
					adapter.setDropDownViewResource(R.layout.spinner_drop_item);
					reminderMeSpinner.setAdapter(adapter);
					
					reminderMeSpinner.setSelection(remindSpinnerPosition, true);
					final String[] frequencyString = {"1","3","7","30"};
					
					TeremindSpinnerPosition =  remindSpinnerPosition ;
					remindBefore = frequencyString[remindSpinnerPosition];
					reminderMeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							// TODO Auto-generated method stub
							remindMeString = NewBillActivity.this.getResources().getStringArray(R.array.reminder)[arg2];
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
										new ContextThemeWrapper(NewBillActivity.this,android.R.style.Theme_Holo_Light),
										mTimeSetListener, mHour, mMinute, false);
								DPD1.setTitle("Remind At");
								DPD1.show();
							}
							return false;
						}
					});

					AlertDialog.Builder remindMeDialog = new AlertDialog.Builder(
							NewBillActivity.this);
					remindMeDialog.setView(view);

					remindMeDialog.setPositiveButton("Done",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									remindSpinnerPosition = TeremindSpinnerPosition;
									reminderOnOf = TereminderOnOf;
									
									if (remindMeSwitch.isChecked()) {
										remindMe.setText(remindMeString+" , at "+remindTimeString);
									} else {
										remindMe.setText("No reminder");
									}
									Log.v("mtake", "���ѵľ���ʱ�䣺"+remindatTime);
									Log.v("mtake", "remindBefore���ѵľ���ʱ�䣺"+remindBefore);
									if (remindBefore != null && remindBefore.length() != 0) {
										
//										long remindwhichDay =(long)(dueDateLong - (Integer.parseInt(remindBefore))*24*60*60*1000L); //�������ѵľ���ʱ��
//										
//										Date whichDate = new Date(remindwhichDay);
//										SimpleDateFormat adFormat = new SimpleDateFormat("MM-dd-yyyy");
//										String whichString = adFormat.format(whichDate);
//										String whichDayTimeString  = whichString+" "+mHour+":"+mMinute+":"+"00";
//										
//										Log.v("mtake", whichDayTimeString+"����Сʱ���Ӻ��ʱ��");
//										
//										Calendar c = Calendar.getInstance();
//										try {
//											c.setTime(new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").parse(whichDayTimeString));
//										} catch (ParseException e) {
//											// TODO Auto-generated catch block
//											e.printStackTrace();
//										}
										remindDateLong = Integer.parseInt(remindBefore);
										remindatTime = mHour*60*60*1000 + mMinute*60*1000;
										Log.v("mtake", "���ѵľ���ʱ�䣺"+remindatTime);
									}
									
								}
							});
					remindMeDialog.show();
			        updateDisplayTime();
				}
				return false;
			}
		});

		Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		
		uYear = c.get(Calendar.YEAR);
		uMonth = c.get(Calendar.MONTH);
		uDay = c.get(Calendar.DAY_OF_MONTH);
		
		updateDisplay();
		
		View cancelActionView = customActionBarView.findViewById(R.id.action_cancel);
        cancelActionView.setOnClickListener(mActionBarListener);
        View doneActionView = customActionBarView.findViewById(R.id.action_done);
        doneActionView.setOnClickListener(mActionBarListener);
        
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
		Cursor cursorCC = db.rawQuery("select bk_accountName, _id from BK_Account order by BK_Account.bk_accountSequence ASC", null);
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
	 

	 private final View.OnClickListener mActionBarListener = new View.OnClickListener() { //����actionbar����
	        @Override
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
	        			Log.v("mtest", "msg"+checkAuto);
	                } catch (NumberFormatException e) {
	                	checkAuto = 1;
	                }
	                
	    			if (accountName.length() == 0) {
	    				
	    				new AlertDialog.Builder(NewBillActivity.this)
	    						.setTitle("Warning! ")
	    						.setMessage("Please make sure the account name is not empty! ")
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
	    				
	    				new AlertDialog.Builder(NewBillActivity.this)
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
	    				bksql=new BillKeeperSql(NewBillActivity.this);
	    				SQLiteDatabase db = bksql.getWritableDatabase();
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
	    					if(isForever == -1){
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
	    				}
	    				Log.v("mdb","����reminderOnOf��"+reminderOnOf);
	    				Log.v("mdb","remindDateLong��"+remindDateLong);
	    				Log.v("mdb","����ʱ��remindatTime��"+remindatTime);
	    				
	    				long mkey = db.insert("BK_Bill", null, values);
	    				String keyCode = Long.toString(mkey);
	    				db.close();
	    				
	    				Intent intent = new Intent();
	    				intent.putExtra("pKey", keyCode);
						setResult(2, intent);
						finish();
						
						if(mkey > 0 && reminderOnOf==1){ //����ɹ��������������ѵ�
							
							 Log.v("position", "启动服务");
							 
							 Intent service=new Intent(NewBillActivity.this, BillNotificationService.class);   //�ж�����
							 NewBillActivity.this.startService(service);  
						}
	    			}
	    			break;
	    			
	        	}
	        }
	    };
	    
	    
}
