package com.uandme.flight.entity;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import java.util.ArrayList;



public class AllAirCraft {
	
	public TResponseObject ResponseObject;
	public class TResponseObject{
		public TResponseData ResponseData;
	}
	
	public class TResponseData{
		public ArrayList<TIAppObject> IAppObject;
	}

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
	
	public static AllAirCraft parse(String jsonStr){
		Gson gson = new Gson();
		AllAirCraft fromJson = null;
		try {
			fromJson = gson.fromJson(jsonStr, AllAirCraft.class);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return fromJson;
	}

}
