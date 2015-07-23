package com.uandme.flight.entity;

import java.util.ArrayList;

import com.google.gson.Gson;


public class AllUsers {
	
	public TResponseObject ResponseObject;
	class TResponseObject{
		public TResponseData ResponseData;
	}
	
	class TResponseData{
		public ArrayList<TIAppObject> IAppObject;
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
	
	public static AllUsers parse(String jsonStr){
		Gson gson = new Gson();
		AllUsers fromJson = gson.fromJson(jsonStr, AllUsers.class);
		return fromJson;
	}

}
