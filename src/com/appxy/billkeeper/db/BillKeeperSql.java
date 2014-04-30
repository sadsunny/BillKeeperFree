package com.appxy.billkeeper.db;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.R.integer;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;
import android.widget.Adapter;

public class BillKeeperSql extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1; // ��ݿ�İ汾��
	private static final String DATABASE_NAME = "billkeeperforandroid_v1"; // ��ݿ�����
	public static final String ZcategoriesTABLE_NAME = "BK_Category"; // �������
	private SQLiteDatabase db;
	public BillKeeperSql(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		/*
		 * �ο�api�ĵ���������Թر�ʱ�����������дͬʱ���У�ͨ�� �� ����֤������ʱ��������һ��д�߳��������߳�ͬʱ��һ��SQLiteDatabase�������á�
		ʵ��ԭ����д������ʵ����һ���������ļ�������ԭ��ݿ��ļ�������д��ִ��ʱ������Ӱ���������������������ԭ����ļ�
		����д������ʼ֮ǰ�����ݡ���д����ִ�гɹ��󣬻���޸ĺϲ���ԭ��ݿ��ļ�����ʱ���������ܶ����޸ĺ�����ݡ����������Ѹ����ڴ档
		��������̶߳�д����ͽ���ˣ���ϧֻ����API 11 ����ʹ�á�
		 */
//		if( Build.VERSION.SDK_INT >= 11){   
//            getWritableDatabase().enableWriteAheadLogging();  
//		}
	}

	public BillKeeperSql(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
//		if(  Build.VERSION.SDK_INT >= 11){  
//            getWritableDatabase().enableWriteAheadLogging();  
//		}
	}
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		this.db=db;
		
		db.execSQL("CREATE TABLE BK_Account ( _id INTEGER PRIMARY KEY AUTOINCREMENT , bk_accountBool1 INTEGER ,bk_accountDate DATE,bk_accountMemo TEXT,bk_accountName TEXT,bk_accountNo TEXT,bk_accountString1 TEXT,bk_accountString2 TEXT,bk_accountWebsite TEXT,bk_pk INTEGER,cb_accountDate1 DATE,cb_accountPhoneNo TEXT,accounthasPayment INTEGER ,accounthasCategory INTEGER ,accounthasBill INTEGER ,accounthasBillTwo INTEGER ,bk_accountSequence INTEGER , FOREIGN KEY(accounthasCategory) REFERENCES BK_Category(_id) ON DELETE CASCADE )");
		
		db.execSQL("CREATE TABLE BK_AppDBV ( _id INTEGER PRIMARY KEY AUTOINCREMENT ,bk_vNumber TEXT)");
		
		db.execSQL("CREATE TABLE BK_Bill ( _id INTEGER PRIMARY KEY AUTOINCREMENT ,bk_bilBool1 INTEGER,bk_billAmount Double,bk_billAmountUnknown INTEGER,bk_billAutoPay INTEGER,bk_billBool2 INTEGER,bk_billDate1 Date,bk_billDateAutoCreate Date,bk_billDuedate Date,bk_billEndDate Date,bk_billisReminder INTEGER,bk_billisRepeat INTEGER,bk_billisVariaable INTEGER,bk_billNoteReserved TEXT,bk_billReminderDate TEXT,bk_billReminderTime Date,bk_billRepeatNumber INTEGER,bk_billRepeatType TEXT,bk_billString1 TEXT,bk_billString2 TEXT,bk_billString3 TEXT,bk_pk INTEGER,billhasPayment INTEGER,billhasBillO INTEGER,billhasAccount INTEGER , FOREIGN KEY(billhasAccount) REFERENCES BK_Account(_id) ON DELETE CASCADE )");
	
		db.execSQL("CREATE TABLE BK_BillObject ( _id INTEGER PRIMARY KEY AUTOINCREMENT ,bk_billsDelete INTEGER,bk_billOAmount Double,bk_billOAmountUnknown INTEGER,bk_billOAutoPay INTEGER,bk_billOBool2 INTEGER,bk_billODate1 Date,bk_billODueDate Date,bk_billODueDateNew Date,bk_billOEndDate Date,bk_billOisReminder INTEGER,bk_billOisRepeat INTEGER,bk_billOisVariaable INTEGER,bk_billONoteReserved TEXT,bk_billOReminderDate TEXT,bk_billOReminderTime Date,bk_billORepeatNumber INTEGER,bk_billORepeatType TEXT,bk_billOString1 TEXT,bk_billOString2 TEXT,bk_billOString3 TEXT,bk_pk Integer,billObjecthasPayment Integer,billObjecthasBill Integer,billObjecthasAccount Integer , FOREIGN KEY(billObjecthasAccount) REFERENCES BK_Account(_id) ON DELETE CASCADE , FOREIGN KEY(billObjecthasPayment) REFERENCES BK_Payment(_id) ON DELETE CASCADE)");
	
		db.execSQL("CREATE TABLE BK_Category ( _id INTEGER PRIMARY KEY AUTOINCREMENT ,bk_categoryBool1 INTEGER,bk_categoryBool2 INTEGER,bk_categoryDate1 Date,bk_categoryIconName TEXT,bk_categoryInt1 Integer,bk_categoryName TEXT,bk_categoryString1 TEXT,bk_categoryString2 TEXT,bk_categoryString3 TEXT,bk_categoryUptime Date,bk_nsRecord INTEGER,categoryHasAccount Integer, bk_categorySequence Integer)");
	
		db.execSQL("CREATE TABLE BK_Payment ( _id INTEGER PRIMARY KEY AUTOINCREMENT ,bk_payAmount Double,bk_payBool1 INTEGER,bk_payBool2 INTEGER,bk_payBool3 INTEGER,bk_payConfigNumber TEXT,bk_payDate Date,bk_payMemo TEXT,bk_payMode TEXT,bk_payString1 TEXT,bk_payString2 TEXT,bk_payString3 TEXT,paymenthasBillO Integer,paymenthasBill Integer,paymenthasAccount Integer,FOREIGN KEY(paymenthasBill) REFERENCES BK_Bill(_id) ON DELETE CASCADE,FOREIGN KEY(paymenthasBillO) REFERENCES BK_BillObject(_id) ON DELETE CASCADE)");
	
		db.execSQL("CREATE TABLE BK_Seeting ( _id INTEGER PRIMARY KEY AUTOINCREMENT ,bk_settingBadge INTEGER,bk_settingBool2Reserved INTEGER,bk_settingBool3Reserved INTEGER,bk_settingBoolReserved INTEGER,bk_settingCurrency TEXT,bk_settingDateFormatter TEXT,bk_settingDateReserved Date,bk_settingPasscode TEXT,bk_settingString2Reserved TEXT,bk_settingString3Reserved TEXT,bk_settingString4Reserved TEXT,bk_settingString5Reserved TEXT,bk_settingString6Reserved TEXT,bk_settingStringReserved TEXT)");
	    
		categoryIni( 4, "Credit Cards", System.currentTimeMillis(),1);
		categoryIni( 12, "Loans", System.currentTimeMillis(),2);
		categoryIni( 17, "Rent", System.currentTimeMillis(),3);
		categoryIni( 24, "Utilities:Water", System.currentTimeMillis(),4);
		
		categoryIni( 7, "Utilities:Electricity", System.currentTimeMillis(),5);
		categoryIni( 8, "Utilities:Gas", System.currentTimeMillis(),6);
		categoryIni( 21, "Utilities:Telephone", System.currentTimeMillis(),7);
		categoryIni( 23, "Utilities:Cable TV", System.currentTimeMillis(),8);
		categoryIni( 11, "Utilities:Internet", System.currentTimeMillis(),9);
		categoryIni( 14, "Others", System.currentTimeMillis(),10);
		settingIni(0,148);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	public long settingIni(int badge ,int currency){
		ContentValues cv=new ContentValues();
		cv.put("bk_settingBadge", badge);
		cv.put("bk_settingCurrency", currency);
		long row= db.insert("BK_Seeting", null, cv);
		return row;
	}
	
	
	public long categoryIni(int categoryIconName,String category,long date,int sort){//����category
//		SQLiteDatabase db=this.getWritableDatabase();
		ContentValues cv=new ContentValues();
		db.execSQL("PRAGMA foreign_keys = ON ");
		cv.put("bk_categoryIconName", categoryIconName);
		cv.put("bk_categoryName", category);
		cv.put("bk_categoryUptime", date);
		cv.put("bk_categorySequence", sort);
		long row= db.insert(ZcategoriesTABLE_NAME, null, cv);
		return row;
	}
	

}
