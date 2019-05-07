package com.zg.database.util;

import com.zg.util.reflect.FieldSQLUtils;
import com.zg.util.reflect.FieldUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;


public class SerializeObjectUtils implements Runnable {


    //将实体类List按格式存入文件
    public static void tableToFile(File file, List<Object> list, String charset, boolean append) throws IOException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException {

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), charset));

        List<String> slist = new ArrayList<String>();
        String line = new String();
        Object model = (Object) list.get(0);//表头放在第一行
        Field fs[] = model.getClass().getFields();
        for (Field f : fs) {
            String s = f.getName() + "         ";    //表头
            line = line + s + "   ";
        }
        slist.add(line.toString());     //存放表头
        Iterator it = list.iterator();
        while (it.hasNext()) {
            line = new String();
            model = it.next();  //从第二行开始
            fs = model.getClass().getFields();
            for (Field f : fs) {
                line = line + f.get(model) + "           ";

            }
            slist.add(line);
        }

        it = slist.iterator();
        while (it.hasNext()) {
            out.write((String) it.next());
            out.newLine();
        }
        out.close();
    }

    public static List<Object> tableFromFile(File file, Object model, String charset) throws IOException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException, ParseException, SQLException, InstantiationException {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
        String line = null;

        List<Map> list = new ArrayList<Map>();
        List<Object> list_model = new ArrayList<Object>();
        line = in.readLine();
        String mem_name[] = line.split("\\s{1,}|/t");  //正则表达式
        while ((line = in.readLine()) != null) {
            Map map = new HashMap();    //
            String mem[] = line.split("\\s{1,}|/t");

            for (int i = 0; i < mem.length; i++) {

                map.put(mem_name[i], mem[i]);

            }
            list.add(map);
        }


        list_model = setMember(list, model);

        return list_model;
    }


    //映射实体类
    public static List setMember(List<Map> list, Object model) throws ClassNotFoundException, IOException, InstantiationException, ParseException, IllegalAccessException {
        return setMember(list, model.getClass());
    }

    //映射实体类
    public static List setMember(List<Map> list, Class modelClass) throws IllegalArgumentException, IllegalAccessException, IOException, ClassNotFoundException, ParseException, InstantiationException {
        Field[] model_fields = modelClass.getFields();
        Map<String, String> map = new HashMap();
        List model_list = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            Object model = modelClass.newInstance();
            map = (Map) list.get(i);

            for (Field f : model_fields) {

                if (FieldUtils.isPrimitive(f)) {
                    f.setAccessible(true);
                    if (map.get(f.getName()) != null) {
                        //   f.set(Model, map.get(f.getName()));
                        FieldSQLUtils.setFieldSql(f, model, map.get(f.getName()));
                    }

                }  else if(FieldUtils.isMainModel(f)){

                    f.set(model, setMember(list, f.getType()).get(0));

                }else if (FieldUtils.isCollection(f)) {

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


 //序列化
    public static String serializeToString(Object obj) throws Exception{
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(obj);
        String str = byteOut.toString("ISO-8859-1");//此处只能是ISO-8859-1,但是不会影响中文使用
        return str;
         }

    //反序列化
    public static Object deserializeToObject(String str) throws Exception{
        ByteArrayInputStream byteIn = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
        ObjectInputStream objIn = new ObjectInputStream(byteIn);
        Object obj =objIn.readObject();
        return obj;
    }

    public static List setMember(List<Map> list, Map<String, String> tableInfo, Class modelClass) throws IllegalArgumentException, IllegalAccessException, IOException, ClassNotFoundException, ParseException, SQLException, InstantiationException {
        Field[] model_fields = modelClass.getFields();
        Map<String, String> map = null;
        List model_list = new ArrayList();
        for (int i = 0; i < list.size(); i++) {

            Object model = modelClass.newInstance();
            map = (Map) list.get(i);

            for (Field f : model_fields) {
                f.setAccessible(true);
                if (map.get(f.getName()) != null) {
                    //   f.set(Model, map.get(f.getName()));
                    String type = tableInfo.get(f.getName());
                    if (type != null) {
                        FieldUtils.setField(f, model, map.get(f.getName()), type);
                        /*               f.set(Model,map.get(f.getName()))*/
                        ;
                    } else {
                        FieldSQLUtils.setFieldSql(f, model, map.get(f.getName()));
                    }
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


    private int explainOrder() {
        return 0;
    }


    @Override
    public void run() {
        // TODO Auto-generated method stub


    }


}
