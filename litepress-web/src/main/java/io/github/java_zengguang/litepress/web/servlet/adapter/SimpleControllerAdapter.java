package io.github.java_zengguang.litepress.web.servlet.adapter;

import io.github.java_zengguang.litepress.web.servlet.intercept.PostControllerIntercept;
import io.github.java_zengguang.litepress.web.servlet.intercept.PreControllerIntercept;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


public class SimpleControllerAdapter extends BaseControllerAdapter {
    private static ControllerAdapterInte controllerAdapterInte;


    private SimpleControllerAdapter() {
    }

    public synchronized static ControllerAdapterInte getInstance() {
        if (controllerAdapterInte == null) {
            controllerAdapterInte = new SimpleControllerAdapter();
        }
        return controllerAdapterInte;
    }

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
