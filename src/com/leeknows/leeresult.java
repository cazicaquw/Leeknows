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
import android.preference.PreferenceManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.Window;

public class leeresult extends Activity {
	public ArrayList<HashMap<String, Object>> listItem=null ;
	public ArrayList<HashMap<String, Object>> listItemtmp=null ;
	ListView list;
	public SimpleAdapter listItemAdapter=null;
	public  boolean Firstblood =false; //第一次更新后设为true
	public  boolean updateflag = false;
	public  int item_count =0;         //已经更新的个数
	public  int total_num=0;			 //搜索到的总数
	public String Params=null;
	public final String url="http://202.119.83.14:8080/uopac/opac/openlink.php";
	public final int buffersize = 60;
	protected final static int PROGRESSBAR = 0x102; 
	protected final static int LISTUPDATE = 0x103;
	public SharedPreferences sharebook_store;
	public SharedPreferences sharebook_set;
	public Editor book_store ;
	public int book_store_count;
	public bookfunc b_func;
	
  
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.resultlist);
		setProgressBarIndeterminateVisibility(false);
		initializeresult();
		//得到搜索项
		 Intent intent = getIntent();
		 Bundle inputdata = intent.getExtras();
		 bookinfo b_info = new bookinfo();
		 b_info = (bookinfo)inputdata.getSerializable("S_book");
		 Params ="strSearchType="+b_info.getsearch_type()+"&historyCount=1&strText="+b_info.getsearch_content()+"&doctype=ALL&match_flag=forward&displaypg=20&sort=CATA_DATE&orderby=desc&showmode=list&dept=ALL&x=56&y=13";
		 sharebook_set = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		 boolean showonlyavail = sharebook_set.getBoolean("Libshowavail", false);
		 if(showonlyavail==true)Params+="&onlylendable=yes";
		 list.setOnScrollListener(new AbsListView.OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				if(scrollState == SCROLL_STATE_FLING)
				{
					if(view.getLastVisiblePosition()==(view.getCount()-1)){
						if(item_count!=0&& item_count%buffersize==0){
						b_func.toastinfo(getBaseContext(), "加载下60条数据");
           			  updateflag = true;
           			  }
					}
				}
			}			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				
			}
		});
		 //showDialog(PROGRESSBAR);
	        //添加点击
	        list.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					//setTitle("点击第"+arg2+"个项目");
					if(listItem.get(arg2).get("Itemappendtitle").toString()!=""){
					bookinfo book_click = new bookinfo();
					book_click.setbook_url(listItem.get(arg2).get("Itemappendtitle").toString());
					book_click.setbook_name(listItem.get(arg2).get("Itembigtitle").toString());
					book_click.setbook_owner(listItem.get(arg2).get("Itemsmalltitle").toString());
//					String avail= listItem.get(arg2).get("Itemsmalltitle").toString();
//					int index = avail.indexOf("：");
//					int indexlast = avail.lastIndexOf(" ", index);
//					book_click.setbook_avail(avail.substring(index+1, indexlast-1));
					Bundle inputdata = new Bundle();
			         inputdata.putSerializable("S_book", book_click);
			         //Intent intent = new Intent(leesearch.this,leeresult.class);
			         Intent intent = new Intent(leeresult.this,leelocation.class);
			         intent.putExtras(inputdata);
			         startActivity(intent);}
					else {
						Intent intent = new Intent(leeresult.this,leesearch.class);
				         startActivity(intent);
					}
				}
			});
	//	 新建线程
		 new Thread(new MyThread()).start();//新建一个子线程并运行
	        
	
		//添加长按点击  
        list.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {  
              
            @Override  	
            public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
                menu.setHeaderTitle("确定收藏");     
                menu.add(0, 0, 0, "收藏");       
            }
        });
	}
	 @Override  
	    public boolean onContextItemSelected(MenuItem item) {  
		 AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
	     int start = info.position;
	     bookinfo b_info= new bookinfo();
	     b_info.setbook_name(listItemtmp.get(start).get("Itembigtitle").toString());
	     b_info.setbook_owner(listItemtmp.get(start).get("Itemsmalltitle").toString());
	     b_info.setbook_url(listItem.get(start).get("Itemappendtitle").toString());
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
			   
			    Toast toast =Toast.makeText(leeresult.this, "已收藏",Toast.LENGTH_LONG);
  			toast.setGravity(Gravity.BOTTOM, 0, 0);
  			  toast.show();
			       
	        return super.onContextItemSelected(item);  
	    } 
	public void initializeresult()
	{
		listItem=null ;
		listItemAdapter=null;
		Firstblood =false; //第一次更新后设为true
		item_count =0;         //已经更新的个数
		total_num=0;			 //搜索到的总数
		updateflag = true;
		Params=null;
		//绑定LISTVIEW
		list=(ListView)findViewById(R.id.resultlist);
		//生成动态数组，加入数据
		listItem = new ArrayList<HashMap<String,Object>>();
		listItemtmp = new ArrayList<HashMap<String,Object>>();

		//生成容器
		listItemAdapter = new SimpleAdapter(this,listItem,R.layout.listitem,
				new String[]{"Itembigtitle","Itemsmalltitle","Itemurl"},
				new int[]{R.id.Itembigtitle,R.id.Itemsmalltitle,R.id.Itemurl});
		
		//添加并且显示
		list.setAdapter(listItemAdapter);
		//读取收藏夹数据
		book_store = getSharedPreferences("book_store", 0).edit();
		sharebook_store = getSharedPreferences("book_store",0);
		b_func = new bookfunc();
		
	}
	 //长按菜单响应函数  
   
    public void updateStatus(final int id) {
        // Be a good citizen.  Make sure UI changes fire on the UI thread.
        this.runOnUiThread(new Runnable() {
            public void run() {
            	switch (id)
            	{
            	case LISTUPDATE:
            		setTitle("已为您显示了"+item_count+"/"+total_num+"本图书");
            		bookfunc.clonelist(listItem, listItemtmp);
            		setProgressBarIndeterminateVisibility(false);
            		list.setVisibility(View.VISIBLE);
                 	listItemAdapter.notifyDataSetChanged();
                 	
                 	
                 	break;
            	case PROGRESSBAR:
            		setProgressBarIndeterminateVisibility(true);
            		break;
            	default:
            		break;
            	}
            		
                 
            }
        });
    }
    //线程更新数据
    class MyThread implements Runnable{  
  	  
        @Override  
        public void run() {  
        	while (!Thread.currentThread().isInterrupted()) {    
                
                Message message1 = new Message();
                Message message2 = new Message();
                  
              	 if(updateflag==true&&(Firstblood==false||total_num>item_count))   //如果第一次更新或者总数大于更新数
                 {        
              		message1.what = leeresult.PROGRESSBAR; 
              		leeresult.this.mHandler.sendMessage(message1);
              	
                 	Params=Params+"&"+"page="+(item_count/20+1);
       				String html = httphandler.httpget(url, Params);
       	  			 //parse html
       				 total_num=httphandler.http_parser(html,listItemtmp);
       				 if(total_num==0)item_count = 0;
       				 else item_count=listItemtmp.size();
                 	if(Firstblood==false)
                 		{
                 			Firstblood=true;
                 		}
                 		
                 	if(total_num!=0 && (item_count%buffersize==0))
                 		updateflag=false;
                 	
                 	
                 	message2.what = leeresult.LISTUPDATE; 
                 	leeresult.this.mHandler.sendMessage(message2);   
                    	
                 }           
                try {   
                     Thread.sleep(2000);    
                } catch (InterruptedException e) {   
                     Thread.currentThread().interrupt();   
                }   
           } 
        }
      }
    //handle中更新UI
    private Handler mHandler= new Handler(){

        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {   
            case leeresult.LISTUPDATE:
            	//if(mypDialog.isShowing())mypDialog.dismiss();
                 updateStatus(leeresult.LISTUPDATE); 
                 break;   
            case leeresult.PROGRESSBAR:
            	updateStatus(leeresult.PROGRESSBAR);
            	break;
        	}
        	super.handleMessage(msg);
        }};
 
        @Override
        protected void onResume() {
        	// TODO Auto-generated method stub
        	System.out.println("onresume");
        	super.onResume();
        	
        }
        @Override
        protected void onStart() {
        	// TODO Auto-generated method stub
        	System.out.println("onstart");
        	super.onStart();
        	
        }
        @Override
        protected void onStop() {
        	// TODO Auto-generated method stub
        	System.out.println("onstop");
        	super.onStop();
        	
        }
}