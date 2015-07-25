package com.uandme.flight.network;

import android.text.TextUtils;
import com.uandme.flight.entity.AddOneUsers;
import com.uandme.flight.entity.AllAirCraft;
import com.uandme.flight.entity.AllAirTypeCraft;
import com.uandme.flight.entity.AllSameTypeAirCraft;
import com.uandme.flight.entity.AllUsers;
import com.uandme.flight.entity.AllUsersNew;
import com.uandme.flight.entity.LoginUserInfo;
import com.uandme.flight.entity.OneAirCraft;
import com.uandme.flight.entity.OneAirTypeCraft;
import com.uandme.flight.entity.OneUsers;
import com.uandme.flight.entity.SeatByAcReg;
import com.uandme.flight.entity.TUserInfo;
import com.uandme.flight.entity.UserGrantsInfo;
import com.uandme.flight.util.CommonUtils;
import com.uandme.flight.util.DigestUtils;
import com.uandme.flight.util.UserManager;

/**
 * Created by QingYang on 15/7/21.
 */
public class MoccApiImpl implements MoccApi{

    private final String TAG = MoccApiImpl.class.getSimpleName();

    /**
     * 获得校验值
     * @param userName
     * @param responseListner
     */
    @Override
    public void getRandomString(String userName, ResponseListner responseListner) {
        String url = "http://124.127.106.196:80/Login1.ashx";
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
                .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetRandomString</CMD>")
                .append("<UserCode>"+userName+"</UserCode>")
                .append("RequestData/>").append("</MessageObject>");
        String xmlParam = sb.toString();
        NetBase net = new NetBase(url, xmlParam, responseListner);
        net.execute();
    }

    /**
     * 验证用户
     * @param userName
     * @param pwd
     * @param responseListner
     */
    @Override
    public void validateUser(String userName, String pwd, ResponseListner responseListner){
        //String url = "http://124.127.106.196:80/Login1.ashx";
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
                .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>ValidateUser</CMD>")
                .append("<UserCode>" + userName + "</UserCode>")
                .append("<CheckCode>" + pwd + "</CheckCode>")
                .append("RequestData/>").append("</MessageObject>");
        String xmlParam = sb.toString();
        NetBase net = new NetBase(BASE_URL, xmlParam, responseListner);
        net.execute();

    }

    /**
     * 实现登录效果
     * @param userName
     * @param pwd
     */
    public void doLogin(final String userName, final String pwd, final ResponseListner ll){
        getRandomString(userName, new ResponseListner<String>() {
            @Override public void onResponse(String response) {
                String xml2json = CommonUtils.xml2JSON(response);
                TUserInfo userInfo = TUserInfo.parse(xml2json);
                if (userInfo == null
                        || RESULT_ERROR.equals(userInfo.ResponseObject.ResponseCode)
                        || "-100".equals(userInfo.ResponseObject.ResponseCode)) {
                    ll.onEmptyOrError(userInfo.ResponseObject.ResponseErr);
                    return;
                }
                String newStr = userInfo.ResponseObject.ResponseData.IAppObject.GUIDCode + pwd;
                newStr = DigestUtils.md5(newStr);
                newStr = newStr.toUpperCase();//注意必须是大写的 md5
                validateUser(userName, newStr, new ResponseListner<String>() {
                    @Override public void onResponse(String response) {
                        String xml2json2 = CommonUtils.xml2JSON(response);
                        LoginUserInfo info = LoginUserInfo.parse(xml2json2);
                        ll.onResponse(info);
                    }

                    @Override public void onEmptyOrError(String message) {
                        ll.onEmptyOrError(message);
                    }
                });
            }

            @Override public void onEmptyOrError(String message) {
                ll.onEmptyOrError(message);
            }
        });
    }

