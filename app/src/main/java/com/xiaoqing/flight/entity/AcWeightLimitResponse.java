package com.xiaoqing.flight.entity;

import com.google.gson.Gson;
import com.xiaoqing.flight.data.dao.AcWeightLimit;
import java.util.ArrayList;

/**
 * Created by QingYang on 15/8/8.
 */
public class AcWeightLimitResponse extends BaseResponse{

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

    public static AcWeightLimitResponse parse(String jsonStr){
        Gson gson = new Gson();
        if(jsonStr.contains("\"ResponseData\":\"\""))
            jsonStr = jsonStr.replace("\"ResponseData\":\"\"","\"ResponseData\":{}");
        AcWeightLimitResponse
                fromJson = gson.fromJson(jsonStr, AcWeightLimitResponse.class);
        return fromJson;
    }

}
