package com.leemodels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.httpclient.httphandler;
import com.leeknows.leesetting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.Window;
import android.widget.Toast;

public class bookfunc {
	
	public static String url = "http://202.119.83.14:8080/uopac/opac/item.php";
	public static String uripost = "http://202.119.83.14:8080/uopac/reader/redr_verify.php";
	public static String url_preg = "http://202.119.83.14:8080/uopac/opac/userpreg.php";
	public static String url_result = "http://202.119.83.14:8080/uopac/opac/userpreg_result.php";
	public static String sessionid;
	public SharedPreferences cookieshare;
	
	
	public bookfunc()
	{
		sessionid = "";
		
	}
	public boolean mylib_login(Context context)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String username = prefs.getString("Libuser", "");
        String password = prefs.getString("Libpass", "");
        
		List <NameValuePair> params = new ArrayList <NameValuePair>(); 
        params.add(new BasicNameValuePair("number",username )); 
        params.add(new BasicNameValuePair("passwd", password));
        params.add(new BasicNameValuePair("select", "cert_no"));
        
        String html = httphandler.http_post_cookie(context,params, uripost);
        if(html.equals(""))
        {
        	//登录失败处理 
        	toastinfo(context,"网络没有响应");
        	return false;
        }
        else
        {
        	Document doc = Jsoup.parse(html);
        	Elements inps = doc.select("input[type=submit]");
        	if(inps.size()==0)
        	{
        		cookieshare = context.getSharedPreferences("cookieshare",0);
       		 	sessionid = cookieshare.getString("Sessionid", "");
        		 toastinfo(context,"登录成功");
        		 return true;
        	}
        	else 
        	{
        		toastinfo(context,"登录失败，请检查用户名密码");
        		return false;
        	}
        }
	}
	
	public String getsessionid()
	{
		return sessionid;      
	}
    public static void clonelist(ArrayList<HashMap<String, Object>> list,ArrayList<HashMap<String, Object>> listtmp)
    {
    	for(int i=list.size();i<listtmp.size();i++)
    	{
    		list.add(listtmp.get(i));
    	}
    }
    public static int findduplicatedstore(SharedPreferences sharefile,String cmp_str)
    {
    	int count_num = sharefile.getInt("store_count", 0);
    	System.out.println(count_num);
    	String str;
    	for(int i=0; i <count_num;i++)
    	{
    		str = sharefile.getString("book_name"+i,"");
    		if( str.equalsIgnoreCase(cmp_str))
    		{
    			return i ; //找到重复的
    		}
    	}
    	return -1;
    }
    public static void savelist(SharedPreferences sharefile,ArrayList<HashMap<String, Object>> list)
    {
    	sharefile.edit().clear();  	
		for(int i=0;i<list.size() ; i++)
		{
			sharefile.edit().putString("book_name"+i,list.get(i).get("Itembigtitle").toString());
			sharefile.edit().putString("book_owner"+i,list.get(i).get("Itemsmalltitle").toString());
			sharefile.edit().putString("book_url"+i,list.get(i).get("Itemappendtitle").toString());
		}
		sharefile.edit().putInt("store_count", list.size());
    }
    public void toastinfo(Context context ,String message)
    {
    	final Context context_this = context;
    	final String message_this = message;
    	Handler handler = new Handler(Looper.getMainLooper());
    	handler.post(new Runnable() {
    		public void run() {
    			Toast.makeText(context_this, message_this,
    					Toast.LENGTH_SHORT).show();
    		}
    	});
    }
}

