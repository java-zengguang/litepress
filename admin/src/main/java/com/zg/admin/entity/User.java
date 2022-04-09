package com.zg.admin.entity;

import com.zg.bean.annotation.Model;
import com.zg.bean.entity.MainModel;

/**
 * Created by Administrator on 2019/3/13 0013.
 */

@Model(tableName = "user_user")
public class User extends MainModel {
    public int id;
    public String nickname;
    public String phone;
    public boolean phone_status;
    public String email;
    public int role_id;
    public boolean email_status;
    public boolean status;
    public String skin;
    public int last_log_time;
    public int now_log_time;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", phone='" + phone + '\'' +
                ", phone_status=" + phone_status +
                ", email='" + email + '\'' +
                ", role_id=" + role_id +
                ", email_status=" + email_status +
                ", status=" + status +
                ", skin='" + skin + '\'' +
                ", last_log_time=" + last_log_time +
                ", now_log_time=" + now_log_time +
                '}';
    }
}
