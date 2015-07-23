package com.uandme.flight.entity;

import java.util.ArrayList;

import com.google.gson.Gson;


public class AllAirTypeCraft {
	
	public TResponseObject ResponseObject;
	class TResponseObject{
		public TResponseData ResponseData;
	}
	
	class TResponseData{
		public ArrayList<TIAppObject> IAppObject;
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
	
	public static AllAirTypeCraft parse(String jsonStr){
		Gson gson = new Gson();
		AllAirTypeCraft fromJson = gson.fromJson(jsonStr, AllAirTypeCraft.class);
		return fromJson;
	}

}
