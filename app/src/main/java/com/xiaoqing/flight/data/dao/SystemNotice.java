package com.xiaoqing.flight.data.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table SystemNotice.
 */
public class SystemNotice implements java.io.Serializable {

    private Long id;
    private String LMsgId;
    private String StrMessageContent;
    private String StrSendUser;
    private String DtSendDate;
    private String MsustRead;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public SystemNotice() {
    }

    public SystemNotice(Long id) {
        this.id = id;
    }

    public SystemNotice(Long id, String LMsgId, String StrMessageContent, String StrSendUser, String DtSendDate, String MsustRead) {
        this.id = id;
        this.LMsgId = LMsgId;
        this.StrMessageContent = StrMessageContent;
        this.StrSendUser = StrSendUser;
        this.DtSendDate = DtSendDate;
        this.MsustRead = MsustRead;
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

    public String getStrMessageContent() {
        return StrMessageContent;
    }

    public void setStrMessageContent(String StrMessageContent) {
        this.StrMessageContent = StrMessageContent;
    }

    public String getStrSendUser() {
        return StrSendUser;
    }

    public void setStrSendUser(String StrSendUser) {
        this.StrSendUser = StrSendUser;
    }

    public String getDtSendDate() {
        return DtSendDate;
    }

    public void setDtSendDate(String DtSendDate) {
        this.DtSendDate = DtSendDate;
    }

    public String getMsustRead() {
        return MsustRead;
    }

    public void setMsustRead(String MsustRead) {
        this.MsustRead = MsustRead;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
