package com.xiaoqing.flight.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import com.xiaoqing.flight.FlightApplication;
import com.xiaoqing.flight.data.dao.AcGrants;
import com.xiaoqing.flight.data.dao.ActionFeed;
import com.xiaoqing.flight.data.dao.ActionFeedDao;
import com.xiaoqing.flight.data.dao.AddFlightInfo;
import com.xiaoqing.flight.data.dao.AllAcSb;
import com.xiaoqing.flight.data.dao.AllAircraft;
import com.xiaoqing.flight.data.dao.AllAircraftDao;
import com.xiaoqing.flight.data.dao.AllAirport;
import com.xiaoqing.flight.data.dao.ReadSystemNotice;
import com.xiaoqing.flight.data.dao.ReadSystemNoticeDao;
import com.xiaoqing.flight.data.dao.SystemNotice;
import com.xiaoqing.flight.data.dao.SystemNoticeDao;
import com.xiaoqing.flight.data.dao.UploadAirPerson;
import com.xiaoqing.flight.data.dao.UploadAirPersonDao;
import com.xiaoqing.flight.data.dao.User;
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
import com.xiaoqing.flight.entity.MessageResponse;
import com.xiaoqing.flight.entity.SeatByAcRegResponse;
import com.xiaoqing.flight.entity.UpdateInfoResponse;
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
                        if (response != null
                                && response.ResponseObject != null
                                && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                            ArrayList<User> allUsers =
                                    response.ResponseObject.ResponseData.IAppObject;
                            DBManager.getInstance().insertAllUsers(allUsers);
                        }
                        if (responseListner != null) responseListner.onResponse(response);
                    }

                    @Override public void onEmptyOrError(String message) {
                        LogUtil.LOGD(TAG, "get AllUser error " + message);
                        if (responseListner != null) responseListner.onEmptyOrError(message);
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
                    DBManager.getInstance()
                            .insertAllAcType(response.ResponseObject.ResponseData.IAppObject);
                }
                if (responseListner != null) responseListner.onResponse(response);
            }

            @Override public void onEmptyOrError(String message) {
                if (responseListner != null) responseListner.onEmptyOrError(message);
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

                                DBManager.getInstance().insertSeatByAcReg(response.ResponseObject.ResponseData.IAppObject);
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
                    DBManager.getInstance()
                            .insertAllSb(response.ResponseObject.ResponseData.IAppObject);
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
                        && response.ResponseObject != null) {
                    if (response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                        if (responseResponseListner != null && response.ResponseObject != null)
                            responseResponseListner.onResponse(response.ResponseObject.ResponseErr);
                    }else {
                        if (responseResponseListner != null && response.ResponseObject != null)
                            responseResponseListner.onResponse(response.ResponseObject.ResponseErr);
                    }
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
            String LandWeightLimit, String Mzfw, String OpDate, String SysVersion, final ResponseListner<FuleLimitResponse> responseListner) {

        FlightApplication.getMoccApi().getFuleLimitByAcType(AircraftType, PortLimit, TofWeightLimit,
                LandWeightLimit, Mzfw, OpDate, SysVersion,
                new ResponseListner<FuleLimitResponse>() {
                    @Override public void onResponse(FuleLimitResponse response) {
                        if (response != null
                                && response.ResponseObject != null
                                && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                            DBManager.getInstance()
                                    .insertFuleLimit(
                                            response.ResponseObject.ResponseData.IAppObject);
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
    public void addFlightInfo(final AddFlightInfo addFlightInfo, final ResponseListner<AddFlightInfoResponse> responseListner) {
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
                        if (responseListner != null) {
                            responseListner.onResponse(response);
                        }
                        //if (response != null
                        //        && response.ResponseObject != null
                        //        && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                        //    ActionFeed actionFeed = new ActionFeed();
                        //    actionFeed.setFeed_id(
                        //            UserManager.getInstance().getAddFlightInfo().getFlightId());
                        //    actionFeed.setUserCode(
                        //            UserManager.getInstance().getUser().getUserCode());
                        //    actionFeed.setFeed_type(FeedType.toInt(FeedType.ADD_PLAYINFO));
                        //    DBManager.getInstance().deleteActionFeed(actionFeed);
                        //    DBManager.getInstance().deleteFlightInfo(addFlightInfo.getFlightId());
                        //}


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
                if (responseResponseListner != null) responseResponseListner.onEmptyOrError(message);
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
                        if (response != null
                                && response.ResponseObject != null
                                && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                            DBManager.getInstance()
                                    .insertAllAircrart(
                                            response.ResponseObject.ResponseData.IAppObject);
                        }
                    }

                    @Override public void onEmptyOrError(String message) {
                    }
                });
    }


    public void getAllAirport(final ResponseListner<AllAirportResponse> responseListner) {
        getMoccApi().getAllAirPort(new ResponseListner<AllAirportResponse>() {

            @Override public void onResponse(AllAirportResponse response) {
                if (response != null
                        && response.ResponseObject != null
                        && response.ResponseObject.ResponseData != null
                        && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                    ArrayList<AllAirport> iAppObject =
                            response.ResponseObject.ResponseData.IAppObject;
                    DBManager.getInstance().insertAllAirPort(iAppObject);
                }
                if (responseListner != null) responseListner.onResponse(response);
            }

            @Override public void onEmptyOrError(String message) {
                if (responseListner != null) responseListner.onEmptyOrError(message);
            }
        });
    }

    //上传飞机上成员信息
    public void uploadAirPersonInfo() {
        if (UserManager.getInstance().getUser() == null)
            return;
        final UploadAirPersonDao uploadAirPersonDao =
                FlightApplication.getDaoSession().getUploadAirPersonDao();
        ActionFeedDao actionFeedDao = FlightApplication.getDaoSession().getActionFeedDao();
        List<ActionFeed> list = actionFeedDao.queryBuilder()
                .where(ActionFeedDao.Properties.Feed_type.eq(FeedType.toInt(FeedType.ADD_FLIGHTPERSON)))
                .list();
        //所有待同步数据
        if (list != null && list.size() > 0) {
            for (ActionFeed actionFeed : list) {
                List<UploadAirPerson> uploadList = uploadAirPersonDao.queryBuilder()
                        .where(UploadAirPersonDao.Properties.Id.eq(actionFeed.getFeed_id()))
                        .list();
                if (uploadList == null || uploadList.size() == 0) {
                    return;
                }
                final UploadAirPerson uploadAirPerson = uploadList.get(0);
                getMoccApi().addFlightCd(uploadAirPerson.getAircraftReg(), uploadAirPerson.getSeatId() + "",
                        UserManager.getInstance().getAddFlightInfo().getFlightId(),
                        uploadAirPerson.getSeatCode(), uploadAirPerson.getSeatType(),
                        uploadAirPerson.getAcTypeSeatLimit() + "", uploadAirPerson.getAcTypeLb()+"",
                        uploadAirPerson.getAcRegCargWeight()+"", uploadAirPerson.getAcTypeLb()+"",
                        uploadAirPerson.getSeatLastLimit() + "",
                        uploadAirPerson.getPassagerName(), uploadAirPerson.getRealWeight()+"",
                        UserManager.getInstance().getUser().getUserCode(),
                        DateFormatUtil.formatZDate(),
                        new ResponseListner<GrantsByUserCodeResponse>() {
                            @Override
                            public void onResponse(GrantsByUserCodeResponse response) {
                                if (response != null
                                        && response.ResponseObject != null
                                        && response.ResponseObject.ResponseCode
                                        == Constants.RESULT_OK) {
                                    deleteFlightPersonFeed();
                                    uploadAirPersonDao.deleteByKey(uploadAirPerson.getId());
                                }
                            }

                            private void deleteFlightPersonFeed() {
                                ActionFeed actionFeed = new ActionFeed();
                                actionFeed.setUserCode(
                                        UserManager.getInstance().getUser().getUserCode());
                                actionFeed.setFeed_id(String.valueOf(uploadAirPerson.getId()));
                                actionFeed.setFeed_type(FeedType.toInt(FeedType.ADD_FLIGHTPERSON));
                                DBManager.getInstance().deleteActionFeed(actionFeed);
                            }

                            @Override public void onEmptyOrError(String message) {
                                LogUtil.LOGD(TAG, "上传飞机成员错误： " + message);
                            }
                        });
            }
        }
    }

    /**
     * 获取所有机型座椅
     * @param responseListner
     */
    public void getAllSeat(final ResponseListner<SeatByAcRegResponse> responseListner) {
        getMoccApi().getAllSeat(new ResponseListner<SeatByAcRegResponse>() {
            @Override public void onResponse(SeatByAcRegResponse response) {
                if (responseListner != null)
                    responseListner.onResponse(response);
                if (response != null && response.ResponseObject != null && response.ResponseObject.ResponseData != null
                        && response.ResponseObject.ResponseData.IAppObject != null) {
                        DBManager.getInstance().insertSeatByAcReg(response.ResponseObject.ResponseData.IAppObject);
                }
            }

            @Override public void onEmptyOrError(String message) {
                if(responseListner != null) responseListner.onEmptyOrError(message);
            }
        });
    }

    /**
     * 获取所有机型燃油力矩表
     * @param responseListner
     */
    public void getAllFuleLimit(final ResponseListner<FuleLimitResponse> responseListner) {
        getMoccApi().getAllFuleLimit(new ResponseListner<FuleLimitResponse>() {
            @Override public void onResponse(FuleLimitResponse response) {
                if (responseListner != null) responseListner.onResponse(response);
                if (response != null
                        && response.ResponseObject != null
                        && response.ResponseObject.ResponseData != null
                        && response.ResponseObject.ResponseData.IAppObject != null) {
                    DBManager.getInstance()
                            .insertFuleLimit(response.ResponseObject.ResponseData.IAppObject);
                }
            }

            @Override public void onEmptyOrError(String message) {
                if (responseListner != null) responseListner.onEmptyOrError(message);
            }
        });
    }

    public void getAllAcWeightLimit(final ResponseListner<AcWeightLimitResponse> responseListner) {
        getMoccApi().getAllAcWeightLimit(new ResponseListner<AcWeightLimitResponse>() {
            @Override public void onResponse(AcWeightLimitResponse response) {
                if (response != null
                        && response.ResponseObject != null
                        && response.ResponseObject.ResponseData != null
                        && response.ResponseObject.ResponseData.IAppObject != null) {
                    DBManager.getInstance()
                            .insertAllAcWeightLimit(
                                    response.ResponseObject.ResponseData.IAppObject);
                }
            }

            @Override public void onEmptyOrError(String message) {
                if (responseListner != null) responseListner.onEmptyOrError(message);
            }
        });
    }

    public void checkUpdate(final Context context) {
        getMoccApi().checkUpdate(CommonUtils.getVersionName(context),
                new ResponseListner<UpdateInfoResponse>() {

                    @Override public void onResponse(UpdateInfoResponse response) {
                        if (response == null
                                || response.ResponseObject == null
                                || response.ResponseObject.ResponseCode != Constants.RESULT_OK
                                || response.ResponseObject.ResponseData == null) {
                            return;
                        }
                        final UpdateInfoResponse.UpdateInfo t =
                                response.ResponseObject.ResponseData.IAppObject;
                        if (null != t) {
                            switch (String.valueOf(CommonUtils.getVersionCode(context))
                                    .compareTo(t.getVersion())) {
                                case 0: // 版本一致,不更新
                                case 1: // 当前版本更大,不更新
                                    //    Toast.makeText(activity, "当前版本为最新版本", Toast.LENGTH_SHORT).show();
                                    break;
                                default: // 低版本，更新
                                    // 加入红点提示
                                    new AlertDialog.Builder((Activity) context).setTitle("版本升级")
                                            .setMessage(t.getContent())
                                            .setNegativeButton("立即更新",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog,
                                                                int which) {
                                                            CommonUtils.startWebView(context,
                                                                    t.getUrl());
                                                        }
                                                    })
                                            .setPositiveButton("忽略本次",
                                                    new DialogInterface.OnClickListener() {

                                                        @Override
                                                        public void onClick(DialogInterface dialog,
                                                                int which) {

                                                        }
                                                    })
                                            .create()
                                            .show();
                                    break;
                            }
                        }
                    }

                    @Override public void onEmptyOrError(String message) {
                    }
                });
    }


    public void getAllAcSb(ResponseListner<AllAcSb> responseListner) {
        getMoccApi().getAllAcSb(new ResponseListner<AllAcSbResponse>() {
            @Override public void onResponse(AllAcSbResponse response) {
                if (response != null && response.ResponseObject != null && response.ResponseObject.ResponseCode == Constants.RESULT_OK && response.ResponseObject.ResponseData != null) {
                    DBManager.getInstance().insertAllAcSb(response.ResponseObject.ResponseData.IAppObject);
                }
            }

            @Override public void onEmptyOrError(String message) {

            }
        });
    }


}

