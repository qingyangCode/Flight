package com.xiaoqing.flight.entity;

import com.google.gson.Gson;
import com.xiaoqing.flight.data.dao.AcGrants;
import com.xiaoqing.flight.data.dao.AllAirport;
import java.util.ArrayList;

/**
 * Created by QingYang on 15/8/30.
 * 获取机场列表
 */
public class AllAirportResponse extends BaseResponse{

    public TResponseObject ResponseObject;
    public class TResponseObject{
        public TResponseData ResponseData;
        public int ResponseCode;
    }

    public class TResponseData{
        public ArrayList<AllAirport> IAppObject;
    }


    public static AllAirportResponse parse(String jsonStr){
        Gson gson = new Gson();
        if(jsonStr.contains("\"ResponseData\":\"\""))
            jsonStr = jsonStr.replace("\"ResponseData\":\"\"","\"ResponseData\":{}");
        AllAirportResponse fromJson = gson.fromJson(jsonStr, AllAirportResponse.class);
        return fromJson;
    }
}
