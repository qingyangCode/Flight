package com.xiaoqing.flight.entity;

import com.google.gson.Gson;
import com.xiaoqing.flight.data.dao.AllAcType;
import java.util.ArrayList;

/**
 * Created by QingYang on 15/8/8.
 * 所有机型信息
 */
public class AllAcTypeResponse {
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
        public String AircraftType; //机型
        public String PortLimit;// 机坪限重
        public String LimitType; //限制类型   N 限重曲线不能超出  Y可以超出
        public String AircraftTypeChName; //机型中文名
        public String TofWeightLimit; //最大起飞重量
        public String LandWeightLimit; //落地最大重量
        public String Mzfw; //最大无油重量
        public String Mac; //Mac参数1
        public String Mac2; //Mac参数2
        public String OpDate; //操作日期
        public String SysVersion; //版本号
        public String UserCode; //操作人
        public String MacFlg; //操作人
    }

    public static AllAcTypeResponse parse(String jsonStr){
        Gson gson = new Gson();
        if(jsonStr.contains("\"ResponseData\":\"\""))
            jsonStr = jsonStr.replace("\"ResponseData\":\"\"","\"ResponseData\":{}");
        AllAcTypeResponse fromJson = gson.fromJson(jsonStr, AllAcTypeResponse.class);
        return fromJson;
    }
}
