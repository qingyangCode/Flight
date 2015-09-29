package com.xiaoqing.flight.util;

import java.io.File;

/**
 * Created by QingYang on 15/9/29.
 */
public class FileUtil {

    public static void makeRootDirs() {
        new File(Constants.SDCARD_STORAGE_PATH).mkdirs();
    }


}
