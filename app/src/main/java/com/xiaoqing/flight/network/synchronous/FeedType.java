package com.xiaoqing.flight.network.synchronous;

/**
 * Created by QingYang on 15/8/30.
 */
public enum FeedType {
    ADD_PLAYINFO;

    public static int toInt(FeedType feedType){
        switch (feedType){
            case ADD_PLAYINFO:
                return 1;

            default:
                return -1;
        }
    }
}
