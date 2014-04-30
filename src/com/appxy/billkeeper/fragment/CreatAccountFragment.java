package com.appxy.billkeeper.fragment;
//package com.xiuyue.bliikeeper.fragment;
///**
// * Creat new account?????��????��??�?�?ZACCOUNTINFO
// * Category??��?��????��??�?�?zcategories
// * Categoryicon�?�????drawable???�?�?
// * 
// * �?�???????�?�?�?
// */
//
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.CountDownLatch;
//
//import com.xiuyue.bliikeeper.R;
//import com.xiuyue.bliikeeper.activity.EditAccountActivity;
//import com.xiuyue.bliikeeper.adapter.DialogCategoryIconAdapter;
//import com.xiuyue.bliikeeper.db.BillKeeperSql;
//
//import android.R.anim;
//import android.R.integer;
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.text.InputType;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.View.OnKeyListener;
//import android.view.View.OnTouchListener;
//import android.view.ViewGroup;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.EditText;
//import android.widget.GridView;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//public class CreatAccountFragment extends Fragment {
//	
//	private EditText myAccountName;
//	private EditText myCategory;
//	private EditText myAccountIconName;
//	private EditText myAccountNumber;
//	private EditText myAccountMemo;
//	
//	private String accountName;
//	private String category;
//	private String accountIconName;
//	private String accountNumber;
//	private String accountMemo;
//	private ImageView categoryIconImageView;
//	
//	private int isNull;//??��????��??�????�????为空
//	
//	private int closePK;//�?次�?��?????主�??ID
//	
//	private BillKeeperSql bksql;
//	private Context context;
//	
//	private int whichCategory=0;//category?????�项
//	private int whichIcon=0;//icon?????�项
//	
//	private String categoryPKey = "1"; //�?�???��?��??�????category主�??
//	private String[] categoryString;
//	private String[] categoryPKeyString;
//	
//	private Integer[] images = {  //categoryicon???�?
//			   //GridView??��?????设置  
////			   R.drawable.categoryicon1, 
////			   R.drawable.categoryicon2,  
////			   R.drawable.categoryicon3,  
////			   R.drawable.categoryicon4,  
////			   R.drawable.categoryicon5, 
////			   R.drawable.categoryicon6, 
////			   R.drawable.categoryicon7,  
////			   R.drawable.categoryicon8,  
////			   R.drawable.categoryicon9,  
////			   R.drawable.categoryicon10,
//			   };  
//	
//	private AlertDialog alertCicon;
//	
//	public Context CreatAccountFragment(Context context) {
//		this.context=context;
//		return context;
//		
//	}
//	
//	  @Override
//	     public void onCreate(Bundle savedInstanceState)
//	     {
//	        super.onCreate(savedInstanceState);
//	        setHasOptionsMenu(true);//?????????�?fragment???actionbar???载�??activity�?
//	     }
//	     
//	     @Override
//	     public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
//	     {
//	    	 View view = inflater.inflate(R.layout.fragment_creat_account, container, false);
//	    	 
//	    	myAccountName = (EditText)view.findViewById(R.id.accountna);
//	    	
//	    	bksql = new BillKeeperSql(getActivity());
//	    	 SQLiteDatabase db = bksql.getReadableDatabase();
//	    	 
//	    	 Cursor cursorCC = db.rawQuery("select zcategoryname,Z_PK from zcategories order by zcategoryname ASC limit 1 ", null);
//	    	 String mCategoryName = "No Category";
//	    	 isNull=cursorCC.getCount();
//	    	 while (cursorCC.moveToNext()) {
//	    		 mCategoryName = cursorCC.getString(0);
//	    		 categoryPKey = cursorCC.getString(1);
//			}
//	    	 cursorCC.close();
//	    	 db.close();
//	 		myCategory = (EditText)view.findViewById(R.id.category);
//	 		myCategory.setText(mCategoryName);
//
//	 		myAccountIconName = (EditText)view.findViewById(R.id.icon);
//	 		myAccountIconName.setInputType(InputType.TYPE_NULL);
//	 		
//	 		myAccountNumber = (EditText)view.findViewById(R.id.accountnu);
//	 		myAccountMemo = (EditText)view.findViewById(R.id.memo);
//	 		
//	 		categoryIconImageView=(ImageView)view.findViewById(R.id.imageView1);
//	 		
//	 		myCategory.setOnTouchListener(new OnTouchListener() {//dialog??????category
//				
//				@Override
//				public boolean onTouch(View v, MotionEvent event) {
//					// TODO Auto-generated method stub
//					if (MotionEvent.ACTION_UP == event.getAction()) { //??��??�?件�??�????
//						
//						bksql = new BillKeeperSql(getActivity());
//				    	 SQLiteDatabase db = bksql.getReadableDatabase();
//				    	 StringBuffer sb =new StringBuffer();
//				    	 
//				    	 Cursor cursorCC = db.rawQuery("select Z_PK ,zcategoryname from zcategories ", null);
//				    	 int index=0;
//				    	 
//				    	 categoryString = new  String[cursorCC.getCount()]; //�?�???��??�???��?????�?类�??�?
//				    	 categoryPKeyString = new String[cursorCC.getCount()];//�?�?主�??
//				    	 
//				    	 while (cursorCC.moveToNext()) {
//				    		 
//				    		 String ZPKey = cursorCC.getString(0);
//				    		 String accountname = cursorCC.getString(1);
//				    		 categoryPKeyString[index] = ZPKey;
//				    		 categoryString[index]=accountname;
//				    		 index++;
//						}
//				    	 
//						new AlertDialog.Builder(getActivity())
//					 	.setTitle("Choose Category")
//					 	.setSingleChoiceItems(categoryString, 0, 
//					 	  new DialogInterface.OnClickListener()	 {
//					 	                              
//					 	     public void onClick(DialogInterface dialog, int which) {
//					 	    	whichCategory=which;
//					 	    	myCategory.setText(categoryString[which]);
//					 	    	categoryPKey = categoryPKeyString[which];
//					 	        dialog.dismiss();
//					 	     }
//					 	  }
//					 	)
//					 	.show();
//					}
//					return false;
//					
//				}
//			});
//	 		
//
//	 		myAccountIconName.setOnTouchListener(new OnTouchListener() { //??????icon
//				
//				@Override
//				public boolean onTouch(View v, MotionEvent event) {
//					// TODO Auto-generated method stub
//					if (MotionEvent.ACTION_UP == event.getAction()) { 
//						
//						LayoutInflater flater = LayoutInflater.from(getActivity());//�?categoryicon?????????
//					    View view = flater.inflate(R.layout.dialog_category_icon, null);
//					    
//						DialogCategoryIconAdapter Dadapter = new DialogCategoryIconAdapter(getActivity());
//						GridView gridView = (GridView)view.findViewById(R.id.dialogGridView);
//						gridView.setAdapter(Dadapter);
//						
//						gridView.setOnItemClickListener(new OnItemClickListener() {
//
//							@Override
//							public void onItemClick(AdapterView<?> arg0,
//									View arg1, int arg2, long arg3) {
//								// TODO Auto-generated method stub
//								  whichIcon=arg2;
//								  categoryIconImageView.setImageResource(images[arg2]);
//								  alertCicon.dismiss();
//							}
//							
//						});
//					    
//						AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//						builder.setView(view);
//						alertCicon = builder.create(); 
//						alertCicon.show();
//						
//					}
//					return false;
//					
//				}
//			});
//
//	 		
//	         return view;
//	     }
//	     
//	     @Override
//		    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//		    	// TODO Auto-generated method stub
//	    	inflater.inflate(R.menu.creat_new_account_menu, menu);
//		    	super.onCreateOptionsMenu(menu, inflater);
//		    }
//	     
//	     @Override
//	    public boolean onOptionsItemSelected(MenuItem item) {
//	    	// TODO Auto-generated method stub
//	    	 switch (item.getItemId()) {
//			case R.id.creatnewaccountmenu:
//				
//		 		accountName=myAccountName.getText().toString();
//		 		category=myCategory.getText().toString();
//		 		accountIconName=myAccountIconName.getText().toString();
//		 		accountNumber=myAccountNumber.getText().toString();
//		 		accountMemo=myAccountMemo.getText().toString();
//				
//				if (accountName.length() == 0) {
//					new AlertDialog.Builder(getActivity())
//					.setTitle("Warning")
//					.setMessage("Please make sure the account name is not empty")
//					.setPositiveButton("OK",new DialogInterface.OnClickListener() {
//						
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							// TODO Auto-generated method stub
//							dialog.dismiss();
//						}
//					})
//					.show();
//				}else if (isNull==0) {
//					
//					new AlertDialog.Builder(getActivity())
//					.setTitle("Warning")
//					.setMessage("Please make sure the Category name is not empty")
//					.setPositiveButton("OK",new DialogInterface.OnClickListener() {
//						
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							// TODO Auto-generated method stub
//							dialog.dismiss();
//						}
//					})
//					.show();
//					break;
//					
//				}else if (accountName !=null && accountName.length() != 0) {
//					
//				bksql=new BillKeeperSql(getActivity());
//				SQLiteDatabase db = bksql.getWritableDatabase();
//				Map<String, Object> map = null;
//				
//				db.execSQL(("INSERT INTO ZACCOUNTINFO(ZACCOUNTNAME,ZCATEGORY,ZACCOUNTICONNAME,ZACCOUNTNUMBER,ZACCOUNTMEMO,ZISREPEAT) VALUES(?,?,?,?,?,?)"),
//						new Object[]{accountName,categoryPKey,whichIcon,accountNumber,accountMemo,0});
//				
//				Cursor cursorAccountZ_PK =db.rawQuery("select Z_PK from ZACCOUNTINFO order by Z_PK desc limit 1",null); //主�?????�???????�??????��??�?�?.??��???????��?��?????主�??
//				while (cursorAccountZ_PK.moveToNext()) {
//					
//					String AccountZ_PK = cursorAccountZ_PK.getString(0);
//		   	    	map= new HashMap<String, Object>();
//		   	    	map.put("AccountZ_PK", AccountZ_PK);				
//				}
//				
//				
//				cursorAccountZ_PK.close();
//				db.close();
//				
//				Intent intent = new Intent();
//				intent.putExtra("accountNa", accountName);
//				intent.putExtra("zAccountinfoZ_PK",(String)map.get("AccountZ_PK"));
//				
//				getActivity().setResult(0, intent);  
//				getActivity().finish(); 
//				}
//				break;
//
//			default:
//				break;
//			}
//	    	return super.onOptionsItemSelected(item);
//	    }
//}
