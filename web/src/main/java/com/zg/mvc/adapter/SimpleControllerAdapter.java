package com.zg.mvc.adapter;

import com.zg.mvc.intercept.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class SimpleControllerAdapter extends BaseControllerAdapter {

    public static List<PostControllerIntercept> postControllerIntercepts = Arrays.asList(new JsonPostIntercept(), new FilePostIntercept());

    public static List<PreControllerIntercept> preControllerIntercepts = Arrays.asList(new AuthPreIntercept());

    @Override
    public Object postIntercept(HttpServletRequest request, HttpServletResponse response, Object args) throws IOException {
        for (PostControllerIntercept postControllerIntercept : postControllerIntercepts) {
            args = postControllerIntercept.doInvoke(request, response, args);
        }
        return args;
    }

    @Override
    public boolean preIntercept(HttpServletRequest request, HttpServletResponse response) {
        for (PreControllerIntercept preControllerIntercept : preControllerIntercepts) {
            if (!preControllerIntercept.doInvoke(request, response)) {
                return false;
            }
        }
        return true;
    }
}
