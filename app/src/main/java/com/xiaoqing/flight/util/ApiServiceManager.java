package com.xiaoqing.flight.util;

import android.content.Context;
import android.content.Intent;
import com.xiaoqing.flight.FlightApplication;
import com.xiaoqing.flight.data.dao.AcGrants;
import com.xiaoqing.flight.data.dao.ActionFeed;
import com.xiaoqing.flight.data.dao.AddFlightInfo;
import com.xiaoqing.flight.data.dao.AllAircraft;
import com.xiaoqing.flight.data.dao.AllAircraftDao;
import com.xiaoqing.flight.data.dao.AllAirport;
import com.xiaoqing.flight.data.dao.AllSb;
import com.xiaoqing.flight.data.dao.AllSbDao;
import com.xiaoqing.flight.data.dao.FuleLimit;
import com.xiaoqing.flight.data.dao.FuleLimitDao;
import com.xiaoqing.flight.data.dao.ReadSystemNotice;
import com.xiaoqing.flight.data.dao.ReadSystemNoticeDao;
import com.xiaoqing.flight.data.dao.SeatByAcReg;
import com.xiaoqing.flight.data.dao.SeatByAcRegDao;
import com.xiaoqing.flight.data.dao.SystemNotice;
import com.xiaoqing.flight.data.dao.SystemNoticeDao;
import com.xiaoqing.flight.data.dao.User;
import com.xiaoqing.flight.entity.AcGrantsResponse;
import com.xiaoqing.flight.entity.AddFlightInfoResponse;
import com.xiaoqing.flight.entity.AllAcTypeResponse;
import com.xiaoqing.flight.entity.AllAirCraftResponse;
import com.xiaoqing.flight.entity.AllAirportResponse;
import com.xiaoqing.flight.entity.AllSbResponse;
import com.xiaoqing.flight.entity.AllUsersResponse;
import com.xiaoqing.flight.entity.FlightidResponse;
import com.xiaoqing.flight.entity.FuleLimitByAcType;
import com.xiaoqing.flight.entity.MessageResponse;
import com.xiaoqing.flight.entity.SeatByAcRegResponse;
import com.xiaoqing.flight.network.MoccApi;
import com.xiaoqing.flight.network.ResponseListner;
import com.xiaoqing.flight.network.synchronous.FeedType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by QingYang on 15/8/15.
 */
public class ApiServiceManager {

    private String TAG = ApiServiceManager.class.getSimpleName();

    private ApiServiceManager() {

    }
    private static ApiServiceManager sApiServiceManager;

    public static ApiServiceManager getInstance() {
        if (sApiServiceManager == null)
            sApiServiceManager = new ApiServiceManager();
        return sApiServiceManager;
    }


    private MoccApi getMoccApi() {
       return  FlightApplication.getMoccApi();
    }

    /**
     * 所有用户列表
     * @param responseListner
     */
    public void getAllUser(final ResponseListner<AllUsersResponse> responseListner) {
        FlightApplication.getMoccApi().getAllUser(UserManager.getInstance().getUser().getUserName(),
                new ResponseListner<AllUsersResponse>() {

                    @Override public void onResponse(AllUsersResponse response) {
                        if (response != null && response.ResponseObject != null
                                && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                            ArrayList<User> allUsers = response.ResponseObject.ResponseData.IAppObject;
                            DBManager.getInstance().insertAllUsers(allUsers);
                        }
                        if (responseListner != null)
                            responseListner.onResponse(response);
                    }

                    @Override public void onEmptyOrError(String message) {
                        LogUtil.LOGD(TAG, "get AllUser error " + message);
                        if (responseListner != null)
                            responseListner.onEmptyOrError(message);
                    }
                });
    }

    /**
     *获取所有机型
     * @param responseListner
     */
    public void getAllAcType(final ResponseListner<AllAcTypeResponse> responseListner) {
        FlightApplication.getMoccApi().getAllAcType(new ResponseListner<AllAcTypeResponse>() {
            @Override public void onResponse(AllAcTypeResponse response) {
                if (response != null
                        && response.ResponseObject != null
                        && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                    DBManager.getInstance().insertAllAcType(response.ResponseObject.ResponseData.IAppObject);
                }
                if (responseListner != null)
                    responseListner.onResponse(response);
            }

            @Override public void onEmptyOrError(String message) {
                if (responseListner != null)
                    responseListner.onEmptyOrError(message);
                LogUtil.LOGD(TAG, "response Message ==== " + message);
            }
        });
    }

