package com.xiaoqing.flight.entity;

import com.google.gson.Gson;
import com.xiaoqing.flight.data.dao.AllAcType;
import java.util.ArrayList;

/**
 * Created by QingYang on 15/8/8.
 * 所有机型信息
 */
public class UpdateInfoResponse {
    public TResponseObject ResponseObject;
    public class TResponseObject{
        public TResponseData ResponseData;
        public int ResponseCode;
    }

    public class TResponseData{
        public UpdateInfo IAppObject;
    }

    public class UpdateInfo{
        private int Version;
        private String Content;
        private String Url;

        public int getVersion() {
            return Version;
        }

        public void setVersion(int version) {
            Version = version;
        }

        public String getContent() {
            return Content;
        }

        public void setContent(String content) {
            Content = content;
        }

        public String getUrl() {
            return Url;
        }

        public void setUrl(String url) {
            Url = url;
        }
    }

    public static UpdateInfoResponse parse(String jsonStr){
        Gson gson = new Gson();
        if(jsonStr.contains("\"ResponseData\":\"\""))
            jsonStr = jsonStr.replace("\"ResponseData\":\"\"","\"ResponseData\":{}");
        UpdateInfoResponse fromJson = gson.fromJson(jsonStr, UpdateInfoResponse.class);
        return fromJson;
    }
}
