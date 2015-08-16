package com.uandme.flight.network;

import com.uandme.flight.entity.AcWeightLimitByAcTypeResponse;
import com.uandme.flight.entity.AddFlightInfoResponse;
import com.uandme.flight.entity.AllAcTypeResponse;
import com.uandme.flight.entity.AllSbResponse;
import com.uandme.flight.entity.FuleLimitByAcType;
import com.uandme.flight.entity.GrantsByUserCodeResponse;
import com.uandme.flight.entity.SeatByAcRegResponse;

/**
 * Created by QingYang on 15/7/20.
 */
public interface MoccApi {

    public final String BASE_URL = "http://124.127.106.196:80/Login1.ashx";

    public final String RESULT_ERROR = "0";

    /**
     * 获得校验值
     */
    void getRandomString(String username, ResponseListner responseListner);

    /**
     * 验证用户登录
     */
    void validateUser(String userName, String pwd, ResponseListner responseListner);

    /**
     * 登录
     */
    public void doLogin(String userName, String pwd, ResponseListner ll);

    /**
     * 获取所有飞机信息
     */
    public void getAllAircraft(String userName, ResponseListner responseListner);

    /**
     * 根据机号获取信息
     */
    public void getAircraftByAcReg(String userName, String airReg, ResponseListner responseListner);

    /**
     *
     * @param userName
     * @param AircraftType
     * @param responseListner
     */
    public void getAircraftByAcType(String userName, String AircraftType, ResponseListner responseListner);

    /**
     *
     * @param userName
     * @param responseListner
     */
    public void addNewAircraft(String userName, ResponseListner responseListner);

    /**
     * @param userName
     * @param responseListner
     */
    public void getActypeByType(String userName, ResponseListner responseListner);

    /**
     *  @param userName
     * @param responseListner
     */
    public void getAllUser(String userName, ResponseListner responseListner);


    /**
     * 按用户代码获取
     */
    public void getUserByCode(String userName, ResponseListner responseListner);

    /**
     *
     * @param userName
     * @param responseListner
     */
    public void getAllUserNew(String userName, ResponseListner responseListner);

    /**
     * 获取飞机座位信息
     */
    public void getSeatByAcReg(String aircraftReg, String bw, String lj, String opDate, String SysVersion, ResponseListner<SeatByAcRegResponse> responseListner);

    /**
     * 获取机型燃油力矩表
     *
     * @param AircraftType 请求的机型
     * @param PortLimit 机坪限重
     * @param TofWeightLimit 起飞重量
     * @param LandWeightLimit 落地重量
     * @param Mzfw 最大无油重量
     * @param OpDate 操作日期
     * @param SysVersion 版本信息
     */
    public void getFuleLimitByAcType(String AircraftType, String PortLimit, String TofWeightLimit,
            String LandWeightLimit, String Mzfw, String OpDate, String SysVersion, ResponseListner<FuleLimitByAcType> responseListner);

    /**
     * 获取机型重心限制信息表
     *
     * @param AircraftType 请求的机型
     * @param PortLimit 机坪限重
     * @param TofWeightLimit 起飞重量
     * @param LandWeightLimit 落地重量
     * @param Mzfw 最大无油重量
     * @param OpDate 操作日期
     * @param SysVersion 版本信息
     */
    public void getAcWeightLimitByAcType(String AircraftType, String PortLimit, String TofWeightLimit, String LandWeightLimit, String Mzfw, String OpDate,
            String SysVersion, ResponseListner<AcWeightLimitByAcTypeResponse> responseListner);

    /**
     * 获取所有机型信息
     */
    public void getAllAcType(ResponseListner<AllAcTypeResponse> responseListner);

    /**
     * 获取机型设备列表
     */
    public void getAllSb(ResponseListner<AllSbResponse> responseListner);

    /**
     * 请求增加机上人员信息
     */
    public void addFlightCd(String AircraftReg, String SeatId, String FlightId, String SeatCode,
            String SeatType, String AcTypeSeatLimit, String AcTypeLj, String AcRegCagWeight, String AcRegCagLj,
            String SeatLastLimit, String PassagerName, String RealWeight,
            String OpUser, String OpDate ,ResponseListner<GrantsByUserCodeResponse> responseListner);

    /**
     * 按用户获取此用户的授权信息
     */
    public void getGrantsByUserCode(String UserCode, ResponseListner<GrantsByUserCodeResponse> responseListner);

    public void addFlightInfo(String FlightId, String FlightDate, String AircraftReg,
            String AircraftType, String FlightNo, String Dep4Code, String DepAirportName, String Arr4Code,
            String ArrAirportName, String MaxFule, String RealFule, String SlieFule, String RouteFule,
            String TofWeight, String LandWeight, String NoFuleWeight, String AirportLimitWeight,
            String BalancePic, String BalancePicName, String OpUser, String OpDate, ResponseListner<AddFlightInfoResponse> responseListner);
}