package com.uandme.flight.entity;

import com.google.gson.Gson;
import com.uandme.flight.data.dao.AllAircraft;
import java.util.ArrayList;

/**
 * 获取机型用户授权信息
 */
public class GrantsByUserCodeResponse {
	
	public TResponseObject ResponseObject;
	public class TResponseObject{
		public TResponseData ResponseData;
		public int ResponseCode;
	}
	
	public class TResponseData{
		public ArrayList<TIAppObject> IAppObject;
	}

	@Deprecated
	public class TIAppObject{
		public String GrantsId;
		public String AircraftReg;
		public String UserCode;
		public String AcType;
		public String AcLongType;
		public String Caption;
		public String OpUser;
		public String OpDate;
		public String SysVersion;

	}
	
	public static GrantsByUserCodeResponse parse(String jsonStr){
		Gson gson = new Gson();
		GrantsByUserCodeResponse fromJson = null;
		if(jsonStr.contains("\"ResponseData\":\"\""))
			jsonStr = jsonStr.replace("\"ResponseData\":\"\"","\"ResponseData\":{}");
		try {
			fromJson = gson.fromJson(jsonStr, GrantsByUserCodeResponse.class);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return fromJson;
	}

}
