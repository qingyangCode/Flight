package com.xiaoqing.flight.util;

import android.content.Intent;

/**
 * Created by QingYang on 15/7/25.
 */
public interface Constants {
    final int RESULT_OK = 100;

    String DB_ALLUSER = "alluser";

    String DB_AllAcType ="allAcType";

    String DB_ALLAirCart = "allAirCart";

    String ACTION_SEATLIST = "seatList";

    String ACTION_AIRCRAFTREG = "AircraftReg";
    String ACTION_AIRCRAFTTYPE = "AircraftType";
    String ACTION_FAILSEATINFOLIST = "failSeatInfoList";
    String BROADCAST_SYSTEMNOTICE = "system_notice";
    String PARAM_HASNEWNOTICE = "has_new_notice";
    String PARAM_NOTICEID = "notice_id";
    int RESLUT_ADDPARSSENGER = 1;
    String DB_ACGRANTS = "acgrants";
    int RESLUT_READNOTICE = 2;
    String PARAM_POSITION = "position";

    String DB_ALLAIRPORT = "allairPort";
    String DB_SEATBYREG = "seatbyreg";
    String DB_ALLSB = "all_sb";
    String DB_FULELIMIT = "fulelimit";
    String DB_SYSTEMNOTICE = "system_notice";
    String ACTION_ADDUSERNAME = "addusername";
}

