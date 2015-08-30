package com.xiaoqing.flight.entity;

import com.google.gson.Gson;
import com.xiaoqing.flight.data.dao.AcGrants;
import java.util.ArrayList;

/**
 * Created by QingYang on 15/8/30.
 */
public class AcGrantsResponse {

    public TResponseObject ResponseObject;
    public class TResponseObject{
        public TResponseData ResponseData;
        public int ResponseCode;
    }

    public class TResponseData{
        public ArrayList<AcGrants> IAppObject;
    }


    public static AcGrantsResponse parse(String jsonStr){
        Gson gson = new Gson();
        if(jsonStr.contains("\"ResponseData\":\"\""))
            jsonStr = jsonStr.replace("\"ResponseData\":\"\"","\"ResponseData\":{}");
        AcGrantsResponse fromJson = gson.fromJson(jsonStr, AcGrantsResponse.class);
        return fromJson;
    }
}
