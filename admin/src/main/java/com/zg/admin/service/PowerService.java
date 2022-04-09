package com.zg.admin.service;

import com.zg.admin.dao.PowerDao;
import com.zg.admin.entity.Power;
import com.zg.handler.CommitClassHandler;

import java.util.List;

public class PowerService extends CommitClassHandler {


    private PowerDao powerDao = new PowerDao();

    public List getPowerList(Power power) {

        List list = null;
        try {
            list = powerDao.getPowerList(power);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

}
