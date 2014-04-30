package com.appxy.billkeeper.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appxy.billkeeper.BaseHomeActivity;
import com.appxy.billkeeper.R;
import com.appxy.billkeeper.R.layout;
import com.appxy.billkeeper.R.menu;
import com.appxy.billkeeper.adapter.CategoriesActivityAdapter;
import com.appxy.billkeeper.adapter.SearchActivityAdapter;
import com.appxy.billkeeper.db.BillKeeperSql;

import android.os.Bundle;
import android.R.integer;
import android.R.string;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

public class CategoriesActivity extends BaseHomeActivity {
	
	private BillKeeperSql bksql;
	private ListView categoryListView;
	private CategoriesActivityAdapter adapter;
	private ModeCallback mCallback;
	private String[] categoryKey;
	private List<Map<String, Object>> mData;
	private List<Map<String, Object>> mKeyPosition;
	private EditText editCategoryEditText ;
	
	private Menu mMenu ;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_categories);
		mData= getDate();
		mCallback = new ModeCallback();
		mCallback.getData(keyDate());
		
		categoryListView = (ListView)findViewById(R.id.mCategoryMistView);
		adapter = new CategoriesActivityAdapter(this,mData);
		categoryListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		categoryListView.setMultiChoiceModeListener(mCallback);
		
		categoryListView.setAdapter(adapter);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
	}
	
	private class ModeCallback implements ListView.MultiChoiceModeListener {
		
		private List<Map<String, Object>> mDataList ;
		
		public List<Map<String, Object>> getData (List<Map<String, Object>> mData){
			
			this.mDataList= mData;
			return mDataList;
		}
		
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.categories_delete_edit, menu);
            mMenu = menu; //��ø�menu
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }
        
        @Override
		public boolean onActionItemClicked(final ActionMode mode,
				android.view.MenuItem item) {
			// TODO Auto-generated method stub
        	mKeyPosition = new ArrayList<Map<String,Object>>();
        	Map<String, Object> kMap;
        	for(Map<String, Object> keyMap:mDataList){ //��ȡcheckedΪ1 ��key��ĳ���
 				int check = (Integer) keyMap.get("mChecked");
 				if ( check == 1) {
 					kMap = new HashMap<String, Object>();
 					String mKey = keyMap.get("ZPKey").toString();
 					int position = (Integer) keyMap.get("mPosition");
 					String mName = keyMap.get("categoryName").toString();
 					
 					kMap.put("mKey", mKey);
 					kMap.put("mPosition", position);
 					kMap.put("mCategoryname", mName);
 					
 					mKeyPosition.add(kMap);
 				}
 			}
        	
        	 switch (item.getItemId()) {
             case R.id.delete:
            	 
            	 AlertDialog.Builder alertDialog = new AlertDialog.Builder(
 						CategoriesActivity.this);
 				// Setting Dialog Title
 				alertDialog.setTitle("Confirm Delete");
 				// Setting Dialg Message
 				alertDialog.setMessage("Are you sure you want to delete?");
 				alertDialog.setPositiveButton("YES",
 						new DialogInterface.OnClickListener() {
 							public void onClick(DialogInterface dialog,
 									int which) {
 								
 								for(Map<String, Object> mMap:mKeyPosition){
 									String mKey = (String) mMap.get("mKey"); 
 									int mPosition = (Integer) mMap.get("mPosition"); 
 									deleteCategory(mKey);
 					 			}
 								mData= getDate();
  								adapter = new CategoriesActivityAdapter(CategoriesActivity.this,mData);
  						        categoryListView.setAdapter(adapter);
  								mode.finish();
  								
  								for ( Map<String, Object> mMap:mDataList) {
  									mMap.put("mChecked", 0);
								}
 							}
 						});
 				// Setting Negative "NO" Button
 				alertDialog.setNegativeButton("NO",
 						new DialogInterface.OnClickListener() {
 							public void onClick(DialogInterface dialog,
 									int which) {
 								dialog.cancel();
 							}
 						});
 				// Showing Alert Message
 				alertDialog.show();
               
                 break;
                 
             case R.id.edit:
            	 LayoutInflater flater = LayoutInflater.from(CategoriesActivity.this);
            	 View view = flater.inflate(R.layout.dialog_edit_categoryname, null);
            	 editCategoryEditText = (EditText) view.findViewById(R.id.categoryEditText); 
            	 Log.v("mtake", mKeyPosition.size()+"mKeyPosition��С");
            	 for(Map<String, Object> mMap:mKeyPosition){
						String mName = (String) mMap.get("mCategoryname"); 
						editCategoryEditText.setText(mName);
		 			}
            	 
            	 
            	 AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
  						CategoriesActivity.this);
  				alertDialog2.setTitle("Rename the category name:");
  				alertDialog2.setView(view);
  				alertDialog2.setPositiveButton("YES",
  						new DialogInterface.OnClickListener() {
  							public void onClick(DialogInterface dialog,
  									int which) {
  								
  								for(Map<String, Object> mMap:mKeyPosition){
 									String mKey = (String) mMap.get("mKey"); 
 									String mName = editCategoryEditText.getText().toString();
 									editCategory(mName, mKey);
 					 			}
  								mData= getDate();
  								adapter = new CategoriesActivityAdapter(CategoriesActivity.this,mData);
  						        categoryListView.setAdapter(adapter);
  								mode.finish();
  								
  								for ( Map<String, Object> mMap:mDataList) {
  									mMap.put("mChecked", 0);
								}
  								
  								
  							}
  						});
  				// Setting Negative "NO" Button
  				alertDialog2.setNegativeButton("NO",
  						new DialogInterface.OnClickListener() {
  							public void onClick(DialogInterface dialog,
  									int which) {
  								dialog.cancel();
  							}
  						});
  				// Showing Alert Message
  				alertDialog2.show();
                 mode.finish();
                 break;
             default:
             	
                 break;
             }
             return true;
		}
        
        public void onDestroyActionMode(ActionMode mode) { //����actionbar���
        	
        	for (int i = 0; i < mData.size(); i++) {
        		adapter.getItemState()[i] = 0 ;
			}
        	adapter.notifyDataSetChanged();
        }

		public void onItemCheckedStateChanged(ActionMode mode,
                int position, long id, boolean checked) {
        	
        	int value = adapter.getItemState()[position] == 1 ? 0 : 1;
			adapter.getItemState()[position] = value ;
			adapter.notifyDataSetChanged();
			
			if (checked) {
				mDataList.get(position).put("mChecked", 1);
				mDataList.get(position).put("mPosition", position);
			} else {
				mDataList.get(position).put("mChecked", 0);
				mDataList.get(position).put("mPosition", -1);
			}
			mDataList.get(position).put("mPosition", position);
			
			Log.v("position", position+"λ��"+checked+"checked");
            final int checkedCount = categoryListView.getCheckedItemCount();
            MenuItem edititem = mMenu.findItem(R.id.edit);
            switch (checkedCount) {
                case 0:
                    mode.setTitle("null");
                    break;
                case 1:
                	mode.setTitle("" + checkedCount + " items selected");
                	edititem.setVisible(true);
                    break;
                default:
                    mode.setTitle("" + checkedCount + " items selected");
                    edititem.setVisible(false);
                    break;
            }
        }
	}
	
	public void deleteCategory(String key){ //ɾ�����ű�key��Ӧ�ļ�¼
		bksql = new BillKeeperSql(this);
	   	 SQLiteDatabase db = bksql.getReadableDatabase();
	   	 String sql ="delete from zcategories where Z_PK="+key;
	   	 String sqlinfo = "delete from zaccountinfo where zcategory="+key;
	   	 db.execSQL(sql);
	   	 db.execSQL(sqlinfo);
	   	 db.close();
	}
	
	public void editCategory(String mValues,String mkey){
		bksql = new BillKeeperSql(this);
	   	 SQLiteDatabase db = bksql.getReadableDatabase();
	 	ContentValues values = new ContentValues();
	 	
	 	values.put("zcategoryname", mValues);
	 	db.update("zcategories", values, "Z_PK=?", new String[]{mkey});
	}
	
	public List<Map<String, Object>> getDate(){ //���Դ���
		 List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	     Map<String, Object> map;
	     bksql = new BillKeeperSql(this);
	   	 SQLiteDatabase db = bksql.getReadableDatabase();
	     
	     Cursor cursorEA = db.rawQuery("select zcategories.ZCATEGORYNAME, count(ZACCOUNTINFO.z_pk),zcategories.Z_PK from zcategories,ZACCOUNTINFO where ZACCOUNTINFO.zcategory=zcategories.Z_PK GROUP BY ZACCOUNTINFO.zcategory", null);
	   	    while (cursorEA.moveToNext()) {
	       
	            map = new HashMap<String, Object>();
	            
	            String categoryName = cursorEA.getString(0);
	            String mNumber = cursorEA.getString(1); 
	            String categoryZPK = cursorEA.getString(2); 
	            String categoryNumber = "("+ mNumber+" accounts)";
	            
	   		    map.put("Categories", categoryName);
				map.put("mCategoryNumber", categoryNumber);
				map.put("categoryZPK", categoryZPK);
			
	            list.add(map);
	        }
	   	 cursorEA.close();
	   	 db.close();
	     return list;
	}
	
	public List<Map<String, Object>> keyDate(){ 
		 List<Map<String, Object>> mlist = new ArrayList<Map<String, Object>>();
	     Map<String, Object> mMap;
	     
	     for(Map<String, Object> imap:mData){
	    	 mMap = new HashMap<String, Object>();
	    	 mMap.put("ZPKey", imap.get("categoryZPK"));
	    	 mMap.put("mChecked",0);  //��item�Ƿ�ѡ��
	    	 mMap.put("mPosition",-1);//��ѡ��item��positon
	    	 mMap.put("categoryName",imap.get("Categories")); 
	    	 mlist.add(mMap);
	     }
	     return mlist;
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.categoryaddbill:
			Intent intent = new Intent();
			intent.setClass(this, NewBillActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.categories, menu);
		
		return true;
	}

}
