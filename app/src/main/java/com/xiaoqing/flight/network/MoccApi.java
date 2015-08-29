package com.xiaoqing.flight.network;

import com.xiaoqing.flight.entity.AcWeightLimitByAcTypeResponse;
import com.xiaoqing.flight.entity.AddFlightInfoResponse;
import com.xiaoqing.flight.entity.AllAcTypeResponse;
import com.xiaoqing.flight.entity.AllAirCraftResponse;
import com.xiaoqing.flight.entity.AllSbResponse;
import com.xiaoqing.flight.entity.FlightidResponse;
import com.xiaoqing.flight.entity.FuleLimitByAcType;
import com.xiaoqing.flight.entity.GrantsByUserCodeResponse;
import com.xiaoqing.flight.entity.MessageResponse;
import com.xiaoqing.flight.entity.SeatByAcRegResponse;
import com.xiaoqing.flight.entity.ValidCaptionResponse;

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
     * @param AircraftReg //机号
     * @param SeatId //座椅编号
     * @param FlightId  //航班编号
     * @param SeatCode  //座椅代码
     * @param SeatType //座椅类型 S座椅 C货物
     * @param AcTypeSeatLimit  //机型座椅限重
     * @param AcTypeLj  //机型座椅力臂
     * @param AcRegCagrWeight  //飞机额外物品重量
     * @param AcRegCagLj 座椅力臂
     * @param SeatLastLimit //机型限重减去飞机额外物品后的最大重量限制
     * @param PassagerName //乘客名称
     * @param RealWeight //乘客/货 实际重量
     * @param OpUser
     * @param OpDate
     * @param responseListner
     */
    public void addFlightCd(String AircraftReg, String SeatId, String FlightId, String SeatCode,
            String SeatType, String AcTypeSeatLimit, String AcTypeLj, String AcRegCagrWeight, String AcRegCagLj,
            String SeatLastLimit, String PassagerName, String RealWeight,
            String OpUser, String OpDate ,ResponseListner<GrantsByUserCodeResponse> responseListner);


    /**
     * 添加航班信息
     * @param FlightId
     * @param FlightDate //航班日期
     * @param AircraftReg ////飞机号
     * @param AircraftType //机型
     * @param FlightNo //航班号
     * @param Dep4Code //出发机场四字代码
     * @param DepAirportName //出发机场名
     * @param Arr4Code //到达机场四字代码
     * @param ArrAirportName //到达机场名
     * @param MaxFule //机型最大燃油
     * @param RealFule //实际加油
     * @param SlieFule //滑行油量
     * @param RouteFule //航段耗油
     * @param TofWeight //起飞重量
     * @param LandWeight //落地重量
     * @param NoFuleWeight//无油重量
     * @param AirportLimitWeight //机坪限重
     * @param BalancePic
     * @param BalancePicName //计算载重图表名
     * @param OpUser
     * @param OpDate
     * @param responseListner
     */
    public void addFlightInfo(String FlightId, String FlightDate, String AircraftReg,
            String AircraftType, String FlightNo, String Dep4Code, String DepAirportName, String Arr4Code,
            String ArrAirportName, String MaxFule, String RealFule, String SlieFule, String RouteFule,
            String TofWeight, String LandWeight, String NoFuleWeight, String AirportLimitWeight,
            String BalancePic, String BalancePicName, String OpUser, String OpDate, ResponseListner<AddFlightInfoResponse> responseListner);

    /**
     * 获取飞机ID
     * @param responseListner
     */
    public void getflightid(ResponseListner<FlightidResponse> responseListner);

    /**
     * 根据登录用户获取授权机型
     * @param responseResponseListner
     */
    public void getGrantsByUserCode(ResponseListner<AllAirCraftResponse> responseResponseListner);

    /**
     * 验证机长信息
     * @param StrUserCode
     * @param StrAcType
     * @param StrUserPass
     * @param responseResponseListner
     */
    public void validCaption(String StrUserCode, String StrAcType, String StrUserPass, ResponseListner<ValidCaptionResponse> responseResponseListner);

    /**
     * 获取系统消息
     * @param startDate // 开始时间
     * @param endDate // 结束时间
     * @param responseListner
     */
    void getMessageByDate(String startDate, ResponseListner<MessageResponse> responseListner);
}