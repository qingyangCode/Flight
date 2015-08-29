package com.xiaoqing.flight.entity;

import com.google.gson.Gson;

public class TUserInfo {
	public TResponseObject ResponseObject;
	
	public class TResponseObject{
		public String ResponseCode;
		public TResponseData ResponseData;
		public String ResponseErr;
	}
	
	public class TResponseData{
		public TIAppObject IAppObject;
	}
	
	public class TIAppObject{
		public String GUIDCode;
	}

	public static TUserInfo parse(String jsonStr){

		if(jsonStr.contains("\"ResponseData\":\"\""))
			jsonStr = jsonStr.replace("\"ResponseData\":\"\"","\"ResponseData\":{}");
		Gson gson = new Gson();
		TUserInfo fromJson = null;
		try {
			fromJson = gson.fromJson(jsonStr, TUserInfo.class);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return fromJson;
	}
	
}