    //	一、获取所有飞机信息
    public void getAllAircraft(String userName, String checkCode,
            final ResponseListner responseListner) {
        //String url = "http://124.127.106.196:80/Login1.ashx";
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
                .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetAllAircraft</CMD>")
                .append("<UserCode>"+"Test"+"</UserCode>")
                .append("<CheckCode>"+checkCode+"</CheckCode>")
                .append("RequestData/>").append("</MessageObject>");
        String xmlParam = sb.toString();
        NetBase net = new NetBase(BASE_URL, xmlParam, new ResponseListner<String>() {
            @Override
            public void onResponse(String response) {
                String xml2json = CommonUtils.xml2JSON(response);
                if(!TextUtils.isEmpty(xml2json)){
                    AllAirCraft allAirCraft = AllAirCraft.parse(xml2json);
                    responseListner.onResponse(allAirCraft);
                }
            }

            @Override
            public void onEmptyOrError(String message) {
                responseListner.onEmptyOrError(message);
            }

        });
        net.execute();
    }



    ///B8099  二、根据机号获取信息
    public void getAircraftByAcReg(String userName, String checkCode, String airReg,
            final ResponseListner responseListner) {
        //String url = "http://124.127.106.196:80/Login1.ashx";
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
                .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetAircraftByAcReg</CMD>")
                .append("<UserCode>"+userName+"</UserCode>")
                .append("<CheckCode>"+checkCode+"</CheckCode>")
                .append("<RequestData>")
                .append("<IAppObject xsi:type=\"AppAircraft\">")
                .append("<AircraftReg>"+airReg+"</AircraftReg>")
                .append("<Bw>0</Bw>")
                .append("<UseWeight1>0</UseWeight1>")
                .append("<UseWeight2>0</UseWeight2>")
                .append("<AircraftZx1>0</AircraftZx1>")
                .append("<AircraftZx2>0</AircraftZx2>")
                .append("<MaxTofWeight>0</MaxTofWeight>")
                .append("<MaxLandWeight>0</MaxLandWeight>")
                .append("<MaxAllowFule>0</MaxAllowFule>")
                .append("<Mzfw>0</Mzfw>")
                .append("<OpDate>0001-01-01T00:00:00</OpDate>")
                .append("</IAppObject>")
                .append("</RequestData>")
                .append("</MessageObject>");
        String xmlParam = sb.toString();
        NetBase net = new NetBase(BASE_URL, xmlParam, new ResponseListner<String>() {
            @Override
            public void onResponse(String response) {
                String xml2json = CommonUtils.xml2JSON(response);
                if(!TextUtils.isEmpty(xml2json)){
                    OneAirCraft oneAir = OneAirCraft.parse(xml2json);
                }
                responseListner.onResponse(response);
            }

            @Override
            public void onEmptyOrError(String message) {
                responseListner.onEmptyOrError(message);
            }

        });
        net.execute();
    }




    //ce680
    public void getAircraftByAcType(String userName, String checkCode, String AircraftType,
            final ResponseListner responseListner) {
        //String url = "http://124.127.106.196:80/Login1.ashx";
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
                .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>getAircraftByAcType</CMD>")
                .append("<UserCode>"+userName+"</UserCode>")
                .append("<CheckCode>"+checkCode+"</CheckCode>")
                .append("<RequestData>")
                .append("<IAppObject xsi:type=\"AppAircraft\">")
                .append("<AircraftType>"+AircraftType+"</AircraftType>")
                .append("<Bw>0</Bw>")
                .append("<UseWeight1>0</UseWeight1>")
                .append("<UseWeight2>0</UseWeight2>")
                .append("<AircraftZx1>0</AircraftZx1>")
                .append("<AircraftZx2>0</AircraftZx2>")
                .append("<MaxTofWeight>0</MaxTofWeight>")
                .append("<MaxLandWeight>0</MaxLandWeight>")
                .append("<MaxAllowFule>0</MaxAllowFule>")
                .append("<Mzfw>0</Mzfw>")
                .append("</IAppObject>")
                .append("</RequestData>")
                .append("</MessageObject>");
        String xmlParam = sb.toString();
        NetBase net = new NetBase(BASE_URL, xmlParam, new ResponseListner<String>() {
            @Override
            public void onResponse(String response) {
                String xml2json = CommonUtils.xml2JSON(response);
                AllSameTypeAirCraft parse = null;
                if(!TextUtils.isEmpty(xml2json))
                    parse = AllSameTypeAirCraft.parse(xml2json);

                if(responseListner != null){
                    responseListner.onResponse(parse);
                }
            }

            @Override
            public void onEmptyOrError(String message) {
            }
        });
        net.execute();
    }


