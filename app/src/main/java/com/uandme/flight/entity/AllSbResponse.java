package com.uandme.flight.entity;

import com.google.gson.Gson;
import com.uandme.flight.data.dao.AllAcType;
import com.uandme.flight.data.dao.AllSb;
import java.util.ArrayList;

/**
 * Created by QingYang on 15/8/8.
 * 所有机型设备列表
 */
public class AllSbResponse {
    public TResponseObject ResponseObject;
    public class TResponseObject{
        public TResponseData ResponseData;
        public int ResponseCode;
    }

    public class TResponseData{
        public ArrayList<AllSb> IAppObject;
    }


    public static AllSbResponse parse(String jsonStr){
        Gson gson = new Gson();
        if(jsonStr.contains("\"ResponseData\":\"\""))
            jsonStr = jsonStr.replace("\"ResponseData\":\"\"","\"ResponseData\":{}");
        AllSbResponse fromJson = gson.fromJson(jsonStr, AllSbResponse.class);
        return fromJson;
    }
}
