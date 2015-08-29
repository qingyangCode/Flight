package com.xiaoqing.flight.entity;

import com.google.gson.Gson;


public class OneAirCraft {
	
	public TResponseObject ResponseObject;
	class TResponseObject{
		public TResponseData ResponseData;
	}
	
	class TResponseData{
		public TIAppObject IAppObject;
	}

	class TIAppObject{
		String AircraftReg;
		String UserCode;
		String AircraftType;
		String Bw;
		String Lj;
		String LayoutPic;
		String OpDate;
		String SysVersion;
		String AcRemark;
	}
	
	public static OneAirCraft parse(String jsonStr){
		Gson gson = new Gson();
		OneAirCraft fromJson = gson.fromJson(jsonStr, OneAirCraft.class);
		return fromJson;
	}

}
