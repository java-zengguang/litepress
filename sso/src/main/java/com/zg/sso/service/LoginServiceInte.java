package com.zg.sso.service;

import java.util.Map;

public interface LoginServiceInte {


     String verification(String username,String passworld) ;

     Map<String,String> login(String password,String username,String token ) ;

     String registToken(String url, String domain, String rootPath);

     int isLogin(String token,String uuid) ;

     Map<String,String> getTokenValue(String token)  ;

     Map<String,String> getTokenValue(String token,String del_flag) ;

     void invalidToken(String token);
     int updateLoginValid(String uuid,String token) ;
     int updateLoginInvalid(String uuid,String token);


}
