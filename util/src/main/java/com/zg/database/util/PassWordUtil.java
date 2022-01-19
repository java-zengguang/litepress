package com.zg.database.util;

public class PassWordUtil {
   public static String decrypt(String password){
       String result="";
       if(password.equals("新一代数据库")){
           result="HvgaE#7ML_";
       }else{
           result=password;
       }

       return result;
   }
}
