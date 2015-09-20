package com.xiaoqing.flight.entity;

import com.google.gson.Gson;
import com.xiaoqing.flight.data.dao.SeatByAcReg;
import java.util.ArrayList;

/**
 * Created by QingYang on 15/7/25.
 * 座椅信息
 */
public class SeatByAcRegResponse {

    public TResponseObject ResponseObject;
    public class TResponseObject{
        public int ResponseCode;
        public TResponseData ResponseData;
        public int SysVersion;
    }

    public class TResponseData{
        public ArrayList<SeatByAcReg> IAppObject;
    }

    public static SeatByAcRegResponse parse(String jsonStr) {
        {
            Gson gson = new Gson();
            SeatByAcRegResponse fromJson = null;
            if(jsonStr.contains("\"ResponseData\":\"\""))
                jsonStr = jsonStr.replace("\"ResponseData\":\"\"","\"ResponseData\":{}");
            try {
                fromJson = gson.fromJson(jsonStr, SeatByAcRegResponse.class);
            }catch (Exception e) {
                e.printStackTrace();
            }
            return fromJson;
        }
    }


}
