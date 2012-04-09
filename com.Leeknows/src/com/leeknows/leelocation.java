package com.leeknows;

import java.util.ArrayList;
import java.util.HashMap;

import com.httpclient.httphandler;
import com.leemodels.bookfunc;
import com.leemodels.bookinfo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class leelocation extends Activity
{

	public TextView local_bookbrief;
	public Button local_store;
	public Button local_findmore;
	public Button preq_btn;
	public SimpleAdapter listItemAdapter;
	public ArrayList<HashMap<String, Object>> listItem ;
	public ArrayList<HashMap<String, Object>> listItemtmp ;
	
	public 	ListView list2;
	public ArrayList<HashMap<String, Object>> listItem2=null ;
	public ArrayList<HashMap<String, Object>> listItemtmp2=null ;
	public SimpleAdapter listItemAdapter2=null;
	
	ListView list_local;
	public static String url = "http://202.119.83.14:8080/uopac/opac/item.php";
	public static String uripost = "http://202.119.83.14:8080/uopac/reader/redr_verify.php";
	public static String url_preg = "http://202.119.83.14:8080/uopac/opac/userpreg.php";
	public static String url_result = "http://202.119.83.14:8080/uopac/opac/userpreg_result.php";
	public static String param;
	public static String book_detail;
	protected final static int LISTUPDATE = 0x100;
	protected final static int LIST2UPDATE = 0x101;
	protected final static int TEXTUPDATE = 0x102;
	protected final static int TEXTDISMISS = 0x103;
	protected final static int PROGRESSBAR = 0x104;
	public SharedPreferences sharebook_store;
	public Editor book_store ;
	public int book_store_count;
	public bookinfo b_info;
	public Bundle inputdata;
	public bookfunc b_func;
	public static int check;
	public boolean prog_flag = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.location);
		setProgressBarIndeterminateVisibility(false);
		 initializelocal();
		 Intent intent = getIntent();
		 inputdata = intent.getExtras();
		 b_info = (bookinfo)inputdata.getSerializable("S_book");
		 param = b_info.getbook_url().substring(9);
		 new Thread(new MyThread()).start();//新建一个子线程并运行
		 
		 list2.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				// TODO Auto-generated method stub
				menu.setHeaderTitle("图书预约");
				menu.add(0,0,0,"预约");
				
			}
		});
		 
		 
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		int start = info.position;
		check=start+1;
		if(item.toString().equals("预约")){
			if(!listItemtmp2.get(start).get("Itemappendtitle").toString().equals(""))
			{
				new Thread(new MyThread3()).start();
			}
			else
			{
				b_func.toastinfo(getBaseContext(), "可借而不可预约");
			}
			
		}
	
		return super.onMenuItemSelected(featureId, item);
	}
	
	public void onclick_findmore(View v)
	{
		Message msg = new Message();
		System.out.println("test="+local_bookbrief.getText());
		System.out.println(local_bookbrief.getVisibility());
		if(local_bookbrief.getVisibility()==View.INVISIBLE)
			msg.what = leelocation.TEXTUPDATE;
		else
			msg.what = leelocation.TEXTDISMISS;
		mHandler.sendMessage(msg);
    	
	}
	
	public void onclick_preq(View v)
	{
		//负责刷新预约的选项
		 b_func.toastinfo(getBaseContext(), "正在加载预约列表");
		 
		 Message msg = new Message();
 		msg.what = leelocation.PROGRESSBAR;
 		leelocation.this.mHandler.sendMessage(msg);
 		
		 new Thread(new MyThread2()).start();
		
	}
	
	public void onclick_store(View v)
	{
		int book_index = bookfunc.findduplicatedstore(sharebook_store,b_info.getbook_name());
	    if( book_index == -1)
	    {
	    	book_store_count = sharebook_store.getInt("store_count", 0);
	    	book_store.putString("book_name"+book_store_count, b_info.getbook_name());
			book_store.putString("book_owner"+book_store_count, b_info.getbook_owner());
			book_store.putString("book_url"+book_store_count, b_info.getbook_url());
			
			book_store_count++;
			book_store.putInt("store_count", book_store_count);
	    }
	    else{
	    book_store.putString("book_name"+book_index, b_info.getbook_name());
		book_store.putString("book_owner"+book_index, b_info.getbook_owner());
		book_store.putString("book_url"+book_index, b_info.getbook_url());
		
		}
	    book_store.commit();
	    b_func.toastinfo(getBaseContext(),"已收藏");
	}
	public boolean post_preq(int check)
	{
    			
    				String preq_parms=param+"&count="+listItemtmp2.size();
    				for(int i=1;i<=listItemtmp2.size();i++)
    				{
    					String preq_str = listItemtmp2.get(i-1).get("Itemappendtitle").toString();
    					String preq_parms_array[] = preq_str.split(" ");
    					preq_parms +="&preg_days"+i+"=15"+"&take_loca"+i+"="+preq_parms_array[1]+"&callno"+i+"="+preq_parms_array[0]+"&location"+i+"="+preq_parms_array[1]+"&pregKeepDays"+i+"=7";
    					if(check == i)
    						preq_parms+="&check="+check;
    				}
    				

    				preq_parms = preq_parms.replace("/","%2F");
    				preq_parms = preq_parms.replace(" ", "+");
    				
    				httphandler.httpget_session(url_result, preq_parms,b_func.getsessionid());
    				return true;
    		
		
	}
	
	public void updateStatus(final int id) {
        // Be a good citizen.  Make sure UI changes fire on the UI thread.
		this.runOnUiThread(new Runnable() {
            public void run() {
				switch (id) {
				case LISTUPDATE:
			        bookfunc.clonelist(listItem, listItemtmp);
			        listItemAdapter.notifyDataSetChanged(); 
			        preq_btn.setVisibility(View.VISIBLE);
			        local_store.setVisibility(View.VISIBLE);
			        local_findmore.setVisibility(View.VISIBLE);
			        list_local.setVisibility(View.VISIBLE);
			        local_bookbrief.setText(book_detail);
			       
			      //  local_bookbrief.setText(book_detail);   
			        break;
				case LIST2UPDATE:
			        bookfunc.clonelist(listItem2, listItemtmp2);
			        list2.setVisibility(View.VISIBLE);
			        listItemAdapter2.notifyDataSetChanged(); 
			      //  local_bookbrief.setText(book_detail);   
			        break;
				case TEXTUPDATE:
					local_bookbrief.setVisibility(View.VISIBLE);
					
					break;
			            
				case TEXTDISMISS:
					local_bookbrief.setVisibility(View.INVISIBLE);
					
					break;
            	case PROGRESSBAR:
            		prog_flag=(prog_flag==true?false:true);
            		setProgressBarIndeterminateVisibility(prog_flag);
            		break;
		
				default:
					break;
				}
            }
		});
        
    }
	
	class MyThread2 implements Runnable{  
	  	  
        @Override  
        public void run() {  
        	if(b_func.mylib_login(getBaseContext())) // 先登录
    		{
        		
    			if(listItemtmp2.size()!=0)
    				listItemtmp2.clear();
    			String result = httphandler.httpget_session(url_preg, param,b_func.getsessionid());
    			httphandler.http_parser_preg(result, listItemtmp2);
    			
    			Message message = new Message();   
    	        message.what = leelocation.LIST2UPDATE;        
    	        leelocation.this.mHandler.sendMessage(message);
    	        b_func.toastinfo(getBaseContext(), "加载完毕，长按预约");
    	        
    	        Message msg = new Message();
        		msg.what = leelocation.PROGRESSBAR;
        		leelocation.this.mHandler.sendMessage(msg);
        		
    	        
    		}
    		else{
    			Intent intent = new Intent(getBaseContext(),leesetting.class);
    			startActivity(intent);
    		}
                 }           
     }
	class MyThread3 implements Runnable{  
	  	  
        @Override  
        public void run() {  
        	if(post_preq(check)==true)
        		b_func.toastinfo(getBaseContext(), "预约成功");
                 }           
     }
	
	//线程更新数据
    class MyThread implements Runnable{  
  	  
        @Override  
        public void run() {  
        		Message msg = new Message();
        		msg.what = leelocation.PROGRESSBAR;
        		leelocation.this.mHandler.sendMessage(msg);
        		b_func.toastinfo(getBaseContext(), "正在加载");
        		Message message = new Message();   
                message.what = leelocation.LISTUPDATE;                  	
        		String html = httphandler.httpget(url, param);
        		book_detail = b_info.getbook_name()+'\n'+'\n';
        		book_detail += httphandler.http_parser_local(html,listItemtmp);
        		leelocation.this.mHandler.sendMessage(message);   
        		b_func.toastinfo(getBaseContext(), "加载完毕");
        		Message msg2 = new Message();
        		msg2.what = leelocation.PROGRESSBAR;
        		leelocation.this.mHandler.sendMessage(msg2);
                 }           
     }
    public void initializelocal()
	{
    	b_func = new bookfunc();
    	book_detail=null;
    	param = null;
		listItem=null ;
		listItemAdapter=null;
		listItemtmp=null;
		b_info = new bookinfo();
		book_store_count =0 ;
		 
		//绑定LISTVIEW
		list_local=(ListView)findViewById(R.id.local_list);
		//生成动态数组，加入数据
		listItem = new ArrayList<HashMap<String,Object>>();
		listItemtmp = new ArrayList<HashMap<String,Object>>();
		//生成容器
		listItemAdapter = new SimpleAdapter(this,listItem,R.layout.listitemlocal,
				new String[]{"local_item1","local_item2"},
				new int[]{R.id.local_item1,R.id.local_item2});
		//添加并且显示
		list_local.setAdapter(listItemAdapter);
		
		listItem2=null ;
		listItemAdapter2=null;
		//绑定LISTVIEW
		list2=(ListView)findViewById(R.id.preq_list);
		//生成动态数组，加入数据
		listItem2 = new ArrayList<HashMap<String,Object>>();
		listItemtmp2 = new ArrayList<HashMap<String,Object>>();

		//生成容器
		listItemAdapter2 = new SimpleAdapter(this,listItem2,R.layout.listitem,
				new String[]{"Itembigtitle","Itemsmalltitle","Itemurl"},
				new int[]{R.id.Itembigtitle,R.id.Itemsmalltitle,R.id.Itemurl});
		
		//添加并且显示
		list2.setAdapter(listItemAdapter2);
		
		local_bookbrief = (TextView)findViewById(R.id.local_bookbrief);
		local_bookbrief.setMovementMethod(ScrollingMovementMethod.getInstance());
		book_store = getSharedPreferences("book_store", 0).edit();
		sharebook_store = getSharedPreferences("book_store",0);
		local_findmore =(Button)findViewById(R.id.local_findmore);
		local_bookbrief.setVisibility(View.INVISIBLE);
		preq_btn = (Button)findViewById(R.id.preq_btn);
		local_store = (Button)findViewById(R.id.local_store);
			
	}
    

	private Handler mHandler= new Handler(){

        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {   
            case leelocation.LISTUPDATE:
                 updateStatus(LISTUPDATE); 
                 break; 
            case leelocation.LIST2UPDATE:
                updateStatus(LIST2UPDATE); 
                break;
            case leelocation.TEXTUPDATE:
            	 updateStatus(TEXTUPDATE);
            	 break;
            case leelocation.TEXTDISMISS:
            	 updateStatus(TEXTDISMISS);
            	 break;
        	case leelocation.PROGRESSBAR:
        		updateStatus(PROGRESSBAR);
        		break;
        	}
        	super.handleMessage(msg);
        }};
        
}