    public void addNewAircraft(String userName, String checkCode,
            final ResponseListner responseListner) {
        //String url = "http://124.127.106.196:80/Login1.ashx";
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
                .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>AddNewAircraft</CMD>")
                .append("<UserCode>"+userName+"</UserCode>")
                .append("<CheckCode>"+checkCode+"</CheckCode>")
                .append("<RequestData>")
                .append("<IAppObject xsi:type=\"AppAircraft\">")
                .append("<AircraftReg>B1111</AircraftReg>")
                .append("<UserCode>Test</UserCode>")
                .append("<AircraftType>A320</AircraftType>")
                .append("<AircraftLongType>A320-300</AircraftLongType>")
                .append("<Bw>20</Bw>")
                .append("<UseWeight1>15</UseWeight1>")
                .append("<UseWeight2>13</UseWeight2>")
                .append("<AircraftZx1>2.2</AircraftZx1>")
                .append("<AircraftZx2>2.2</AircraftZx2>")
                .append("<MaxTofWeight>22</MaxTofWeight>")
                .append("<MaxLandWeight>22</MaxLandWeight>")
                .append("<MaxAllowFule>22</MaxAllowFule>")
                .append("<Mzfw>15</Mzfw>")
                .append("<LayoutPic>D:\\</LayoutPic>")
                .append("<OpDate>2014-11-30T17:26:44.2543286+08:00</OpDate>")
                .append("<SysVersion>0</SysVersion>")
                .append("</IAppObject>")
                .append("</RequestData>")
                .append("</MessageObject>");
        String xmlParam = sb.toString();
        NetBase net = new NetBase(BASE_URL, xmlParam, new ResponseListner<String>() {
            @Override
            public void onResponse(String response) {
                String xml2json = CommonUtils.xml2JSON(response);
                if(!TextUtils.isEmpty(xml2json)){
                    AllSameTypeAirCraft parse = AllSameTypeAirCraft.parse(xml2json);
                    responseListner.onResponse(parse);
                }


            }
            @Override
            public void onEmptyOrError(String message) {
                responseListner.onEmptyOrError(message);

            }
        });
        net.execute();
    }


    public void getAllAcType(String userName, String checkCode,
            final ResponseListner responseListner) {
        //String url = "http://124.127.106.196:80/Login1.ashx";
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
                .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetAllAcType</CMD>").
                        append("<UserCode>" + userName + "</UserCode>")
                .append("<CheckCode>"+checkCode+"</CheckCode>")
                .append("<RequestData />")
                .append("</MessageObject>");
        String xmlParam = sb.toString();
        NetBase net = new NetBase(BASE_URL, xmlParam, new ResponseListner<String>() {
            @Override
            public void onResponse(String response) {
                String xml2json = CommonUtils.xml2JSON(response);
                if(!TextUtils.isEmpty(xml2json)){
                    AllAirTypeCraft kk = AllAirTypeCraft.parse(xml2json);
                    responseListner.onResponse(kk);
                } else {
                    responseListner.onResponse(null);
                }
            }

            @Override
            public void onEmptyOrError(String message) {
                responseListner.onEmptyOrError(message);
            }
        });
        net.execute();
    }

