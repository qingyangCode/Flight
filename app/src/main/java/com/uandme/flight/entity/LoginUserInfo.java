package com.uandme.flight.entity;

import com.google.gson.Gson;


/**
 * Created by WeiShuangbo on 2015/5/22.
 */
public class LoginUserInfo {
	public TResponseObject ResponseObject;
	private String checkCode;
	private String userName;
	
	public static class TResponseObject{
		public String ResponseCode;
		public TResponseData ResponseData;
	}
	
	public static class TResponseData{
		public TIAppObject IAppObject;
	}
	
	public static class TIAppObject{
		public String UserPassWord;
		public String ActiveStart;
		public String UserCode;
		public String Grant_S_M;
		public String DepCode;
		public String UserName;
		public String CodeCheck;
	}
	
	public String getUserCode(){
		return ResponseObject.ResponseData.IAppObject.UserCode;
	}
	
	public String getUserCodeCheck(){
		return ResponseObject.ResponseData.IAppObject.CodeCheck;
	}
	
	public static LoginUserInfo parse(String jsonStr){
		Gson gson = new Gson();
		LoginUserInfo fromJson = gson.fromJson(jsonStr, LoginUserInfo.class);
		return fromJson;
	}

	public TResponseObject getResponseObject() {
		return ResponseObject;
	}

	public void setResponseObject(TResponseObject responseObject) {
		ResponseObject = responseObject;
	}

	public String getCheckCode() {
		return checkCode;
	}

	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
