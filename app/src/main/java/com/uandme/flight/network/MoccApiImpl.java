package com.uandme.flight.network;

import android.text.TextUtils;
import com.uandme.flight.entity.AcWeightLimitByAcTypeResponse;
import com.uandme.flight.entity.AddFlightInfoResponse;
import com.uandme.flight.entity.AllAcTypeResponse;
import com.uandme.flight.entity.AllAirCraftResponse;
import com.uandme.flight.entity.AllSameTypeAirCraft;
import com.uandme.flight.entity.AllSbResponse;
import com.uandme.flight.entity.AllUsers;
import com.uandme.flight.entity.AllUsersNewResponse;
import com.uandme.flight.entity.FlightidResponse;
import com.uandme.flight.entity.FuleLimitByAcType;
import com.uandme.flight.entity.GrantsByUserCodeResponse;
import com.uandme.flight.entity.LoginUserInfo;
import com.uandme.flight.entity.OneAirCraft;
import com.uandme.flight.entity.OneAirTypeCraft;
import com.uandme.flight.entity.OneUsers;
import com.uandme.flight.entity.SeatByAcRegResponse;
import com.uandme.flight.entity.TUserInfo;
import com.uandme.flight.util.CommonUtils;
import com.uandme.flight.util.DateFormatUtil;
import com.uandme.flight.util.DigestUtils;
import com.uandme.flight.util.LogUtil;
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
                final String xml2json = CommonUtils.xml2JSON(response);
                TUserInfo userInfo = TUserInfo.parse(xml2json);
                if (userInfo == null
                        || RESULT_ERROR.equals(userInfo.ResponseObject.ResponseCode)
                        || "-100".equals(userInfo.ResponseObject.ResponseCode)) {
                    ll.onEmptyOrError(userInfo.ResponseObject.ResponseErr);
                    return;
                }
                final String guidCode = userInfo.ResponseObject.ResponseData.IAppObject.GUIDCode;
                String newStr = guidCode + pwd;
                newStr = DigestUtils.md5(newStr);
                newStr = newStr.toUpperCase();//注意必须是大写的 md5
                validateUser(userName, newStr, new ResponseListner<String>() {
                    @Override public void onResponse(String response) {
                        String xml2json2 = CommonUtils.xml2JSON(response);
                        LogUtil.LOGD(TAG, "doLogin === " + xml2json2);
                        LoginUserInfo info = LoginUserInfo.parse(xml2json2);
                        info.setCheckCode(guidCode);
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
    public void getAllAircraft(String userName, final ResponseListner responseListner) {
        //String url = "http://124.127.106.196:80/Login1.ashx";
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
                .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetAllAircraft</CMD>")
                .append("<UserCode>"+"Test"+"</UserCode>")
                .append("<CheckCode>"+ UserManager.getInstance().getUser().getCheckCode() +"</CheckCode>")
                .append("RequestData/>").append("</MessageObject>");
        String xmlParam = sb.toString();
        NetBase net = new NetBase(BASE_URL, xmlParam, new ResponseListner<String>() {
            @Override
            public void onResponse(String response) {
                String xml2json = CommonUtils.xml2JSON(response);
                LogUtil.LOGD(TAG, "getAllAircraft === " + xml2json);
                if(!TextUtils.isEmpty(xml2json)){
                    AllAirCraftResponse allAirCraft = AllAirCraftResponse.parse(xml2json);
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
    public void getAircraftByAcReg(String userName,  String airReg,
            final ResponseListner responseListner) {
        //String url = "http://124.127.106.196:80/Login1.ashx";
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
                .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetAircraftByAcReg</CMD>")
                .append("<UserCode>"+userName+"</UserCode>")
                .append("<CheckCode>"+ UserManager.getInstance().getUser().getCheckCode() +"</CheckCode>")
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
                LogUtil.LOGD(TAG, "getAircraftByAcReg === " + xml2json);
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
    public void getAircraftByAcType(String userName,  String AircraftType,
            final ResponseListner responseListner) {
        //String url = "http://124.127.106.196:80/Login1.ashx";
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
                .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>getAircraftByAcType</CMD>")
                .append("<UserCode>"+userName+"</UserCode>")
                .append("<CheckCode>"
                        + UserManager.getInstance().getUser().getCheckCode()
                        + "</CheckCode>")
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
                LogUtil.LOGD(TAG, "getAircraftByAcReg === " + xml2json);
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


    public void addNewAircraft(String userName, final ResponseListner responseListner) {
        //String url = "http://124.127.106.196:80/Login1.ashx";
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
                .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>AddNewAircraft</CMD>")
                .append("<UserCode>"+userName+"</UserCode>")
                .append("<CheckCode>"+ UserManager.getInstance().getUser().getCheckCode() +"</CheckCode>")
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



    public void getActypeByType(String userName,
            final ResponseListner responseListner) {
        //String url = "http://124.127.106.196:80/Login1.ashx";
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
                .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetActypeByType</CMD>")
                .append("<UserCode>"+userName+"</UserCode>")
                .append("<CheckCode>"+ UserManager.getInstance().getUser().getCheckCode()+"</CheckCode>")
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
                LogUtil.LOGD(TAG, "getActypeByType ===== " + xml2json);
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




    public void getAllUser(String userName,  final ResponseListner responseListner) {
        //String url = "http://124.127.106.196:80/Login1.ashx";
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
                .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetAllUser</CMD>")
                .append("<UserCode>"+userName+"</UserCode>")
                .append("<CheckCode>"+UserManager.getInstance().getUser().getCheckCode()+"</CheckCode>")
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
                String xml2json = CommonUtils.xml2JSON(response);
                LogUtil.LOGD(TAG,"AllUsers ==== " + xml2json);
                if(!TextUtils.isEmpty(xml2json)){
                    AllUsers parse = AllUsers.parse(xml2json);
                    responseListner.onResponse(parse);
                } else {
                    responseListner.onResponse(null);
                }
            }

            @Override
            public void onEmptyOrError(String message) {
                responseListner.onResponse(null);
            }
        });
        net.execute();
    }

    //一、按用户代码获取
    public void getUserByCode(String userName,
            final ResponseListner responseListner) {
        //String url = "http://124.127.106.196:80/Login1.ashx";
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
                .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetUserByCode</CMD>")
                .append("<UserCode>"+userName+"</UserCode>")
                .append("<CheckCode>"+UserManager.getInstance().getUser().getCheckCode()+"</CheckCode>")
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




    public void getAllUserNew(String userName,
            final ResponseListner responseListner) {
        //String url = "http://124.127.106.196:80/Login1.ashx";
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
                .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetAllUserNew</CMD>")
                .append("<UserCode>"+userName+"</UserCode>")
                .append("<CheckCode>"+UserManager.getInstance().getUser().getCheckCode()+"</CheckCode>")
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
                    AllUsersNewResponse usersNew = AllUsersNewResponse.parse(xml2json);
                }
            }

            @Override
            public void onEmptyOrError(String message) {
                // TODO Auto-generated method stub

            }
        });
        net.execute();
    }


    public void getSeatByAcReg(String aircraftReg, String bw, String lj, String opDate, String SysVersion, final ResponseListner<SeatByAcRegResponse> responseListner){
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
                .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetSeatByAcReg</CMD>")
                .append("<UserCode>"+ UserManager.getInstance().getUser().getUserCode()+"</UserCode>")
                .append("<CheckCode>"+UserManager.getInstance().getUser().getCheckCode()+"</CheckCode>")
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
                LogUtil.LOGD(TAG, "getSeatByAcReg ===== " + xmlStr);
                SeatByAcRegResponse engineeRoom = SeatByAcRegResponse.parse(xmlStr);
                responseListner.onResponse(engineeRoom);
            }

            @Override public void onEmptyOrError(String message) {
                responseListner.onEmptyOrError(message);
            }
        }; NetBase net = new NetBase(BASE_URL, xmlParam, responseListner1);
        net.execute();
    };

    @Override public void getFuleLimitByAcType(String AircraftType, String PortLimit,
            String TofWeightLimit, String LandWeightLimit, String Mzfw, String OpDate,
            String SysVersion, final ResponseListner<FuleLimitByAcType> responseListner) {
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
                .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetFuleLimitByAcType</CMD>")
                .append("<UserCode>"
                        + UserManager.getInstance().getUser().getUserCode()
                        + "</UserCode>")
                .append("<CheckCode>"+ UserManager.getInstance().getUser().getCheckCode()+"</CheckCode>")
                .append("<RequestData>")
                .append("<IAppObject xsi:type=\"AppAircraft\">")
                .append("<AircraftType>" + AircraftType + "</AircraftType>")
                .append("<PortLimit>" + PortLimit + "</PortLimit>")
                .append("<TofWeightLimit>" + TofWeightLimit + "</TofWeightLimit>")
                .append("<LandWeightLimit>" + LandWeightLimit + "</LandWeightLimit>")
                .append("<Mzfw>" + Mzfw + "</Mzfw>")
                .append("<OpDate>" + OpDate + "</OpDate>")
                .append("<SysVersion> " + SysVersion + " </SysVersion>")
                .append("</IAppObject>\n" + "  </RequestData>\n" + "</MessageObject>")
                ;
        final String xmlParam = sb.toString();
        ResponseListner<String> responseListner1 = new ResponseListner<String>() {
            @Override public void onResponse(String response) {
                String xmlStr = CommonUtils.xml2JSON(response);
                LogUtil.LOGD(TAG, "getFuleLimitByAcType ===== " + xmlStr);
                FuleLimitByAcType fuleLimitByAcType = FuleLimitByAcType.parse(xmlStr);
                responseListner.onResponse(fuleLimitByAcType);
            }

            @Override public void onEmptyOrError(String message) {
                responseListner.onEmptyOrError(message);
            }
        }; NetBase net = new NetBase(BASE_URL, xmlParam, responseListner1);
        net.execute();
    }

    @Override public void getAcWeightLimitByAcType(String AircraftType, String PortLimit,
            String TofWeightLimit, String LandWeightLimit, String Mzfw, String OpDate,
            String SysVersion, final ResponseListner<AcWeightLimitByAcTypeResponse> responseListner) {

        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
                .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetAcWeightLimitByAcType</CMD>")
                .append("<UserCode>" + UserManager.getInstance().getUser().getUserCode()+ "</UserCode>")
                .append("<CheckCode>" + UserManager.getInstance().getUser().getCheckCode() + "</CheckCode>")
                .append("<RequestData>")
                .append("<IAppObject xsi:type=\"AppAircraftType\">")
                .append("<AircraftType>" +AircraftType+ "</AircraftType>")
                .append("<PortLimit>" +PortLimit+ "</PortLimit>")
                .append("<TofWeightLimit>"  +TofWeightLimit+ "</TofWeightLimit>")
                .append("<LandWeightLimit>" +LandWeightLimit+ "</LandWeightLimit>")
                .append("<Mzfw>0</Mzfw>")
                .append("<OpDate>0001-01-01T00:00:00</OpDate>")
                .append("</IAppObject>\n" + "  </RequestData>\n" + "</MessageObject>")
                ;
        final String xmlParam = sb.toString();
        ResponseListner<String> responseListner1 = new ResponseListner<String>() {
            @Override public void onResponse(String response) {
                String xmlStr = CommonUtils.xml2JSON(response);
                LogUtil.LOGD(TAG, "getAcWeightLimitByAcType ===== " + xmlStr);
                AcWeightLimitByAcTypeResponse allAcType = AcWeightLimitByAcTypeResponse.parse(xmlStr);
                responseListner.onResponse(allAcType);
            }

            @Override public void onEmptyOrError(String message) {
                responseListner.onEmptyOrError(message);
            }
        }; NetBase net = new NetBase(BASE_URL, xmlParam, responseListner1);
        net.execute();

    }

    @Override public void getAllAcType(final ResponseListner<AllAcTypeResponse> responseListner) {
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
                .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetAllAcType</CMD>")
                .append("<UserCode>"+ UserManager.getInstance().getUser().getUserCode()+"</UserCode>")
                .append("<CheckCode>"+ UserManager.getInstance().getUser().getCheckCode()+"</CheckCode>")
                .append("<RequestData/>")
                .append("</MessageObject>")
                ;
        final String xmlParam = sb.toString();
        ResponseListner<String> responseListner1 = new ResponseListner<String>() {
            @Override public void onResponse(String response) {
                String xmlStr = CommonUtils.xml2JSON(response);
                LogUtil.LOGD(TAG, "getAllAcType ===== " + xmlStr);
                AllAcTypeResponse allAcType = AllAcTypeResponse.parse(xmlStr);
                responseListner.onResponse(allAcType);
            }

            @Override public void onEmptyOrError(String message) {
                responseListner.onEmptyOrError(message);
            }
        }; NetBase net = new NetBase(BASE_URL, xmlParam, responseListner1);
        net.execute();
    }

    @Override public void getAllSb(final ResponseListner<AllSbResponse> responseListner) {
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
                .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetAllSb</CMD>")
                .append("<UserCode>"+ UserManager.getInstance().getUser().getUserCode()+"</UserCode>")
                .append("<CheckCode>"+ UserManager.getInstance().getUser().getCheckCode()+"</CheckCode>")
                .append("<RequestData/>")
                .append("</MessageObject>")
                ;
        final String xmlParam = sb.toString();
        ResponseListner<String> responseListner1 = new ResponseListner<String>() {
            @Override public void onResponse(String response) {
                String xmlStr = CommonUtils.xml2JSON(response);
                LogUtil.LOGD(TAG, "GetAllSb ===== " + xmlStr);
                AllSbResponse allAcType = AllSbResponse.parse(xmlStr);
                responseListner.onResponse(allAcType);
            }

            @Override public void onEmptyOrError(String message) {
                responseListner.onEmptyOrError(message);
            }
        }; NetBase net = new NetBase(BASE_URL, xmlParam, responseListner1);
        net.execute();
    }

    //StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
    //        .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
    //        .append("<CMD>GetGrantsByUserCode</CMD>")
    //        .append("<UserCode>"
    //                + UserManager.getInstance().getUser().getUserCode()
    //                + "</UserCode>")
    //        .append("<CheckCode>"
    //                + UserManager.getInstance().getUser().getCheckCode()
    //                + "</CheckCode>")
    //        .append("<RequestData>")
    //        .append("<IAppObject xsi:type=\"AppUser\">")
    //        .append("<AircraftReg>" + AircraftReg + "</AircraftReg>")
    //        .append("<SeatId>" + SeatId + "</SeatId>")
    //        .append("<FlightId>" + FlightId + "</FlightId>")
    //        .append("<SeatCode>" + SeatCode + "</SeatCode>")
    //        .append("<SeatType>" + SeatType + "</SeatType>")
    //        .append("<AcTypeSeatLimit>" + AcTypeSeatLimit + "</AcTypeSeatLimit>")
    //        .append("<AcTypeLj>" + AcTypeLj + "</AcTypeLj>")
    //        .append("<AcRegCagWeight>" + 20 + "</AcRegCagWeight>")
    //        .append("<AcRegCagLj>" + AcRegCagLj + "</AcRegCagLj>")
    //        .append("<SeatLastLimit>" + SeatLastLimit + "</SeatLastLimit>")
    //        .append("<PassagerName>" + PassagerName + "</PassagerName>")
    //        .append("<RealWeight>" + RealWeight + "</RealWeight>")
    //        .append("<OpUser>" + OpUser + "</OpUser>")
    //        .append("<OpDate> " + OpDate + " </OpDate>")
    //        .append("</IAppObject>")
    //        .append("</RequestData>")
    //        .append("</MessageObject>");
    @Override public void addFlightCd(String AircraftReg, String SeatId, String FlightId, String SeatCode, String SeatType, String AcTypeSeatLimit, String AcTypeLj, String AcRegCagWeight, String AcRegCagLj, String SeatLastLimit, String PassagerName, String RealWeight, String OpUser, String OpDate, final ResponseListner<GrantsByUserCodeResponse> responseListner) {
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
                .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>AddFlightCd</CMD>")
                .append("<UserCode>"
                        + UserManager.getInstance().getUser().getUserCode()
                        + "</UserCode>")
                .append("<CheckCode>"
                        + UserManager.getInstance().getUser().getCheckCode()
                        + "</CheckCode>")
                .append("<RequestData>")
                .append("<IAppObject xsi:type=\"AircraftCd\">")
                .append("<AircraftReg>" + AircraftReg + "</AircraftReg>")
                .append("<SeatId>" + SeatId + "</SeatId>")
                .append("<FlightId>" + FlightId + "</FlightId>")
                .append("<SeatCode>" + SeatCode + "</SeatCode>")
                .append("<SeatType>" + SeatType + "</SeatType>")
                .append("<AcTypeSeatLimit>" + AcTypeSeatLimit + "</AcTypeSeatLimit>")
                .append("<AcTypeLj>" + AcTypeLj + "</AcTypeLj>")
                .append("<AcRegCagWeight>" + 20 + "</AcRegCagWeight>")
                .append("<AcRegCagLj>" + AcRegCagLj + "</AcRegCagLj>")
                .append("<SeatLastLimit>" + SeatLastLimit + "</SeatLastLimit>")
                .append("<PassagerName>" + PassagerName + "</PassagerName>")
                .append("<RealWeight>" + RealWeight + "</RealWeight>")
                .append("<OpUser>" + OpUser + "</OpUser>")
                .append("<OpDate> " + OpDate + " </OpDate>")
                .append("</IAppObject>")
                .append("</RequestData>")
                .append("</MessageObject>");
        String xmlParam = sb.toString();
        NetBase net = new NetBase(BASE_URL, xmlParam, new ResponseListner<String>() {
            @Override
            public void onResponse(String response) {
                String xml2json = CommonUtils.xml2JSON(response);
                GrantsByUserCodeResponse parse = GrantsByUserCodeResponse.parse(xml2json);
                responseListner.onResponse(parse);
            }

            @Override
            public void onEmptyOrError(String message) {
                responseListner.onEmptyOrError(message);
            }
        });
        net.execute();
    }

        //一、按用户获取此用户的授权信息
        public void getGrantsByUserCode(String userName, final ResponseListner<GrantsByUserCodeResponse> responseListner) {
            //String url = "http://124.127.106.196:80/Login1.ashx";
            StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
                    .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetGrantsByUserCode</CMD>")
                .append("<UserCode>"+ UserManager.getInstance().getUser().getUserCode() +"</UserCode>")
                .append("<CheckCode>"
                        + UserManager.getInstance().getUser().getCheckCode()
                        + "</CheckCode>")
                .append("<RequestData>")
                .append("<IAppObject xsi:type=\"AppUser\">")
                .append("<UserCode> " + userName + " </UserCode>")
                .append("<ActiveStart> " + DateFormatUtil.formatTDate() + "</ActiveStart>")
                .append("</IAppObject>")
                .append("</RequestData>")
                .append("</MessageObject>");
        String xmlParam = sb.toString();
        NetBase net = new NetBase(BASE_URL, xmlParam, new ResponseListner<String>() {
            @Override
            public void onResponse(String response) {
                String xml2json = CommonUtils.xml2JSON(response);
                GrantsByUserCodeResponse parse = GrantsByUserCodeResponse.parse(xml2json);
                responseListner.onResponse(parse);
            }

            @Override
            public void onEmptyOrError(String message) {
                responseListner.onEmptyOrError(message);
            }
        });
        net.execute();
    }

    @Override public void addFlightInfo(String FlightId, String FlightDate, String AircraftReg,
            String AircraftType, String FlightNo, String Dep4Code, String DepAirportName,
            String Arr4Code, String ArrAirportName, String MaxFule, String RealFule,
            String SlieFule, String RouteFule, String TofWeight, String LandWeight,
            String NoFuleWeight, String AirportLimitWeight, String BalancePic,
            String BalancePicName, String OpUser, String OpDate,
            final ResponseListner<AddFlightInfoResponse> responseListner) {
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
                .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>AddFlightInfo</CMD>")
                .append("<UserCode>"
                        + UserManager.getInstance().getUser().getUserCode()
                        + "</UserCode>")
                .append("<CheckCode>"
                        + UserManager.getInstance().getUser().getCheckCode()
                        + "</CheckCode>")
                .append("<RequestData>")
                .append("<IAppObject xsi:type=\"FlightInfo\">")
                .append("<FlightId>" + FlightId + "</FlightId>")
                .append("<FlightDate>" + FlightDate + "</FlightDate>")
                .append("<AircraftReg>" + AircraftReg + "</AircraftReg>")
                .append("<AircraftType>" + AircraftType + "</AircraftType>")
                .append("<FlightNo>" + FlightNo + "</FlightNo>")
                .append("<Dep4Code>" + Dep4Code + "</Dep4Code>")
                .append("<DepAirportName>" + DepAirportName + "</DepAirportName>")
                .append("<Arr4Code>" + Arr4Code + "</Arr4Code>")
                .append("<ArrAirportName>" + ArrAirportName + "</ArrAirportName>")
                .append("<MaxFule>" + MaxFule + "</MaxFule>")
                .append("<RealFule>" + RealFule + "</RealFule>")
                .append("<SlieFule>" + SlieFule + "</SlieFule>")
                .append("<RouteFule>" + RouteFule + "</RouteFule>")
                .append("<TofWeight>" + TofWeight + "</TofWeight>")
                .append("<LandWeight>" + LandWeight + "</LandWeight>")
                .append("<NoFuleWeight>" + NoFuleWeight + "</NoFuleWeight>")
                .append("<AirportLimitWeight>" + AirportLimitWeight + "</AirportLimitWeight>")
                .append("<BalancePic>" + BalancePic + "</BalancePic>")
                .append("<BalancePicName>" + BalancePicName + "</BalancePicName>")
                .append("<OpUser>" + OpUser + "</OpUser>")
                .append("<OpDate>" + OpDate + "</OpDate>")
                .append("</IAppObject>")
                .append("</RequestData>")
                .append("</MessageObject>");
        String xmlParam = sb.toString();
        NetBase net = new NetBase(BASE_URL, xmlParam, new ResponseListner<String>() {
            @Override
            public void onResponse(String response) {
                String xml2json = CommonUtils.xml2JSON(response);
                AddFlightInfoResponse parse = AddFlightInfoResponse.parse(xml2json);
                responseListner.onResponse(parse);
            }

            @Override
            public void onEmptyOrError(String message) {
                responseListner.onEmptyOrError(message);
            }
        });
        net.execute();
    }

    @Override public void getflightid(final ResponseListner<FlightidResponse> responseListner) {
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>")
                .append("<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetFlightId</CMD>")
                .append("<UserCode>"+ UserManager.getInstance().getUser().getUserCode()+"</UserCode>")
                .append("<CheckCode>"+ UserManager.getInstance().getUser().getCheckCode()+"</CheckCode>")
                .append("<RequestData/>")
                .append("</MessageObject>")
                ;
        final String xmlParam = sb.toString();
        ResponseListner<String> responseListner1 = new ResponseListner<String>() {
            @Override public void onResponse(String response) {
                String xmlStr = CommonUtils.xml2JSON(response);
                LogUtil.LOGD(TAG, "GetAllSb ===== " + xmlStr);
                FlightidResponse allAcType = FlightidResponse.parse(xmlStr);
                responseListner.onResponse(allAcType);
            }

            @Override public void onEmptyOrError(String message) {
                responseListner.onEmptyOrError(message);
            }
        }; NetBase net = new NetBase(BASE_URL, xmlParam, responseListner1);
        net.execute();
    }
}
