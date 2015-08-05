package com.uandme.flight.entity;

import java.util.ArrayList;

import com.google.gson.Gson;


public class AllUsers {

	
	public TResponseObject ResponseObject;
	public class TResponseObject{
		public TResponseData ResponseData;
		public int ResponseCode;
	}
	
	public class TResponseData{
		public ArrayList<TIAppObject> IAppObject;
	}

	public class TIAppObject{
		public String UserPassWord;
		public String ActiveStart;
		public String UserCode;
		public String Grant_S_M;
		public String DepCode;
		public String UserName;
		public String CodeCheck;
		public String SysVersion;
	}
	
	public static AllUsers parse(String jsonStr){
		Gson gson = new Gson();
		if(jsonStr.contains("\"ResponseData\":\"\""))
			jsonStr = jsonStr.replace("\"ResponseData\":\"\"","\"ResponseData\":{}");
		AllUsers fromJson = gson.fromJson(jsonStr, AllUsers.class);
		return fromJson;
	}

}
