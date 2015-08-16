package com.uandme.greendao;

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
    }

    private void addSeatInfos() {
        Entity seatByAcReg = mSchema.addEntity("SeatByAcReg");
        seatByAcReg.addIdProperty().autoincrement().primaryKey();
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
    }

    private void addAllSb() {
        Entity allSb = mSchema.addEntity("AllSb");
        allSb.addIdProperty().autoincrement().primaryKey();
        allSb.addIntProperty("SbId");
        allSb.addStringProperty("SbName");
        allSb.addDoubleProperty("SbWeight");
        allSb.addStringProperty("AcType");
        allSb.addIntProperty("SeatId");
    }

    private void addAllAircraft() {
        Entity allAircraft = mSchema.addEntity("AllAircraft");
        allAircraft.addIdProperty().autoincrement().primaryKey();
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
        fuleLimit.addIdProperty().autoincrement().primaryKey();
        fuleLimit.addStringProperty("AcType");
        fuleLimit.addIntProperty("FuleWeight");
        fuleLimit.addIntProperty("FuleLj");
        fuleLimit.addStringProperty("OpUser");
        fuleLimit.addStringProperty("OpDate");
    }

    private void addAcWeightLimit() {
        Entity acWeoghtLimit = mSchema.addEntity("AcWeightLimit");
        acWeoghtLimit.addIdProperty().autoincrement().notNull();
        acWeoghtLimit.addStringProperty("AcType");
        acWeoghtLimit.addStringProperty("Weight");
        acWeoghtLimit.addStringProperty("WeightCg1");
        acWeoghtLimit.addStringProperty("WeightCg2");
        acWeoghtLimit.addStringProperty("OpUser");
        acWeoghtLimit.addStringProperty("OpDate");
    }

    private void addAllAcType() {
        Entity allAcType = mSchema.addEntity("AllAcType");
        allAcType.addIdProperty().autoincrement().primaryKey();
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
        systemVersion.addIdProperty().autoincrement().primaryKey();
        systemVersion.addStringProperty("VserionName");
        systemVersion.addIntProperty("vserion").notNull();
    }

    private void addUser() {
        Entity user = mSchema.addEntity("User");
        user.setTableName("user");
        user.addIdProperty().autoincrement().primaryKey();
        user.addStringProperty("UserName").unique().notNull();
        user.addStringProperty("UserCode");
        user.addStringProperty("DepCode");
        user.addStringProperty("UserPassWord");
        user.addStringProperty("CodeCheck");
        user.addStringProperty("Grant_S_M");
        user.addStringProperty("ActiveStart");
        user.addIntProperty("SysVersion").notNull();
    }

}
