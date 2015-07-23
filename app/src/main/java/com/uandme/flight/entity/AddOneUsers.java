package com.uandme.flight.entity;

import com.google.gson.Gson;


public class AddOneUsers {
	
	public TResponseObject ResponseObject;
	class TResponseObject{
		public TResponseData ResponseData;
	}
	
	class TResponseData{
		public TIAppObject IAppObject;
	}

	class TIAppObject{
		String AircraftReg;
		String AcRegCagLj;
		String OpUser;
		String AcRegCagWeight;
		String SeatCode;
		String SeatLastLimit;
		String SeatType;
		String AcTypeSeatLimit;
		String AcTypeLj;
		String PassagerName;
		String FlightId;
		String SeatId;
		String RealWeight;
		String OpDate;
	}
	
	public static AddOneUsers parse(String jsonStr){
		Gson gson = new Gson();
		AddOneUsers fromJson = gson.fromJson(jsonStr, AddOneUsers.class);
		return fromJson;
	}

}
