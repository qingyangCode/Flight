package com.uandme.flight.util;

import com.uandme.flight.FlightApplication;
import com.uandme.flight.data.dao.AllAircraft;
import com.uandme.flight.data.dao.AllAircraftDao;
import com.uandme.flight.data.dao.AllSb;
import com.uandme.flight.data.dao.AllSbDao;
import com.uandme.flight.data.dao.FuleLimit;
import com.uandme.flight.data.dao.FuleLimitDao;
import com.uandme.flight.data.dao.SeatByAcReg;
import com.uandme.flight.data.dao.SeatByAcRegDao;
import com.uandme.flight.data.dao.SystemVersion;
import com.uandme.flight.data.dao.SystemVersionDao;
import com.uandme.flight.entity.AllSbResponse;
import com.uandme.flight.entity.FlightidResponse;
import com.uandme.flight.entity.FuleLimitByAcType;
import com.uandme.flight.entity.SeatByAcRegResponse;
import com.uandme.flight.network.MoccApi;
import com.uandme.flight.network.ResponseListner;
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
                LandWeightLimit, Mzfw, OpDate, SysVersion, new ResponseListner<FuleLimitByAcType>() {
                    @Override public void onResponse(FuleLimitByAcType response) {
                        if (response != null && response.ResponseObject != null && response.ResponseObject.ResponseCode == Constants.RESULT_OK) {
                            if(responseListner != null)
                                responseListner.onResponse(response);

                            FuleLimitDao fuleLimitDao =
                                    FlightApplication.getDaoSession().getFuleLimitDao();
                            List<FuleLimit> list = fuleLimitDao.queryBuilder()
                                    .where(FuleLimitDao.Properties.AcType.eq(AircraftType))
                                    .list();
                            if (list != null && list.size() > 0) {
                                fuleLimitDao.getDatabase().rawQuery("delete from "+ fuleLimitDao.getTablename()+" where AC_TYPE = '" +AircraftType+ "'", null);
                                fuleLimitDao.insertInTx(response.ResponseObject.ResponseData.IAppObject);
                            }

                            //SystemVersionDao systemVersionDao =
                            //        FlightApplication.getDaoSession().getSystemVersionDao();
                            //List<SystemVersion> fuleLimit = systemVersionDao.queryBuilder()
                            //        .where(SystemVersionDao.Properties.VserionName.eq("FuleLimit"))
                            //        .list();
                            //if (fuleLimit != null && fuleLimit.size() > 0) {
                            //    if (fuleLimit.get(0).getVserion() != 0) {
                            //        FuleLimitDao fuleLimitDao =
                            //                FlightApplication.getDaoSession().getFuleLimitDao();
                            //        fuleLimitDao.insertInTx(response.ResponseObject.ResponseData.IAppObject);
                            //        UserManager.getSystemVersion();
                            //        UserManager.getInstance().insertSystemVersion("FuleLimit", response.ResponseObject.ResponseData.IAppObject.get(0).);
                            //    }
                            //}
                        }
                    }

                    @Override public void onEmptyOrError(String message) {
                        if (responseListner != null)
                            responseListner.onEmptyOrError(message);
                    }
                });
    }




}
