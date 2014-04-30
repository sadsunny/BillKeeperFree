package com.appxy.billkeeper.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.CheckedInputStream;

import com.appxy.billkeeper.R;
import com.appxy.billkeeper.db.BillKeeperSql;
import com.appxy.billkeeper.entity.Common;

import android.R.integer;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import android.util.Log;

public class ExistingAccountAdapter extends BaseAdapter{
	
    private List<Map<String, Object>> data;
    private BillKeeperSql bksql;
    private Context context;
 
    private LayoutInflater mInflater;
    private int ISREPEAT;
    public  static int ISCHECKED = -1;
    public static Map<String, Object> KEYIDMap;
    
    public  ExistingAccountAdapter(Context context,List<Map<String, Object>> data) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.data=data;
	}
    
   
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (data==null) {
			return 0;
		}
		return data.size();
	}
	
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	

	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		if (Integer.parseInt((String) data.get(position).get("isRepeatTag"))==1) {
			
			return false;
		}else {
			return true;
		}
		
	}
	
	public void isChecked(int checked){
		this.ISCHECKED=checked;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		 ViewHolder viewholder = null;

		 if(convertView == null)
         {
			 viewholder = new ViewHolder();
             //����Զ����Item���ּ��ز���
             convertView = mInflater.inflate(R.layout.item_existing_account, null);
             viewholder.accountNa1 = (TextView)convertView.findViewById(R.id.accountNa);
             viewholder.repeat1 = (TextView)convertView.findViewById(R.id.repeatUnit);
             viewholder.choosedImageView = (ImageView)convertView.findViewById(R.id.choosedimageView);
             //�����úõĲ��ֱ��浽�����У�������������Tag��Ա���淽��ȡ��Tag
             convertView.setTag(viewholder);
         }else
         {
        	 viewholder = (ViewHolder)convertView.getTag();
         }
		 
		 viewholder.accountNa1.setText((String)data.get(position).get("accountNa"));
		 viewholder.repeat1.setText((String)data.get(position).get("repeat"));
		 viewholder.choosedImageView.setImageResource(R.drawable.confirm);	
		
//			KEYIDMap.put(String.valueOf(position),(String)data.get(position).get("AccountZ_PK"));
		 
		 if (Integer.parseInt((String) data.get(position).get("isRepeatTag"))==1 ) {
			 viewholder.accountNa1.setTextColor(android.graphics.Color.GRAY);
			 viewholder.repeat1.setTextColor(android.graphics.Color.GRAY);
		} if (Integer.parseInt((String) data.get(position).get("isRepeatTag"))==0 || Integer.parseInt((String) data.get(position).get("isRepeatTag"))==2 ){
			viewholder.accountNa1.setTextColor(android.graphics.Color.BLACK);
			 viewholder.repeat1.setTextColor(android.graphics.Color.BLACK);
		}
		 if (ISCHECKED==position && (Integer.parseInt((String) data.get(position).get("isRepeatTag"))==0)) {
			 viewholder.choosedImageView.setVisibility(View.VISIBLE);
		}else{
			viewholder.choosedImageView.setVisibility(View.INVISIBLE);
		}	 
         return convertView;
	}
	
	
   class ViewHolder
    {
        public TextView accountNa1;
        public TextView repeat1;
        public ImageView choosedImageView; 
    }
	
}
