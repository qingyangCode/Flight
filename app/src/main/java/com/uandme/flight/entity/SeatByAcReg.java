package com.uandme.flight.entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;

/**
 * Created by QingYang on 15/7/25.
 */
public class SeatByAcReg {

    public TResponseObject ResponseObject;
    public class TResponseObject{
        public int ResponseCode;
        public TResponseData ResponseData;
    }

    public class TResponseData{
        public ArrayList<SeatInfos> IAppObject;
    }

    public static SeatByAcReg parse(String jsonStr) {
        {
            Gson gson = new Gson();
            SeatByAcReg fromJson = null;
            try {
                fromJson = gson.fromJson(jsonStr, SeatByAcReg.class);
            }catch (Exception e) {
                e.printStackTrace();
            }
            return fromJson;
        }
    }

    public class SeatInfos {
        public String AcReg;//机号
        public int SeatId;//-座椅编号
        public String SeatCode;//座椅名称
        public String SeatType;//座椅类型 S乘客座椅 C货物
        public float AcTypeSeatLimit;//座椅最大限重
        public float AcTypeLb;//座椅力臂
        public float AcRegCargWeight;//此位置额外物品重量
        public double AcRegSbWeight;//此位置机上设备重量
        public float SeatLastLimit;//最后所允许的重量
        public String OpUser;//操作人
        public String OpDate;//操作日期
        public String SysVersion;//版本号
        public double XPos;//横坐标占比
        public double YPos;//坚坐标占比
        public String Direction;//座椅朝向 LEFT朝左 UP朝上 RIGHT朝右 DWON朝下
        public String userName;
    }
}
