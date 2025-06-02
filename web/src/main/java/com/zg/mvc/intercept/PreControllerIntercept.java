package com.zg.mvc.intercept;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface PreControllerIntercept {

    boolean doInvoke(HttpServletRequest request, HttpServletResponse response);

}
