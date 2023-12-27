package com.zg.network.im.client;

import com.zg.common.bean.entity.MainModel;
import com.zg.common.util.reflect.ListUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/2/28 0028.
 */
public class ClientDB {
    public static Map<String, List> map = new HashMap();


    public void insert(String key, MainModel model) {
        if (model != null) {
            List list = new ArrayList();
            list.add(model);
            insert(key, list);
        }
    }

    public void insert(String key, List list) {

        if (list != null && list.size() > 0) {
            if (map.get(key) == null) {
                map.put(key, list);
            } else {
                map.get(key).addAll(list);
            }
        }
    }

    public List select(String key, Class classes, String... terms) {
        List list = map.get(key);
        return ListUtils.findModel(list, classes, terms);
    }

    public List select(String key) {
        return map.get(key);
    }

    public Object selectOne(String key) {
        List list = map.get(key);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }
}
