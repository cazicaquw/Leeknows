package com.leemodels;

import java.io.Serializable;

public class bookinfo implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String search_type;			//搜索类型：书名，作者
	private String search_content; 		//搜索内容
	private String search_num;  		//总共找到的书数
	private String book_type;			//图书类型：中文图书，西文图书
	private String book_name;			//图书名称
	private String book_id;				//图书索引号
	private String book_num;			//馆藏复本
	private String book_avail;			//可接复本
	private String book_owner;			//作者
	private String book_press;			//出版社
	private String book_url;

	public bookinfo()
	{
	}
	
	public bookinfo(String search_type , String search_content)
	{
		this.search_type=search_type;
		this.search_content=search_content;
	}
	//search_type method
	public String getsearch_type()
	{
		return search_type;
	}
	public void setsearch_type(String search_type)
	{
		this.search_type=search_type;
	}
	
	//search_content method
	public String getsearch_content()
	{
		return search_content;
	}
	public void setsearch_content(String search_content)
	{
		this.search_content=search_content;
	}
	//search_num method
	public String getsearch_num()
	{
		return search_num;
	}
	public void setsearch_num(String search_num)
	{
		this.search_num=search_num;
	}
	//book_type method
	public String getbook_type()
	{
		return book_type;
	}
	public void setbook_type(String book_type)
	{
		this.book_type=book_type;
	}
	//book_name method
	public String getbook_name()
	{
		return book_name;
	}
	public void setbook_name(String book_name)
	{
		this.book_name=book_name;
	}
	//book_id method
	public String getbook_id()
	{
		return book_id;
	}
	public void setbook_id(String book_id)
	{
		this.book_id=book_id;
	}
	//book_num method
	public String getbook_num()
	{
		return book_num;
	}
	public void setbook_num(String book_num)
	{
		this.book_num=book_num;
	}
	//book_avail method
	public String getbook_avail()
	{
		return book_avail;
	}
	public void setbook_avail(String book_avail)
	{
		this.book_avail=book_avail;
	}
	//book_owner method
	public String getbook_owner()
	{
		return book_owner;
	}
	public void setbook_owner(String book_owner)
	{
		this.book_owner=book_owner;
	}
	//book_press method
	public String getbook_press()
	{
		return book_press;
	}
	public void setbook_press(String book_press)
	{
		this.book_press=book_press;
	}
	//book_url method
	public String getbook_url()
	{
		return book_url;
	}
	public void setbook_url(String book_url)
	{
		this.book_url=book_url;
	}
}
