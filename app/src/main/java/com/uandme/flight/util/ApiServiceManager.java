package com.uandme.flight.util;

import com.uandme.flight.FlightApplication;
import com.uandme.flight.data.dao.AllAircraft;
import com.uandme.flight.data.dao.AllAircraftDao;
import com.uandme.flight.data.dao.AllSb;
import com.uandme.flight.data.dao.AllSbDao;
import com.uandme.flight.data.dao.SeatByAcReg;
import com.uandme.flight.data.dao.SeatByAcRegDao;
import com.uandme.flight.entity.AllSbResponse;
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




}
