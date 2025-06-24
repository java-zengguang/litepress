package io.github.java_zengguang.litepress.network.bean;

import java.io.Serializable;


public class ZGMPBean   implements Serializable, Cloneable {
    public String token;
    public String username;
    public String password;
    public String message;   //携带信息
    public String methodType;
    public String operationType;
    public String uuid;
    public String targetUuid;
    public long sendTime;
    public int status;
    public String direction;   //标识请求还是返回
    public String errorStr;
    public String heartBeatID;


    public ZGMPBean() {
    }

    public ZGMPBean(String direction) {
        this.direction = direction;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
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
