package com.appxy.billkeeper.fragment;


import android.R.integer;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;

import com.appxy.billkeeper.R;
import com.appxy.billkeeper.activity.AccountDetailActivity;
import com.appxy.billkeeper.activity.AccountoExport;
import com.appxy.billkeeper.activity.AddNewAccountActivity;
import com.appxy.billkeeper.activity.EditAccountActivity;
import com.appxy.billkeeper.activity.EditBillActivity;
import com.appxy.billkeeper.activity.ExportActivity;
import com.appxy.billkeeper.activity.NewBillActivity;
import com.appxy.billkeeper.activity.PaymentActivity;
import com.appxy.billkeeper.adapter.AccountFragmentAdapter;
import com.appxy.billkeeper.adapter.DialogAccountItemAdapter;
import com.appxy.billkeeper.db.BillKeeperSql;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;


  @SuppressLint("ResourceAsColor")
public class AccountFragment extends Fragment{
	  
	 public static DragSortListView listView;
	 public static AccountFragmentAdapter adapter;
	 private BillKeeperSql bksql;
	 private List<Map<String, Object>> dataList ;
	 private ListView diaListView;
	 
	 private AlertDialog alertDialog;
	 private DialogAccountItemAdapter dialogAccountItemAdapter ;
	 public static SectionController c ;
	 private Menu mMenu;
	 public static MenuItem item0;
	 public static MenuItem item1;
	 private LinearLayout accountBac;
	 public static int sortCheck = 0;
	 public static Handler handler;
	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		dialogAccountItemAdapter =  new DialogAccountItemAdapter(getActivity());
		getActivity().getActionBar().setDisplayShowTitleEnabled(true);
		getActivity().setTitle("Account");
		
	}
	
	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener()
	{
	    @Override
	    public void drop(int from, int to)
	    {
	        if (from != to)
	        {
  	        	Map<String, Object> mdata = dataList.remove(from);
	        	dataList.add(to, mdata);
	        	adapter.notifyDataSetChanged(); 
	        	
	        }
	    }

	};
	
	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.drag_sort_listview_account, container, false); 
		
		listView = (DragSortListView)view.findViewById(R.id.drag_sort_listview);
		accountBac = (LinearLayout)view.findViewById(R.id.account_bag);
				
		dataList = new ArrayList<Map<String, Object>>();
		handler = new Handler();
		handler.post(mUpdater);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
			   int key =  (Integer) dataList.get(arg2).get("accountId");
				 
				Intent intent = new Intent();
				intent.putExtra("account_id", key);
				intent.setClass(getActivity(), AccountDetailActivity.class);
				startActivityForResult(intent, 5);
			}
		});
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				 View  dialogview = inflater.inflate(R.layout.dialog_account_item_operation,null); 
				 diaListView = (ListView)dialogview.findViewById(R.id.dia_listview);
				 
				 final int mpositon =  arg2;
				 
				 final int key =  (Integer) dataList.get(arg2).get("accountId");
				 diaListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						if (arg2 == 0) {
							
							Intent intent = new Intent();
							intent.putExtra("account_id", key);
							intent.setClass(getActivity(), EditAccountActivity.class);
							startActivityForResult(intent, 4);
							alertDialog.dismiss();
							
						} else if (arg2 == 1) {
							
							Intent intent = new Intent();
							intent.putExtra("account_id", key);
							intent.putExtra("account_position", mpositon);
							
							intent.setClass(getActivity(), AccountoExport.class);
							startActivityForResult(intent, 3);
							alertDialog.dismiss();
							
						} else if (arg2 == 2) {
							
							if (judgeIfhasBill(key)>0) {
								
								alertDialog.dismiss();
								new AlertDialog.Builder(getActivity())
								.setTitle("Warning")
								.setMessage(
								"There are bills related to this account, delete this account will also delete bills. Are you sure to proceed?")
								.setPositiveButton("Delete",
										new DialogInterface.OnClickListener() {

									@Override	
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										
										alertDialog.dismiss();
										accountDelete(key);
//										getData();
//										adapter = new AccountFragmentAdapter(getActivity(), dataList);
//										listView.setAdapter(adapter);
										handler.post(mUpdater);
									}
								})
								.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub

									}

								}).show();
								
							} else {
								
								alertDialog.dismiss();
								accountDelete(key);
//								getData();
//								adapter = new AccountFragmentAdapter(getActivity(), dataList);
//								listView.setAdapter(adapter);
								handler.post(mUpdater);
							}
							
						}else if (arg2 == 3) {
							
							sortCheck = 1;
							alertDialog.dismiss();
							adapter.isChecked(1);
							adapter.notifyDataSetChanged();
							listView.setLongClickable(false);
							
//							item1.setVisible(false);
							listView.setDropListener(onDrop); //����listview����ҷ
						    // make and set controller on dslv
							c.setSortEnabled(true);//�ؼ���룬����listview������ҷ*********
						    c = new SectionController(listView, adapter);
					        listView.setFloatViewManager(c);
					        listView.setOnTouchListener(c);
					        item0.setVisible(true);
					        item1.setVisible(false);
						}				
					}
				});
				 
				 diaListView.setAdapter(dialogAccountItemAdapter);
				 AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				 builder.setView(dialogview);
				 alertDialog = builder.create();
				 alertDialog.show();
				 
				return true;
			}
		});
		
		return view;
	}
	
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		sortCheck = 0;
		super.onDestroy();
	}

	public Runnable mUpdater = new Runnable() {

		@Override
		public void run() {
			getData();
			
			if (dataList.size()!=0) {
				adapter = new AccountFragmentAdapter(getActivity(), dataList);
			    adapter.isChecked(-1);
				c=new SectionController(listView, adapter);
		        listView.setAdapter(adapter);
		        accountBac.setVisibility(View.INVISIBLE);
		        
			} else {
				adapter = new AccountFragmentAdapter(getActivity(), dataList);
			    adapter.isChecked(-1);
				c=new SectionController(listView, adapter);
		        listView.setAdapter(adapter);
//		        accountBac.setBackgroundColor(Color.rgb(255, 255, 255));
				accountBac.setVisibility(View.VISIBLE);
			}
		}

	};
	
	public class SectionController extends DragSortController { //list item ��ҷ

        private AccountFragmentAdapter mAdapter;

        DragSortListView mDslv;

        public SectionController(DragSortListView dslv, AccountFragmentAdapter adapter) {
            super(dslv);
            setDragHandleId(R.id.Sort_ImageView);
            mDslv = dslv;
            mAdapter = adapter;
        }

        @Override
        public int startDragPosition(MotionEvent ev) {
            int res = super.dragHandleHitPosition(ev);
            int width = mDslv.getWidth();
           return res;
        }

        @Override
        public View onCreateFloatView(int position) {

            View v = mAdapter.getView(position, null, mDslv);
            v.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_handle_section1));
            v.getBackground().setLevel(10000);
            return v;
        }
        
        private int origHeight = -1;

        @Override
        public void onDragFloatView(View floatView, Point floatPoint, Point touchPoint) {
          
        }

        @Override
        public void onDestroyFloatView(View floatView) {
            //do nothing; block super from crashing
        }

    }
	
	private long accountDelete(int rowid){
		
		bksql = new BillKeeperSql(getActivity()); 
		SQLiteDatabase db = bksql.getReadableDatabase();
		String id = rowid+"";
		db.execSQL("PRAGMA foreign_keys = ON ");
		long row=db.delete("BK_Account", "_id = ?", new String[]{id});
		db.close();
		return row;
	}
	
	
	public void updateSort(String from_id,String from, String to_id,String to) { //������ݿ��е������ֶ�
		
		bksql = new BillKeeperSql(getActivity()); 
		SQLiteDatabase db = bksql.getReadableDatabase();
		
		ContentValues cv=new ContentValues();
		cv.put("bk_accountSequence", from);	
		String row = from_id;
		db.update("BK_Account", cv, "_id = ?", new String[] {row});
		
		ContentValues cv1=new ContentValues();
		cv1.put("bk_accountSequence", to);	
		String row1 = to_id;
		db.update("BK_Account", cv1, "_id = ?", new String[] {row1});
		db.close();
		
	}
	
	public List<Map<String, Object>> getData()//��ȡ��䵽listView�����
    {
		
        dataList.clear();
        Map<String, Object> map;
        
        bksql = new BillKeeperSql(getActivity());
   	    SQLiteDatabase db = bksql.getReadableDatabase();
   	    
   	    Cursor cursorEA = db.rawQuery("select BK_Account._id, BK_Account.bk_accountName, BK_Account.bk_accountSequence,BK_Category.bk_categoryName , BK_Category.bk_categoryIconName from BK_Account,BK_Category where BK_Account.accounthasCategory = BK_Category._id order by BK_Account.bk_accountSequence ASC", null);
   	    while (cursorEA.moveToNext()) {
       
            map = new HashMap<String, Object>();
            
            int accountId = cursorEA.getInt(0);
            String accountName = cursorEA.getString(1); 
   		    int accountSort = cursorEA.getInt(2);
   		    String categoryName = cursorEA.getString(3);
   		    int categoryIcon = cursorEA.getInt(4);
   		    
   		    map.put("accountId", accountId);
            map.put("accountName", accountName);
            map.put("accountSort", accountSort);
            map.put("categoryName", categoryName);
			map.put("iconPosition", categoryIcon);
		
			dataList.add(map);
        }
   	     cursorEA.close();
   	     db.close();
        return dataList;
    }
	
