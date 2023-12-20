package com.zg.mvc.adapter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface ControllerAdapterInte {


    void doMain(HttpServletRequest request, HttpServletResponse response);
    Object routeRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException, InterruptedException;
    public void analysisResponse(HttpServletRequest request, HttpServletResponse response,Object resultObj) throws IOException, ServletException;
    Object postIntercept(HttpServletRequest request, HttpServletResponse response, Object args) throws IOException;
    boolean preIntercept(HttpServletRequest request, HttpServletResponse response);
}