    public void getActypeByType(String userName, String checkCode,
            final ResponseListner responseListner) {
        //String url = "http://124.127.106.196:80/Login1.ashx";
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
                .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetActypeByType</CMD>")
                .append("<UserCode>"+userName+"</UserCode>")
                .append("<CheckCode>"+checkCode+"</CheckCode>")
                .append("<RequestData>")
                .append("<IAppObject xsi:type=\"AppAircraftType\">")
                .append("<AircraftType>CE560</AircraftType>")
                .append("<FuleLj>0</FuleLj>")
                .append("<Cglimit1>0</Cglimit1>")
                .append("<Cglimit2>0</Cglimit2>")
                .append("<WeightLimit>0</WeightLimit>")
                .append("<OpDate>0001-01-01T00:00:00</OpDate>")
                .append("<LstAirTypeSeat />")
                .append("</IAppObject>")
                .append("</RequestData>")
                .append("</MessageObject>");
        String xmlParam = sb.toString();
        NetBase net = new NetBase(BASE_URL, xmlParam, new ResponseListner<String>() {

            @Override
            public void onResponse(String response) {
                String xml2json = CommonUtils.xml2JSON(response);
                if(!TextUtils.isEmpty(xml2json)){
                    OneAirTypeCraft oneAirTypeCraft = OneAirTypeCraft.parse(xml2json);
                    responseListner.onResponse(oneAirTypeCraft);
                } else {
                    responseListner.onResponse(null);
                }
            }

            @Override
            public void onEmptyOrError(String message) {
              responseListner.onEmptyOrError(message);
            }
        });
        net.execute();
    }




    public void getAllUser(String userName, String checkCode, final ResponseListner responseListner) {
        //String url = "http://124.127.106.196:80/Login1.ashx";
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
                .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetAllUser</CMD>")
                .append("<UserCode>"+userName+"</UserCode>")
                .append("<CheckCode>"+checkCode+"</CheckCode>")
                .append("<RequestData>")
                .append("<IAppObject xsi:type=\"AppUser\">")
                .append("<UserCode>Test</UserCode>")
                .append("<ActiveStart>0001-01-01T00:00:00</ActiveStart>")
                .append("</IAppObject>")
                .append("</RequestData>")
                .append("</MessageObject>");
        String xmlParam = sb.toString();
        NetBase net = new NetBase(BASE_URL, xmlParam, new ResponseListner<String>() {
            @Override
            public void onResponse(String response) {
                if(responseListner != null){
                    responseListner.onResponse(response);
                }
                String xml2json = CommonUtils.xml2JSON(response);
                if(!TextUtils.isEmpty(xml2json)){

                    AllUsers parse = AllUsers.parse(xml2json);
                }
            }

            @Override
            public void onEmptyOrError(String message) {
                // TODO Auto-generated method stub

            }
        });
        net.execute();
    }



    //一、按用户获取此用户的授权信息
    public void getGrantsByUserCode(String userName, String checkCode,
            final ResponseListner responseListner) {
        //String url = "http://124.127.106.196:80/Login1.ashx";
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
                .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetGrantsByUserCode</CMD>")
                .append("<UserCode>"+userName+"</UserCode>")
                .append("<CheckCode>"+checkCode+"</CheckCode>")
                .append("<RequestData>")
                .append("<IAppObject xsi:type=\"AppUser\">")
                .append("<UserCode>Test</UserCode>")
                .append("<ActiveStart>0001-01-01T00:00:00</ActiveStart>")
                .append("</IAppObject>")
                .append("</RequestData>")
                .append("</MessageObject>");
        String xmlParam = sb.toString();
        NetBase net = new NetBase(BASE_URL, xmlParam, new ResponseListner<String>() {
            @Override
            public void onResponse(String response) {
                if(responseListner != null){
                    responseListner.onResponse(response);
                }
                String xml2json = CommonUtils.xml2JSON(response);
                if(!TextUtils.isEmpty(xml2json)){
                    UserGrantsInfo parse = UserGrantsInfo.parse(xml2json);

                }
            }

            @Override
            public void onEmptyOrError(String message) {
                // TODO Auto-generated method stub

            }
        });
        net.execute();
    }





