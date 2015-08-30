package com.xiaoqing.flight.data.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table READ_SYSTEM_NOTICE.
 */
public class ReadSystemNotice implements java.io.Serializable {

    private Long id;
    private String LMsgId;
    private Boolean isReaded;
    private String MsustRead;
    private String UserCode;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public ReadSystemNotice() {
    }

    public ReadSystemNotice(Long id) {
        this.id = id;
    }

    public ReadSystemNotice(Long id, String LMsgId, Boolean isReaded, String MsustRead, String UserCode) {
        this.id = id;
        this.LMsgId = LMsgId;
        this.isReaded = isReaded;
        this.MsustRead = MsustRead;
        this.UserCode = UserCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLMsgId() {
        return LMsgId;
    }

    public void setLMsgId(String LMsgId) {
        this.LMsgId = LMsgId;
    }

    public Boolean getIsReaded() {
        return isReaded;
    }

    public void setIsReaded(Boolean isReaded) {
        this.isReaded = isReaded;
    }

    public String getMsustRead() {
        return MsustRead;
    }

    public void setMsustRead(String MsustRead) {
        this.MsustRead = MsustRead;
    }

    public String getUserCode() {
        return UserCode;
    }

    public void setUserCode(String UserCode) {
        this.UserCode = UserCode;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
