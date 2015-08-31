package com.xiaoqing.flight.entity;

import com.google.gson.Gson;
import com.xiaoqing.flight.data.dao.AllAircraft;
import java.util.ArrayList;



public class AllAirCraftResponse extends BaseResponse{
	
	public TResponseObject ResponseObject;
	public class TResponseObject{
		public TResponseData ResponseData;
		public int ResponseCode;
		public String ResponseErr;
	}
	
	public class TResponseData{
		public ArrayList<AllAircraft> IAppObject;
	}

	@Deprecated
	public class TIAppObject{
		public String AircraftReg;
		public String UserCode;
		public String AircraftType;
		public String Bw;
		public String Lj;
		public String LayoutPic;
		public String OpDate;
		public String SysVersion;
		public String AcRemark;

	}
	
	public static AllAirCraftResponse parse(String jsonStr){
		Gson gson = new Gson();
		AllAirCraftResponse fromJson = null;
		if(jsonStr.contains("\"ResponseData\":\"\""))
			jsonStr = jsonStr.replace("\"ResponseData\":\"\"","\"ResponseData\":{}");
		try {
			fromJson = gson.fromJson(jsonStr, AllAirCraftResponse.class);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return fromJson;
	}

}
