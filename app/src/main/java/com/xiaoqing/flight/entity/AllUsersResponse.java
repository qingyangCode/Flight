package com.xiaoqing.flight.entity;

import com.xiaoqing.flight.data.dao.User;
import java.util.ArrayList;

import com.google.gson.Gson;


public class AllUsersResponse extends BaseResponse{

	
	public TResponseObject ResponseObject;
	public class TResponseObject{
		public TResponseData ResponseData;
		public int ResponseCode;
		public int SysVersion;
	}
	
	public class TResponseData{
		public ArrayList<User> IAppObject;
	}



	//public class TIAppObject{
	//	public String UserPassWord;
	//	public String ActiveStart;
	//	public String UserCode;
	//	public String Grant_S_M;
	//	public String DepCode;
	//	public String UserName;
	//	public String CodeCheck;
	//	public String SysVersion;
	//}
	
	public static AllUsersResponse parse(String jsonStr){
		Gson gson = new Gson();
		if(jsonStr.contains("\"ResponseData\":\"\""))
			jsonStr = jsonStr.replace("\"ResponseData\":\"\"","\"ResponseData\":{}");
		AllUsersResponse fromJson = gson.fromJson(jsonStr, AllUsersResponse.class);
		return fromJson;
	}

}