    //一、按用户代码获取
    public void getUserByCode(String userName, String checkCode,
            final ResponseListner responseListner) {
        //String url = "http://124.127.106.196:80/Login1.ashx";
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
                .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetUserByCode</CMD>")
                .append("<UserCode>"+userName+"</UserCode>")
                .append("<CheckCode>"+checkCode+"</CheckCode>")
                .append("<RequestData>")
                .append("<IAppObject xsi:type=\"AppUser\">")
                .append("<UserCode>Test</UserCode>")
                .append("<ActiveStart>0001-01-01T00:00:00</ActiveStart>")
                .append("</IAppObject>")
                .append("</RequestData>")
                .append("</MessageObject>");
        String xmlParam = sb.toString();
        NetBase net = new NetBase(BASE_URL, xmlParam, new ResponseListner<String>() {

            @Override
            public void onResponse(String response) {
                if(responseListner != null){
                    responseListner.onResponse(response);
                }
                String xml2json = CommonUtils.xml2JSON(response);
                if(!TextUtils.isEmpty(xml2json)){
                    OneUsers oneUser = OneUsers.parse(xml2json);
                }
            }

            @Override
            public void onEmptyOrError(String message) {
                // TODO Auto-generated method stub

            }
        });
        net.execute();
    }




    public void getAllUserNew(String userName, String checkCode,
            final ResponseListner responseListner) {
        //String url = "http://124.127.106.196:80/Login1.ashx";
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
                .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetAllUserNew</CMD>")
                .append("<UserCode>"+userName+"</UserCode>")
                .append("<CheckCode>"+checkCode+"</CheckCode>")
                .append("<RequestData>")
                .append("<IAppObject xsi:type=\"AppUser\">")
                .append("<UserCode>Test</UserCode>")
                .append("<ActiveStart>0001-01-01T00:00:00</ActiveStart>")
                .append("</IAppObject>")
                .append("</RequestData>")
                .append("</MessageObject>");
        String xmlParam = sb.toString();
        NetBase net = new NetBase(BASE_URL, xmlParam, new ResponseListner<String>() {

            @Override
            public void onResponse(String response) {
                if(responseListner != null){
                    responseListner.onResponse(response);
                }
                String xml2json = CommonUtils.xml2JSON(response);
                if(!TextUtils.isEmpty(xml2json)){
                    AllUsersNew usersNew = AllUsersNew.parse(xml2json);
                }
            }

            @Override
            public void onEmptyOrError(String message) {
                // TODO Auto-generated method stub

            }
        });
        net.execute();
    }




    public void addFlightCd(String userName, String checkCode,
            final ResponseListner responseListner) {
        //String url = "http://124.127.106.196:80/Login1.ashx";
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
                .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>AddFlightCd</CMD>")
                .append("<UserCode>"+userName+"</UserCode>")
                .append("<CheckCode>"+checkCode+"</CheckCode>")
                .append("<RequestData>")
                .append("<IAppObject xsi:type=\"AircraftCd\">")
                .append("<AircraftReg>B8888</AircraftReg>")
                .append("<SeatId>1</SeatId>")
                .append("<FlightId>1</FlightId>")
                .append("<SeatCode>1号</SeatCode>")
                .append("<SeatType>S</SeatType>")
                .append("<AcTypeSeatLimit>180</AcTypeSeatLimit>")
                .append("<AcTypeLj>1000.01</AcTypeLj>")
                .append("<AcRegCagWeight>80</AcRegCagWeight>")
                .append("<AcRegCagLj>1000.01</AcRegCagLj>")
                .append("<SeatLastLimit>100</SeatLastLimit>")
                .append("<PassagerName>测试人员</PassagerName>")
                .append("<RealWeight>90</RealWeight>")
                .append("<OpUser>Test</OpUser>")
                .append("<OpDate>2015-01-29T22:45:06.9541255+08:00</OpDate>")
                .append("</IAppObject>")
                .append("</RequestData>")
                .append("</MessageObject>");
        String xmlParam = sb.toString();
        NetBase net = new NetBase(BASE_URL, xmlParam, new ResponseListner<String>() {

            @Override
            public void onResponse(String response) {
                if(responseListner != null){
                    responseListner.onResponse(response);
                }
                String xml2json = CommonUtils.xml2JSON(response);
                if(!TextUtils.isEmpty(xml2json)){
                    AddOneUsers parse = AddOneUsers.parse(xml2json);

                }

            }

            @Override
            public void onEmptyOrError(String message) {
                // TODO Auto-generated method stub

            }
        });
        net.execute();
    }

