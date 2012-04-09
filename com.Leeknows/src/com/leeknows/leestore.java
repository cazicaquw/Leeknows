package com.leeknows;

import java.util.ArrayList;
import java.util.HashMap;

import com.leemodels.bookfunc;
import com.leemodels.bookinfo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class leestore extends Activity {
	public ArrayList<HashMap<String, Object>> listItem=null ;
	public ArrayList<HashMap<String, Object>> listItemtmp ;
	ListView list;
	public SimpleAdapter listItemAdapter=null;
	protected final static int GUIUPDATEIDENTIFIER = 0x101; 
	private static final int DELETEALLITEM = 1;
	public SharedPreferences sharebook_store;
	public Editor sharebook_editor;
	public bookfunc b_func ; 
	Message message;
	int store_count=0;
	
  
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//进度条标题栏显示
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); 
		setContentView(R.layout.resultlist);
		
		initializeresult();
		//显示进度条
		
		
		sharebook_store = getSharedPreferences("book_store",0);
		sharebook_editor = sharebook_store.edit();
		store_count = sharebook_store.getInt("store_count", 0);
		for(int i=0;i<store_count ; i++)
		{
			HashMap<String, Object> map = new HashMap<String, Object>();  
			map.put("Itembigtitle", sharebook_store.getString("book_name"+i, ""));
			map.put("Itemsmalltitle", sharebook_store.getString("book_owner"+i, ""));
			map.put("Itemappendtitle",sharebook_store.getString("book_url"+i, ""));
			listItemtmp.add(map);
		}
		message = new Message();   
        message.what = leestore.GUIUPDATEIDENTIFIER;
        mHandler.sendMessage(message);
        b_func.toastinfo(getBaseContext(), "加载完毕");
        //关闭进度条
        setProgressBarIndeterminateVisibility(false);
		 
	        //添加点击
	        list.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					//setTitle("点击第"+arg2+"个项目");
					bookinfo book_click = new bookinfo();
					book_click.setbook_url(listItem.get(arg2).get("Itemappendtitle").toString());
					book_click.setbook_name(listItem.get(arg2).get("Itembigtitle").toString());
					book_click.setbook_owner(listItem.get(arg2).get("Itemsmalltitle").toString());
					Bundle inputdata = new Bundle();
			         inputdata.putSerializable("S_book", book_click);
			         //Intent intent = new Intent(leesearch.this,leeresult.class);
			         Intent intent = new Intent(leestore.this,leelocation.class);
			         intent.putExtras(inputdata);
			         startActivity(intent);
				}
			});

	
		//添加长按点击  
        list.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {  
              
            @Override  	
            public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
                menu.setHeaderTitle("确定删除？");     
                menu.add(0, 0, 0, "删除");  
               
          
            }
        });
        
	}
	public void Deletebook(int start)
	{
    	store_count-=1;
    	
    	sharebook_editor.remove("book_name"+start);
    	sharebook_editor.remove("book_owner"+start);
    	sharebook_editor.remove("book_url"+start);
    	sharebook_editor.putInt("store_count", store_count);
    	sharebook_editor.commit();
    	listItemtmp.remove(start);
    	Message msg=new Message(); 
        msg.what = leestore.GUIUPDATEIDENTIFIER;
    	mHandler.sendMessage(msg);
	}
	
	public void Deletebookall()
	{
		store_count=0;
		sharebook_editor.clear();
		sharebook_editor.commit();
		listItemtmp.clear();
		Message msg=new Message(); 
        msg.what = leestore.GUIUPDATEIDENTIFIER;
    	mHandler.sendMessage(msg);
	}
	 //长按菜单响应函数  
    @Override  
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
    	int start = info.position;
    	Deletebook(start); 	
       // setTitle("点击了长按菜单里面的第"+item.getItemId()+"个项目");   
        return super.onContextItemSelected(item);  
    } 
    
	public void initializeresult()
	{
		b_func = new bookfunc();
		listItem=null ;
		listItemAdapter=null;
	
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
	}

    public void updateStatus() {
        // Be a good citizen.  Make sure UI changes fire on the UI thread.
        this.runOnUiThread(new Runnable() {
            public void run() {
            		listItem.clear();
            		bookfunc.clonelist(listItem, listItemtmp);
                 	listItemAdapter.notifyDataSetChanged();
                 
            }
        });
    }

    //handle中更新UI
    private Handler mHandler= new Handler(){

        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {   
            case leestore.GUIUPDATEIDENTIFIER:
            	
                 updateStatus(); 
             	bookfunc.savelist(sharebook_store, listItemtmp);
                 break;   
        	}
        	super.handleMessage(msg);
        }};
        
        /*CREAT MENU OPTION
         * (non-Javadoc)
         * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
         */
        public boolean onCreateOptionsMenu(Menu menu) {
            menu.add(0, DELETEALLITEM, 0, "一键清除");	

            return true;
        }
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case DELETEALLITEM	:
                	Deletebookall();               	
                    break;
            }
            return true;
        }
        

}