public void accountSort(List<Map<String, Object>> mData) {
		
		int size = mData.size();
		int a = 0;
		String[] mKeyString = new String[size];
		for(Map<String, Object> iMap:mData){
			mKeyString[a] = iMap.get("accountId").toString();
			a++;
		}
		
		  bksql = new BillKeeperSql(getActivity()); 
		  SQLiteDatabase db = bksql.getReadableDatabase();
		  db.beginTransaction();//��ʼ����
		  try {
		     for (int i = 0; i < size; i++) {
		    	  ContentValues cv=new ContentValues();
				  cv.put("bk_accountSequence", i);
				  db.update("BK_Account", cv, "_id = ?", new String[] {mKeyString[i]});
			}
		      db.setTransactionSuccessful();//���ô˷�������ִ�е�endTransaction() ʱ�ύ��ǰ���������ô˷�����ع�����
		  } finally {
		      db.endTransaction();//������ı�־�������ύ���񣬻��ǻع�����
		  } 
		  db.close(); 
		  
	}

	
	public void onActivityResult(int requestCode, int resultCode, Intent data) { 
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 3:
			 if (data != null) {
				 alertDialog.dismiss();
			}
			break;
			
		case 4:
			 if (data != null) {
				 alertDialog.dismiss();
//				 getData();
//			     adapter = new AccountFragmentAdapter(getActivity(), dataList);
//			     listView.setAdapter(adapter);
				 handler.post(mUpdater);

			}
		case 0:
			 if (data != null) {
//				 getData();
//			     adapter = new AccountFragmentAdapter(getActivity(), dataList);
//			     listView.setAdapter(adapter);
				 handler.post(mUpdater);

			}
		case 5:
			 if (data != null) {
//				 getData();
//			     adapter = new AccountFragmentAdapter(getActivity(), dataList);
//			     listView.setAdapter(adapter);
				 handler.post(mUpdater);

			}
			 
			break;

		}
	}
	
	public int judgeIfhasBill(int accountId) {
		  bksql = new BillKeeperSql(getActivity());
	   	  SQLiteDatabase db = bksql.getReadableDatabase();
	   	  String sql = "select a._id from BK_Bill a,BK_Account b where a.billhasAccount = b._id and b._id = " + accountId;
	   	  Cursor cursorEA = db.rawQuery(sql, null);
	   	  int  cursorSize = cursorEA.getCount();
	   	  Log.v("mdb", "cursorSize:"+cursorSize);
	   	  return cursorSize;
	}
    
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		this.mMenu = menu;
		inflater.inflate(R.menu.confirm, menu);
		
	    item0 = mMenu.findItem(R.id.confirm);//����actionbar�е�������ť���ɼ�
	    item0.setVisible(false);
	    item1 = mMenu.findItem(R.id.addbill);
	    
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.confirm:
			
			c.setSortEnabled(false);
			sortCheck = 0;
			item0.setVisible(false);
			item1.setVisible(true);
			listView.setLongClickable(true);
			accountSort(dataList);
			handler.post(mUpdater);
//			item1.setVisible(true);
			return true;
			
		case R.id.addbill:
			Intent intent = new Intent();
			intent.setClass(getActivity(), AddNewAccountActivity.class);
			startActivityForResult(intent, 0);
			return true;
			
		}
		return super.onOptionsItemSelected(item);
	}

	  
	  
}
