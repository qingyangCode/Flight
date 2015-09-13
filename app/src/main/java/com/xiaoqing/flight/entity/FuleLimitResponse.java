package com.xiaoqing.flight.entity;

import com.google.gson.Gson;
import com.xiaoqing.flight.data.dao.FuleLimit;
import java.util.ArrayList;

/**
 * Created by QingYang on 15/8/8.
 */
public class FuleLimitResponse {

    public TResponseObject ResponseObject;
    public class TResponseObject{
        public TResponseData ResponseData;
        public int ResponseCode;
    }

    public class TResponseData{
        public ArrayList<FuleLimit> IAppObject;
    }

    //public class TIAppObject{
    //    public String AcType; //机型
    //    public String FuleWeight;// 燃油重量
    //    public String FuleLj; //力矩
    //    public String OpUser; //操作人
    //    public String OpDate; //操作日期
    //}

    public static FuleLimitResponse parse(String jsonStr){
        Gson gson = new Gson();
        if(jsonStr.contains("\"ResponseData\":\"\""))
            jsonStr = jsonStr.replace("\"ResponseData\":\"\"","\"ResponseData\":{}");
        FuleLimitResponse fromJson = gson.fromJson(jsonStr, FuleLimitResponse.class);
        return fromJson;
    }

}
