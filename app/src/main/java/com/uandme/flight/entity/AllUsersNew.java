package com.uandme.flight.entity;

import java.util.ArrayList;

import com.google.gson.Gson;


public class AllUsersNew {
	
	public TResponseObject ResponseObject;
	class TResponseObject{
		public TResponseData ResponseData;
	}
	
	class TResponseData{
		public ArrayList<TIAppObject> IAppObject;
	}

	class TIAppObject{
		String UserPassWord;
		String UserName;
		String UserCode;
		String AcTypes;
	}
	
	public static AllUsersNew parse(String jsonStr){
		Gson gson = new Gson();
		AllUsersNew fromJson = gson.fromJson(jsonStr, AllUsersNew.class);
		return fromJson;
	}

}
