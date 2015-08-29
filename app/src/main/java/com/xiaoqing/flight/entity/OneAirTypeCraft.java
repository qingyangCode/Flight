package com.xiaoqing.flight.entity;

import com.google.gson.Gson;


public class OneAirTypeCraft {
	
	public TResponseObject ResponseObject;
	class TResponseObject{
		public TResponseData ResponseData;
	}
	
	class TResponseData{
		public TIAppObject IAppObject;
	}

	class TIAppObject{
		String AircraftTypeChName;
		String UserCode;
		String AircraftType;
		String Mzfw;
		String LandWeightLimit;
		String MacFlg;
		String PortLimit;
		String Mac;
		String TofWeightLimit;
		String LimitType;
		String Mac2;
		String SysVersion;
		String OpDate;
	}
	
	public static OneAirTypeCraft parse(String jsonStr){
		Gson gson = new Gson();
		OneAirTypeCraft fromJson = gson.fromJson(jsonStr, OneAirTypeCraft.class);
		return fromJson;
	}

}
