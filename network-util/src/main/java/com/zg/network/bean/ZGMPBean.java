package com.zg.network.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/2/25 0025.
 */
public class ZGMPBean implements Serializable,Cloneable {
    public String token;
    public String username;
    public String password;
    public String message;
    public String methodType;
    public String uuid;
    public String targetUuid;
    public long sendTime;
    public int status;
    public String direction;
    public String errorStr;
    public String heartBeatID;


    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public ZGMPBean() {
    }

    public ZGMPBean(String direction) {
        this.direction = direction;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTargetUuid() {
        return targetUuid;
    }

    public void setTargetUuid(String targetUuid) {
        this.targetUuid = targetUuid;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getErrorStr() {
        return errorStr;
    }

    public void setErrorStr(String errorStr) {
        this.errorStr = errorStr;
    }


    @Override
    public String toString() {
        return "ZGMPBean{" +
                "token='" + token + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", message='" + message + '\'' +
                ", methodType='" + methodType + '\'' +
                ", uuid='" + uuid + '\'' +
                ", targetUuid='" + targetUuid + '\'' +
                ", sendTime=" + sendTime +
                ", status=" + status +
                ", direction='" + direction + '\'' +
                ", errorStr='" + errorStr + '\'' +
                '}';
    }
}
