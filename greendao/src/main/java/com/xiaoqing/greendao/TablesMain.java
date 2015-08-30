package com.xiaoqing.greendao;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 */
public class TablesMain {

    protected Schema mSchema;

    public TablesMain(Schema schema) {
        mSchema = schema;
    }

    /**
     * add table here
     */
    public void build() {
        addUser();
        addSystemVersion();
        addAllAcType();//所有机型信息
        addAcWeightLimit();//中心限制信息
        addFuleLimit();//机型燃油力矩表
        addAllAircraft();//所有机型信息
        addAllSb();//获取机型设备列表
        addSeatInfos();//座椅信息
        adllFilghtInfo();//添加飞机信息
        getSystemNotice();//系统消息
        getIsReadSystemNotice();// 是否阅读过消息
        getAircraftPerson();// 机上成员用户表
        getAcGrants();//获取用户授权机型

        getSyncDB();//同步数据
    }

    private void getSyncDB() {
        Entity actionFeed = mSchema.addEntity("ActionFeed");
        actionFeed.setTableName("ACTION_FEED");
        actionFeed.addIdProperty().autoincrement();
        actionFeed.addIntProperty("feed_type");
        actionFeed.addStringProperty("feed_id");
        actionFeed.addStringProperty("UserCode");
        actionFeed.addIntProperty("feed_status");//aircraftReg,aircraftType
    }

    private void getAcGrants() {
        Entity acGrants = mSchema.addEntity("AcGrants");
        acGrants.setTableName("AcGrants");
        acGrants.addIdProperty().autoincrement();
        acGrants.addStringProperty("UserCode");
        acGrants.addStringProperty("AcReg");//授权机号
        acGrants.addStringProperty("AcType");//授权机型
        acGrants.addDoubleProperty("AcRegBw");//机型基本空重
        acGrants.addDoubleProperty("AcLj"); //机型基本力矩
        acGrants.addStringProperty("IsCaption");//是否是机长
        acGrants.addIntProperty("SysVersion");
    }

    private void getAircraftPerson() {
        Entity passenger = mSchema.addEntity("Passenger");
        passenger.addIdProperty().autoincrement();
        passenger.addStringProperty("userName");
        passenger.addDoubleProperty("userWeight");
        passenger.addStringProperty("AircraftReg");
        passenger.addBooleanProperty("isChecked");
    }

    private void getIsReadSystemNotice() {
        Entity readSystemBotice = mSchema.addEntity("ReadSystemNotice");
        readSystemBotice.implementsSerializable();
        readSystemBotice.addIdProperty().autoincrement();
        readSystemBotice.addStringProperty("LMsgId");
        readSystemBotice.addBooleanProperty("isReaded");
        readSystemBotice.addStringProperty("MsustRead");
        readSystemBotice.addStringProperty("UserCode");
    }

    private void adllFilghtInfo() {
        Entity flightInfo = mSchema.addEntity("AddFlightInfo");
        flightInfo.implementsSerializable();
        flightInfo.addStringProperty("FlightId").primaryKey().notNull();
        flightInfo.addStringProperty("FlightDate");
        flightInfo.addStringProperty("AircraftReg");
        flightInfo.addStringProperty("AircraftType");
        flightInfo.addStringProperty("FlightNo");
        flightInfo.addStringProperty("Dep4Code");
        flightInfo.addStringProperty("DepAirportName");
        flightInfo.addStringProperty("Arr4Code");
        flightInfo.addStringProperty("ArrAirportName");
        flightInfo.addStringProperty("MaxFule");
        flightInfo.addStringProperty("RealFule");
        flightInfo.addStringProperty("SlieFule");
        flightInfo.addStringProperty("RouteFule");
        flightInfo.addStringProperty("TofWeight");
        flightInfo.addStringProperty("LandWeight");
        flightInfo.addStringProperty("NoFuleWeight");
        flightInfo.addStringProperty("AirportLimitWeight");
        flightInfo.addStringProperty("BalancePic");
        flightInfo.addStringProperty("BalancePicName");
        flightInfo.addStringProperty("OpUser");
        flightInfo.addStringProperty("OpDate");

        flightInfo.addStringProperty("weightCg");




    }

    private void addSeatInfos() {
        Entity seatByAcReg = mSchema.addEntity("SeatByAcReg");
        seatByAcReg.implementsSerializable();
        seatByAcReg.addIdProperty().autoincrement();
        seatByAcReg.addStringProperty("AcReg");
        seatByAcReg.addIntProperty("SeatId");
        seatByAcReg.addStringProperty("SeatCode");
        seatByAcReg.addStringProperty("SeatType");
        seatByAcReg.addFloatProperty("AcTypeSeatLimit");
        seatByAcReg.addFloatProperty("AcTypeLb");
        seatByAcReg.addFloatProperty("AcRegCargWeight");
        seatByAcReg.addFloatProperty("AcRegSbWeight");
        seatByAcReg.addFloatProperty("SeatLastLimit");
        seatByAcReg.addStringProperty("OpUser");
        seatByAcReg.addStringProperty("OpDate");
        seatByAcReg.addStringProperty("SysVersion");
        seatByAcReg.addFloatProperty("XPos");
        seatByAcReg.addFloatProperty("YPos");
        seatByAcReg.addStringProperty("Direction");
        seatByAcReg.addStringProperty("userName");
        seatByAcReg.addFloatProperty("seatWeight").notNull();
    }

