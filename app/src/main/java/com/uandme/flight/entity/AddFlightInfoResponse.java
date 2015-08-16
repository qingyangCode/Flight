package com.uandme.flight.entity;

import com.google.gson.Gson;
import com.uandme.flight.data.dao.AllAcType;
import java.util.ArrayList;

/**
 * Created by QingYang on 15/8/8.
 * 增加航班信息
 */
public class AddFlightInfoResponse {
    public TResponseObject ResponseObject;
    public class TResponseObject{
        public TResponseData ResponseData;
        public int ResponseCode;
    }

    public class TResponseData{
        public ArrayList<AllAcType> IAppObject;
    }

    //改用数据库字段自动生成的bean
    @Deprecated
    public class TIAppObject{
        public String FlightId;
        public String FlightDate;
        public String AircraftReg;
        public String AircraftType;
        public String FlightNo;
        public String Dep4Code;
        public String DepAirportName;
        public String Arr4Code;
        public String ArrAirportName;
        public String MaxFule;
        public String RealFule;
        public String SlieFule;
        public String RouteFule;
        public String TofWeight;
        public String LandWeight;
        public String NoFuleWeight;
        public String AirportLimitWeight;
        public String BalancePic;
        public String BalancePicName;
        public String OpUser;
        public String OpDate;
    }

    public static AddFlightInfoResponse parse(String jsonStr){
        Gson gson = new Gson();
        if(jsonStr.contains("\"ResponseData\":\"\""))
            jsonStr = jsonStr.replace("\"ResponseData\":\"\"","\"ResponseData\":{}");
        AddFlightInfoResponse fromJson = gson.fromJson(jsonStr, AddFlightInfoResponse.class);
        return fromJson;
    }
}
