package com.xiaoqing.flight.entity;

import com.google.gson.Gson;
import com.xiaoqing.flight.data.dao.User;

/**
 * Created by WeiShuangbo on 2015/5/22.
 */
public class LoginUserInfoResponse extends  BaseResponse{
	public TResponseObject ResponseObject;
	private String codeCheck;

	public static class TResponseObject{
		public int ResponseCode;
		public String ResponseErr;
		public TResponseData ResponseData;
	}
	
	public static class TResponseData{
		public User IAppObject;
	}
	
	//public static class TIAppObject{
	//	public String UserPassWord;
	//	public String ActiveStart;
	//	public String UserCode;
	//	public String Grant_S_M;
	//	public String DepCode;
	//	public String UserName;
	//	public String CodeCheck;
	//}

	public static LoginUserInfoResponse parse(String jsonStr){
		Gson gson = new Gson();
		if(jsonStr.contains("\"ResponseData\":\"\""))
			jsonStr = jsonStr.replace("\"ResponseData\":\"\"","\"ResponseData\":{}");
		LoginUserInfoResponse fromJson = gson.fromJson(jsonStr, LoginUserInfoResponse.class);
		return fromJson;
	}

	public void setCodeCheck(String codeCheck) {
		this.codeCheck = codeCheck;
	}

	public String getCodeCheck() {
		return codeCheck;
	}
}
