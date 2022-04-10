package com.zg.util.reflect;

import com.zg.bean.entity.MainModel;
import com.zg.database.util.DataBaseUtil;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.util.*;


public class DynamicClass {


    private static String produceEntityJavaCode(List<String> referenceList, String calssName, Map<String, String> natureMap, List<String> interfaceList, String parentClass) throws Exception {

        StringBuffer sb = new StringBuffer();
        if (calssName == null || natureMap.size() == 0) {
            throw new Exception();
        } else {
            sb.append("package com.zg.bean.entity;\r\n");
            if (referenceList != null && referenceList.size() > 0) {
                for (String reference : referenceList) {
                    sb.append("import " + reference + ";\r\n");
                }
            }
            sb.append("import java.util.*;\r\n");
            sb.append("@Model(tableName = \"" + calssName.toUpperCase() + "\")\n");
            sb.append("@FieldTypeMode(typeMode = \"entity\")\n");
            sb.append("public class ");
            sb.append(calssName);
            if (parentClass != null)
                sb.append(" extends " + parentClass);
            if (interfaceList != null && interfaceList.size() > 0) {
                sb.append(" implements ");
                for (String inter : interfaceList) {
                    sb.append(inter + " ");
                }
            }
            sb.append("{\r\n");
            Set<String> natureSet = natureMap.keySet();
            for (String name : natureSet) {
                sb.append("public " + natureMap.get(name) + " " + name + ";\r\n");
            }
            sb.append("}");
        }

        return sb.toString();
    }
/*    private static String produceEntityJavaCode(Map<String,Object> map,String tableName) throws Exception {
        Map<String,String> natureMap=new HashMap<>();
        Set<String> keySet=map.keySet();
        for(String key:keySet){
            natureMap.put(key,map.get(key).getClass().getTypeName());
        }
        return produceEntityJavaCode(Arrays.asList("com.zg.bean.entity.MainModel","com.zg.bean.annotation.FieldTypeMode","com.zg.bean.annotation.Model"),tableName,natureMap, null, "MainModel");
    }

    public static Object getDynamicClass(Map<String,Object> map,String tableName){
        String javaCode;
        try {
            javaCode = produceEntityJavaCode(map,tableName);
            return getDynamicClass(tableName, javaCode);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }*/

    public static Object getDynamicClass(List<String> referenceList, String className, Map<String, String> natureMap, List<String> interfaceList, String parentClass) {
        String javaCode;
        try {
            javaCode = produceEntityJavaCode(referenceList, className, natureMap, interfaceList, parentClass);
            return getDynamicClass(className, javaCode);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }


    private static Object getDynamicClass(String name, String javaCode) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Object o = null;
        Map<String, byte[]> results;
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        JavaFileManager stdManager = compiler.getStandardFileManager(null, null, null);
        JavaFileObject jfo = new StringJavaFileObject(name, javaCode);
        List<String> options = new ArrayList<String>();
        String path = MainModel.class.getClassLoader().getResource("").getPath();
        System.out.println(DataBaseUtil.class + "====calss生成路径" + path);
        options.addAll(Arrays.asList("-d", path));
        List<? extends JavaFileObject> jfos = Arrays.asList(jfo);
        CompilationTask task = compiler.getTask(null, stdManager, null, options, null, jfos);
        if (task.call()) {
            o = Class.forName("com.zg.bean.entity." + name).newInstance();
            return o;
        } else {
            return null;
        }


    }

}

