package com.zg.mvc.intercept;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface PostControllerIntercept {

    Object doInvoke( HttpServletRequest request, HttpServletResponse response, Object args) throws IOException;

}
