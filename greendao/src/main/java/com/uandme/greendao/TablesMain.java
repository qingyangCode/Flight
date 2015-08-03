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
    }


    private void addUser() {
        Entity user = mSchema.addEntity("User");
        user.setTableName("user");
        user.addLongProperty("id").primaryKey().notNull();
        user.addStringProperty("uuid");
        user.addStringProperty("email");
        user.addStringProperty("role");
        user.addStringProperty("created_at");
        user.addStringProperty("updated_at");
        user.addStringProperty("first_name");
        user.addStringProperty("last_name");
        user.addStringProperty("handicap_style");
        user.addStringProperty("handicap_style_1");
        user.addStringProperty("handicap_style_2");
        user.addDoubleProperty("height").notNull();
        user.addDoubleProperty("weight").notNull();
        user.addIntProperty("birth_year").notNull();
        user.addIntProperty("gender").notNull();
        user.addIntProperty("right_handed").notNull();
        user.addStringProperty("grip_posture");
        user.addStringProperty("grip_position");
        user.addStringProperty("is_phone_timer");
        user.addStringProperty("video_record");
        user.addStringProperty("auto_save");
        user.addStringProperty("auto_comment");
        user.addStringProperty("sound_effect");
        user.addStringProperty("text_hint");
        user.addStringProperty("power_save");
        user.addStringProperty("unit");
        user.addStringProperty("impact_detect");
        user.addStringProperty("goals");
        user.addStringProperty("user_image_file_name");
        user.addStringProperty("user_image_content_type");
        user.addStringProperty("user_image_file_size");
        user.addStringProperty("user_image_fingerprint");
        user.addStringProperty("versions");
        user.addStringProperty("language");
        user.addStringProperty("timezone");
        user.addStringProperty("test_account");
        user.addStringProperty("user_role");
        user.addStringProperty("flags");
        user.addStringProperty("authentication_token");
        user.addStringProperty("profile");
        user.addStringProperty("avatar_url");
        user.addBooleanProperty("facebook_connected").notNull();
        user.addBooleanProperty("wechat_connected").notNull();
        user.addIntProperty("position").notNull();//1 \ 2\ 3\ 4
    }

}
