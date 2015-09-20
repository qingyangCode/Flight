package com.xiaoqing.flight.network;

import android.os.AsyncTask;
import android.text.TextUtils;
import com.xiaoqing.flight.entity.AcGrantsResponse;
import com.xiaoqing.flight.entity.AcWeightLimitResponse;
import com.xiaoqing.flight.entity.AddFlightInfoResponse;
import com.xiaoqing.flight.entity.AllAcSbResponse;
import com.xiaoqing.flight.entity.AllAcTypeResponse;
import com.xiaoqing.flight.entity.AllAirCraftResponse;
import com.xiaoqing.flight.entity.AllAirportResponse;
import com.xiaoqing.flight.entity.AllSbResponse;
import com.xiaoqing.flight.entity.AllUsersResponse;
import com.xiaoqing.flight.entity.FlightidResponse;
import com.xiaoqing.flight.entity.FuleLimitResponse;
import com.xiaoqing.flight.entity.GrantsByUserCodeResponse;
import com.xiaoqing.flight.entity.LoginUserInfoResponse;
import com.xiaoqing.flight.entity.MessageResponse;
import com.xiaoqing.flight.entity.ResetPasswordResponse;
import com.xiaoqing.flight.entity.SeatByAcRegResponse;
import com.xiaoqing.flight.entity.TUserInfo;
import com.xiaoqing.flight.entity.UpdateInfoResponse;
import com.xiaoqing.flight.entity.ValidCaptionResponse;
import com.xiaoqing.flight.util.CommonUtils;
import com.xiaoqing.flight.util.DateFormatUtil;
import com.xiaoqing.flight.util.DigestUtils;
import com.xiaoqing.flight.util.LogUtil;
import com.xiaoqing.flight.util.UserManager;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by QingYang on 15/7/21.
 */
public class MoccApiImpl implements MoccApi {

    private final String TAG = MoccApiImpl.class.getSimpleName();

