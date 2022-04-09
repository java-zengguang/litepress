package com.zg.database.util;

import com.zg.util.reflect.EntityUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SerializeObjectUtils implements Runnable {


    private static boolean isCollection(Field field) {
        Class type = field.getType();
        if ("List".equals(type.getSimpleName()) || "Set".equals(type.getSimpleName())) {
            return true;
        }
        return false;
    }

    private static boolean isMainModel(Field field) {
        Class type = field.getType().getSuperclass();
        if (type != null && "MainModel".equals(type.getSimpleName())) {
            return true;
        }
        return false;
    }


    //映射实体类
    public static List setMember(List<Map> list, Class modelClass) throws IllegalArgumentException, IllegalAccessException, IOException, ClassNotFoundException, ParseException, InstantiationException {

        Field[] model_fields = modelClass.getFields();

        List model_list = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            Object model = modelClass.newInstance();
            Map<String, Object> map = (Map) list.get(i);

            for (Field f : model_fields) {

                if (EntityUtils.isPrimitive(f)) {
                    f.setAccessible(true);
                    if (map.get(f.getName()) != null) {
                        //   f.set(Model, map.get(f.getName()));
                        EntityUtils.setFieldObject(f, model, map.get(f.getName()));
                    }

                } else if (isMainModel(f)) {

                    f.set(model, setMember(list, f.getType()).get(0));

                } else if (isCollection(f)) {

                }

                if (EntityUtils.isPrimitive(f)) {
                    f.setAccessible(true);
                    if (map.get(f.getName().toUpperCase()) != null) {
                        // f.set(model, map.get(f.getName()));
                        EntityUtils.setFieldObject(f, model, map.get(f.getName().toUpperCase()));
                    }

                } else if (isMainModel(f)) {

                    f.set(model, setMember(list, f.getType()).get(0));

                } else if (isCollection(f)) {

                }
            }
            //序列化，实现深度克隆
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(model);
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            Object model_copy = (Object) in.readObject();
            model_list.add(model_copy);
        }
        return model_list;
    }


    @Override
    public void run() {
        // TODO Auto-generated method stub

    }


}
