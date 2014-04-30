package com.appxy.billkeeper.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appxy.billkeeper.BaseHomeActivity;
import com.appxy.billkeeper.R;
import com.appxy.billkeeper.R.layout;
import com.appxy.billkeeper.R.menu;
import com.appxy.billkeeper.adapter.AccountFragmentAdapter;
import com.appxy.billkeeper.adapter.DialogCategoryAdapter;
import com.appxy.billkeeper.adapter.EditCategoryActivityAdapter;
import com.appxy.billkeeper.db.BillKeeperSql;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class EditCategoryActivity extends BaseHomeActivity {
	
	private DragSortListView mListView;
	private List<Map<String, Object>> dataList ;
	
	private BillKeeperSql bksql;
	private Handler handler;
	private SectionController c ;
	private EditCategoryActivityAdapter ECAAdapter;
	private ListView diaListView;
	private LayoutInflater inflater; 
	
	private AlertDialog alertDialog;
	private AlertDialog alertDialog1;
	
	private Menu mMenu;
    private MenuItem item0;
	private MenuItem item1;
	
	private EditText rename_EditText ;
	
	private DialogCategoryAdapter  dialogCategoryAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_category);
		
//		getActionBar().setDisplayShowHomeEnabled(false);
//		getActionBar().setDisplayShowTitleEnabled(true);
//		int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
//		TextView title = (TextView) findViewById(titleId);
//		title.setTextColor(this.getResources().getColor(R.color.white));
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		inflater= LayoutInflater.from(this);
		dataList = new ArrayList<Map<String, Object>>();
		mListView = (DragSortListView)findViewById(R.id.drag_sort_listview);
		dialogCategoryAdapter = new DialogCategoryAdapter(this);
		ECAAdapter = new EditCategoryActivityAdapter(EditCategoryActivity.this);
		mListView.setAdapter(ECAAdapter);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				 int key =  (Integer) dataList.get(arg2).get("categoryId");
				 String  nameString = (String)dataList.get(arg2).get("bk_categoryName");
				 int position = (Integer) dataList.get(arg2).get("bk_categoryIconName");
							
				Intent intent = new Intent();
				intent.setClass(EditCategoryActivity.this, ChangeCategoryActivity.class);
				intent.putExtra("category_id", key);
				intent.putExtra("category_name", nameString);
				intent.putExtra("category_position", position);
				startActivityForResult(intent, 1);
				
			}
		});
		
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				View  dialogview = inflater.inflate(R.layout.dialog_account_item_operation,null); 
				diaListView = (ListView)dialogview.findViewById(R.id.dia_listview);
				final int key =  (Integer) dataList.get(arg2).get("categoryId");
				diaListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						if (arg2 == 0) {
							
							if (judgeIfhasAccount(key)>0) {
								
								new AlertDialog.Builder(EditCategoryActivity.this)
								.setTitle("Warning! ")
								.setMessage(
								"There are accounts related to this category, delete this category will also delete the accounts. Are you sure to proceed? ")
								.setPositiveButton("Delete",
										new DialogInterface.OnClickListener() {

									@Override	
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										  categoryDelete(key);
										  alertDialog.dismiss();
										  mUpdater();
									}
								})
								.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub

									}

								}).show();
								
							} else {
								
								 categoryDelete(key);
								  alertDialog.dismiss();
								  mUpdater();

							}
							
						} else if (arg2 == 1) {
							
							alertDialog.dismiss();
							ECAAdapter.isChecked(1);
							ECAAdapter.notifyDataSetChanged();
							mListView.setLongClickable(false);
							
							mListView.setDropListener(onDrop); //����listview����ҷ
						    // make and set controller on dslv
							c.setSortEnabled(true);//�ؼ���룬����listview������ҷ*********
						    c = new SectionController(mListView, ECAAdapter);
						    mListView.setFloatViewManager(c);
						    mListView.setOnTouchListener(c);
					        item0.setVisible(true);
					        item1.setVisible(false);
						}
					}
				});
				
				 diaListView.setAdapter(dialogCategoryAdapter);
				 AlertDialog.Builder builder = new AlertDialog.Builder(EditCategoryActivity.this);
				 builder.setView(dialogview);
				 alertDialog = builder.create();
				 alertDialog.show();
				 
				return true;
			}
			
		});
		
		mUpdater();
		
	}
	
	public void mUpdater() {

			getData();
			ECAAdapter.isChecked(-1);
			c=new SectionController(mListView, ECAAdapter);
			ECAAdapter.getAdapterData(dataList);
			ECAAdapter.notifyDataSetChanged();
	}

	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener()
	{
	    @Override
	    public void drop(int from, int to)
	    {
	        if (from != to)
	        {
	        	
	        	Map<String, Object> mMap = dataList.remove(from);
	        	dataList.add(to, mMap);
	        	
	        	ECAAdapter.notifyDataSetChanged(); //
	        	
	        }
	    }

	};
	
  
  private long categoryDelete(int rowid){
		
		bksql = new BillKeeperSql(this); 
		SQLiteDatabase db = bksql.getReadableDatabase();
		String id = rowid+"";
		db.execSQL("PRAGMA foreign_keys = ON ");
		long row=db.delete("BK_Category", "_id = ?", new String[]{id});
		db.close();
		return row;
	}
  
  
  private long categoryUpdate(int rowid, String caName){
	  bksql = new BillKeeperSql(this); 
	  SQLiteDatabase db = bksql.getReadableDatabase();
	  String id = rowid+"";
	  
	  ContentValues cv=new ContentValues();
	  cv.put("bk_categoryName", caName);
	  
	  long row=db.update("BK_Category", cv, "_id = ?", new String[] {id});
	  db.close();
	  return row;
	  
  }
  
  private class SectionController extends DragSortController { //list item ��ҷ

      private EditCategoryActivityAdapter mAdapter;

      DragSortListView mDslv;

      public SectionController(DragSortListView dslv, EditCategoryActivityAdapter adapter) {
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
  
  public int judgeIfhasAccount(int categoryId) {
	  bksql = new BillKeeperSql(this);
   	  SQLiteDatabase db = bksql.getReadableDatabase();
   	  String sql = "select a._id from BK_Account a ,BK_Category b where a.accounthasCategory = b._id and b._id = " + categoryId;
   	  Cursor cursorEA = db.rawQuery(sql, null);
   	  int  cursorSize = cursorEA.getCount();
   	  Log.v("mdb", "cursorSize:"+cursorSize);
   	  return cursorSize;
}
  

	
	public void getData()//��ȡ��䵽listView�����
    {
		dataList.clear();
        Map<String, Object> map;
        bksql = new BillKeeperSql(this);
   	    SQLiteDatabase db = bksql.getReadableDatabase();
   	    
   	    Cursor cursorEA = db.rawQuery("select _id, bk_categoryIconName, bk_categoryName ,bk_categorySequence from BK_Category order by bk_categorySequence ASC" , null);
   	    while (cursorEA.moveToNext()) {
       
            map = new HashMap<String, Object>();
            
            int categoryId = cursorEA.getInt(0);
            int bk_categoryIconName = cursorEA.getInt(1);
            String bk_categoryName = cursorEA.getString(2); 
   		    int bk_categorySequence = cursorEA.getInt(3);
   		    
   		    map.put("categoryId", categoryId);
   		    map.put("bk_categoryIconName", bk_categoryIconName);
            map.put("bk_categoryName", bk_categoryName);
            map.put("bk_categorySequence", bk_categorySequence);
			dataList.add(map);
        }
   	     cursorEA.close();
   	     db.close();
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_category, menu);
		
		this.mMenu = menu;
	    item0 = mMenu.findItem(R.id.confirm);//����actionbar�е�������ť���ɼ�
	    item0.setVisible(false);
	    item1 = mMenu.findItem(R.id.addbill);
	    
		return true;
	}
	
	public void categorySort(List<Map<String, Object>> mData) {
		
		int size = mData.size();
		int a=0;
		String[] mKeyString = new String[size];
		for(Map<String, Object> iMap:mData){
			mKeyString[a] = iMap.get("categoryId").toString();
			a++;
		}
		
		  bksql = new BillKeeperSql(this); 
		  SQLiteDatabase db = bksql.getReadableDatabase();
		  db.beginTransaction();//��ʼ����
		  try {
		     for (int i = 0; i < size; i++) {
		    	  ContentValues cv=new ContentValues();
				  cv.put("bk_categorySequence", i);
				  db.update("BK_Category", cv, "_id = ?", new String[] {mKeyString[i]});
			}
		      db.setTransactionSuccessful();//���ô˷�������ִ�е�endTransaction() ʱ�ύ��ǰ���������ô˷�����ع�����
		  } finally {
		      db.endTransaction();//������ı�־�������ύ���񣬻��ǻع�����
		  } 
		  db.close(); 
		  
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		
		case android.R.id.home:  //���Ͻǰ�ť���ص�id
			finish();
			return true;
			
		case R.id.confirm:
			
			c.setSortEnabled(false);
			
			item0.setVisible(false);
			item1.setVisible(true);
			mListView.setLongClickable(true);
//			item1.setVisible(true);
			Log.v("mtest", "dataList������"+dataList);
			categorySort(dataList);
			mUpdater();
			
			return true;
			
		case R.id.addbill:
			
			new AlertDialog.Builder(EditCategoryActivity.this)
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

//							List<ApplicationInfo> ls = getPackageManager().getInstalledApplications(
//											0);
//							int size = ls.size();
//							ApplicationInfo info = null;
//							for (int i = 0; i < size; i++) {
//
//								if (ls.get(i).packageName.equals("com.android.vending")) {
//									info = ls.get(i);
//								}
//							}
//							if (info != null) {
//								Intent intent = new Intent(
//										Intent.ACTION_VIEW);
//								intent.setData(Uri
//										.parse("market://details?id="+ appName));
//								intent.setPackage(info.packageName);
//								startActivity(intent);
//							} else {
//								startActivity(new Intent(
//										Intent.ACTION_VIEW,
//										Uri.parse("http://play.google.com/store/apps/details?id="
//												+ appName)));
//							}
							
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
			
			return true;
			
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) { 
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 1:
			 if (data != null) {
				mUpdater();
			}
			break;
		}
	}

}
