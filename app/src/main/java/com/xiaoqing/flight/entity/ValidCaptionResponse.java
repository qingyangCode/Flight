package com.xiaoqing.flight.entity;

import com.google.gson.Gson;

/**
 * Created by QingYang on 15/8/8.
 * 验证机长信息
 */
public class ValidCaptionResponse {

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

    }

    public static ValidCaptionResponse parse(String jsonStr){
        Gson gson = new Gson();
        if(jsonStr.contains("\"ResponseData\":\"\""))
            jsonStr = jsonStr.replace("\"ResponseData\":\"\"","\"ResponseData\":{}");
        ValidCaptionResponse fromJson = gson.fromJson(jsonStr, ValidCaptionResponse.class);
        return fromJson;
    }
}
