package com.xiaoqing.flight.util;

import android.content.Context;
import android.content.Intent;
import com.xiaoqing.flight.FlightApplication;
import com.xiaoqing.flight.data.dao.ActionFeed;
import com.xiaoqing.flight.data.dao.AddFlightInfo;
import com.xiaoqing.flight.data.dao.AddFlightInfoDao;
import com.xiaoqing.flight.data.dao.AllAircraft;
import com.xiaoqing.flight.data.dao.AllAircraftDao;
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
import com.xiaoqing.flight.entity.AddFlightInfoResponse;
import com.xiaoqing.flight.entity.AllSbResponse;
import com.xiaoqing.flight.entity.FlightidResponse;
import com.xiaoqing.flight.entity.FuleLimitByAcType;
import com.xiaoqing.flight.entity.MessageResponse;
import com.xiaoqing.flight.entity.SeatByAcRegResponse;
import com.xiaoqing.flight.network.MoccApi;
import com.xiaoqing.flight.network.ResponseListner;
import com.xiaoqing.flight.network.synchronous.FeedType;
import java.util.List;

/**
 * Created by QingYang on 15/8/15.
 */
public class ApiServiceManager {

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
     * 获取座椅信息
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
                                try {
                                    SeatByAcRegDao seatByAcRegDao =
                                            FlightApplication.getDaoSession().getSeatByAcRegDao();
                                    List<SeatByAcReg> list1 = seatByAcRegDao.queryBuilder()
                                            .where(SeatByAcRegDao.Properties.AcReg.eq(aircraftReg))
                                            .list();
                                    if (list1 == null || list1.size() == 0) {
                                        seatByAcRegDao.insertOrReplaceInTx(response.ResponseObject.ResponseData.IAppObject, true);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
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
     * 获取飞机差分站设备信息
     */
    public void getAllSb(final ResponseListner<AllSbResponse> responseResponseListner) {
        FlightApplication.getMoccApi().getAllSb(new ResponseListner<AllSbResponse>() {
            @Override public void onResponse(AllSbResponse response) {
                if (response != null && response.ResponseObject != null && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                    AllSbDao allSbDao = FlightApplication.getDaoSession().getAllSbDao();
                    List<AllSb> list = allSbDao.queryBuilder().list();
                    if (list == null || list.size() == 0)
                        allSbDao.insertOrReplaceInTx(response.ResponseObject.ResponseData.IAppObject);
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
     * 获取添加飞机的ID
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


    public void getFuleLimitByAcType(final String AircraftType, String PortLimit, String TofWeightLimit,
            String LandWeightLimit, String Mzfw, String OpDate, String SysVersion, final ResponseListner<FuleLimitByAcType> responseListner) {

        FlightApplication.getMoccApi().getFuleLimitByAcType(AircraftType, PortLimit, TofWeightLimit,
                LandWeightLimit, Mzfw, OpDate, SysVersion,
                new ResponseListner<FuleLimitByAcType>() {
                    @Override public void onResponse(FuleLimitByAcType response) {
                        if (response != null
                                && response.ResponseObject != null
                                && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                            if (responseListner != null) responseListner.onResponse(response);

                            FuleLimitDao fuleLimitDao =
                                    FlightApplication.getDaoSession().getFuleLimitDao();
                            List<FuleLimit> list = fuleLimitDao.queryBuilder()
                                    .where(FuleLimitDao.Properties.AcType.eq(AircraftType))
                                    .list();
                            if (list != null && list.size() > 0) {
                                fuleLimitDao.getDatabase()
                                        .rawQuery("delete from "
                                                + fuleLimitDao.getTablename()
                                                + " where AC_TYPE = '"
                                                + AircraftType
                                                + "'", null);
                                fuleLimitDao.insertInTx(
                                        response.ResponseObject.ResponseData.IAppObject);
                            }
                        }
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

    public void addFlightInfo(AddFlightInfo addFlightInfo, final ResponseListner<AddFlightInfoResponse> responseListner) {
        getMoccApi().addFlightInfo(addFlightInfo.getFlightId(), DateFormatUtil.formatZDate(),
                addFlightInfo.getAircraftReg(), addFlightInfo.getAircraftType(), addFlightInfo.getFlightNo(), addFlightInfo.getDep4Code(),
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
                        if (response != null && response.ResponseObject != null && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                            ActionFeed actionFeed = new ActionFeed();
                            actionFeed.setFeed_id(UserManager.getInstance().getAddFlightInfo().getFlightId());
                            actionFeed.setUserCode(
                                    UserManager.getInstance().getUser().getUserCode());
                            actionFeed.setFeed_type(FeedType.toInt(FeedType.ADD_PLAYINFO));
                            UserManager.getInstance().deleteActionFeed(actionFeed);
                            UserManager.getInstance().deleteFlightInfo();
                        }

                        if (responseListner != null) {
                            responseListner.onResponse(response);
                        }
                    }

                    @Override public void onEmptyOrError(String message) {
                        if (responseListner != null)
                            responseListner.onEmptyOrError(message);
                    }
                });
    }
}