    public  void addFlightInfo(String userName, String checkCode, ResponseListner responseListner) {
        //String url = "http://124.127.106.196:80/Login1.ashx";
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
                .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>AddFlightInfo</CMD>")
                .append("<UserCode>"+userName+"</UserCode>")
                .append("<CheckCode>"+checkCode+"</CheckCode>")
                .append("<RequestData>")
                .append("<IAppObject xsi:type=\"FlightInfo\">")
                .append("<FlightId>1</FlightId>")
                .append("<FlightDate>2015-01-29T21:34:16.2984668+08:00</FlightDate>")
                .append("<AircraftReg>B8888</AircraftReg>")
                .append("<AircraftType>A320</AircraftType>")
                .append("<FlightNo>CFI090</FlightNo>")
                .append("<Dep4Code>ZBAA</Dep4Code>")
                .append("<DepAirportName>北京</DepAirportName>")
                .append("<Arr4Code>ZSHC</Arr4Code>")
                .append("<ArrAirportName>杭州</ArrAirportName>")
                .append("<MaxFule>2000</MaxFule>")
                .append("<RealFule>1200</RealFule>")
                .append("<SlieFule>100</SlieFule>")
                .append("<RouteFule>900</RouteFule>")
                .append("<TofWeight>3000</TofWeight>")
                .append("<LandWeight>1000</LandWeight>")
                .append("<NoFuleWeight>1000</NoFuleWeight>")
                .append("<AirportLimitWeight>200</AirportLimitWeight>")
                .append("<BalancePic>D:\\</BalancePic>")
                .append("<BalancePicName>测试图</BalancePicName>")
                .append("<OpUser>Test</OpUser>")
                .append("<OpDate>2015-01-29T21:34:16.2984668+08:00</OpDate>")
                .append("</IAppObject>")
                .append("</RequestData>")
                .append("</MessageObject>");
        String xmlParam = sb.toString();
        NetBase net = new NetBase(BASE_URL, xmlParam, responseListner);
        net.execute();
    }

    public void getSeatByAcReg(String aircraftReg, String bw, String lj, String opDate, String SysVersion, final ResponseListner<SeatByAcReg> responseListner){
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
                .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetSeatByAcReg</CMD>")
                .append("<UserCode>"+ UserManager.getInstance().getUser().getUserCode()+"</UserCode>")
                .append("<CheckCode>"+"09C5BCBC5A38CF01FC7CDC71F59D4925"+"</CheckCode>")
                .append("<RequestData>")
                .append("<IAppObject xsi:type=\"AppAircraft\">")
                .append("<AircraftReg>" + aircraftReg + "</AircraftReg>")
                .append("<Bw>" + bw + "</Bw>")
                .append("<Lj>" + lj + "</Lj>")
                .append("<OpDate>" + opDate + "</OpDate>")
                .append("<SysVersion> " + SysVersion + " </SysVersion>")
                .append("</IAppObject>\n" + "  </RequestData>\n" + "</MessageObject>")
                ;
        final String xmlParam = sb.toString();
        ResponseListner<String> responseListner1 = new ResponseListner<String>() {
            @Override public void onResponse(String response) {
                String xmlStr = CommonUtils.xml2JSON(response);
                SeatByAcReg engineeRoom = SeatByAcReg.parse(xmlStr);
                responseListner.onResponse(engineeRoom);
            }

            @Override public void onEmptyOrError(String message) {
                responseListner.onEmptyOrError(message);
            }
        }; NetBase net = new NetBase(BASE_URL, xmlParam, responseListner1);
        net.execute();
    };

}
