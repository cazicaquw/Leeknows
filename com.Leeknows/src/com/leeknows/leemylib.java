package com.leeknows;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.httpclient.httphandler;
import com.leemodels.bookfunc;

import android.app.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;



public class leemylib extends Activity{

	public bookfunc b_func;
	
	public 	ListView list;
	public ArrayList<HashMap<String, Object>> listItem=null ;
	public ArrayList<HashMap<String, Object>> listItemtmp=null ;
	public SimpleAdapter listItemAdapter=null;
	
	public 	ListView list2;
	public ArrayList<HashMap<String, Object>> listItem2=null ;
	public ArrayList<HashMap<String, Object>> listItemtmp2=null ;
	public SimpleAdapter listItemAdapter2=null;
	public static int check;
	protected final static int LISTUPDATE = 0x100;
	protected final static int LIST2UPDATE = 0x101;
	protected final static int WAITUPDATE = 0x102;
	protected final static int TITLEUPDATE = 0x103;
	protected final static int PROGRESSBAR = 0x104;
	public boolean prog_flag=false;
	String mylogin_uname;
	private final static String url_renew = "http://202.119.83.14:8080/uopac/reader/ajax_renew.php";
	private final static String url_preg = "http://202.119.83.14:8080/uopac/reader/ajax_preg.php";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.mylib);  
		setProgressBarIndeterminateVisibility(false);
        /*
         * NameValuePair实现请求参数的封装
        */
		initializeresult();
		new Thread(new MyThread()).start();
		//添加长按点击  
        list.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {  
              
            @Override  	
            public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
                menu.setHeaderTitle("确定续借");     
                menu.add(0, 0, 0, "续借");
                
            }
        });
        list2.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {  
            
            @Override  	
            public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
                menu.setHeaderTitle("图书预约");     
                menu.add(0, 0, 0, "取消预约");
                
            }
        });
         
	}
	
	@Override  
    public boolean onContextItemSelected(MenuItem item) {  
	 AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
     	int start = info.position;
     	check = start;
     	if(item.toString().equals("续借")){	
     		b_func.toastinfo(getBaseContext(), "请稍等");
     		new Thread(new MyThread2()).start();
     	}
     	else if(item.toString().equals("取消预约"))
     	{
     		b_func.toastinfo(getBaseContext(), "请稍等");
     		new Thread(new MyThread3()).start();
     	}
         
         
         
        return super.onContextItemSelected(item);  
    } 
	
	 //线程更新数据
    class MyThread implements Runnable{  
  	  
        @Override  
        public void run() {  
            Message msg1 = new Message();
    		msg1.what = leelocation.PROGRESSBAR;
    		leemylib.this.mHandler.sendMessage(msg1);
    		
        	boolean judge = b_func.mylib_login(getBaseContext());
        	if(judge) // 先登录
    		{
        		mylib_refresh();
        		mylib_refresh2();
    		}
            Message msg2 = new Message();
    		msg2.what = leelocation.PROGRESSBAR;
    		leemylib.this.mHandler.sendMessage(msg2);
        }
      }
    
    class MyThread2 implements Runnable{  
    	  
        @Override  
        public void run() {  
            Message msg1 = new Message();
    		msg1.what = leelocation.PROGRESSBAR;
    		leemylib.this.mHandler.sendMessage(msg1);
    		
        	String time =String.valueOf(componentTimeToTimestamp());
         	//String time = "1330876800000";
         	String bar_code = listItem.get(check).get("Itemappendtitle").toString();
         	
         	List <NameValuePair> params = new ArrayList <NameValuePair>();
         	String url_api = url_renew+"?bar_code="+bar_code+"&"+"time="+time;
         	String result = httphandler.http_post_session(params,url_api,b_func.getsessionid());
         	Document doc = Jsoup.parse(result);
         	
         	b_func.toastinfo(getBaseContext(), doc.text());
         	
            Message msg2 = new Message();
    		msg2.what = leelocation.PROGRESSBAR;
    		leemylib.this.mHandler.sendMessage(msg2);
    		
        }
      }
    class MyThread3 implements Runnable{  
  	  
        @Override  
        public void run() { 
            Message msg1 = new Message();
    		msg1.what = leelocation.PROGRESSBAR;
    		leemylib.this.mHandler.sendMessage(msg1);
    		
        	String str_par= listItem2.get(check).get("Itemappendtitle").toString();   //javascript:getInLib('0000488314','TN929.53/147','1','sjl  ')
     		if(!str_par.equals("")){
	     		String time =String.valueOf(componentTimeToTimestamp());
	     		
	     		str_par = str_par.replace('\'', ' ');//javascript:getInLib( 0000488314 , TN929.53/147 , 1 , sjl  )
	     		str_par = str_par.replace(", ","");//javascript:getInLib( 0000488314   TN929.53/147   1   sjl  )
	     		String  str_par_array[] = str_par.split(" ");//
	     		
	     		List <NameValuePair> params = new ArrayList <NameValuePair>();
	     		String uri_api = url_preg + "?call_no="+str_par_array[2]+"&marc_no="+str_par_array[1]+"&loca="+str_par_array[4]+"&time="+time;
	     		String result = httphandler.http_post_session(params, uri_api, b_func.getsessionid());
	     		Document doc = Jsoup.parse(result);
	         	
	         	b_func.toastinfo(getBaseContext(), doc.text());
	    		
	    		listItemtmp2.remove(check);
	    		
	    		Message message = new Message();   
	            message.what = leemylib.LIST2UPDATE;        
	            leemylib.this.mHandler.sendMessage(message);
     		}
     		else
     			b_func.toastinfo(getBaseContext(), "不能取消");
     		
     		Message msg2 = new Message();
    		msg2.what = leelocation.PROGRESSBAR;
    		leemylib.this.mHandler.sendMessage(msg2);
        }
        
      }
    
    
	public void mylib_refresh()
	{
		
		String uriget = "http://202.119.83.14:8080/uopac/reader/book_lst.php";
		String html = httphandler.httpget_session(uriget, "", b_func.getsessionid());
    	mylogin_uname = httphandler.http_parser_mylib(html,listItemtmp);

    	Message message = new Message();   
        message.what = leemylib.LISTUPDATE;        
        leemylib.this.mHandler.sendMessage(message);    

	}
	
	public void mylib_refresh2()
	{
		String uriget = "http://202.119.83.14:8080/uopac/reader/preg.php";
		String html = httphandler.httpget_session(uriget, "",b_func.getsessionid());
    	httphandler.http_parser_myresv(html,listItemtmp2);
    	Message message = new Message();   
        message.what = leemylib.LIST2UPDATE;        
        leemylib.this.mHandler.sendMessage(message);
        b_func.toastinfo(getBaseContext(), "加载完毕");
	}
	
	public void initializeresult()
	{
		b_func = new bookfunc();
		listItem=null ;
		listItemAdapter=null;
		//绑定LISTVIEW
		list=(ListView)findViewById(R.id.mylib_list);
		//生成动态数组，加入数据
		listItem = new ArrayList<HashMap<String,Object>>();
		listItemtmp = new ArrayList<HashMap<String,Object>>();

		//生成容器
		listItemAdapter = new SimpleAdapter(this,listItem,R.layout.listitem,
				new String[]{"Itembigtitle","Itemsmalltitle","Itemurl"},
				new int[]{R.id.Itembigtitle,R.id.Itemsmalltitle,R.id.Itemurl});
		
		//添加并且显示
		list.setAdapter(listItemAdapter);
		
		listItem2=null ;
		listItemAdapter2=null;
		//绑定LISTVIEW
		list2=(ListView)findViewById(R.id.mylib_listrev);
		//生成动态数组，加入数据
		listItem2 = new ArrayList<HashMap<String,Object>>();
		listItemtmp2 = new ArrayList<HashMap<String,Object>>();

		//生成容器
		listItemAdapter2 = new SimpleAdapter(this,listItem2,R.layout.listitem,
				new String[]{"Itembigtitle","Itemsmalltitle","Itemurl"},
				new int[]{R.id.Itembigtitle,R.id.Itemsmalltitle,R.id.Itemurl});
		
		//添加并且显示
		list2.setAdapter(listItemAdapter2);
		
	
		
	}
	
	public void updateStatus(final int id) {
        // Be a good citizen.  Make sure UI changes fire on the UI thread.
		this.runOnUiThread(new Runnable() {
            public void run() {
				switch (id) {
				case LISTUPDATE:
					setTitle(mylogin_uname+",你好");
			        bookfunc.clonelist(listItem, listItemtmp);
			        list.setVisibility(View.VISIBLE);
			        listItemAdapter.notifyDataSetChanged(); 
			        break;
				case LIST2UPDATE:	
					listItem2.clear();   //先删除
			        bookfunc.clonelist(listItem2, listItemtmp2);
			        list2.setVisibility(View.VISIBLE);
			        listItemAdapter2.notifyDataSetChanged(); 
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

	private Handler mHandler= new Handler(){

        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {   
            case leemylib.LISTUPDATE:
                 updateStatus(LISTUPDATE); 
                 break; 
            case leemylib.LIST2UPDATE:
                updateStatus(LIST2UPDATE); 
                break;
            case leemylib.PROGRESSBAR:
            	updateStatus(PROGRESSBAR);
            	break;
            
        	}
        	super.handleMessage(msg);
        }};
        public long componentTimeToTimestamp() {

            Calendar c = Calendar.getInstance();

            return (long) (c.getTimeInMillis());
        }
	
}