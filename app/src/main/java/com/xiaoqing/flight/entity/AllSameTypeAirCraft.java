package com.xiaoqing.flight.entity;

import java.util.ArrayList;

import com.google.gson.Gson;


public class AllSameTypeAirCraft {
	
	public TResponseObject ResponseObject;
	class TResponseObject{
		public TResponseData ResponseData;
	}
	
	class TResponseData{
		public ArrayList<TIAppObject> IAppObject;
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
	
	public static AllSameTypeAirCraft parse(String jsonStr){
		Gson gson = new Gson();
		AllSameTypeAirCraft fromJson = gson.fromJson(jsonStr, AllSameTypeAirCraft.class);
		return fromJson;
	}

}