    /**
     * 获得校验值
     */
    @Override public void getRandomString(String userName, ResponseListner responseListner) {
        String url = "http://124.127.106.196:80/Login1.ashx";
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>").append(
                "<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetRandomString</CMD>")
                .append("<UserCode>" + userName + "</UserCode>")
                .append("RequestData/>").append("</MessageObject>");
        String xmlParam = sb.toString();
        NetBase net = new NetBase(url, xmlParam, responseListner);
        net.execute();
    }

    /**
     * 验证用户
     */
    @Override public void validateUser(String userName, String pwd,
            ResponseListner responseListner) {
        //String url = "http://124.127.106.196:80/Login1.ashx";
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>").append(
                "<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
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
     */
    public void doLogin(final String userName, final String pwd, final ResponseListner ll) {
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
                final String checkCode = newStr;
                validateUser(userName, newStr, new ResponseListner<String>() {
                    @Override public void onResponse(String response) {
                        String xml2json2 = CommonUtils.xml2JSON(response);
                        LogUtil.LOGD(TAG, "doLogin === " + xml2json2);
                        LoginUserInfoResponse info = LoginUserInfoResponse.parse(xml2json2);
                        //UserManager.getInstance().getUser().setCodeCheck(newStr);
                        info.setCodeCheck(checkCode);
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
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>").append(
                "<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetAllAircraft</CMD>")
                .append("<UserCode>" + UserManager.getInstance().getUser().getUserCode() + "</UserCode>")
                .append("<CheckCode>"
                        + UserManager.getInstance().getUser().getCodeCheck()
                        + "</CheckCode>")
                .append("RequestData/>").append("</MessageObject>");
        String xmlParam = sb.toString();
        NetBase net = new NetBase(BASE_URL, xmlParam, new ResponseListner<String>() {
            @Override public void onResponse(String response) {
                String xml2json = CommonUtils.xml2JSON(response);
                LogUtil.LOGD(TAG, "getAllAircraft === " + xml2json);
                if (!TextUtils.isEmpty(xml2json)) {
                    AllAirCraftResponse allAirCraft = AllAirCraftResponse.parse(xml2json);
                    responseListner.onResponse(allAirCraft);
                }
            }

            @Override public void onEmptyOrError(String message) {
                responseListner.onEmptyOrError(message);
            }
        });
        net.execute();
    }

    public void getAllUser(String userName, final ResponseListner responseListner) {
        //String url = "http://124.127.106.196:80/Login1.ashx";
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>").append(
                "<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetAllUser</CMD>")
                .append("<UserCode>" + UserManager.getInstance().getUser().getUserCode() + "</UserCode>")
                .append("<CheckCode>"
                        + UserManager.getInstance().getUser().getCodeCheck()
                        + "</CheckCode>")
                .append("<RequestData>")
                .append("<IAppObject xsi:type=\"AppUser\">")
                .append("<UserCode>" + userName + "</UserCode>")
                .append("<ActiveStart>" + DateFormatUtil.formatTDate() + "</ActiveStart>")
                .append("</IAppObject>")
                .append("</RequestData>")
                .append("</MessageObject>");
        String xmlParam = sb.toString();
        NetBase net = new NetBase(BASE_URL, xmlParam, new ResponseListner<String>() {
            @Override public void onResponse(String response) {
                String xml2json = CommonUtils.xml2JSON(response);
                LogUtil.LOGD(TAG, "GetAllUser ==== " + xml2json);
                if (!TextUtils.isEmpty(xml2json)) {
                    AllUsersResponse parse = AllUsersResponse.parse(xml2json);
                    responseListner.onResponse(parse);
                } else {
                    responseListner.onResponse(null);
                }
            }

            @Override public void onEmptyOrError(String message) {
                responseListner.onResponse(null);
            }
        });
        net.execute();
    }

    public void getSeatByAcReg(String aircraftReg, String bw, String lj, String opDate, String SysVersion, final ResponseListner<SeatByAcRegResponse> responseListner) {
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>").append(
                "<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetSeatByAcReg</CMD>")
                .append("<UserCode>"
                        + UserManager.getInstance().getUser().getUserCode()
                        + "</UserCode>")
                .append("<CheckCode>"
                        + UserManager.getInstance().getUser().getCodeCheck()
                        + "</CheckCode>")
                .append("<RequestData>")
                .append("<IAppObject xsi:type=\"AppAircraft\">")
                .append("<AircraftReg>" + aircraftReg + "</AircraftReg>")
                .append("<Bw>" + bw + "</Bw>")
                .append("<Lj>" + lj + "</Lj>")
                .append("<OpDate>" + opDate + "</OpDate>")
                .append("<SysVersion> " + SysVersion + " </SysVersion>")
                .append("</IAppObject>\n" + "  </RequestData>\n" + "</MessageObject>");
        final String xmlParam = sb.toString();
        ResponseListner<String> responseListner1 = new ResponseListner<String>() {
            @Override public void onResponse(String response) {
                String xmlStr = CommonUtils.xml2JSON(response);
                LogUtil.LOGD(TAG, "GetSeatByAcReg ===== " + xmlStr);
                SeatByAcRegResponse engineeRoom = SeatByAcRegResponse.parse(xmlStr);
                responseListner.onResponse(engineeRoom);
            }

            @Override public void onEmptyOrError(String message) {
                responseListner.onEmptyOrError(message);
            }
        };
        NetBase net = new NetBase(BASE_URL, xmlParam, responseListner1);
        net.execute();
    };

    @Override public void getFuleLimitByAcType(String AircraftType, String PortLimit, String TofWeightLimit,
            String LandWeightLimit, String Mzfw, String OpDate, String SysVersion, final ResponseListner<FuleLimitResponse> responseListner) {
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>").append(
                "<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetFuleLimitByAcType</CMD>")
                .append("<UserCode>"
                        + UserManager.getInstance().getUser().getUserCode()
                        + "</UserCode>")
                .append("<CheckCode>"
                        + UserManager.getInstance().getUser().getCodeCheck()
                        + "</CheckCode>")
                .append("<RequestData>")
                .append("<IAppObject xsi:type=\"AppAircraft\">")
                .append("<AircraftType>" + AircraftType + "</AircraftType>")
                .append("<PortLimit>" + PortLimit + "</PortLimit>")
                .append("<TofWeightLimit>" + TofWeightLimit + "</TofWeightLimit>")
                .append("<LandWeightLimit>" + LandWeightLimit + "</LandWeightLimit>")
                .append("<Mzfw>" + Mzfw + "</Mzfw>")
                .append("<OpDate>" + OpDate + "</OpDate>")
                .append("<SysVersion> " + SysVersion + " </SysVersion>")
                .append("</IAppObject>\n" + "  </RequestData>\n" + "</MessageObject>");
        final String xmlParam = sb.toString();
        ResponseListner<String> responseListner1 = new ResponseListner<String>() {
            @Override public void onResponse(String response) {
                if (response.contains("</ResponseData>")) response = response.replace("</ResponseData>",
                        "<IAppObject xsi:type=\"AppMessage\"> </IAppObject></ResponseData>");
                String xmlStr = CommonUtils.xml2JSON(response);
                LogUtil.LOGD(TAG, "GetFuleLimitByAcType ===== " + xmlStr);
                FuleLimitResponse fuleLimitResponse = FuleLimitResponse.parse(xmlStr);
                responseListner.onResponse(fuleLimitResponse);
            }

            @Override public void onEmptyOrError(String message) {
                responseListner.onEmptyOrError(message);
            }
        };
        NetBase net = new NetBase(BASE_URL, xmlParam, responseListner1);
        net.execute();
    }

    @Override public void getAcWeightLimitByAcType(String AircraftType, String PortLimit,
            String TofWeightLimit, String LandWeightLimit, String Mzfw, String OpDate,
            String SysVersion, final ResponseListner<AcWeightLimitResponse> responseListner) {

        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>").append(
                "<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetAcWeightLimitByAcType</CMD>")
                .append("<UserCode>" + UserManager.getInstance().getUser().getUserCode() + "</UserCode>")
                .append("<CheckCode>" + UserManager.getInstance().getUser().getCodeCheck() + "</CheckCode>")
                .append("<RequestData>")
                .append("<IAppObject xsi:type=\"AppAircraftType\">")
                .append("<AircraftType>" + AircraftType + "</AircraftType>")
                .append("<PortLimit>" + PortLimit + "</PortLimit>")
                .append("<TofWeightLimit>" + TofWeightLimit + "</TofWeightLimit>")
                .append("<LandWeightLimit>" + LandWeightLimit + "</LandWeightLimit>")
                .append("<Mzfw>" + Mzfw + "</Mzfw>")
                .append("<OpDate>" + DateFormatUtil.formatTDate() + "</OpDate>")
                .append("</IAppObject>\n" + "  </RequestData>\n" + "</MessageObject>");
        final String xmlParam = sb.toString();
        ResponseListner<String> responseListner1 = new ResponseListner<String>() {
            @Override public void onResponse(String response) {
                String xmlStr = CommonUtils.xml2JSON(response);
                LogUtil.LOGD(TAG, "getAcWeightLimitByAcType ===== " + xmlStr);
                AcWeightLimitResponse allAcType = AcWeightLimitResponse.parse(xmlStr);
                responseListner.onResponse(allAcType);
            }

            @Override public void onEmptyOrError(String message) {
                responseListner.onEmptyOrError(message);
            }
        };
        NetBase net = new NetBase(BASE_URL, xmlParam, responseListner1);
        net.execute();
    }

    @Override public void getAllAcType(final ResponseListner<AllAcTypeResponse> responseListner) {
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>").append(
                "<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetAllAcType</CMD>")
                .append("<UserCode>"
                        + UserManager.getInstance().getUser().getUserCode()
                        + "</UserCode>")
                .append("<CheckCode>"
                        + UserManager.getInstance().getUser().getCodeCheck()
                        + "</CheckCode>")
                .append("<RequestData/>")
                .append("</MessageObject>");
        final String xmlParam = sb.toString();
        ResponseListner<String> responseListner1 = new ResponseListner<String>() {
            @Override public void onResponse(String response) {
                String xmlStr = CommonUtils.xml2JSON(response);
                LogUtil.LOGD(TAG, "GetAllAcType ===== " + xmlStr);
                AllAcTypeResponse allAcType = AllAcTypeResponse.parse(xmlStr);
                responseListner.onResponse(allAcType);
            }

            @Override public void onEmptyOrError(String message) {
                responseListner.onEmptyOrError(message);
            }
        };
        NetBase net = new NetBase(BASE_URL, xmlParam, responseListner1);
        net.execute();
    }

    @Override public void getAllSb(final ResponseListner<AllSbResponse> responseListner) {
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>").append(
                "<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetAllSb</CMD>")
                .append("<UserCode>"
                        + UserManager.getInstance().getUser().getUserCode()
                        + "</UserCode>")
                .append("<CheckCode>"
                        + UserManager.getInstance().getUser().getCodeCheck()
                        + "</CheckCode>")
                .append("<RequestData/>")
                .append("</MessageObject>");
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
        };
        NetBase net = new NetBase(BASE_URL, xmlParam, responseListner1);
        net.execute();
    }

    @Override public void addFlightCd(String AircraftReg, String SeatId, String FlightId, String SeatCode, String SeatType, String AcTypeSeatLimit, String AcTypeLj,
            String AcRegCagWeight, String AcRegCagLj, String SeatLastLimit, String PassagerName,
            String RealWeight, String OpUser, String OpDate, final ResponseListner<GrantsByUserCodeResponse> responseListner) {
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>").append(
                "<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>AddFlightCd</CMD>")
                .append("<UserCode>"
                        + UserManager.getInstance().getUser().getUserCode()
                        + "</UserCode>")
                .append("<CheckCode>"
                        + UserManager.getInstance().getUser().getCodeCheck()
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
                .append("<AcRegCagWeight>" + AcRegCagWeight + "</AcRegCagWeight>")
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
            @Override public void onResponse(String response) {
                String xml2json = CommonUtils.xml2JSON(response);
                GrantsByUserCodeResponse parse = GrantsByUserCodeResponse.parse(xml2json);
                responseListner.onResponse(parse);
            }

            @Override public void onEmptyOrError(String message) {
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
            String BalancePicName, String OpUser, String OpDate, String Caption, String TkoZx, String TkoMac,
            final ResponseListner<AddFlightInfoResponse> responseListner) {
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>").append(
                "<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>AddFlightInfo</CMD>")
                .append("<UserCode>"
                        + UserManager.getInstance().getUser().getUserCode()
                        + "</UserCode>")
                .append("<CheckCode>"
                        + UserManager.getInstance().getUser().getCodeCheck()
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
                .append("<Caption>" + Caption + "</Caption>")
                .append("<TkoZx>" + TkoZx + "</TkoZx>")
                .append("<TkoMac>" + TkoMac + "</TkoMac>")
                .append("</IAppObject>")
                .append("</RequestData>")
                .append("</MessageObject>");
        String xmlParam = sb.toString();
        NetBase net = new NetBase(BASE_URL, xmlParam, new ResponseListner<String>() {
            @Override public void onResponse(String response) {
                String xml2json = CommonUtils.xml2JSON(response);
                AddFlightInfoResponse parse = AddFlightInfoResponse.parse(xml2json);
                responseListner.onResponse(parse);
            }

            @Override public void onEmptyOrError(String message) {
                responseListner.onEmptyOrError(message);
            }
        });
        net.execute();
    }

    @Override public void getflightid(final ResponseListner<FlightidResponse> responseListner) {
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>").append(
                "<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetFlightId</CMD>")
                .append("<UserCode>"
                        + UserManager.getInstance().getUser().getUserCode()
                        + "</UserCode>")
                .append("<CheckCode>"
                        + UserManager.getInstance().getUser().getCodeCheck()
                        + "</CheckCode>")
                .append("<RequestData/>")
                .append("</MessageObject>");
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
        };
        NetBase net = new NetBase(BASE_URL, xmlParam, responseListner1);
        net.execute();
    }

    public void getGrantsByUserCode(final ResponseListner<AllAirCraftResponse> responseListner) {
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>").append(
                "<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetGrantsByUserCode</CMD>")
                .append("<UserCode>"
                        + UserManager.getInstance().getUser().getUserCode()
                        + "</UserCode>")
                .append("<CheckCode>"
                        + UserManager.getInstance().getUser().getCodeCheck()
                        + "</CheckCode>")
                .append("<RequestData>")
                .append("<IAppObject xsi:type=\"AppUser\">")
                .append("<UserCode>" + UserManager.getInstance().getUser().getUserCode() + "</UserCode>")
                .append("<ActiveStart>" + DateFormatUtil.formatTDate() + "</ActiveStart>")
                .append("</IAppObject>")
                .append("</RequestData>")
                .append("</MessageObject>");
        String xmlParam = sb.toString();
        NetBase net = new NetBase(BASE_URL, xmlParam, new ResponseListner<String>() {
            @Override public void onResponse(String response) {
                String xml2json = CommonUtils.xml2JSON(response);
                LogUtil.LOGD(TAG, "getAllAircraft === " + xml2json);
                if (!TextUtils.isEmpty(xml2json)) {
                    AllAirCraftResponse allAirCraft = AllAirCraftResponse.parse(xml2json);
                    responseListner.onResponse(allAirCraft);
                }
            }

            @Override public void onEmptyOrError(String message) {
                responseListner.onEmptyOrError(message);
            }
        });
        net.execute();
    }

    @Override public void validCaption(String StrUserCode, String StrAcType, String StrUserPass,
            final ResponseListner<ValidCaptionResponse> responseListner) {
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>").append(
                "<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>ValidCaption</CMD>")
                .append("<UserCode>" + UserManager.getInstance().getUser().getUserCode() + "</UserCode>")
                .append("<CheckCode>" + UserManager.getInstance().getUser().getCodeCheck() + "</CheckCode>")
                .append("<RequestData>")
                .append("<IAppObject xsi:type=\"AppCapEntiry\">")
                .append("<StrUserCode>" + StrUserCode + "</StrUserCode>")
                .append("<StrAcType>" + StrAcType + "</StrAcType>")
                .append("<StrUserPass>" + StrUserPass + "</StrUserPass>")
                .append("</IAppObject>")
                .append("</RequestData>")
                .append("</MessageObject>");
        final String xmlParam = sb.toString();
        ResponseListner<String> responseListner1 = new ResponseListner<String>() {
            @Override public void onResponse(String response) {
                String xmlStr = CommonUtils.xml2JSON(response);
                LogUtil.LOGD(TAG, "ValidCaption ===== " + xmlStr);
                ValidCaptionResponse allAcType = ValidCaptionResponse.parse(xmlStr);
                responseListner.onResponse(allAcType);
            }

            @Override public void onEmptyOrError(String message) {
                responseListner.onEmptyOrError(message);
            }
        };
        NetBase net = new NetBase(BASE_URL, xmlParam, responseListner1);
        net.execute();
    }

    @Override public void getMessageByDate(String startDate,
            final ResponseListner<MessageResponse> responseListner) {
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>").append(
                "<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetMessageByDate</CMD>")
                .append("<UserCode>"
                        + UserManager.getInstance().getUser().getUserCode()
                        + "</UserCode>")
                .append("<CheckCode>"
                        + UserManager.getInstance().getUser().getCodeCheck()
                        + "</CheckCode>")
                .append("<RequestData>")
                .append("<IAppObject xsi:type=\"MsgQDate\">")
                .append("<DtStart>" + startDate + "</DtStart>")
                .append("<DtEnd>" + DateFormatUtil.formatZDate() + "</DtEnd>")
                .append("</IAppObject>")
                .append("</RequestData>")
                .append("</MessageObject>");
        final String xmlParam = sb.toString();
        ResponseListner<String> responseListner1 = new ResponseListner<String>() {
            @Override public void onResponse(String response) {
                if (response.contains("</ResponseData>")) response = response.replace("</ResponseData>",
                        "<IAppObject xsi:type=\"AppMessage\"> </IAppObject></ResponseData>");
                String xmlStr = CommonUtils.xml2JSON(response);
                LogUtil.LOGD(TAG, "GetMessageByDate ===== " + xmlStr);
                MessageResponse allAcType = MessageResponse.parse(xmlStr);
                responseListner.onResponse(allAcType);
            }

            @Override public void onEmptyOrError(String message) {
                responseListner.onEmptyOrError(message);
            }
        };
        NetBase net = new NetBase(BASE_URL, xmlParam, responseListner1);
        net.execute();
    }

    @Override public void getAcGrants(final ResponseListner<AcGrantsResponse> responseListner) {
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>").append(
                "<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetAcGrants</CMD>")
                .append("<UserCode>"
                        + UserManager.getInstance().getUser().getUserCode()
                        + "</UserCode>")
                .append("<CheckCode>"
                        + UserManager.getInstance().getUser().getCodeCheck()
                        + "</CheckCode>")
                .append("<RequestData/>")
                .append("</MessageObject>");
        final String xmlParam = sb.toString();
        ResponseListner<String> responseListner1 = new ResponseListner<String>() {
            @Override public void onResponse(String response) {
                String xmlStr = CommonUtils.xml2JSON(response);
                LogUtil.LOGD(TAG, "GetAllSb ===== " + xmlStr);
                AcGrantsResponse allAcType = AcGrantsResponse.parse(xmlStr);
                responseListner.onResponse(allAcType);
            }

            @Override public void onEmptyOrError(String message) {
                responseListner.onEmptyOrError(message);
            }
        };
        NetBase net = new NetBase(BASE_URL, xmlParam, responseListner1);
        net.execute();
    }

    @Override public void getAllAirPort(final ResponseListner<AllAirportResponse> responseListner) {
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>").append(
                "<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetAllAirport</CMD>")
                .append("<UserCode>"
                        + UserManager.getInstance().getUser().getUserCode()
                        + "</UserCode>")
                .append("<CheckCode>"
                        + UserManager.getInstance().getUser().getCodeCheck()
                        + "</CheckCode>")
                .append("<RequestData/>")
                .append("</MessageObject>");
        final String xmlParam = sb.toString();
        ResponseListner<String> responseListner1 = new ResponseListner<String>() {
            @Override public void onResponse(String response) {
                String xmlStr = CommonUtils.xml2JSON(response);
                LogUtil.LOGD(TAG, "GetAllSb ===== " + xmlStr);
                AllAirportResponse allAcType = AllAirportResponse.parse(xmlStr);
                responseListner.onResponse(allAcType);
            }

            @Override public void onEmptyOrError(String message) {
                responseListner.onEmptyOrError(message);
            }
        };
        NetBase net = new NetBase(BASE_URL, xmlParam, responseListner1);
        net.execute();
    }

    @Override public void getAllSeat(final ResponseListner<SeatByAcRegResponse> responseListner) {
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>").append(
                "<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetAllSeat</CMD>")
                .append("<UserCode>"
                        + UserManager.getInstance().getUser().getUserCode()
                        + "</UserCode>")
                .append("<CheckCode>"
                        + UserManager.getInstance().getUser().getCodeCheck()
                        + "</CheckCode>")
                .append("<RequestData/>")
                .append("</MessageObject>");
        final String xmlParam = sb.toString();
        ResponseListner<String> responseListner1 = new ResponseListner<String>() {
            @Override public void onResponse(String response) {
                String xmlStr = CommonUtils.xml2JSON(response);
                LogUtil.LOGD(TAG, "GetAllSeat ===== " + xmlStr);
                SeatByAcRegResponse allAcType = SeatByAcRegResponse.parse(xmlStr);
                responseListner.onResponse(allAcType);
            }

            @Override public void onEmptyOrError(String message) {
                responseListner.onEmptyOrError(message);
            }
        };
        NetBase net = new NetBase(BASE_URL, xmlParam, responseListner1);
        net.execute();
    }

    @Override public void getAllFuleLimit(final ResponseListner<FuleLimitResponse> responseListner) {
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>").append(
                "<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetAllFuleLimit</CMD>")
                .append("<UserCode>"
                        + UserManager.getInstance().getUser().getUserCode()
                        + "</UserCode>")
                .append("<CheckCode>"
                        + UserManager.getInstance().getUser().getCodeCheck()
                        + "</CheckCode>")
                .append("<RequestData/>")
                .append("</MessageObject>");
        final String xmlParam = sb.toString();
        ResponseListner<String> responseListner1 = new ResponseListner<String>() {
            @Override public void onResponse(String response) {
                String xmlStr = CommonUtils.xml2JSON(response);
                LogUtil.LOGD(TAG, "GetAllFuleLimit ===== " + xmlStr);
                FuleLimitResponse allAcType = FuleLimitResponse.parse(xmlStr);
                responseListner.onResponse(allAcType);
            }

            @Override public void onEmptyOrError(String message) {
                responseListner.onEmptyOrError(message);
            }
        };
        NetBase net = new NetBase(BASE_URL, xmlParam, responseListner1);
        net.execute();
    }

    @Override public void getAllAcWeightLimit(final ResponseListner<AcWeightLimitResponse> responseListner) {
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>").append(
                "<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetAllAcWeightLimit</CMD>")
                .append("<UserCode>"
                        + UserManager.getInstance().getUser().getUserCode()
                        + "</UserCode>")
                .append("<CheckCode>"
                        + UserManager.getInstance().getUser().getCodeCheck()
                        + "</CheckCode>")
                .append("<RequestData/>")
                .append("</MessageObject>");
        final String xmlParam = sb.toString();
        ResponseListner<String> responseListner1 = new ResponseListner<String>() {
            @Override public void onResponse(String response) {
                String xmlStr = CommonUtils.xml2JSON(response);
                LogUtil.LOGD(TAG, "GetAllAcWeightLimit ===== " + xmlStr);
                AcWeightLimitResponse allAcType = AcWeightLimitResponse.parse(xmlStr);
                responseListner.onResponse(allAcType);
            }

            @Override public void onEmptyOrError(String message) {
                responseListner.onEmptyOrError(message);
            }
        };
        NetBase net = new NetBase(BASE_URL, xmlParam, responseListner1);
        net.execute();
    }

    @Override public void checkUpdate(String versionCode,
            final ResponseListner<UpdateInfoResponse> responseListner) {

        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>").append(
                "<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetVersion</CMD>")
                .append("<UserCode>" + UserManager.getInstance().getUser().getUserCode()+ "</UserCode>")
                .append("<CheckCode>" + UserManager.getInstance().getUser().getCodeCheck() + "</CheckCode>")
                //.append("<RequestData>")
                //.append("<IAppObject xsi:type=\"MsgQDate\">")
                //.append("<VersionCode>" + versionCode + "</VersionCode>")
                        //.append("</IAppObject>")
                .append("<RequestData />")
                .append("</MessageObject>");
        ;
        final String xmlParam = sb.toString();
        ResponseListner<String> responseListner1 = new ResponseListner<String>() {
            @Override public void onResponse(String response) {
                String xmlStr = CommonUtils.xml2JSON(response);
                LogUtil.LOGD(TAG, "GetVersion ===== " + xmlStr);
                UpdateInfoResponse allAcType = UpdateInfoResponse.parse(xmlStr);
                responseListner.onResponse(allAcType);
            }

            @Override public void onEmptyOrError(String message) {
                responseListner.onEmptyOrError(message);
            }
        };
        NetBase net = new NetBase(BASE_URL, xmlParam, responseListner1);
        net.execute();
    }

    @Override public void getAllAcSb(final ResponseListner<AllAcSbResponse> responseListner) {
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>").append(
                "<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>GetAllAcSb</CMD>")
                .append("<UserCode>"
                        + UserManager.getInstance().getUser().getUserCode()
                        + "</UserCode>")
                .append("<CheckCode>"
                        + UserManager.getInstance().getUser().getCodeCheck()
                        + "</CheckCode>")
                .append("<RequestData/>")
                .append("</MessageObject>");
        final String xmlParam = sb.toString();
        ResponseListner<String> responseListner1 = new ResponseListner<String>() {
            @Override public void onResponse(String response) {
                String xmlStr = CommonUtils.xml2JSON(response);
                LogUtil.LOGD(TAG, "GetAllAcSb ===== " + xmlStr);
                AllAcSbResponse allAcType = AllAcSbResponse.parse(xmlStr);
                responseListner.onResponse(allAcType);
            }

            @Override public void onEmptyOrError(String message) {
                responseListner.onEmptyOrError(message);
            }
        };
        NetBase net = new NetBase(BASE_URL, xmlParam, responseListner1);
        net.execute();
    }

    @Override public void changePassword(String userName, String oldPwd, String confirmPwd, final ResponseListner<ResetPasswordResponse> responseListner) {

        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>").append(
                "<MessageObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
                .append("<CMD>UpdateUserPass</CMD>\n")
                .append("<UserCode>"+ userName + "</UserCode>\n")
                .append("<CheckCode>"+ UserManager.getInstance().getUser().getCodeCheck()+ "</CheckCode>\n")
                .append("<RequestData>\n")
                .append("<IAppObject xsi:type = \"AppChangePass\">\n")
                .append("<UserCode>" + userName + "</UserCode>\n")
                .append("<OldPass>"+ oldPwd + "</OldPass>\n")
                .append("<NewPass>"+ confirmPwd +"</NewPass>\n")
                .append("</IAppObject>\n")
                .append("</RequestData>\n")
                .append("</MessageObject>\n");
        final String xmlParam = sb.toString();
        ResponseListner<String> responseListner1 = new ResponseListner<String>() {
            @Override public void onResponse(String response) {
                String xmlStr = CommonUtils.xml2JSON(response);
                LogUtil.LOGD(TAG, "GetAllAcSb ===== " + xmlStr);
                ResetPasswordResponse allAcType = ResetPasswordResponse.parse(xmlStr);
                responseListner.onResponse(allAcType);
            }

            @Override public void onEmptyOrError(String message) {
                responseListner.onEmptyOrError(message);
            }
        };
        NetBase net = new NetBase(BASE_URL, xmlParam, responseListner1);
        net.execute();
    }

    @Override
    public void getURLResponse(final ResponseListner<String> responseListner) {


        new AsyncTask<Void, Void, String>(){

            @Override protected String doInBackground(Void... params) {
                HttpURLConnection conn = null; //连接对象
                InputStream is = null;
                String resultData = "";
                try {
                    URL url = new URL("http://dhc001.pagekite.me/jiuAPP/login/loginController/state3.action"); //URL对象
                    conn = (HttpURLConnection) url.openConnection(); //使用URL打开一个链接
                    //conn.setDoInput(true); //允许输入流，即允许下载
                    //conn.setDoOutput(true); //允许输出流，即允许上传
                    conn.setUseCaches(false); //不使用缓冲
                    conn.setRequestMethod("GET"); //使用get请求
                    is = conn.getInputStream();   //获取输入流，此时才真正建立链接
                    byte[] bytes = new byte[is.available()];
                    is.read(bytes);

                    //resultData = new String(bytes);
                    StringBuilder sb2;
                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        sb2 = new StringBuilder();
                        int ch;
                        while ((ch = is.read()) != -1)
                        {
                            sb2.append((char) ch);
                        }

                        resultData = sb2.toString();
                    }

                    //InputStreamReader isr = new InputStreamReader(is);
                    //BufferedReader bufferReader = new BufferedReader(isr);
                    //String inputLine = "";
                    //while ((inputLine = bufferReader.readLine()) != null) {
                    //    resultData += inputLine + "\n";
                    //}

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
                return resultData;
            }

            @Override protected void onPostExecute(String s) {
                super.onPostExecute(s);
                responseListner.onResponse(s);
            }
        }.execute();
    }


}
