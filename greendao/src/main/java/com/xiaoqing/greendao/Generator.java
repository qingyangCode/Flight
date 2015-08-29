package com.xiaoqing.greendao;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Schema;

public class Generator {

    public static void main(String[] args) throws Exception {
        if (args == null || args.length < 3) return;
       buildFlight(args);
    }

    private static void buildFlight(String[] args) throws Exception{
        Schema schema = new Schema(Integer.valueOf(args[0]), args[1]);
        setSchema(schema);
        new TablesMain(schema).build();
        new DaoGenerator().generateAll(schema, args[2]);
    }

    private static void setSchema(Schema schema) {
        // 方便在生成的Entity类里自定义代码
        schema.enableKeepSectionsByDefault();
    }
}