    /**
     * 座椅 ：获取座椅信息
     * @param aircraftReg
     * @param responseResponseListner
     */
    public void getSeatInfo(final String aircraftReg, final ResponseListner<SeatByAcRegResponse> responseResponseListner) {
        AllAircraftDao allAircraftDao = FlightApplication.getDaoSession().getAllAircraftDao();
        List<AllAircraft> list = allAircraftDao.queryBuilder()
                .where(AllAircraftDao.Properties.AircraftReg.eq(aircraftReg))
                .list();
        if (list != null && list.size() > 0) {
            AllAircraft allAircraft = list.get(0);
            getMoccApi().getSeatByAcReg(aircraftReg, String.valueOf(allAircraft.getBw()), String.valueOf(allAircraft.getLj()), allAircraft.getOpDate(), String.valueOf(allAircraft.getSysVersion()),
                    new ResponseListner<SeatByAcRegResponse>() {

                        @Override public void onResponse(SeatByAcRegResponse response) {
                            if (response != null
                                    && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {

                                DBManager.getInstance().insertSeatByAcReg(aircraftReg, response.ResponseObject.ResponseData.IAppObject);
                                if (responseResponseListner != null)
                                    responseResponseListner.onResponse(response);
                            }
                        }

                        @Override public void onEmptyOrError(String message) {
                            if (responseResponseListner != null)
                                responseResponseListner.onEmptyOrError(message);
                        }
                    });
        }
    }

    /**
     * 差分站： 获取飞机差分站设备信息
     */
    public void getAllSb(final ResponseListner<AllSbResponse> responseResponseListner) {
        FlightApplication.getMoccApi().getAllSb(new ResponseListner<AllSbResponse>() {
            @Override public void onResponse(AllSbResponse response) {
                if (response != null
                        && response.ResponseObject != null
                        && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                    DBManager.getInstance().insertAllSb(response.ResponseObject.ResponseData.IAppObject);
                }
                if (responseResponseListner != null) {
                    responseResponseListner.onResponse(response);
                }
            }

            @Override public void onEmptyOrError(String message) {
                if (responseResponseListner != null) {
                    responseResponseListner.onEmptyOrError(message);
                }
            }
        });
    }

    /**
     * 航班ID： 获取添加飞机的ID
     * @param responseResponseListner
     */
    public void getFilghtId(final ResponseListner<String> responseResponseListner) {
        FlightApplication.getMoccApi().getflightid(new ResponseListner<FlightidResponse>() {
            @Override public void onResponse(FlightidResponse response) {
                if (response != null
                        && response.ResponseObject != null
                        && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                    if (responseResponseListner != null && response.ResponseObject != null)
                        responseResponseListner.onResponse(response.ResponseObject.ResponseErr);
                }
            }

            @Override public void onEmptyOrError(String message) {
                if (responseResponseListner != null)
                    responseResponseListner.onEmptyOrError(message);
            }
        });
    }

    /**
     * 重心极限值
     * @param AircraftType
     * @param PortLimit
     * @param TofWeightLimit
     * @param LandWeightLimit
     * @param Mzfw
     * @param OpDate
     * @param SysVersion
     * @param responseListner
     */
    public void getFuleLimitByAcType(final String AircraftType, String PortLimit, String TofWeightLimit,
            String LandWeightLimit, String Mzfw, String OpDate, String SysVersion, final ResponseListner<FuleLimitByAcType> responseListner) {

        FlightApplication.getMoccApi().getFuleLimitByAcType(AircraftType, PortLimit, TofWeightLimit,
                LandWeightLimit, Mzfw, OpDate, SysVersion,
                new ResponseListner<FuleLimitByAcType>() {
                    @Override public void onResponse(FuleLimitByAcType response) {
                        if (response != null
                                && response.ResponseObject != null
                                && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                            DBManager.getInstance().insertFuleLimit(AircraftType, response.ResponseObject.ResponseData.IAppObject);
                        }
                        if (responseListner != null) responseListner.onResponse(response);
                    }

                    @Override public void onEmptyOrError(String message) {
                        if (responseListner != null) responseListner.onEmptyOrError(message);
                    }
                });
    }

    /**
     * 获取系统消息
     * @param startTime 系统消息开始时间
     * @param responseListner
     */
    public void getSystemMessage(String startTime, final ResponseListner<MessageResponse> responseListner) {
        FlightApplication.getMoccApi().getMessageByDate(startTime,
                new ResponseListner<MessageResponse>() {
                    @Override public void onResponse(MessageResponse response) {
                        if (response != null
                                && response.ResponseObject != null
                                && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                            SystemNoticeDao systemNoticeDao =
                                    FlightApplication.getDaoSession().getSystemNoticeDao();
                            List<SystemNotice> list = systemNoticeDao.queryBuilder().list();

                            ReadSystemNoticeDao readSystemNoticeDao =
                                    FlightApplication.getDaoSession().getReadSystemNoticeDao();
                            List<ReadSystemNotice> readNoticeList =
                                    readSystemNoticeDao.queryBuilder()
                                            .where(ReadSystemNoticeDao.Properties.UserCode.eq(
                                                    UserManager.getInstance()
                                                            .getUser()
                                                            .getUserCode()))
                                            .list();

                            if (list != null && list.size() > 0) {
                                systemNoticeDao.deleteInTx(list);
                            }
                            List<SystemNotice> iAppObject =
                                    response.ResponseObject.ResponseData.IAppObject;
                            if (iAppObject != null && iAppObject.size() > 0) {
                                iAppObject.remove(iAppObject.size() - 1);
                                systemNoticeDao.insertInTx(iAppObject);

                                if (iAppObject.size() != readNoticeList.size()) {
                                    sendHasNewNotice(true);
                                } else {
                                    sendHasNewNotice(false);
                                }
                            }

                            if (responseListner != null) {
                                responseListner.onResponse(response);
                            }
                        }
                    }

                    private void sendHasNewNotice(boolean hasNewMessage) {
                        Context context = FlightApplication.getContext();
                        context.sendBroadcast(new Intent(Constants.BROADCAST_SYSTEMNOTICE).putExtra(
                                Constants.PARAM_HASNEWNOTICE, hasNewMessage));
                    }

                    @Override public void onEmptyOrError(String message) {
                        if (responseListner != null) responseListner.onEmptyOrError(message);
                    }
                });
    }

    /**
     * 添加航班信息
     * @param addFlightInfo
     * @param responseListner
     */
    public void addFlightInfo(AddFlightInfo addFlightInfo, final ResponseListner<AddFlightInfoResponse> responseListner) {
        getMoccApi().addFlightInfo(addFlightInfo.getFlightId(), DateFormatUtil.formatZDate(),
                addFlightInfo.getAircraftReg(), addFlightInfo.getAircraftType(),
                addFlightInfo.getFlightNo(), addFlightInfo.getDep4Code(),
                addFlightInfo.getDepAirportName(), addFlightInfo.getArr4Code(),
                addFlightInfo.getArrAirportName(), addFlightInfo.getMaxFule(),
                addFlightInfo.getRealFule(), addFlightInfo.getSlieFule(),
                addFlightInfo.getRouteFule(), addFlightInfo.getTofWeight(),
                addFlightInfo.getLandWeight(), addFlightInfo.getNoFuleWeight(),
                addFlightInfo.getAirportLimitWeight(), addFlightInfo.getBalancePic(),
                addFlightInfo.getBalancePicName(),
                UserManager.getInstance().getUser().getUserCode(), DateFormatUtil.formatZDate(),
                new ResponseListner<AddFlightInfoResponse>() {

                    @Override public void onResponse(AddFlightInfoResponse response) {
                        if (response != null
                                && response.ResponseObject != null
                                && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                            ActionFeed actionFeed = new ActionFeed();
                            actionFeed.setFeed_id(
                                    UserManager.getInstance().getAddFlightInfo().getFlightId());
                            actionFeed.setUserCode(
                                    UserManager.getInstance().getUser().getUserCode());
                            actionFeed.setFeed_type(FeedType.toInt(FeedType.ADD_PLAYINFO));
                            DBManager.getInstance().deleteActionFeed(actionFeed);
                            DBManager.getInstance().deleteFlightInfo();
                        }

                        if (responseListner != null) {
                            responseListner.onResponse(response);
                        }
                    }

                    @Override public void onEmptyOrError(String message) {
                        if (responseListner != null) responseListner.onEmptyOrError(message);
                    }
                });
    }

    /**
     * 获取用户所有授权飞机信息
     * @param responseResponseListner
     */
    public void getAcGrants(final ResponseListner<AcGrantsResponse> responseResponseListner) {
        getMoccApi().getAcGrants(new ResponseListner<AcGrantsResponse>() {
            @Override public void onResponse(AcGrantsResponse response) {
                if (response != null
                        && response.ResponseObject != null
                        && response.ResponseObject.ResponseData != null
                        && response.ResponseObject.ResponseData.IAppObject != null) {
                    ArrayList<AcGrants> AcGrantLists =
                            response.ResponseObject.ResponseData.IAppObject;
                    DBManager.getInstance().insertAcGrants(AcGrantLists);
                }
                if (responseResponseListner != null) responseResponseListner.onResponse(response);
            }

            @Override public void onEmptyOrError(String message) {
                if (responseResponseListner != null)
                    responseResponseListner.onEmptyOrError(message);
            }
        });
    }

    /**
     * 所有机型列表
     */
    public void getAllAircraft() {
        getMoccApi().getAllAircraft(UserManager.getInstance().getUser().getUserCode(),
                new ResponseListner<AllAirCraftResponse>() {

                    @Override public void onResponse(AllAirCraftResponse response) {
                        //dialog.dismiss();
                        if (response != null && response.ResponseObject != null && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                            DBManager.getInstance().insertAllAircrart(response.ResponseObject.ResponseData.IAppObject);
                        }
                    }

                    @Override public void onEmptyOrError(String message) {
                    }
                });
    }


    public void getAllAirport(final ResponseListner<AllAirportResponse> responseListner) {
        getMoccApi().getAllAirPort(new ResponseListner<AllAirportResponse>() {

            @Override public void onResponse(AllAirportResponse response) {
                if (response != null && response.ResponseObject != null && response.ResponseObject.ResponseData != null && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                    ArrayList<AllAirport> iAppObject =
                            response.ResponseObject.ResponseData.IAppObject;
                    DBManager.getInstance().insertAllAirPort(iAppObject);
                }
                if (responseListner != null)
                    responseListner.onResponse(response);
            }

            @Override public void onEmptyOrError(String message) {
                if (responseListner != null)
                    responseListner.onEmptyOrError(message);
            }
        });
    }

}
