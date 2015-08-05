package com.uandme.greendao;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 */
public class TablesMain {

    protected Schema mSchema;

    public TablesMain(Schema schema) {
        mSchema = schema;
    }

    /**
     * add table here
     */
    public void build() {
        addUser();
        addSystemVersion();
    }

    private void addSystemVersion() {
        Entity systemVersion = mSchema.addEntity("SystemVersion");
        systemVersion.setTableName("SystemVersion");
        systemVersion.addIdProperty().autoincrement().primaryKey();
        systemVersion.addStringProperty("VserionName");
        systemVersion.addIntProperty("vserion").notNull();
    }

    private void addUser() {
        Entity user = mSchema.addEntity("User");
        user.setTableName("user");
        user.addIdProperty().autoincrement().primaryKey();
        user.addStringProperty("UserName").unique().notNull();
        user.addStringProperty("UserCode");
        user.addStringProperty("DepCode");
        user.addStringProperty("UserPassWord");
        user.addStringProperty("CodeCheck");
        user.addStringProperty("Grant_S_M");
        user.addStringProperty("ActiveStart");
        user.addIntProperty("SysVersion").notNull();
    }

}
