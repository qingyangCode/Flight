package com.xiaoqing.flight.entity;

import com.google.gson.Gson;
import com.xiaoqing.flight.data.dao.AllAcType;

/**
 * Created by QingYang on 15/8/8.
 * 增加航班信息
 */
public class AddFlightInfoResponse extends BaseResponse{
    public TResponseObject ResponseObject;
    public class TResponseObject{
        public TResponseData ResponseData;
        public int ResponseCode;
        public String ResponseErr;
    }
    public class TResponseData{
        public TIAppObject IAppObject;
    }

    //改用数据库字段自动生成的bean
    @Deprecated
    public class TIAppObject{
        public String SlieFule;
        public String BalancePic;
        public String AircraftType;
        public String AircraftReg;
        public String OpUser;
        public String AirportLimitWeight;
        public String TofWeight;
        public String RouteFule;
        public String DepAirportName;
        public String RealFule;
        public String LandWeight;
        public String MaxFule;
        public String ArrAirportName;
        public String FlightDate;
        public String BalancePicName;
        public String FlightId;
        public String Arr4Code;
        public String FlightNo;
        public String OpDate;
        public String Dep4Code;
        public String NoFuleWeight;
    }

    public static AddFlightInfoResponse parse(String jsonStr){
        Gson gson = new Gson();
        if(jsonStr.contains("\"ResponseData\":\"\""))
            jsonStr = jsonStr.replace("\"ResponseData\":\"\"","\"ResponseData\":{}");
        AddFlightInfoResponse fromJson = gson.fromJson(jsonStr, AddFlightInfoResponse.class);
        return fromJson;
    }
}
