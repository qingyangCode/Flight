package com.uandme.flight.network;

import com.uandme.flight.entity.SeatByAcReg;

/**
 * Created by QingYang on 15/7/20.
 */
public interface MoccApi {

    public final String BASE_URL =  "http://124.127.106.196:80/Login1.ashx";

    public final String RESULT_ERROR = "0";

    /**
     * 获得校验值
     * @param username
     * @param responseListner
     */
    void getRandomString(String username, ResponseListner responseListner);

    /**
     * 验证用户登录
     * @param userName
     * @param pwd
     * @param responseListner
     */
    void validateUser(String userName, String pwd, ResponseListner responseListner);

    /**
     * 登录
     * @param userName
     * @param pwd
     * @param ll
     */
    public void doLogin(final String userName, final String pwd, final ResponseListner ll);

    /**
     * 获取所有飞机信息
     * @param userName
     * @param checkCode
     * @param responseListner
     */
    public void getAllAircraft(String userName, String checkCode,
            final ResponseListner responseListner);

    /**
     * 根据机号获取信息
     * @param userName
     * @param checkCode
     * @param airReg
     * @param responseListner
     */
    public void getAircraftByAcReg(String userName, String checkCode, String airReg,
            final ResponseListner responseListner);

    /**
     *
     * @param userName
     * @param checkCode
     * @param AircraftType
     * @param responseListner
     */
    public void getAircraftByAcType(String userName, String checkCode, String AircraftType,
            final ResponseListner responseListner);

    /**
     *
     * @param userName
     * @param checkCode
     * @param responseListner
     */
    public void addNewAircraft(String userName, String checkCode,
            final ResponseListner responseListner);

    /**
     *
     * @param userName
     * @param checkCode
     * @param responseListner
     */
    public void getAllAcType(String userName, String checkCode,
            final ResponseListner responseListner);

    /**
     * @param userName
     * @param checkCode
     * @param responseListner
     */
    public void getActypeByType(String userName, String checkCode,
            final ResponseListner responseListner);

    /**
     *  @param userName
     * @param checkCode
     * @param responseListner
     */
    public void getAllUser(String userName, String checkCode, final ResponseListner responseListner);

    /**
     * 按用户获取此用户的授权信息
     * @param userName
     * @param checkCode
     * @param responseListner
     */
    public void getGrantsByUserCode(String userName, String checkCode,
            final ResponseListner responseListner);

    /**按用户代码获取
     *
     * @param userName
     * @param checkCode
     * @param responseListner
     */
    public void getUserByCode(String userName, String checkCode,
            final ResponseListner responseListner);

    /**
     *
     * @param userName
     * @param checkCode
     * @param responseListner
     */
    public void getAllUserNew(String userName, String checkCode,
            final ResponseListner responseListner);

    /**
     *
     * @param userName
     * @param checkCode
     * @param responseListner
     */
    public void addFlightCd(String userName, String checkCode,
            final ResponseListner responseListner);

    /**
     *
     * @param userName
     * @param checkCode
     * @param responseListner
     */
    public  void addFlightInfo(String userName, String checkCode, ResponseListner responseListner);

    /**
     * 获取飞机座位信息
     * @param aircraftReg
     * @param bw
     * @param lj
     * @param opDate
     * @param SysVersion
     * @param responseListner
     */
    public void getSeatByAcReg(String aircraftReg, String bw, String lj, String opDate, String SysVersion, ResponseListner<SeatByAcReg> responseListner);

}
