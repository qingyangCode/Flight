package com.xiaoqing.flight.entity;

import com.google.gson.Gson;


public class OneUsers {
	
	public TResponseObject ResponseObject;
	class TResponseObject{
		public TResponseData ResponseData;
	}
	
	class TResponseData{
		public TIAppObject IAppObject;
	}

	class TIAppObject{
		String UserPassWord;
		String ActiveStart;
		String UserCode;
		String Grant_S_M;
		String DepCode;
		String UserName;
		String CodeCheck;
		String SysVersion;
	}
	
	public static OneUsers parse(String jsonStr){
		Gson gson = new Gson();
		OneUsers fromJson = gson.fromJson(jsonStr, OneUsers.class);
		return fromJson;
	}

}
