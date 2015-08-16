package com.uandme.flight.entity;

import com.google.gson.Gson;
import com.uandme.flight.data.dao.AcWeightLimit;
import java.util.ArrayList;

/**
 * Created by QingYang on 15/8/8.
 */
public class AcWeightLimitByAcTypeResponse {

    public TResponseObject ResponseObject;
    public class TResponseObject{
        public TResponseData ResponseData;
        public int ResponseCode;
    }

    public class TResponseData{
        public ArrayList<AcWeightLimit> IAppObject;
    }

    @Deprecated
    public class TIAppObject{
        public String AcType; //机型
        public String FuleWeight;// 燃油重量
        public String FuleLj; //力矩
        public String OpUser; //操作人
        public String OpDate; //操作日期
    }

    public static AcWeightLimitByAcTypeResponse parse(String jsonStr){
        Gson gson = new Gson();
        if(jsonStr.contains("\"ResponseData\":\"\""))
            jsonStr = jsonStr.replace("\"ResponseData\":\"\"","\"ResponseData\":{}");
        AcWeightLimitByAcTypeResponse
                fromJson = gson.fromJson(jsonStr, AcWeightLimitByAcTypeResponse.class);
        return fromJson;
    }

}
