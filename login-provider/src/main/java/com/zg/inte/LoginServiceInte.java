package com.zg.inte;

import java.util.Map;

public interface LoginServiceInte {


     String verification(String username, String passworld) ;

     Map<String,String> login(String password, String username, String token) ;

     String registToken(String url, String domain, String rootPath);

     Integer isLogin(String token, String uuid) ;

     Map<String,String> getTokenValue(String token)  ;

     Map<String,String> getTokenValue(String token, String del_flag) ;

     void invalidToken(String token);
     Integer updateLoginValid(String uuid, String token) ;
     Integer updateLoginInvalid(String uuid, String token);


}
