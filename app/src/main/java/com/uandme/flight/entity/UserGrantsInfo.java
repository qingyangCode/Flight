package com.uandme.flight.entity;

import com.google.gson.Gson;


public class UserGrantsInfo {
	
	public TResponseObject ResponseObject;
	class TResponseObject{
		public TResponseData ResponseData;
	}
	
	class TResponseData{
		public TIAppObject IAppObject;
	}

	class TIAppObject{
		String GrantsId;
		String AircraftReg;
		String UserCode;
		String AcType;
		String AcLongType;
		String Caption;
		String OpUser;
		String OpDate;
		String SysVersion;
	}
	
	public static UserGrantsInfo parse(String jsonStr){
		Gson gson = new Gson();
		UserGrantsInfo fromJson = gson.fromJson(jsonStr, UserGrantsInfo.class);
		return fromJson;
	}

}
