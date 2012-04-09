package com.httpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;



//"http://202.119.83.14:8080/uopac/opac/openlink.php?strSearchType=title&historyCount=1&strText=android&doctype=ALL&match_flag=forward&displaypg=20&sort=CATA_DATE&orderby=desc&showmode=list&dept=ALL&x=56&y=13"
public class httphandler
{
	//用于保存cookie
	public static SharedPreferences cookieshare;
	public static Editor cookieedit;
	
	public static String Stringencode(HttpResponse response)
	{
		StringBuffer sb = new StringBuffer();  
        HttpEntity entity = response.getEntity();  
        InputStream is;
		try {
			is = entity.getContent();
			BufferedReader br = new BufferedReader(  
	                new InputStreamReader(is,"UTF-8"));  
	        String data = "";  

	        while ((data = br.readLine()) != null) {  
	            sb.append(data);  
	        }  
	        
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		String strResult = sb.toString(); 
        return strResult;
	}
	
	public static String httpget (String url,String Params)
	{
	    String html = null;
		DefaultHttpClient httpClient = new DefaultHttpClient();
		//创建HttpGet实例
		HttpGet request = new HttpGet(url+"?"+Params);
		try 
		    {
		        //连接服务器
			HttpResponse response =httpClient.execute(request);  
		        
		        //取得数据记录
		        HttpEntity entity = response.getEntity();
		        //取得数据记录内容
	
		        InputStream is = entity.getContent();
		        
		        byte[] buff =StreamTool.readInputStream(is);
		        
		      html = new String(buff);
		      httpClient.getConnectionManager().shutdown(); 
		      return html;
		   }
		     catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return null;
			
	}
	public static String httpget_session (String url,String Params,String Sessionid)
	{
	    String html = null;
		DefaultHttpClient httpClient = new DefaultHttpClient();
		//创建HttpGet实例
		HttpGet request = new HttpGet(url+"?"+Params);
		request.setHeader("Cookie",Sessionid);
		
		try 
		    {
		        //连接服务器
			HttpResponse response =httpClient.execute(request);  
		        
		        //取得数据记录
		        HttpEntity entity = response.getEntity();
		        //取得数据记录内容
	
		        InputStream is = entity.getContent();
		        
		        byte[] buff =StreamTool.readInputStream(is);
		        
		      html = new String(buff);
		      
		      httpClient.getConnectionManager().shutdown(); 
		      return html;
		   }
		     catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return null;
			
	}
	public static boolean http_post(List <NameValuePair> params,String uriAPI)
	{
        HttpPost httpRequest = new HttpPost(uriAPI); 
        DefaultHttpClient httpClient = new DefaultHttpClient();
        /*
         * NameValuePair实现请求参数的封装
        */
        try 
        { 
          /* 添加请求参数到请求对象*/
          httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8)); 
          /*发送请求并等待响应*/
          HttpResponse httpResponse = httpClient.execute(httpRequest); 
          /*若状态码为200 ok*/
          if(httpResponse.getStatusLine().getStatusCode() == 200)  
          { 
            /*读返回数据*/
            //String strResult = EntityUtils.toString(httpResponse.getEntity());
        	  return true;
          } 
          else 
          { 
            //mTextView1.setText("Error Response: "+httpResponse.getStatusLine().toString()); 
        	  return false;
          } 
        } 
        catch (ClientProtocolException e) 
        {   
          e.printStackTrace(); 
        } 
        catch (IOException e) 
        {   
          e.printStackTrace(); 
        } 
        catch (Exception e) 
        {   
          e.printStackTrace();  
        }
		return false; 
	}
	
	public static String http_post_session(List <NameValuePair> params,String uriAPI,String Sessionid)
	{
        HttpPost httpRequest = new HttpPost(uriAPI); 
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpRequest.setHeader("Cookie",Sessionid);
        /*
         * NameValuePair实现请求参数的封装
        */
        try 
        { 
          /* 添加请求参数到请求对象*/
          httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8)); 
          /*发送请求并等待响应*/
          HttpResponse httpResponse = httpClient.execute(httpRequest); 
          /*若状态码为200 ok*/
          if(httpResponse.getStatusLine().getStatusCode() == 200)  
          { 
            /*读返回数据*/
        	  String strResult = httphandler.Stringencode(httpResponse);
            //String strResult = EntityUtils.toString(httpResponse.getEntity());
        	  return strResult;
          } 
          else 
          { 
            //mTextView1.setText("Error Response: "+httpResponse.getStatusLine().toString()); 
        	  return "";
          } 
        } 
        catch (ClientProtocolException e) 
        {   
          e.printStackTrace(); 
        } 
        catch (IOException e) 
        {   
          e.printStackTrace(); 
        } 
        catch (Exception e) 
        {   
          e.printStackTrace();  
        }
		return ""; 
	}
	
	public static String http_post_cookie(Context context,List <NameValuePair> params,String uriAPI)
	{
        HttpPost httpRequest = new HttpPost(uriAPI); 
        DefaultHttpClient httpClient = new DefaultHttpClient();
        /*
         * NameValuePair实现请求参数的封装
        */
        try 
        { 
          /* 添加请求参数到请求对象*/
          httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8)); 
          /*发送请求并等待响应*/
          HttpResponse httpResponse = httpClient.execute(httpRequest); 
          /*若状态码为200 ok*/
          if(httpResponse.getStatusLine().getStatusCode() == 200)  
          { 
            /*读返回数据*/
            String strResult = EntityUtils.toString(httpResponse.getEntity());
            List<Cookie> cookies =httpClient.getCookieStore().getCookies();
            if (!cookies.isEmpty()) 
            {
      			 //保存cookie  
            	Cookie cookie = cookies.get(0);  
      		  
	            cookieshare = context.getSharedPreferences("cookieshare",0);
	            cookieedit = cookieshare.edit();
	            cookieedit.putString("Sessionid", cookie.getName()+"="+cookie.getValue());
	            cookieedit.commit();

            }
            return strResult;
          }
          else 
          { 
            //mTextView1.setText("Error Response: "+httpResponse.getStatusLine().toString()); 
        	  return "";
          } 
        } 
        catch (ClientProtocolException e) 
        {   
          e.printStackTrace(); 
        } 
        catch (IOException e) 
        {   
          e.printStackTrace(); 
        } 
        catch (Exception e) 
        {   
          e.printStackTrace();  
        }
		return null; 
	}
	public static int http_parser(String html,ArrayList<HashMap<String, Object>> list)
	{
		Document doc = Jsoup.parse(html);
		Elements divs = doc.select("div");
		int lengthdiv = divs.size();
		if(lengthdiv>11){
			Elements ele_num = doc.select("#titlenav");
			String search_num_str = ele_num.get(0).text();
			String strsearch[]=search_num_str.split(" ");
			Elements book_url = doc.select("#list_books a[href]");
			Elements book_name = doc.select("#list_books");
			int length=0,tmp;
			String test = strsearch[1].substring(4);
			length=Integer.parseInt(test);
			tmp=length;
			
			length=book_name.size();
			
			for (int i = 0; i < length; i++)
			{
				HashMap<String, Object> map = new HashMap<String, Object>();  
				String tmp1 =book_name.get(i).text();
				String strArray[]=tmp1.split(" ");
				int strlen = strArray.length;
				int index_num=-1;
				int j;
				for(j =0 ;j<strlen;j++)
				{
					if(strArray[j].indexOf("馆藏复本")==0)
					{
						index_num=j;
						j=strlen;
					}
				}
				String book_owner_str="",book_name_str=strArray[0].substring(4);
				for(j=index_num+2;j<strlen-1;j++)
				{
					 book_owner_str+=strArray[j];
				}
				for(j=1;j<index_num-1;j++)
				{
					book_name_str += strArray[j];
				}
				// map.put("ItemImage",R.drawable.checked);//图像资源的ID  
		            map.put("Itembigtitle", book_name_str);  
		            map.put("Itemsmalltitle", strArray[index_num-1]+" "+strArray[index_num+1]+"\n"+book_owner_str+" "+strArray[strlen-1]);  
		            map.put("Itemappendtitle",book_url.get(i).attr("href"));
		            
//				map.put("book_name", book_name_str);
//				map.put("book_type", strArray[0].substring(0,4));
//				map.put("book_id", strArray[index_num-1]);
//				map.put("book_num", strArray[index_num]);
//				map.put("book_avail", strArray[index_num+1]);
//				map.put("book_owner",book_owner_str);
//				map.put("book_press", strArray[strlen-1]);  //最后一个是出版社
//				map.put("book_url", book_url.get(i).attr("href"));	
//				map.put("book_content", strArray[0].substring(0,4)+" "+strArray[index_num-1]+"\n"+strArray[index_num]+" "+strArray[index_num+1]+"\n"+book_owner_str+" "+strArray[strlen-1]);
//				map.put("search_num", tmp);
				list.add(map);
			}	
			return tmp;
		}
		else {
			
			HashMap<String, Object> map = new HashMap<String, Object>();  
			// map.put("ItemImage",R.drawable.checked);//图像资源的ID  
	            map.put("Itembigtitle", "亲，很抱歉");  
	            map.put("Itemsmalltitle","没有找到这本书 ,请出门左拐");  
	            map.put("Itemappendtitle","");
			list.add(map);
			return 0;
		}
	}
	public static String http_parser_local(String html,ArrayList<HashMap<String, Object>> list)
	{
		Document doc = Jsoup.parse(html);
		Elements divs = doc.select("td.whitetext");
		Elements booklist = doc.select("dl.booklist");
		String book_detail=null;
		
		int lengtasd = divs.size();
		for(int i=0;i<lengtasd;)
		{
			HashMap<String, Object> map = new HashMap<String, Object>();  
			map.put("local_item1", divs.get(i+4).text());
			map.put("local_item2", divs.get(i+5).text());
			list.add(map);
			i+=6;
		}
		lengtasd = booklist.size();
		for(int i=0;i<lengtasd;i++)
		{
			if(booklist.get(i).text().indexOf("提要")==0)
				book_detail =booklist.get(i).text()+'\n';
		}
		
		return book_detail;
	}
	
	public static String http_parser_mylib(String html,ArrayList<HashMap<String, Object>> list)
	{
		Document doc = Jsoup.parse(html);
		Elements divs = doc.select("td.whitetext");
		Elements na = doc.select("div[style=color:#FFF; float:right;padding:5px 20px 0 0px]");
		String name =na.text();
		String strsearch[]=name.split(" ");
		int lengtasd = divs.size();
		for(int i=0;i<lengtasd;)
		{
			HashMap<String, Object> map = new HashMap<String, Object>();  
			map.put("Itembigtitle", divs.get(i+1).text());
			map.put("Itemsmalltitle", divs.get(i+2).text()+ "~"+divs.get(i+3).text()+" "+divs.get(i+5).text());
			map.put("Itemappendtitle",divs.get(i).text());
			list.add(map);
			i+=8;
		}
		return strsearch[0];
		
	}
	
	public static void http_parser_myresv(String html,ArrayList<HashMap<String, Object>> list)
	{
		Document doc = Jsoup.parse(html);
		Elements trs = doc.select("tr");
		
		int lengtasd = trs.size();
		for(int i=1;i<lengtasd;i++)
		{
			Elements tds = trs.get(i).select("td");
			HashMap<String, Object> map = new HashMap<String, Object>();  
			map.put("Itembigtitle", tds.get(1).text());
			map.put("Itemsmalltitle", tds.get(0).text()+ " 预约到期:"+tds.get(5).text()+" 取书地："+tds.get(6).text()+" 状态："+tds.get(7).text());
			Elements inps = trs.get(i).select("input[value]");
			if(inps.size()!=0)
				map.put("Itemappendtitle",inps.get(1).attr("onclick"));
			else 
				map.put("Itemappendtitle","");
			list.add(map);
			
		}		
	}
	
	public static void http_parser_preg(String html,ArrayList<HashMap<String, Object>> list)
	{
		Document doc = Jsoup.parse(html);
//		Elements divs = doc.select("td.whitetext");
//		Elements parms = doc.select("input[value]");
		Elements trs = doc.select("tr");
		
//		int lengtasd = divs.size();
//		for(int i=0;i<lengtasd;)
//		{
//			HashMap<String, Object> map = new HashMap<String, Object>();  
//			map.put("Itembigtitle", divs.get(i+1).text());
//			map.put("Itemsmalltitle", divs.get(i+5).text());
//			map.put("Itemappendtitle",parms.get(i/8*5+3).attr("value")+" "+parms.get(i/8*5+4).attr("value")+" "+parms.get(i/8*5+5).attr("value")+" "+parms.get(i/8*5+6).attr("value"));
//			list.add(map);
//			i+=8;
//		}
		int len = trs.size();
		for(int i=1 ;i<len-1;i++)
		{
			Elements tds = trs.get(i).select("td");
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("Itembigtitle", tds.get(1).text());
			map.put("Itemsmalltitle", tds.get(5).text());
			Elements inps = trs.get(i).select("input[value]");
			map.put("Itemappendtitle",inps.get(1).attr("value")+" "+inps.get(2).attr("value"));
			list.add(map);
		}
		
	}
	
	
}