    private void addAllSb() {
        Entity allSb = mSchema.addEntity("AllSb");
        allSb.addIdProperty().autoincrement();
        allSb.addIntProperty("SbId");
        allSb.addStringProperty("SbName");
        allSb.addDoubleProperty("SbWeight");
        allSb.addStringProperty("AcType");
        allSb.addIntProperty("SeatId");
    }

    private void addAllAircraft() {
        Entity allAircraft = mSchema.addEntity("AllAircraft");
        allAircraft.addIdProperty().autoincrement();
        allAircraft.addStringProperty("AircraftReg");//操作人
        allAircraft.addStringProperty("UserCode");//机型
        allAircraft.addStringProperty("AircraftType");//机型
        allAircraft.addIntProperty("Bw");//飞机基本空重
        allAircraft.addDoubleProperty("Lj");//飞机力矩
        allAircraft.addStringProperty("LayoutPic");//未标注，无数据
        allAircraft.addStringProperty("OpDate");// 操作日期
        allAircraft.addIntProperty("SysVersion");//当前版本
        allAircraft.addStringProperty("AcRemark");//备注
    }

    private void addFuleLimit() {
        Entity fuleLimit = mSchema.addEntity("FuleLimit");
        fuleLimit.addIdProperty().autoincrement();
        fuleLimit.addStringProperty("AcType");
        fuleLimit.addFloatProperty("FuleWeight");
        fuleLimit.addFloatProperty("FuleLj");
        fuleLimit.addStringProperty("OpUser");
        fuleLimit.addStringProperty("OpDate");
    }

    private void addAcWeightLimit() {
        Entity acWeoghtLimit = mSchema.addEntity("AcWeightLimit");
        acWeoghtLimit.addIdProperty().autoincrement();
        acWeoghtLimit.addStringProperty("AcType");
        acWeoghtLimit.addStringProperty("Weight");
        acWeoghtLimit.addStringProperty("WeightCg1");
        acWeoghtLimit.addStringProperty("WeightCg2");
        acWeoghtLimit.addStringProperty("OpUser");
        acWeoghtLimit.addStringProperty("OpDate");
    }

    private void addAllAcType() {
        Entity allAcType = mSchema.addEntity("AllAcType");
        allAcType.addIdProperty().autoincrement();
        allAcType.addStringProperty("AircraftType").notNull();
        allAcType.addIntProperty("PortLimit").notNull();
        allAcType.addStringProperty("LimitType");
        allAcType.addStringProperty("AircraftTypeChName");
        allAcType.addStringProperty("TofWeightLimit");
        allAcType.addIntProperty("LandWeightLimit").notNull();
        allAcType.addIntProperty("Mzfw").notNull();
        allAcType.addStringProperty("Mac");
        allAcType.addStringProperty("Mac2");
        allAcType.addStringProperty("OpDate");
        allAcType.addIntProperty("SysVersion").notNull();
        allAcType.addStringProperty("UserCode");
        allAcType.addStringProperty("MacFlg");
    }

    private void addSystemVersion() {
        Entity systemVersion = mSchema.addEntity("SystemVersion");
        systemVersion.setTableName("SystemVersion");
        systemVersion.addIdProperty().autoincrement();
        systemVersion.addStringProperty("VserionName");
        systemVersion.addIntProperty("vserion").notNull();
    }

    private void addUser() {
        Entity user = mSchema.addEntity("User");
        user.setTableName("user");
        user.addIdProperty().autoincrement();
        user.addStringProperty("UserName").unique().notNull();
        user.addStringProperty("UserCode");
        user.addStringProperty("DepCode");
        user.addStringProperty("UserPassWord");
        user.addStringProperty("CheckCode");
        user.addStringProperty("Grant_S_M");
        user.addStringProperty("ActiveStart");
        user.addIntProperty("SysVersion").notNull();
    }

    private void getSystemNotice() {
        Entity systemNotice = mSchema.addEntity("SystemNotice");
        systemNotice.implementsSerializable();
        systemNotice.setTableName("SystemNotice");
        systemNotice.addIdProperty().autoincrement();
        systemNotice.addStringProperty("LMsgId"); // 消息ID是唯一的标识
        systemNotice.addStringProperty("StrMessageContent"); //消息内容
        systemNotice.addStringProperty("StrSendUser"); //发送消息的人
        systemNotice.addStringProperty("DtSendDate"); //消息发送日期
        systemNotice.addStringProperty("MsustRead"); //是否必读 Y必读 N否

    }


}
