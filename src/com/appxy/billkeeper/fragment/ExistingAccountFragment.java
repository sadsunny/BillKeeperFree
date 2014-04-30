package com.appxy.billkeeper.fragment;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Text;

import com.appxy.billkeeper.R;
import com.appxy.billkeeper.activity.NewBillActivity;
import com.appxy.billkeeper.adapter.ExistingAccountAdapter;
import com.appxy.billkeeper.db.BillKeeperSql;
import com.appxy.billkeeper.entity.Common;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ExistingAccountFragment extends Fragment {
	
	private ListView listView;
	private List<Map<String, Object>> data;
	private ExistingAccountAdapter adapter;
	private BillKeeperSql bksql;
	private final String NOREPEAT="No repeating bills";//û���ظ���Ԥ��ֵ
	
	  @Override
	     public void onCreate(Bundle savedInstanceState)
	     {
	        super.onCreate(savedInstanceState);
	        setHasOptionsMenu(true);
//	    	 getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
	     }
	  
	  public List<Map<String, Object>> getData()//��ȡ��䵽listView�����
	    {
	        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	        Map<String, Object> map;
	        
	        bksql = new BillKeeperSql(getActivity());
	   	    SQLiteDatabase db = bksql.getReadableDatabase();
	   	    
	   	    Cursor cursorEA = db.rawQuery("select Z_PK,ZACCOUNTNAME,ZISREPEAT,ZREPEATFREQUENCY,ZREPEATUNIT from ZACCOUNTINFO ", null);
	   	    while (cursorEA.moveToNext()) {
	       
	            map = new HashMap<String, Object>();
	            
	            String AccountZ_PK = cursorEA.getString(0);
	            String accountname = cursorEA.getString(1); 
	   		    String isrepeat = cursorEA.getString(2);
	   		    String repeatfreqency = cursorEA.getString(3);
	   		    String repeatunit = cursorEA.getString(4);
	   		    
	   		    String repeatOnce = "Once every "+repeatfreqency+" "+repeatunit;
	   		    
	   		    map.put("AccountZ_PK", AccountZ_PK);
	            map.put("accountNa", accountname);
	            if (Integer.parseInt(isrepeat)==0 || Integer.parseInt(isrepeat)==2) {
	            	map.put("repeat", NOREPEAT);
				}if (Integer.parseInt(isrepeat)==1) {
					map.put("repeat", repeatOnce);
				}
				map.put("isRepeatTag", isrepeat);
			
	            list.add(map);
	        }
	   	 cursorEA.close();
	   	 db.close();
	        return list;
	    }
	     
	     @Override
	     public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	     {

	    	 View view = inflater.inflate(R.layout.fragment_existing_account, container, false);
	    	 //test��ȡ���
	    	 listView=(ListView)view.findViewById(R.id.accountlist);
	    	 adapter = new ExistingAccountAdapter(getActivity(),getData());
	    	 
	    	 listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					adapter.isChecked(arg2);
					
					String nameString=(String) getData().get(arg2).get("accountNa");
					String keyIdString=(String) getData().get(arg2).get("AccountZ_PK");
					
					adapter.notifyDataSetChanged();//֪ͨ��̨����ˢ��listview����
					
					Intent intent = new Intent();
					intent.putExtra("accountNa", nameString);
					intent.putExtra("zAccountinfoZ_PK", keyIdString);
					getActivity().setResult(0, intent);  
					getActivity().finish(); 
				}
			});
	    	 
	    	 listView.setAdapter(adapter);
	         return view;
	     }

	     @Override
		    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		    	// TODO Auto-generated method stub
		    	super.onCreateOptionsMenu(menu, inflater);
		    }
	     
	     @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	    	// TODO Auto-generated method stub
	    	return super.onOptionsItemSelected(item);
	    }
}
