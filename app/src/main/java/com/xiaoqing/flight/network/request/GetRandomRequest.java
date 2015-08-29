package com.xiaoqing.flight.network.request;

//import org.simpleframework.xml.Element;
//import org.simpleframework.xml.Root;

/**
 * Created by QingYang on 15/7/21.
 */
//@Root(name = "MessageObject")
public class GetRandomRequest {

    //@Element(name = "CMD")
    private String CMD;
    //@Element(name = "UserCode")
    private String UserCode;
    //@Element(name = "CheckCode")
    private String CheckCode;
    //@Element(name = "RequestData")
    private String RequestData;

    public GetRandomRequest(String username) {
        this.CMD = "getRandomString";
        this.UserCode = username;
    }

    public String getCMD() {
        return CMD;
    }

    public void setCMD(String CMD) {
        this.CMD = CMD;
    }

    public String getUserCode() {
        return UserCode;
    }

    public void setUserCode(String userCode) {
        UserCode = userCode;
    }

    public String getCheckCode() {
        return CheckCode;
    }

    public void setCheckCode(String checkCode) {
        CheckCode = checkCode;
    }

    public String getRequestData() {
        return RequestData;
    }

    public void setRequestData(String requestData) {
        RequestData = requestData;
    }
}
