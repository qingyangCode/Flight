package com.xiaoqing.flight.entity;

import java.util.ArrayList;

import com.google.gson.Gson;


public class AllUsersNewResponse {
	
	public TResponseObject ResponseObject;
	class TResponseObject{
		public TResponseData ResponseData;
		public int ResponseCode;
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
	
	public static AllUsersNewResponse parse(String jsonStr){
		Gson gson = new Gson();
		AllUsersNewResponse fromJson = null;
		if(jsonStr.contains("\"ResponseData\":\"\""))
			jsonStr = jsonStr.replace("\"ResponseData\":\"\"","\"ResponseData\":{}");
		try {
			fromJson = gson.fromJson(jsonStr, AllUsersNewResponse.class);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return fromJson;
	}

}
