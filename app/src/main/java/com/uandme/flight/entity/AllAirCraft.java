package com.uandme.flight.entity;

import com.google.gson.Gson;
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
		AllAirCraft fromJson = gson.fromJson(jsonStr, AllAirCraft.class);
		return fromJson;
	}

}
