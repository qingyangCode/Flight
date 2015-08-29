package com.xiaoqing.flight.entity;

import com.google.gson.Gson;
import java.util.List;

/**
 * Created by QingYang on 15/8/8.
 * 消息
 */
public class MessageResponse {
    public TResponseObject ResponseObject;
    public class TResponseObject{
        public TResponseData ResponseData;
        public int ResponseCode;
    }

    public class TResponseData {
        public List<TIAppObject> IAppObject;
    }

    public class TIAppObject{
        public String LMsgId; // 消息ID是唯一的标识
        public String StrMessageContent; //消息内容
        public String StrSendUser; //发送消息的人
        public String DtSendDate; //消息发送日期
        public String MsustRead; //是否必读 Y必读 N否
    }

    public static MessageResponse parse(String jsonStr){
        Gson gson = new Gson();
        if(jsonStr.contains("\"ResponseData\":\"\""))
            jsonStr = jsonStr.replace("\"ResponseData\":\"\"","\"ResponseData\":{}");
        MessageResponse fromJson = null;
        try {
            fromJson = gson.fromJson(jsonStr, MessageResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fromJson;
    }
}
